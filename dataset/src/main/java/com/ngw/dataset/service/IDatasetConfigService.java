package com.ngw.dataset.service;

import com.ngw.dataset.pojo.*;

import java.sql.SQLException;
import java.util.List;

public interface IDatasetConfigService {

//    List<RespParam> filterParam(String dataSourceCode, String datasetEntity);

    List<ReqParam> reqParams(String text);

    List<RespParam> respParams(String dataSourceCode, String sqlText, List<ReqParam> params) throws SQLException;

//    List<GroupParam> groupParams();

//    List<OrderParam> orderParams();

    List<PageParam> pageParams();

    DatasetResult test(String dataSourceCode, String datasetEntity, ParamsJson paramsJson) throws SQLException;

    DatasetResult result(Dataset dataset, ParamsJson paramsJson) throws SQLException;

    String buildResultSql(String dsEntity, ParamsJson configParamsJson, ParamsJson paramsJson, String daCode) throws SQLException;
}
