package com.ngw.dataset.pojo;

import com.ngw.fusion.common.util.CamelStringUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
public class DatasetResult {

    private Page page;
    private List<RespParam> resp;
    private List<Map<String, Object>> result;

    public void setResp(List<RespParam> respParams) {
        respParams.forEach(respParam -> {
            String paramEn = respParam.getParamEn();
            String paramCn = respParam.getParamCn();
            paramEn = StringUtils.isBlank(paramEn) ? respParam.getBindEn() : paramEn;
            paramEn = CamelStringUtils.underlineToCamel(paramEn);
            respParam.setParamEn(paramEn);
            if (StringUtils.isBlank(paramCn)) respParam.setParamCn(paramEn);
            respParam.setBindEn(null);
        });
        this.resp = respParams;
    }
}
