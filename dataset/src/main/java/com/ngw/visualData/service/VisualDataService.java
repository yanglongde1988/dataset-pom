package com.ngw.visualData.service;

import cn.hutool.core.lang.Assert;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSON;
import com.ngw.dataset.pojo.Dataset;
import com.ngw.dataset.pojo.DatasetResult;
import com.ngw.dataset.pojo.ParamsJson;
import com.ngw.dataset.pojo.ReqParam;
import com.ngw.dataset.service.DatasetService;
import com.ngw.visualData.pojo.VisualData;
import com.ngw.fusion.common.dataBus.DataBus;
import com.ngw.fusion.common.dataBus.ResultCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.sql.SQLException;
import java.util.*;

@Service
public class VisualDataService {

//    @Value("${data-ingestion.url}")
    private String url;

    public Object list(String id, HttpServletRequest request) throws SQLException {
//        DataBus dataBus = getDataBus(id, request);
//        return dataBus.getData().get("data");
        return getData(id, request);
    }

    @Autowired
    private DatasetService datasetService;

    private List<Map<String, Object>> getData(String dsCode, HttpServletRequest request) throws SQLException {
        ParamsJson paramsJson = getParamsJson(request);
        DatasetResult result = datasetService.result(dsCode, paramsJson, new HashMap<>());
        return result.getResult();
    }

    private ParamsJson getParamsJson(HttpServletRequest request) {
        Map<String, String[]> parameterMap = request.getParameterMap();
        ParamsJson paramsJson = new ParamsJson();
        List<ReqParam> req = new ArrayList<>();
        paramsJson.setReq(req);
        parameterMap.forEach((key, values) -> {
            ReqParam reqParam = new ReqParam();
            reqParam.setParamEn(key);
            reqParam.setValue(values[0]);
            req.add(reqParam);
        });
        return paramsJson;
    }

    private DataBus getDataBus(String id, HttpServletRequest request) {
        Map<String, String[]> parameterMap = request.getParameterMap();
        Map<String, Object> paramsMap = mapFormat(parameterMap);
        String get = HttpUtil.get(url + id, paramsMap);
        DataBus dataBus = JSON.parseObject(get, DataBus.class);
        String code = dataBus.getCode();
        Assert.isTrue(ResultCode.SUCCESS.equals(code), dataBus.getMessage());
        return dataBus;
    }

    private Map<String, Object> mapFormat(Map<String, String[]> parameterMap) {
        Map<String, Object> paramsMap = new HashMap<>();
        parameterMap.forEach((key, values) -> paramsMap.put(key, values[0]));
        return paramsMap;
    }

    public Map<String, Object> map(String id, HttpServletRequest request) throws SQLException {
//        DataBus dataBus = getDataBus(id, request);
//        List<Map<String, Object>> list = (List<Map<String, Object>>) dataBus.getData().get("data");
        List<Map<String, Object>> list = getData(id, request);
        return list.get(0);
    }

    public Map<String, List> data(String id, HttpServletRequest request) throws SQLException {
//        DataBus dataBus = getDataBus(id, request);
//        List<Map<String, Object>> list = (List<Map<String, Object>>) dataBus.getData().get("data");
        List<Map<String, Object>> list = getData(id, request);
        Map<String, List> map = new HashMap<>();
        if (null != list && list.size() > 0) {
            list.forEach(m -> {
                for (Map.Entry<String, Object> entry : m.entrySet()) {
                    String key = entry.getKey();
                    Object value = entry.getValue();
                    if (map.containsKey(key)) {
                        map.get(key).add(value);
                    } else {
                        map.put(key, new ArrayList(Arrays.asList(value)));
                    }
                }
            });
        }
        return map;
    }

    public void export(String id, HttpServletRequest request) {
        ParamsJson paramsJson = getParamsJson(request);
        datasetService.export(id, paramsJson);
    }

    public void exportMore(List<VisualData> datasetList) {
        List<Dataset> datasets = new ArrayList<>();
        for (VisualData visualData : datasetList) {
            Dataset dataset = new Dataset();
            ParamsJson paramsJson = new ParamsJson();
            dataset.setId(visualData.getDatasetId());
            List<ReqParam> reqParams = new ArrayList<>();
            Map<String, String> reqMap = visualData.getReqMap();
            reqMap.forEach((k, v) -> {
                ReqParam reqParam = new ReqParam();
                reqParam.setParamEn(k);
                reqParam.setValue(v);
                reqParams.add(reqParam);
            });
            paramsJson.setReq(reqParams);
            dataset.setParamsJson(JSON.toJSONString(paramsJson));
//            dataset.setParamsJson(paramsJson);
            datasets.add(dataset);
        }
        datasetService.exportMore(datasets);
    }

    @Async
    public void async(int i) {
        for (int j = 1; j < 4; j++) {
            String str = String.format("第%d轮第%d次", i, j);
            System.out.println(str);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
