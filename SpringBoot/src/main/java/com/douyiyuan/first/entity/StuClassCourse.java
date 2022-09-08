package com.douyiyuan.first.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.ToString;

@Data
@TableName("stu_class_course")
@ToString
public class StuClassCourse {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private Integer stuClassId;

    private Integer courseId;

    private String weekDay;

    private String section;

    private String room;

    private Integer teacherId;

    @TableField(exist = false)
    private String courseName;

    @TableField(exist = false)
    private String teacher;

    @TableField(exist = false)
    private String time;
}
