package com.amiao.eduservice.service;

import com.amiao.eduservice.entity.EduChapter;
import com.amiao.eduservice.entity.chapter.ChapterVo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 课程 服务类
 * </p>
 *
 * @author amiao
 * @since 2022-07-06
 */
public interface EduChapterService extends IService<EduChapter> {

    List<ChapterVo> getChapterVideoByCourseId(String courseId);

    boolean deleteChapter(String chapterId);

    //2 根据课程id删除章节
    void removeChapterByCourseId(String courseId);
}

