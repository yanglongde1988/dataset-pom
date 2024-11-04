package com.ngw.fusion.common.base;


import cn.hutool.core.text.csv.CsvRow;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
* @author 杨 砻德
* @description 针对 数据导入回调方法 的数据库操作Service
* @createDate 2024-03-22 14:28:12
*/
public interface BaseIService<T> extends IService<T> {
    /**
     * 导入数据有对应的实体类时，可以实现该方法（实体类需要添加excel的注解ExcelProperty或csv的注解Alias）
     * @param entityList
     */
    void saveEntityBatch(Collection<T> entityList, Map map);

    /**
     * 导入数据为csv文件，且没有实体类时，实现该方法（excel无实体类时暂无实现）
     * @param rows
     */
    void saveCsvRowBatch(List<CsvRow> rows);
}
