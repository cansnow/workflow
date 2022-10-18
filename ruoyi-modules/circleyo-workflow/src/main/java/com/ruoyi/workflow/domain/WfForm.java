package com.ruoyi.workflow.domain;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.core.annotation.Excel;
import com.ruoyi.common.core.web.domain.BaseEntity;

/**
 * 【请填写功能名称】对象 wf_form
 *
 * @author circleyo
 * @date 2022-06-18
 */
public class WfForm extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /**  */
    private Long id;

    /** 模板名称 */
    @Excel(name = "模板名称")
    private String name;

    /** 表名称 */
    @Excel(name = "表名称")
    private String tableName;

    /** 表单内容 */
    @Excel(name = "表单内容")
    private String formContent;

    /** 创建人 */
    @Excel(name = "创建人")
    private String createdBy;

    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "创建时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date createdDate;

    /** 更新人 */
    @Excel(name = "更新人")
    private String lastUpdatedBy;

    /** 更新时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "更新时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date lastUpdatedDate;

    /** $column.columnComment */
    @Excel(name = "${comment}", readConverterExp = "$column.readConverterExp()")
    private String sheetName;

    /** $column.columnComment */
    @Excel(name = "${comment}", readConverterExp = "$column.readConverterExp()")
    private String backgroud;

    /** $column.columnComment */
    @Excel(name = "${comment}", readConverterExp = "$column.readConverterExp()")
    private String pageRouter;

    /** $column.columnComment */
    @Excel(name = "${comment}", readConverterExp = "$column.readConverterExp()")
    private String status;

    /** $column.columnComment */
    @Excel(name = "${comment}", readConverterExp = "$column.readConverterExp()")
    private String deletedFlag;

    public void setId(Long id)
    {
        this.id = id;
    }

    public Long getId()
    {
        return id;
    }
    public void setName(String name)
    {
        this.name = name;
    }

    public String getName()
    {
        return name;
    }
    public void setTableName(String tableName)
    {
        this.tableName = tableName;
    }

    public String getTableName()
    {
        return tableName;
    }
    public void setFormContent(String formContent)
    {
        this.formContent = formContent;
    }

    public String getFormContent()
    {
        return formContent;
    }
    public void setCreatedBy(String createdBy)
    {
        this.createdBy = createdBy;
    }

    public String getCreatedBy()
    {
        return createdBy;
    }
    public void setCreatedDate(Date createdDate)
    {
        this.createdDate = createdDate;
    }

    public Date getCreatedDate()
    {
        return createdDate;
    }
    public void setLastUpdatedBy(String lastUpdatedBy)
    {
        this.lastUpdatedBy = lastUpdatedBy;
    }

    public String getLastUpdatedBy()
    {
        return lastUpdatedBy;
    }
    public void setLastUpdatedDate(Date lastUpdatedDate)
    {
        this.lastUpdatedDate = lastUpdatedDate;
    }

    public Date getLastUpdatedDate()
    {
        return lastUpdatedDate;
    }
    public void setSheetName(String sheetName)
    {
        this.sheetName = sheetName;
    }

    public String getSheetName()
    {
        return sheetName;
    }
    public void setBackgroud(String backgroud)
    {
        this.backgroud = backgroud;
    }

    public String getBackgroud()
    {
        return backgroud;
    }
    public void setPageRouter(String pageRouter)
    {
        this.pageRouter = pageRouter;
    }

    public String getPageRouter()
    {
        return pageRouter;
    }
    public void setStatus(String status)
    {
        this.status = status;
    }

    public String getStatus()
    {
        return status;
    }
    public void setDeletedFlag(String deletedFlag)
    {
        this.deletedFlag = deletedFlag;
    }

    public String getDeletedFlag()
    {
        return deletedFlag;
    }
    private List<WfFormItem> itemList;

    private Long dataId;

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("name", getName())
            .append("tableName", getTableName())
            .append("formContent", getFormContent())
            .append("createdBy", getCreatedBy())
            .append("createdDate", getCreatedDate())
            .append("lastUpdatedBy", getLastUpdatedBy())
            .append("lastUpdatedDate", getLastUpdatedDate())
            .append("sheetName", getSheetName())
            .append("backgroud", getBackgroud())
            .append("pageRouter", getPageRouter())
            .append("remark", getRemark())
            .append("status", getStatus())
            .append("deletedFlag", getDeletedFlag())
            .toString();
    }

    public List<WfFormItem> getItemList() {
        return itemList;
    }

    public void setItemList(List<WfFormItem> itemList) {
        this.itemList = itemList;
    }

    public Long getDataId() {
        return dataId;
    }

    public void setDataId(Long dataId) {
        this.dataId = dataId;
    }
}
