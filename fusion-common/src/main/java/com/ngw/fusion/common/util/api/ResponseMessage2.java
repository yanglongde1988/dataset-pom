package com.ngw.fusion.common.util.api;

import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;

public class ResponseMessage2<T> implements Serializable {
    private static final long serialVersionUID = 8992436576262574064L;
    private int status;
    protected String message;
    protected T data;
    protected final Long timestamp = Long.valueOf(System.currentTimeMillis());

    public static <T> ResponseMessage2<T> ok() {
        return ok(null);
    }

    public static <T> ResponseMessage2<T> ok(T result) {
        return builder().data(result)
                .message("请求成功")
                .status(0)
                .build();
    }

    public static <T> ResponseMessage2<T> error(IResponseCode code) {
        return error(code, null);
    }

    public static <T> ResponseMessage2<T> error(String message) {
        return error(Integer.valueOf(1), message);
    }

    public static <T> ResponseMessage2<T> error(IResponseCode code, String message) {
        return error(Integer.valueOf(code.getCode()), StringUtils.isNotBlank(message) ? message : code.getMessage());
    }

    public static <T> ResponseMessage2<T> error(Integer code, String message) {
        return builder()
                .message(message)
                .status(code.intValue())
                .build();
    }


    public ResponseMessage2<T> data(T result) {
        this.data = result;
        return this;
    }

    public static <T> ResponseMessageBuilder<T> builder()
    {
        return new ResponseMessageBuilder();
    }

    public int getStatus()
    {
        return this.status;
    }
    public String getMessage() {
        return this.message;
    }
    public T getData() {
        return this.data;
    }
    public Long getTimestamp() {
        return this.timestamp;
    }


    public void setStatus(int status)
    {
        this.status = status;
    }
    public void setMessage(String message) { this.message = message; }
    public void setData(T data) { this.data = data; }
    public ResponseMessage2(int status, String message, T data) {
        this.status = status; this.message = message; this.data = data;
    }

    public ResponseMessage2()
    {
    }

    public static class ResponseMessageBuilder<T>
    {
        private int status;
        private String message;
        private T data;

        public ResponseMessageBuilder<T> status(int status)
        {
            this.status = status; return this;
        }
        public ResponseMessageBuilder<T> message(String message) { this.message = message; return this; }
        public ResponseMessageBuilder<T> data(T data) { this.data = data; return this; }
        public <T> ResponseMessage2<T> build() {
            return new ResponseMessage2(this.status, this.message, this.data);
        }
        public String toString() { return "ResponseMessage.ResponseMessageBuilder(status=" + this.status + ", message=" + this.message + ", data=" + this.data + ")"; }

    }
}
