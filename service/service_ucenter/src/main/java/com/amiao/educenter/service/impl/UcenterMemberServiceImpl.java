package com.amiao.educenter.service.impl;

import com.amiao.commonutils.JwtUtils;
import com.amiao.commonutils.MD5;
import com.amiao.educenter.entity.UcenterMember;
import com.amiao.educenter.entity.vo.RegisterVo;
import com.amiao.educenter.mapper.UcenterMemberMapper;
import com.amiao.educenter.service.UcenterMemberService;
import com.amiao.servicebase.exceptionhandler.AmiaoException;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;


/**
 * <p>
 * 会员表 服务实现类
 * </p>
 *
 * @author amiao
 * @since 2022-07-15
 */
@Service
public class UcenterMemberServiceImpl extends ServiceImpl<UcenterMemberMapper, UcenterMember> implements UcenterMemberService {

    @Autowired
    private RedisTemplate<String,String> redisTemplate;

    //登录
    @Override
    public String login(UcenterMember member) {
        //获取登录手机号和密码
        String mobile=member.getMobile();
        String password=member.getPassword();

        //手机号和密码非空判断
        if(StringUtils.isEmpty(mobile)||StringUtils.isEmpty(password)){
            throw new AmiaoException(20001,"登录失败");
        }

        //判断手机号是否正确
        QueryWrapper<UcenterMember> wrapper=new QueryWrapper<>();
        wrapper.eq("mobile",mobile);
        UcenterMember mobileMember = baseMapper.selectOne(wrapper);

        //判断查询对象是否为空
        if(mobileMember==null){//没有这个手机号
            throw new AmiaoException(20001,"账号不存在");
        }
        //判断密码是否正确
        //因为存储到数据库密码肯定是加密的
        //把输入的密码进行加密，然后再和数据库密码进行比较
        //加密方式 MD5
        if(!MD5.encrypt(password).equals(mobileMember.getPassword())){
            throw new AmiaoException(20001,"密码错误");
        }
        //判断用户是否禁用
        if(mobileMember.getIsDisabled()){
            throw new AmiaoException(20001,"用户被禁用");
        }

        //登录成功
        //生成token字符串，使用jwt工具类
        String jwtToken = JwtUtils.getJwtToken(mobileMember.getId(), mobileMember.getNickname());
        return jwtToken;
    }

    //注册的方法
    @Override
    public void register(RegisterVo registerVo) {
        //获取注册的数据
        String code = registerVo.getCode();//验证码
        String mobile = registerVo.getMobile();//手机号
        String nickname = registerVo.getNickname();//昵称
        String password = registerVo.getPassword();//密码

        //非空判断
        if(StringUtils.isEmpty(mobile)||StringUtils.isEmpty(password)
                ||StringUtils.isEmpty(code)||StringUtils.isEmpty(nickname)){
            throw new AmiaoException(20001,"注册失败");
        }

        //判断验证码
        //获取redis中的验证码
        String redisCode = redisTemplate.opsForValue().get(mobile);
        if(!code.equals(redisCode)){
            throw new AmiaoException(20001,"验证码错误");
        }

        //判断手机号是否重复，表里面存在相同手机号不进行添加
        QueryWrapper<UcenterMember> wrapper=new QueryWrapper<>();
        wrapper.eq("mobile",mobile);
        Integer count = baseMapper.selectCount(wrapper);
        if(count>0){
            throw new AmiaoException(20001,"账号已存在，注册失败");
        }

        //数据添加数据库中
        UcenterMember member=new UcenterMember();
        member.setMobile(mobile);
        member.setNickname(nickname);
        member.setPassword(MD5.encrypt(password));//密码需要加密
        member.setIsDisabled(false);
        member.setAvatar("https://amiao-edu.oss-cn-guangzhou.aliyuncs.com/pikaqiu.jpg");
        baseMapper.insert(member);

    }

    //判断数据库表里面是否存在相同微信信息,根据openid判断
    @Override
    public UcenterMember getOpenIdMember(String openid) {
        QueryWrapper wrapper=new QueryWrapper();
        wrapper.eq("openid",openid);
        UcenterMember member = baseMapper.selectOne(wrapper);
        return member;
    }

    //查询某一天注册人数
    @Override
    public Integer countRegister(String day) {
        return baseMapper.countRegister(day);
    }
}
