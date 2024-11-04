package com.ngw.fusion.common.util;

import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EntityToMapConverter {
    public static Map<String, Object> toMap(Object entity) {
        Map<String, Object> map = new HashMap<>();
        try {
            for (PropertyDescriptor propertyDescriptor : Introspector.getBeanInfo(entity.getClass()).getPropertyDescriptors()) {
                if (propertyDescriptor.getReadMethod() != null && !"class".equals(propertyDescriptor.getName())) {
                    Method readMethod = propertyDescriptor.getReadMethod();
                    if (!readMethod.isAccessible()) {
                        readMethod.setAccessible(true);
                    }
                    map.put(propertyDescriptor.getName(), readMethod.invoke(entity));
                }
            }
        } catch (Exception e) {
            // Handle exception
        }
        return map;
    }
    public static<T> List<Map<String, Object>> toMap(List<T> entityList) {
        List<Map<String, Object>> resultList = new ArrayList<>();
        for (Object entity : entityList){
            Map<String, Object> map = new HashMap<>();
            try {
                for (PropertyDescriptor propertyDescriptor : Introspector.getBeanInfo(entity.getClass()).getPropertyDescriptors()) {
                    if (propertyDescriptor.getReadMethod() != null && !"class".equals(propertyDescriptor.getName())) {
                        Method readMethod = propertyDescriptor.getReadMethod();
                        if (!readMethod.isAccessible()) {
                            readMethod.setAccessible(true);
                        }
                        map.put(propertyDescriptor.getName(), readMethod.invoke(entity));
                    }
                }
            } catch (Exception e) {
                // Handle exception
            }
            resultList.add(map);
        }
        return resultList;
    }
}
