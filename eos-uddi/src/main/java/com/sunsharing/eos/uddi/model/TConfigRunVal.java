package com.sunsharing.eos.uddi.model;

import javax.persistence.*;

/**
 * Created by criss on 16/5/5.
 */
@Entity
@Table(name = "T_CONFIG_RUN_VAL")
public class TConfigRunVal {
    private int runValId;
    private Integer runId;
    private Integer configId;
    private String val;

    @Id
    @Column(name = "RUN_VAL_ID")
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    public int getRunValId() {
        return runValId;
    }

    public void setRunValId(int runValId) {
        this.runValId = runValId;
    }

    @Basic
    @Column(name = "RUN_ID")
    public Integer getRunId() {
        return runId;
    }

    public void setRunId(Integer runId) {
        this.runId = runId;
    }

    @Basic
    @Column(name = "CONFIG_ID")
    public Integer getConfigId() {
        return configId;
    }

    public void setConfigId(Integer configId) {
        this.configId = configId;
    }

    @Basic
    @Column(name = "VAL")
    public String getVal() {
        return val;
    }

    public void setVal(String val) {
        this.val = val;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TConfigRunVal that = (TConfigRunVal) o;

        if (runValId != that.runValId) return false;
        if (runId != null ? !runId.equals(that.runId) : that.runId != null) return false;
        if (configId != null ? !configId.equals(that.configId) : that.configId != null) return false;
        if (val != null ? !val.equals(that.val) : that.val != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = runValId;
        result = 31 * result + (runId != null ? runId.hashCode() : 0);
        result = 31 * result + (configId != null ? configId.hashCode() : 0);
        result = 31 * result + (val != null ? val.hashCode() : 0);
        return result;
    }
}
