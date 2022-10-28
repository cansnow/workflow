package com.ruoyi.workflow.entity;

public class JdbcEntity {
    private String classforName;
    private String linkurl;
    private String username;
    private String password;
    private String sql;

    public String getClassforName() {
        return classforName;
    }

    public void setClassforName(String classforName) {
        this.classforName = classforName;
    }

    public String getLinkurl() {
        return linkurl;
    }

    public void setLinkurl(String linkurl) {
        this.linkurl = linkurl;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }
}
