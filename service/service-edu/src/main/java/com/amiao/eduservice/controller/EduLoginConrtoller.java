package com.amiao.eduservice.controller;

import com.amiao.commonutils.R;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/eduservice/user")
////@CrossOrigin  //解决跨域
public class EduLoginConrtoller {

    //login
    @PostMapping("login")
    public R login(){
        return R.ok().data("token","admin");
    }

    //info
    @GetMapping("info")
    public R info(){
        return R.ok().data("roles","[admin]").data("name","admin").data("avater","https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fmaterials.cdn.bcebos.com%2Fimages%2F1077304%2Fe84b70281767ba39fb7bf3c479bb06f1.jpg&refer=http%3A%2F%2Fmaterials.cdn.bcebos.com&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=auto?sec=1658236694&t=4f8854021dd88bab807ff536bcd4567f");
    }
}
