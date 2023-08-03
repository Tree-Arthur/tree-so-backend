package com.tree.treeso.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tree.treeso.common.BaseResponse;
import com.tree.treeso.common.ErrorCode;
import com.tree.treeso.common.ResultUtils;
import com.tree.treeso.exception.BusinessException;
import com.tree.treeso.exception.ThrowUtils;
import com.tree.treeso.manager.SearchFacade;
import com.tree.treeso.model.dto.post.PostQueryRequest;
import com.tree.treeso.model.dto.search.SearchRequest;
import com.tree.treeso.model.dto.user.UserQueryRequest;
import com.tree.treeso.model.entity.Picture;
import com.tree.treeso.model.enums.SearchTypeEnum;
import com.tree.treeso.model.vo.PostVO;
import com.tree.treeso.model.vo.SearchVO;
import com.tree.treeso.model.vo.UserVO;
import com.tree.treeso.service.PictureService;
import com.tree.treeso.service.PostService;
import com.tree.treeso.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * 搜索接口
 */
@RestController
@RequestMapping("/search")
@Slf4j
public class SearchController {
    @Resource
    private PictureService pictureService;
    @Resource
    private PostService postService;
    @Resource
    private UserService userService;

    @Resource
    private SearchFacade searchFacade;//搜索门面
    // region 增删改查


    public BaseResponse<SearchVO> searchAllOld(@RequestBody SearchRequest searchRequest, HttpServletRequest request) {
        String searchText = searchRequest.getSearchText();
        Page<Picture> picturePage = pictureService.searchPicture(searchText, 1, 10);

        UserQueryRequest userQueryRequest = new UserQueryRequest();
        userQueryRequest.setUserName(searchText);
        Page<UserVO> userVOPage = userService.listUserVoByPage(userQueryRequest);

        PostQueryRequest postQueryRequest = new PostQueryRequest();
        postQueryRequest.setSearchText(searchText);
        Page<PostVO> postVOPage = postService.listPostVOByPage(postQueryRequest, request);

        SearchVO searchVO = new SearchVO();
        searchVO.setUserList(userVOPage.getRecords());
        searchVO.setPostList(postVOPage.getRecords());
        searchVO.setPictureList(picturePage.getRecords());
        return ResultUtils.success(searchVO);
    }
    public BaseResponse<SearchVO> searchAllOld2(@RequestBody SearchRequest searchRequest, HttpServletRequest request) {
        String type = searchRequest.getType();//获取类型
        SearchTypeEnum searchTypeEnum = SearchTypeEnum.getEnumByValue(type);//获取类型枚举
        ThrowUtils.throwIf(StringUtils.isBlank(type), ErrorCode.PARAMS_ERROR);//如果类型为空抛参数异常(语法糖)
        String searchText = searchRequest.getSearchText();//从请求中获取搜索参数

        if (searchTypeEnum == null) {//如果没有类型全查
            //并发对象
            CompletableFuture<Page<Picture>> pictureTask = CompletableFuture.supplyAsync(() -> {
                Page<Picture> picturePage = pictureService.searchPicture(searchText, 1, 10);//查询图片
                return picturePage;
            });
            //并发对象
            CompletableFuture<Page<UserVO>> userTask = CompletableFuture.supplyAsync(() -> {
                UserQueryRequest userQueryRequest = new UserQueryRequest();
                userQueryRequest.setUserName(searchText);//设置searchText内容为userName
                Page<UserVO> userVOPage = userService.listUserVoByPage(userQueryRequest);//查询用户
                return userVOPage;
            });
            //并发对象
            CompletableFuture<Page<PostVO>> postTask = CompletableFuture.supplyAsync(() -> {
                PostQueryRequest postQueryRequest = new PostQueryRequest();
                postQueryRequest.setSearchText(searchText);//设置统一搜索参数到帖子请求包装类
                Page<PostVO> postVOPage = postService.listPostVOByPage(postQueryRequest, request);//查询文章
                return postVOPage;
            });

            CompletableFuture.allOf(userTask, postTask, pictureTask).join();//卡住直到三个都执行完成类似断点
            try {
                Page<UserVO> userVOPage = userTask.get();
                Page<Picture> picturePage = pictureTask.get();
                Page<PostVO> postVOPage = postTask.get();

                SearchVO searchVO = new SearchVO();
                searchVO.setPictureList(picturePage.getRecords());//设置图片分页到搜索视图
                searchVO.setUserList(userVOPage.getRecords());//设置用户分页到搜索视图
                searchVO.setPostList(postVOPage.getRecords());//设置帖子分页到搜索视图
                return ResultUtils.success(searchVO);

            } catch (Exception e) {
                log.error("查询异常", e);
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "查询异常");
            }
        } else {
            SearchVO searchVO = new SearchVO();
            switch (searchTypeEnum){//根据枚举类型选择查询
                case POST:
                    PostQueryRequest postQueryRequest = new PostQueryRequest();
                    postQueryRequest.setSearchText(searchText);//设置统一搜索参数到帖子请求包装类
                    Page<PostVO> postVOPage = postService.listPostVOByPage(postQueryRequest, request);//查询文章
                    searchVO.setPostList(postVOPage.getRecords());//设置帖子分页到搜索视图
                    break;
                case USER:
                    UserQueryRequest userQueryRequest = new UserQueryRequest();
                    userQueryRequest.setUserName(searchText);//设置searchText内容为userName
                    Page<UserVO> userVOPage = userService.listUserVoByPage(userQueryRequest);//查询用户
                    searchVO.setUserList(userVOPage.getRecords());//设置用户分页到搜索视图
                    break;
                case PICTURE:
                    Page<Picture> picturePage = pictureService.searchPicture(searchText, 1, 10);//查询图片
                    searchVO.setPictureList(picturePage.getRecords());//设置图片分页到搜索视图
                    break;
            }
            return ResultUtils.success(searchVO);
        }
        // endregion
    }
    @PostMapping("/all")
    public BaseResponse<SearchVO> searchAll(@RequestBody SearchRequest searchRequest, HttpServletRequest request){
        return ResultUtils.success(searchFacade.searchAll(searchRequest,request));
    }
}
