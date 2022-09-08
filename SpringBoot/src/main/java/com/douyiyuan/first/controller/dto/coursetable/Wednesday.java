package com.douyiyuan.first.controller.dto.coursetable;

import lombok.Data;

/**
 * coursetable包下的实体类只是为了课程表服务的，并不与数据库表互相对应
 */

@Data
public class Wednesday {

    private String courseName;

    private String room;

    private String teacher;
}
