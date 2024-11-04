package com.ngw.dataset.pojo;

import lombok.Data;

@Data
public class ReqParam extends Param{

    /**
     * 是否必填 TRUE 必填 FALSE 非必填
     */
    private String isMust;

    /**
     * 比较关系 参见操作枚举对象Operator
     */
    private String relationType;

    /**
     * 参数值
     */
    private String value;
}
