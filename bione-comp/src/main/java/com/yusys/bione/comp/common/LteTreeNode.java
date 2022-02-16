package com.yusys.bione.comp.common;

import java.util.ArrayList;
import java.util.List;

/**
 * @Title : AdminLTE样式模板树形节点类
 * @Description : 用于基于BootStrap3模板的自适应树形结构生成
 * @author wangjun
 * @Date 2018/8/21 11:16
 * @Version 1.0
 **/
public class LteTreeNode implements java.io.Serializable{

    private static final long serialVersionUID = 706621550112134151L;

    private String id ; // 节点id
    private String icon ; // 节点图标
    private String text ; // 节点名称
    private String url ; //访问路径
    private String urlType ; //url类型：relative为相对路径；absolute为绝对路径
    private String targetType ; //多页签显示方式
    private List<LteTreeNode> children = new ArrayList(); // 子节点集合

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTargetType() {
        return targetType;
    }

    public void setTargetType(String targetType) {
        this.targetType = targetType;
    }

    public List getChildren() {
        return children;
    }

    public void addChildren(LteTreeNode childTreeNode) {
        if(childTreeNode!=null) this.children.add(childTreeNode);
    }

    public String getUrlType() {
        return urlType;
    }

    public void setUrlType(String urlType) {
        this.urlType = urlType;
    }
}
