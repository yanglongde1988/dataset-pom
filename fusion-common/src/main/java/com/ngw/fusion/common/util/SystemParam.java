package com.ngw.fusion.common.util;


import org.apache.commons.lang3.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public enum SystemParam {

    CURRENT_YEAR("currentYear", "NUMBER", "当前年份") {
        @Override
        public Integer getValue() {
            return LocalDate.now().getYear();
        }
    },
    CURRENT_MONTH("currentMonth", "NUMBER", "当前月份") {
        @Override
        public Integer getValue() {
            return LocalDate.now().getMonthValue();
        }
    },
    CURRENT_DAY("currentDay", "NUMBER", "当前日") {
        @Override
        public Integer getValue() {
            return LocalDate.now().getDayOfMonth();
        }
    },
    CURRENT_DATE("currentDate", "TEXT", "当前年月日") {
        @Override
        public String getValue() {
            return LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        }
    },
    CURRENT_HOUR("currentHour", "NUMBER", "当前小时") {
        @Override
        public Integer getValue() {
            return LocalDateTime.now().getHour();
        }
    },
    CURRENT_MINUTE("currentMinute", "NUMBER", "当前分钟") {
        @Override
        public Integer getValue() {
            return LocalDateTime.now().getMinute();
        }
    },
    CURRENT_SECOND("currentSecond", "NUMBER", "当前秒") {
        @Override
        public Integer getValue() {
            return LocalDateTime.now().getSecond();
        }
    },
    CURRENT_TIME("currentTime", "TEXT", "当前时间") {
        @Override
        public String getValue() {
            return LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
        }
    },
    CURRENT_DATETIME("currentDatetime", "TEXT", "当前日期时间") {
        @Override
        public String getValue() {
            return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        }
    };


    private String key;

    private String type;

    private String remark;

    public String getKey() {
        return key;
    }

    public String getType() {
        return type;
    }

    SystemParam(String key, String type, String remark) {
        this.key = key;
        this.type = type;
        this.remark = remark;
    }

    public static Object getValueByKey(String key) {
        if (StringUtils.isBlank(key)) {
            return null;
        }
        for (SystemParam enums : SystemParam.values()) {
            if (StringUtils.equals(enums.getKey(), key)) {
                return enums.getValue();
            }
        }
        return null;
    }

    public static SystemParam getParamByKey(String key) {
        if (StringUtils.isBlank(key)) {
            return null;
        }
        for (SystemParam enums : SystemParam.values()) {
            if (StringUtils.equals(enums.getKey(), key)) {
                return enums;
            }
        }
        return null;
    }

    public abstract Object getValue();

    public static void main(String[] args) {
        System.out.println(SystemParam.getValueByKey("currentYear"));
        System.out.println(SystemParam.getValueByKey("currentMonth"));
        System.out.println(SystemParam.getValueByKey("currentDay"));
        System.out.println(SystemParam.getValueByKey("currentDate"));
        System.out.println(SystemParam.getValueByKey("currentHour"));
        System.out.println(SystemParam.getValueByKey("currentMinute"));
        System.out.println(SystemParam.getValueByKey("currentSecond"));
        System.out.println(SystemParam.getValueByKey("currentTime"));
        System.out.println(SystemParam.getValueByKey("currentDatetime"));
    }
}
