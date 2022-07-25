package com.amiao.staservice.service.impl;

import com.amiao.commonutils.R;
import com.amiao.staservice.client.UcenterClient;
import com.amiao.staservice.entity.StatisticsDaily;
import com.amiao.staservice.mapper.StatisticsDailyMapper;
import com.amiao.staservice.service.StatisticsDailyService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * <p>
 * 网站统计日数据 服务实现类
 * </p>
 *
 * @author amiao
 * @since 2022-07-20
 */
@Service
public class StatisticsDailyServiceImpl extends ServiceImpl<StatisticsDailyMapper, StatisticsDaily> implements StatisticsDailyService {

    @Autowired
    private UcenterClient ucenterClient;

    //统计某一天注册人数,生成统计数据
    @Override
    public void registerCount(String day) {
        //添加记录之前删除表相同日期的数据
        QueryWrapper<StatisticsDaily> wrapper=new QueryWrapper<>();
        wrapper.eq("date_calculated",day);
        baseMapper.delete(wrapper);

        //远程调用得到某一天注册人数
        R registerR = ucenterClient.countRegister(day);
        Integer countRegister = (Integer)registerR.getData().get("countRegister");

        //其他的数据类似，也是通过远程调用，获取数据返回即可，下面使用随机数模拟

        //把获取数据添加到数据库，统计分析表
        StatisticsDaily sta=new StatisticsDaily();
        sta.setRegisterNum(countRegister);//注册人数
        sta.setDateCalculated(day);//统计日期
        sta.setCourseNum(RandomUtils.nextInt(100,200));
        sta.setLoginNum(RandomUtils.nextInt(200,300));//登录数
        sta.setVideoViewNum(RandomUtils.nextInt(200,300));//视频流量数

        baseMapper.insert(sta);
    }

    //图表显示，返回两部分数据，日期json数组，数量json数组
    @Override
    public Map<String, Object> getShowData(String type, String begin, String end) {
        //根据条件查询对应数据
        QueryWrapper<StatisticsDaily> wrapper=new QueryWrapper<>();
        wrapper.between("date_calculated",begin,end);
        wrapper.select("date_calculated",type);
        List<StatisticsDaily> statisticsDailyList = baseMapper.selectList(wrapper);
        System.out.println(statisticsDailyList);
        //因为返回有两部分数据：日期 和 日期对应的数量
        //前端要求数组json结构,对应后端java代码是list集合
        //创建两个list集合，一个日期list，一个数量list
        List<String> date_calculatedList=new ArrayList<>();
        List<Integer> numDataList=new ArrayList<>();

        //遍历查询所有数据list集合，进行分装
        for (int i = 0; i < statisticsDailyList.size(); i++) {
            StatisticsDaily daily = statisticsDailyList.get(i);
            //封装日期的list集合
            date_calculatedList.add(daily.getDateCalculated());
            //封装对应type类型的数量
            switch (type){
                case "login_num":
                    numDataList.add(daily.getLoginNum());
                    break;
                case "register_num":
                    numDataList.add(daily.getRegisterNum());
                    break;
                case "video_view_num":
                    numDataList.add(daily.getVideoViewNum());
                    break;
                case "course_num":
                    numDataList.add(daily.getCourseNum());
                    break;
                default:
                    break;
            }
        }
        //把封装之后两个list集合放到map集合返回
        Map<String,Object> map=new HashMap<>();

        map.put("date_calculatedList",date_calculatedList);
        map.put("numDataList",numDataList);

        return map;
    }
}
