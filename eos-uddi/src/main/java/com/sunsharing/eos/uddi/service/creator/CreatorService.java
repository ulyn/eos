/**
 * @(#)Creator
 * 版权声明 厦门畅享信息技术有限公司, 版权所有 违者必究
 *
 *<br> Copyright:  Copyright (c) 2016
 *<br> Company:厦门畅享信息技术有限公司
 *<br> @author ulyn
 *<br> 16-6-22 下午4:06
 *<br> @version 1.0
 *————————————————————————————————
 *修改记录
 *    修改者：
 *    修改时间：
 *    修改原因：
 *————————————————————————————————
 */
package com.sunsharing.eos.uddi.service.creator;

import com.sunsharing.eos.uddi.dao.SimpleHibernateDao;
import com.sunsharing.eos.uddi.db.AntZip;
import com.sunsharing.eos.uddi.model.TApp;
import com.sunsharing.eos.uddi.model.TService;
import com.sunsharing.eos.uddi.model.TServiceVersion;
import com.sunsharing.eos.uddi.sys.SysInit;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * <pre></pre>
 * <br>----------------------------------------------------------------------
 * <br> <b>功能描述:</b>
 * <br>
 * <br> 注意事项:
 * <br>
 * <br>
 * <br>----------------------------------------------------------------------
 * <br>
 */
@Service
public class CreatorService {

    @Autowired
    JdbcTemplate jdbcTemplate;
    private SimpleHibernateDao<TService, Integer> serviceDao;//用户管理
    private SimpleHibernateDao<TApp, Integer> appDao;//用户管理


    @Autowired
    public void setSessionFactory(SessionFactory sessionFactory) {
        serviceDao = new SimpleHibernateDao<TService, Integer>(sessionFactory, TService.class);
        appDao = new SimpleHibernateDao<TApp, Integer>(sessionFactory, TApp.class);
    }

    public String lastVersion(String appId){
        String sql = "SELECT SUM(SC) FROM( " +
                " SELECT MAX(V.VERSION_ID) SC FROM " +
                " T_SERVICE_VERSION V,T_APP A " +
                " WHERE V.APP_CODE = A.APP_CODE AND V.STATUS = ? AND A.APP_ID =? " +
                " GROUP BY V.SERVICE_ID) A";
        int maxServiceId = jdbcTemplate.queryForInt(sql,"1",appId);
        return String.valueOf(maxServiceId);
    }

    public File createServices(String appId,String v,CreatorType creatorType) throws Exception {
        TApp app = appDao.get(new Integer(appId));
        List<TService> servicesTemp = serviceDao.find("from TService where appId=?", new Integer(appId));
        List<TService> services = new ArrayList<TService>();
        for (TService service : servicesTemp) {
            List<TServiceVersion> list = service.getVersions();
            for(TServiceVersion serviceVersion : list){
                if (serviceVersion.getStatus().equals("1")) {
                    services.add(service);
                    break;
                }
            }
        }
        String fileDir = SysInit.path + "/stub_service/" + app.getAppCode()
                + "/" + v + "/" + creatorType.name();
        File dir = new File(fileDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return creatorType.getCreator().create(fileDir,app,v,services);
    }

    public void writeServicesZip(String appId,String v,CreatorType creatorType,OutputStream out) throws Exception {
        File file = createServices(appId,v,creatorType);
        AntZip zip = new AntZip();
        zip.doZip(file.getPath(),out);
    }
}

