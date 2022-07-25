package com.amiao.eduorder.service;

import com.amiao.eduorder.entity.PayLog;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Map;

/**
 * <p>
 * 支付日志表 服务类
 * </p>
 *
 * @author amiao
 * @since 2022-07-19
 */
public interface PayLogService extends IService<PayLog> {
    //生成微信支付二维码接口
    Map createNative(String orderNo);

    //查询订单支付状态
    Map<String, String> queryPayStatus(String orderNo);

    //添加记录到支付表，更新订单表订单状态
    void updateOrderStatus(Map<String, String> map);
}
