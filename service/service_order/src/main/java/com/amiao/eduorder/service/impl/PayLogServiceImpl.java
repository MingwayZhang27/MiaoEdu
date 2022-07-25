package com.amiao.eduorder.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.amiao.eduorder.entity.Order;
import com.amiao.eduorder.entity.PayLog;
import com.amiao.eduorder.mapper.PayLogMapper;
import com.amiao.eduorder.service.OrderService;
import com.amiao.eduorder.service.PayLogService;
import com.amiao.eduorder.utils.HttpClient;
import com.amiao.servicebase.exceptionhandler.AmiaoException;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.wxpay.sdk.WXPayUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * 支付日志表 服务实现类
 * </p>
 *
 * @author amiao
 * @since 2022-07-19
 */
@Service
public class PayLogServiceImpl extends ServiceImpl<PayLogMapper, PayLog> implements PayLogService {

    @Autowired
    private OrderService orderService;

    //生成微信支付二维码接口
    @Override
    public Map createNative(String orderNo) {
        try {
            //1 根据订单号查询订单信息
            QueryWrapper<Order> wrapper=new QueryWrapper<>();
            wrapper.eq("order_no",orderNo);
            Order order = orderService.getOne(wrapper);

            //2 使用map设置生成二维码需要的参数
            Map map=new HashMap();
            map.put("appid","wx74862e0dfcf69954");//支付id
            map.put("mch_id", "1558950191");//商户号
            map.put("nonce_str", WXPayUtil.generateNonceStr());//生成随机的字符串，让每次生成的二维码不一样
            map.put("body", order.getCourseTitle());//生成二维码的名字==>课程标题
            map.put("out_trade_no", orderNo);//二维码唯一的标识==>订单号
            map.put("total_fee", order.getTotalFee().multiply(new BigDecimal("100")).longValue()+"");//支付金额
            map.put("spbill_create_ip", "127.0.0.1");//现在进行支付的ip地址，实际项目使用项目的域名
            map.put("notify_url", "http://guli.shop/api/order/weixinPay/weixinNotify");//支付后回调地址
            map.put("trade_type", "NATIVE");//支付类型，NATIVE:根据价格生成二维码

            //3 发送httpclient请求，传递参数，参数格式是xml格式
            HttpClient client=new HttpClient("https://api.mch.weixin.qq.com/pay/unifiedorder");
            //设置xml格式的参数
            client.setXmlParam(WXPayUtil.generateSignedXml(map,"T6m9iK73b0kn9g5v426MKfHQH7X8rKwb"));
            client.setHttps(true);//上面发送请求的是https。默认支持，需要设置为true支持
            //执行post请求发送
            client.post();

            //4 得到发送请求返回结果
            //返回内容，是使用xml格式返回
            String xml = client.getContent();

            //把xml格式转换map集合，把map集合返回
            Map<String,String> resultMap=WXPayUtil.xmlToMap(xml);

            //最终返回数据的封装
            Map hashMap=new HashMap();
            hashMap.put("out_trade_no",orderNo);
            hashMap.put("course_id",order.getCourseId());
            hashMap.put("total_fee",order.getTotalFee());
            hashMap.put("result_code",resultMap.get("result_code")); //返回二维码操作状态码
            hashMap.put("code_url",resultMap.get("code_url"));       //二维码地址

            return hashMap;

        }catch (Exception e){
            throw new AmiaoException(20001,"生成二维码失败");
        }
    }

    //查询订单支付状态
    @Override
    public Map<String, String> queryPayStatus(String orderNo) {
        try {
            //1、封装参数
            Map m = new HashMap<>();
            m.put("appid", "wx74862e0dfcf69954");
            m.put("mch_id", "1558950191");
            m.put("out_trade_no", orderNo);
            m.put("nonce_str", WXPayUtil.generateNonceStr());

            //2、发送httpClient请求
            HttpClient client = new HttpClient("https://api.mch.weixin.qq.com/pay/orderquery");
            client.setXmlParam(WXPayUtil.generateSignedXml(m,"T6m9iK73b0kn9g5v426MKfHQH7X8rKwb"));//通过商户key加密
            client.setHttps(true);
            client.post();

            //3、返回第三方的数据
            String xml = client.getContent();
            //4、转成Map
            Map<String, String> resultMap = WXPayUtil.xmlToMap(xml);
            //5、返回
            return resultMap;

        }catch (Exception e){
            throw new AmiaoException(20001,"查询订单支付状态失败");
        }
    }

    //添加记录到支付表，更新订单表订单状态
    @Override
    public void updateOrderStatus(Map<String, String> map) {
        //获取前一步生成二维码中的订单号
        String orderNo = map.get("out_trade_no");
        //根据订单号查询订单信息
        QueryWrapper<Order> wrapper=new QueryWrapper<>();
        wrapper.eq("order_no",orderNo);
        Order order = orderService.getOne(wrapper);

        //判断订单状态是否为1，为1就是支付过了
        if(order.getStatus().intValue()==1){ return; }
        order.setStatus(1);
        orderService.updateById(order);

        //向支付表里添加支付记录
        PayLog payLog = new PayLog();
        payLog.setOrderNo(orderNo);//支付订单号
        payLog.setPayTime(new Date());//支付时间
        payLog.setPayType(1);//支付类型
        payLog.setTotalFee(order.getTotalFee());//总金额(分)
        payLog.setTradeState(map.get("trade_state"));//支付状态
        payLog.setTransactionId(map.get("transaction_id"));//订单流水号
        payLog.setAttr(JSONObject.toJSONString(map));
        baseMapper.insert(payLog);


    }
}
