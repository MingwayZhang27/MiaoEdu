package com.amiao.educms.controller;


import com.amiao.commonutils.R;
import com.amiao.educms.entity.CrmBanner;
import com.amiao.educms.service.CrmBannerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>
 * 前台banner显示
 * </p>
 *
 * @author amiao
 * @since 2022-07-14
 */
@RestController
@RequestMapping("/educms/bannerFront")
//@CrossOrigin
public class BannerFontController {

    @Autowired
    private CrmBannerService crmBannerService;

    //查询所有banner
    @GetMapping("getAllBanner")
    public R getAllBanner(){
        List<CrmBanner> list=crmBannerService.selectAllBanner();
        return R.ok().data("list",list);
    }
}

