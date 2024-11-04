package com.ngw.dataset.pojo;

import lombok.Data;

@Data
public class OrderParam extends Param{

    /**
     * 排序规则：ASC升序 DESC降序
     */
    private String orderType;
}
