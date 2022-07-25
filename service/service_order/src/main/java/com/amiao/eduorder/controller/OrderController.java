package com.amiao.eduorder.controller;


import com.amiao.commonutils.JwtUtils;
import com.amiao.commonutils.R;
import com.amiao.eduorder.entity.Order;
import com.amiao.eduorder.service.OrderService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * <p>
 * 订单 前端控制器
 * </p>
 *
 * @author amiao
 * @since 2022-07-19
 */
@RestController
@RequestMapping("/eduorder/order")
//@CrossOrigin
public class OrderController {
    @Autowired
    private OrderService orderService;

    //1 生成订单方法
    @PostMapping("createOrder/{courseId}")
    public R saveOrder(@PathVariable String courseId, HttpServletRequest request){
        //创建订单，返回订单号
        String orderNo=orderService.createOrders(courseId,JwtUtils.getMemberIdByJwtToken(request));
        return R.ok().data("orderId",orderNo);
    }

    //2 根据订单id查询订单信息
    @GetMapping("getOrderInfo/{orderId}")
    public R getOrderInfo(@PathVariable String orderId){
        QueryWrapper wrapper=new QueryWrapper();
        wrapper.eq("order_no",orderId);
        Order order = orderService.getOne(wrapper);
        return R.ok().data("item",order);
    }

    //根据课程id和用户id查询订单表中订单状态
    @GetMapping("isBuyCourse/{courseId}/{numberId}")
    public boolean isBuyCourse(@PathVariable String courseId,
                               @PathVariable String numberId){
        QueryWrapper wrapper=new QueryWrapper();
        wrapper.eq("course_id",courseId);
        wrapper.eq("member_id",numberId);
        wrapper.eq("status",1); //支付状态 1表示已经支付

        int count = orderService.count(wrapper);
        if (count>0){//已经支付
            return true;
        }else {
            return false;
        }
    }
}

