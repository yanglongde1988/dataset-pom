package com.ngw.dataset.service.impl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.ngw.dataset.pojo.OrderParam;
import com.ngw.dataset.pojo.ParamsJson;
import com.ngw.dataset.pojo.ReqParam;
import com.ngw.dataset.pojo.RespParam;
import com.ngw.datasource.pojo.Datasource;
import com.ngw.util.field.Field;
import com.ngw.util.field.FieldType;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

@Slf4j
@Service
public class JsonDatasetConfigServiceImpl extends TableDatasetConfigServiceImpl {

    @Override
    public List<RespParam> respParams(String dataSourceCode, String datasetEntity, List<ReqParam> params) throws SQLException {
        // 校验json格式
        LinkedHashMap<String, Field> fieldMap = new LinkedHashMap<>();
        check(datasetEntity, fieldMap);
        List<RespParam> respParams = new ArrayList<>();
        fieldMap.values().forEach(field -> {
            RespParam respParam = new RespParam();
            respParam.setParamEn(field.getFieldName());
            respParam.setBindEn(field.getFieldName());
            respParam.setParamType(field.getFieldType());
            respParams.add(respParam);
        });
        return respParams;
    }

    @Override
    public String buildResultSql(String datasetEntity, ParamsJson configParamsJson, ParamsJson paramsJson, String dsCode) throws SQLException {
        // 数据入库的逻辑 删除表 创建表 写入数据
        LinkedHashMap<String, Field> fieldMap = new LinkedHashMap<>();
        JSONArray jsonArray = check(datasetEntity, fieldMap);
        String tableName = createTable(dsCode, fieldMap);
        dataInStorage(tableName, jsonArray, fieldMap);
        // 构建filter参数拼接条件
        String whereSql = getWhereSql(configParamsJson.getFilter());
        // 开始构建 resp
        List<RespParam> respParams = configParamsJson.getResp();
        String respSql = getRespSql(respParams);
        StringBuilder sb = new StringBuilder("SELECT ").append(respSql).append(" FROM ").append(tableName).append(whereSql);
        // 开始构建 order 拼配置的排序信息
        List<OrderParam> orderParams = configParamsJson.getOrder();
        String insideOrderSql = getOrderSql(orderParams, false);
        if (StringUtils.isNotBlank(insideOrderSql)) sb.append(insideOrderSql);
        return sb.toString();
    }

    // json格式要求[{"a":"A","b":1,……},{"a":"A","b":1,……}……]
    private JSONArray check(String json, LinkedHashMap<String, Field> fieldMap) {
        JSONArray jsonArray;
        try {
            jsonArray = JSONArray.parseArray(json);
            Assert.isTrue(null != jsonArray && jsonArray.size() > 0, "json数据结构不能为空！");
            JSONObject jsonObject = jsonArray.getJSONObject(0);
            Assert.isTrue(null != jsonObject && jsonObject.size() > 0, "json数据结构不能为空！");
            jsonObject.keySet().forEach(key -> {
                Object o = jsonObject.get(key);
                String fieldType;
                Integer fieldLength = 0, fieldPrecision = 0;
                if (o instanceof Integer) {
                    fieldType = FieldType.TYPE_NUMBER;
                    fieldLength = 32;
                    fieldPrecision = 0;
                } else if (o instanceof BigDecimal) {
                    fieldType = FieldType.TYPE_NUMBER;
                    fieldLength = 40;
                    fieldPrecision = 8;
                } else {
                    fieldType = FieldType.TYPE_TEXT;
                }
                Field field = new Field(key, fieldType, fieldLength, fieldPrecision);
                fieldMap.put(key, field);
            });
        } catch (Exception e) {
            throw new IllegalArgumentException("json格式异常：" + e.getMessage());
        }
        return jsonArray;
    }

    private String createTable(String dsCode, LinkedHashMap<String, Field> fieldMap) throws SQLException {
        String tableName = "dataset_json_" + dsCode;
        // 先删除表
        dropTable(tableName);
        // 创建表
        StringBuilder sb = new StringBuilder();
        sb.append("CREATE TABLE ").append(tableName).append(" (");
        fieldMap.values().forEach(field -> {
            sb.append(field.getFieldName());
            String fieldType = field.getFieldType();
            if (FieldType.TYPE_NUMBER.equals(fieldType)) {
                sb.append(" numeric(").append(field.getFieldLength()).append(",").append(field.getFieldPrecision()).append("),");
            } else {
                sb.append(" text,");
            }
        });
        String createSql = sb.substring(0, sb.length() - 1) + ")";
        log.debug("json数据入库，创建表：{}", createSql);
        executeSql(createSql);
        return tableName;
    }

    private boolean dropTable(String tableName) throws SQLException {
        String dropSql = "DROP TABLE IF EXISTS " + tableName;
        log.debug("json数据入库，删除表：{}", dropSql);
        return executeSql(dropSql);
    }

    private boolean executeSql(String sqlText) throws SQLException {
        Datasource dm = datasourceManageService.getByCode(null);
        Connection connection = DriverManager.getConnection(dm.getJdbcUrl(), dm.getJdbcName(), dm.getJdbcPwd());
        Statement stmt = connection.createStatement();
        boolean execute = stmt.execute(sqlText);
        return execute;
    }

    private void dataInStorage(String tableName, JSONArray jsonArray, LinkedHashMap<String, Field> fieldMap) throws SQLException {
        // INSERT INTO dataset_json_ds3 VALUES ('name1', 18, '男', '12345678', '北京', 100.00),('name2', 19, '男', '12345678', '北京', 100.00);
        StringBuilder insert = new StringBuilder("INSERT INTO ").append(tableName).append(" VALUES ");
        StringBuilder values = new StringBuilder();
        for (int i = 0, size = jsonArray.size(); i < size; i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            values.append("(");
            String fieldValues = "";
            for (String key : fieldMap.keySet()) {
                Object o = jsonObject.get(key);
                o = o instanceof Integer || o instanceof BigDecimal ? o : "'" + o + "'";
                fieldValues += o + ",";
            }
            fieldValues = fieldValues.substring(0, fieldValues.length() - 1);
            values.append(fieldValues).append(")");
            // 每500或循环结束时执行一次写入
            if (i % 2 < 1 && i != size - 1) {
                values.append(",");
            } else {
                log.debug("json数据入库，累计写入表数据记录数：{}", i + 1);
                executeSql(insert.toString() + values.toString());
                values.delete(0, values.length());
            }
        }
    }

}
