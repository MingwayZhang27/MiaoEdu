package com.amiao.eduservice.controller.front;

import com.amiao.commonutils.R;
import com.amiao.eduservice.entity.EduCourse;
import com.amiao.eduservice.entity.EduTeacher;
import com.amiao.eduservice.service.EduCourseService;
import com.amiao.eduservice.service.EduTeacherService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/eduservice/teacherfront")
//@CrossOrigin
public class TeacherFrontController {
    @Autowired
    private EduTeacherService eduTeacherService;
    @Autowired
    private EduCourseService eduCourseService;

    //1 分页查询讲师的方法
    @PostMapping("getTeacherFrontList/{page}/{limit}")
    public R getTeacherFrontList(@PathVariable long page,@PathVariable long limit){
        Page<EduTeacher> pageTeacher=new Page<>(page,limit);
        Map<String,Object> map=eduTeacherService.getTeacherFrontList(pageTeacher);

        //返回分页所以数据
        return R.ok().data(map);
    }

    //2 讲师详情的功能
    @GetMapping("getTeacherFrontInfo/{teacherId}")
    public R getTeacherFrontInfo(@PathVariable String teacherId){
        //1 根据讲师id查询讲师基本信息
        EduTeacher eduTeacher = eduTeacherService.getById(teacherId);

        //2 根据讲师id查询所讲课程
        QueryWrapper<EduCourse> wrapper=new QueryWrapper<>();
        wrapper.eq("teacher_id",teacherId);
        List<EduCourse> courseList = eduCourseService.list(wrapper);

        return R.ok().data("teacher",eduTeacher).data("courseList",courseList);
    }
}

