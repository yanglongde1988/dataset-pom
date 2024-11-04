package com.ngw.fusion.common.util;

import java.util.UUID;

public class UUIDUtils {
    public static String uuid(){
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    public static String uuid(int length){
        return uuid().substring(0, length);
    }

    public static synchronized long LongId(){
        return System.currentTimeMillis();
    }
}
