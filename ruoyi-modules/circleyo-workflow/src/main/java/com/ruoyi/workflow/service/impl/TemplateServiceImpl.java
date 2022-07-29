package com.ruoyi.workflow.service.impl;

import java.util.Date;
import java.util.List;

import com.ruoyi.common.security.utils.SecurityUtils;
import com.ruoyi.workflow.domain.FieldVO;
import com.ruoyi.workflow.domain.FormDataVO;
import com.ruoyi.workflow.domain.ConditionVO;
import com.ruoyi.workflow.domain.Template;
import com.ruoyi.workflow.mapper.TemplateMapper;
import com.ruoyi.workflow.service.ITemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
        template.setUid(SecurityUtils.getLoginUser().getUserid());
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
        for (ConditionVO form : deletes) {
            templateMapper.deleteFormData(form);
        }
        return deletes.size();
    }
}
