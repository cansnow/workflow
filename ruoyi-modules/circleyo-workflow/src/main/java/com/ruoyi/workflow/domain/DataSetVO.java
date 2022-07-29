package com.ruoyi.workflow.domain;

import java.util.List;
import java.util.Map;

public class DataSetVO {
    private String dataSetId;
    private List<Map> valueList;

    public String getDataSetId() {
        return dataSetId;
    }

    public void setDataSetId(String dataSetId) {
        this.dataSetId = dataSetId;
    }


    public List<Map> getValueList() {
        return valueList;
    }

    public void setValueList(List<Map> valueList) {
        this.valueList = valueList;
    }
}
