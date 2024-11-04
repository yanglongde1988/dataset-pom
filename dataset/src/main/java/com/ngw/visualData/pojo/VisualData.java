package com.ngw.visualData.pojo;

import lombok.Data;

import java.util.Map;

@Data
public class VisualData {
    private String datasetId;
    private Map<String, String> reqMap;
}
