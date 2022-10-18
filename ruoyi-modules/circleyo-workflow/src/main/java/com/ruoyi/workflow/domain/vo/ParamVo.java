package com.ruoyi.workflow.domain.vo;

import java.util.Map;

public class ParamVo {

    private String table;
    private Map<String,String> map;

    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }

    public Map<String, String> getMap() {
        return map;
    }

    public void setMap(Map<String, String> map) {
        this.map = map;
    }
}
