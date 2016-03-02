package com.sunsharing.eos.uddi.model;

import org.hibernate.annotations.Cascade;

import javax.persistence.*;
import java.util.List;

/**
 * Created by criss on 14-1-30.
 */
@Entity
@Table(name = "T_USER")
public class TUser {
    private int userId;
    private String userName;
    private String pwd;
    private String role;
    private String creatTime;
    private String eamil;

    private TApp defaultApp;

    @Basic
    @Column(name = "CREATE_TIME")
    public String getCreatTime() {
        return creatTime;
    }

    public void setCreatTime(String creatTime) {
        this.creatTime = creatTime;
    }

    @Basic
    @Column(name = "EMAIL")
    public String getEamil() {
        return eamil;
    }

    public void setEamil(String eamil) {
        this.eamil = eamil;
    }

    @ManyToOne
    @JoinColumn(name="DEFAULT_APPID")
    public TApp getDefaultApp() {
        return defaultApp;
    }

    public void setDefaultApp(TApp defaultApp) {
        this.defaultApp = defaultApp;
    }

    List<TUserApp> userApps;

    @OneToMany(mappedBy="user",cascade={CascadeType.ALL},fetch=FetchType.LAZY)
    @Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
    public List<TUserApp> getUserApps() {
        return userApps;
    }

    public void setUserApps(List<TUserApp> userApps) {
        this.userApps = userApps;
    }

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    @Column(name = "USER_ID")
    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    @Basic
    @Column(name = "USER_NAME")
    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    @Basic
    @Column(name = "PWD")
    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    @Basic
    @Column(name = "ROLE")
    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TUser tUser = (TUser) o;

        if (userId != tUser.userId) return false;
        if (pwd != null ? !pwd.equals(tUser.pwd) : tUser.pwd != null) return false;
        if (role != null ? !role.equals(tUser.role) : tUser.role != null) return false;
        if (userName != null ? !userName.equals(tUser.userName) : tUser.userName != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = userId;
        result = 31 * result + (userName != null ? userName.hashCode() : 0);
        result = 31 * result + (pwd != null ? pwd.hashCode() : 0);
        result = 31 * result + (role != null ? role.hashCode() : 0);
        return result;
    }
}
