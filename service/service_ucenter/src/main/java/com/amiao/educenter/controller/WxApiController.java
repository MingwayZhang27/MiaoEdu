package com.amiao.educenter.controller;

import com.amiao.commonutils.JwtUtils;
import com.amiao.educenter.entity.UcenterMember;
import com.amiao.educenter.service.UcenterMemberService;
import com.amiao.educenter.utils.ConstantWxUtils;
import com.amiao.educenter.utils.HttpClientUtils;
import com.amiao.servicebase.exceptionhandler.AmiaoException;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.net.URLEncoder;
import java.util.HashMap;

@Controller  //现在只是请求地址，不需要返回数据
//@CrossOrigin
@RequestMapping("/api/ucenter/wx")
public class WxApiController {
    @Autowired
    private UcenterMemberService ucenterMemberService;

    //1 生成微信扫描二维码
    @GetMapping("login")
    public String getWxCode() {
        //%s 相当于?占位符
        String baseUrl = "https://open.weixin.qq.com/connect/qrconnect"
                + "?appid=%s"
                + "&redirect_uri=%s"
                + "&response_type=code"
                + "&scope=snsapi_login"
                + "&state=%s"
                + "#wechat_redirect";

        //对redirect_url进行URLEncoder编码
        String redirectUrl = ConstantWxUtils.WX_REDIRECT_URL;
        try {
            redirectUrl = URLEncoder.encode(redirectUrl, "utf-8");
        } catch (Exception e) {
            e.printStackTrace();
        }

        //设置%s里面的值
        String url = String.format(
                baseUrl,
                ConstantWxUtils.WX_APP_ID,
                redirectUrl,
                "amiao"
        );

        //重定向到请求微信地址
        return "redirect:" + url;
    }

    //2 获取扫描人信息，添加数据
    @GetMapping("callback")
    public String callback(String code, String state) {
        try {
            //1 获取code值，临时票据，类似于验证码

            //2 拿着code请求微信固定的地址，得到两个值 access_token和openid
            String baseAccessTokenUrl = "https://api.weixin.qq.com/sns/oauth2/access_token" +
                    "?appid=%s" +
                    "&secret=%s" +
                    "&code=%s" +
                    "&grant_type=authorization_code";

            //拼接三个参数：id、秘钥和code
            String accessToken = String.format(
                    baseAccessTokenUrl,
                    ConstantWxUtils.WX_APP_ID,
                    ConstantWxUtils.WX_APP_SECRET,
                    code);

            //请求这个拼接好的地址，得到返回两个值 access_token和openid
            //使用httpclient发送请求，得到返回结果
            String accessTokenInfo = HttpClientUtils.get(accessToken);

            //从accessTokenInfo中获取出  access_token 和 openid 的值
            //把accessTokenInfo字符串转换map集合，根据map里面的key获取对应值
            //使用json转换工具Gson
            Gson gson = new Gson();
            HashMap mapAccessToken = gson.fromJson(accessTokenInfo, HashMap.class);
            String access_token = (String) mapAccessToken.get("access_token");
            String openid = (String) mapAccessToken.get("openid");

            //把扫码人信息添加数据库里面
            //判断数据库表里面是否存在相同微信信息,根据openid判断
            UcenterMember member = ucenterMemberService.getOpenIdMember(openid);
            if (member == null){
                //3 拿着得到access_token和openid，再去请求微信提供固定的地址，获取到扫描人的信息
                String baseUserInfoUrl = "https://api.weixin.qq.com/sns/userinfo" +
                        "?access_token=%s" +
                        "&openid=%s";
                //拼接两个参数
                String userInfoUrl = String.format(
                        baseUserInfoUrl,
                        access_token,
                        openid);

                //发送请求
                String userInfo = HttpClientUtils.get(userInfoUrl);
                //获取返回userinfo字符串扫码人信息
                HashMap userInfoMap = gson.fromJson(userInfo, HashMap.class);
                String nickname = (String) userInfoMap.get("nickname");
                nickname=nickname.substring(0,3);
                String headimgurl = (String) userInfoMap.get("headimgurl");

                //member是空，表没有相同微信号，进行添加操作
                member=new UcenterMember();
                member.setOpenid(openid);
                member.setNickname(nickname);
                member.setAvatar(headimgurl);
                ucenterMemberService.save(member);
            }

            //使用jwt根据member对象生成token字符串
            String jwtToken = JwtUtils.getJwtToken(member.getId(), member.getNickname());

            //最后，返回首页面，通过路径传递token字符串
            return "redirect:http://localhost:3000?token="+jwtToken;

        } catch (Exception e) {
            throw new AmiaoException(20001,"登陆失败");
        }
    }
}
