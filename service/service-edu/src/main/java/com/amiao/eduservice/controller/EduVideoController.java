package com.amiao.eduservice.controller;


import com.amiao.commonutils.R;
import com.amiao.eduservice.client.VodClient;
import com.amiao.eduservice.entity.EduVideo;
import com.amiao.eduservice.service.EduVideoService;
import com.amiao.servicebase.exceptionhandler.AmiaoException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 课程视频 前端控制器
 * </p>
 *
 * @author amiao
 * @since 2022-07-06
 */
@RestController
@RequestMapping("/eduservice/edu-video")
//@CrossOrigin
public class EduVideoController {
    @Autowired
    private EduVideoService eduVideoService;

    //注入vodClient
    @Autowired
    private VodClient vodClient;

    //添加小节
    @PostMapping("addVideo")
    public R addVideo(@RequestBody EduVideo eduVideo){
        eduVideoService.save(eduVideo);
        return R.ok();
    }
    //删除小节
    //TODO
    @DeleteMapping("{id}")
    public R deleteVideo(@PathVariable String id){
        //这里的id是小节id，根据小节id删除视频id
        EduVideo eduVideo = eduVideoService.getById(id);
        String videoSourceId = eduVideo.getVideoSourceId();

        //先判断小节里面是否有视频id
        if(!StringUtils.isEmpty(videoSourceId)){
            //根据视频id，远程调用实现视频删除
            R result = vodClient.removeAlyVideo(videoSourceId);
            if (result.getCode()==20001){
                throw new AmiaoException(20001,"删除视频失败，熔断器...");
            }
        }

        //删除视频完成之后，再删除小节
        eduVideoService.removeById(id);
        return R.ok();
    }
    //修改小节 TODO

}

