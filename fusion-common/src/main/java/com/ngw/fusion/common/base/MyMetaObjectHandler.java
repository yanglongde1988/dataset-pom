package com.ngw.fusion.common.base;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class MyMetaObjectHandler implements MetaObjectHandler {
    @Override
    public void insertFill(MetaObject metaObject) {
        this.strictInsertFill(metaObject, "createTime", () -> LocalDateTime.now(), LocalDateTime.class); // 起始版本 3.3.3(推荐)
        this.strictInsertFill(metaObject, "updateTime", () -> LocalDateTime.now(), LocalDateTime.class); // 起始版本 3.3.3(推荐)
//        this.strictInsertFill(metaObject, "createBy",  () -> getUserId(), String.class); // 起始版本 3.3.3(推荐)
//        this.strictInsertFill(metaObject, "sysOrgCode",  () -> getOrgCode(), String.class); // 起始版本 3.3.3(推荐)
        this.strictInsertFill(metaObject, "delFlag",  () -> 0, Integer.class); // 起始版本 3.3.3(推荐)
    }
    @Override
    public void updateFill(MetaObject metaObject) {
//        this.strictUpdateFill(metaObject, "updateTime", () -> LocalDateTime.now(), LocalDateTime.class); // 起始版本 3.3.3(推荐)
//        this.strictUpdateFill(metaObject, "updateBy", () -> getUserId(), String.class); // 起始版本 3.3.3(推荐)
        this.setFieldValByName("updateTime", LocalDateTime.now(), metaObject);
    }

//    String getUserId(){
//        return "创建/修改人";
//    }

//    String getUserName(){
//        return getPerson().getPersonName();
//    }

//    String getOrgCode(){
//        return "默认当前登陆人部门";
//    }

}
