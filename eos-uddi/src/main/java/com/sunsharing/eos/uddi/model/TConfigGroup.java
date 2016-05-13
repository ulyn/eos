package com.sunsharing.eos.uddi.model;

import javax.persistence.*;

/**
 * Created by criss on 16/5/5.
 */
@Entity
@Table(name = "T_CONFIG_GROUP")
public class TConfigGroup {
    private int groupId;
    private Integer appId;
    private String groupName;
    private String isCommon;
    private Integer childAppId;
    private String _delete ="0";

    @Id
    @Column(name = "GROUP_ID")
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    @Basic
    @Column(name = "APP_ID")
    public Integer getAppId() {
        return appId;
    }

    public void setAppId(Integer appId) {
        this.appId = appId;
    }

    @Basic
    @Column(name = "GROUP_NAME")
    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    @Basic
    @Column(name = "IS_COMMON")
    public String getIsCommon() {
        return isCommon;
    }

    public void setIsCommon(String isCommon) {
        this.isCommon = isCommon;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TConfigGroup that = (TConfigGroup) o;

        if (groupId != that.groupId) return false;
        if (appId != null ? !appId.equals(that.appId) : that.appId != null) return false;
        if (groupName != null ? !groupName.equals(that.groupName) : that.groupName != null) return false;
        if (isCommon != null ? !isCommon.equals(that.isCommon) : that.isCommon != null) return false;

        return true;
    }

    @Basic
    @Column(name = "_DEL")
    public String get_delete() {
        return _delete;
    }

    public void set_delete(String _delete) {
        this._delete = _delete;
    }

    @Override
    public int hashCode() {
        int result = groupId;
        result = 31 * result + (appId != null ? appId.hashCode() : 0);
        result = 31 * result + (groupName != null ? groupName.hashCode() : 0);
        result = 31 * result + (isCommon != null ? isCommon.hashCode() : 0);
        return result;
    }

    @Basic
    @Column(name = "CHILD_APP_ID")
    public Integer getChildAppId() {
        return childAppId;
    }

    public void setChildAppId(Integer childAppId) {
        this.childAppId = childAppId;
    }
}
