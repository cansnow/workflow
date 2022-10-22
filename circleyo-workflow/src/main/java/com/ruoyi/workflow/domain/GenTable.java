package com.ruoyi.workflow.domain;


import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.util.List;

/**
 * 业务表 gen_table
 *
 * @author ruoyi
 */
public class GenTable
{
    private static final long serialVersionUID = 1L;

    /** 表名称 */
    @NotBlank(message = "表名称不能为空")
    private String tableName;

    /** 表描述 */
    @NotBlank(message = "表描述不能为空")
    private String tableComment;


    public String getTableName()
    {
        return tableName;
    }

    public void setTableName(String tableName)
    {
        this.tableName = tableName;
    }

    public String getTableComment()
    {
        return tableComment;
    }

    public void setTableComment(String tableComment)
    {
        this.tableComment = tableComment;
    }

}
