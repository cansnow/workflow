package com.ruoyi.workflow.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
//import com.ruoyi.common.core.utils.StringUtils;
//import com.ruoyi.common.core.utils.poi.ExcelUtil;
//import com.ruoyi.common.core.web.controller.BaseController;
//import com.ruoyi.common.core.web.domain.AjaxResult;
//import com.ruoyi.common.core.web.page.TableDataInfo;
//import com.ruoyi.common.log.annotation.Log;
//import com.ruoyi.common.log.enums.BusinessType;
//import com.ruoyi.common.security.annotation.RequiresPermissions;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.enums.BusinessType;
//import com.ruoyi.common.annotation.RequiresPermissions;
import com.ruoyi.workflow.domain.FormDataVO;
import com.ruoyi.workflow.domain.ConditionVO;
import com.ruoyi.workflow.domain.FormVO;
import com.ruoyi.workflow.domain.Template;
import com.ruoyi.workflow.entity.JdbcEntity;
import com.ruoyi.workflow.service.ITemplateService;
import com.ruoyi.workflow.utils.DataSetDetailUtil;
import com.ruoyi.workflow.utils.HttpUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.lang.annotation.Documented;
import java.util.ArrayList;
import java.util.List;

import static com.ruoyi.common.utils.PageUtils.startPage;

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

    //定义从数据库名，后续改为库读取？
    List<String> slaveTables = new ArrayList(){
        {
//            this.add("person");
//            this.add("tea_sale");
            this.add("lang");
            this.add("coffee");
        }
    };

    /**
     * 查询模板列表
     */
//    @RequiresPermissions("system:template:list")
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
//    @RequiresPermissions("system:template:export")
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
//    @RequiresPermissions("system:template:query")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Integer id)
    {
        return AjaxResult.success(templateService.selectTemplateById(id));
    }

    /**
     * 新增模板
     */
////    @RequiresPermissions("systetem:template:add")
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
//    @RequiresPermissions("system:template:edit")
    @Log(title = "模板", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody Template template)
    {
        return toAjax(templateService.updateTemplate(template));
    }

    /**
     * 删除模板
     */
//    @RequiresPermissions("system:template:remove")
    @Log(title = "模板", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Integer[] ids)
    {
        return toAjax(templateService.deleteTemplateByIds(ids));
    }

//    @Log(title = "表单数据", businessType = BusinessType.INSERT)
//    @PostMapping("/saveFormData")
//    public AjaxResult saveFormData(@RequestBody List<FormDataVO> formDataVO)
//    {
//        return toAjax(templateService.insrtFormDatas(formDataVO));
//    }
    @Log(title = "表单数据", businessType = BusinessType.UPDATE)
    @PostMapping("/updateFormData")
    public AjaxResult updateFormData(HttpServletResponse response, @RequestBody List<ConditionVO> updates)
    {
        return toAjax(templateService.updateFormData(updates));
    }
//    @Log(title = "表单数据", businessType = BusinessType.DELETE)
//    @PostMapping("/deleteFormData")
//    public AjaxResult deleteFormData(HttpServletResponse response, @RequestBody List<ConditionVO> deletes)
//    {
//        return toAjax(templateService.deleteFormData(deletes));
//    }


    @Log(title = "表单数据", businessType = BusinessType.INSERT)
    @PostMapping("/saveFormData")
    public AjaxResult saveFormData(@RequestBody List<FormDataVO> formDataVO)
    {
        try{
            if(formDataVO.isEmpty()){
                System.out.println("没有传任何参数！！！");
                return toAjax(0);
            }
            for (FormDataVO form : formDataVO) {
                System.out.println("table:"+form.getTable());
                if(StringUtils.isEmpty(form.getTable())){
                    System.out.println("table字段为空");
                    return toAjax(0);
                }
                if(!slaveTables.contains(form.getTable())){
                    System.out.println("查询从库");
                    templateService.insrtFormDataSlave(form);
                }else{
                    System.out.println("查询主库");
                    templateService.insrtFormData(form);
                }
            }
            return toAjax(formDataVO.size());
        }catch (Exception e){
            System.out.println("报错！！！");
            e.printStackTrace();
            return toAjax(0);
        }
    }

