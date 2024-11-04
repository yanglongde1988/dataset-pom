package com.ngw.fusion.common.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

public class TaskCodeUtil {
    private static final AtomicInteger SEQUENCE_NUMBER = new AtomicInteger(0);
    private static final Map<String,Integer> map = new HashMap();
    public static String taskCode(String keynum){
        return "key" + keynum + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + num();
    }

    public static String infoCode(String taskCode){
        return taskCode + "_" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + num();
    }

    public static String num() {
        return String.format("%03d", getNextSequence());
    }

    private static int getCurrentDay() {
        Integer day = map.get("current");
        Calendar calendar = Calendar.getInstance();
        int now = calendar.get(Calendar.DAY_OF_YEAR);
        if (day == null || day.intValue() != now) {
            map.put("current", calendar.get(Calendar.DAY_OF_YEAR));
            return 1;
        }
        return 0;
    }

    public static int getNextSequence() {
        int currentDay = getCurrentDay();
        if (currentDay != 0) {
            SEQUENCE_NUMBER.getAndSet(0);
        }

        return SEQUENCE_NUMBER.incrementAndGet();
    }

//    public static void main(String[] args) {
//        for (int i = 0; i < 10; i++) {
//            try {
//                if (i==5)
//                Thread.sleep(10000,1);
//                System.out.println("=====");
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//            System.out.println(num());
//        }
//    }
}
