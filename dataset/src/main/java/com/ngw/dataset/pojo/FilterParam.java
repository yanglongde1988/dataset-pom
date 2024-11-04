package com.ngw.dataset.pojo;

import lombok.Data;

import java.util.List;

/**
 *
 */
@Data
public class FilterParam extends Param{

    /**
     * 逻辑关系 AND 和 OR 或
     */
    private String logicType;

    /**
     * 逻辑主体列表
     */
    private List<FilterParam> filter;

    /**
     * 比较关系  参见操作枚举对象Operator
     */
    private String relationType;

    /**
     * 参数比较值
     */
    private String value;
}
