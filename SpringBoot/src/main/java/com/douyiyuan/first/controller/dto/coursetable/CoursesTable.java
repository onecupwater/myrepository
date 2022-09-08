package com.douyiyuan.first.controller.dto.coursetable;

import com.douyiyuan.first.entity.StuClassCourse;
import lombok.Data;

/**
 * coursetable包下的实体类只是为了课程表服务的，并不与数据库表互相对应
 */

@Data
public class CoursesTable {

    private SectionTime sectionTime;

    private Monday monday;

    private Tuesday tuesday;

    private Wednesday wednesday;

    private Thursday thursday;

    private Friday friday;

    private Saturday saturday;

    private Sunday sunday;


}
