package com.luoluo.acquisition.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.io.Serializable;
import java.util.Date;

/**
 * ES 实体类
 */
@Document(indexName = "report", type = "news" , shards = 12, replicas = 2)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NewsDto implements Serializable {
    @Id
    private Long Id;

    @Field(type = FieldType.Text)
    private String pagetype;

    @Field(type = FieldType.Text)
    private Long zfs;

    @Field(type = FieldType.Text)
    private Long fss;

    @Field(type = FieldType.Text)
    private String title;

    @Field(type = FieldType.Text)
    private String titlemd5;

    @Field(type = FieldType.Text,analyzer = "ik_max_word", searchAnalyzer = "ik_max_word")
    private String content;

    @Field(type = FieldType.Long)
    private Long pls;

    @Field( type = FieldType.Date,format = DateFormat.custom,pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern ="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private String publishtime;

    @Field(type = FieldType.Text)
    private String sitename;

    @Field(type = FieldType.Text)
    private String pageurl;

    @Field(type = FieldType.Text)
    private String guidhbase;

    @Field(type = FieldType.Text)
    private Integer dzs;

    @Field(type = FieldType.Text)
    private String guidsolr;

    @Field(type = FieldType.Text)
    private String poster;

    @Field(type = FieldType.Date)
    private Date createTime;

}
