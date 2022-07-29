package com.ruoyi.workflow.domain;

import java.util.List;

public class FormDataVO {
    private String table;
    private List<List<FieldVO>> fields;
    private List<FieldVO> singleFields;
    private List<ConditionVO> updateObj;
    private List<ConditionVO> deleteObj;
    private Object id;

    public void setTable(String table) {
        this.table = table;
    }

    public String getTable() {
        return table;
    }


    public List<List<FieldVO>> getFields() {
        return fields;
    }

    public void setFields(List<List<FieldVO>> fields) {
        this.fields = fields;
    }

    public List<FieldVO> getSingleFields() {
        return singleFields;
    }

    public void setSingleFields(List<FieldVO> singleFields) {
        this.singleFields = singleFields;
    }


    public List<ConditionVO> getUpdateObj() {
        return updateObj;
    }

    public void setUpdateObj(List<ConditionVO> updateObj) {
        this.updateObj = updateObj;
    }

    public List<ConditionVO> getDeleteObj() {
        return deleteObj;
    }

    public void setDeleteObj(List<ConditionVO> deleteObj) {
        this.deleteObj = deleteObj;
    }

    public Object getId() {
        return id;
    }

    public void setId(Object id) {
        this.id = id;
    }
}
