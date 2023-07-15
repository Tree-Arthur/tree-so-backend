package com.tree.treeso.datasource;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tree.treeso.model.dto.user.UserQueryRequest;
import com.tree.treeso.model.vo.UserVO;
import com.tree.treeso.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 视频数据源
 */
@Service
@Slf4j
public class VideoDataSource implements DataSource<Object> {
    @Override
    public Page<Object> doSearch(String searchText, long pageNum, long pageSize) {
        return null;
    }
}
