package com.ngw.dataset.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.ngw.dataset.pojo.*;
import com.ngw.datasource.pojo.Datasource;
import com.ngw.fusion.common.util.SystemParam;
import com.ngw.util.Operator;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Service
public class TableDatasetConfigServiceImpl extends SqlDatasetConfigServiceImpl {

    @Override
    public List<RespParam> respParams(String dataSourceCode, String datasetEntity, List<ReqParam> params) throws SQLException {
        // 只查询响应结果的列
        datasetEntity = "select * from " + datasetEntity + " where 1=2";
        log.debug("获取响应参数执行sql脚本：{}", datasetEntity);
        // 获取响应结果的列
        List<RespParam> respParams = getResultColumns(dataSourceCode, datasetEntity);
        return respParams;
    }

    /**
     * 获取拼接的where条件 属于数据集配置好的动态查询条件
     *
     * @param reqParamList 配置的请求参数
     * @param reqParams    传入的请求参数 只需要英文参数名及参数值
     * @return
     */
    private String getWhereSql(List<ReqParam> reqParamList, List<ReqParam> reqParams) {
        // 需要获取传入参数对应的参数配置信息 记录必传参数防止未传入
        List mustReqParams = new ArrayList();
        Map<String, ReqParam> paramMap = new HashMap<>();
        for (ReqParam reqParam : reqParamList) {
            String bindEn = reqParam.getBindEn();
            String paramEn = reqParam.getParamEn();
            paramEn = StringUtils.isNotBlank(paramEn) ? paramEn : bindEn;
            paramMap.put(paramEn, reqParam);
            String isMust = reqParam.getIsMust();
            if ("true".equalsIgnoreCase(isMust)) mustReqParams.add(paramEn);
        }
        List<String> reqList = new ArrayList<>();
        for (ReqParam reqParam : reqParams) {
            String value = reqParam.getValue();
            String paramEn = reqParam.getParamEn();
            ReqParam param = paramMap.get(paramEn);
            if (null == param) continue;
            String isMust = param.getIsMust();
            String msg = String.format("必填参数【%s】值不能为空！", paramEn);
            // 如果是必填必须要传入参数值
            Assert.isTrue("FALSE".equalsIgnoreCase(isMust) || ("TRUE".equalsIgnoreCase(isMust) && null != value), msg);
            mustReqParams.remove(paramEn);
            // 有传入参数值就拼接上
            if (null != value) {
                String relationType = param.getRelationType();
                String paramType = param.getParamType();
                String bindEn = param.getBindEn();
                bindEn += Operator.getOperatorByKey(relationType).loadParam(value, paramType);
                reqList.add(bindEn);
            }
        }
        if (mustReqParams.size() > 0) {
            throw new IllegalArgumentException("必传参数【" + mustReqParams.get(0) + "】未传入！");
        }
        String whereSql = String.join(" AND ", reqList);
        return whereSql;
    }

    String getWhereSql(FilterParam filterParam) {
        String filterSql = getFilterSql(filterParam);
        if (StringUtils.isNotBlank(filterSql)) filterSql = " WHERE " + filterSql;
        return filterSql;
    }

    /**
     * 获取拼接的where条件 属于数据集配置好的固定查询条件
     * @param filterParam 配置的filter条件
     * @return
     */
    private String getFilterSql(FilterParam filterParam) {
        if (filterParam == null) return "";
        String filterSql = "";
        List<FilterParam> filter = filterParam.getFilter();
        if (null == filter || filter.size() == 0) {
            String bindEn = filterParam.getBindEn();
            String relationType = filterParam.getRelationType();
            String paramType = filterParam.getParamType();
            String value = filterParam.getValue();
            filterSql = bindEn + Operator.getOperatorByKey(relationType).loadParam(value, paramType);
        } else {
            StringBuilder sb = new StringBuilder("(");
            String logicType = filterParam.getLogicType();
            Assert.isTrue("AND".equalsIgnoreCase(logicType) || "OR".equalsIgnoreCase(logicType), "过滤参数【" + logicType + "】异常！");
            List<String> conditionList = new ArrayList<>();
            filter.forEach(f -> conditionList.add(getFilterSql(f)));
            sb.append(String.join(" " + logicType + " ", conditionList));
            sb.append(")");
            filterSql = sb.toString();
        }
        return filterSql;
    }

