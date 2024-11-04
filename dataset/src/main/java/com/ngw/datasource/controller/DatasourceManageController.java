package com.ngw.datasource.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ngw.fusion.common.dataBus.DataBus;
import com.ngw.datasource.pojo.Datasource;
import com.ngw.datasource.service.IDatasourceManageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 数据源管理 前端控制器
 * </p>
 *
 * @author 阎荣珠
 * @since 2024-05-14
 */
@Slf4j
@RestController
@RequestMapping("/datasource")
public class DatasourceManageController {

    @Autowired
    private IDatasourceManageService datasourceManageService;

    /**
     * 保存数据源
     * @param datasource
     * @return
     */
    @RequestMapping("/save")
    public DataBus save(@RequestBody Datasource datasource){
        try {
            log.debug("======================= 数据源保存 =======================");
            datasourceManageService.check(datasource);
            datasourceManageService.saveOrUpdate(datasource);
            log.debug("======================= 数据源保存成功 =======================");
            return DataBus.success();
        } catch (Exception e) {
            e.printStackTrace();
            log.debug("======================= 数据源保存失败 =======================");
            return DataBus.error().message("保存失败：" + e.getMessage());
        }
    }

    /**
     * 删除数据源
     * @param ids id列表
     * @return
     */
    @RequestMapping("/del")
    public DataBus delete(@RequestBody Map<String, List> ids){
        try {
            log.debug("======================= 数据源移除 =======================");
            List list = ids.get("ids");
            datasourceManageService.removeBatchByIds(list);
            log.debug("======================= 数据源移除成功 =======================");
            return DataBus.success();
        } catch (Exception e) {
            e.printStackTrace();
            log.debug("======================= 数据源移除失败 =======================");
            return DataBus.error().message("移除失败：" + e.getMessage());
        }
    }

    /**
     * 数据源分页列表
     * @param pageNum
     * @param pageSize
     * @param cont 查询内容
     * @return
     */
    @RequestMapping("/list")
    public DataBus list(Integer pageNum,Integer pageSize,String cont){
        try {
            log.debug("======================= 数据源列表查询 =======================");
            log.debug("======================= pageNum:{} pageSize:{} cont:{} =======================", pageNum, pageSize, cont);
            Object result;
            LambdaQueryWrapper<Datasource> wrapper = Wrappers.lambdaQuery();
            wrapper.like(StringUtils.isNotBlank(cont), Datasource::getDsName, cont);
            if (null == pageNum || null == pageSize) {
                result = datasourceManageService.list(wrapper);
            } else {
                Page<Datasource> page = new Page<>(pageNum,pageSize);
                result = datasourceManageService.page(page, wrapper);
            }
            log.debug("======================= 数据源列表查询成功 =======================");
            return DataBus.success().data(result);
        } catch (Exception e) {
            e.printStackTrace();
            log.debug("======================= 数据源列表查询失败 =======================");
            return DataBus.error().message("列表查询失败：" + e.getMessage());
        }
    }

    /**
     * 获取个案信息
     * @param id
     * @return
     */
    @RequestMapping("/get")
    public DataBus getById(String id){
        try {
            log.debug("======================= 数据源个案查询 =======================");
            return DataBus.success().data("list", datasourceManageService.getById(id));
        } catch (Exception e) {
            e.printStackTrace();
            log.debug("======================= 数据源个案查询失败 =======================");
            return DataBus.error().message("获取详细信息失败：" + e.getMessage());
        }
    }

    /**
     * 根据数据集编码获取数据集信息
     * @param dsCode
     * @return
     */
    @RequestMapping("/getByCode")
    public DataBus getByCode(String dsCode){
        try {
            log.debug("======================= 数据源个案查询 =======================");
            return DataBus.success().data("list", datasourceManageService.getByCode(dsCode));
        } catch (Exception e) {
            e.printStackTrace();
            log.debug("======================= 数据源个案查询失败 =======================");
            return DataBus.error().message("获取详细信息失败：" + e.getMessage());
        }
    }

    /**
     * 测试连接，有id，传id，没有id传另外三个参数，都有，则优先使用id
     * @param info 连接信息
     * @return
     */
    @PostMapping("/connectTest")
    public DataBus connectTest(@RequestBody Map<String,String> info){
        log.debug("======================= 数据源连通测试 =======================");
        String url;
        String username;
        String pwd;
        try {
            url = info.get("jdbcUrl");
            username = info.get("jdbcName");
            pwd = info.get("jdbcPwd");
            log.debug("======================= url:{} username:{} pwd:{} =======================", url, username, pwd);
            if (datasourceManageService.connect(url, username, pwd)) {
                log.debug("======================= 数据源连通测试成功 =======================");
                return DataBus.success();
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
            log.debug("======================= 数据源连通测试失败 =======================");
            return DataBus.error().message("参数出错，连接失败");
        } catch (Exception e) {
            e.printStackTrace();
        }
        log.debug("======================= 数据源连通测试失败 =======================");
        return DataBus.error().message("数据源测试连接失败");
    }

    @GetMapping("/connectTest")
    public DataBus connectTest(String id){
        try {
            log.debug("======================= 数据源连通测试 =======================");
            if (datasourceManageService.connect(id)) {
                return DataBus.success();
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
            log.debug("======================= 数据源连通测试失败 =======================");
            return DataBus.error().message("参数出错，连接失败");
        } catch (Exception e) {
            e.printStackTrace();
        }
        log.debug("======================= 数据源连通测试失败 =======================");
        return DataBus.error().message("数据源测试连接失败");
    }

}
