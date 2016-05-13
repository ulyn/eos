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

    private SimpleHibernateDao<TConfigGroup, Integer> groupDao;//用户管理
    private SimpleHibernateDao<TConfig, Integer> configDao;//用户管理
    private SimpleHibernateDao<TConfigChildApp, Integer> childDao;//用户管理
    private SimpleHibernateDao<TConfigRun, Integer> runDao;//用户管理
    private SimpleHibernateDao<TConfigRunVal, Integer> runValDao;//用户管理


    @Autowired
    public void setSessionFactory(SessionFactory sessionFactory) {
        groupDao = new SimpleHibernateDao<TConfigGroup, Integer>(sessionFactory, TConfigGroup.class);
        configDao = new SimpleHibernateDao<TConfig, Integer>(sessionFactory, TConfig.class);
        childDao = new SimpleHibernateDao<TConfigChildApp, Integer>(sessionFactory, TConfigChildApp.class);
        runDao = new SimpleHibernateDao<TConfigRun, Integer>(sessionFactory, TConfigRun.class);
        runValDao = new SimpleHibernateDao<TConfigRunVal, Integer>(sessionFactory, TConfigRunVal.class);
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
        TConfig  config = configDao.get(new Integer(configId));
        config.setIsCommit("1");
        configDao.update(config);
//        if(config.getRelConfigId()!=null && config.getRelConfigId()!=0)
//        {
//            TConfig config1 = configDao.get(new Integer(config.getRelConfigId()));
//            config1.setIsCommit("1");
//            configDao.update(config1);
//        }
        String hql = "update TConfig set isCommit = '1'  where relConfigId = "+configId;
        configDao.createQuery(hql).executeUpdate();
    }

    public boolean containKey(String key,String childAppId)
    {
        String sql = "";
        if(!StringUtils.isBlank(childAppId))
        {
            sql = "from TConfig where key='"+key+"' and chlidAppId="+childAppId;
        }else
        {
            //sql = "from TConfig where key='"+key+"' and IS_BASIC = '1'";
            return false;
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
        return groupDao.get(new Integer(groupId));
    }

    public TConfig loadConfig(String configId)
    {
        return configDao.get(new Integer(configId));
    }

    public void deleteGroup(String groupId)
    {
        TConfigGroup group = groupDao.get(new Integer(groupId));
        group.set_delete("1");
        groupDao.saveOrUpdate(group);
    }

    public void deleteConfig(String config)
    {
        TConfig group = configDao.get(new Integer(config));
        group.set_delete("1");
        configDao.saveOrUpdate(group);
        String hql = "update TConfig set _delete = '1'  where relConfigId = "+config;
        configDao.createQuery(hql).executeUpdate();


    }

    public void saveRun(TConfigRun run)
    {
        runDao.save(run);
    }

    public void saveRunVal(String runId,String configId,String val)
    {
        String hql = "from TConfigRunVal where runId = "+runId+" and configId="+configId;
        List<TConfigRunVal> runVals = runValDao.find(hql);
        if(runVals.size()>0)
        {
            runVals.get(0).setVal(val);
            runValDao.update(runVals.get(0));
        }else
        {
            TConfigRunVal runVal = new TConfigRunVal();
            runVal.setVal(val);
            runVal.setConfigId(new Integer(configId));
            runVal.setRunId(new Integer(runId));
            runValDao.saveObject(runVal);
        }
    }

    public TConfigRunVal getRunVal(String runId,String configId)
    {
        String hql = "from TConfigRunVal where runId = "+runId+" and configId="+configId;
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
