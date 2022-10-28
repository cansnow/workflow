package com.ruoyi.workflow.controller;

//import com.ruoyi.common.core.utils.poi.ExcelUtil;
//import com.ruoyi.common.core.web.controller.BaseController;
//import com.ruoyi.common.core.web.domain.AjaxResult;
//import com.ruoyi.common.core.web.page.TableDataInfo;
//import com.ruoyi.common.log.annotation.Log;
//import com.ruoyi.common.log.enums.BusinessType;
//import com.ruoyi.common.security.annotation.RequiresPermissions;
//import com.ruoyi.common.security.utils.SecurityUtils;
//import com.ruoyi.system.api.model.LoginUser;
import com.ruoyi.common.core.domain.model.LoginUser;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.workflow.domain.SysFileInfo;
import com.ruoyi.workflow.service.ISysFileInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import utils.FileUploadUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;

/**
 * 【请填写功能名称】Controller
 *
 * @author circleyo
 * @date 2022-07-27
 */
@RestController
@RequestMapping("/fileInfo")
public class SysFileInfoController extends BaseController {
    @Autowired
    private ISysFileInfoService sysFileInfoService;

    /**
     * 查询【请填写功能名称】列表
     */
//    @RequiresPermissions("system:info:list")
    @GetMapping("/list")
    public TableDataInfo list(SysFileInfo sysFileInfo) {
        startPage();
        List<SysFileInfo> list = sysFileInfoService.selectSysFileInfoList(sysFileInfo);
        return getDataTable(list);
    }

    /**
     * 导出【请填写功能名称】列表
     */
//    @RequiresPermissions("system:info:export")
    @Log(title = "【请填写功能名称】", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, SysFileInfo sysFileInfo) {
        List<SysFileInfo> list = sysFileInfoService.selectSysFileInfoList(sysFileInfo);
        ExcelUtil<SysFileInfo> util = new ExcelUtil<SysFileInfo>(SysFileInfo.class);
        util.exportExcel(response, list, "【请填写功能名称】数据");
    }

    /**
     * 获取【请填写功能名称】详细信息
     */
//    @RequiresPermissions("system:info:query")
    @GetMapping(value = "/{fileId}")
    public AjaxResult getInfo(@PathVariable("fileId") Long fileId) {
        return AjaxResult.success(sysFileInfoService.selectSysFileInfoByFileId(fileId));
    }

    /**
     * 新增【请填写功能名称】
     */
//    @RequiresPermissions("system:info:add")
    @Log(title = "【请填写功能名称】", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody SysFileInfo sysFileInfo) {
        return toAjax(sysFileInfoService.insertSysFileInfo(sysFileInfo));
    }

    /**
     * 修改【请填写功能名称】
     */
//    @RequiresPermissions("system:info:edit")
    @Log(title = "【请填写功能名称】", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody SysFileInfo sysFileInfo) {
        return toAjax(sysFileInfoService.updateSysFileInfo(sysFileInfo));
    }

    /**
     * 删除【请填写功能名称】
     */
//    @RequiresPermissions("system:info:remove")
    @Log(title = "【请填写功能名称】", businessType = BusinessType.DELETE)
    @DeleteMapping("/{fileIds}")
    public AjaxResult remove(@PathVariable Long[] fileIds) {
        return toAjax(sysFileInfoService.deleteSysFileInfoByFileIds(fileIds));
    }

    @PostMapping("/upload")
    public AjaxResult avatar(HttpServletRequest request, @RequestParam("uploadFile") MultipartFile file) {
        AjaxResult ajax = AjaxResult.success();
        if (!file.isEmpty()) {
//            String path = request.getSession().getServletContext().getRealPath("WEB-INF/fileInfo");
            String path = "/www/wwwroot/workflow/WEB-INF/fileInfo";
            System.out.println("path:"+path);
            logger.info("path:"+path);
            String name = null;
            try {
                name = FileUploadUtils.upload(path, file);
            } catch (IOException e) {
                return ajax;
            }
            File newFile=new File(path+name);

            LoginUser loginUser = SecurityUtils.getLoginUser();
            SysFileInfo sysFileInfo = new SysFileInfo();
            sysFileInfo.setFileName(newFile.getName());
            sysFileInfo.setFilePath(name);
            sysFileInfo.setCreatedBy(loginUser.getUsername());
            sysFileInfo.setCreatedDate(new Date());
            sysFileInfoService.insertSysFileInfo(sysFileInfo);

            ajax.put("fileInfo", sysFileInfo);
            return ajax;
        }
        return AjaxResult.error("上传文件异常，请联系管理员");
    }
}
