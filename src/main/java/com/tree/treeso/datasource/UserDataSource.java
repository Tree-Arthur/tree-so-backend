package com.tree.treeso.datasource;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tree.treeso.common.ErrorCode;
import com.tree.treeso.constant.CommonConstant;
import com.tree.treeso.exception.BusinessException;
import com.tree.treeso.mapper.UserMapper;
import com.tree.treeso.model.dto.user.UserQueryRequest;
import com.tree.treeso.model.entity.User;
import com.tree.treeso.model.enums.UserRoleEnum;
import com.tree.treeso.model.vo.LoginUserVO;
import com.tree.treeso.model.vo.UserVO;
import com.tree.treeso.service.UserService;
import com.tree.treeso.utils.SqlUtils;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.bean.WxOAuth2UserInfo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.tree.treeso.constant.UserConstant.USER_LOGIN_STATE;

/**
 * 用户服务实现
 *
 * @author <a href="https://github.com/liyupi">程序员鱼皮</a>
 * @from <a href="https://yupi.icu">编程导航知识星球</a>
 */
@Service
@Slf4j
public class UserDataSource  implements DataSource<UserVO> {
    @Resource
    private UserService userService;

    @Override
    public Page<UserVO> doSearch(String searchText, long pageNum, long pageSize) {
        UserQueryRequest userQueryRequest = new UserQueryRequest();
        userQueryRequest.setUserName(searchText);//设置搜索参数为用户名
        userQueryRequest.setCurrent(pageNum);//设置页码
        userQueryRequest.setPageSize(pageSize);//设置页面数据量
        Page<UserVO> userVOPage = userService.listUserVoByPage(userQueryRequest);//调用service接口方法
        return userVOPage;
    }
}
