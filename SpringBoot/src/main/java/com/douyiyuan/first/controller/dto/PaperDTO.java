package com.douyiyuan.first.controller.dto;

import lombok.Data;
import lombok.ToString;

import java.util.List;

@Data
@ToString
public class PaperDTO {

    private Integer paperId;
    private Integer courseId;
    private Integer choiceNum;
    private Integer judgeNum;
    private Integer fillNum;
    private List<Integer> questionIds;
}
