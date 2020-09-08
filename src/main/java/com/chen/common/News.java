package com.chen.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class News implements Serializable {
    private String pagetype;

    private Long zfs;

    private Long fss;

    private String title;

    private String titlemd5;

    private String content;

    private Long pls;

    private String publishtime;

    private String sitename;

    private String pageurl;

    private String guidhbase;

    private Integer dzs;

    private String guidsolr;

    private String poster;

    private String Id;

    private String createTime;
}
