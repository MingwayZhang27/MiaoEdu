package com.amiao.staservice.schedule;

import com.amiao.staservice.service.StatisticsDailyService;
import com.amiao.staservice.utils.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class ScheduledTask {
    @Autowired
    private StatisticsDailyService statisticsDailyService;

    //0/5 * * * * ?表示每隔五秒去执行一次
    //指定cron表达式规则
//    @Scheduled(cron = "0/5 * * * * ?")
//    public void task1(){
//        System.out.println("********************task1执行了");
//    }

    //每天凌晨一点执行方法，把前一天数据进行数据查询添加
    @Scheduled(cron = "0 0 1 * * ? ")
    public void task2(){
        //要计算的是前一天，amount=-1
        statisticsDailyService.registerCount(DateUtil.formatDate(DateUtil.addDays(new Date(),-1)));
    }
}
