package com.ngw.visualData.controller;

import com.alibaba.fastjson.JSONObject;
import com.ngw.visualData.pojo.VisualData;
import com.ngw.visualData.service.VisualDataService;
import com.ngw.fusion.common.dataBus.DataBus;
import com.ngw.fusion.common.util.IPUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/visual")
public class VisualDataController {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private String hostAddress;

    @Value("${testAddress:192.168.1.}")
    private String testAddress;

    @PostConstruct
    private void init() {
        hostAddress = IPUtils.getLocalIp();
    }

    @Autowired
    private VisualDataService visualDataService;

    @RequestMapping("/async")
    public void async() {
        for (int i = 1; i < 4; i++) {
            visualDataService.async(i);
        }
    }

    @RequestMapping("/export/{id}")
    public void export(@PathVariable String id, HttpServletRequest request) {
        visualDataService.export(id, request);
    }

    @RequestMapping("/exportMore")
    public void exportMore(@RequestBody List<VisualData> visualDataList) {
        visualDataService.exportMore(visualDataList);
    }

    @RequestMapping("list/{id}")
    private DataBus list(@PathVariable String id, HttpServletRequest request) {
        try {
            logger.info("本机IP：{}", hostAddress);
//            if (hostAddress.startsWith(testAddress)) return listTest(id, request);
            Object list = visualDataService.list(id, request);
            return DataBus.success().data("data", list);
        } catch (Exception e) {
            e.printStackTrace();
            return DataBus.error().message(e.getMessage());
        }
    }

    @RequestMapping("map/{id}")
    private DataBus map(@PathVariable String id, HttpServletRequest request) {
        try {
            logger.info("本机IP：{}", hostAddress);
            if (hostAddress.startsWith(testAddress)) return mapTest(id, request);
            Map<String, Object> list = visualDataService.map(id, request);
            return DataBus.success().data("data", list);
        } catch (Exception e) {
            e.printStackTrace();
            return DataBus.error().message(e.getMessage());
        }
    }

    @RequestMapping("data/{id}")
    private DataBus data(@PathVariable String id, HttpServletRequest request) {
        try {
            logger.info("本机IP：{}", hostAddress);
            if (hostAddress.startsWith(testAddress)) return dataTest(id, request);
            Map<String, List> list = visualDataService.data(id, request);
            return DataBus.success().data("data", list);
        } catch (Exception e) {
            e.printStackTrace();
            return DataBus.error().message(e.getMessage());
        }
    }

    private DataBus listTest(@PathVariable String id, HttpServletRequest request) {
        try {
            JSONObject jsonObject = JSONObject.parseObject(json);
            Object list = jsonObject.getJSONArray(id);
            return DataBus.success().data("data", list);
        } catch (Exception e) {
            e.printStackTrace();
            return DataBus.error().message(e.getMessage());
        }
    }

    private DataBus mapTest(@PathVariable String id, HttpServletRequest request) {
        try {
            JSONObject jsonObject = JSONObject.parseObject(json);
            Object list = jsonObject.getJSONObject(id);
            return DataBus.success().data("data", list);
        } catch (Exception e) {
            e.printStackTrace();
            return DataBus.error().message(e.getMessage());
        }
    }

    private DataBus dataTest(@PathVariable String id, HttpServletRequest request) {
        try {
            JSONObject jsonObject = JSONObject.parseObject(json);
            Object list = jsonObject.getJSONObject(id);
            return DataBus.success().data("data", list);
        } catch (Exception e) {
            e.printStackTrace();
            return DataBus.error().message(e.getMessage());
        }
    }

    private String json = "{}";

}
