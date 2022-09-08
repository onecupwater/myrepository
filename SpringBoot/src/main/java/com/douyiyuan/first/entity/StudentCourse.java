package com.douyiyuan.first.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@TableName(value = "student_course")
@Data
public class StudentCourse {

    private Integer studentId;

    private Integer courseId;
}
