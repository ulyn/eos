package com.sunsharing.component.eos.serverexample.test;

import com.sunsharing.component.eos.serverexample.dao.SimpleHibernateDao;
import com.sunsharing.component.model.BmClzt;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

/**
 * Created by criss on 14-2-14.
 */
@Service
public class Test2Impl implements Test2 {

    @Autowired
    JdbcTemplate jdbcTemplate;

    private SimpleHibernateDao<BmClzt, String> activityDao; //活动

    @Autowired
    public void setSessionFactory(SessionFactory sessionFactory) {
        activityDao = new SimpleHibernateDao<BmClzt, String>(sessionFactory, BmClzt.class);

    }

    @Override
    public String sayHello(String abc) {

//        String sql = "insert into BM_CLZT(BH0000) values('abc')";
//        jdbcTemplate.execute(sql);
        BmClzt clz = new BmClzt();
        clz.setCode("addd");
        activityDao.save(clz);
        return "我是真实的";
    }
}
