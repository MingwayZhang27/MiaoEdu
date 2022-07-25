package com.amiao.eduservice.service;

import com.amiao.eduservice.entity.EduVideo;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 课程视频 服务类
 * </p>
 *
 * @author amiao
 * @since 2022-07-06
 */
public interface EduVideoService extends IService<EduVideo> {
    //1 根据课程id删除小节
    void removeVideoByCourseId(String courseId);
}
