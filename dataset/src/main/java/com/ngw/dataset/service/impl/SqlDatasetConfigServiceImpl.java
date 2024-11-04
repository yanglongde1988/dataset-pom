package com.ngw.dataset.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.ngw.dataset.service.IDatasetConfigService;
import com.ngw.fusion.common.util.CamelStringUtils;
import com.ngw.datasource.pojo.Datasource;
import com.ngw.datasource.service.IDatasourceManageService;
import com.ngw.fusion.common.util.SystemParam;
import com.ngw.dataset.pojo.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.sql.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Service
public class SqlDatasetConfigServiceImpl implements IDatasetConfigService {

    private static String regex = "(\\$|\\#)\\{\\w+\\}";

    @Override
    public List<ReqParam> reqParams(String sqlText) {
        Assert.isTrue(StringUtils.isNotBlank(sqlText), "sql脚本无效！");
        List<ReqParam> reqParams = new ArrayList<>();
        List<String> list = new ArrayList<>();
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(sqlText);
        while (matcher.find()) {
            String group = matcher.group();
            if (!list.contains(group)) {
                list.add(group);
                ReqParam reqParam = new ReqParam();
                if (group.startsWith("#")) {
                    reqParam.setParamType("TEXT");
                } else {
                    reqParam.setParamType("NUMBER");
                }
                String bindEn = group.substring(2, group.length() - 1);
                reqParam.setBindEn(bindEn);
                reqParam.setParamEn(bindEn);
                reqParam.setParamCn(bindEn);
                reqParam.setIsMust("true");
                reqParams.add(reqParam);
            }
        }
        return reqParams;
    }

    @Override
    public List<RespParam> respParams(String dataSourceCode, String datasetEntity, List<ReqParam> params) throws SQLException {
        // 替换系统参数
        datasetEntity = replaceSystemParams(datasetEntity);
        // 替换查询参数
        datasetEntity = replaceReqParams(datasetEntity, params, params);
        // 只查询响应结果的列
        datasetEntity = "select * from (" + datasetEntity + ") t where 1=2";
        log.debug("获取响应参数执行sql脚本：{}", datasetEntity);
        // 获取响应结果的列
        List<RespParam> respParams = getResultColumns(dataSourceCode, datasetEntity);
        return respParams;
    }

    static String sysRegex = "\\@\\{\\w+\\}";

    String replaceSystemParams(String datasetEntity) {
        List<String> list = new ArrayList<>();
        Pattern pattern = Pattern.compile(sysRegex);
        Matcher matcher = pattern.matcher(datasetEntity);
        while (matcher.find()) {
            String group = matcher.group();
            if (!list.contains(group)) {
                list.add(group);
            }
        }
        for (String param : list) {
            String key = param.substring(2, param.length() - 1);
            SystemParam systemParam = SystemParam.getParamByKey(key);
            Assert.isTrue(systemParam != null, "请检查输入的系统参数" + param);
//            Object value = SystemParam.getValueByKey(key);
            Object value = systemParam.getValue();
            String type = systemParam.getType();
            String paramTemp = "\\@\\{" + param.substring(2, param.length() - 1) + "\\}";
            if ("TEXT".equals(type)) {
                datasetEntity = datasetEntity.replaceAll(paramTemp, "'" + value + "'");
            } else {
                datasetEntity = datasetEntity.replaceAll(paramTemp, "" + value);
            }
        }
        return datasetEntity;
    }

