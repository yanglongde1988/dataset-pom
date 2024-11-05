package com.ngw.util;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Assert;

import java.util.Arrays;

public enum Operator {

    EQUAL("eq", "=") {
        @Override
        public String loadParam(String param, String paramType) {
            if (!"NUMBER".equalsIgnoreCase(paramType)) {
                param = "'" + param + "'";
            }
            return String.format("=%s", param);
        }
    },
    NOT_EQUAL("ne", "!=") {
        @Override
        public String loadParam(String param, String paramType) {
            if (!"NUMBER".equalsIgnoreCase(paramType)) {
                param = "'" + param + "'";
            }
            return String.format("!=%s", param);
        }
    },
    GREATER_THAN("gt", ">") {
        @Override
        public String loadParam(String param, String paramType) {
            if (!"NUMBER".equalsIgnoreCase(paramType)) {
                param = "'" + param + "'";
            }
            return ">" + param;
        }
    },
    GREATER_THAN_OR_EQUAL("ge", ">=") {
        @Override
        public String loadParam(String param, String paramType) {
            if (!"NUMBER".equalsIgnoreCase(paramType)) {
                param = "'" + param + "'";
            }
            return ">=" + param;
        }
    },
    LESS_THAN("lt", "<") {
        @Override
        public String loadParam(String param, String paramType) {
            if (!"NUMBER".equalsIgnoreCase(paramType)) {
                param = "'" + param + "'";
            }
            return "<" + param;
        }
    },
    LESS_THAN_OR_EQUAL("le", "<=") {
        @Override
        public String loadParam(String param, String paramType) {
            if (!"NUMBER".equalsIgnoreCase(paramType)) {
                param = "'" + param + "'";
            }
            return "<=" + param;
        }
    },
    IN("in", " in ") {
        @Override
        public String loadParam(String param, String paramType) {
            String in = getIn(param, paramType);
            return String.format(" in (%s)", in);
        }
    },
    NOT_IN("nin", " not in ") {
        @Override
        public String loadParam(String param, String paramType) {
            String in = getIn(param, paramType);
            return String.format(" not in (%s)", in);
        }
    },
    LIKE("like", " like ") {
        @Override
        public String loadParam(String param, String paramType) {
            Assert.isTrue("TEXT".equalsIgnoreCase(paramType), "参数类型不支持");
            return " like '%" + param + "%'";
        }
    },
    NOT_LIKE("unlike", " not like ") {
        @Override
        public String loadParam(String param, String paramType) {
            Assert.isTrue("TEXT".equalsIgnoreCase(paramType), "参数类型不支持");
            return " not like '%" + param + "%'";
        }
    },
    START_WITH("sw", " like ") {
        @Override
        public String loadParam(String param, String paramType) {
            Assert.isTrue("TEXT".equalsIgnoreCase(paramType), "参数类型不支持");
            return " like '" + param + "%'";
        }
    },
    NOT_START_WITH("nsw", " not like ") {
        @Override
        public String loadParam(String param, String paramType) {
            Assert.isTrue("TEXT".equalsIgnoreCase(paramType), "参数类型不支持");
            return " not like '" + param + "%'";
        }
    },
    END_WITH("ew", " like ") {
        @Override
        public String loadParam(String param, String paramType) {
            Assert.isTrue("TEXT".equalsIgnoreCase(paramType), "参数类型不支持");
            return " like '%" + param + "'";
        }
    },
    NOT_END_WITH("new", " not like ") {
        @Override
        public String loadParam(String param, String paramType) {
            Assert.isTrue("TEXT".equalsIgnoreCase(paramType), "参数类型不支持");
            return " not like '%" + param + "'";
        }
    },
    CONTAIN_THAN("ct", " like ") {
        @Override
        public String loadParam(String param, String paramType) {
            Assert.isTrue("TEXT".equalsIgnoreCase(paramType), "参数类型不支持");
            return " like '%" + param + "%'";
        }
    },
    NOT_CONTAIN_THAN("nct", " not like ") {
        @Override
        public String loadParam(String param, String paramType) {
            Assert.isTrue("TEXT".equalsIgnoreCase(paramType), "参数类型不支持");
            return " not like '%" + param + "%'";
        }
    },
    NULL("null", " is null ") {
        @Override
        public String loadParam(String param, String paramType) {
            return " is null";
        }
    },
    NOT_NULL("notnull", " is not null ") {
        @Override
        public String loadParam(String param, String paramType) {
            return " is not null";
        }
    },
    IN_ARRAY("ia", " in ") {
        @Override
        public String loadParam(String param, String paramType) {
            String in = getIn(param, paramType);
            return String.format(" in (%s)", in);
        }
    },
    NOT_IN_ARRAY("nia", " not in ") {
        @Override
        public String loadParam(String param, String paramType) {
            String in = getIn(param, paramType);
            return String.format(" not in (%s)", in);
        }
    };

    private static String getIn(String param, String paramType) {
        String[] paramArr = param.split(",");
        String in;
        if ("NUMBER".equalsIgnoreCase(paramType)) {
            in = StringUtils.join(Arrays.asList(paramArr), ",");
        } else {
            in = "'" + StringUtils.join(Arrays.asList(paramArr), "','") + "'";
        }
        return in;
    }

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
