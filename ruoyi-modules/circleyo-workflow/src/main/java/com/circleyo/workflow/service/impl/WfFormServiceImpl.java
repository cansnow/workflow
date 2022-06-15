package com.circleyo.workflow.service.impl;

import java.util.List;

import com.circleyo.workflow.domain.TableColumn;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import com.circleyo.workflow.mapper.WfFormMapper;
import com.circleyo.workflow.domain.WfForm;
import com.circleyo.workflow.service.IWfFormService;

/**
 * 工作流表单Service业务层处理
 *
 * @author circleyo
 * @date 2022-06-12
 */
@Service
@Repository
public class WfFormServiceImpl implements IWfFormService
{
    @Autowired
    private WfFormMapper wfFormMapper;

    /**
     * 查询工作流表单
     *
     * @param id 工作流表单主键
     * @return 工作流表单
     */
    @Override
    public WfForm selectWfFormById(Long id)
    {
        return wfFormMapper.selectWfFormById(id);
    }

    /**
     * 查询工作流表单列表
     *
     * @param wfForm 工作流表单
     * @return 工作流表单
     */
    @Override
    public List<WfForm> selectWfFormList(WfForm wfForm)
    {
        return wfFormMapper.selectWfFormList(wfForm);
    }

    /**
     * 新增工作流表单
     *
     * @param wfForm 工作流表单
     * @return 结果
     */
    @Override
    public int insertWfForm(WfForm wfForm)
    {
        return wfFormMapper.insertWfForm(wfForm);
    }

    /**
     * 修改工作流表单
     *
     * @param wfForm 工作流表单
     * @return 结果
     */
    @Override
    public int updateWfForm(WfForm wfForm)
    {
        return wfFormMapper.updateWfForm(wfForm);
    }

    /**
     * 批量删除工作流表单
     *
     * @param ids 需要删除的工作流表单主键
     * @return 结果
     */
    @Override
    public int deleteWfFormByIds(Long[] ids)
    {
        return wfFormMapper.deleteWfFormByIds(ids);
    }

    /**
     * 删除工作流表单信息
     *
     * @param id 工作流表单主键
     * @return 结果
     */
    @Override
    public int deleteWfFormById(Long id)
    {
        return wfFormMapper.deleteWfFormById(id);
    }

    @Override
    public List<TableColumn> findFieldList(String table) {
       return wfFormMapper.selectDbTableColumnsByName(table);
    }
}
