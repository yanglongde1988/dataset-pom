package com.ngw.datasource.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ngw.datasource.mapper.DatasourceMapper;
import com.ngw.datasource.pojo.Datasource;
import com.ngw.datasource.service.IDatasourceManageService;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 * <p>
 * 数据源管理 服务实现类
 * </p>
 *
 * @author 阎荣珠
 * @since 2024-05-14
 */
@Service
public class DatasourceManageServiceImpl extends ServiceImpl<DatasourceMapper, Datasource> implements IDatasourceManageService {

    public Boolean connect(String url, String username, String pwd) {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(url);
        config.setUsername(username);
        config.setPassword(pwd);
        // 创建 Hikari 数据源
        HikariDataSource dataSource = new HikariDataSource(config);
        try(Connection connection = dataSource.getConnection()){
            return connection != null ? true : false;
        }catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public Boolean connect(String id) {
         Datasource dm = getById(id);
         return connect(dm.getJdbcUrl(), dm.getJdbcName(), dm.getJdbcPwd());
    }

    @Override
    public void check(Datasource datasource) {
        Assert.isTrue(datasource != null, "数据源信息为空！");
        String dataCode = datasource.getDsCode();
        Assert.isTrue(StringUtils.isNotBlank(dataCode), "数据源编码为空！");
        String id = datasource.getId();
        LambdaQueryWrapper<Datasource> query = Wrappers.lambdaQuery(Datasource.class);
        query.eq(Datasource::getDsCode, dataCode)
                .ne(StringUtils.isNotBlank(id), Datasource::getId, id);
        List<Datasource> list = list(query);
        Assert.isTrue(list.size() == 0, "数据源编码已存在！");
    }

    @Value("${spring.datasource.url:jdbc:postgresql://localhost:5432/dataset}")
    private String jdbcUrl;

    @Value("${spring.datasource.username:postgres}")
    private String jdbcName;

    @Value("${spring.datasource.password:jdbc:Abc123$%^}")
    private String jdbcPwd;

    @Override
    public Datasource getByCode(String dsCode) {
        if(StringUtils.isBlank(dsCode)){
            Datasource datasource = new Datasource();
            datasource.setJdbcUrl(jdbcUrl);
            datasource.setJdbcName(jdbcName);
            datasource.setJdbcPwd(jdbcPwd);
            datasource.setDsDesc("数据集服务数据源");
            return datasource;
        }
        Assert.isTrue(StringUtils.isNotBlank(dsCode), "数据源编码为空！");
        LambdaQueryWrapper<Datasource> query = Wrappers.lambdaQuery(Datasource.class);
        query.eq(Datasource::getDsCode, dsCode);
        return getOne(query);
    }

}
