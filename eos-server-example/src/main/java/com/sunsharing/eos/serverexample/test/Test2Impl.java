package com.sunsharing.eos.serverexample.test;

import com.sunsharing.eos.serverexample.dao.SimpleHibernateDao;
import com.sunsharing.eos.serverexample.model.BmClzt;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by criss on 14-2-14.
 */
@Service
@Transactional
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
        clz.setName("sds");
        activityDao.save(clz);
        return "我是真实的";
    }
}
