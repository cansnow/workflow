package com.ruoyi.workflow.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ruoyi.workflow.entity.JdbcEntity;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 填报工具相关接口
 */
@Component
public class DataSetDetailUtil {
    private static String apiUrl;

    private static String apiSign;

    @Value("${workflow.apiUrl}")
    public void setApiUrl(String p){
        apiUrl = p;
    }

    @Value("${workflow.apiSign}")
    public void setApiSign(String p){
        apiSign = p;
    }

    public static JSONArray getDataSetTree(){
//        String res = HttpUtils.sendGet("http://admin.datains.cn/finance-admin/form/getDataSetDetail","id="+id);
        String res = HttpUtils.sendGet(apiUrl + "/form/getDataSetTree", "apiSign=" + apiSign);
        JSONObject jsonObject = JSON.parseObject(res);
        Integer code = (Integer) jsonObject.get("code");

        if(code == 1000){
            JSONArray dataArray = jsonObject.getJSONArray("data");
            return dataArray;
        }else{
            return null;
        }
    }

    public static String returnTable(String id){
//        String res = HttpUtils.sendGet("http://admin.datains.cn/finance-admin/form/getDataSetDetail","id="+id);
        String res = HttpUtils.sendGet(apiUrl + "/form/getDataSetDetail", "id=" + id + "&apiSign=" + apiSign);
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
//        String dbRes = HttpUtils.sendGet("http://admin.datains.cn/finance-admin/form/getDataSetLink","id="+id);
        String dbRes = HttpUtils.sendGet(apiUrl + "/form/getDataSetLink", "id=" + id + "&apiSign=" + apiSign);
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
