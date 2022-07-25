package com.amiao.eduservice.service.impl;

import com.alibaba.excel.EasyExcel;
import com.amiao.eduservice.entity.EduSubject;
import com.amiao.eduservice.entity.excel.SubjectData;
import com.amiao.eduservice.entity.subject.OneSubject;
import com.amiao.eduservice.entity.subject.TwoSubject;
import com.amiao.eduservice.listener.SubjectExcelListener;
import com.amiao.eduservice.mapper.EduSubjectMapper;
import com.amiao.eduservice.service.EduSubjectService;
import com.amiao.servicebase.exceptionhandler.AmiaoException;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * <p>
 * 课程科目 服务实现类
 * </p>
 *
 * @author amiao
 * @since 2022-06-24
 */
@Service
public class EduSubjectServiceImpl extends ServiceImpl<EduSubjectMapper, EduSubject> implements EduSubjectService {

    //添加课程分类
    @Override
    public void saveSubject(MultipartFile file, EduSubjectService subjectService) {
        try {
            //文件输入流
            InputStream in=file.getInputStream();
            //调用方法读取
            EasyExcel.read(in, SubjectData.class,new SubjectExcelListener(subjectService)).sheet().doRead();
        }catch (Exception e){
            e.printStackTrace();
            throw new AmiaoException(20002,"添加课程分类失败");
        }
    }

    //课程分类列表(树型)
    @Override
    public List<OneSubject> getAllOneTwoSubject() {
        //1、查询出所有的一级分类 parent_id=0
        QueryWrapper<EduSubject> wrapperOne=new QueryWrapper<>();
        wrapperOne.eq("parent_id","0");
        List<EduSubject> oneSubjectList = baseMapper.selectList(wrapperOne);

        //2、查询出所有的二级分类 parent_id！=0
        QueryWrapper<EduSubject> wrapperTwo=new QueryWrapper<>();
        wrapperOne.ne("parent_id","0");
        List<EduSubject> twoSubjectList = baseMapper.selectList(wrapperTwo);

        //创建list集合，用于存储最终封装数据
        List<OneSubject> finalSubjectList=new ArrayList<>();

        //3、封装一级分类
        //查询出来所有的一级分类list集合遍历，得到每个一级分类对象，获取每个一级分类对象值
        //封装到要求的最终list集合中
        for (int i = 0; i < oneSubjectList.size(); i++) { //遍历oneSubjectList集合
            //得到oneSubjectsList中每个eduSubject对象
            EduSubject eduSubject = oneSubjectList.get(i);

            //把eduSubject里面值获取出来，放到oneSubject对象中去
            OneSubject oneSubject = new OneSubject();
//            oneSubject.setId(eduSubject.getId());
//            oneSubject.setTitle(eduSubject.getTitle());

            //把eduSubject值复制到oneSubject中去【要求两个类的复制注入的属性名一致】
            BeanUtils.copyProperties(eduSubject,oneSubject);

            //多个OneSubject放到finalSubjectList里面
            finalSubjectList.add(oneSubject);

            //在一级分类循环遍历查询所有的二级分类
            //创建list集合封装每个一级分类的二级分类
            List<TwoSubject> twoFinalSubjects = new ArrayList<>();
            //遍历二级分类list集合
            for (int m = 0; m < twoSubjectList.size(); m++) {
                //获取每个二级分类
                EduSubject tSubject = twoSubjectList.get(m);
                //判断二级分类parentid和一级分类是否一样
                if(tSubject.getParentId().equals(eduSubject.getId())){
                    //把tSubject复制到TwoSubject里面
                    TwoSubject twoSubject=new TwoSubject();
                    BeanUtils.copyProperties(tSubject,twoSubject);
                    twoFinalSubjects.add(twoSubject);
                }
            }
            //把一级下面所有二级分类放到oneSubject里面
            oneSubject.setChildren(twoFinalSubjects);
        }
        return finalSubjectList;
    }

}
