package com.sunsharing.component.model;

import javax.persistence.*;

/**
 * Created by criss on 14-5-6.
 */
@Entity
@Table(name = "BM_CLZT", schema = "", catalog = "")
public class BmClzt {
    private String code;
    private String name;

    @Id
    @Column(name = "CODE")
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Basic
    @Column(name = "NAME")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
