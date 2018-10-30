package com.sunsharing.eos.uddi.model;

import com.sunsharing.eos.common.utils.StringUtils;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

/**
 * Created by criss on 16/5/6.
 */
@Entity
@Table(name = "T_CONFIG_CHILD_APP")
public class TConfigChildApp {
    private String childAppId;
    private String childAppName;
    private String appId;

    public TConfigChildApp(){
        this.childAppId = StringUtils.genUUID();
    }

    @Id
    @Column(name = "CHILD_APP_ID")
    @GenericGenerator(name = "system-uuid", strategy = "uuid")
    public String getChildAppId() {
        return childAppId;
    }

    public void setChildAppId(String childAppId) {
        this.childAppId = childAppId;
    }

    @Basic
    @Column(name = "CHILD_APP_NAME")
    public String getChildAppName() {
        return childAppName;
    }

    public void setChildAppName(String childAppName) {
        this.childAppName = childAppName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TConfigChildApp that = (TConfigChildApp) o;

        if (childAppId != that.childAppId) return false;
        if (childAppName != null ? !childAppName.equals(that.childAppName) : that.childAppName != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = childAppId.hashCode();
        result = 31 * result + (childAppName != null ? childAppName.hashCode() : 0);
        return result;
    }

    @Basic
    @Column(name = "APP_ID")
    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }
}
