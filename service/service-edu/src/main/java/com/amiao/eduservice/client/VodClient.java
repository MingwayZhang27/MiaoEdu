package com.amiao.eduservice.client;

import com.amiao.commonutils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Component
@FeignClient(name="service-vod",fallback = VodFileDegradeFeignClient.class)
public interface VodClient {

    //定义调用的方法路径
    //根据视频id删除阿里云视频
    @DeleteMapping("/eduvod/video/removeAlyVideo/{id}")//完全路径
    public R removeAlyVideo(@PathVariable("id") String id); //@PathVariable注解一定要指定参数名称，否则出错

    //删除多个阿里云视频的方法
    //参数多个视频id
    @DeleteMapping("/eduvod/video/delete-batch")
    public R deleteBatch(@RequestParam("videoIdList") List<String> videoIdList);
}
