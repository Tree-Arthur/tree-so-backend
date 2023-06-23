package com.tree.treeso;
import java.io.IOException;
import java.util.ArrayList;

import cn.hutool.http.HttpRequest;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.tree.treeso.model.entity.Picture;
import com.tree.treeso.model.entity.Post;
import com.tree.treeso.service.PostService;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * @author tree
 * @version 1.0
 * @description 爬虫测试类
 * @date 2023/6/22 20:04
 */
@SpringBootTest
public class CrawlerTest {
    @Resource
    private PostService postService;
    @Test
    void testFetchPassage(){
        // 分析获取数据源
        //发送的请求参数
        String json = "{\"current\":1,\"pageSize\":8,\"sortField\":\"createTime\",\"sortOrder\":\"descend\",\"category\":\"文章\",\"reviewStatus\":1}";
        String url = "https://www.code-nav.cn/api/post/search/page/vo";//请求地址
        String result = HttpRequest
                .post(url)
                .body(json)
                .execute().body();//触发调用
        System.out.println(result);
        //2.json 转对象 数据处理
        Map<String,Object> map = JSONUtil.toBean(result,Map.class);
        System.out.println(map);
        JSONObject data = (JSONObject)map.get("data");//强转JSONObject类型得到data数据
        JSONArray records = (JSONArray)data.get("records");
        List<Post> postList = new ArrayList<>();
        for (Object record : records) {
            JSONObject tempRecord = (JSONObject)record;//强转JSONObject类型方便调用方法
            Post post = new Post();//实例化文章对象
            //TODO 取值后记得判空
            post.setTitle(tempRecord.getStr("title"));//设置标题
            post.setContent(tempRecord.getStr("content"));//设置内容
            JSONArray tags = (JSONArray)tempRecord.get("tags");//强转JSONArray类型
            List<String> tagList = tags.toList(String.class);
            post.setTags(JSONUtil.toJsonStr(tagList));//tagList列表JSON转string 设置标签
            post.setUserId(1L);
            postList.add(post);//保存设置的数据到文章列表
        }
        System.out.println(postList);
        System.out.println(records);
        //3.数据入库
        boolean b = postService.saveBatch(postList);//保存文章列表数据到数据库
        Assertions.assertTrue(b);
    }
    @Test
    void testFetchPicture() throws IOException {
        int current = 1;
        String url ="https://cn.bing.com/images/search?q=小黑子&qs=HS&sc=10-0&cvid=04757E9DEB914ADC8183798927E893D2&form=QBLH&sp=1&lq=0&first="+ current;
        Document doc = Jsoup.connect(url).get();
//        System.out.println(doc);//基本网页数据
        Elements elements = doc.select(".iuscp.isv");//查找css类元素
        List<Picture> pictures = new ArrayList<>();
        for (Element element : elements) {
            //System.out.println(element);
            //取图片地址(murl)
            String m = element.select(".iusc").get(0).attr("m");//获取第一个元素(get(0))取它的属性(attr)
//            System.out.println(m);//未处理的图片数据(JSON格式)
            //2.json 转对象 数据处理
            Map<String,Object> map = JSONUtil.toBean(m,Map.class);
//            System.out.println(map);//以Map类型保存的图片数据
            String murl = (String) map.get("murl");//获取键为murl的值
            System.out.println(murl);//图片数据

            //取标题
            String title = element.select(".inflnk").get(0).attr("aria-label");//获取第一个元素(get(0))取它的aria-label属性(attr)
            System.out.println(title);

            //设置数据到图片列表
            Picture picture = new Picture();//实例化图片对象
            picture.setTitle(title);//设置标题
            picture.setUrl(murl);//设置图片url
            pictures.add(picture);//图片对象添加到图片列表
        }
        System.out.println(pictures);
    }
}
