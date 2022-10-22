package com.ruoyi.workflow.domain;

import java.util.List;

public class TableDataVO {
    private String id;
    private String name;
    private String sql;
    private String type;
    private String tableName;
    private List<ColumnVO> columns;
    private List<SqlParamVO > sqlParams;

    public TableDataVO(String id, String name, String sql, String type, String tableName) {
        this.id = id;
        this.name = name;
        this.sql = sql;
        this.type = type;
        this.tableName = tableName;

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public List<ColumnVO> getColumns() {
        return columns;
    }

    public void setColumns(List<ColumnVO> columns) {
        this.columns = columns;
    }

    public List<SqlParamVO> getSqlParams() {
        return sqlParams;
    }

    public void setSqlParams(List<SqlParamVO> sqlParams) {
        this.sqlParams = sqlParams;
    }
}
