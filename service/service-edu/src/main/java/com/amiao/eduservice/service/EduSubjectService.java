package com.amiao.eduservice.service;

import com.amiao.eduservice.entity.EduSubject;
import com.amiao.eduservice.entity.subject.OneSubject;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * <p>
 * 课程科目 服务类
 * </p>
 *
 * @author amiao
 * @since 2022-06-24
 */
public interface EduSubjectService extends IService<EduSubject> {

    //添加课程分类
    void saveSubject(MultipartFile file, EduSubjectService subjectService);

    //课程分类列表(树型)
    List<OneSubject> getAllOneTwoSubject();
}
