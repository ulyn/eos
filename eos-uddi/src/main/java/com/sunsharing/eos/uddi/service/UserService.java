package com.sunsharing.eos.uddi.service;

import com.sunsharing.component.utils.base.DateUtils;
import com.sunsharing.eos.common.utils.StringUtils;
import com.sunsharing.eos.uddi.dao.SimpleHibernateDao;
import com.sunsharing.eos.uddi.model.TApp;
import com.sunsharing.eos.uddi.model.TUser;
import com.sunsharing.eos.uddi.model.TUserApp;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * Created by criss on 14-1-30.
 */
@Service
@Transactional
public class UserService {

    private SimpleHibernateDao<TUser,String> userDao;//用户管理
    private SimpleHibernateDao<TApp,String> appDao;//用户管理

    @Autowired
    public void setSessionFactory(SessionFactory sessionFactory){
        userDao = new SimpleHibernateDao<TUser,String>(sessionFactory,TUser.class);
        appDao = new SimpleHibernateDao<TApp,String>(sessionFactory,TApp.class);
    }

    public TUser login(String username,String password)
    {
        String sql = "from TUser where userName=? and pwd=?";
        List<TUser> users = userDao.find(sql,username,password);
        if(users.size()==0)
        {
            return null;
        }else
        {
            return users.get(0);
        }
    }


    public void saveUser(TUser user)
    {
        userDao.save(user);
    }

    public void updateUser(TUser user)
    {
        userDao.update(user);
    }

    public List<TUser> getUserlist(String yw)
    {
        if(StringUtils.isBlank(yw)){
            String sql = "from TUser order by  creatTime desc";
            return userDao.find(sql);
        }else{
            String sql = "from TUser where yw = '"+yw+"' order by  creatTime desc";
            return userDao.find(sql);
        }
    }

    public TUser loadUser(String id)
    {
        return userDao.get(id);
    }

    public void updateUser(String id,
                           String role,
                           String apps,
                           String isTest,
                           String yw)
    {
        TUser user = loadUser(id);
        user.setRole(role);
        user.setIsTest(isTest);
        user.setYw(yw);

        user.getUserApps().clear();

        if(!StringUtils.isBlank(apps))
        {
            String[] arr = apps.split(",");
            for(int i=0;i<arr.length;i++)
            {
                TUserApp userApp = new TUserApp();
                userApp.setUser(user);
                userApp.setApp(appDao.get(arr[i]));
                user.getUserApps().add(userApp);
            }
        }
        userDao.update(user);
    }

    public void addUser(String username,String pwd,String email,String yw)
    {
        String sql = "from TUser where userName=?";
        List list = userDao.find(sql,username);
        if(list.size()>0)
        {
            throw new RuntimeException("用户名重复");
        }
        TUser user = new TUser();
        user.setUserName(username);
        user.setEamil(email);
        user.setPwd(pwd);
        user.setYw(yw);
        user.setCreatTime(DateUtils.getDBString(new Date()));
        userDao.save(user);
    }



}
