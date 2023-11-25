package com.atts.attschedule.service.impl;

import com.atts.attschedule.Entity.CourseSchedule;
import com.atts.attschedule.service.AtsService;
import com.atts.attschedule.utils.FindCourseString;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 课表管理接口
 */
@Service
public class AtsServiceImpl implements AtsService {

    private FindCourseString findCousrse = new FindCourseString();

    @Override
    public List<CourseSchedule> list() throws InterruptedException {
        return findCousrse.getCourse();
    }
}
