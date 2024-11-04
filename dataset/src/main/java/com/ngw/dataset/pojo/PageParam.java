package com.ngw.dataset.pojo;

import lombok.Data;


@Data
public class PageParam extends Param{

    /**
     * 是否必填 TRUE 必填 FALSE 非必填
     */
    private String isMust;

    /**
     * 参数值
     */
    private String value;

}
