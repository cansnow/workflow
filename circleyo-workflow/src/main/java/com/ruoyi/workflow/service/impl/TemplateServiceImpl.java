package com.ruoyi.workflow.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

//import com.ruoyi.common.core.utils.StringUtils;
//import com.ruoyi.common.datasource.annotation.Slave;
//import com.ruoyi.common.security.utils.SecurityUtils;
import com.ruoyi.common.annotation.DataSource;
import com.ruoyi.common.enums.DataSourceType;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.workflow.domain.*;
import com.ruoyi.workflow.entity.JdbcEntity;
import com.ruoyi.workflow.mapper.TemplateMapper;
import com.ruoyi.workflow.service.ITemplateService;
import com.ruoyi.workflow.utils.JdbcUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

/**
 * 模板Service业务层处理
 *
 * @author ruoyi
 * @date 2022-07-03
 */
@Service
public class TemplateServiceImpl implements ITemplateService {
    @Autowired
    private TemplateMapper templateMapper;

    /**
     * 查询模板
     *
     * @param id 模板主键
     * @return 模板
     */
    @Override
    public Template selectTemplateById(Integer id) {
        return templateMapper.selectTemplateById(id);
    }

    /**
     * 查询模板列表
     *
     * @param template 模板
     * @return 模板
     */
    @Override
    public List<Template> selectTemplateList(Template template) {
        return templateMapper.selectTemplateList(template);
    }

    /**
     * 新增模板
     *
     * @param template 模板
     * @return 结果
     */
    @Override
    public int insertTemplate(Template template) {
        template.setAddtime(new Date().getTime());
        template.setUpdatetime(new Date().getTime());
        template.setAuthor(SecurityUtils.getUsername());
        template.setUid(SecurityUtils.getLoginUser().getUserId());
        return templateMapper.insertTemplate(template);

    }

    /**
     * 修改模板
     *
     * @param template 模板
     * @return 结果
     */
    @Override
    public int updateTemplate(Template template) {
        return templateMapper.updateTemplate(template);
    }

    /**
     * 批量删除模板
     *
     * @param ids 需要删除的模板主键
     * @return 结果
     */
    @Override
    public int deleteTemplateByIds(Integer[] ids) {
        return templateMapper.deleteTemplateByIds(ids);
    }

    /**
     * 删除模板信息
     *
     * @param id 模板主键
     * @return 结果
     */
    @Override
    public int deleteTemplateById(Integer id) {
        return templateMapper.deleteTemplateById(id);
    }

    @Override
    public int insrtFormData(FormDataVO formDataVO) {
        for (List<FieldVO> fields : formDataVO.getFields()) {
            FormDataVO form = new FormDataVO();
            form.setSingleFields(fields);
            form.setTable(formDataVO.getTable());
            templateMapper.insrtFormData(form);
        }
        return 1;
    }

    @Override
    public int updateFormData(List<ConditionVO> updates) {
        templateMapper.updateFormDatas(updates);
        return updates.size();
    }

    @Override
    public int insrtFormDatas(List<FormDataVO> formDataVO) {
        for (FormDataVO form : formDataVO) {
            insrtFormData(form);
        }
        return formDataVO.size();
    }

    @Override
    public int deleteFormData(List<ConditionVO> deletes) {
//        for (ConditionVO form : deletes) {
//            templateMapper.deleteFormData(form);
//        }
        templateMapper.deleteFormDatas(deletes);
        return deletes.size();
    }


    //slave
    @Override
//    @Slave
    @DataSource(value = DataSourceType.SLAVE)
    public int insrtFormDataSlave(FormDataVO formDataVO) {
        for (List<FieldVO> fields : formDataVO.getFields()) {
            FormDataVO form = new FormDataVO();
            form.setSingleFields(fields);
            form.setTable(formDataVO.getTable());
            templateMapper.insrtFormData(form);
        }
        return 1;
    }

