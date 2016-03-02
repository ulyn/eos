package com.sunsharing.eos.uddi.model;

import javax.persistence.*;

/**
 * Created by criss on 14-1-30.
 */
@Entity
@Table(name = "T_USER_APP")
public class TUserApp implements Comparable {
    private int userAppId;

    private TUser user;
    private TApp app;

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
    @GeneratedValue(strategy=GenerationType.AUTO)
    @Column(name = "USER_APP_ID")
    public int getUserAppId() {
        return userAppId;
    }

    public void setUserAppId(int userAppId) {
        this.userAppId = userAppId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TUserApp tUserApp = (TUserApp) o;

        if (userAppId != tUserApp.userAppId) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return userAppId;
    }

    @Override
    public int compareTo(Object o) {
        if(this.getApp().getAppId()>((TUserApp)o).getApp().getAppId())
        {
            return 1;
        }else if(this.getApp().getAppId()<((TUserApp)o).getApp().getAppId())
        {
            return -1;
        }else
        {
            return 0;
        }
    }
}
