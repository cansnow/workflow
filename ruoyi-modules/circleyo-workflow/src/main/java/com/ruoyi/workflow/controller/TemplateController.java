package com.ruoyi.workflow.controller;

import com.ruoyi.common.core.utils.poi.ExcelUtil;
import com.ruoyi.common.core.web.controller.BaseController;
import com.ruoyi.common.core.web.domain.AjaxResult;
import com.ruoyi.common.core.web.page.TableDataInfo;
import com.ruoyi.common.log.annotation.Log;
import com.ruoyi.common.log.enums.BusinessType;
import com.ruoyi.common.security.annotation.RequiresPermissions;
import com.ruoyi.workflow.domain.FormDataVO;
import com.ruoyi.workflow.domain.ConditionVO;
import com.ruoyi.workflow.domain.Template;
import com.ruoyi.workflow.service.ITemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 模板Controller
 *
 * @author ruoyi
 * @date 2022-07-03
 */
@RestController
@RequestMapping("/template")
public class TemplateController extends BaseController
{
    @Autowired
    private ITemplateService templateService;

    /**
     * 查询模板列表
     */
    @RequiresPermissions("system:template:list")
    @GetMapping("/list")
    public TableDataInfo list(Template template)
    {
        startPage();
        List<Template> list = templateService.selectTemplateList(template);
        return getDataTable(list);
    }

    /**
     * 导出模板列表
     */
    @RequiresPermissions("system:template:export")
    @Log(title = "模板", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, Template template)
    {
        List<Template> list = templateService.selectTemplateList(template);
        ExcelUtil<Template> util = new ExcelUtil<Template>(Template.class);
        util.exportExcel(response, list, "模板数据");
    }

    /**
     * 获取模板详细信息
     */
    @RequiresPermissions("system:template:query")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Integer id)
    {
        return AjaxResult.success(templateService.selectTemplateById(id));
    }

    /**
     * 新增模板
     */
//    @RequiresPermissions("systetem:template:add")
    @Log(title = "模板", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody Template template)
    {
        templateService.insertTemplate(template);
        AjaxResult result=new AjaxResult(200,"操作成功",template);
        return result;
    }

    /**
     * 修改模板
     */
    @RequiresPermissions("system:template:edit")
    @Log(title = "模板", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody Template template)
    {
        return toAjax(templateService.updateTemplate(template));
    }

    /**
     * 删除模板
     */
    @RequiresPermissions("system:template:remove")
    @Log(title = "模板", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Integer[] ids)
    {
        return toAjax(templateService.deleteTemplateByIds(ids));
    }

    @Log(title = "表单数据", businessType = BusinessType.INSERT)
    @PostMapping("/saveFormData")
    public AjaxResult saveFormData(@RequestBody List<FormDataVO> formDataVO)
    {
        return toAjax(templateService.insrtFormDatas(formDataVO));
    }
    @Log(title = "表单数据", businessType = BusinessType.UPDATE)
    @PostMapping("/updateFormData")
    public AjaxResult updateFormData(HttpServletResponse response, @RequestBody List<ConditionVO> updates)
    {
        return toAjax(templateService.updateFormData(updates));
    }
    @Log(title = "表单数据", businessType = BusinessType.DELETE)
    @PostMapping("/deleteFormData")
    public AjaxResult deleteFormData(HttpServletResponse response, @RequestBody List<ConditionVO> deletes)
    {
        return toAjax(templateService.deleteFormData(deletes));
    }
}
