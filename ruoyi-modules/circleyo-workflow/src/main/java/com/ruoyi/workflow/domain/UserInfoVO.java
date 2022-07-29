package com.ruoyi.workflow.domain;

public class UserInfoVO {
    private Long userId;
    private String userName;
    private String roleName;
    private String orgName;

    public UserInfoVO(Long userId,String userName, String roleName,String orgName) {
        this.userId = userId;
        this.userName = userName;
        this.roleName = roleName;
        this.orgName = orgName;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }
}
