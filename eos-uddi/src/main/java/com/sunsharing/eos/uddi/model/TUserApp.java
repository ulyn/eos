package com.sunsharing.eos.uddi.model;

import com.sunsharing.eos.common.utils.StringUtils;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

/**
 * Created by criss on 14-1-30.
 */
@Entity
@Table(name = "T_USER_APP")
public class TUserApp implements Comparable {
    private String userAppId;

    private TUser user;
    private TApp app;

    public TUserApp(){
        userAppId = StringUtils.genUUID();
    }

    @ManyToOne
    @JoinColumn(name="USER_ID")
    public TUser getUser() {
        return user;
    }

    public void setUser(TUser user) {
        this.user = user;
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
    @Column(name = "USER_APP_ID")
    public String getUserAppId() {
        return userAppId;
    }

    public void setUserAppId(String userAppId) {
        this.userAppId = userAppId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TUserApp tUserApp = (TUserApp) o;

        return userAppId.equals(tUserApp.userAppId);
    }

    @Override
    public int hashCode() {
        return userAppId.hashCode();
    }

    @Override
    public int compareTo(Object o) {
        if(this.getApp().getAppId().compareTo (((TUserApp)o).getApp().getAppId()) > 0 )
        {
            return 1;
        }else if(this.getApp().getAppId().compareTo (((TUserApp)o).getApp().getAppId()) < 0)
        {
            return -1;
        }else
        {
            return 0;
        }
    }
}
