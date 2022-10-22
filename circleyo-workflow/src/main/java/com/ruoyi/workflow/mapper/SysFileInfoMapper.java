package com.ruoyi.workflow.mapper;

import com.ruoyi.workflow.domain.SysFileInfo;

import java.util.List;

/**
 * 【请填写功能名称】Mapper接口
 *
 * @author ruoyi
 * @date 2022-07-27
 */
public interface SysFileInfoMapper
{
    /**
     * 查询【请填写功能名称】
     *
     * @param fileId 【请填写功能名称】主键
     * @return 【请填写功能名称】
     */
    public SysFileInfo selectSysFileInfoByFileId(Long fileId);

    /**
     * 查询【请填写功能名称】列表
     *
     * @param sysFileInfo 【请填写功能名称】
     * @return 【请填写功能名称】集合
     */
    public List<SysFileInfo> selectSysFileInfoList(SysFileInfo sysFileInfo);

    /**
     * 新增【请填写功能名称】
     *
     * @param sysFileInfo 【请填写功能名称】
     * @return 结果
     */
    public int insertSysFileInfo(SysFileInfo sysFileInfo);

    /**
     * 修改【请填写功能名称】
     *
     * @param sysFileInfo 【请填写功能名称】
     * @return 结果
     */
    public int updateSysFileInfo(SysFileInfo sysFileInfo);

    /**
     * 删除【请填写功能名称】
     *
     * @param fileId 【请填写功能名称】主键
     * @return 结果
     */
    public int deleteSysFileInfoByFileId(Long fileId);

    /**
     * 批量删除【请填写功能名称】
     *
     * @param fileIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteSysFileInfoByFileIds(Long[] fileIds);
}
