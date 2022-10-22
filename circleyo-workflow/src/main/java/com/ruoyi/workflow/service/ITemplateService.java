package com.ruoyi.workflow.service;

import com.ruoyi.workflow.domain.FormDataVO;
import com.ruoyi.workflow.domain.ConditionVO;
import com.ruoyi.workflow.domain.FormVO;
import com.ruoyi.workflow.domain.Template;

import java.util.List;

/**
 * 模板Service接口
 *
 * @author ruoyi
 * @date 2022-07-03
 */
public interface ITemplateService
{
    /**
     * 查询模板
     *
     * @param id 模板主键
     * @return 模板
     */
    public Template selectTemplateById(Integer id);

    /**
     * 查询模板列表
     *
     * @param template 模板
     * @return 模板集合
     */
    public List<Template> selectTemplateList(Template template);

    /**
     * 新增模板
     *
     * @param template 模板
     * @return 结果
     */
    public int insertTemplate(Template template);

    /**
     * 修改模板
     *
     * @param template 模板
     * @return 结果
     */
    public int updateTemplate(Template template);

    /**
     * 批量删除模板
     *
     * @param ids 需要删除的模板主键集合
     * @return 结果
     */
    public int deleteTemplateByIds(Integer[] ids);

    /**
     * 删除模板信息
     *
     * @param id 模板主键
     * @return 结果
     */
    public int deleteTemplateById(Integer id);

    int insrtFormData(FormDataVO formDataVO);


    int updateFormData(List<ConditionVO> updates);

    int insrtFormDatas(List<FormDataVO> formDataVO);

    int deleteFormData(List<ConditionVO> deletes);

    //slave
    int insrtFormDataSlave(FormDataVO formDataVO);

    int updateFormDataSlave(List<ConditionVO> updates);

    int deleteFormDataSlave(List<ConditionVO> deletes);

    int updateFormDatasNew(ConditionVO updates);

    int updateFormDatasNewSlave(ConditionVO updates);

//    int saveFormDatasNew(List<FormDataVO> formDataVO);

    int saveFormDatasNew(FormVO formVO);

    int insrtFormDataSlaveNew(FormDataVO formDataVO);

    int insrtFormDataNew(FormDataVO formDataVO);
}
