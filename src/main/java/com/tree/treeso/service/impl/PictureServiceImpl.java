package com.tree.treeso.service.impl;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tree.treeso.common.ErrorCode;
import com.tree.treeso.exception.BusinessException;
import com.tree.treeso.model.entity.Picture;
import com.tree.treeso.service.PictureService;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author tree
 * @version 1.0
 * @description 图片服务接口实现类
 * @date 2023/6/22 22:42
 */
@Service
public class PictureServiceImpl implements PictureService {

    @Override
    public Page<Picture> searchPicture(String searchText, long pageNum, long pageSize) {
        long current = (pageNum - 1) * pageSize;
        String url = String.format("https://cn.bing.com/images/search?q=%s&first=%s", searchText, current);
        Document doc = null;//基本网页数据
        try {
            doc = Jsoup.connect(url).get();
        } catch (IOException e) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "数据获取异常");
        }

        Elements elements = doc.select(".iuscp.isv");//查找css类元素
        List<Picture> pictures = new ArrayList<>();
        for (Element element : elements) {
            //取图片地址(murl)
            //未处理的图片数据(JSON格式)
            String m = element.select(".iusc").get(0).attr("m");//获取第一个元素(get(0))取它的属性(attr)
            //2.json 转对象 数据处理 以Map类型保存的图片数据
            Map<String, Object> map = JSONUtil.toBean(m, Map.class);
            String murl = (String) map.get("murl");//获取键为murl的值 图片数据
            //取标题
            String title = element.select(".inflnk").get(0).attr("aria-label");//获取第一个元素(get(0))取它的aria-label属性(attr)
            //设置数据到图片列表
            Picture picture = new Picture();//实例化图片对象
            picture.setTitle(title);//设置标题
            picture.setUrl(murl);//设置图片url
            pictures.add(picture);//图片对象添加到图片列表
            if (pictures.size() >= pageSize) {//如果图片列表数量大于页面所显示数据量
                break;//跳过
            }
        }
        Page<Picture> picturePage = new Page<>(pageNum, pageSize);
        picturePage.setRecords(pictures);
        return picturePage;
    }
}
