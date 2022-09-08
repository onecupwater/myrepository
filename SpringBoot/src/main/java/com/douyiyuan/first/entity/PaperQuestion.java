package com.douyiyuan.first.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("paper_question")
public class PaperQuestion {

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    private Integer paperId;
    private Integer questionId;
}