    @Override
//    @Slave
    @DataSource(value = DataSourceType.SLAVE)
    public int updateFormDataSlave(List<ConditionVO> updates) {
        templateMapper.updateFormDatas(updates);
        return updates.size();
    }

    @Override
//    @Slave
    @DataSource(value = DataSourceType.SLAVE)
    public int deleteFormDataSlave(List<ConditionVO> deletes) {
//        for (ConditionVO form : deletes) {
//            templateMapper.deleteFormData(form);
//        }
        templateMapper.deleteFormDatas(deletes);
        return deletes.size();
    }

    @Override
    public int updateFormDatasNew(ConditionVO update) {
        return templateMapper.updateFormDatasNew(update);
    }

    @Override
//    @Slave
    @DataSource(value = DataSourceType.SLAVE)
    public int updateFormDatasNewSlave(ConditionVO update) {
        return templateMapper.updateFormDatasNew(update);
    }

    //v2.3
    //定义从数据库名，后续改为库读取？
    List<String> slaveTables = new ArrayList(){
        {
//            this.add("person");
//            this.add("tea_sale");
            this.add("lang");
            this.add("coffee");
        }
    };

//    @Override
//    @Transactional
//    public int saveFormDatasNew(List<FormDataVO> formDataVO) {
//        for (FormDataVO form : formDataVO) {
//            System.out.println("table:"+form.getTable());
//            if(StringUtils.isEmpty(form.getTable())){
//                System.out.println("table字段为空");
//                return 0;
//            }
//
//            if(slaveTables.contains(form.getTable())){
//                System.out.println("查询从库");
//                this.insrtFormDataSlaveNew(form);
//            }else{
//                System.out.println("查询主库");
//                this.insrtFormDataNew(form);
//            }
//            for(ConditionVO update : form.getUpdateObj()){
//                if(slaveTables.contains(update.getTable())){
//                    System.out.println("修改从库");
//                    this.updateFormDatasNewSlave(update);
//                }else {
//                    System.out.println("修改主库");
//                    this.updateFormDatasNew(update);
//                }
//            }
//        }
//        return 1;
//    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int saveFormDatasNew(FormVO formVO) {
        try{
            for (FormDataVO form : formVO.getFormDataVO()) {
                System.out.println("add table:"+form.getTable());
                if(StringUtils.isEmpty(form.getTable())){
                    System.out.println("table字段为空");
                    throw new RuntimeException("table字段为空");
                }
                if(!slaveTables.contains(form.getTable())){
                    System.out.println("查询从库");
                    this.insrtFormDataSlaveNew(form);
                }else{
                    System.out.println("查询主库");
                    this.insrtFormDataNew(form);
                }

            }
            for(ConditionVO update : formVO.getUpdateObj()){
                System.out.println("update table:"+update.getTable());
                if(StringUtils.isEmpty(update.getTable())){
                    System.out.println("table字段为空");
                    throw new RuntimeException("table字段为空");
                }
                if(!slaveTables.contains(update.getTable())){
                    System.out.println("修改从库");
                    this.updateFormDatasNewSlave(update);
                }else {
                    System.out.println("修改主库");
                    this.updateFormDatasNew(update);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return 0;
        }
        return 1;
    }


    @Override
//    @Slave
    @DataSource(value = DataSourceType.SLAVE)
    public int insrtFormDataSlaveNew(FormDataVO formDataVO) {
        FormDataVO form = new FormDataVO();
        form.setSingleFields(formDataVO.getSingleFields());
        form.setTable(formDataVO.getTable());
        templateMapper.insrtFormData(form);
        return 1;
    }

    @Override
    public int insrtFormDataNew(FormDataVO formDataVO) {
        FormDataVO form = new FormDataVO();
        form.setSingleFields(formDataVO.getSingleFields());
        form.setTable(formDataVO.getTable());
        templateMapper.insrtFormData(form);
        return 1;
    }

    //3.0 jdbc
    @Override
    public int insrtFormDataJdbc(FormDataVO formDataVO, JdbcEntity jdbcEntity) {
//        FormDataVO form = new FormDataVO();
//        form.setSingleFields(formDataVO.getSingleFields());
//        form.setTable(formDataVO.getTable());
//        templateMapper.insrtFormData(form);
        List<FieldVO> list = new ArrayList<>();

        String sql = "insert into " + formDataVO.getTable();
        StringBuilder builder = new StringBuilder(sql);
        builder.append(" (");
        for(FieldVO singleFields : formDataVO.getSingleFields()){
            if(!StringUtils.isEmpty(singleFields.getFieldName()) && !"null".equals(singleFields.getFieldName()) && singleFields.getFieldValue() != null && !"null".equals(singleFields.getFieldValue())){
                builder.append(singleFields.getFieldName()+",");
            }
        }
        builder.replace(builder.length()-1,builder.length(),")");
        builder.append(" value (");
        for(FieldVO singleFields : formDataVO.getSingleFields()){
            if(!StringUtils.isEmpty(singleFields.getFieldName()) && !"null".equals(singleFields.getFieldName()) && singleFields.getFieldValue() != null && !"null".equals(singleFields.getFieldValue())){
                builder.append("'"+singleFields.getFieldValue()+"',");
            }
        }
        builder.replace(builder.length()-1,builder.length(),")");
        return JdbcUtils.jdbcSave(jdbcEntity.getClassforName(),jdbcEntity.getLinkurl(),jdbcEntity.getUsername(),jdbcEntity.getPassword(),builder.toString());
    }

    @Override
    public int updateFormDatasJdbc(ConditionVO update, JdbcEntity jdbcEntity) {
        String sql = "update " + update.getTable();
        StringBuilder builder = new StringBuilder(sql);
        builder.append(" set ");
        for(FieldVO fieldVO : update.getFields()){
            if(!StringUtils.isEmpty(fieldVO.getFieldName()) && !"null".equals(fieldVO.getFieldName()) && fieldVO.getFieldValue() != null && !"null".equals(fieldVO.getFieldValue())){
                builder.append(fieldVO.getFieldName()+" = '"+fieldVO.getFieldValue()+"',");
            }
        }
        builder.replace(builder.length()-1,builder.length(),"");
        builder.append(" where 1=1");
        for(FieldVO fieldVO : update.getConditions()){
            if(!StringUtils.isEmpty(fieldVO.getFieldName()) && !"null".equals(fieldVO.getFieldName()) && fieldVO.getFieldValue() != null && !"null".equals(fieldVO.getFieldValue())){
                builder.append(" and "+ fieldVO.getFieldName()+" = '"+fieldVO.getFieldValue()+"'");
            }
        }
        return JdbcUtils.jdbcSave(jdbcEntity.getClassforName(),jdbcEntity.getLinkurl(),jdbcEntity.getUsername(),jdbcEntity.getPassword(),builder.toString());
    }

    @Override
    public int deleteFormDatasJdbc(ConditionVO delete, JdbcEntity jdbcEntity) {
        String sql = "delete from " + delete.getTable();
        StringBuilder builder = new StringBuilder(sql);
        builder.append(" where 1=1");
        for(FieldVO fieldVO : delete.getConditions()){
            if(!StringUtils.isEmpty(fieldVO.getFieldName()) && !"null".equals(fieldVO.getFieldName()) && fieldVO.getFieldValue() != null && !"null".equals(fieldVO.getFieldValue())){
                builder.append(" and "+ fieldVO.getFieldName()+" = '"+fieldVO.getFieldValue()+"'");
            }
        }
        return JdbcUtils.jdbcSave(jdbcEntity.getClassforName(),jdbcEntity.getLinkurl(),jdbcEntity.getUsername(),jdbcEntity.getPassword(),builder.toString());
    }
}
