package com.amiao.eduservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan(basePackages = {"com.amiao"})
@SpringBootApplication
@EnableDiscoveryClient //nacos注册
@EnableFeignClients
public class Service_edu_Main8001 {
    public static void main(String[] args) {
        SpringApplication.run(Service_edu_Main8001.class,args);
    }
}
