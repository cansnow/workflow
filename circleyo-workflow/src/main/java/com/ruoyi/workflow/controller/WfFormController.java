package com.ruoyi.workflow.controller;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.core.domain.model.LoginUser;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.workflow.domain.*;
import com.ruoyi.workflow.domain.vo.ParamVo;
import com.ruoyi.workflow.entity.JdbcEntity;
import com.ruoyi.workflow.service.IWfFormService;
import com.ruoyi.workflow.utils.DataSetDetailUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

//import com.ruoyi.common.annotation.RequiresPermissions;

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
     * 3.0 动态配置数据源
     * @param request
     * @return
     */
    @PostMapping("/user/db/list")
    public Object dataList(HttpServletRequest request) {
        Map result = new HashMap();
        try{
            System.out.println(request.getParameter("table"));
            System.out.println(request.getParameter("tid"));
            String tid = null;
            String table = null;
            JdbcEntity jdbcEntity = null;
            if(!StringUtils.isEmpty(request.getParameter("tid"))){
                tid = request.getParameter("tid");
                table = DataSetDetailUtil.returnTable(tid);
                jdbcEntity = DataSetDetailUtil.returnJdbcEntity(tid);
            }else if(StringUtils.isEmpty(request.getParameter("table"))){
                result.put("code", 5001);
                result.put("desc", "table字段不能为空！");
                return result;
            }
            result.put("code", 1000);
            result.put("desc", "成功");
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
                        }else if("tid".equals(paramName)){
                            //tid不做条件
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

            if(tid != null && jdbcEntity != null){
                result.put("data", wfFormService.userDataListParamJdbc(paramVo,jdbcEntity));
            }else if(!slaveTables.contains(table)){
                result.put("data", wfFormService.userDataListParamSlave(paramVo));
            }else{
                result.put("data", wfFormService.userDataListParam(paramVo));
            }
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
//        LoginUser loginUser = getLoginUser();
        SysUser user = loginUser.getUser();
        result.setConstants(new UserInfoVO(user.getUserId(),loginUser.getUsername(),"",""));
        return result;
    }

    /**
     * 查询工作流表单列表
     */
//    @RequiresPermissions("workflow:wfForm:list")
    @GetMapping("/list")
    public TableDataInfo list(WfForm wfForm) {
        startPage();
        List<WfForm> list = wfFormService.selectWfFormList(wfForm);
        return getDataTable(list);
    }

    /**
     * 导出工作流表单列表
     */
//    @RequiresPermissions("workflow:wfForm:export")
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
//    @RequiresPermissions("workflow:wfForm:query")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id) {
        return AjaxResult.success(wfFormService.selectWfFormById(id));
    }

    /**
     * 新增工作流表单
     */
//    @RequiresPermissions("workflow:wfForm:add")
    @Log(title = "工作流表单", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody WfForm wfForm) {
        return toAjax(wfFormService.insertWfForm(wfForm));
    }

    /**
     * 修改工作流表单
     */
//    @RequiresPermissions("workflow:wfForm:edit")
    @Log(title = "工作流表单", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody WfForm wfForm) {
        return toAjax(wfFormService.updateWfForm(wfForm));
    }

    /**
     * 删除工作流表单
     */
//    @RequiresPermissions("workflow:wfForm:remove")
    @Log(title = "工作流表单", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids) {
        return toAjax(wfFormService.deleteWfFormByIds(ids));
    }

    /**
     * 获取系统所有表
     * @param table 表名
     * @return
     */
    @Log(title = "工作流表单", businessType = BusinessType.OTHER)
    @PostMapping("/fieldList")
    public Map findFieldList(@RequestParam(value = "table") String table) {
        List<TableColumn> tableColumns = new ArrayList();
        if(!slaveTables.contains(table)){
            tableColumns = wfFormService.findFieldListSlave(table);
        }else{
            tableColumns = wfFormService.findFieldList(table);
        }
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
//    @RequiresPermissions("workflow:wfForm:addItemData")
    @Log(title = "工作流表单", businessType = BusinessType.INSERT)
    @PostMapping("/addItemData")
    public AjaxResult addItemData(@RequestBody WfForm wfForm) {
        return toAjax(wfFormService.addItemData(wfForm));
    }

    /**
     * 删除表单业务数据
     */
//    @RequiresPermissions("workflow:wfForm:deleteItemData")
    @Log(title = "工作流表单", businessType = BusinessType.INSERT)
    @PostMapping("/deleteItemData")
    public AjaxResult deleteItemData(@RequestBody WfForm wfForm) {
        return toAjax(wfFormService.deleteItemData(wfForm));
    }

    /**
     * 更新业务表单数据
     */
//    @RequiresPermissions("workflow:wfForm:updateItemData")
    @Log(title = "工作流表单", businessType = BusinessType.UPDATE)
    @PostMapping("/updateItemData")
    public AjaxResult updateItemData(@RequestBody WfForm wfForm) {
        return toAjax(wfFormService.updateItemData(wfForm));
    }

    //3.0 jdbc动态读取数据源

    /**
     * 工作流表单
     * @param table 表名
     * @param tid api接口调用的表id
     * @return
     */
    @Log(title = "工作流表单", businessType = BusinessType.OTHER)
    @PostMapping("/fieldListNew")
    public Map findFieldListNew(@RequestParam(value = "table" ,required = false) String table, @RequestParam(value = "tid" ,required = false) String tid) {
        Map result = new HashMap();

        List<TableColumn> tableColumns = new ArrayList();
        try{
            if(StringUtils.isNotEmpty(tid)){

                String tab = DataSetDetailUtil.returnTable(tid);
                if(!StringUtils.isEmpty(tab)){
                    //3.0以前
//                    tableColumns = wfFormService.findFieldListSlave(tab);
                    //3.0 查询数据库连接信息
                    JdbcEntity jdbcEntity = DataSetDetailUtil.returnJdbcEntity(tid);
                    tableColumns = wfFormService.findFieldListJdbc(tab, jdbcEntity);
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
