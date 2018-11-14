package com.sunsharing.eos.uddi.service;

import com.sunsharing.eos.common.utils.StringUtils;
import com.sunsharing.eos.uddi.dao.SimpleHibernateDao;
import com.sunsharing.eos.uddi.model.*;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.*;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by criss on 16/5/6.
 */
@org.springframework.stereotype.Service
@Transactional
public class ConfigService {
    @Autowired
    JdbcTemplate jdbc;

    private SimpleHibernateDao<TConfigGroup, String> groupDao;//用户管理
    private SimpleHibernateDao<TConfig, String> configDao;//用户管理
    private SimpleHibernateDao<TConfigChildApp, String> childDao;//用户管理
    private SimpleHibernateDao<TConfigRun, String> runDao;//用户管理
    private SimpleHibernateDao<TConfigRunVal, String> runValDao;//用户管理


    @Autowired
    public void setSessionFactory(SessionFactory sessionFactory) {
        groupDao = new SimpleHibernateDao<TConfigGroup, String>(sessionFactory, TConfigGroup.class);
        configDao = new SimpleHibernateDao<TConfig, String>(sessionFactory, TConfig.class);
        childDao = new SimpleHibernateDao<TConfigChildApp, String>(sessionFactory, TConfigChildApp.class);
        runDao = new SimpleHibernateDao<TConfigRun, String>(sessionFactory, TConfigRun.class);
        runValDao = new SimpleHibernateDao<TConfigRunVal, String>(sessionFactory, TConfigRunVal.class);
    }

    public void saveGroup(TConfigGroup group)
    {
        groupDao.saveOrUpdate(group);
    }

    public void saveConfig(TConfig config)
    {
        configDao.saveOrUpdate(config);
    }

    public TConfig findSql(String hql)
    {
        List<TConfig> list = configDao.find(hql);
        if(list.size()>0)
        {
            return list.get(0);
        }
        return null;
    }

    public void commitConfig(String configId)
    {
        TConfig  config = configDao.get(new String(configId));
        config.setIsCommit("1");
        configDao.update(config);
//        if(config.getRelConfigId()!=null && config.getRelConfigId()!=0)
//        {
//            TConfig config1 = configDao.get(new Integer(config.getRelConfigId()));
//            config1.setIsCommit("1");
//            configDao.update(config1);
//        }
        String hql = "update TConfig set isCommit = '1'  where relConfigId = '"+configId+"'";
        configDao.createQuery(hql).executeUpdate();
    }

    public boolean containKey(String key,String childAppId)
    {
        String sql = "";
        if(!StringUtils.isBlank(childAppId))
        {
            sql = "from TConfig where key='"+key+"' and chlidAppId="+childAppId+" and _DEL = '0'";
        }else
        {
            return false;
            //sql = "from TConfig where key='"+key+"' and IS_BASIC = '1' and REL_CONFIG_ID = '0' and _DEL='0' " ;
        }
        List list = configDao.find(sql);
        if(list.size()>0)
        {
            return true;
        }
        return false;
    }

    public void saveConfigChildApp(TConfigChildApp childApp)
    {
        childDao.save(childApp);
    }

    public TConfigGroup loadGroup(String groupId)
    {
        return groupDao.get(new String(groupId));
    }

    public TConfig loadConfig(String configId)
    {
        return configDao.get(new String(configId));
    }

    public void deleteGroup(String groupId)
    {
        TConfigGroup group = groupDao.get(new String(groupId));
        group.set_delete("1");
        groupDao.saveOrUpdate(group);
    }

    public void deleteConfig(String config)
    {
        TConfig group = configDao.get(new String(config));
        group.set_delete("1");
        configDao.saveOrUpdate(group);
        String hql = "update TConfig set _delete = '1'  where relConfigId = "+config;
        configDao.createQuery(hql).executeUpdate();


    }


    public TConfigRun loadRun(String runId)
    {
        TConfigRun run = runDao.get(new String(runId));
        return run;
    }

    public void saveRun(TConfigRun run)
    {
        runDao.save(run);
    }

    public void saveRunVal(String runId,String configId,String val)
    {
        String hql = "from TConfigRunVal where runId = '"+runId+"' and configId='"+configId+"'";
        List<TConfigRunVal> runVals = runValDao.find(hql);
        if(runVals.size()>0)
        {
            runVals.get(0).setVal(val);
            runValDao.update(runVals.get(0));
        }else
        {
            TConfigRunVal runVal = new TConfigRunVal();
            runVal.setVal(val);
            runVal.setConfigId(new String(configId));
            runVal.setRunId(new String(runId));
            runValDao.saveObject(runVal);
        }
    }

    public TConfigRunVal getRunVal(String runId,String configId)
    {
        String hql = "from TConfigRunVal where runId = '"+runId+"' and configId='"+configId+"'";
        List<TConfigRunVal> runVals = runValDao.find(hql);
        if(runVals.size()>0)
        {
            return runVals.get(0);
        }else
        {
            return null;
        }
    }


}
