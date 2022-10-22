package com.ruoyi.workflow.domain;

public class ColumnVO {
    private String columnName;
    private String aliasName;
    private String columnType;

    public ColumnVO(String columnName,String aliasName,String columnType) {
        this.columnType=columnType;
        this.columnName=columnName;
        this.aliasName=aliasName;
    }

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public String getAliasName() {
        return aliasName;
    }

    public void setAliasName(String aliasName) {
        this.aliasName = aliasName;
    }

    public String getColumnType() {
        return columnType;
    }

    public void setColumnType(String columnType) {
        this.columnType = columnType;
    }
}