    /**
     * sql类型的req参数都是必填属性
     * @param datasetEntity
     * @param reqParamList 配置的请求参数
     * @param reqParams 传入的请求参数
     * @return
     * @throws SQLException
     */
    String replaceReqParams(String datasetEntity, List<ReqParam> reqParamList, List<ReqParam> reqParams) throws SQLException {
        // 替换值的逻辑是根据写的sql参数类型来判断，而传递参数的显示类型是可以更改的
        List<ReqParam> params = reqParams(datasetEntity);
        Map<String, String> reqMap = new HashMap<>();
        params.forEach(reqParam -> reqMap.put(reqParam.getBindEn(), reqParam.getParamType()));
        // 配置时若没有配置映射的英文名取绑定的英文名  记录绑定英文名与配置英文名的映射关系
        Map<String, String> bindParamMap = new HashMap<>();
        Map<String, String> paramBindMap = new HashMap<>();
        for (ReqParam reqParam : reqParamList) {
            String bindEn = reqParam.getBindEn();
            String paramEn = reqParam.getParamEn();
            paramEn = StringUtils.isNotBlank(paramEn) ? paramEn : bindEn;
            bindParamMap.put(bindEn, paramEn);
            paramBindMap.put(paramEn, bindEn);
        }
        // 请求传入只需要配置的英文参数及值，需要查找对应配置信息的绑定参数及类型
        for (ReqParam param : reqParams) {
            String paramEn = param.getParamEn();
            String bindEn = paramBindMap.get(paramEn);
            String paramType = reqMap.get(bindEn);
            String value = param.getValue();
            Assert.isTrue(value != null, "参数【" + bindEn + "】值为NULL");
            if ("TEXT".equals(paramType)) {
                String key = "\\#\\{" + bindEn + "\\}";
                datasetEntity = datasetEntity.replaceAll(key, "'" + value + "'");
            } else {
                String key = "\\$\\{" + bindEn + "\\}";
                datasetEntity = datasetEntity.replaceAll(key, value);
            }
        }

        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(datasetEntity);
        if (matcher.find()) {
            String group = matcher.group();
            log.debug("待执行sql脚本：{}", datasetEntity);
            String key = group.substring(2, group.length() - 1);
            throw new SQLException("参数【" + bindParamMap.get(key) + "】值为NULL");
        }
//        Assert.isTrue(Pattern.matches(regex, sqlText), "sqlText【" + sqlText + "】存在未传值参数！");
        return datasetEntity;
    }

    @Autowired
    IDatasourceManageService datasourceManageService;

