package com.tree.treeso.datasource;

import com.tree.treeso.model.enums.SearchTypeEnum;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * @author tree
 * @version 1.0
 * @description 数据源注册
 * @date 2023/6/24 0:14
 */
@Component
public class DataSourceRegister {
    @Resource
    private PostDataSource postDataSource;
    @Resource
    private PictureDataSource pictureDataSource;
    @Resource
    private UserDataSource userDataSource;

    private Map<String,DataSource<T>> typeDataSourceMap;
    @PostConstruct
    public void doInit(){
        typeDataSourceMap = new HashMap(){{
            put(SearchTypeEnum.POST.getValue(),postDataSource);
            put(SearchTypeEnum.PICTURE.getValue(),pictureDataSource);
            put(SearchTypeEnum.USER.getValue(),userDataSource);
        }};
    }

    public DataSource getDataSourceByType(String type){
        if (typeDataSourceMap == null){
            return null;
        }
        return typeDataSourceMap.get(type);
    }

}
