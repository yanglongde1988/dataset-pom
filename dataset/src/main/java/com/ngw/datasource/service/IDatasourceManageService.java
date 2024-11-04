package com.ngw.datasource.service;

import com.ngw.datasource.pojo.Datasource;
import com.baomidou.mybatisplus.extension.service.IService;

public interface IDatasourceManageService extends IService<Datasource> {

    Boolean connect(String username, String pwd, String url);

    Boolean connect(String id);

    void check(Datasource datasource);

    Datasource getByCode(String dataCode);
}
