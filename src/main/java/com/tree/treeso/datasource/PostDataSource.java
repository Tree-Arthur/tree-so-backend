package com.tree.treeso.datasource;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.gson.Gson;
import com.tree.treeso.common.ErrorCode;
import com.tree.treeso.constant.CommonConstant;
import com.tree.treeso.exception.BusinessException;
import com.tree.treeso.exception.ThrowUtils;
import com.tree.treeso.mapper.PostFavourMapper;
import com.tree.treeso.mapper.PostMapper;
import com.tree.treeso.mapper.PostThumbMapper;
import com.tree.treeso.model.dto.post.PostEsDTO;
import com.tree.treeso.model.dto.post.PostQueryRequest;
import com.tree.treeso.model.entity.Post;
import com.tree.treeso.model.entity.PostFavour;
import com.tree.treeso.model.entity.PostThumb;
import com.tree.treeso.model.entity.User;
import com.tree.treeso.model.vo.PostVO;
import com.tree.treeso.model.vo.UserVO;
import com.tree.treeso.service.PostService;
import com.tree.treeso.service.UserService;
import com.tree.treeso.utils.SqlUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 帖子数据源
 */
@Service
@Slf4j
public class PostDataSource implements DataSource<PostVO> {
    @Resource
    private PostService postService;

    @Override
    public Page<PostVO> doSearch(String searchText, long pageNum, long pageSize) {
        PostQueryRequest postQueryRequest = new PostQueryRequest();//实例化查询请求包装类
        postQueryRequest.setSearchText(searchText);//设置搜索文本
        postQueryRequest.setCurrent(pageNum);//设置页码
        postQueryRequest.setPageSize(pageSize);
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = requestAttributes.getRequest();
//        Page<PostVO> postVOPage = postService.listPostVOByPage(postQueryRequest,request);
//        return postVOPage;

        Page<Post> postPage = postService.searchFromEs(postQueryRequest);
        return postService.getPostVOPage(postPage, request);
    }
}




