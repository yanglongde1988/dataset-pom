package com.ngw.dataset.service;

import com.ngw.dataset.pojo.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.sql.SQLException;
import java.util.List;

@Service
public class DatasetConfigService {

    public IDatasetConfigService iDatasetConfigService;

    @Resource(name = "sqlDatasetConfigServiceImpl")
    private IDatasetConfigService sqlDatasetService;

    @Resource(name = "tableDatasetConfigServiceImpl")
    private IDatasetConfigService tableDatasetService;

    @Resource(name = "jsonDatasetConfigServiceImpl")
    private IDatasetConfigService jsonDatasetService;

    public void getDatasetService(String datasetType) {
        Assert.isTrue(StringUtils.isNotBlank(datasetType), "数据集类型不能为空！");
        switch (datasetType) {
            case "sql":
                iDatasetConfigService = sqlDatasetService;
                break;
            case "table":
                iDatasetConfigService = tableDatasetService;
                break;
            case "json":
                iDatasetConfigService = jsonDatasetService;
                break;
            default:
                String message = String.format("不支持的数据集类型【%s】！", datasetType);
                Assert.isTrue(true, message);
        }
    }

    public List<PageParam> pageParams() {
        getDatasetService("sql");
        List<PageParam> pageParams = iDatasetConfigService.pageParams();
        return pageParams;
    }

    public List<ReqParam> reqParams(String datasetType, String text) {
        getDatasetService(datasetType);
        List<ReqParam> reqParams = iDatasetConfigService.reqParams(text);
        return reqParams;
    }

    public List<RespParam> respParams(String datasetType, String dataSourceCode, String sqlText, List<ReqParam> params) throws SQLException {
        getDatasetService(datasetType);
        List<RespParam> respParams = iDatasetConfigService.respParams(dataSourceCode, sqlText, params);
        return respParams;
    }

    public DatasetResult test(String datasetType, String dataSourceCode, String datasetEntity, ParamsJson paramsJson) throws SQLException {
        getDatasetService(datasetType);
        DatasetResult test = iDatasetConfigService.test(dataSourceCode, datasetEntity, paramsJson);
        return test;
    }

}
