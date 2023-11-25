package com.atts.attschedule.service;

import com.atts.attschedule.Entity.CourseSchedule;

import java.util.List;

/**
 * 课表管理接口
 */
public interface AtsService {
    /**
     * 返回课表数据
     * @return
     */
    List<CourseSchedule> list() throws InterruptedException;
}
