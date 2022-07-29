package com.ruoyi.workflow.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletResponse;

import com.ruoyi.workflow.domain.*;
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

    //    @GetMapping("/db/list")
//    public TableDataInfo dataList(GenTable genTable)
//    {
//        List<GenTable> list = wfFormService.selectDbTableList(genTable);
//        return getDataTable(list);
//    }
    @GetMapping("/db/list")
    public Map dataList(GenTable genTable) {
        Map result = new HashMap();
        result.put("code", 1000);
        result.put("desc", "成功");
        result.put("tid", "e95bb1f9-dd3d-413b-be9e-8ef76e502535");
        result.put("data", wfFormService.dataList());
        return result;
    }

    @PostMapping("/user/db/list")
    public Map dataList(String table) {
        Map result = new HashMap();
        result.put("code", 1000);
        result.put("desc", "成功");
        result.put("data", wfFormService.userDataList(table));
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
        List<TableColumn> tableColumns = wfFormService.findFieldList(table);
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
}
