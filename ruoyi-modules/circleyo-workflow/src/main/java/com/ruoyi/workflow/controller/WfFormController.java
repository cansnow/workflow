package com.ruoyi.workflow.controller;

import java.util.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ruoyi.common.core.utils.StringUtils;
import com.ruoyi.common.security.utils.SecurityUtils;
import com.ruoyi.system.api.model.LoginUser;
import com.ruoyi.workflow.domain.*;
import com.ruoyi.workflow.domain.vo.ParamVo;
import com.ruoyi.workflow.utils.HttpUtils;
import com.ruoyi.workflow.utils.HttpsUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.log.annotation.Log;
import com.ruoyi.common.log.enums.BusinessType;
import com.ruoyi.common.security.annotation.RequiresPermissions;
import com.ruoyi.workflow.service.IWfFormService;
import com.ruoyi.common.core.web.controller.BaseController;
import com.ruoyi.common.core.web.domain.AjaxResult;
import com.ruoyi.common.core.utils.poi.ExcelUtil;
import com.ruoyi.common.core.web.page.TableDataInfo;

/**
 * 工作流表单Controller
 *
 * @author circleyo
 * @date 2022-06-12
 */
@RestController
@RequestMapping("/wfForm")
public class WfFormController extends BaseController {
    @Autowired
    private IWfFormService wfFormService;

    //定义从数据库名，后续改为库读取？
    List<String> slaveTables = new ArrayList(){
        {
//            this.add("person");
//            this.add("tea_sale");
            this.add("lang");
            this.add("coffee");
        }
    };

    //    @GetMapping("/db/list")
//    public TableDataInfo dataList(GenTable genTable)
//    {
//        List<GenTable> list = wfFormService.selectDbTableList(genTable);
//        return getDataTable(list);
//    }
    @GetMapping("/db/list")
    public Map dataList(GenTable genTable) {
        Map result = new HashMap();
        try{
            result.put("code", 1000);
            result.put("desc", "成功");
            result.put("tid", "e95bb1f9-dd3d-413b-be9e-8ef76e502535");
            result.put("data", wfFormService.dataListObject());
            return result;
        }catch (Exception e){
            e.printStackTrace();
            result.put("code", 5000);
            result.put("desc", "系统繁忙，请联系管理员！");
            result.put("error", e.getMessage());
            return result;
        }
    }

//    @PostMapping("/user/db/list")
//    public Map dataList(String table) {
//        Map result = new HashMap();
//        result.put("code", 1000);
//        result.put("desc", "成功");
//        result.put("data", wfFormService.userDataList(table));
//        return result;
//    }

    /**
     *
     * @param request
     * @return
     */
    @PostMapping("/user/db/list")
//    public Map dataList(HttpServletRequest request) {
    public Object dataList(HttpServletRequest request) {
        Map result = new HashMap();
        try{
            System.out.println(request.getParameter("table"));
            System.out.println(request.getParameter("tid"));
            if(!StringUtils.isEmpty(request.getParameter("tid"))){
                System.out.println("tid："+request.getParameter("tid"));
                String res = HttpUtils.sendGet("http://admin.datains.cn/finance-admin/form/getDataSetDetail","id="+request.getParameter("tid"));
                return res;
            }
            if(StringUtils.isEmpty(request.getParameter("table"))){
                result.put("code", 5001);
                result.put("desc", "table字段不能为空！");
                return result;
            }
            result.put("code", 1000);
            result.put("desc", "成功");
            String table = "";
            Map map = new HashMap();
            Enumeration paramNames = request.getParameterNames();
            while (paramNames.hasMoreElements()) {
                String paramName = (String) paramNames.nextElement();
                String[] paramValues = request.getParameterValues(paramName);
                if (paramValues.length == 1) {
                    String paramValue = paramValues[0];
                    if (paramValue.length() != 0) {
                        System.out.println("参数：" + paramName + "=" + paramValue);
                        if("table".equals(paramName)){
                            table = paramValue;
                        }else{
                            map.put(paramName, paramValue);
                        }
                    }
                }
            }
            System.out.println("table:"+table);

            ParamVo paramVo = new ParamVo();
            paramVo.setTable(table);
            paramVo.setMap(map);

            if(!slaveTables.contains(table)){
                result.put("data", wfFormService.userDataListParamSlave(paramVo));
            }else{
                result.put("data", wfFormService.userDataListParam(paramVo));
            }

//        if(table.equals("person")){   //查询职员信息
////            result.put("data", personService.userDataList(table));
//            result.put("data", wfFormService.userDataListParamSlave(paramVo));
//        }else if(table.equals("tea_sale")){  //查询茶叶销售
////            result.put("data", personService.userDataList(table));
//            result.put("data", wfFormService.userDataListParamSlave(paramVo));
//        }else{
//            result.put("data", wfFormService.userDataListParam(paramVo));
//        }
////        result.put("data", wfFormService.userDataList(table));
            return result;
        }catch (Exception e){
            e.printStackTrace();
            result.put("code", 5000);
            result.put("desc", "系统繁忙，请联系管理员！");
            result.put("error", e.getMessage());
            return result;
        }
    }

