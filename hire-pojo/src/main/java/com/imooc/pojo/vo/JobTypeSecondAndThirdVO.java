package com.imooc.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class JobTypeSecondAndThirdVO implements Serializable {

    private String secondLevelId;
    private String secondLevelName;
    private Integer secondLevelSort;

    private List<JobTypeThirdVO> jobTypeThirdVOList;
}
