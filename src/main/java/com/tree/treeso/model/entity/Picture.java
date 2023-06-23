package com.tree.treeso.model.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * @author tree
 * @version 1.0
 * @description 图片实体类
 * @date 2023/6/22 22:16
 */
@Data
public class Picture implements Serializable {
    /**
     * 标题
     */
    private String title;
    /**
     * 图片url
     */
    private String url;
    private static final long serialVersionUID = 1L;
}
