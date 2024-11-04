package com.ngw.fusion.common.util.treeNode;

import lombok.Data;

import java.util.List;

@Data
public class TreeNode {
    /**
     * 编码值
     */
    private String value;
    /**
     * 显示值
     */
    private String label;
    /**
     * 层级
     */
    private int level;
    /**
     * 序号
     */
    private int sortNum;

    /**
     * 序号
     */
    private String orgType;

    /**
     * 子节点
     */
    private List<TreeNode> children;

}
