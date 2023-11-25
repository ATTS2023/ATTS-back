package com.atts.attschedule.controller;

import com.atts.attschedule.Entity.CourseSchedule;
import com.atts.attschedule.Entity.Result;
import com.atts.attschedule.service.AtsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 课表管理
 */
@Slf4j
@RequestMapping("/Ats")
@RestController
public class AtsController {
    @Autowired
    private AtsService atsService;

    @GetMapping
    public Result getCourse() throws InterruptedException {
        log.info("通过获取账号密码,查询并返回课表");
        List<CourseSchedule> list = atsService.list();
        return Result.success(list);
    }
}
