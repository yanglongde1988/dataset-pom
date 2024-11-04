package com.ngw.dataset.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
public class Param {

    /**
     * 参数英文名 对外
     */
    private String paramEn;

    /**
     * 参数中文名 对外
     */
    private String paramCn;

    /**
     * 绑定参数英文 不对外
     */
    private String bindEn;

    /**
     * 参数类型 TEXT 文本型 NUMBER 数值型
     */
    private String paramType;

    /**
     * 参数注释
     */
    private String paramNotes;
}
