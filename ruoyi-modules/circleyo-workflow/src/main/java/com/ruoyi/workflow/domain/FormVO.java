package com.ruoyi.workflow.domain;

import java.util.List;

public class FormVO {
    private List<FormDataVO> formDataVO;
    private List<ConditionVO> updateObj;

    public List<FormDataVO> getFormDataVO() {
        return formDataVO;
    }

    public void setFormDataVO(List<FormDataVO> formDataVO) {
        this.formDataVO = formDataVO;
    }

    public List<ConditionVO> getUpdateObj() {
        return updateObj;
    }

    public void setUpdateObj(List<ConditionVO> updateObj) {
        this.updateObj = updateObj;
    }

}
