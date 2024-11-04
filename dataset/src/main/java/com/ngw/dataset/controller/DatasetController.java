package com.ngw.dataset.controller;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson2.JSON;
import com.ngw.dataset.pojo.Dataset;
import com.ngw.dataset.pojo.DatasetResult;
import com.ngw.dataset.pojo.ParamsJson;
import com.ngw.dataset.service.DatasetService;
import com.ngw.fusion.common.dataBus.DataBus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URLDecoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/dataset")
public class DatasetController {


    @Autowired
    private DatasetService datasetService;

    @RequestMapping("/save")
    public DataBus save(@RequestBody Dataset dataset){
        log.debug("===========================数据集保存===========================");
        try {
            dataset.setDsEntity(URLDecoder.decode(dataset.getDsEntity(), "UTF-8"));
            datasetService.check(dataset);
            datasetService.saveOrUpdate(dataset);
            log.debug("===========================数据集保存成功===========================");
            return DataBus.success().message("保存成功");
        } catch (Exception e) {
            e.printStackTrace();
            log.debug("===========================数据集保存失败===========================");
            return DataBus.error().message("保存失败：" + e.getMessage());
        }
    }

    @RequestMapping("/del")
    public DataBus del(@RequestBody List<String> ids){
        log.debug("===========================数据集删除===========================");
        try {
            datasetService.removeBatchByIds(ids);
            log.debug("===========================数据集删除成功===========================");
            return DataBus.success().message("删除成功");
        } catch (Exception e) {
            e.printStackTrace();
            log.debug("===========================数据集删除失败===========================");
            return DataBus.error().message("删除失败：" + e.getMessage());
        }
    }

    @RequestMapping("/get")
    public DataBus get(String id){
        log.debug("===========================数据集查看===========================");
        try {
            Dataset dataset = datasetService.getById(id);
            log.debug("===========================数据集查看成功===========================");
            return DataBus.success().data(dataset).message("获取成功");
        } catch (Exception e) {
            e.printStackTrace();
            log.debug("===========================数据集查看失败===========================");
            return DataBus.error().message("获取失败：" + e.getMessage());
        }
    }

    @RequestMapping("/list")
    public DataBus list(Integer pageNum, Integer pageSize, String text, String datasetType){
        log.debug("===========================数据集列表查询===========================");
        try {
            Object list = datasetService.list(pageNum, pageSize, text, datasetType);
            log.debug("===========================数据集列表查询成功===========================");
            return DataBus.success().data(list).message("列表查询成功");
        } catch (Exception e) {
            e.printStackTrace();
            log.debug("===========================数据集列表查询失败===========================");
            return DataBus.error().message("列表查询失败：" + e.getMessage());
        }
    }

    @RequestMapping("/result")
    public DataBus getResult(@RequestBody Map<String, Object> map) {
        String dsCode = (String) map.get("dsCode");
        ParamsJson paramsJson = JSONObject.parseObject(JSON.toJSONString(map.get("paramsJson")), ParamsJson.class);
        log.debug("===========================数据集查询===========================");
        try {
            DatasetResult result = datasetService.result(dsCode, paramsJson, new HashMap<>());
            log.debug("===========================数据集查询返回成功===========================");
            return DataBus.success().data("data", result);
        } catch (Exception e) {
            e.printStackTrace();
            log.debug("===========================数据集查询返回失败===========================");
            return DataBus.error().message(e.getMessage());
        }
    }

    @RequestMapping("/export")
    public void export(@RequestBody Map<String, Object> map) {
        String dsCode = (String) map.get("dsCode");
        ParamsJson paramsJson = JSONObject.parseObject(JSON.toJSONString(map.get("paramsJson")), ParamsJson.class);
        datasetService.export(dsCode, paramsJson);
    }
}
