package com.ngw.datasource.pojo;

import com.baomidou.mybatisplus.annotation.TableName;
import com.ngw.fusion.common.base.BaseEntity;
import lombok.Data;


/**
 * <p>
 * 数据源管理
 * </p>
 *
 * @author 阎荣珠
 * @since 2024-05-14
 */
@Data
@TableName("t_manage_datasource")
public class Datasource extends BaseEntity {

    private static final long serialVersionUID = 1L;

    private String id;

    /**
     * 数据源名称
     */
    private String dsName;

    /**
     * 数据源编码
     */
    private String dsCode;

    /**
     * 用户名
     */
    private String jdbcUrl;

    /**
     * 密码
     */
    private String jdbcName;

    private String jdbcPwd;

    /**
     * 备注
     */
    private String dsDesc;

}
