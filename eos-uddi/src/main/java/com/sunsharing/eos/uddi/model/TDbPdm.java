package com.sunsharing.eos.uddi.model;

import com.sunsharing.eos.common.utils.StringUtils;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

/**
 * Created by criss on 16/2/24.
 */
@Entity
@Table(name = "T_DB_PDM")
public class TDbPdm {
    private String id;
    private TApp appId;
    private String pdm;
    private String lock;
    private TUser lockUserId;

    public TDbPdm(){
        this.id = StringUtils.genUUID();
    }

    @Id
    @Column(name = "ID")
    @GenericGenerator(name = "system-uuid", strategy = "uuid")
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Basic
    @Column(name = "PDM")
    public String getPdm() {
        return pdm;
    }

    public void setPdm(String pdm) {
        this.pdm = pdm;
    }

    @Basic
    @Column(name = "IS_LOCK")
    public String getLock() {
        return lock;
    }

    public void setLock(String lock) {
        this.lock = lock;
    }

    @ManyToOne
    @JoinColumn(name = "LOCK_USER_ID")
    public TUser getLockUserId() {
        return lockUserId;
    }

    public void setLockUserId(TUser lockUserId) {
        this.lockUserId = lockUserId;
    }

    @ManyToOne
    @JoinColumn(name = "APP_ID")
    public TApp getAppId() {
        return appId;
    }

    public void setAppId(TApp appId) {
        this.appId = appId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TDbPdm tDbPdm = (TDbPdm) o;

        if (id != tDbPdm.id) return false;
        if (pdm != null ? !pdm.equals(tDbPdm.pdm) : tDbPdm.pdm != null) return false;
        if (lock != null ? !lock.equals(tDbPdm.lock) : tDbPdm.lock != null) return false;
        if (lockUserId != null ? !lockUserId.equals(tDbPdm.lockUserId) : tDbPdm.lockUserId != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + (pdm != null ? pdm.hashCode() : 0);
        result = 31 * result + (lock != null ? lock.hashCode() : 0);
        result = 31 * result + (lockUserId != null ? lockUserId.hashCode() : 0);
        return result;
    }
}
