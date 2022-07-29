package com.ruoyi.workflow.domain;

import java.util.List;

public class UserDBVO {
    private UserInfoVO constants;
    private List<DataSetVO> dataSetList;

    public List<DataSetVO> getDataSetList() {
        return dataSetList;
    }

    public void setDataSetList(List<DataSetVO> dataSetList) {
        this.dataSetList = dataSetList;
    }

    public UserInfoVO getConstants() {
        return constants;
    }

    public void setConstants(UserInfoVO constants) {
        this.constants = constants;
    }
}
