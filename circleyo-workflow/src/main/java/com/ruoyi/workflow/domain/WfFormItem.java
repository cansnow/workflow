package com.ruoyi.workflow.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
//import com.ruoyi.common.core.annotation.Excel;
//import com.ruoyi.common.core.web.domain.BaseEntity;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 【请填写功能名称】对象 wf_form_item
 *
 * @author ruoyi
 * @date 2022-06-15
 */
public class WfFormItem extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** $column.columnComment */
    private Long id;

    /** $column.columnComment */
    @Excel(name = "${comment}", readConverterExp = "$column.readConverterExp()")
    private Long formId;

    /** $column.columnComment */
    @Excel(name = "${comment}", readConverterExp = "$column.readConverterExp()")
    private String fieldName;

    /** $column.columnComment */
    @Excel(name = "${comment}", readConverterExp = "$column.readConverterExp()")
    private String fieldDataType;

    /** $column.columnComment */
    @Excel(name = "${comment}", readConverterExp = "$column.readConverterExp()")
    private String fieldPageType;

    /** $column.columnComment */
    @Excel(name = "${comment}", readConverterExp = "$column.readConverterExp()")
    private String fieldIndex;

    /** $column.columnComment */
    @Excel(name = "${comment}", readConverterExp = "$column.readConverterExp()")
    private String titleCn;

    /** $column.columnComment */
    @Excel(name = "${comment}", readConverterExp = "$column.readConverterExp()")
    private String titleEn;

    /** $column.columnComment */
    @Excel(name = "${comment}", readConverterExp = "$column.readConverterExp()")
    private String isCondition;

    /** $column.columnComment */
    @Excel(name = "${comment}", readConverterExp = "$column.readConverterExp()")
    private String isShow;

    /** $column.columnComment */
    @Excel(name = "${comment}", readConverterExp = "$column.readConverterExp()")
    private String dictionary;

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
    public void setFormId(Long formId)
    {
        this.formId = formId;
    }

    public Long getFormId()
    {
        return formId;
    }
    public void setFieldName(String fieldName)
    {
        this.fieldName = fieldName;
    }

    public String getFieldName()
    {
        return fieldName;
    }
    public void setFieldDataType(String fieldDataType)
    {
        this.fieldDataType = fieldDataType;
    }

    public String getFieldDataType()
    {
        return fieldDataType;
    }
    public void setFieldPageType(String fieldPageType)
    {
        this.fieldPageType = fieldPageType;
    }

    public String getFieldPageType()
    {
        return fieldPageType;
    }
    public void setFieldIndex(String fieldIndex)
    {
        this.fieldIndex = fieldIndex;
    }

    public String getFieldIndex()
    {
        return fieldIndex;
    }
    public void setTitleCn(String titleCn)
    {
        this.titleCn = titleCn;
    }

    public String getTitleCn()
    {
        return titleCn;
    }
    public void setTitleEn(String titleEn)
    {
        this.titleEn = titleEn;
    }

    public String getTitleEn()
    {
        return titleEn;
    }
    public void setIsCondition(String isCondition)
    {
        this.isCondition = isCondition;
    }

    public String getIsCondition()
    {
        return isCondition;
    }
    public void setIsShow(String isShow)
    {
        this.isShow = isShow;
    }

    public String getIsShow()
    {
        return isShow;
    }
    public void setDictionary(String dictionary)
    {
        this.dictionary = dictionary;
    }

    public String getDictionary()
    {
        return dictionary;
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

    private Object value;

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("formId", getFormId())
            .append("fieldName", getFieldName())
            .append("fieldDataType", getFieldDataType())
            .append("fieldPageType", getFieldPageType())
            .append("fieldIndex", getFieldIndex())
            .append("titleCn", getTitleCn())
            .append("titleEn", getTitleEn())
            .append("isCondition", getIsCondition())
            .append("isShow", getIsShow())
            .append("dictionary", getDictionary())
            .append("createdBy", getCreatedBy())
            .append("createdDate", getCreatedDate())
            .append("lastUpdatedBy", getLastUpdatedBy())
            .append("lastUpdatedDate", getLastUpdatedDate())
            .toString();
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }
}
