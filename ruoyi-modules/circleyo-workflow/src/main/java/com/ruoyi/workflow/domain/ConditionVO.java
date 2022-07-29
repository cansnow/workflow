package com.ruoyi.workflow.domain;

import java.util.List;

public class ConditionVO {
    private String table;
    private String idName;
    private Object idValue;
    private List<FieldVO> conditions;
    private List<FieldVO> fields;

    public void setIdName(String idName) {
        this.idName = idName;
    }

    public String getIdName() {
        return idName;
    }

    public void setIdValue(Object idValue) {
        this.idValue = idValue;
    }

    public Object getIdValue() {
        return idValue;
    }

    public List<FieldVO> getFields() {
        return fields;
    }

    public void setFields(List<FieldVO> fields) {
        this.fields = fields;
    }

    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }

    public List<FieldVO> getConditions() {
        return conditions;
    }

    public void setConditions(List<FieldVO> conditions) {
        this.conditions = conditions;
    }
}
