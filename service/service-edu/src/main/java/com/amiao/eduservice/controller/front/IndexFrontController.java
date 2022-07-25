package com.amiao.eduservice.controller.front;

import com.amiao.commonutils.R;
import com.amiao.eduservice.entity.EduCourse;
import com.amiao.eduservice.entity.EduTeacher;
import com.amiao.eduservice.service.EduCourseService;
import com.amiao.eduservice.service.EduTeacherService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
//@CrossOrigin
@RequestMapping("/eduservice/indexfront")
public class IndexFrontController {

    @Autowired
    private EduCourseService eduCourseService;
    @Autowired
    private EduTeacherService eduTeacherService;

    //查询前8条热门课程，查询前4条名师
    @Cacheable(value = "courseTeacher",key = "'selectCourseTeacherList'")
    @GetMapping("/index")
    public R index(){
        //查询前8条热门课程
        QueryWrapper<EduCourse> wrapper=new QueryWrapper<>();
        wrapper.orderByDesc("view_count");
        wrapper.last("limit 8");
        List<EduCourse> eduCourseList=eduCourseService.list(wrapper);

        //查询前4条名师
        QueryWrapper<EduTeacher> wrapperTeacher=new QueryWrapper<>();
        wrapperTeacher.orderByDesc("id");
        wrapperTeacher.last("limit 4");
        List<EduTeacher> eduTeacherList=eduTeacherService.list(wrapperTeacher);

        return R.ok().data("eduList",eduCourseList).data("teacherList",eduTeacherList);

    }
}
