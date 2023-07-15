package com.tree.treeso.manager;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tree.treeso.common.ErrorCode;
import com.tree.treeso.datasource.*;
import com.tree.treeso.exception.BusinessException;
import com.tree.treeso.exception.ThrowUtils;
import com.tree.treeso.model.dto.search.SearchRequest;
import com.tree.treeso.model.entity.Picture;
import com.tree.treeso.model.enums.SearchTypeEnum;
import com.tree.treeso.model.vo.PostVO;
import com.tree.treeso.model.vo.SearchVO;
import com.tree.treeso.model.vo.UserVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.concurrent.CompletableFuture;

/**
 * @description 搜索门面模式
 * @author tree
 * @version 1.0
 * @date 2023/6/23 22:01
 */
@Component
@Slf4j
public class SearchFacade {

    @Resource
    private PostDataSource postDataSource;
    @Resource
    private PictureDataSource pictureDataSource;
    @Resource
    private UserDataSource userDataSource;

    @Resource
    private DataSourceRegister dataSourceRegister;

    public SearchVO searchAll(@RequestBody SearchRequest searchRequest, HttpServletRequest request) {
        String type = searchRequest.getType();//获取类型
        SearchTypeEnum searchTypeEnum = SearchTypeEnum.getEnumByValue(type);//获取类型枚举
        ThrowUtils.throwIf(StringUtils.isBlank(type), ErrorCode.PARAMS_ERROR);//如果类型为空抛参数异常(语法糖)
        String searchText = searchRequest.getSearchText();//从请求中获取搜索参数
        long current = searchRequest.getCurrent();
        long pageSize = searchRequest.getPageSize();

        if (searchTypeEnum == null) {//如果没有类型全查
            //并发对象
            CompletableFuture<Page<Picture>> pictureTask = CompletableFuture.supplyAsync(() -> {
                Page<Picture> picturePage = pictureDataSource.doSearch(searchText, current, pageSize);//查询图片
                return picturePage;
            });
            //并发对象
            CompletableFuture<Page<UserVO>> userTask = CompletableFuture.supplyAsync(() -> {
                Page<UserVO> userVOPage = userDataSource.doSearch(searchText,current,pageSize);//查询用户
                return userVOPage;
            });
            //并发对象
            CompletableFuture<Page<PostVO>> postTask = CompletableFuture.supplyAsync(() -> {
                Page<PostVO> postVOPage = postDataSource.doSearch(searchText, current,pageSize);//查询文章
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
                return searchVO;

            } catch (Exception e) {
                log.error("查询异常", e);
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "查询异常");
            }
        } else {
            SearchVO searchVO = new SearchVO();
            DataSource<?> dataSource = dataSourceRegister.getDataSourceByType(type);
            Page<?> page = dataSource.doSearch(searchText, current, pageSize);
            searchVO.setDataList(page.getRecords());

            return searchVO;
        }
        // endregion
    }
}
