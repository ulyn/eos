package com.sunsharing.eos.uddi.model;

import javax.persistence.*;

/**
 * Created by criss on 14-1-30.
 */
@Entity
@Table(name = "T_MODULE")
public class TModule {
    private int moduleId;
    private String moduleName;
    private TApp app;
    private Integer column4;

    @ManyToOne
    @JoinColumn(name="APP_ID")
    public TApp getApp() {
        return app;
    }

    public void setApp(TApp app) {
        this.app = app;
    }

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    @Column(name = "MODULE_ID")
    public int getModuleId() {
        return moduleId;
    }

    public void setModuleId(int moduleId) {
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



    @Basic
    @Column(name = "Column_4")
    public Integer getColumn4() {
        return column4;
    }

    public void setColumn4(Integer column4) {
        this.column4 = column4;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TModule tModule = (TModule) o;

        if (moduleId != tModule.moduleId) return false;
        if (column4 != null ? !column4.equals(tModule.column4) : tModule.column4 != null) return false;
        if (moduleName != null ? !moduleName.equals(tModule.moduleName) : tModule.moduleName != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = moduleId;
        result = 31 * result + (moduleName != null ? moduleName.hashCode() : 0);
        result = 31 * result + (column4 != null ? column4.hashCode() : 0);
        return result;
    }
}