    @Override
    public String buildResultSql(String datasetEntity, ParamsJson configParamsJson, ParamsJson paramsJson, String dsCode) throws SQLException {
        // 构建filter参数拼接条件
        String whereSql = getWhereSql(configParamsJson.getFilter());
        // 开始构建 resp
        List<RespParam> respParams = configParamsJson.getResp();
        String respSql = getRespSql(respParams);
        StringBuilder sb = new StringBuilder("SELECT ").append(respSql).append(" FROM ").append(datasetEntity).append(whereSql);
        // 开始构建 order 拼配置的排序信息
        List<OrderParam> orderParams = configParamsJson.getOrder();
        String insideOrderSql = getOrderSql(orderParams, false);
        if (StringUtils.isNotBlank(insideOrderSql)) sb.append(insideOrderSql);
        return sb.toString();
    }

    @Override
    public DatasetResult test(String dataSourceCode, String datasetEntity, ParamsJson configParamsJson) throws SQLException {
        // 构建sql
        datasetEntity = buildResultSql(datasetEntity, configParamsJson, configParamsJson, null);
        // 替换系统参数
        datasetEntity = replaceSystemParams(datasetEntity);
        // 获取req条件
        List<ReqParam> reqParams = configParamsJson.getReq();
        String whereSql = getWhereSql(reqParams, reqParams);
        // 当whereSql不为空时才拼 需要判断datasetEntity是否有拼接filter条件
        if (StringUtils.isNotBlank(whereSql)) {
            whereSql = datasetEntity.contains(" WHERE ") ? " AND " + whereSql : " WHERE " + whereSql;
            // 需要拼接在 ORDER BY 语句之前
            int index = datasetEntity.indexOf(" ORDER BY ");
            String prefixStr = index > -1 ? datasetEntity.substring(0, index) : datasetEntity;
            String suffixStr = index > -1 ? datasetEntity.substring(index) : "";
            datasetEntity = prefixStr + whereSql + suffixStr;
        }
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
        // 替换系统参数
        dsSql = replaceSystemParams(dsSql);
        // 获取req条件
        String configParamsJsonText = dataset.getParamsJson();
        ParamsJson configParamsJson = JSONObject.parseObject(configParamsJsonText, ParamsJson.class);
        List<ReqParam> reqParamList = configParamsJson.getReq();
        List<ReqParam> reqParams = paramsJson.getReq();
        String whereSql = getWhereSql(reqParamList, reqParams);
        // 当whereSql不为空时才拼 需要判断datasetEntity是否有拼接filter条件
        if (StringUtils.isNotBlank(whereSql)) {
            whereSql = dsSql.contains(" WHERE ") ? " AND " + whereSql : " WHERE " + whereSql;
            // 需要拼接在 ORDER BY 语句之前
            int index = dsSql.indexOf(" ORDER BY ");
            String prefixStr = index > -1 ? dsSql.substring(0, index) : dsSql;
            String suffixStr = index > -1 ? dsSql.substring(index) : "";
            dsSql = prefixStr + whereSql + suffixStr;
        }
        // 拼传入的排序信息
        List<OrderParam> orders = paramsJson.getOrder();
        if (null != orders && orders.size() > 0) {
            String outOrderSql = getOrderSql(orders, true);
            dsSql = "SELECT * FROM (" + dsSql + ") t" + outOrderSql;
        }
        // 开始构建 page 拼接分页信息
        List<PageParam> pageParams = configParamsJson.getPage();
        List<PageParam> pages = paramsJson.getPage();
        Page page = getPage(pageParams, pages);
        Integer pageNum = page.getCurrent();
        Integer pageSize = page.getSize();
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

    @Override
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
            Object value = systemParam.getValue();
            String paramTemp = "\\@\\{" + param.substring(2, param.length() - 1) + "\\}";
            datasetEntity = datasetEntity.replaceAll(paramTemp, "" + value);
        }
        return datasetEntity;
    }

}