    /**
     * 获取用户登录全局变量
     * @param request
     * @return
     */
    @GetMapping("/user/getConstants")
    public UserDBVO getUserConstants(HttpServletRequest request) {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        UserDBVO result=new UserDBVO();
        result.setConstants(new UserInfoVO(loginUser.getUserid(),loginUser.getUsername(),"",""));
        return result;
    }

    /**
     * 查询工作流表单列表
     */
    @RequiresPermissions("workflow:wfForm:list")
    @GetMapping("/list")
    public TableDataInfo list(WfForm wfForm) {
        startPage();
        List<WfForm> list = wfFormService.selectWfFormList(wfForm);
        return getDataTable(list);
    }

    /**
     * 导出工作流表单列表
     */
    @RequiresPermissions("workflow:wfForm:export")
    @Log(title = "工作流表单", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, WfForm wfForm) {
        List<WfForm> list = wfFormService.selectWfFormList(wfForm);
        ExcelUtil<WfForm> util = new ExcelUtil<WfForm>(WfForm.class);
        util.exportExcel(response, list, "工作流表单数据");
    }

    /**
     * 获取工作流表单详细信息
     */
    @RequiresPermissions("workflow:wfForm:query")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id) {
        return AjaxResult.success(wfFormService.selectWfFormById(id));
    }

    /**
     * 新增工作流表单
     */
    @RequiresPermissions("workflow:wfForm:add")
    @Log(title = "工作流表单", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody WfForm wfForm) {
        return toAjax(wfFormService.insertWfForm(wfForm));
    }

    /**
     * 修改工作流表单
     */
    @RequiresPermissions("workflow:wfForm:edit")
    @Log(title = "工作流表单", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody WfForm wfForm) {
        return toAjax(wfFormService.updateWfForm(wfForm));
    }

    /**
     * 删除工作流表单
     */
    @RequiresPermissions("workflow:wfForm:remove")
    @Log(title = "工作流表单", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids) {
        return toAjax(wfFormService.deleteWfFormByIds(ids));
    }

    /**
     * 获取系统所有表
     */
