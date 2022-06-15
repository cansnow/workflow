package com.circleyo.workflow.controller;

import java.util.List;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;
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
import com.circleyo.workflow.domain.WfForm;
import com.circleyo.workflow.service.IWfFormService;
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
public class WfFormController extends BaseController
{
    @Autowired
    private IWfFormService wfFormService;

    /**
     * 查询工作流表单列表
     */
    @RequiresPermissions("workflow:wfForm:list")
    @GetMapping("/list")
    public TableDataInfo list(WfForm wfForm)
    {
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
    public void export(HttpServletResponse response, WfForm wfForm)
    {
        List<WfForm> list = wfFormService.selectWfFormList(wfForm);
        ExcelUtil<WfForm> util = new ExcelUtil<WfForm>(WfForm.class);
        util.exportExcel(response, list, "工作流表单数据");
    }

    /**
     * 获取工作流表单详细信息
     */
    @RequiresPermissions("workflow:wfForm:query")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return AjaxResult.success(wfFormService.selectWfFormById(id));
    }

    /**
     * 新增工作流表单
     */
    @RequiresPermissions("workflow:wfForm:add")
    @Log(title = "工作流表单", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody WfForm wfForm)
    {
        return toAjax(wfFormService.insertWfForm(wfForm));
    }

    /**
     * 修改工作流表单
     */
    @RequiresPermissions("workflow:wfForm:edit")
    @Log(title = "工作流表单", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody WfForm wfForm)
    {
        return toAjax(wfFormService.updateWfForm(wfForm));
    }

    /**
     * 删除工作流表单
     */
    @RequiresPermissions("workflow:wfForm:remove")
    @Log(title = "工作流表单", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(wfFormService.deleteWfFormByIds(ids));
    }

    /**
     * 获取系统所有表
     */
    @RequiresPermissions("workflow:wfForm:fieldList")
    @Log(title = "工作流表单", businessType = BusinessType.OTHER)
    @PostMapping("/fieldList")
    public TableDataInfo findFieldList(String table)
    {
        return getDataTable(wfFormService.findFieldList(table));
    }
}
