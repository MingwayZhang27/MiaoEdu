package com.amiao.eduservice.controller;


import com.amiao.commonutils.R;
import com.amiao.eduservice.entity.vo.TeacherQuery;
import com.amiao.eduservice.service.EduTeacherService;
import com.amiao.eduservice.entity.EduTeacher;
import com.amiao.servicebase.exceptionhandler.AmiaoException;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 讲师 前端控制器
 * </p>
 *
 * @author amiao
 * @since 2022-05-19
 */
@RestController
@RequestMapping("/eduservice/edu-teacher")
@Api(tags="讲师管理")
////@CrossOrigin
public class EduTeacherController {
    //访问地址：http://localhost:8001/eduservice/edu-teacher/findAll
    //把service注入
    @Autowired
    private EduTeacherService eduTeacherService;

    //1、查询讲师表所有数据
    @GetMapping("/findAll")
    @ApiOperation(value = "所有讲师列表")
    public R findAllTeacher(){
        List<EduTeacher> list = eduTeacherService.list(null);
        return R.ok().data("items",list);
    }

    //2、讲师逻辑删除
    @DeleteMapping("{id}")
    @ApiOperation(value = "根据ID删除讲师")
    public R removeTeacher(
            @ApiParam(name="id", value = "讲师ID", required = true)
            @PathVariable String id){
        boolean flag = eduTeacherService.removeById(id);
        if (flag){
            return R.ok();
        }else{
            return R.error();
        }
    }

    //3、分页查询教师的方法
    //page：当前页
    //limit：每页显示记录数
    @GetMapping("pageTeacher/{page}/{limit}")
    @ApiOperation(value = "分页查询讲师")
    public R pageListTeacher(@PathVariable long page,
                             @PathVariable long limit){

        //创建page对象
        Page<EduTeacher> pageTeacher=new Page<>(page,limit);
        //调用方法实现分页
        //分页查询，查完后，会将数据封装在pageTeacher中
        eduTeacherService.page(pageTeacher,null);

        long total = pageTeacher.getTotal();
        List<EduTeacher> records = pageTeacher.getRecords();

//        try {
//            int a=1/0;
//        }catch (Exception e){
//            throw new AmiaoException(20001,"执行了自定义异常处理。。。");
//        }

//        格式一：
//        Map map=new HashMap();
//        map.put("total",total);
//        map.put("rows",records);
//        return R.ok().data(map);

        //格式二：
        return R.ok().data("total",total).data("rows",records);
    }

    //4、条件查询带分页的方法
    @PostMapping("pageTeacherCondition/{page}/{limit}")
    public R pageTeacherCondition(
            @PathVariable long page, @PathVariable long limit,
            @RequestBody(required = false) TeacherQuery teacherQuery){
        //创建page对象
        Page<EduTeacher> pageTeacher=new Page<>(page,limit);

        //构建条件
        QueryWrapper<EduTeacher> wrapper=new QueryWrapper<>();
        //多条件组合查询
        //mybatis学过 动态sql
        String name = teacherQuery.getName();
        Integer level=teacherQuery.getLevel();
        String begin = teacherQuery.getBegin();
        String end = teacherQuery.getEnd();

        //判断条件值是否为空，如果不为空，拼接条件
        if(!StringUtils.isEmpty(name)){
            //构建条件
            wrapper.like("name",name);
        }
        if(!StringUtils.isEmpty(level)){
            wrapper.eq("level",level);
        }
        if(!StringUtils.isEmpty(begin)){
            wrapper.ge("gmt_create",begin);
        }
        if(!StringUtils.isEmpty(end)){
            wrapper.le("gmt_modified",end);
        }

        //排序
        wrapper.orderByDesc("gmt_create");

        //调用方法实现分页查询
        //调用方法实现多条件分页查询
        eduTeacherService.page(pageTeacher,wrapper);
        //获取总记录数
        long total = pageTeacher.getTotal();
        //获取查询到的数据
        List<EduTeacher> records = pageTeacher.getRecords();
        System.out.println(total);
        return R.ok().data("total",total).data("rows",records);
    }

    //添加讲师接口的方法
    @ApiModelProperty(value = "新增讲师")
    @PostMapping("addTeacher")
    public R addTeacher(@RequestBody EduTeacher eduTeacher){
        boolean save = eduTeacherService.save(eduTeacher);
        if(save){
            return R.ok();
        }else{
            return R.error();
        }
    }

    //根据讲师id进行查询
    @GetMapping("getTeacher/{id}")
    public R getTeacher(@PathVariable String id){
        EduTeacher eduTeacher = eduTeacherService.getById(id);
        return R.ok().data("teacher",eduTeacher);
    }

    //讲师修改功能
    @PostMapping("updateTeacher")
    public R updateTeacher(@RequestBody EduTeacher eduTeacher){
        boolean flag = eduTeacherService.updateById(eduTeacher);
        if(flag){
            return R.ok();
        }else{
            return R.error();
        }
    }
}

