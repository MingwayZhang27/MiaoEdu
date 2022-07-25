package com.amiao.eduorder.service.impl;

import com.amiao.commonutils.ordervo.CourseWebVoOrder;
import com.amiao.commonutils.ordervo.UcenterMemberOrder;
import com.amiao.eduorder.client.EduClient;
import com.amiao.eduorder.client.UcenterClient;
import com.amiao.eduorder.entity.Order;
import com.amiao.eduorder.mapper.OrderMapper;
import com.amiao.eduorder.service.OrderService;
import com.amiao.eduorder.utils.OrderNoUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * <p>
 * 订单 服务实现类
 * </p>
 *
 * @author amiao
 * @since 2022-07-19
 */
@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements OrderService {

    @Autowired
    private EduClient eduClient;
    @Autowired
    private UcenterClient ucenterClient;

    //1 生成订单方法
    @Override
    public String createOrders(String courseId, String memberId) {
        //通过远程根据用户id调用获取用户信息
        UcenterMemberOrder userInfoOrder = ucenterClient.getUserInfoOrder(memberId);
        //通过远程根据课程id调用获取课程信息
        CourseWebVoOrder courseInfoOrder = eduClient.getCourseInfoOrder(courseId);

        //创建order对象，向order对象里面设置需要数据
        Order order = new Order();
        order.setOrderNo(OrderNoUtil.getOrderNo());//订单号
        order.setCourseId(courseId);
        order.setMobile(userInfoOrder.getMobile());
        order.setNickname(userInfoOrder.getNickname());
        order.setMemberId(memberId);
        order.setCourseCover(courseInfoOrder.getCover());
        order.setCourseTitle(courseInfoOrder.getTitle());
        order.setTeacherName(courseInfoOrder.getTeacherName());
        order.setTotalFee(courseInfoOrder.getPrice());
        order.setStatus(0);//支付状态：（ 0：已支付，1：未支付 ）
        order.setPayType(1);//支付类型： 1：微信 ， 2：支付宝
        baseMapper.insert(order);

        //返回订单号
        return order.getOrderNo();

    }
}
