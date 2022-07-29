package com.ruoyi.workflow.mapper;

import com.ruoyi.workflow.domain.FormDataVO;
import com.ruoyi.workflow.domain.ConditionVO;
import com.ruoyi.workflow.domain.Template;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 模板Mapper接口
 *
 * @author ruoyi
 * @date 2022-07-03
 */
public interface TemplateMapper
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
     * 删除模板
     *
     * @param id 模板主键
     * @return 结果
     */
    public int deleteTemplateById(Integer id);

    /**
     * 批量删除模板
     *
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteTemplateByIds(Integer[] ids);

    int insrtFormData(FormDataVO formDataVO);

    int updateFormData(ConditionVO idvo);

    void deleteFormData(ConditionVO form);

    void updateFormDatas(@Param("updates")List<ConditionVO> updates);
}
