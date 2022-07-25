package com.amiao.eduorder.service;

import com.amiao.eduorder.entity.Order;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 订单 服务类
 * </p>
 *
 * @author amiao
 * @since 2022-07-19
 */
public interface OrderService extends IService<Order> {
    //1 生成订单方法
    String createOrders(String courseId, String memberIdByJwtToken);
}
