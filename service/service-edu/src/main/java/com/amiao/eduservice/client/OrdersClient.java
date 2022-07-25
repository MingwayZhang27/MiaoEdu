package com.amiao.eduservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Component
@FeignClient(name="service-order")
public interface OrdersClient {
    //根据课程id和用户id查询订单表中订单状态
    @GetMapping("/eduorder/order/isBuyCourse/{courseId}/{numberId}")
    public boolean isBuyCourse(@PathVariable("courseId") String courseId,
                               @PathVariable("numberId") String numberId);
}
