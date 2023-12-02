package com.atts.attschedule.Entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * <h3>用户实体类</h3>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    /**
     * <h3>ID</h3>
     * <h3>用户课程</h3>
     * <h3>用户姓名</h3>
     * <h3>用户Id</h3>
     * <h3>用户密码</h3>
     */
    private int ID;
    private List<CourseSchedule> CourseList;
    private String UserName;
    private String UserId;
    private String UserPassword;

}
