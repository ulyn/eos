package com.sunsharing.eos.uddi.service;

import com.sunsharing.component.utils.base.DateUtils;
import com.sunsharing.eos.uddi.dao.SimpleHibernateDao;
import com.sunsharing.eos.uddi.model.TApp;
import com.sunsharing.eos.uddi.model.TModule;
import com.sunsharing.eos.uddi.model.TUser;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * Created by criss on 14-1-31.
 */
@Service
@Transactional
public class AppService {

    private SimpleHibernateDao<TApp,Integer> appDao;//用户管理
    private SimpleHibernateDao<TModule,String> moduleDao;//用户管理

    @Autowired
    public void setSessionFactory(SessionFactory sessionFactory){
        appDao = new SimpleHibernateDao<TApp,Integer>(sessionFactory,TApp.class);
        moduleDao = new SimpleHibernateDao<TModule,String>(sessionFactory,TModule.class);
    }

    public List<TApp> listApp()
    {
        String app = "from TApp order by creatTime desc";
        return appDao.find(app);
    }

    public void saveApp(String app_en,String app_cn,String modules)
    {
        TApp app = new TApp();
        app.setAppCode(app_en);
        app.setAppName(app_cn);
        app.setCreatTime(DateUtils.getDBString(new Date()));


        String[] moduleArr = modules.split(",");
        for(int i=0;i<moduleArr.length;i++)
        {
            TModule module = new TModule();
            module.setApp(app);
            module.setModuleName(moduleArr[i]);
            app.getModules().add(module);
            //moduleDao.save(module);
        }
        appDao.save(app);
    }

    public void updateApp(String appId,String app_en,String app_cn,String modules)
    {
        TApp app = appDao.get(new Integer(appId));
        app.setAppCode(app_en);
        app.setAppName(app_cn);

        app.getModules().clear();

        String[] moduleArr = modules.split(",");
        for(int i=0;i<moduleArr.length;i++)
        {
            TModule module = new TModule();
            module.setApp(app);
            module.setModuleName(moduleArr[i]);
            app.getModules().add(module);
            //moduleDao.save(module);
        }
    }

    public TApp loadApp(String id)
    {
        return appDao.get(new Integer(id));
    }

}
