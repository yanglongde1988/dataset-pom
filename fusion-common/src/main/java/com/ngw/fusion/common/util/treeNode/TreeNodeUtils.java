package com.ngw.fusion.common.util.treeNode;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ngw.fusion.common.util.JSON;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TreeNodeUtils {

    /**
     * Entity转TreeNode 需要指明各字段信息，驼峰命名
     * @param list Entity集合
     * @param pId 根节点
     * @param pValue 父节点字段
     * @param value 节点值字段
     * @param label 节点名称字段
     * @param level 层级字段
     * @param sort 排序字段
     * @param <T>
     * @return
     */
    public static <T> List<TreeNode> listToTree(List<T> list, String pId, String pValue, String value, String label, String level, String sort){
        if (list == null || pValue == null || value == null || label == null){
            return null;
        }
        List<JSONObject> objects = JSONArray.parseArray(JSON.toJSON(list), JSONObject.class);
        List<JSONObject> nodes = new ArrayList<>();
        if (StringUtils.isBlank(pId)){
            nodes = objects.stream().filter(i -> StringUtils.isBlank(i.getString(pValue))).collect(Collectors.toList());
        }else {
            nodes = objects.stream().filter(i -> pId.equals(i.getString(pValue))).collect(Collectors.toList());
        }
        List<TreeNode> r = new ArrayList<>();
        nodes.forEach(i->{
            TreeNode treeNode = new TreeNode();
            treeNode.setValue(i.getString(value));
            treeNode.setLabel(i.getString(label));
            if (level != null) treeNode.setLevel(i.getInteger(level));
            if (sort != null) treeNode.setSortNum(i.getInteger(sort));
            List<TreeNode> childrenNodes = listToTree(list, i.getString(value), pValue, value, label, level, sort);
            childrenNodes = childrenNodes.size() == 0 ? null : childrenNodes;
            treeNode.setChildren(childrenNodes);
            r.add(treeNode);
        });
        return r;
    }

}
