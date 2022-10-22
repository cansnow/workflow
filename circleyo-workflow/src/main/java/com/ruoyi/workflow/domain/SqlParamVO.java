package com.ruoyi.workflow.domain;

public class SqlParamVO {
    private String paramName;
    private String paramDesc;
    public SqlParamVO(String paramName,String paramDesc){
        this.paramName=paramName;
        this.paramDesc=paramDesc;
    }

    public String getParamName() {
        return paramName;
    }

    public void setParamName(String paramName) {
        this.paramName = paramName;
    }

    public String getParamDesc() {
        return paramDesc;
    }

    public void setParamDesc(String paramDesc) {
        this.paramDesc = paramDesc;
    }
}
