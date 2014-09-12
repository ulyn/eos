package com.sunsharing.eos.uddi.model;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by criss on 14-2-1.
 */
@Entity
@Table(name = "T_SERVICE", schema = "", catalog = "eos")
public class TService {
    private int serviceId;
    private Integer appId;
    private String module;
    private TUser user;
    private String serviceCode;
    private String serviceName;
    private String appCode;
    private String createTime;
    private String test = "0";

    private List<TServiceVersion> versions = new ArrayList<TServiceVersion>();

    @OneToMany(mappedBy="service",cascade={CascadeType.ALL},fetch=FetchType.LAZY)
    @OrderBy(value = "version desc ")
    public List<TServiceVersion> getVersions() {
        return versions;
    }

    public void setVersions(List<TServiceVersion> versions) {
        this.versions = versions;
    }
    @Basic
    @Column(name = "SERVICE_NAME")
    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    @Id
    @Column(name = "SERVICE_ID")
    @GeneratedValue(strategy=GenerationType.AUTO)
    public int getServiceId() {
        return serviceId;
    }

    public void setServiceId(int serviceId) {
        this.serviceId = serviceId;
    }

    @Basic
    @Column(name = "TEST")
    public String getTest() {
        return test;
    }

    public void setTest(String test) {
        this.test = test;
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
    @Column(name = "MODULE")
    public String getModule() {
        return module;
    }

    public void setModule(String module) {
        this.module = module;
    }
    @ManyToOne
    @JoinColumn(name = "USER_ID")
    public TUser getUser() {
        return user;
    }

    public void setUser(TUser user) {
        this.user = user;
    }
    @Basic
    @Column(name = "SERVICE_CODE")
    public String getServiceCode() {
        return serviceCode;
    }

    public void setServiceCode(String serviceCode) {
        this.serviceCode = serviceCode;
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
    @Column(name = "CREATE_TIME")
    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TService tService = (TService) o;

        if (serviceId != tService.serviceId) return false;
        if (appCode != null ? !appCode.equals(tService.appCode) : tService.appCode != null) return false;
        if (appId != null ? !appId.equals(tService.appId) : tService.appId != null) return false;
        if (createTime != null ? !createTime.equals(tService.createTime) : tService.createTime != null) return false;
        if (module != null ? !module.equals(tService.module) : tService.module != null) return false;
        if (serviceCode != null ? !serviceCode.equals(tService.serviceCode) : tService.serviceCode != null)
            return false;
       // if (user != null ? !user.getu.equals(tService.userId) : tService.userId != null) return false;
        //if (version != null ? !version.equals(tService.version) : tService.version != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = serviceId;
        result = 31 * result + (appId != null ? appId.hashCode() : 0);
        result = 31 * result + (module != null ? module.hashCode() : 0);
        //result = 31 * result + (userId != null ? userId.hashCode() : 0);
        result = 31 * result + (serviceCode != null ? serviceCode.hashCode() : 0);
        //result = 31 * result + (version != null ? version.hashCode() : 0);
        result = 31 * result + (appCode != null ? appCode.hashCode() : 0);
        result = 31 * result + (createTime != null ? createTime.hashCode() : 0);
        return result;
    }
}
