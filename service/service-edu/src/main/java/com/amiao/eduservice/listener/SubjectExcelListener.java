package com.amiao.eduservice.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.amiao.eduservice.entity.EduSubject;
import com.amiao.eduservice.entity.excel.SubjectData;
import com.amiao.eduservice.service.EduSubjectService;
import com.amiao.servicebase.exceptionhandler.AmiaoException;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

public class SubjectExcelListener extends AnalysisEventListener<SubjectData> {
    //因为SubjectExcelListener不能交给spring进行ioc管理，需要自己手动new，不能注入其他对象
    //不能实现数据库操作
    public EduSubjectService subjectService;

    //无参
    public SubjectExcelListener() {
    }
    //有参，传递subjectService用于操作数据库
    public SubjectExcelListener(EduSubjectService subjectService) {
        this.subjectService = subjectService;
    }

    //读取excel内容，一行一行读取
    @Override
    public void invoke(SubjectData subjectData, AnalysisContext analysisContext) {
        if(subjectData == null){
            throw new AmiaoException(20001,"添加失败");
        }
        //一行一行读取，每次读取有两个值，第一个值一级分类，第二个值二级分类
        //判断是否有一级分类是否重复
        EduSubject existOneSubject = this.existOneSubject(subjectService, subjectData.getOneSubjectName());
        if (existOneSubject == null) { //没有相同的一级分类，进行添加
            existOneSubject = new EduSubject();
            existOneSubject.setParentId("0"); //设置一级分类id值，0代表为一级分类
            existOneSubject.setTitle(subjectData.getOneSubjectName());//设置一级分类名
            subjectService.save(existOneSubject);//给数据库添加一级分类
        }

        //获取一级分类的id值
        String pid = existOneSubject.getId();
        //判断是否有二级分类是否重复
        EduSubject existTwoSubject = this.existTwoSubject(subjectService, subjectData.getTwoSubjectName(), pid);
        if (existTwoSubject==null){//没有相同的二级分类，进行添加
            existTwoSubject = new EduSubject();
            existTwoSubject.setParentId(pid); //设置二级分类id值
            existTwoSubject.setTitle(subjectData.getTwoSubjectName());//设置二级分类名
            subjectService.save(existTwoSubject);//给数据库添加二级分类
        }
    }

    //判断一级分类不能重复添加
    private EduSubject existOneSubject(EduSubjectService subjectService,String name){
        QueryWrapper<EduSubject> wrapper = new QueryWrapper<>();
        wrapper.eq("title",name)
                .eq("parent_id","0");
        EduSubject oneSubject = subjectService.getOne(wrapper);
        return oneSubject;
    }

    //判断二级分类不能重复添加
    private EduSubject existTwoSubject(EduSubjectService subjectService,String name,String pid){
        QueryWrapper<EduSubject> wrapper = new QueryWrapper<>();
        wrapper.eq("title",name)
                .eq("parent_id",pid);
        EduSubject twoSubject = subjectService.getOne(wrapper);
        return twoSubject;
    }




    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {

    }
}
