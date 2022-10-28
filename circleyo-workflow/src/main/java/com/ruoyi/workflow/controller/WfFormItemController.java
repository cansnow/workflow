package com.ruoyi.workflow.controller;

import java.util.List;
import javax.servlet.http.HttpServletResponse;

import com.ruoyi.workflow.domain.WfFormItem;
import com.ruoyi.workflow.service.IWfFormItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 【请填写功能名称】Controller
 *
 * @author circleyo
 * @date 2022-06-15
 */
@RestController
@RequestMapping("/item")
public class WfFormItemController extends BaseController
{
    @Autowired
    private IWfFormItemService wfFormItemService;

    /**
     * 查询【请填写功能名称】列表
     */
//    @RequiresPermissions("system:item:list")
    @GetMapping("/list")
    public TableDataInfo list(WfFormItem wfFormItem)
    {
        startPage();
        List<WfFormItem> list = wfFormItemService.selectWfFormItemList(wfFormItem);
        return getDataTable(list);
    }

    /**
     * 导出【请填写功能名称】列表
     */
//    @RequiresPermissions("system:item:export")
    @Log(title = "【请填写功能名称】", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, WfFormItem wfFormItem)
    {
        List<WfFormItem> list = wfFormItemService.selectWfFormItemList(wfFormItem);
        ExcelUtil<WfFormItem> util = new ExcelUtil<WfFormItem>(WfFormItem.class);
        util.exportExcel(response, list, "【请填写功能名称】数据");
    }

    /**
     * 获取【请填写功能名称】详细信息
     */
//    @RequiresPermissions("system:item:query")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return AjaxResult.success(wfFormItemService.selectWfFormItemById(id));
    }

    /**
     * 新增【请填写功能名称】
     */
//    @RequiresPermissions("system:item:add")
    @Log(title = "【请填写功能名称】", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody WfFormItem wfFormItem)
    {
        return toAjax(wfFormItemService.insertWfFormItem(wfFormItem));
    }

    /**
     * 修改【请填写功能名称】
     */
//    @RequiresPermissions("system:item:edit")
    @Log(title = "【请填写功能名称】", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody WfFormItem wfFormItem)
    {
        return toAjax(wfFormItemService.updateWfFormItem(wfFormItem));
    }

    /**
     * 删除【请填写功能名称】
     */
//    @RequiresPermissions("system:item:remove")
    @Log(title = "【请填写功能名称】", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(wfFormItemService.deleteWfFormItemByIds(ids));
    }
}
