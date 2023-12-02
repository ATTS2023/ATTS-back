package com.atts.attschedule.Entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <h3>课程实体类</h3>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CourseSchedule {
    /**
     * <h3>课程名称</h3>
     * <h3>课程教师</h3>
     * <h3>课程地点</h3>
     * <h3>课程周次(第1-16周/第9-16周/...)</h3>
     * <h3>课程日期(周一/周二/..) 用1 2 3代替</h3>
     * <h3>课程节次(第5-6节/第1-2节)</h3>
     */
    String CourseName;
    String teacher;
    String classRoom;
    String WeekTimes;
    int WeekDate;
    String Session;

}
