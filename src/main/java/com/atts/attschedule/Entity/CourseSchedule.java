package com.atts.attschedule.Entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 课程实体类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CourseSchedule {
    /**
     * 课程名称
     * 授课老师
     * 教室
     * 上课周次(第1-16周/第9-16周/...)
     * 上课日期(周一/周二/..) 用1 2 3代替
     * 课程节次(第5-6节/第1-2节)
     */
    String CourseName;
    String teacher;
    String classRoom;
    String WeekTimes;
    int WeekDate;
    String Session;

}
