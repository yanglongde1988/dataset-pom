package com.ngw.fusion.common.util;

import java.io.Serializable;

public interface Constants extends Serializable {

    /**
     * 文件类型：excel
     */
    String FILE_TYPE_EXCEL = "EXCEL";
    /**
     * 文件类型：csv（默认为 utf-8）
     */
    String FILE_TYPE_CSV = "CSV";
    /**
     * 文件类型：csv（utf-8）
     */
    String FILE_TYPE_CSV_UTF8 = "CSV_UTF-8";
    /**
     * 文件类型：csv（gb2312）
     */
    String FILE_TYPE_CSV_GB2312 = "CSV_GB2312";

}
