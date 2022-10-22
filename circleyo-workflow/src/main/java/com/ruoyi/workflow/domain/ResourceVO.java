package com.ruoyi.workflow.domain;

import java.util.ArrayList;
import java.util.List;

public class ResourceVO {
    private String id;
    private String resourcename;
    private long resourcetype;
    private List<ResourceVO> children;
    private String tableName;

    public ResourceVO(String id, String resourcename, Long resourceType, List<ResourceVO> children,String tableName) {
        this.id = id;
        this.resourcename = resourcename;
        this.resourcetype = resourceType;
        this.children = children;
        this.tableName=tableName;
    }

    private RelativeData relativeData;

    public List<ResourceVO> getChildren() {
        if (this.children == null) {
            this.children = new ArrayList<>();
        }
        return children;
    }

    public void setChildren(List<ResourceVO> children) {

        this.children = children;
    }

    public long getResourcetype() {
        return resourcetype;
    }

    public void setResourcetype(long resourcetype) {
        this.resourcetype = resourcetype;
    }

    public String getResourcename() {
        return resourcename;
    }

    public void setResourcename(String resourcename) {
        this.resourcename = resourcename;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public RelativeData getRelativeData() {
        return relativeData;
    }

    public void setRelativeData(RelativeData relativeData) {
        this.relativeData = relativeData;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }
}
