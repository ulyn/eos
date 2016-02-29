package com.sunsharing.eos.uddi.service;

import com.sunsharing.component.utils.base.DateUtils;
import com.sunsharing.eos.common.utils.StringUtils;
import com.sunsharing.eos.uddi.dao.SimpleHibernateDao;
import com.sunsharing.eos.uddi.model.*;
import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.*;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * Created by criss on 16/2/25.
 */
@org.springframework.stereotype.Service
@Transactional
public class DbChangeService {

    Logger logger = Logger.getLogger(AppService.class);
    @Autowired
    JdbcTemplate jdbc;

    private SimpleHibernateDao<TDbChange, Integer> dbChangeDao;//用户管理
    private SimpleHibernateDao<TDbChecklist, Integer> dbCheckListDao;//用户管理
    private SimpleHibernateDao<TDbPdm, Integer> pdmDao;//用户管理
    private SimpleHibernateDao<TUser, Integer> userDao;//用户管理
    private SimpleHibernateDao<TApp, Integer> appDao;//用户管理
    private SimpleHibernateDao<TDbChecklist, Integer> checkListDao;//用户管理


    @Autowired
    public void setSessionFactory(SessionFactory sessionFactory) {
        dbChangeDao = new SimpleHibernateDao<TDbChange, Integer>(sessionFactory, TDbChange.class);
        dbCheckListDao = new SimpleHibernateDao<TDbChecklist, Integer>(sessionFactory, TDbChecklist.class);
        pdmDao = new SimpleHibernateDao<TDbPdm, Integer>(sessionFactory, TDbPdm.class);
        userDao = new SimpleHibernateDao<TUser, Integer>(sessionFactory, TUser.class);
        appDao = new SimpleHibernateDao<TApp, Integer>(sessionFactory, TApp.class);
        checkListDao = new SimpleHibernateDao<TDbChecklist, Integer>(sessionFactory, TDbChecklist.class);
    }

    public List<TDbChange> list()
    {
        String hql = "from TDbChange order by version desc";
        Query query = dbCheckListDao.createQuery(hql);
        query.setMaxResults(100);
        return  query.list();
    }

    public TDbPdm isNotMyLock(int appId,int userId)
    {
        List<TDbPdm> pdmList = pdmDao.
                find("from TDbPdm where lock=? and appId.appId=? and lockUserId.userId<>?",
                        "1",appId, userId);
        if(pdmList.size()==0)
        {
            return null;
        }else
        {
            return pdmList.get(0);
        }
    }

    public TDbChange loadDbchange(String changeId)
    {
        TDbChange change = dbChangeDao.get(new Integer(changeId));
        change.getDbChecklistList().size();
        return change;
    }

    public String getVersion(String appId)
    {

        String dt = DateUtils.getDBString(new Date()).substring(0,8);
        String sql = "select count(*)+1 from T_DB_CHANGE where APP_ID="+appId+" AND PUBISH_TIME like '"+dt+"%'";
        String num = jdbc.queryForInt(sql)+"";
        for(int i=0;i<3-num.length();i++)
        {
            num = "0"+num;
        }
        return dt+num;
    }

    public void saveChange(String appId,String changelog,
                           String db,int userId,String changeId)
    {
        TApp app = appDao.get(new Integer(appId));
        TUser user = userDao.get(new Integer(userId));

        List<TDbPdm> pdmList = pdmDao.find("from TDbPdm where appId.appId=?",
                new Integer(appId));
        if(pdmList.size() == 0)
        {
            TDbPdm pdm = new TDbPdm();
            pdm.setLock("0");
            pdm.setPdm(app.getAppCode()+".pdm");
            pdm.setAppId(app);
            pdm.setLockUserId(user);
            pdmDao.save(pdm);
        }else
        {
            TDbPdm pdm = pdmList.get(0);
            pdm.setLock("0");
            pdm.setLockUserId(null);
            pdmDao.update(pdm);
        }
        TDbChange change = new TDbChange();
        if(!StringUtils.isBlank(changeId))
        {
            change = loadDbchange(changeId);
        }
        change.setAppId(app);
        change.setChangeLog(changelog);
        change.setDb(db);
        change.setUser(user);
        change.setPubishTime(DateUtils.getDBString(new Date()));
        if(StringUtils.isBlank(changeId)) {
            change.setScript(getVersion(appId) + ".sql");
            change.setVersion(getVersion(appId));
            dbChangeDao.save(change);
        }else
        {
            dbChangeDao.update(change);
        }


    }

    public void saveCheck(String changeId,String checkStatus,String checkContent,
                          TUser user)
    {
        TDbChecklist dbChecklist = new TDbChecklist();
        dbChecklist.setCheckContent(checkContent);
        dbChecklist.setCheckStatus(checkStatus);
        dbChecklist.setCheckUser(user);
        dbChecklist.setCheckTime(DateUtils.getDBString(new Date()));
        dbChecklist.setChange(loadDbchange(changeId));
        checkListDao.save(dbChecklist);
    }

    public TDbPdm loadDbPdm(String appId)
    {
        String hql = "from TDbPdm where appId.appId=?";
        List<TDbPdm> pdmList = pdmDao.find(hql,new Integer(appId));
        if(pdmList.size()>0)
        {
            return pdmList.get(0);
        }else
        {
            return null;
        }
    }

    public void lockPdm(String appId,TUser user)
    {
        String hql = "from TDbPdm where appId.appId=?";
        List<TDbPdm> pdmList = pdmDao.find(hql,new Integer(appId));
        if(pdmList.size()>0)
        {
            TDbPdm pdm = pdmList.get(0);
            pdm.setLock("1");
            pdm.setLockUserId(user);
            pdmDao.update(pdm);
        }else
        {
            return;
        }
    }



}
