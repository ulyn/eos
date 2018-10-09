package com.sunsharing.eos.uddi.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;

/**
 * Created by criss on 16/2/24.
 */
@Entity
@Table(name = "T_DB_CHANGE")
public class TDbChange {
    private int id;
    private TApp appId;
    private String version;
    //private Integer user;
    private TUser user;
    private String changeLog;
    private String pubishTime;
    private String db;
    private String script;
    private String module;
    private String dbType;
    private String hasSend;

    private List<TDbChecklist> dbChecklistList =
            new ArrayList<TDbChecklist>();

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy=GenerationType.AUTO)
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @ManyToOne
    @JoinColumn(name = "APP_ID")
    public TApp getAppId() {
        return appId;
    }

    public void setAppId(TApp appId) {
        this.appId = appId;
    }

    @Basic
    @Column(name = "VERSION")
    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    @ManyToOne
    @JoinColumn(name = "USER")
    public TUser getUser() {
        return user;
    }

    public void setUser(TUser user) {
        this.user = user;
    }

    @Basic
    @Column(name = "CHANGE_LOG")
    public String getChangeLog() {
        return changeLog;
    }

    public void setChangeLog(String changeLog) {
        this.changeLog = changeLog;
    }

    @Basic
    @Column(name = "PUBISH_TIME")
    public String getPubishTime() {
        return pubishTime;
    }

    public void setPubishTime(String pubishTime) {
        this.pubishTime = pubishTime;
    }

    @Basic
    @Column(name = "DB")
    public String getDb() {
        return db;
    }

    public void setDb(String db) {
        this.db = db;
    }

    @Basic
    @Column(name = "SCRIPT")
    public String getScript() {
        return script;
    }

    public void setScript(String script) {
        this.script = script;
    }

    @Basic
    @Column(name = "MODULE")
    public String getModule() {
        return module;
    }

    public void setModule(String module) {
        this.module = module;
    }

    @Basic
    @Column(name = "DB_TYPE")
    public String getDbType() {
        return dbType;
    }

    public void setDbType(String dbType) {
        this.dbType = dbType;
    }
    @Basic
    @Column(name = "HAS_SEND")
    public String getHasSend() {
        return hasSend;
    }

    public void setHasSend(String hasSend) {
        this.hasSend = hasSend;
    }

    @OneToMany(mappedBy="change")
    @OrderBy(value="checkTime desc")
    public List<TDbChecklist> getDbChecklistList() {
        return dbChecklistList;
    }

    public void setDbChecklistList(List<TDbChecklist> dbChecklistList) {
        this.dbChecklistList = dbChecklistList;
    }


    public boolean geCheckStatus()
    {

        //小组长审批
        boolean xzz = false;
        //数据组审批
        boolean sjj = false;
        for(TDbChecklist check:dbChecklistList)
        {
            //
            {
                //审批通过
                if("2".equals(check.getCheckUser().getRole()) ||
                        "3".equals(check.getCheckUser().getRole()))
                {
                    if("1".equals(check.getCheckStatus()))
                    {
                        xzz = true;
                    }else
                    {
                        xzz = false;
                    }
                    break;
                }

            }
        }
        for(TDbChecklist check:dbChecklistList)
        {
            //
            {
                //审批通过
                if("4".equals(check.getCheckUser().getRole()))
                {
                    if("1".equals(check.getCheckStatus()))
                    {
                        sjj = true;
                    }else
                    {
                        sjj = false;
                    }
                    break;
                }

            }
        }
        if(xzz && sjj)
        {
            return true;
        }else
        {
            return false;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TDbChange tDbChange = (TDbChange) o;

        if (id != tDbChange.id) return false;
        if (appId != null ? !appId.equals(tDbChange.appId) : tDbChange.appId != null) return false;
        if (version != null ? !version.equals(tDbChange.version) : tDbChange.version != null) return false;
        if (user != null ? !user.equals(tDbChange.user) : tDbChange.user != null) return false;
        if (changeLog != null ? !changeLog.equals(tDbChange.changeLog) : tDbChange.changeLog != null) return false;
        if (pubishTime != null ? !pubishTime.equals(tDbChange.pubishTime) : tDbChange.pubishTime != null) return false;
        if (db != null ? !db.equals(tDbChange.db) : tDbChange.db != null) return false;
        if (script != null ? !script.equals(tDbChange.script) : tDbChange.script != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (appId != null ? appId.hashCode() : 0);
        result = 31 * result + (version != null ? version.hashCode() : 0);
        result = 31 * result + (user != null ? user.hashCode() : 0);
        result = 31 * result + (changeLog != null ? changeLog.hashCode() : 0);
        result = 31 * result + (pubishTime != null ? pubishTime.hashCode() : 0);
        result = 31 * result + (db != null ? db.hashCode() : 0);
        result = 31 * result + (script != null ? script.hashCode() : 0);
        return result;
    }
}
