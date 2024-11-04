package com.ngw.fusion.common.dataBus;

//import io.swagger.annotations.ApiModelProperty;
//import lombok.Data;

import java.util.HashMap;
import java.util.Map;

//@Data
public class DataBus {
//    @ApiModelProperty(value = "是否成功")


    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public void setData(Map<String, Object> data) {
        this.data = data;
    }

    //    @ApiModelProperty(value = "返回码")
    private String code;

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public Boolean getIsOk() {
        return isOk;
    }

    public void setIsOk(Boolean ok) {
        isOk = ok;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    //    @ApiModelProperty(value = "返回消息")
    private String message;

    private int currentPage;

    private Boolean isOk;

    private int pageSize;



//    @ApiModelProperty(value = "返回数据")
    private Map<String, Object> data = new HashMap<String, Object>();

    private DataBus(){}

    public static DataBus success(){
        DataBus dataBus = new DataBus();
        dataBus.setCode(ResultCode.SUCCESS);
        dataBus.setIsOk(true);
        dataBus.setMessage("成功");
        return dataBus;
    }

    public static DataBus error(){
        DataBus dataBus = new DataBus();
        dataBus.setCode(ResultCode.ERROR);
        dataBus.setIsOk(false);
        dataBus.setMessage("失败");
        return dataBus;
    }

    public DataBus code(String code) {
        this.setCode(code);
        return this;
    }

    public DataBus isOk(Boolean success){
        this.setIsOk(success);
        return this;
    }

    public DataBus message(String message){
        this.setMessage(message);
        return this;
    }

    public DataBus pageSize(int pageSize){
        this.setPageSize(pageSize);
        return this;
    }

    public DataBus currentPage(int currentPage){
        this.setCurrentPage(currentPage);
        return this;
    }

    public DataBus data(String key, Object value){
        this.data.put(key, value);
        return this;
    }

    public DataBus data(Object value){
        this.data.put("data", value);
        return this;
    }

    public DataBus data(Map<String, Object> map){
        this.setData(map);
        return this;
    }
}
