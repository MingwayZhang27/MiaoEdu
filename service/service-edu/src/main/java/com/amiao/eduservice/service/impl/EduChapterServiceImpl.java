package com.amiao.eduservice.service.impl;

import com.amiao.eduservice.entity.EduChapter;
import com.amiao.eduservice.entity.EduVideo;
import com.amiao.eduservice.entity.chapter.ChapterVo;
import com.amiao.eduservice.entity.chapter.VideoVo;
import com.amiao.eduservice.mapper.EduChapterMapper;
import com.amiao.eduservice.service.EduChapterService;
import com.amiao.eduservice.service.EduVideoService;
import com.amiao.servicebase.exceptionhandler.AmiaoException;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 课程 服务实现类
 * </p>
 *
 * @author amiao
 * @since 2022-07-06
 */
@Service
public class EduChapterServiceImpl extends ServiceImpl<EduChapterMapper, EduChapter> implements EduChapterService {

    @Autowired
    private EduVideoService eduVideoService;

    @Override
    public List<ChapterVo> getChapterVideoByCourseId(String courseId) {
        //最终要的数据列表
        ArrayList<ChapterVo> finalChapterVos = new ArrayList<>();

        //查询章节信息
        QueryWrapper<EduChapter> wrapper = new QueryWrapper<>();
        wrapper.eq("course_id",courseId);
        List<EduChapter> eduChapters = baseMapper.selectList(wrapper);

        //查询小节信息
        QueryWrapper<EduVideo> wrapper1 = new QueryWrapper<>();
        wrapper1.eq("course_id",courseId);
        List<EduVideo> eduVideos = eduVideoService.list(wrapper1);

        //填充章节vo数据
        for (int i = 0; i < eduChapters.size(); i++) {
            EduChapter chapter = eduChapters.get(i);

            //创建章节vo对象
            ChapterVo chapterVo = new ChapterVo();
            BeanUtils.copyProperties(chapter,chapterVo);
            finalChapterVos.add(chapterVo);

            //填充课时vo对象
            ArrayList<VideoVo> finalVideoVos = new ArrayList<>();
            for (int j = 0; j < eduVideos.size(); j++) {
                EduVideo video = eduVideos.get(j);

                if (chapter.getId().equals(video.getChapterId())){
                    VideoVo videoVo = new VideoVo();
                    BeanUtils.copyProperties(video,videoVo);
                    finalVideoVos.add(videoVo);
                }
            }

            chapterVo.setChildren(finalVideoVos);

        }
        return finalChapterVos;

    }

    @Override
    public boolean deleteChapter(String chapterId) {
        //根据chapterId查询小节表，如果查询有数据，则不删除
        QueryWrapper<EduVideo> wrapper=new QueryWrapper<>();
        wrapper.eq("chapter_id",chapterId);
        int count = eduVideoService.count(wrapper);

        //判断
        if(count>0){//查询出小节，不进行删除
            throw new AmiaoException(20001,"不能删除，请先删除小节");
        }else {//不能查询出数据，进行删除
            int result = baseMapper.deleteById(chapterId);
            //删除成功，result>0
            //删除失败，result=0
            return result>0;
        }
    }

    //2 根据课程id删除章节
    @Override
    public void removeChapterByCourseId(String courseId) {
        QueryWrapper<EduChapter> wrapper=new QueryWrapper<>();
        wrapper.eq("course_id",courseId);
        baseMapper.delete(wrapper);
    }
}