    public List<RespParam> getResultColumns(String dataSourceCode, String sqlText) {
        List<RespParam> resultColumns = new ArrayList<>();
        Datasource dm = datasourceManageService.getByCode(dataSourceCode);
        try (Connection connection = DriverManager.getConnection(dm.getJdbcUrl(), dm.getJdbcName(), dm.getJdbcPwd());
             Statement stmt = connection.createStatement();
             ResultSet resultSet = stmt.executeQuery(sqlText)
        ) {
            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();
            for (int i = 1; i < columnCount + 1; i++) {
                String columnClassName = metaData.getColumnClassName(i);
//                System.out.println(columnClassName);
                RespParam respParam = new RespParam();
                if ("java.lang.String".equals(columnClassName) || "java.sql.Timestamp".equals(columnClassName)) {
                    respParam.setParamType("TEXT");
                } else {
                    respParam.setParamType("NUMBER");
                }
                String bindEn = metaData.getColumnLabel(i);
                respParam.setBindEn(bindEn);
                respParam.setParamEn(bindEn);
                resultColumns.add(respParam);
            }
            return resultColumns;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return resultColumns;
    }

    @Override
    public List<PageParam> pageParams() {
        PageParam pageNum = new PageParam();
        pageNum.setParamCn("当前查询页");
        pageNum.setParamEn("pageNum");
        pageNum.setParamType("NUMBER");
        pageNum.setIsMust("TRUE");
        pageNum.setValue("1");
        PageParam pageSize = new PageParam();
        pageSize.setParamCn("每页显示数");
        pageSize.setParamEn("pageSize");
        pageSize.setParamType("NUMBER");
        pageSize.setIsMust("TRUE");
        pageSize.setValue("1");
        return new ArrayList<>(Arrays.asList(pageNum, pageSize));
    }

    @Override
    public DatasetResult test(String dataSourceCode, String datasetEntity, ParamsJson configParamsJson) throws SQLException {
        // 构建sql
        datasetEntity = buildResultSql(datasetEntity, configParamsJson, configParamsJson, null);
        // 替换系统参数
        datasetEntity = replaceSystemParams(datasetEntity);
        // 替换查询参数
        List<ReqParam> reqParams = configParamsJson.getReq();
        datasetEntity = replaceReqParams(datasetEntity, reqParams, reqParams);
        // 只返回10条示例数据
        datasetEntity = "select * from (" + datasetEntity + ") t limit 10";
        log.debug("待执行sql脚本：{}", datasetEntity);
        // 查询数据
        List<RespParam> respParams = configParamsJson.getResp();
        Datasource datasource = datasourceManageService.getByCode(dataSourceCode);
        List<Map<String, Object>> data = getData(datasource, datasetEntity, respParams);
        Page page = new Page(1, 10, 1, data.size());
        // 返回数据表头驼峰命名等处理
        getResultRespParams(respParams);
        DatasetResult datasetResult = new DatasetResult(page, respParams, data);
        return datasetResult;
    }

    @Override
    public DatasetResult result(Dataset dataset, ParamsJson paramsJson) throws SQLException {
        Assert.isTrue(dataset != null, "数据集不存在！");
        String dataSourceCode = dataset.getDsSourceCode();
        Datasource datasource = datasourceManageService.getByCode(dataSourceCode);
        // 获取待执行sql
        String dsSql = dataset.getDsSql();
        // 拼传入的排序信息
        List<OrderParam> orders = paramsJson.getOrder();
        if (null != orders && orders.size() > 0) {
            String outOrderSql = getOrderSql(orders, true);
            dsSql = "SELECT * FROM (" + dsSql + ") t" + outOrderSql;
        }
        // 开始构建 page 拼接分页信息
        String configParamsJsonText = dataset.getParamsJson();
        ParamsJson configParamsJson = JSONObject.parseObject(configParamsJsonText, ParamsJson.class);
        List<PageParam> pageParams = configParamsJson.getPage();
        List<PageParam> pages = paramsJson.getPage();
        Page page = getPage(pageParams, pages);
        Integer pageNum = page.getCurrent();
        Integer pageSize = page.getSize();
        // 替换系统参数
        dsSql = replaceSystemParams(dsSql);
        // 替换查询参数
        List<ReqParam> reqParamList = configParamsJson.getReq();
        List<ReqParam> reqParams = paramsJson.getReq();
        dsSql = replaceReqParams(dsSql, reqParamList, reqParams);
        // 获取总记录数
        String tempDatasetEntity = "SELECT COUNT(1) AS total FROM (" + dsSql + ") t";
        // 拼分页条件
        if (null != pageNum && null != pageSize)
            dsSql = dsSql + " LIMIT " + pageSize + " OFFSET " + ((pageNum - 1) * pageSize);

        log.debug("待执行sql脚本：{}", dsSql);
        // 查询数据
        List<RespParam> respParams = configParamsJson.getResp();
        List<Map<String, Object>> data = getData(datasource, dsSql, respParams);
        // 查询总记录数
        Integer total = getTotal(datasource, tempDatasetEntity);
        page.setTotal(total);
        if (null != pageNum && null != pageSize) {
            page.setPages(1 + total / pageSize);
        } else {
            page.setCurrent(1);
            page.setSize(total);
            page.setPages(1);
        }
        // 返回数据表头驼峰命名等处理
        getResultRespParams(respParams);
        DatasetResult datasetResult = new DatasetResult(page, respParams, data);
        return datasetResult;
    }

    void getResultRespParams(List<RespParam> respParams) {
        respParams.forEach(respParam -> {
            respParam.setBindEn(null);
            String paramEn = respParam.getParamEn();
            paramEn = CamelStringUtils.underlineToCamel(paramEn);
            respParam.setParamEn(paramEn);
            String paramCn = respParam.getParamCn();
            if (StringUtils.isBlank(paramCn)) respParam.setParamCn(paramEn);
        });
    }

    Integer getTotal(Datasource datasource, String datasetEntity) {
        try (Connection connection = DriverManager.getConnection(datasource.getJdbcUrl(), datasource.getJdbcName(), datasource.getJdbcPwd());
             Statement stmt = connection.createStatement();
             ResultSet resultSet = stmt.executeQuery(datasetEntity)
        ) {
            resultSet.next();
            int count = resultSet.getInt("total");
            return count;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return 0;
    }

    List<Map<String, Object>> getData(Datasource datasource, String datasetEntity, List<RespParam> respParams) throws SQLException {
        List<Map<String, Object>> list = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(datasource.getJdbcUrl(), datasource.getJdbcName(), datasource.getJdbcPwd());
             Statement stmt = connection.createStatement();
             ResultSet resultSet = stmt.executeQuery(datasetEntity)
        ) {
            while (resultSet.next()) {
                Map<String, Object> map = new LinkedHashMap<>();
                for (RespParam respParam : respParams) {
                    String paramEn = respParam.getParamEn();
                    paramEn = StringUtils.isBlank(paramEn) ? respParam.getBindEn() : paramEn;
                    Object object = resultSet.getObject(paramEn);
                    map.put(CamelStringUtils.underlineToCamel(paramEn), object);
                }
                list.add(map);
            }
            return list;
        }
    }

    /**
     *
     * @param datasetEntity
     * @param configParamsJson 配置的参数信息
     * @param paramsJson 传入的参数信息
     * @return
     * @throws SQLException
     */
    @Override
    public String buildResultSql(String datasetEntity, ParamsJson configParamsJson, ParamsJson paramsJson, String dsCode) throws SQLException {
        // 开始构建 resp
        List<RespParam> respParams = configParamsJson.getResp();
        String respSql = getRespSql(respParams);
        StringBuilder sb = new StringBuilder("SELECT ").append(respSql).append(" FROM (").append(datasetEntity).append(") t");
        // 开始构建 order 拼配置的排序信息
        List<OrderParam> orderParams = configParamsJson.getOrder();
        String insideOrderSql = getOrderSql(orderParams, false);
        if (StringUtils.isNotBlank(insideOrderSql)) sb.append(insideOrderSql);
        return sb.toString();
    }

    /**
     * 拼接分页信息
     *
     * @param pageParams 配置项分页信息
     * @param pages      传入分页信息
     */
    Page getPage(List<PageParam> pageParams, List<PageParam> pages) {
        Page page = new Page();
        Integer pageNum = null;
        Integer pageSize = null;
        // 获取传入的分页信息 可能未传 但是数据集有分页限制
        if (pages == null) pages = new ArrayList<>();
        for (PageParam pageParam : pages) {
            String paramEn = pageParam.getParamEn();
            if ("pageNum".equalsIgnoreCase(paramEn)) {
                pageNum = Integer.valueOf(pageParam.getValue());
            }
            if ("pageSize".equalsIgnoreCase(paramEn)) {
                pageSize = Integer.valueOf(pageParam.getValue());
            }
        }
        // 数据集有分页限制
        if (null != pageParams && pageParams.size() == 2) {
            // 判断是否必传 否 则只判断传入信息 是 则需要结合配置信息
            String isMust = pageParams.get(0).getIsMust();
            if ("true".equalsIgnoreCase(isMust)) {
                for (PageParam pageParam : pageParams) {
                    String paramEn = pageParam.getParamEn();
                    if ("pageNum".equalsIgnoreCase(paramEn)) {
                        pageNum = null == pageNum ? Integer.valueOf(pageParam.getValue()) : pageNum;
                    }
                    if ("pageSize".equalsIgnoreCase(paramEn)) {
                        pageSize = null == pageSize ? Integer.valueOf(pageParam.getValue()) : pageSize;
                    }
                }
                Assert.isTrue(null != pageNum, "分页参数信息【pageNum】异常！");
                Assert.isTrue(null != pageSize, "分页参数信息【pageSize】异常！");
            }
        }
        page.setCurrent(pageNum);
        page.setSize(pageSize);
        return page;
    }

    String getOrderSql(List<OrderParam> orders, boolean isOut) {
        if (null == orders || orders.size() == 0) return "";
        List<String> list = new ArrayList<>();
        for (OrderParam orderParam : orders) {
            String param = orderParam.getBindEn();
            if (isOut) param = CamelStringUtils.camelToUnderline(orderParam.getParamEn());
            String orderType = orderParam.getOrderType();
            if (StringUtils.isNotBlank(orderType)) {
                param = param + " " + orderType;
            }
            list.add(param);
        }
        return " ORDER BY " + String.join(", ", list);
    }

    String getRespSql(List<RespParam> respParams) {
        List<String> respList = new ArrayList<>();
        Assert.isTrue(null != respParams && respParams.size() > 0, "未配置返回参数信息！");
        for (RespParam respParam : respParams) {
            String bindEn = respParam.getBindEn();
            String paramEn = respParam.getParamEn();
            if (StringUtils.isNotBlank(paramEn)) {
                bindEn = bindEn + " AS " + paramEn;
            }
            respList.add(bindEn);
        }
        return String.join(", ", respList);
    }

}
