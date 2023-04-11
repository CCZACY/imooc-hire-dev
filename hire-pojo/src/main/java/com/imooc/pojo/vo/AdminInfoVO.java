package com.imooc.pojo.vo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class AdminInfoVO {
    private String id;
    private String username;
    private String remark;
    private String face;
    private LocalDateTime createTime;
    private LocalDateTime updatedTime;
}
