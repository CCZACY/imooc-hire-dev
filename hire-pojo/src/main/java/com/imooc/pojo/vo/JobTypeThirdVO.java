package com.imooc.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class JobTypeThirdVO implements Serializable {

    private String thirdLevelId;
    private String thirdLevelName;
    private Integer thirdLevelSort;
}
