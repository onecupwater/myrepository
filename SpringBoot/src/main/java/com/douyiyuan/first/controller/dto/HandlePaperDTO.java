package com.douyiyuan.first.controller.dto;

import lombok.Data;

import java.util.List;

@Data
public class HandlePaperDTO {
    private List<Integer> questionIds;
    private Integer paperId;
}
