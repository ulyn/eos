package com.sunsharing.eos.uddi.model;

import com.sunsharing.eos.common.utils.StringUtils;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

/**
 * Created by criss on 16/5/5.
 */
@Entity
@Table(name = "T_CONFIG_RUN")
public class TConfigRun {
    private String runId;
    private String bswz;
    private String runKey;
    private Integer childAppId;
    private Integer appId;

    public TConfigRun(){
        this.runId = StringUtils.genUUID();
    }

    @Id
    @Column(name = "RUN_ID")
    @GenericGenerator(name = "system-uuid", strategy = "uuid")
    public String getRunId() {
        return runId;
    }

    public void setRunId(String runId) {
        this.runId = runId;
    }

    @Basic
    @Column(name = "BSWZ")
    public String getBswz() {
        return bswz;
    }

    public void setBswz(String bswz) {
        this.bswz = bswz;
    }

    @Basic
    @Column(name = "RUN_KEY")
    public String getRunKey() {
        return runKey;
    }

    public void setRunKey(String runKey) {
        this.runKey = runKey;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TConfigRun that = (TConfigRun) o;

        if (runId != that.runId) return false;
        if (bswz != null ? !bswz.equals(that.bswz) : that.bswz != null) return false;
        if (runKey != null ? !runKey.equals(that.runKey) : that.runKey != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = runId.hashCode();
        result = 31 * result + (bswz != null ? bswz.hashCode() : 0);
        result = 31 * result + (runKey != null ? runKey.hashCode() : 0);
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

    @Basic
    @Column(name = "APP_ID")
    public Integer getAppId() {
        return appId;
    }

    public void setAppId(Integer appId) {
        this.appId = appId;
    }
}
