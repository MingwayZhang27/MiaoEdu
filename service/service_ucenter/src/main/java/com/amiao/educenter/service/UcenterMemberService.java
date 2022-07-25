package com.amiao.educenter.service;

import com.amiao.educenter.entity.UcenterMember;
import com.amiao.educenter.entity.vo.RegisterVo;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 会员表 服务类
 * </p>
 *
 * @author amiao
 * @since 2022-07-15
 */
public interface UcenterMemberService extends IService<UcenterMember> {

    //登录
    String login(UcenterMember member);
    //注册
    void register(RegisterVo registerVo);

    UcenterMember getOpenIdMember(String openid);
    //查询某一天注册人数
    Integer countRegister(String day);
}
