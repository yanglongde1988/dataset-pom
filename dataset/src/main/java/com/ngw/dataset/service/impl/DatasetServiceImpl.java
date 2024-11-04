package com.ngw.dataset.service.impl;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.alibaba.excel.write.style.column.SimpleColumnWidthStyleStrategy;
import com.alibaba.excel.write.style.row.SimpleRowHeightStyleStrategy;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ngw.dataset.mapper.DatasetMapper;
import com.ngw.dataset.pojo.Dataset;
import com.ngw.dataset.pojo.DatasetResult;
import com.ngw.dataset.pojo.ParamsJson;
import com.ngw.dataset.pojo.RespParam;
import com.ngw.dataset.service.DatasetConfigService;
import com.ngw.dataset.service.DatasetService;
import com.ngw.fusion.common.util.BeanUtils;
import com.ngw.fusion.common.util.Constants;
import com.ngw.fusion.common.util.FileUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Slf4j
@Service
@EnableAsync
public class DatasetServiceImpl extends ServiceImpl<DatasetMapper, Dataset>
        implements DatasetService {
    @Autowired
    private DatasetConfigService dsConfig;

    @Override
    public Object list(Integer pageNum, Integer pageSize, String text, String datasetType) {
        log.debug("===========================数据集列表查询,pageNum:{},pageSize:{},datasetType:{},text:{}===========================", pageNum, pageSize, datasetType, text);
        LambdaQueryWrapper<Dataset> query = Wrappers.lambdaQuery(Dataset.class);
        query.eq(StringUtils.isNotBlank(datasetType), Dataset::getDsType, datasetType);
        query.and(StringUtils.isNotBlank(text),
                i -> i.like(Dataset::getDsCode, text)
                        .or().like(Dataset::getDsName, text)
                        .or().like(Dataset::getDsDesc, text));
        if (Objects.isNull(pageNum) || Objects.isNull(pageSize)) return list(query);
        Page<Dataset> page = new Page<>(pageNum, pageSize);
        return page(page, query);
    }

    @Override
    public void check(Dataset dataset) throws SQLException {
        String id = dataset.getId();
        String dsCode = dataset.getDsCode();
        Assert.isTrue(StringUtils.isNotBlank(dsCode), "数据集编码不能为空！");
        LambdaQueryWrapper<Dataset> query = Wrappers.lambdaQuery(Dataset.class);
        query.eq(Dataset::getDsCode, dsCode).ne(StringUtils.isNotBlank(id), Dataset::getId, id);
        List<Dataset> list = list(query);
        Assert.isTrue(null == list || list.size() == 0, "数据集编码已存在！");
        // 生成待执行sql
        String dsEntity = dataset.getDsEntity();
        String configParamsJsonText = dataset.getParamsJson();
        ParamsJson configParamsJson = JSONObject.parseObject(configParamsJsonText, ParamsJson.class);
        dsConfig.getDatasetService(dataset.getDsType());
        dataset.setDsSql(dsConfig.iDatasetConfigService.buildResultSql(dsEntity, configParamsJson, configParamsJson, dsCode));
    }

    @Override
    public Dataset getByCode(String dsCode) {
        Assert.isTrue(StringUtils.isNotBlank(dsCode), "数据集编码不能为空！");
        LambdaQueryWrapper<Dataset> wrapper = Wrappers.lambdaQuery(Dataset.class);
        wrapper.eq(Dataset::getDsCode, dsCode);
        return getOne(wrapper);
    }

    @Resource
    private HttpServletResponse response;

    @Override
    public DatasetResult result(String dsCode, ParamsJson paramsJson, Map<String, Dataset> datasetMap) throws SQLException {
        Dataset dataset = getByCode(dsCode);
        Assert.isTrue(null != dataset, "数据集不存在！");
        datasetMap.put(dsCode, dataset);
        dsConfig.getDatasetService(dataset.getDsType());
        DatasetResult result = dsConfig.iDatasetConfigService.result(dataset, paramsJson);
        return result;
    }

    @Override
    public void export(String dsCode, ParamsJson paramsJson) {
        try {
            Map<String, Dataset> datasetMap = new HashMap<>();
            DatasetResult result = result(dsCode, paramsJson, datasetMap);
            Dataset dataset = datasetMap.get(dsCode);
            // 查询结果
            List<Map<String, Object>> dataList = result.getResult();
            List<RespParam> respParams = result.getResp();
            // 存储表头
            LinkedHashMap<String, String> headMap = new LinkedHashMap<>();
            getHeadMapAndDataList(respParams, headMap, dataList);
            FileUtils.exportFile(Constants.FILE_TYPE_EXCEL, dataset.getDsName(), headMap, dataList, response);
        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }
    }

    private AtomicInteger zipOutNum;

    public void exportMore(List<Dataset> datasets) {
        try (ZipOutputStream out = new ZipOutputStream(response.getOutputStream())) {
            response.setContentType("application/zip");
            response.setCharacterEncoding("utf-8");
            String fileName = URLEncoder.encode("大屏数据集", "UTF-8").replaceAll("\\+", "%20");
            response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".zip");
            int size = datasets.size();
            zipOutNum = new AtomicInteger(0);
            for (Dataset dataset : datasets) {
                BeanUtils.getBean(this.getClass()).zipOut(dataset, out);
            }
            while (size > zipOutNum.get()) {
                log.debug("已导出数据集个数：{}", zipOutNum.get());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Async
    void zipOut(Dataset dataset, ZipOutputStream out) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        String datasetName = addZipEntry(dataset, baos);
        out.putNextEntry(new ZipEntry(datasetName + ".xlsx"));
        byte[] excelStream = baos.toByteArray();
        baos.close();
        out.write(excelStream);
        out.closeEntry();
        out.flush();
        zipOutNum.incrementAndGet();
    }

    private String addZipEntry(Dataset dataset, ByteArrayOutputStream out1) {
        String datasetCode = dataset.getId();
        String datasetName = datasetCode + "导出异常";
        try {
            ParamsJson paramsJson = JSONObject.parseObject(dataset.getParamsJson(), ParamsJson.class);
            Map<String, Dataset> datasetMap = new HashMap<>();
            DatasetResult result = result(datasetCode, paramsJson, datasetMap);
            dataset = datasetMap.get(datasetCode);
            datasetName = dataset.getDsName();
            // 查询结果
            List<Map<String, Object>> dataList = result.getResult();
            List<RespParam> respParams = result.getResp();
            // 存储表头
            LinkedHashMap<String, String> headMap = new LinkedHashMap<>();
            getHeadMapAndDataList(respParams, headMap, dataList);
            List<List<String>> headList = FileUtils.getHeadList(headMap);
            List<String> enHead = headList.get(0);
            List<String> cnHead = headList.get(1);
            // 输出文件表头
            try (ExcelWriter excelWriter = EasyExcel.write(out1).head(FileUtils.headList(cnHead)).autoCloseStream(Boolean.FALSE).build()) {
                // 输出文件sheet名称
                String sheetName = URLDecoder.decode(datasetName, "UTF-8");
                // 限制单个sheet页行数，拆分数据集
                List<List<Map<String, Object>>> list = FileUtils.subList(dataList, 50000);
                for (int i = 0, size = list.size(); i < size; i++) {
                    List<Map<String, Object>> subList = list.get(i);
                    // 每次都要创建writeSheet 这里注意必须指定sheetNo 而且sheetName必须不一样
                    String suffix = "";
                    if (size > 1) suffix = String.valueOf(i + 1);
                    WriteSheet writeSheet = EasyExcel.writerSheet(i, sheetName + suffix)
                            .registerWriteHandler(new SimpleRowHeightStyleStrategy((short) 25, (short) 20))
                            .registerWriteHandler(new SimpleColumnWidthStyleStrategy(25))
                            .build();
                    excelWriter.write(FileUtils.dataList(enHead, subList), writeSheet);
                }
            }
        } catch (IOException | SQLException e) {
            log.warn("数据集{}查询异常！", datasetCode);
        }
        return datasetName;
    }

    private void getHeadMapAndDataList(List<RespParam> respParams, LinkedHashMap<String, String> headMap, List<Map<String, Object>> dataList) {
        // 存储转码字典项目
        Map<String, Map<String, String>> dictMap = new HashMap<>();
        // 遍历返回参数，识别需要
        respParams.forEach(respParam -> {
            // 转码 需要转码的项
            String dictItem = respParam.getDictItem();
            if (StringUtils.isNotBlank(dictItem)) {
//                Map<String, String> dicItemMap = getDicItemMap(dictItem);
                // todo 本地环境未调用代码表 线上环境需要上面方法打开
                Map<String, String> dicItemMap = new HashMap<>();
                dictMap.put(respParam.getParamEn(), dicItemMap);
            }
            // 生成表头
            headMap.put(respParam.getParamEn(), respParam.getParamCn());
        });
        // 结果集转码
        dataList.forEach(map -> {
            map.forEach((k, v) -> {
                if (dictMap.containsKey(k)) {
                    map.put(k, dictMap.get(k).get(v));
                }
            });
        });
    }

/*    // todo 本地环境未调用代码表 线上环境需要上面方法打开
    @Autowired
    private Auth2Feign auth2Feign;

    private Map<String, String> getDicItemMap(String code) {
        DataBus dataBus = auth2Feign.getByCodeCodeItem(code);
        List<Map<String, String>> codeItems = (List<Map<String, String>>) dataBus.getData().get("data");
        Map<String, String> map = new HashMap<>();
        if (codeItems != null) codeItems.forEach(item -> map.put(item.get("itemCode"), item.get("itemName")));
        return map;
    }*/
}




