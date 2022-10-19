package com.ruoyi.workflow.domain;

public class RelativeData {
    public RelativeData(String tbType){
        this.tbType=tbType;
    }
    private String tbType;

    public String getTbType() {
        return tbType;
    }

    public void setTbType(String tbType) {
        this.tbType = tbType;
    }
}
