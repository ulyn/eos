package com.sunsharing.eos.uddi.model;

import com.sunsharing.eos.common.utils.StringUtils;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

/**
 * Created by criss on 16/5/6.
 */
@Entity
@Table(name = "T_CONFIG")
public class TConfig {
    private String configId;
    private String appId = "0";
    private String chlidAppId = "0";
    private String groupId;
    private String key;
    private String isBasic;
    private String relConfigId = "0";
    private String defaultValue;
    private String isCommit;
    private String att;
    private String conDesc;
    private String _delete = "0";

    public TConfig(){
        this.configId = StringUtils.genUUID();
    }

    @Id
    @Column(name = "CONFIG_ID")
    @GenericGenerator(name = "system-uuid", strategy = "uuid")
    public String getConfigId() {
        return configId;
    }

    public void setConfigId(String configId) {
        this.configId = configId;
    }

    @Basic
    @Column(name = "APP_ID")
    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    @Basic
    @Column(name = "CHLID_APP_ID")
    public String getChlidAppId() {
        return chlidAppId;
    }

    public void setChlidAppId(String chlidAppId) {
        this.chlidAppId = chlidAppId;
    }

    @Basic
    @Column(name = "GROUP_ID")
    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    @Basic
    @Column(name = "CON_KEY")
    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    @Basic
    @Column(name = "IS_BASIC")
    public String getIsBasic() {
        return isBasic;
    }

    public void setIsBasic(String isBasic) {
        this.isBasic = isBasic;
    }

    @Basic
    @Column(name = "REL_CONFIG_ID")
    public String getRelConfigId() {
        return relConfigId;
    }

    public void setRelConfigId(String relConfigId) {
        this.relConfigId = relConfigId;
    }

    @Basic
    @Column(name = "DEFAULT_VALUE")
    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    @Basic
    @Column(name = "IS_COMMIT")
    public String getIsCommit() {
        return isCommit;
    }

    public void setIsCommit(String isCommit) {
        this.isCommit = isCommit;
    }

    @Basic
    @Column(name = "ATT")
    public String getAtt() {
        return att;
    }

    public void setAtt(String att) {
        this.att = att;
    }

    @Basic
    @Column(name = "CON_DESC")
    public String getConDesc() {
        return conDesc;
    }

    public void setConDesc(String conDesc) {
        this.conDesc = conDesc;
    }

    @Basic
    @Column(name = "_DEL")
    public String get_delete() {
        return _delete;
    }

    public void set_delete(String _delete) {
        this._delete = _delete;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TConfig tConfig = (TConfig) o;

        if (configId != tConfig.configId) return false;
        if (appId != null ? !appId.equals(tConfig.appId) : tConfig.appId != null) return false;
        if (chlidAppId != null ? !chlidAppId.equals(tConfig.chlidAppId) : tConfig.chlidAppId != null) return false;
        if (groupId != null ? !groupId.equals(tConfig.groupId) : tConfig.groupId != null) return false;
        if (key != null ? !key.equals(tConfig.key) : tConfig.key != null) return false;
        if (isBasic != null ? !isBasic.equals(tConfig.isBasic) : tConfig.isBasic != null) return false;
        if (relConfigId != null ? !relConfigId.equals(tConfig.relConfigId) : tConfig.relConfigId != null) return false;
        if (defaultValue != null ? !defaultValue.equals(tConfig.defaultValue) : tConfig.defaultValue != null)
            return false;
        if (isCommit != null ? !isCommit.equals(tConfig.isCommit) : tConfig.isCommit != null) return false;
        if (att != null ? !att.equals(tConfig.att) : tConfig.att != null) return false;
        if (conDesc != null ? !conDesc.equals(tConfig.conDesc) : tConfig.conDesc != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = configId.hashCode();
        result = 31 * result + (appId != null ? appId.hashCode() : 0);
        result = 31 * result + (chlidAppId != null ? chlidAppId.hashCode() : 0);
        result = 31 * result + (groupId != null ? groupId.hashCode() : 0);
        result = 31 * result + (key != null ? key.hashCode() : 0);
        result = 31 * result + (isBasic != null ? isBasic.hashCode() : 0);
        result = 31 * result + (relConfigId != null ? relConfigId.hashCode() : 0);
        result = 31 * result + (defaultValue != null ? defaultValue.hashCode() : 0);
        result = 31 * result + (isCommit != null ? isCommit.hashCode() : 0);
        result = 31 * result + (att != null ? att.hashCode() : 0);
        result = 31 * result + (conDesc != null ? conDesc.hashCode() : 0);
        return result;
    }
}
