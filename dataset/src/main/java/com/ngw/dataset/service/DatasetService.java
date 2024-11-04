package com.ngw.dataset.service;

import com.ngw.dataset.pojo.Dataset;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ngw.dataset.pojo.DatasetResult;
import com.ngw.dataset.pojo.ParamsJson;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface DatasetService extends IService<Dataset> {

    Object list(Integer pageNum, Integer pageSize, String text, String datasetType);

    void check(Dataset dataset) throws SQLException;

    Dataset getByCode(String datasetCode);

    DatasetResult result(String dsCode, ParamsJson paramsJson, Map<String, Dataset> datasetMap) throws SQLException;

    void export(String dsCode, ParamsJson paramsJson);

    void exportMore(List<Dataset> datasets);
}
