package com.ngw.fusion.common.util;


import org.apache.commons.lang3.StringUtils;

public class CamelStringUtils {

    public static String camelToUnderline(String text) {
        if (StringUtils.isBlank(text)) return text;
        String[] split = StringUtils.splitByCharacterTypeCamelCase(text);
        StringBuilder sb = new StringBuilder();
        for (int i = 0, length = split.length; i < length; i++) {
            String s = split[i].toLowerCase();
            sb.append(s);
            if (i != length - 1) sb.append("_");
        }
        return sb.toString();
    }

    public static String underlineToCamel(String text) {
        if (StringUtils.isBlank(text)) return text;
        text = text.toLowerCase();
        String[] split = StringUtils.splitByWholeSeparator(text, "_");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < split.length; i++) {
            String s = split[i];
            if (i > 0 && s.length() > 0) s = s.substring(0, 1).toUpperCase() + s.substring(1);
            sb.append(s);
        }
        return sb.toString();
    }
}
