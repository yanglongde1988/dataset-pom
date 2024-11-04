package com.ngw.fusion.common.base;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public abstract class BaseEntity implements Serializable {

    /**
     * 获取当前系统时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    @ExcelIgnore
    private LocalDateTime createTime;


    /**
     * 获取当前系统时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    @ExcelIgnore
    private LocalDateTime updateTime;



    /**
     * 禁止硬删除数据
     默认0是有效，1是删除
     */
    @TableField(value = "del_flag" , fill = FieldFill.INSERT)
    @ExcelIgnore
    private Integer delFlag;


    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
