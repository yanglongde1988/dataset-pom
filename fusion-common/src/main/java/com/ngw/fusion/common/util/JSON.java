package com.ngw.fusion.common.util;

import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.serializer.ValueFilter;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JSON {

    public static Map parse(String json) {
        return com.alibaba.fastjson.JSON.parseObject(json);
    }
    
    public static <T> T parseObject(String json, Class<T> type) {
        T obj = com.alibaba.fastjson.JSON.parseObject(json, type);
        return obj;
    }
    
    public static Object parseObject(byte[] data, Class<?> type) {
        return com.alibaba.fastjson.JSON.parseObject(data, type);
    }

    public static List parseArray(String json) {
        return com.alibaba.fastjson.JSON.parseArray(json);
    }

    public static String toJSON(Object obj) {
    	
    	ValueFilter filter = new ValueFilter() {

            public Object process(Object obj, String s, Object v) {
                // 将null转化为空字符串
                if(v==null)
                    return "";
                return v;
            }
    	    
    	};
    	//DisableCircularReferenceDetect 禁用了循环引用。否则前台不能正确解析引用信息。
        return com.alibaba.fastjson.JSON.toJSONString(obj, filter,
                SerializerFeature.WriteDateUseDateFormat,
                SerializerFeature.WriteMapNullValue,
                SerializerFeature.DisableCircularReferenceDetect
//                SerializerFeature.WriteNullStringAsEmpty,
//                SerializerFeature.WriteNullBooleanAsFalse,
//                SerializerFeature.WriteNullNumberAsZero,
//                SerializerFeature.WriteNullListAsEmpty
                );
    }
    
    public static byte[] toJSONBytes(Object obj) {
        return com.alibaba.fastjson.JSON.toJSONBytes(obj);
    }
   
    public static void main(String[] args) {
        Map map = new HashMap();
        map.put("aa", "bb");
        map.put("dd", "bb");
        map.put("ee", "bb");
        map.put("bb", null);
        map.put("cc", new Date());
        String xxx = toJSON(map);
        System.out.println(xxx);
    }
    
}
