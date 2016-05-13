package com.sunsharing.eos.uddi.dao;

import org.hibernate.cfg.ImprovedNamingStrategy;

/**
 * Created by criss on 16/5/12.
 */
public class CsmpNamingStrategy extends ImprovedNamingStrategy {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    @Override
    public String columnName(String columnName) {
        return addUnderscores(columnName).toUpperCase();
    }
    @Override
    public String tableName(String tableName) {
        return addUnderscores(tableName).toUpperCase();
    }


}