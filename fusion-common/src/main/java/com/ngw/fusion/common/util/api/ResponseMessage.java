package com.ngw.fusion.common.util.api;

import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;

public class ResponseMessage<T> implements Serializable {
    private static final long serialVersionUID = 8992436576262574064L;
    private int code;
    protected String message;
    protected T data;
    protected final Long timestamp = Long.valueOf(System.currentTimeMillis());

    public static <T> ResponseMessage<T> ok() {
        return ok(null);
    }

    public static <T> ResponseMessage<T> ok(T result) {
        return builder().data(result)
                .message("请求成功")
                .code(200)
                .build();
    }

    public static <T> ResponseMessage<T> error(IResponseCode code) {
        return error(code, null);
    }

    public static <T> ResponseMessage<T> error(String message) {
        return error(Integer.valueOf(500), message);
    }

    public static <T> ResponseMessage<T> error(IResponseCode code, String message) {
        return error(Integer.valueOf(code.getCode()), StringUtils.isNotBlank(message) ? message : code.getMessage());
    }

    public static <T> ResponseMessage<T> error(Integer code, String message) {
        return builder()
                .message(message)
                .code(code.intValue())
                .build();
    }


    public ResponseMessage<T> data(T result) {
        this.data = result;
        return this;
    }

    public static <T> ResponseMessageBuilder<T> builder()
    {
        return new ResponseMessageBuilder();
    }

    public int getCode()
    {
        return this.code;
    }
    public String getMessage() { return this.message; }
    public T getData() {
        return this.data;
    }
    public Long getTimestamp() { return this.timestamp; }


    public void setCode(int code)
    {
        this.code = code; }
    public void setMessage(String message) { this.message = message; }
    public void setData(T data) { this.data = data; }
    public ResponseMessage(int code, String message, T data) {
        this.code = code; this.message = message; this.data = data;
    }

    public ResponseMessage()
    {
    }

    public static class ResponseMessageBuilder<T>
    {
        private int code;
        private String message;
        private T data;

        public ResponseMessageBuilder<T> code(int code)
        {
            this.code = code; return this;
        }
        public ResponseMessageBuilder<T> message(String message) { this.message = message; return this; }
        public ResponseMessageBuilder<T> data(T data) { this.data = data; return this; }
        public <T> ResponseMessage<T> build() {
            return new ResponseMessage(this.code, this.message, this.data);
        }
        public String toString() { return "ResponseMessage.ResponseMessageBuilder(code=" + this.code + ", message=" + this.message + ", data=" + this.data + ")"; }

    }
}
