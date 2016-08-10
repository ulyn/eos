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

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sunsharing.eos.uddi.dao.SimpleHibernateDao;
import com.sunsharing.eos.uddi.db.AntZip;
import com.sunsharing.eos.uddi.model.TApp;
import com.sunsharing.eos.uddi.model.TService;
import com.sunsharing.eos.uddi.model.TServiceVersion;
import com.sunsharing.eos.uddi.sys.SysInit;
import org.apache.commons.io.FileUtils;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.File;
import java.io.OutputStream;
import java.util.*;

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
    private SimpleHibernateDao<TServiceVersion, Integer> versionDao;//版本管理
    @Resource
    com.sunsharing.eos.uddi.service.Service service;

    @Autowired
    public void setSessionFactory(SessionFactory sessionFactory) {
        serviceDao = new SimpleHibernateDao<TService, Integer>(sessionFactory, TService.class);
        appDao = new SimpleHibernateDao<TApp, Integer>(sessionFactory, TApp.class);
        versionDao = new SimpleHibernateDao<TServiceVersion, Integer>(sessionFactory, TServiceVersion.class);
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
            TService serviceCopy = JSON.parseObject(JSONObject.toJSONString(service),TService.class);
            List<TServiceVersion> list = serviceCopy.getVersions();
            //防止版本排序被修改了，自己再排序一次做兼容
            Collections.sort(list,new Comparator<TServiceVersion>() {
                @Override
                public int compare(TServiceVersion o1, TServiceVersion o2) {
                    if(o1.getVersionId() < o2.getVersionId()){
                        return 1;
                    }else if(o1.getVersionId() > o2.getVersionId()){
                        return -1;
                    }else{
                        return 0;
                    }
                }
            });
            for(TServiceVersion serviceVersion : list){
                if (serviceVersion.getStatus().equals("1")) {
                    List<TServiceVersion> serviceVersions = new ArrayList<TServiceVersion>();
                    serviceVersions.add(serviceVersion);
                    serviceCopy.setVersions(serviceVersions);
                    services.add(serviceCopy);
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

    public Map createService(String versionId,String type) throws Exception {
        Map map = new HashMap();
        File file;
        String name;
        //先简单处理。。。
        if("DynamicJava".equals(type) || "JS".equals(type)){
            TServiceVersion version = versionDao.get(new Integer(versionId));
            String fileDir = SysInit.path + "/stub_service/" + version.getAppCode()
                    + "/" + version.getVersionId();
            File dir = new File(fileDir);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            if("DynamicJava".equals(type)){
                file = new DynamicJavaCreator().createServiceFile(version.getService(),fileDir);
            }else{
                file = new JSCreator().createServiceFile(version.getService(),fileDir);
            }
            name = file.getName();
        }else{
            file = service.getJavaFile(versionId);
            name = service.getName(versionId);
            name = name.substring(0, 1).toUpperCase() + name.substring(1);
            name += ".java";
        }
        map.put("content",FileUtils.readFileToString(file,"utf-8"));
        map.put("name",name);
        return map;
    }
}

