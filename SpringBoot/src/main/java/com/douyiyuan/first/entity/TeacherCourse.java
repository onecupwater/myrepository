package com.douyiyuan.first.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@TableName(value = "teacher_course")
@Data
public class TeacherCourse {

    private Integer teacherId;

    private Integer courseId;
}
