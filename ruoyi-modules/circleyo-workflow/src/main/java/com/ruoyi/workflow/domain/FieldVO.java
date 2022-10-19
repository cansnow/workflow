package com.ruoyi.workflow.domain;

import com.ruoyi.common.core.web.domain.BaseEntity;

public class FieldVO {
    public FieldVO(){}
    private String fieldName;
    private Object fieldValue;
    public FieldVO(String fieldName,Object fieldValue){
        this.fieldName=fieldName;
        this.fieldValue=fieldValue;
    }

    public void setFieldName(String fieldName){
        this.fieldName=fieldName;
    }
    public String getFieldName(){
        return fieldName;
    }
    public void setFieldValue(Object fieldValue){
        this.fieldValue=fieldValue;
    }
    public Object getFieldValue(){
        return fieldValue;
    }
}
