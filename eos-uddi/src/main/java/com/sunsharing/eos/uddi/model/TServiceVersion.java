package com.sunsharing.eos.uddi.model;

import org.hibernate.annotations.Cascade;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by criss on 14-2-1.
 */
@Entity
@Table(name = "T_SERVICE_VERSION")
public class TServiceVersion {
    private int versionId;
    private TService service;
    private String appCode;
    private String status;
    private String serviceVersion;
    private String createTime;

    List<TMethod> methods = new ArrayList<TMethod>();

    @OneToMany(mappedBy="versionObj",cascade={CascadeType.ALL},fetch=FetchType.LAZY)
    @Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
    public List<TMethod> getMethods() {
        return methods;
    }

    public void setMethods(List<TMethod> methods) {
        this.methods = methods;
    }

    @Id
    @Column(name = "VERSION_ID")
    @GeneratedValue(strategy=GenerationType.AUTO)
    public int getVersionId() {
        return versionId;
    }

    public void setVersionId(int versionId) {
        this.versionId = versionId;
    }

    @ManyToOne
    @JoinColumn(name="SERVICE_ID")
    public TService getService() {
        return service;
    }

    public void setService(TService service) {
        this.service = service;
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
    @Column(name = "STATUS")
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Basic
    @Column(name = "CREATE_TIME")
    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    @Basic
    @Column(name = "VERSION")
    public String getServiceVersion() {
        return serviceVersion;
    }

    public void setServiceVersion(String serviceVersion) {
        this.serviceVersion = serviceVersion;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TServiceVersion that = (TServiceVersion) o;

        if (versionId != that.versionId) return false;
        if (appCode != null ? !appCode.equals(that.appCode) : that.appCode != null) return false;
        if (createTime != null ? !createTime.equals(that.createTime) : that.createTime != null) return false;
        //if (serviceId != null ? !serviceId.equals(that.serviceId) : that.serviceId != null) return false;
        if (status != null ? !status.equals(that.status) : that.status != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = versionId;
        //result = 31 * result + (serviceId != null ? serviceId.hashCode() : 0);
        result = 31 * result + (appCode != null ? appCode.hashCode() : 0);
        result = 31 * result + (status != null ? status.hashCode() : 0);
        result = 31 * result + (createTime != null ? createTime.hashCode() : 0);
        result = 31 * result + (serviceVersion != null ? serviceVersion.hashCode() : 0);
        return result;
    }
}
