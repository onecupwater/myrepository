package com.douyiyuan.first.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 视频信息表
 * </p>
 *
 * @author baomidou
 * @since 2022-07-27
 */
@Getter
@Setter
  public class Video implements Serializable {

    private static final long serialVersionUID = 1L;

      /**
     * id
     */
        @TableId(value = "id", type = IdType.AUTO)
      private Integer id;

      /**
     * 名称
     */
      private String name;

      /**
     * 视频后缀名
     */
      private String type;

      /**
     * 大小(kb)
     */
      private Long size;

      /**
     * 封面
     */
      private String cover;

      /**
     * 视频路径
     */
      private String url;

      /**
     * 状态
     */
      private String state;

      /**
     * 类型
     */
      private String sort;

      /**
     * 更新时间
     */
      @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
      private LocalDateTime updateTime;

      /**
      * 视频的md5
      */
      private String md5;

      /**
      * 封面的md5
      */
      private String coverMd5;

      /**
      * uuid
      */
      private String uuid;


}
