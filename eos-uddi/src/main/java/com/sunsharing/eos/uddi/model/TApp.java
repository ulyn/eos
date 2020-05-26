package com.sunsharing.eos.uddi.model;

import com.sunsharing.eos.common.utils.StringUtils;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by criss on 14-1-30.
 */
@Entity
@Table(name = "T_APP")
public class TApp implements Comparable {
    private String appId;
    private String appName;
    private String appCode;
    private String dbs;
    private String creatTime;
    private String yw;

    public TApp(){
        this.appId = StringUtils.genUUID();
    }

    List<TModule> modules = new ArrayList<TModule>();

    @OneToMany(mappedBy = "app", cascade = {CascadeType.ALL}, fetch = FetchType.LAZY)
    @Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
    public List<TModule> getModules() {
        return modules;
    }

    public void setModules(List<TModule> modules) {
        this.modules = modules;
    }

    @Basic
    @Column(name = "CREATE_TIME")
    public String getCreatTime() {
        return creatTime;
    }

    public void setCreatTime(String creatTime) {
        this.creatTime = creatTime;
    }

    @Id
    @Column(name = "APP_ID")
    @GenericGenerator(name = "system-uuid", strategy = "uuid")
    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    @Basic
    @Column(name = "APP_NAME")
    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    @Basic
    @Column(name = "APP_CODE")
    public String getAppCode() {
        return appCode;
    }

    public void setAppCode(String appCode) {
        this.appCode = appCode;
    }

    @Basic
    @Column(name = "DBS")
    public String getDbs() {
        return dbs;
    }

    public void setDbs(String dbs) {
        this.dbs = dbs;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TApp tApp = (TApp) o;

        if (appId != tApp.appId) return false;
        if (appCode != null ? !appCode.equals(tApp.appCode) : tApp.appCode != null) return false;
        if (appName != null ? !appName.equals(tApp.appName) : tApp.appName != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = appId.hashCode();
        result = 31 * result + (appName != null ? appName.hashCode() : 0);
        result = 31 * result + (appCode != null ? appCode.hashCode() : 0);
        return result;
    }

    @Override
    public int compareTo(Object o) {
        TApp app = (TApp) o;
        if (this.creatTime.compareTo(((TApp) o).getCreatTime()) > 0) {
            return 1;
        } else if (this.creatTime.compareTo(((TApp) o).getCreatTime()) < 0) {
            return -1;
        } else {
            return 0;
        }
    }

    @Basic
    @Column(name = "YW")
    public String getYw() {
        return yw;
    }

    public void setYw(String yw) {
        this.yw = yw;
    }
}
