package com.tree.treeso.job.once;

import cn.hutool.http.HttpRequest;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.tree.treeso.esdao.PostEsDao;
import com.tree.treeso.model.dto.post.PostEsDTO;
import com.tree.treeso.model.entity.Post;
import com.tree.treeso.service.PostService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.elasticsearch.Assertions;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 获取初始帖子列表
 */
// todo 取消注释后每次启动springboot项目会执行一次run方法
//@Component
@Slf4j
public class FetchInitPostList implements CommandLineRunner {
    @Resource
    private PostService postService;
    @Override
    public void run(String... args) {
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
            post.setTitle(tempRecord.getStr("title"));
            post.setContent(tempRecord.getStr("content"));
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
        if (b){
            log.info("获取初始化帖子列表成功,条数 = {}",postList.size());
        }else {
            log.error("获取初始化帖子列表失败");
        }
    }
}
