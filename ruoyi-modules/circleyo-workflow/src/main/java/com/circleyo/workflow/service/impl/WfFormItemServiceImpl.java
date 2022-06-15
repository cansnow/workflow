package com.circleyo.workflow.service.impl;

import java.util.List;

import com.circleyo.workflow.domain.WfFormItem;
import com.circleyo.workflow.mapper.WfFormItemMapper;
import com.circleyo.workflow.service.IWfFormItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 【请填写功能名称】Service业务层处理
 *
 * @author ruoyi
 * @date 2022-06-15
 */
@Service
public class WfFormItemServiceImpl implements IWfFormItemService
{
    @Autowired
    private WfFormItemMapper wfFormItemMapper;

    /**
     * 查询【请填写功能名称】
     *
     * @param id 【请填写功能名称】主键
     * @return 【请填写功能名称】
     */
    @Override
    public WfFormItem selectWfFormItemById(Long id)
    {
        return wfFormItemMapper.selectWfFormItemById(id);
    }

    /**
     * 查询【请填写功能名称】列表
     *
     * @param wfFormItem 【请填写功能名称】
     * @return 【请填写功能名称】
     */
    @Override
    public List<WfFormItem> selectWfFormItemList(WfFormItem wfFormItem)
    {
        return wfFormItemMapper.selectWfFormItemList(wfFormItem);
    }

    /**
     * 新增【请填写功能名称】
     *
     * @param wfFormItem 【请填写功能名称】
     * @return 结果
     */
    @Override
    public int insertWfFormItem(WfFormItem wfFormItem)
    {
        return wfFormItemMapper.insertWfFormItem(wfFormItem);
    }

    /**
     * 修改【请填写功能名称】
     *
     * @param wfFormItem 【请填写功能名称】
     * @return 结果
     */
    @Override
    public int updateWfFormItem(WfFormItem wfFormItem)
    {
        return wfFormItemMapper.updateWfFormItem(wfFormItem);
    }

    /**
     * 批量删除【请填写功能名称】
     *
     * @param ids 需要删除的【请填写功能名称】主键
     * @return 结果
     */
    @Override
    public int deleteWfFormItemByIds(Long[] ids)
    {
        return wfFormItemMapper.deleteWfFormItemByIds(ids);
    }

    /**
     * 删除【请填写功能名称】信息
     *
     * @param id 【请填写功能名称】主键
     * @return 结果
     */
    @Override
    public int deleteWfFormItemById(Long id)
    {
        return wfFormItemMapper.deleteWfFormItemById(id);
    }
}
