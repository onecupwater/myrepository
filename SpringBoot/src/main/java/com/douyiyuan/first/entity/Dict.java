package com.douyiyuan.first.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 对应数据库的dict字典表，里面存放图标。。等等其他零碎物件
 */
@Data
@TableName(value = "dict")
public class Dict implements Serializable {

    private static final long serialVersionUID = 1L;

    private String name;

    private String value;

    private String description;

    private String type;
}
