package com.amiao.eduservice.service.impl;

import com.amiao.eduservice.client.VodClient;
import com.amiao.eduservice.entity.EduVideo;
import com.amiao.eduservice.mapper.EduVideoMapper;
import com.amiao.eduservice.service.EduVideoService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 课程视频 服务实现类
 * </p>
 *
 * @author amiao
 * @since 2022-07-06
 */
@Service
public class EduVideoServiceImpl extends ServiceImpl<EduVideoMapper, EduVideo> implements EduVideoService {

    @Autowired
    private VodClient vodClient;

    //1 根据课程id删除小节
    //TODO 删除小节，需要删除对应的视频
    @Override
    public void removeVideoByCourseId(String courseId) {
        //1 根据课程id查询课程所有的视频id
        QueryWrapper<EduVideo> wrapperVideo=new QueryWrapper<>();
        wrapperVideo.eq("course_Id",courseId);
        //只需要视频的id，不需要全部视频信息
        wrapperVideo.select("video_source_id");
        List<EduVideo> eduVideoList = baseMapper.selectList(wrapperVideo);

        //List<EduVideo>变成List<String>
        List<String> videoIds=new ArrayList<>();
        for (int i = 0; i < eduVideoList.size(); i++) {
            EduVideo eduVideo = eduVideoList.get(i);
            String videoSourceId = eduVideo.getVideoSourceId();
            if(!StringUtils.isEmpty(videoSourceId)){
                //转成完成后放到videoIds集合里面
                videoIds.add(videoSourceId);
            }
        }
        //根据多个视频id，删除多个视频
        if (videoIds.size()>0){
            vodClient.deleteBatch(videoIds);
        }
        QueryWrapper<EduVideo> wrapper=new QueryWrapper<>();
        wrapper.eq("course_id",courseId);
        baseMapper.delete(wrapper);
    }
}
