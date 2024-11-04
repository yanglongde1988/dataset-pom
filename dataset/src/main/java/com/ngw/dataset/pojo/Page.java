package com.ngw.dataset.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Page {

    /**
     * 当前页
     */
    private Integer current;

    /**
     * 每页数
     */
    private Integer size;

    /**
     * 总页数
     */
    private Integer pages;

    /**
     * 总记录数
     */
    private Integer total;

}
