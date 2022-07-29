package com.ruoyi.workflow.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ruoyi.common.redis.service.RedisService;
import com.ruoyi.common.security.utils.SecurityUtils;
import com.ruoyi.system.api.model.LoginUser;
import com.ruoyi.workflow.domain.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import com.ruoyi.workflow.mapper.WfFormMapper;
import com.ruoyi.workflow.service.IWfFormService;

/**
 * 工作流表单Service业务层处理
 *
 * @author circleyo
 * @date 2022-06-12
 */
@Service
@Repository
public class WfFormServiceImpl implements IWfFormService {
    @Autowired
    private WfFormMapper wfFormMapper;
    @Autowired
    private RedisService redisService;

    @Override
    public List<GenTable> selectDbTableList(GenTable genTable) {
        return wfFormMapper.selectDbTableList(genTable);
    }

    /**
     * 查询工作流表单
     *
     * @param id 工作流表单主键
     * @return 工作流表单
     */
    @Override
    public WfForm selectWfFormById(Long id) {
        return wfFormMapper.selectWfFormById(id);
    }

    /**
     * 查询工作流表单列表
     *
     * @param wfForm 工作流表单
     * @return 工作流表单
     */
    @Override
    public List<WfForm> selectWfFormList(WfForm wfForm) {
        return wfFormMapper.selectWfFormList(wfForm);
    }

    /**
     * 新增工作流表单
     *
     * @param wfForm 工作流表单
     * @return 结果
     */
    @Override
    public int insertWfForm(WfForm wfForm) {
        return wfFormMapper.insertWfForm(wfForm);
    }

    /**
     * 修改工作流表单
     *
     * @param wfForm 工作流表单
     * @return 结果
     */
    @Override
    public int updateWfForm(WfForm wfForm) {
        return wfFormMapper.updateWfForm(wfForm);
    }

    /**
     * 批量删除工作流表单
     *
     * @param ids 需要删除的工作流表单主键
     * @return 结果
     */
    @Override
    public int deleteWfFormByIds(Long[] ids) {
        return wfFormMapper.deleteWfFormByIds(ids);
    }

    /**
     * 删除工作流表单信息
     *
     * @param id 工作流表单主键
     * @return 结果
     */
    @Override
    public int deleteWfFormById(Long id) {
        return wfFormMapper.deleteWfFormById(id);
    }

    @Override
    public List<TableColumn> findFieldList(String table) {
        return wfFormMapper.selectDbTableColumnsByName(table);
    }

    @Override
    public int addItemData(WfForm wfForm) {
        WfForm currentForm = wfFormMapper.selectWfFormById(wfForm.getId());
        wfForm.setTableName(currentForm.getTableName());
        return wfFormMapper.dynamicInsert(wfForm);
    }

    @Override
    public int deleteItemData(WfForm wfForm) {
        WfForm currentForm = wfFormMapper.selectWfFormById(wfForm.getId());
        wfForm.setTableName(currentForm.getTableName());
        return wfFormMapper.deleteItemData(wfForm);
    }

    @Override
    public int updateItemData(WfForm wfForm) {
        WfForm currentForm = wfFormMapper.selectWfFormById(wfForm.getId());
        wfForm.setTableName(currentForm.getTableName());
        return wfFormMapper.updateItemData(wfForm);
    }

    @Override
    public List<ResourceVO> dataList() {
        List<ResourceVO> result = new ArrayList<>();
        RelativeData rel1 = new RelativeData("sql=========Table");
        RelativeData rel2 = new RelativeData("excel");
        RelativeData rel3 = new RelativeData("sqlTable");
        RelativeData rel4 = new RelativeData("sqlTable");
        ResourceVO r1 = new ResourceVO("2", "数据集", 1L, null,null);
        ResourceVO r2 = new ResourceVO("eff75208b8b547ea84bed4a54051192a", "工作台", 5L, null,null);
        ResourceVO r3 = new ResourceVO("089a61206f9a4fefb7a92a8444af9137", "sql查询", 5L, null,null);
        ResourceVO r4 = new ResourceVO("be6beb23790443c1b854a871b5ad6aa5", "DataMart查询", 5L, null,null);
        ResourceVO r5 = new ResourceVO("a96125427e854a3ebe7514c2b80d9e6c", "语言", 4L, null,"lang");
        ResourceVO r6 = new ResourceVO("a5a9c13e52e940e6bd1c7254ed73b998", "ddd", 5L, null,null);
        ResourceVO r7 = new ResourceVO("726465814a47490396e16bda49f9eb1b", "A", 5L, null,null);
        ResourceVO r8 = new ResourceVO("6cad66f2ee3b4414a51e5eea6b99a1ad", "eeeee", 5L, null,null);
        ResourceVO r9 = new ResourceVO("ee3d8d1a51f74e2c9ce8118f923e3079", "eeeee-复制", 4L, null,"coffee");
        r5.setRelativeData(rel1);
        r7.setRelativeData(rel2);
        r8.setRelativeData(rel3);
        r9.setRelativeData(rel4);
        r1.getChildren().add(r2);
        r2.getChildren().add(r3);
        r2.getChildren().add(r4);
        r4.getChildren().add(r5);
        r6.getChildren().add(r7);
        r6.getChildren().add(r8);
        r6.getChildren().add(r9);
        r2.getChildren().add(r6);
        result.add(r1);
        return result;
    }

    @Override
    public UserDBVO userDataList(String table) {

        LoginUser loginUser = SecurityUtils.getLoginUser();
        UserDBVO result=new UserDBVO();
        result.setConstants(new UserInfoVO(loginUser.getUserid(),loginUser.getUsername(),"",""));
        List<DataSetVO> dataSetList=new ArrayList<>();
        DataSetVO dateSet1=new DataSetVO();
        dateSet1.setDataSetId(table);
        List<Map> valueList1=this.wfFormMapper.selectTableDataList(table);

        dateSet1.setValueList(valueList1);

        dataSetList.add(dateSet1);

        result.setDataSetList(dataSetList);
        return result;
    }
}
