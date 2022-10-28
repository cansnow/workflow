package com.ruoyi.workflow.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.ruoyi.workflow.entity.JdbcEntity;

/**
 * 填报工具相关接口
 */
public class DataSetDetailUtil {
    public static String returnTable(String id){
        String res = HttpUtils.sendGet("http://admin.datains.cn/finance-admin/form/getDataSetDetail","id="+id);
        JSONObject jsonObject = JSON.parseObject(res);
        Integer code = (Integer) jsonObject.get("code");

        if(code == 1000){
            JSONObject json = jsonObject.getJSONObject("data");
            String sql = json.getString("sql");

            String[] tabs = sql.split("from ");
            String tab = "";
            if(tabs.length <= 1){
                tab = sql.split("FROM ")[1].split(" ")[0];
            }else{
                tab = tabs[1].split(" ")[0];
            }
            System.out.println("sql:"+sql);
            System.out.println("table:"+tab);
            return tab;
        }else{
            return "";
        }
    }

    public static JdbcEntity returnJdbcEntity(String id){
        String dbRes = HttpUtils.sendGet("http://admin.datains.cn/finance-admin/form/getDataSetLink","id="+id);
        JSONObject dbJson = JSON.parseObject(dbRes);
        System.out.println("dbRes:"+dbJson);
        Integer dbCode = (Integer) dbJson.get("code");
        if(dbCode == 1000){
            JSONObject dbj = dbJson.getJSONObject("data");
            String dbname = dbj.getString("dbname");
            String linktype = dbj.getString("linktype");
            String linkmethod = dbj.getString("linkmethod");
            String classforName = dbj.getString("drivername");
            String linkurl = dbj.getString("linkurl");
            String username = dbj.getString("usename");
            String password = dbj.getString("psd");
            if("mysql".equals(linktype)){
                classforName = "com.mysql.cj.jdbc.Driver";
            }

            JdbcEntity jdbcEntity = new JdbcEntity();
            jdbcEntity.setClassforName(classforName);
            jdbcEntity.setLinkurl(linkurl);
            jdbcEntity.setUsername(username);
            jdbcEntity.setPassword(password);
            return jdbcEntity;
        }
        return null;
    }
}
