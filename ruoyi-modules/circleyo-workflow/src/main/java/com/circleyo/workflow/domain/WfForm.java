package com.circleyo.workflow.domain;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.core.annotation.Excel;
import com.ruoyi.common.core.web.domain.BaseEntity;

/**
 * 工作流表单对象 wf_form
 *
 * @author circleyo
 * @date 2022-06-12
 */
public class WfForm extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** $column.columnComment */
    private Long id;

    /** $column.columnComment */
    @Excel(name = "${comment}", readConverterExp = "$column.readConverterExp()")
    private String name;

    /** $column.columnComment */
    @Excel(name = "${comment}", readConverterExp = "$column.readConverterExp()")
    private String tableName;

    /** $column.columnComment */
    @Excel(name = "${comment}", readConverterExp = "$column.readConverterExp()")
    private String formContent;

    /** $column.columnComment */
    @Excel(name = "${comment}", readConverterExp = "$column.readConverterExp()")
    private String createdBy;

    /** $column.columnComment */
    @Excel(name = "${comment}", readConverterExp = "$column.readConverterExp()")
    private Date createdDate;

    /** $column.columnComment */
    @Excel(name = "${comment}", readConverterExp = "$column.readConverterExp()")
    private String lastUpdatedBy;

    /** $column.columnComment */
    @Excel(name = "${comment}", readConverterExp = "$column.readConverterExp()")
    private Date lastUpdatedDate;

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
