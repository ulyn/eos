package com.sunsharing.eos.uddi.model;

import com.sunsharing.eos.common.utils.StringUtils;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

/**
 * Created by criss on 16/2/24.
 */
@Entity
@Table(name = "T_DB_CHECKLIST")
public class TDbChecklist {
    private String id;
    private TUser checkUser;
    private TDbChange change;
    private String checkContent;
    private String checkStatus;
    private String checkTime;

    public TDbChecklist(){
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

    @ManyToOne
    @JoinColumn(name="CHECK_USER")
    public TUser getCheckUser() {
        return checkUser;
    }

    public void setCheckUser(TUser checkUser) {
        this.checkUser = checkUser;
    }

    @ManyToOne
    @JoinColumn(name="CHANGE_ID")
    public TDbChange getChange() {
        return change;
    }

    public void setChange(TDbChange change) {
        this.change = change;
    }

    @Basic
    @Column(name = "CHECK_CONTENT")
    public String getCheckContent() {
        return checkContent;
    }

    public void setCheckContent(String checkContent) {
        this.checkContent = checkContent;
    }

    @Basic
    @Column(name = "CHECK_STATUS")
    public String getCheckStatus() {
        return checkStatus;
    }

    public void setCheckStatus(String checkStatus) {
        this.checkStatus = checkStatus;
    }

    @Column(name = "CHECK_TIME")
    public String getCheckTime() {
        return checkTime;
    }

    public void setCheckTime(String checkTime) {
        this.checkTime = checkTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TDbChecklist that = (TDbChecklist) o;

        if (id != that.id) return false;
        if (checkUser != null ? !checkUser.equals(that.checkUser) : that.checkUser != null) return false;
        if (checkContent != null ? !checkContent.equals(that.checkContent) : that.checkContent != null) return false;
        if (checkStatus != null ? !checkStatus.equals(that.checkStatus) : that.checkStatus != null) return false;
        if (change != null ? !change.equals(that.change) : that.change != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + (checkUser != null ? checkUser.hashCode() : 0);
        result = 31 * result + (checkContent != null ? checkContent.hashCode() : 0);
        result = 31 * result + (checkStatus != null ? checkStatus.hashCode() : 0);
        return result;
    }
}
