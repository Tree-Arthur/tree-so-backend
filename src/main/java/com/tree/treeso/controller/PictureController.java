package com.tree.treeso.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.gson.Gson;
import com.tree.treeso.annotation.AuthCheck;
import com.tree.treeso.common.BaseResponse;
import com.tree.treeso.common.DeleteRequest;
import com.tree.treeso.common.ErrorCode;
import com.tree.treeso.common.ResultUtils;
import com.tree.treeso.constant.UserConstant;
import com.tree.treeso.exception.BusinessException;
import com.tree.treeso.exception.ThrowUtils;
import com.tree.treeso.model.dto.picture.PictureQueryRequest;
import com.tree.treeso.model.dto.post.PostAddRequest;
import com.tree.treeso.model.dto.post.PostEditRequest;
import com.tree.treeso.model.dto.post.PostQueryRequest;
import com.tree.treeso.model.dto.post.PostUpdateRequest;
import com.tree.treeso.model.entity.Picture;
import com.tree.treeso.model.entity.Post;
import com.tree.treeso.model.entity.User;
import com.tree.treeso.model.vo.PostVO;
import com.tree.treeso.service.PictureService;
import com.tree.treeso.service.PostService;
import com.tree.treeso.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 图片接口
 *
 * @author <a href="https://github.com/liyupi">程序员鱼皮</a>
 * @from <a href="https://yupi.icu">编程导航知识星球</a>
 */
@RestController
@RequestMapping("/picture")
@Slf4j
public class PictureController {
    @Resource
    private PictureService pictureService;

    // region 增删改查
    /**
     * 分页获取列表（封装类）
     *
     * @param pictureQueryRequest 图片查询请求
     * @param request
     * @return
     */
    @PostMapping("/list/page/vo")
    public BaseResponse<Page<Picture>> listPostVOByPage(@RequestBody PictureQueryRequest pictureQueryRequest,
                                                        HttpServletRequest request) {

        String searchText = pictureQueryRequest.getSearchText();
        long current = pictureQueryRequest.getCurrent();
        long size = pictureQueryRequest.getPageSize();
        Page<Picture> picturePage = pictureService.searchPicture(searchText, current, size);//
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);//每页超过20条数据的请求
        return ResultUtils.success(picturePage);
    }
    // endregion

}
