package com.amiao.msmservice.service;

import java.util.Map;

//发送短信的方法
public interface MsmService {
    boolean send(Map<String, Object> param, String phone);
}
