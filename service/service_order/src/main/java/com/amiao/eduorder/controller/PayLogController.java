package com.amiao.eduorder.controller;


import com.amiao.commonutils.R;
import com.amiao.eduorder.service.PayLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * <p>
 * 支付日志表 前端控制器
 * </p>
 *
 * @author amiao
 * @since 2022-07-19
 */
@RestController
@RequestMapping("/eduorder/pay-log")
//@CrossOrigin
public class PayLogController {

    @Autowired
    private PayLogService payLogService;

    //生成微信支付二维码接口
    //参数是订单号
    @GetMapping("createNative/{orderNo}")
    public R createNative(@PathVariable String orderNo){
        //返回信息：返回二维码地址还有其他信息
        Map map=payLogService.createNative(orderNo);
        System.out.println("****二维码的map值"+map);

        return R.ok().data(map);
    }

    //查询订单支付状态
    //参数：订单号  根据订单号查询支付状态
    @GetMapping("queryPayStatus/{orderNo}")
    public R queryPayStatus(@PathVariable String orderNo){
        Map<String,String> map=payLogService.queryPayStatus(orderNo);
        System.out.println("****查询订单状态的map值"+map);
        if (map==null){
            return R.error().message("支付出错了");
        }
            //如果返回的map里面不为空，通过map获取订单状态
        if (map.get("trade_state").equals("SUCCESS")){//支付成功
            //添加记录到支付表，更新订单表订单状态
            payLogService.updateOrderStatus(map);
            return R.ok().message("支付成功");
        }
        return R.ok().code(25000).message("支付中");
    }
}

