package com.ngw.dataset.pojo;

import lombok.Data;

@Data
public class RespParam extends Param{

    /**
     * 是否导出 TRUE 导出 FALSE 不导出
     */
    private String isExport;

    /**
     * 是否导出 TRUE 导出 FALSE 不导出
     */
    private String dictItem;
}