//    @RequiresPermissions("workflow:wfForm:fieldList")
//    @Log(title = "工作流表单", businessType = BusinessType.OTHER)
//    @PostMapping("/fieldList")
//    public TableDataInfo findFieldList(String table)
//    {
//        return getDataTable(wfFormService.findFieldList(table));
//    }
    @Log(title = "工作流表单", businessType = BusinessType.OTHER)
    @PostMapping("/fieldList")
    public Map findFieldList(String table) {
        List<TableColumn> tableColumns = new ArrayList();
        if(!slaveTables.contains(table)){
            tableColumns = wfFormService.findFieldListSlave(table);
        }else{
            tableColumns = wfFormService.findFieldList(table);
        }

//        if(table.equals("person")){   //查询职员信息
////            tableColumns = personService.findFieldList(table);
//            tableColumns = wfFormService.findFieldListSlave(table);
//        }else if(table.equals("tea_sale")){  //查询茶叶销售
////            tableColumns = teaSaleService.findFieldList(table);
//            tableColumns = wfFormService.findFieldListSlave(table);
//        }else{
//            tableColumns = wfFormService.findFieldList(table);
//        }
////        List<TableColumn> tableColumns = wfFormService.findFieldList(table);
        Map result = new HashMap();
        TableDataVO tableDataVO = new TableDataVO("a96125427e854a3ebe7514c2b80d9e6c", "dbList里面有返回",
                "select * from " + table, "sqlTable", table);
        List<ColumnVO> l1 = new ArrayList<>();
        for(TableColumn column:tableColumns){
            l1.add(new ColumnVO(column.getColumnName(), column.getColumnComment(), column.getColumnType()));
        }
        List<SqlParamVO> l2 = new ArrayList<>();
        l2.add(new SqlParamVO("p1", "and a =[p1] "));
        result.put("code", 1000);
        result.put("desc", "成功");
        result.put("tid", "b5faf768-4f15-4b61-b109-76b62c987754");
        result.put("data", tableDataVO);

        tableDataVO.setSqlParams(l2);
        tableDataVO.setColumns(l1);
        return result;
    }

    /**
     * 新增表单业务数据
     */
    @RequiresPermissions("workflow:wfForm:addItemData")
    @Log(title = "工作流表单", businessType = BusinessType.INSERT)
    @PostMapping("/addItemData")
    public AjaxResult addItemData(@RequestBody WfForm wfForm) {
        return toAjax(wfFormService.addItemData(wfForm));
    }

    /**
     * 删除表单业务数据
     */
    @RequiresPermissions("workflow:wfForm:deleteItemData")
    @Log(title = "工作流表单", businessType = BusinessType.INSERT)
    @PostMapping("/deleteItemData")
    public AjaxResult deleteItemData(@RequestBody WfForm wfForm) {
        return toAjax(wfFormService.deleteItemData(wfForm));
    }

    /**
     * 更新业务表单数据
     */
    @RequiresPermissions("workflow:wfForm:updateItemData")
    @Log(title = "工作流表单", businessType = BusinessType.UPDATE)
    @PostMapping("/updateItemData")
    public AjaxResult updateItemData(@RequestBody WfForm wfForm) {
        return toAjax(wfFormService.updateItemData(wfForm));
    }


    @Log(title = "工作流表单", businessType = BusinessType.OTHER)
    @PostMapping("/fieldListNew")
    public Map findFieldListNew(String table, String tid) {
        Map result = new HashMap();

        List<TableColumn> tableColumns = new ArrayList();
        try{
            if(StringUtils.isNotEmpty(tid)){
                String res = HttpUtils.sendGet("http://admin.datains.cn/finance-admin/form/getDataSetDetail","id="+tid);
                JSONObject jsonObject = JSON.parseObject(res);
                System.out.println("return:"+jsonObject);
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
                    table = tab;
                    tableColumns = wfFormService.findFieldListSlave(tab);
                }
            }else if(StringUtils.isNotEmpty(table)){
                if(!slaveTables.contains(table)){
                    tableColumns = wfFormService.findFieldListSlave(table);
                }else{
                    tableColumns = wfFormService.findFieldList(table);
                }
            }

            if(tableColumns.isEmpty()){
                result.put("code", 5000);
                result.put("desc", "表："+table+"，不存在！");
                return result;
            }
            List<ColumnVO> columns = new ArrayList<>();
            List<SqlParamVO> sqlParams = new ArrayList<>();
            String sql = "select * from " + table +" where 1=1 ";
            for(TableColumn column:tableColumns){
                columns.add(new ColumnVO(column.getColumnName(), column.getColumnComment(), column.getColumnType()));
                String param = "<" +column.getColumnName()+ ">and "+column.getColumnName() + " = ?[" + column.getColumnName() +"]"+"</"+column.getColumnName()+"> ";
                sql = sql += param;
                sqlParams.add(new SqlParamVO(column.getColumnName(), "and "+column.getColumnName()+" = ["+column.getColumnName()+"] "));
            }

            TableDataVO tableDataVO = new TableDataVO("a96125427e854a3ebe7514c2b80d9e6c", "dbList里面有返回",
                    sql, "sqlTable", table);

            result.put("code", 1000);
            result.put("desc", "成功");
            result.put("tid", "b5faf768-4f15-4b61-b109-76b62c987754");
            result.put("data", tableDataVO);

            tableDataVO.setSqlParams(sqlParams);
            tableDataVO.setColumns(columns);
            return result;
        }catch (Exception e){
            e.printStackTrace();
            result.put("code", 5000);
            result.put("desc", "系统繁忙，请联系管理员！");
            result.put("error", e.getMessage());
            return result;
        }
    }
}
