package com.circleyo.workflow.service;

import java.util.List;

import com.circleyo.workflow.domain.TableColumn;
import com.circleyo.workflow.domain.WfForm;

/**
 * 工作流表单Service接口
 *
 * @author circleyo
 * @date 2022-06-12
 */
public interface IWfFormService
{
    /**
     * 查询工作流表单
     *
     * @param id 工作流表单主键
     * @return 工作流表单
     */
    public WfForm selectWfFormById(Long id);

    /**
     * 查询工作流表单列表
     *
     * @param wfForm 工作流表单
     * @return 工作流表单集合
     */
    public List<WfForm> selectWfFormList(WfForm wfForm);

    /**
     * 新增工作流表单
     *
     * @param wfForm 工作流表单
     * @return 结果
     */
    public int insertWfForm(WfForm wfForm);

    /**
     * 修改工作流表单
     *
     * @param wfForm 工作流表单
     * @return 结果
     */
    public int updateWfForm(WfForm wfForm);

    /**
     * 批量删除工作流表单
     *
     * @param ids 需要删除的工作流表单主键集合
     * @return 结果
     */
    public int deleteWfFormByIds(Long[] ids);

    /**
     * 删除工作流表单信息
     *
     * @param id 工作流表单主键
     * @return 结果
     */
    public int deleteWfFormById(Long id);

    /**
     * 获取表对应的字段
     * @param table 表名称
     * @return 表字段
     */
    List<TableColumn> findFieldList(String table);

    /**
     * 保存业务数据
     * @param wfForm
     * @return
     */
    int addItemData(WfForm wfForm);

    /**
     * 删除表单业务数据
     * @param wfForm
     * @return
     */
    int deleteItemData(WfForm wfForm);

    /**
     * 更新表单业务数据
     * @param wfForm
     * @return
     */
    int updateItemData(WfForm wfForm);
}
