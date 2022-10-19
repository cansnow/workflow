package com.ruoyi.workflow.service.impl;

import java.util.List;

import com.ruoyi.workflow.domain.SysFileInfo;
import com.ruoyi.workflow.mapper.SysFileInfoMapper;
import com.ruoyi.workflow.service.ISysFileInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 【请填写功能名称】Service业务层处理
 *
 * @author ruoyi
 * @date 2022-07-27
 */
@Service
public class SysFileInfoServiceImpl implements ISysFileInfoService
{
    @Autowired
    private SysFileInfoMapper sysFileInfoMapper;

    /**
     * 查询【请填写功能名称】
     *
     * @param fileId 【请填写功能名称】主键
     * @return 【请填写功能名称】
     */
    @Override
    public SysFileInfo selectSysFileInfoByFileId(Long fileId)
    {
        return sysFileInfoMapper.selectSysFileInfoByFileId(fileId);
    }

    /**
     * 查询【请填写功能名称】列表
     *
     * @param sysFileInfo 【请填写功能名称】
     * @return 【请填写功能名称】
     */
    @Override
    public List<SysFileInfo> selectSysFileInfoList(SysFileInfo sysFileInfo)
    {
        return sysFileInfoMapper.selectSysFileInfoList(sysFileInfo);
    }

    /**
     * 新增【请填写功能名称】
     *
     * @param sysFileInfo 【请填写功能名称】
     * @return 结果
     */
    @Override
    public int insertSysFileInfo(SysFileInfo sysFileInfo)
    {
        return sysFileInfoMapper.insertSysFileInfo(sysFileInfo);
    }

    /**
     * 修改【请填写功能名称】
     *
     * @param sysFileInfo 【请填写功能名称】
     * @return 结果
     */
    @Override
    public int updateSysFileInfo(SysFileInfo sysFileInfo)
    {
        return sysFileInfoMapper.updateSysFileInfo(sysFileInfo);
    }

    /**
     * 批量删除【请填写功能名称】
     *
     * @param fileIds 需要删除的【请填写功能名称】主键
     * @return 结果
     */
    @Override
    public int deleteSysFileInfoByFileIds(Long[] fileIds)
    {
        return sysFileInfoMapper.deleteSysFileInfoByFileIds(fileIds);
    }

    /**
     * 删除【请填写功能名称】信息
     *
     * @param fileId 【请填写功能名称】主键
     * @return 结果
     */
    @Override
    public int deleteSysFileInfoByFileId(Long fileId)
    {
        return sysFileInfoMapper.deleteSysFileInfoByFileId(fileId);
    }
}
