package com.ngw.util;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Assert;

import java.util.Arrays;

public enum Operator {

    EQUAL("equal", "=") {
        @Override
        public String loadParam(String param, String paramType) {
            if (!"NUMBER".equalsIgnoreCase(paramType)) {
                param = "'" + param + "'";
            }
            return String.format("=%s", param);
        }
    },
    NOT_EQUAL("notEqual", "!=") {
        @Override
        public String loadParam(String param, String paramType) {
            if (!"NUMBER".equalsIgnoreCase(paramType)) {
                param = "'" + param + "'";
            }
            return String.format("!=%s", param);
        }
    },
    IN("in", "in") {
        @Override
        public String loadParam(String param, String paramType) {
            String[] ins = param.split(",");
            String join;
            if ("NUMBER".equalsIgnoreCase(paramType)) {
                join = StringUtils.join(Arrays.asList(ins), ",");
            } else {
                join = "'" + StringUtils.join(Arrays.asList(ins), "','") + "'";
            }
            return String.format(" in (%s)", join);
        }
    },
    NOT_IN("notIn", "not in") {
        @Override
        public String loadParam(String param, String paramType) {
            String[] ins = param.split(",");
            String join;
            if ("NUMBER".equalsIgnoreCase(paramType)) {
                join = StringUtils.join(Arrays.asList(ins), ",");
            } else {
                join = "'" + StringUtils.join(Arrays.asList(ins), "','") + "'";
            }
            return String.format(" not in (%s)", join);
        }
    },
    LIKE("like", "like") {
        @Override
        public String loadParam(String param, String paramType) {
            Assert.isTrue("TEXT".equalsIgnoreCase(paramType), "参数类型不支持");
            return " like '%" + param + "%'";
        }
    },
    NOT_LIKE("notLike", "not like") {
        @Override
        public String loadParam(String param, String paramType) {
            Assert.isTrue("TEXT".equalsIgnoreCase(paramType), "参数类型不支持");
            return " not like '%" + param + "%'";
        }
    },
    GREATER("greater", ">") {
        @Override
        public String loadParam(String param, String paramType) {
            if (!"NUMBER".equalsIgnoreCase(paramType)) {
                param = "'" + param + "'";
            }
            return ">" + param;
        }
    },
    GREATER_AND_EQUAL("greaterAndEqual", ">=") {
        @Override
        public String loadParam(String param, String paramType) {
            if (!"NUMBER".equalsIgnoreCase(paramType)) {
                param = "'" + param + "'";
            }
            return ">=" + param;
        }
    },
    LESS("less", "<") {
        @Override
        public String loadParam(String param, String paramType) {
            if (!"NUMBER".equalsIgnoreCase(paramType)) {
                param = "'" + param + "'";
            }
            return "<" + param;
        }
    },
    LESS_AND_EQUAL("lessAndEqual", "<=") {
        @Override
        public String loadParam(String param, String paramType) {
            if (!"NUMBER".equalsIgnoreCase(paramType)) {
                param = "'" + param + "'";
            }
            return "<=" + param;
        }
    };


//    LIKE_START("前缀", "like") {
//        @Override
//        public String loadParam(String param, String paramType) {
//            return " like '" + param + "%' ";
//        }
//    },
//    LIKE_NOT_START("非前缀", "not like") {
//        @Override
//        public String loadParam(String param, String paramType) {
//            return " not like '" + param + "%' ";
//        }
//    },
//    LIKE_END("后缀", "like") {
//        @Override
//        public String loadParam(String param, String paramType) {
//            return " like '%" + param + "' ";
//        }
//    },
//    LIKE_EQUAL("全包含", "=") {
//        @Override
//        public String loadParam(String param, String paramType) {
//            return " like '" + param + "' ";
//        }
//    },
//    START_("开始于", "=") {
//        @Override
//        public String loadParam(String param, String paramType) {
//            return " like '" + param + "' ";
//        }
//    },
//    END_("结束于", "=") {
//        @Override
//        public String loadParam(String param, String paramType) {
//            return " like '" + param + "' ";
//        }
//    },
//    IS_NULL("为空", "is null") {
//        @Override
//        public String loadParam(String param, String paramType) {
//            return " is null ";
//        }
//    },
//    IS_NOT_NULL("不为空", "is not null") {
//        @Override
//        public String loadParam(String param, String paramType) {
//            return " is not null ";
//        }
//    },
//    IS_IN_LIST("在列表", "in") {
//        @Override
//        public String loadParam(String param, String paramType) {
//            JSONArray array = JSON.parseArray(param);
//            return String.format(" in ('%s')", StringUtils.join(array, "','"));
//        }
//    },
//    IS_NOT_IN_LIST("不在列表", "not in") {
//        @Override
//        public String loadParam(String param, String paramType) {
//            JSONArray array = JSON.parseArray(param);
//            return String.format(" not in ('%s')", StringUtils.join(array, "','"));
//        }
//    },
//    //TODO
//    LIKE_LIST("包含列表", "like") {
//        @Override
//        public String loadParam(String param, String paramType) {
//            return " 1=1 ";
//        }
//    },
//    //TODO
//    SEARCH("搜索", "search") {
//        @Override
//        public String loadParam(String param, String paramType) {
//            return " 1=1 ";
//        }
//    },
//    //TODO
//    LIKE_START_LIST("前缀列表", "like") {
//        @Override
//        public String loadParam(String param, String paramType) {
//            return " 1=1 ";
//        }
//    },
//    //TODO
//    LIKE_END_LIST("后缀列表", "like") {
//        @Override
//        public String loadParam(String param, String paramType) {
//            return " 1=1 ";
//        }
//    },
//    //TODO
//    NOT_CONTAIN_LIST("不包含列表", "like") {
//        @Override
//        public String loadParam(String param, String paramType) {
//            return " 1=1 ";
//        }
//    },
//    //TODO
//    REG("reg", "reg") {
//        @Override
//        public String loadParam(String param, String paramType) {
//            return " 1=1 ";
//        }
//    };

    private String key;
    private String value;

    Operator(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

    public static Operator getOperatorByKey(String key) {
        for (Operator enums : Operator.values()) {
            if (StringUtils.equals(enums.getKey(), key)) {
                return enums;
            }
        }
        return null;
    }

    public static String getValueByKey(String key) {
        for (Operator enums : Operator.values()) {
            if (StringUtils.equals(enums.getValue(), key)) {
                return enums.getValue();
            }
        }
        return null;
    }

    public abstract String loadParam(String param, String paramType);

    public static void main(String[] args) {
        String equal = Operator.getOperatorByKey("in").loadParam("1,2,3", "NUMBER");
        System.out.println(equal);
    }

}
