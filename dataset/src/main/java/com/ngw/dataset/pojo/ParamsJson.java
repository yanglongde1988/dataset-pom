package com.ngw.dataset.pojo;

import lombok.Data;

import java.util.List;


@Data
public class ParamsJson {

    private FilterParam filter;
    private List<ReqParam> req;
    private List<RespParam> resp;
    private List<GroupParam> group;
    private List<OrderParam> order;
    private List<PageParam> page;

}
