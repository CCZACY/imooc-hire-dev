package com.imooc.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ArticleVO {

    private String id;
    private String title;
    private String content;
    private String articleCover;
    private String publishAdminId;
    private LocalDateTime publishTime;
    private String publisher;
    private String publisherFace;
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    private long readCounts;

}
