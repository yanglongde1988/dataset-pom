package com.ngw.dataset.controller;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.ngw.dataset.pojo.*;
import com.ngw.dataset.service.DatasetConfigService;
import com.ngw.fusion.common.dataBus.DataBus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URLDecoder;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/dataset/config")
public class DatasetConfigController {

    @Autowired
    private DatasetConfigService datasetConfigService;

    @RequestMapping("/pageParams")
    public DataBus getPage() {
        log.debug("===========================分页参数列表===========================");
        try {
            List<PageParam> pageParams = datasetConfigService.pageParams();
            log.debug("===========================分页参数列表返回成功===========================");
            return DataBus.success().data( pageParams);
        } catch (Exception e) {
            e.printStackTrace();
            log.debug("===========================分页参数列表返回失败===========================");
            return DataBus.error().message(e.getMessage());
        }
    }

    @RequestMapping("/reqParams")
    public DataBus getReq(@RequestBody Map<String, String> map) {
        log.debug("===========================请求参数列表===========================");
        try {
            String datasetEntity = map.get("dsEntity");
            datasetEntity = URLDecoder.decode(datasetEntity, "UTF-8");
            String datasetType = map.get("dsType");
            List<ReqParam> reqParamList = datasetConfigService.reqParams(datasetType, datasetEntity);
            log.debug("===========================请求参数列表返回成功===========================");
            return DataBus.success().data("data", reqParamList);
        } catch (Exception e) {
            e.printStackTrace();
            log.debug("===========================请求参数列表返回失败===========================");
            return DataBus.error().message(e.getMessage());
        }
    }

    @RequestMapping("/respParams")
    public DataBus getResp(@RequestBody Map<String, Object> map) {
        String dataSourceCode = (String) map.get("dsSourceCode");
        String datasetEntity = (String) map.get("dsEntity");
        String datasetType = (String) map.get("dsType");
        List<ReqParam> params = JSONArray.parseArray(JSON.toJSONString(map.get("params")), ReqParam.class);
        log.debug("===========================响应参数列表===========================");
        try {
            datasetEntity = URLDecoder.decode(datasetEntity, "UTF-8");
            List<RespParam> respParamList = datasetConfigService.respParams(datasetType, dataSourceCode, datasetEntity, params);
            log.debug("===========================响应参数列表返回成功===========================");
            return DataBus.success().data("data", respParamList);
        } catch (Exception e) {
            e.printStackTrace();
            log.debug("===========================响应参数列表返回失败===========================");
            return DataBus.error().message(e.getMessage());
        }
    }

    @RequestMapping("/test")
    public DataBus getTest(@RequestBody Map<String, Object> map) {
        String dataSourceCode = (String) map.get("dsSourceCode");
        String datasetEntity = (String) map.get("dsEntity");
        String datasetType = (String) map.get("dsType");
        ParamsJson paramsJson = JSONObject.parseObject(JSON.toJSONString(map.get("paramsJson")), ParamsJson.class);
        log.debug("===========================数据集测试===========================");
        try {
            datasetEntity = URLDecoder.decode(datasetEntity, "UTF-8");
            DatasetResult test = datasetConfigService.test(datasetType, dataSourceCode, datasetEntity, paramsJson);
            log.debug("===========================数据集测试返回成功===========================");
            return DataBus.success().data("data", test);
        } catch (Exception e) {
            e.printStackTrace();
            log.debug("===========================数据集测试返回失败===========================");
            return DataBus.error().message(e.getMessage());
        }
    }

}
