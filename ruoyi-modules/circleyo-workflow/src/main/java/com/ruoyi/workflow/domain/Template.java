package com.ruoyi.workflow.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.core.annotation.Excel;
import com.ruoyi.common.core.web.domain.BaseEntity;

/**
 * 模板对象 template
 *
 * @author ruoyi
 * @date 2022-07-03
 */
public class Template extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** $column.columnComment */
    private Integer id;

    /** 名称 */
    @Excel(name = "名称")
    private String title;

    /** ⼯作薄 */
    @Excel(name = "⼯作薄")
    private String sheet;

    /** 模板数据 */
    @Excel(name = "模板数据")
    private String data;

    /** 背景图 */
    @Excel(name = "背景图")
    private String image;

    /** 链接 */
    @Excel(name = "链接")
    private String link;

    /** 备注 */
    @Excel(name = "备注")
    private String description;

    /** 设计者id */
    @Excel(name = "设计者id")
    private Long uid;

    /** 设计者 */
    @Excel(name = "设计者")
    private String author;

    /** 状态 */
    @Excel(name = "状态")
    private Integer status;

    /** 创建时间 */
    @Excel(name = "创建时间")
    private Long addtime;

    /** 创建时间 */
    @Excel(name = "创建时间")
    private Long updatetime;
    /** 是否已删除 */
    @Excel(name = "是否已删除")
    private Integer isdel;

    public void setId(Integer id)
    {
        this.id = id;
    }

    public Integer getId()
    {
        return id;
    }
    public void setTitle(String title)
    {
        this.title = title;
    }

    public String getTitle()
    {
        return title;
    }
    public void setSheet(String sheet)
    {
        this.sheet = sheet;
    }

    public String getSheet()
    {
        return sheet;
    }
    public void setData(String data)
    {
        this.data = data;
    }

    public String getData()
    {
        return data;
    }
    public void setImage(String image)
    {
        this.image = image;
    }

    public String getImage()
    {
        return image;
    }
    public void setLink(String link)
    {
        this.link = link;
    }

    public String getLink()
    {
        return link;
    }
    public void setDescription(String description)
    {
        this.description = description;
    }

    public String getDescription()
    {
        return description;
    }
    public void setUid(Long uid)
    {
        this.uid = uid;
    }

    public Long getUid()
    {
        return uid;
    }
    public void setAuthor(String author)
    {
        this.author = author;
    }

    public String getAuthor()
    {
        return author;
    }
    public void setStatus(Integer status)
    {
        this.status = status;
    }

    public Integer getStatus()
    {
        return status;
    }
    public void setAddtime(Long addtime)
    {
        this.addtime = addtime;
    }

    public Long getAddtime()
    {
        return addtime;
    }
    public void setIsdel(Integer isdel)
    {
        this.isdel = isdel;
    }

    public Integer getIsdel()
    {
        return isdel;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("title", getTitle())
            .append("sheet", getSheet())
            .append("data", getData())
            .append("image", getImage())
            .append("link", getLink())
            .append("description", getDescription())
            .append("uid", getUid())
            .append("author", getAuthor())
            .append("status", getStatus())
            .append("addtime", getAddtime())
            .append("isdel", getIsdel())
            .toString();
    }

    public Long getUpdatetime() {
        return updatetime;
    }

    public void setUpdatetime(Long updatetime) {
        this.updatetime = updatetime;
    }
}
