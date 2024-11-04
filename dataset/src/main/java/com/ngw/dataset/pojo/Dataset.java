package com.ngw.dataset.pojo;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import com.ngw.fusion.common.base.BaseEntity;
import lombok.Data;

/**
 * 数据集信息表
 * @TableName t_dataset_info
 */
@TableName(value ="t_manage_dataset")
@Data
public class Dataset extends BaseEntity {
    /**
     * 数据集ID
     */
    @TableId
    private String id;

    /**
     * 数据集编码
     */
    private String dsCode;

    /**
     * 数据集名称
     */
    private String dsName;

    /**
     * 数据集类型
sql、json、table、http
     */
    private String dsType;

    /**
     * 数据源编码code
sql、table不为空，json、http不需要
     */
    @TableField(updateStrategy = FieldStrategy.ALWAYS)
    private String dsSourceCode;

    /**
     * 待执行sql脚本
     */
    private String dsSql;

    /**
     * 数据集描述
     */
    private String dsDesc;

    /**
     * 参数JSON
     */
    private String paramsJson;

    /**
     * 数据集内容
     sql为sql查询脚本，table为表名，json为json字符串，http为url
     */
    private String dsEntity;

}