//    @Log(title = "表单数据", businessType = BusinessType.UPDATE)
//    @PostMapping("/updateFormDataNew")
//    public AjaxResult updateFormDataNew(HttpServletResponse response, @RequestBody List<ConditionVO> updates)
//    {
//        List<ConditionVO> ma = new ArrayList<>();
//        List<ConditionVO> sl = new ArrayList<>();
//
//        for(ConditionVO update : updates){
//            System.out.println("table:"+update.getTable());
//            if(slaveTables.contains(update.getTable())){
//                System.out.println("插入从库");
//                sl.add(update);
//            }else {
//                System.out.println("插入主库");
//                ma.add(update);
//            }
//        }
//        if(!ma.isEmpty()){
//            System.out.println("修改主库");
//            templateService.updateFormData(ma);
//        }
//        if(!sl.isEmpty()){
//            System.out.println("修改从库");
//            templateService.updateFormDataSlave(sl);
//        }
//        return toAjax(updates.size());
//    }

//    //2.7
//    @Log(title = "表单数据", businessType = BusinessType.UPDATE)
//    @PostMapping("/updateFormDataNew")
//    public AjaxResult updateFormDataNew(HttpServletResponse response, @RequestBody List<ConditionVO> updates)
//    {
//        try{
//            if(updates.isEmpty()){
//                System.out.println("没有传任何参数！！！");
//                return toAjax(0);
//            }
//            for(ConditionVO update : updates){
//                System.out.println("table:"+update.getTable());
//                if(StringUtils.isEmpty(update.getTable())){
//                    System.out.println("table字段为空");
//                    return toAjax(0);
//                }
//                if(slaveTables.contains(update.getTable())){
//                    System.out.println("修改从库");
//                    templateService.updateFormDatasNewSlave(update);
//                }else {
//                    System.out.println("修改主库");
//                    templateService.updateFormDatasNew(update);
//                }
//            }
//            return toAjax(updates.size());
//        }catch (Exception e){
//            System.out.println("报错！！！");
//            e.printStackTrace();
//            return toAjax(0);
//        }
//    }
//
//    //2.7
//    @Log(title = "表单数据", businessType = BusinessType.DELETE)
//    @PostMapping("/deleteFormData")
//    public AjaxResult deleteFormData(HttpServletResponse response, @RequestBody List<ConditionVO> deletes)
//    {
//        try{
//            if(deletes.isEmpty()){
//                System.out.println("没有传任何参数！！！");
//                return toAjax(0);
//            }
//            List<ConditionVO> ma = new ArrayList<>();
//            List<ConditionVO> sl = new ArrayList<>();
//
//            for(ConditionVO delete : deletes){
//                System.out.println("table:"+delete.getTable());
//                if(StringUtils.isEmpty(delete.getTable())){
//                    System.out.println("table字段为空");
//                    return toAjax(0);
//                }
//                if(slaveTables.contains(delete.getTable())){
//                    System.out.println("插入从库List");
//                    sl.add(delete);
//                }else {
//                    System.out.println("插入主库List");
//                    ma.add(delete);
//                }
//            }
//            if(!ma.isEmpty()){
//                System.out.println("删除主库");
//                templateService.deleteFormData(ma);
//            }
//            if(!sl.isEmpty()){
//                System.out.println("删除从库");
//                templateService.deleteFormDataSlave(sl);
//            }
//            return toAjax(deletes.size());
//        }catch (Exception e){
//            System.out.println("报错！！！");
//            e.printStackTrace();
//            return toAjax(0);
//        }
//    }
//
////    @Log(title = "表单数据", businessType = BusinessType.INSERT)
////    @PostMapping("/saveFormDataNew")
////    public AjaxResult saveFormDataNew(@RequestBody List<FormDataVO> formDataVO)
////    {
////        try{
////            return toAjax(templateService.saveFormDatasNew(formDataVO));
////        }catch (Exception e){
////            System.out.println("报错！！！");
////            e.printStackTrace();
////            return toAjax(0);
////        }
////    }
//
//
////    /**
////     * 2.4
////     * @param formVO
////     * @return
////     */
////    @Log(title = "表单数据", businessType = BusinessType.INSERT)
////    @PostMapping("/saveFormDataNew")
////    public AjaxResult saveFormDataNew(@RequestBody FormVO formVO)
////    {
////        try{
////            return toAjax(templateService.saveFormDatasNew(formVO));
////        }catch (Exception e){
////            System.out.println("报错！！！");
////            e.printStackTrace();
////            return toAjax(0);
////        }
////    }
//
//    //2.7
//    @Log(title = "表单数据", businessType = BusinessType.INSERT)
//    @PostMapping("/saveFormDataNew")
//    public AjaxResult saveFormDataNew(@RequestBody FormVO formVO)
//    {
//        try{
//            return toAjax(saveFormDatasNew(formVO));
//        }catch (Exception e){
//            System.out.println("报错！！！");
//            e.printStackTrace();
//            return toAjax(0);
//        }
//    }
//    //2.7
//    @Transactional(rollbackFor = Exception.class)
//    public int saveFormDatasNew(FormVO formVO) {
//        for (FormDataVO form : formVO.getFormDataVO()) {
//            System.out.println("add table:"+form.getTable());
//            if(StringUtils.isEmpty(form.getTable())){
//                System.out.println("table字段为空");
//                throw new RuntimeException("table字段为空");
//            }
//            if(slaveTables.contains(form.getTable())){
//                System.out.println("新增从库");
//                templateService.insrtFormDataSlaveNew(form);
//            }else{
//                System.out.println("新增主库");
//                templateService.insrtFormDataNew(form);
//            }
//
//        }
//        for(ConditionVO update : formVO.getUpdateObj()){
//            System.out.println("update table:"+update.getTable());
//            if(StringUtils.isEmpty(update.getTable())){
//                System.out.println("table字段为空");
//                throw new RuntimeException("table字段为空");
//            }
//            if(slaveTables.contains(update.getTable())){
//                System.out.println("修改从库");
//                templateService.updateFormDatasNewSlave(update);
//            }else {
//                System.out.println("修改主库");
//                templateService.updateFormDatasNew(update);
//            }
//        }
//        return 1;
//    }

    //================================2.8,加id，调用http获取table name==================================
    @Log(title = "表单数据", businessType = BusinessType.UPDATE)
    @PostMapping("/updateFormDataNew")
    public AjaxResult updateFormDataNew(HttpServletResponse response, @RequestBody List<ConditionVO> updates)
    {
        try{
            if(updates.isEmpty()){
                System.out.println("没有传任何参数！！！");
                return toAjax(0);
            }
            for(ConditionVO update : updates){
                System.out.println("table:"+update.getTable());
                if(StringUtils.isNotEmpty(update.getTid())){
                    String table = DataSetDetailUtil.returnTable(update.getTid());
                    if(StringUtils.isNotEmpty(table)){
                        update.setTable(table);
                        System.out.println("id修改从库");
                        templateService.updateFormDatasNewSlave(update);
                    }
                }else{
                    if(StringUtils.isEmpty(update.getTable())){
                        System.out.println("table字段为空");
                        return toAjax(0);
                    }
                    if(!slaveTables.contains(update.getTable())){
                        System.out.println("修改从库");
                        templateService.updateFormDatasNewSlave(update);
                    }else {
                        System.out.println("修改主库");
                        templateService.updateFormDatasNew(update);
                    }
                }
            }
            return toAjax(updates.size());
        }catch (Exception e){
            System.out.println("报错！！！");
            e.printStackTrace();
            return toAjax(0);
        }
    }

    @Log(title = "表单数据", businessType = BusinessType.DELETE)
    @PostMapping("/deleteFormData")
    public AjaxResult deleteFormData(HttpServletResponse response, @RequestBody List<ConditionVO> deletes)
    {
        try{
            if(deletes.isEmpty()){
                System.out.println("没有传任何参数！！！");
                return toAjax(0);
            }
            List<ConditionVO> ma = new ArrayList<>();
            List<ConditionVO> sl = new ArrayList<>();

            for(ConditionVO delete : deletes){
                System.out.println("table:"+delete.getTable());
                if(StringUtils.isNotEmpty(delete.getTid())){
                    String table = DataSetDetailUtil.returnTable(delete.getTid());
                    JdbcEntity jdbcEntity = DataSetDetailUtil.returnJdbcEntity(delete.getTid());
                    if(StringUtils.isNotEmpty(table)){
                        delete.setTable(table);
                        System.out.println("id删除动态数据源List");
                        templateService.deleteFormDatasJdbc(delete, jdbcEntity);
//                        sl.add(delete);
                    }
                }else{
                    if(StringUtils.isEmpty(delete.getTable())){
                        System.out.println("table字段为空");
                        return toAjax(0);
                    }
                    if(!slaveTables.contains(delete.getTable())){
                        System.out.println("插入从库List");
                        sl.add(delete);
                    }else {
                        System.out.println("插入主库List");
                        ma.add(delete);
                    }
                }
            }
            if(!ma.isEmpty()){
                System.out.println("删除主库");
                templateService.deleteFormData(ma);
            }
            if(!sl.isEmpty()){
                System.out.println("删除从库");
                templateService.deleteFormDataSlave(sl);
            }
            return toAjax(deletes.size());
        }catch (Exception e){
            System.out.println("报错！！！");
            e.printStackTrace();
            return toAjax(0);
        }
    }

    @Log(title = "表单数据", businessType = BusinessType.INSERT)
    @PostMapping("/saveFormDataNew")
    public AjaxResult saveFormDataNew(@RequestBody FormVO formVO)
    {
        try{
//            return toAjax(saveFormDatasNew2(formVO));
            //3.0 jdbc
            return toAjax(saveFormDatasJdbc(formVO));
        }catch (Exception e){
            System.out.println("报错！！！");
            e.printStackTrace();
            return toAjax(0);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public int saveFormDatasNew2(FormVO formVO) {
        for (FormDataVO form : formVO.getFormDataVO()) {
            System.out.println("add table:"+form.getTable());
            if(StringUtils.isNotEmpty(form.getTid())){
                String table = DataSetDetailUtil.returnTable(form.getTid());
                if(StringUtils.isNotEmpty(table)){
                    form.setTable(table);
                    System.out.println("id新增从库");
                    templateService.insrtFormDataSlaveNew(form);
                }
            }else{
                if(StringUtils.isEmpty(form.getTable())){
                    System.out.println("table字段为空");
                    throw new RuntimeException("table字段为空");
                }
                if(!slaveTables.contains(form.getTable())){
                    System.out.println("新增从库");
                    templateService.insrtFormDataSlaveNew(form);
                }else{
                    System.out.println("新增主库");
                    templateService.insrtFormDataNew(form);
                }
            }
        }
        for(ConditionVO update : formVO.getUpdateObj()){
            System.out.println("update table:"+update.getTable());
            if(StringUtils.isNotEmpty(update.getTid())){
                String table = DataSetDetailUtil.returnTable(update.getTid());
                if(StringUtils.isNotEmpty(table)){
                    update.setTable(table);
                    System.out.println("id修改从库");
                    templateService.updateFormDatasNewSlave(update);
                }
            }else{
                if(StringUtils.isEmpty(update.getTable())){
                    System.out.println("table字段为空");
                    throw new RuntimeException("table字段为空");
                }
                if(!slaveTables.contains(update.getTable())){
                    System.out.println("修改从库");
                    templateService.updateFormDatasNewSlave(update);
                }else {
                    System.out.println("修改主库");
                    templateService.updateFormDatasNew(update);
                }
            }
        }
        return 1;
    }

    //3.0 dbc
    @Transactional(rollbackFor = Exception.class)
    public int saveFormDatasJdbc(FormVO formVO) {
        for (FormDataVO form : formVO.getFormDataVO()) {
            System.out.println("add table:"+form.getTable());
            if(StringUtils.isNotEmpty(form.getTid())){
                String table = DataSetDetailUtil.returnTable(form.getTid());
                JdbcEntity jdbcEntity = DataSetDetailUtil.returnJdbcEntity(form.getTid());
                if(StringUtils.isNotEmpty(table)){
                    form.setTable(table);
                    System.out.println("id新增从库");
//                    templateService.insrtFormDataSlaveNew(form);
                    templateService.insrtFormDataJdbc(form, jdbcEntity);
                }
            }else{
                if(StringUtils.isEmpty(form.getTable())){
                    System.out.println("table字段为空");
                    throw new RuntimeException("table字段为空");
                }
                if(!slaveTables.contains(form.getTable())){
                    System.out.println("新增从库");
                    templateService.insrtFormDataSlaveNew(form);
                }else{
                    System.out.println("新增主库");
                    templateService.insrtFormDataNew(form);
                }
            }
        }
        for(ConditionVO update : formVO.getUpdateObj()){
            System.out.println("update table:"+update.getTable());
            if(StringUtils.isNotEmpty(update.getTid())){
                String table = DataSetDetailUtil.returnTable(update.getTid());
                JdbcEntity jdbcEntity = DataSetDetailUtil.returnJdbcEntity(update.getTid());
                if(StringUtils.isNotEmpty(table)){
                    update.setTable(table);
                    System.out.println("id修改从库");
//                    templateService.updateFormDatasNewSlave(update);
                    templateService.updateFormDatasJdbc(update, jdbcEntity);
                }
            }else{
                if(StringUtils.isEmpty(update.getTable())){
                    System.out.println("table字段为空");
                    throw new RuntimeException("table字段为空");
                }
                if(!slaveTables.contains(update.getTable())){
                    System.out.println("修改从库");
                    templateService.updateFormDatasNewSlave(update);
                }else {
                    System.out.println("修改主库");
                    templateService.updateFormDatasNew(update);
                }
            }
        }
        return 1;
    }

}
