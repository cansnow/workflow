package com.circleyo.workflow.mapper;

import java.util.List;

import com.circleyo.workflow.domain.TableColumn;
import com.circleyo.workflow.domain.WfForm;
import org.apache.ibatis.annotations.Mapper;

/**
 * 工作流表单Mapper接口
 *
 * @author circleyo
 * @date 2022-06-12
 */
@Mapper
public interface WfFormMapper
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
     * 删除工作流表单
     *
     * @param id 工作流表单主键
     * @return 结果
     */
    public int deleteWfFormById(Long id);

    /**
     * 批量删除工作流表单
     *
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteWfFormByIds(Long[] ids);

    /**
     * 根据表名称获取表字段
     * @param table 表名称
     * @return 表字段
     */
    List<TableColumn> selectDbTableColumnsByName(String table);
}
