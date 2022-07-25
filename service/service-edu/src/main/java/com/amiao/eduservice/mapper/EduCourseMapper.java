package com.amiao.eduservice.mapper;

import com.amiao.eduservice.entity.EduCourse;
import com.amiao.eduservice.entity.frontvo.CourseWebVo;
import com.amiao.eduservice.entity.vo.CoursePublishVo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * <p>
 * 课程 Mapper 接口
 * </p>
 *
 * @author amiao
 * @since 2022-07-06
 */
public interface EduCourseMapper extends BaseMapper<EduCourse> {
    public CoursePublishVo getPublishCourseInfo(String courseId);

    //根据课程id，编写sql语句查询课程信息
    CourseWebVo getBaseCourseInfo(String courseId);
}
