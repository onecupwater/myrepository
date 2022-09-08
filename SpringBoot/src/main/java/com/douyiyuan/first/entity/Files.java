package com.douyiyuan.first.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
@TableName(value = "file")
public class Files {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private String name;

    private String type;

    private Long size;

    private String url;

    /**
     * 图片的md5值，用于验证是否同一张图片
     */

    private String md5;

    /**
     * 是否删除 ( 0 代表false   1代表true )
     */
    private Boolean isDelete;

    /**
     * 是否可用 ( 0 代表false   1代表true )
     */
    private Boolean enable;
}
