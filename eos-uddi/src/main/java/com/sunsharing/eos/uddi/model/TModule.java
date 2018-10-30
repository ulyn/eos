package com.sunsharing.eos.uddi.model;

import com.sunsharing.eos.common.utils.StringUtils;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

/**
 * Created by criss on 14-1-30.
 */
@Entity
@Table(name = "T_MODULE")
public class TModule {
    private String moduleId;
    private String moduleName;
    private TApp app;

    public TModule(){
        this.moduleId = StringUtils.genUUID();
    }

    @ManyToOne
    @JoinColumn(name="APP_ID")
    public TApp getApp() {
        return app;
    }

    public void setApp(TApp app) {
        this.app = app;
    }

    @Id
    @GenericGenerator(name = "system-uuid", strategy = "uuid")
    @Column(name = "MODULE_ID")
    public String getModuleId() {
        return moduleId;
    }

    public void setModuleId(String moduleId) {
        this.moduleId = moduleId;
    }

    @Basic
    @Column(name = "MODULE_NAME")
    public String getModuleName() {
        return moduleName;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }




    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TModule tModule = (TModule) o;

        if (moduleId != tModule.moduleId) return false;
        if (moduleName != null ? !moduleName.equals(tModule.moduleName) : tModule.moduleName != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = moduleId.hashCode();
        result = 31 * result + (moduleName != null ? moduleName.hashCode() : 0);
        return result;
    }
}
