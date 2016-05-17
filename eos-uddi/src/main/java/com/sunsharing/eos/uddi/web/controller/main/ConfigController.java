package com.sunsharing.eos.uddi.web.controller.main;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.sunsharing.component.utils.base.StringUtils;
import com.sunsharing.eos.uddi.model.*;
import com.sunsharing.eos.uddi.service.ConfigService;
import com.sunsharing.eos.uddi.web.common.ResponseHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by criss on 16/5/5.
 */
@Controller
public class ConfigController {

    @Autowired
    JdbcTemplate jdbc;

    @Autowired
    ConfigService configService;

    /**
     * 保存分组
     * @param model
     * @param request
     * @param response
     * @throws Exception
     */
    @RequestMapping(value="/saveBasic.do",method= RequestMethod.POST)
    public void saveBasic(Model model,HttpServletRequest request,HttpServletResponse response)
            throws Exception {
        String name = request.getParameter("name");
        String isCommon = request.getParameter("isCommon");
        String groupId = request.getParameter("groupId");
        String childAppId = request.getParameter("childAppId");
        String appId = request.getParameter("appId");

        TConfigGroup group = new TConfigGroup();
        //group.setAppId(new Integer(appId));
        group.setGroupName(name);
        group.setIsCommon(isCommon);
        if(!StringUtils.isBlank(groupId)) {
            group.setGroupId(new Integer(groupId));
        }
        if(!StringUtils.isBlank(childAppId))
        {
            group.setAppId(new Integer(appId));
            group.setChildAppId(new Integer(childAppId));
            group.setIsCommon("0");
        }
        configService.saveGroup(group);
        ResponseHelper.printOut(response, true, "", "");
    }
    /**
     * 保存分组
     * @param model
     * @param request
     * @param response
     * @throws Exception
     */
    @RequestMapping(value="/loadgroup.do",method= RequestMethod.POST)
    public void loadgroup(Model model,HttpServletRequest request,HttpServletResponse response)
    {
        String groupId = request.getParameter("groupId");
        TConfigGroup group = configService.loadGroup(groupId);
        ResponseHelper.printOut(response, true, "", group);
    }

    /**
     * 保存分组
     * @param model
     * @param request
     * @param response
     * @throws Exception
     */
    @RequestMapping(value="/loadConfig.do",method= RequestMethod.POST)
    public void loadConfig(Model model,HttpServletRequest request,HttpServletResponse response)
    {
        String configId = request.getParameter("configId");
        TConfig config = configService.loadConfig(configId);
        ResponseHelper.printOut(response, true, "", config);
    }

    /**
     * 保存分组
     * @param model
     * @param request
     * @param response
     * @throws Exception
     */
    @RequestMapping(value="/deletegroup.do",method= RequestMethod.POST)
    public void deletegroup(Model model,HttpServletRequest request,HttpServletResponse response)
    {
        String groupId = request.getParameter("groupId");
        configService.deleteGroup(groupId);
        ResponseHelper.printOut(response, true, "", "");
    }

    /**
     * 保存分组
     * @param model
     * @param request
     * @param response
     * @throws Exception
     */
    @RequestMapping(value="/deleteConfig.do",method= RequestMethod.POST)
    public void deleteConfig(Model model,HttpServletRequest request,HttpServletResponse response)
    {
        String configId = request.getParameter("configId");
        configService.deleteConfig(configId);
        ResponseHelper.printOut(response, true, "", "");
    }



    /**
     * 列出分组
     * @param model
     * @param request
     * @param response
     * @throws Exception
     */
    @RequestMapping(value="/saveBasicConfig.do",method= RequestMethod.POST)
    public void saveBasicConfig(Model model,HttpServletRequest request,HttpServletResponse response)
            throws Exception {

        String groupId = request.getParameter("groupId");
        String configId = request.getParameter("configId");
        String key = request.getParameter("key");
        String defaultValue = request.getParameter("defaultValue");
        String conDesc = request.getParameter("conDesc");
        String att = request.getParameter("att");
        String childAppId = request.getParameter("childAppId");
        String appId = request.getParameter("appId");
        if(StringUtils.isBlank(configId)) {
            if (configService.containKey(key, childAppId)) {
                throw new RuntimeException("KEY已经存在");
            }
        }

        TConfig config = new TConfig();
        config.setIsBasic("1");
        config.setDefaultValue(defaultValue);
        config.setConDesc(conDesc);
        config.setKey(key);
        config.setGroupId(new Integer(groupId));
        config.setAtt(att);
        config.setIsCommit("0");
        if(!StringUtils.isBlank(configId))
        {
            config.setConfigId(new Integer(configId));
        }
        if(!StringUtils.isBlank(childAppId))
        {
            config.setIsBasic("0");
            config.setAppId(new Integer(appId));
            config.setChlidAppId(new Integer(childAppId));
        }
        configService.saveConfig(config);
        ResponseHelper.printOut(response, true, "", "");
    }

    /**
     * 列出分组
     * @param model
     * @param request
     * @param response
     * @throws Exception
     */
    @RequestMapping(value="/listBasic.do",method= RequestMethod.POST)
    public void listBasic(Model model,HttpServletRequest request,HttpServletResponse response)
            throws Exception {
        String appId = request.getParameter("appId");
        String childAppId = request.getParameter("childAppId");
        if("COMMON".equals(childAppId))
        {
            childAppId = "";
        }
        boolean isCommon = true;
        if(!StringUtils.isBlank(childAppId))
        {
            isCommon = false;
        }
        if("0".equals(childAppId))
        {
            childAppId = "";
        }

        if(isCommon) {
            String groupSizeSQL = "select count(*) from T_CONFIG_GROUP where IS_COMMON = 1";
            int size = jdbc.queryForInt(groupSizeSQL);
            if (size == 0) {
                TConfigGroup group = new TConfigGroup();
                //group.setAppId(new Integer(appId));
                group.setGroupName("默认");
                group.setIsCommon("1");
                configService.saveGroup(group);
            }
        }else
        {
            //处理分应用
            String childAppSize = "select count(*) from T_CONFIG_CHILD_APP where APP_ID="+appId;
            int size = jdbc.queryForInt(childAppSize);
            if (size == 0) {
                TConfigChildApp childApp = new TConfigChildApp();
                childApp.setAppId(new Integer(appId));
                childApp.setChildAppName("客户端");
                configService.saveConfigChildApp(childApp);

                TConfigGroup group = new TConfigGroup();
                //group.setAppId(new Integer(appId));
                group.setAppId(new Integer(appId));
                group.setChildAppId(childApp.getChildAppId());
                group.setGroupName("默认");
                group.setIsCommon("0");
                configService.saveGroup(group);


                TConfigChildApp childApp2 = new TConfigChildApp();
                childApp2.setAppId(new Integer(appId));
                childApp2.setChildAppName("服务端");
                configService.saveConfigChildApp(childApp2);

                TConfigGroup group2 = new TConfigGroup();
                //group.setAppId(new Integer(appId));
                group2.setAppId(new Integer(appId));
                group2.setChildAppId(childApp2.getChildAppId());
                group2.setGroupName("默认");
                group2.setIsCommon("0");
                configService.saveGroup(group2);

                childAppId = "" + (childApp.getChildAppId());
            }else
            {
                if(StringUtils.isBlank(childAppId))
                {
                    String sql2 = "select * from T_CONFIG_CHILD_APP where APP_ID="+appId + " limit 1";
                    List<Map<String, Object>> list = jdbc.queryForList(sql2);
                    if(list.size()>0)
                    {
                        childAppId = list.get(0).get("CHILD_APP_ID").toString();
                    }
                }
            }
        }



        String sql = "select t1.GROUP_ID,t1.GROUP_NAME," +
                "t2.CON_KEY,t2.CON_DESC,t2.ATT,t2.DEFAULT_VALUE,t2.IS_COMMIT," +
                "t2.CONFIG_ID,t2.REL_CONFIG_ID,t2._DEL from T_CONFIG_GROUP  t1 " +
                "left join T_CONFIG t2 on t1.GROUP_ID = t2.GROUP_ID  " +
                "where t1.IS_COMMON = 1 and t1._DEL='0'  ";
        if(!isCommon)
        {
            sql = "select t1.GROUP_ID,t1.GROUP_NAME," +
                    "t2.CON_KEY,t2.CON_DESC,t2.ATT,t2.DEFAULT_VALUE,t2.IS_COMMIT," +
                    "t2.CONFIG_ID,t2.REL_CONFIG_ID,t2._DEL from T_CONFIG_GROUP  t1 " +
                    "left join T_CONFIG t2 on t1.GROUP_ID = t2.GROUP_ID  " +
                    "where  t1._DEL='0' and  t1.CHILD_APP_ID = "+childAppId;
        }

        List<Map<String, Object>> list = jdbc.queryForList(sql);

        Map cache = new HashMap();
        List result = new ArrayList();
        for(Map row:list)
        {

            String groupName = (String)row.get("GROUP_NAME");
            String groupId = row.get("GROUP_ID").toString();
            JSONObject jsonObject = null;
            if(cache.get(groupId)==null) {
                jsonObject = new JSONObject();
                jsonObject.put("groupName", groupName);
                jsonObject.put("groupId", groupId);
                jsonObject.put("configs", new JSONArray());
                result.add(jsonObject);
                cache.put(groupId,jsonObject);
            }else
            {
                jsonObject = (JSONObject)cache.get(groupId);
            }
            String del = (String)row.get("_DEL");
            if("1".equals(del))
            {
                continue;
            }

            String key = (String)row.get("CON_KEY");
            Integer rel = (Integer)row.get("REL_CONFIG_ID");
            row.put("IS_REL",false);
            if(StringUtils.isBlank(key) && rel!=null && rel!=0)
            {
                String sql2 = "select t1.GROUP_ID,t1.GROUP_NAME," +
                        "t2.CON_KEY,t2.CON_DESC,t2.ATT,t2.DEFAULT_VALUE,t2.IS_COMMIT," +
                        "t2.CONFIG_ID,t2.REL_CONFIG_ID from T_CONFIG_GROUP  t1 " +
                        "left join T_CONFIG t2 on t1.GROUP_ID = t2.GROUP_ID  " +
                        "where  t1._DEL='0' and (t2._DEL='0' or t2._DEL is NULL ) " +" AND CONFIG_ID="+rel;
                List<Map<String, Object>> list2 = jdbc.queryForList(sql2);
                if(list2.size()>0)
                {
                    Map row2 = list2.get(0);
                    row2.put("CONFIG_ID",row.get("CONFIG_ID"));
                    row = row2;
                    key = (String)row.get("CON_KEY");
                    row.put("IS_REL",true);
                }
            }

            if(!StringUtils.isBlank(key))
            {
                JSONArray array = jsonObject.getJSONArray("configs");
                String iscommit = (String)row.get("IS_COMMIT");
                if("1".equals(iscommit))
                {
                    row.put("commit_color","green");
                }else
                {
                    row.put("commit_color","red");
                }
                String att = (String)row.get("ATT");
                if(!StringUtils.isBlank(att))
                {
                    if("1".equals(att))
                    {
                        row.put("att_label","只读");
                        //row.put("default_value_color","#C9C9C9");
                    }else
                    {
                        row.put("att_label","可覆盖");
                        //row.put("default_value_color","#000");
                    }
                }
                if((Boolean)row.get("IS_REL"))
                {
                    row.put("rel_color","#C9C9C9");
                }else
                {
                    row.put("rel_color","#000");
                }


                array.add(row);
            }
        }
        Map rst = new HashMap();
        rst.put("configlist",result);
        rst.put("isCommon",isCommon);
        rst.put("childAppId",childAppId);
        if(!isCommon)
        {
            String sql2 = "select * from T_CONFIG_CHILD_APP where APP_ID="+appId + "";
            List<Map<String, Object>> list2 = jdbc.queryForList(sql2);
            for(Map m :list2)
            {
                if(m.get("CHILD_APP_ID").toString().equals(childAppId))
                {
                    m.put("selected",true);
                }else
                {
                    m.put("selected",false);
                }
            }
            rst.put("childApps",list2);

        }

        ResponseHelper.printOut(response, true, "", rst);
    }

    @RequestMapping(value="/commitConfig.do",method= RequestMethod.POST)
    public void commitConfig(Model model,HttpServletRequest request,HttpServletResponse response)
    {
        TUser u = (TUser) request.getSession().getAttribute("user");
        if (u.getRole().equals("1")) {
            throw new RuntimeException("对不起你没有审批权限");
        }

        String configId = request.getParameter("configId");

        configService.commitConfig(configId);

        ResponseHelper.printOut(response, true, "", "");
    }


    @RequestMapping(value="/relbasic.do",method= RequestMethod.POST)
    public void relbasic(Model model,HttpServletRequest request,HttpServletResponse response)
    {
        String groupId = request.getParameter("groupId");
        boolean includeRel = false;
        if(StringUtils.isBlank(groupId))
        {
            includeRel = true;
        }

        String sql = "";
        if(includeRel) {
             sql = "select CONFIG_ID as ID,CON_KEY as `name`,GROUP_ID as PID,'CONFIG' TYPE,REL_CONFIG_ID " +
                    "from T_CONFIG t1 where t1.IS_BASIC='1' and t1._DEL='0' ";
        }else
        {
            sql = "select CONFIG_ID as ID,CON_KEY as `name`,GROUP_ID as PID,'CONFIG' TYPE,REL_CONFIG_ID " +
                    "from T_CONFIG t1 where t1.IS_BASIC='1'  and GROUP_ID <> " + groupId + " and REL_CONFIG_ID = 0  and t1._DEL='0'";
        }
        List<Map<String, Object>> list = jdbc.queryForList(sql);
        for(Map m:list)
        {
            int rel = (Integer)m.get("REL_CONFIG_ID");
            if(rel!=0)
            {
                TConfig config = configService.loadConfig(rel+"");
                m.put("name",config.getKey());
            }
        }

        sql = "select GROUP_NAME as `name`,GROUP_ID as ID,0 as PID,'GROUP' TYPE from T_CONFIG_GROUP where IS_COMMON='1' and _DEL='0' ";
        List list2 = jdbc.queryForList(sql);
        list.addAll(list2);

        ResponseHelper.printOut(response, true, "", list);
    }
    @RequestMapping(value="/saveRelBasic.do",method= RequestMethod.POST)
    public void saveRelBasic(HttpServletRequest request,HttpServletResponse response)
    {
        String rels = request.getParameter("rels");
        String groupId = request.getParameter("groupId");
        String[] relsArr = rels.split(",");
        String sql = "select REL_CONFIG_ID from T_CONFIG where GROUP_ID = "+groupId+" AND REL_CONFIG_ID <> 0";
        List<Map<String, Object>> list = jdbc.queryForList(sql);
        List relsArray = new ArrayList();
        for(Map m:list)
        {
            Integer i = (Integer)m.get("REL_CONFIG_ID");
            relsArray.add(i);
        }
        for(String rel:relsArr)
        {
            if(!relsArray.contains(new Integer(rel))) {
                TConfig config = new TConfig();
                config.setIsBasic("1");
                config.setGroupId(new Integer(groupId));
                config.setIsCommit("0");
                config.setRelConfigId(new Integer(rel));
                configService.saveConfig(config);
            }else
            {
                TConfig config = configService.loadConfig(rel);
                config.set_delete("0");
                configService.saveConfig(config);
            }
        }
        ResponseHelper.printOut(response, true, "", "");
    }

    @RequestMapping(value="/saveAppRel.do",method= RequestMethod.POST)
    public void saveAppRel(HttpServletRequest request,HttpServletResponse response)
    {
        String childAppId = request.getParameter("childAppId");
        String rels = request.getParameter("rels");
        String appId = request.getParameter("appId");
        //先处理分组
        String [] relArr = rels.split(",");
        for(int i=0;i<relArr.length;i+=3)
        {
            String id = relArr[i];
            String type = relArr[i+1];
            String pid = relArr[i+2];
            if(type.equals("GROUP"))
            {
                TConfigGroup group = configService.loadGroup(id);
                String name = group.getGroupName();
                String sql = "select * from T_CONFIG_GROUP where CHILD_APP_ID = "+childAppId+" and GROUP_NAME='"+name+"'";
                List list = jdbc.queryForList(sql);
                if(list.size()==0)
                {
                    TConfigGroup group1 = new TConfigGroup();
                    group1.set_delete("0");
                    group1.setAppId(new Integer(appId));
                    group1.setChildAppId(new Integer(childAppId));
                    group1.setGroupName(name);
                    group1.setIsCommon("0");
                    configService.saveGroup(group1);
                }
            }
        }
        for(int i=0;i<relArr.length;i+=3)
        {
            String id = relArr[i];
            String type = relArr[i+1];
            String pid = relArr[i+2];
            if(type.equals("CONFIG"))
            {
                TConfigGroup group = configService.loadGroup(pid);
                String name = group.getGroupName();
                String sql = "select * from T_CONFIG_GROUP where CHILD_APP_ID = "+childAppId+" and GROUP_NAME='"+name+"'";
                List<Map<String, Object>> list = jdbc.queryForList(sql);
                int gId = (Integer)list.get(0).get("GROUP_ID");



                TConfig config = new TConfig();
                config.setIsBasic("0");
                config.setGroupId(new Integer(gId));
                config.setIsCommit("0");
                TConfig rconfig = configService.loadConfig(id);
                if(rconfig.getRelConfigId()!=0)
                {
                    rconfig = configService.loadConfig(rconfig.getRelConfigId()+"");
                }
                String hql = "from TConfig where chlidAppId = "+childAppId + " and relConfigId = "+rconfig.getConfigId();
                TConfig config2 = configService.findSql(hql);
                if( config2==null ) {
                    config.setRelConfigId(rconfig.getConfigId());
                    config.setAppId(new Integer(appId));
                    config.setChlidAppId(new Integer(childAppId));
                    config.setIsCommit(rconfig.getIsCommit());
                    configService.saveConfig(config);
                }else
                {
                    config2.set_delete("0");
                    config2.setIsCommit(rconfig.getIsCommit());
                    configService.saveConfig(config2);
                }
            }
        }
        ResponseHelper.printOut(response, true, "", "");

    }


    @RequestMapping(value="/listRun.do",method= RequestMethod.POST)
    public void listRun(HttpServletRequest request,HttpServletResponse response)
    {
        String childAppId = request.getParameter("childAppId");
        String appId = request.getParameter("appId");
        if("0".equals(childAppId) || StringUtils.isBlank(childAppId))
        {
            String sql2 = "select * from T_CONFIG_CHILD_APP where APP_ID="+appId + " limit 1";
            List<Map<String, Object>> list = jdbc.queryForList(sql2);
            if(list.size()>0)
            {
                childAppId = list.get(0).get("CHILD_APP_ID").toString();
            }
        }



        String sql = "select * from T_CONFIG_RUN where CHILD_APP_ID = "+childAppId;
        List list = jdbc.queryForList(sql);
        Map rst = new HashMap();
        rst.put("list",list);
        rst.put("childAppId",childAppId);
        rst.put("appId",appId);
        String sql2 = "select * from T_CONFIG_CHILD_APP where APP_ID="+appId + "";
        List<Map<String, Object>> list2 = jdbc.queryForList(sql2);

        sql = "select * from T_APP where APP_ID = "+appId+"";
        List<Map<String, Object>> list5 = jdbc.queryForList(sql);
        String appCode = "";
        if(list5.size()>0)
        {
            appCode = (String)list5.get(0).get("APP_CODE");
        }

        for(Map m :list2)
        {
            if(m.get("CHILD_APP_ID").toString().equals(childAppId))
            {
                m.put("selected",true);
            }else
            {
                m.put("selected",false);
            }
        }
        rst.put("childApps",list2);
        rst.put("appCode",appCode);
        ResponseHelper.printOut(response, true, "", rst);
    }

    @RequestMapping(value="/saveRun.do",method= RequestMethod.POST)
    public void saveRun(HttpServletRequest request,HttpServletResponse response)
    {
        String childAppId = request.getParameter("childAppId");
        String appId = request.getParameter("appId");
        String runKey = request.getParameter("runKey");
        String xtcs = request.getParameter("xtcs");

        String sql = "select count(*) from T_CONFIG_RUN where APP_ID = "+appId+" " +
                "and RUN_KEY='"+runKey+"'";
        int i = jdbc.queryForInt(sql);
        if(i>0)
        {
            throw new RuntimeException("KEY重复");
        }

        TConfigRun run = new TConfigRun();
        run.setAppId(new Integer(appId));
        run.setChildAppId(new Integer(childAppId));
        run.setRunKey(runKey);
        run.setBswz(xtcs);
        configService.saveRun(run);
        ResponseHelper.printOut(response, true, "", "");
    }


    /**
     * 列出分组
     * @param model
     * @param request
     * @param response
     * @throws Exception
     */
    @RequestMapping(value="/listRunVal.do",method= RequestMethod.POST)
    public void listRunVal(Model model,HttpServletRequest request,HttpServletResponse response)
    {
        String childAppId = request.getParameter("childAppId");
        String runId = request.getParameter("runId");

        String sql = "select t3.GROUP_ID,t3.GROUP_NAME," +
                "t2.CON_KEY,t2.CON_DESC,t2.ATT,t2.DEFAULT_VALUE,t2.IS_COMMIT," +
                "t2.CONFIG_ID,t2.REL_CONFIG_ID,t2._DEL from " +
                "T_CONFIG t2,T_CONFIG_GROUP t3 " +
                "where t2.GROUP_ID=t3.GROUP_ID and  t2._DEL='0' " +
                "and t2.IS_COMMIT = '1'  " +
                "and t2.CHLID_APP_ID = "+childAppId;

        List<Map<String, Object>> list = jdbc.queryForList(sql);

        Map cache = new HashMap();
        List result = new ArrayList();
        for(Map row:list)
        {

            String groupName = (String)row.get("GROUP_NAME");
            String groupId = row.get("GROUP_ID").toString();
            JSONObject jsonObject = null;
            if(cache.get(groupId)==null) {
                jsonObject = new JSONObject();
                jsonObject.put("groupName", groupName);
                jsonObject.put("groupId", groupId);
                jsonObject.put("configs", new JSONArray());
                result.add(jsonObject);
                cache.put(groupId,jsonObject);
            }else
            {
                jsonObject = (JSONObject)cache.get(groupId);
            }

            String key = (String)row.get("CON_KEY");
            Integer rel = (Integer)row.get("REL_CONFIG_ID");
            row.put("IS_REL",false);

            if(rel!=null && rel!=0)
            {
                String sql2 = "select t1.GROUP_ID,t1.GROUP_NAME," +
                        "t2.CON_KEY,t2.CON_DESC,t2.ATT,t2.DEFAULT_VALUE,t2.IS_COMMIT," +
                        "t2.CONFIG_ID,t2.REL_CONFIG_ID from T_CONFIG_GROUP  t1 " +
                        "left join T_CONFIG t2 on t1.GROUP_ID = t2.GROUP_ID  " +
                        "where   " +" CONFIG_ID="+rel;
                List<Map<String, Object>> list2 = jdbc.queryForList(sql2);
                if(list2.size()>0)
                {
                    Map row2 = list2.get(0);
                    row2.put("CONFIG_ID",row.get("CONFIG_ID"));
                    row = row2;
                    key = (String)row.get("CON_KEY");
                    row.put("IS_REL",true);
                }
            }
            String defaultVal = (String) row.get("DEFAULT_VALUE");
            String sql2 = "select VAL from T_CONFIG_RUN_VAL where CONFIG_ID = "+row.get("CONFIG_ID")+" and RUN_ID = "+runId;
            List<Map<String, Object>> list2 = jdbc.queryForList(sql2);
            if(list2.size()>0)
            {
                String val = (String)list2.get(0).get("VAL");
                defaultVal = val;
            }
            row.put("DEFAULT_VALUE",defaultVal);

            if(!StringUtils.isBlank(key))
            {
                JSONArray array = jsonObject.getJSONArray("configs");
                String iscommit = (String)row.get("IS_COMMIT");
                if("1".equals(iscommit))
                {
                    row.put("commit_color","green");
                }else
                {
                    row.put("commit_color","red");
                }
                String att = (String)row.get("ATT");
                if(!StringUtils.isBlank(att))
                {
                    if("1".equals(att))
                    {
                        row.put("att_label","只读");
                        //row.put("default_value_color","#C9C9C9");
                    }else
                    {
                        row.put("att_label","可覆盖");
                        //row.put("default_value_color","#000");
                    }
                }
                if((Boolean)row.get("IS_REL"))
                {
                    row.put("rel_color","#C9C9C9");
                }else
                {
                    row.put("rel_color","#000");
                }
                array.add(row);
            }
        }

        Map rst = new HashMap();
        rst.put("configlist",result);
        rst.put("runId",runId);
        ResponseHelper.printOut(response, true, "", rst);

    }

    /**
     * 列出分组
     * @param model
     * @param request
     * @param response
     * @throws Exception
     */
    @RequestMapping(value="/saveRunVal.do",method= RequestMethod.POST)
    public void saveRunVal(Model model,HttpServletRequest request,HttpServletResponse response)
    {
        String runId = request.getParameter("runId");
        String configId = request.getParameter("configId");
        String val = request.getParameter("val");
        configService.saveRunVal(runId,configId,val);
        ResponseHelper.printOut(response, true, "", "");
    }

    @RequestMapping(value="/getRunVal.do",method= RequestMethod.POST)
    public void getRunVal(Model model,HttpServletRequest request,HttpServletResponse response)
    {
        String runId = request.getParameter("runId");
        String configId = request.getParameter("configId");
        String v = getVal(runId,configId);
        ResponseHelper.printOut(response, true, "", v);
    }

    private String getVal(String runId,String configId)
    {
        TConfigRunVal val = configService.getRunVal(runId, configId);
        if(val!=null)
        {
            return val.getVal();
        }
        TConfig config = configService.loadConfig(configId);
        Integer rel = config.getRelConfigId();
        if(rel!=null && rel!=0)
        {
            config = configService.loadConfig(rel+"");
        }
        return config.getDefaultValue();
    }

    @RequestMapping(value="/getConfig.do",method= RequestMethod.GET)
    public void getConfig(Model model,HttpServletRequest request,HttpServletResponse response)
    {
        String appKey = request.getParameter("appCode");
        String runKey = request.getParameter("runCode");
        String sql = "select * from T_APP where APP_CODE = '"+appKey+"'";
        List<Map<String, Object>> list = jdbc.queryForList(sql);
        if(list.size()==0)
        {
            ResponseHelper.printOut(response, true, appKey+"不存在", "");
            return;
        }
        Integer appId = (Integer)list.get(0).get("APP_ID");

        sql = "select RUN_ID,CHILD_APP_ID from T_CONFIG_RUN where APP_ID = "+appId+" " +
                "and RUN_KEY='"+runKey+"'";
        list = jdbc.queryForList(sql);
        if(list.size()==0)
        {
            ResponseHelper.printOut(response, true, runKey+"不存在", "");
            return;
        }
        Integer runId = (Integer)list.get(0).get("RUN_ID");
        Integer childAppId = (Integer)list.get(0).get("CHILD_APP_ID");

        sql = "select t3.GROUP_ID,t3.GROUP_NAME," +
                "t2.CON_KEY,t2.CON_DESC,t2.ATT,t2.DEFAULT_VALUE,t2.IS_COMMIT," +
                "t2.CONFIG_ID,t2.REL_CONFIG_ID,t2._DEL from " +
                "T_CONFIG t2,T_CONFIG_GROUP t3 " +
                "where t2.GROUP_ID=t3.GROUP_ID and  t2._DEL='0' " +
                "and t2.IS_COMMIT = '1'  " +
                "and t2.CHLID_APP_ID = "+childAppId;

        list = jdbc.queryForList(sql);

        List result = new ArrayList();
        for(Map row:list)
        {


            String key = (String)row.get("CON_KEY");
            Integer rel = (Integer)row.get("REL_CONFIG_ID");
            row.put("IS_REL",false);

            if(rel!=null && rel!=0)
            {
                String sql2 = "select t1.GROUP_ID,t1.GROUP_NAME," +
                        "t2.CON_KEY,t2.CON_DESC,t2.ATT,t2.DEFAULT_VALUE,t2.IS_COMMIT," +
                        "t2.CONFIG_ID,t2.REL_CONFIG_ID from T_CONFIG_GROUP  t1 " +
                        "left join T_CONFIG t2 on t1.GROUP_ID = t2.GROUP_ID  " +
                        "where   " +" CONFIG_ID="+rel;
                List<Map<String, Object>> list2 = jdbc.queryForList(sql2);
                if(list2.size()>0)
                {
                    Map row2 = list2.get(0);
                    row2.put("CONFIG_ID",row.get("CONFIG_ID"));
                    row = row2;
                    key = (String)row.get("CON_KEY");
                    row.put("IS_REL",true);
                }
            }

            String defaultVal = (String) row.get("DEFAULT_VALUE");
            String sql2 = "select VAL from T_CONFIG_RUN_VAL where CONFIG_ID = "+row.get("CONFIG_ID")+ " and RUN_ID = "+runId ;
            List<Map<String, Object>> list2 = jdbc.queryForList(sql2);
            if(list2.size()>0)
            {
                String val = (String)list2.get(0).get("VAL");
                defaultVal = val;
            }
            row.put("DEFAULT_VALUE",defaultVal);

            JSONObject obj = new JSONObject();
            obj.put("code",key);
            obj.put("value",defaultVal);
            obj.put("desc",row.get("CON_DESC"));
            result.add(obj);
        }
        ResponseHelper.printOut(response, true, "", result);

    }

    @RequestMapping(value="/exportConfig.do",method= RequestMethod.GET)
    public void export(Model model,HttpServletRequest request,HttpServletResponse response)
            throws Exception
    {
        //app
        String appId = request.getParameter("appId");
        String sql = "select * from T_APP where APP_ID="+appId;
        List list = jdbc.queryForList(sql);
        Map app = (Map)list.get(0);
        //childApp
        sql = "select * from T_CONFIG_CHILD_APP where APP_ID="+appId;
        List childApps = jdbc.queryForList(sql);
        //T_CONFIG_GROUP
        sql = "select * from T_CONFIG_GROUP where (APP_ID = "+appId+" or  IS_COMMON = 1)";
        List groups = jdbc.queryForList(sql);
        //T_CONFIG
        sql = "select * from T_CONFIG where (APP_ID = "+appId+" or  IS_BASIC = 1)";
        List configs= jdbc.queryForList(sql);

        Map rst = new HashMap();
        rst.put("app",app);
        rst.put("childApp",childApps);
        rst.put("group",groups);
        rst.put("config",configs);

        String str = JSON.toJSONString(rst);

        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "attachment;"
                + " filename="+new String(("config.txt").getBytes("UTF-8"), "ISO8859-1"));
        response.getOutputStream().write(str.getBytes("UTF-8"));
    }

    @RequestMapping(value="/importConfig.do",method= RequestMethod.POST)
    public void importConfig(Model model,HttpServletRequest request,
                             HttpServletResponse response)throws Exception
    {
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest)request;
        // 获得文件
        MultipartFile imgFile  =  multipartRequest.getFile("file");
        byte[] bytes = imgFile.getBytes();
        String str = new String(bytes,"UTF-8");
        Map strMap = JSON.parseObject(str,Map.class);
        Map app = (Map)strMap.get("app");
        //处理APP
        Integer appId = (Integer)app.get("APP_ID");
        String appName = tranSqlVal((String)app.get("APP_NAME"));
        String appCode = tranSqlVal((String)app.get("APP_CODE"));
        String createTime = tranSqlVal((String)app.get("CREATE_TIME"));
        String dbs = tranSqlVal((String)app.get("DBS"));

        String sql = "select count(*) from T_APP where APP_ID="+appId;
        int size=jdbc.queryForInt(sql);
        if(size == 0)
        {
            sql = "insert into T_APP set APP_ID= "+appId+",APP_NAME="+appName+",APP_CODE="+appCode+
                    ",DBS="+dbs+",CREATE_TIME="+createTime;
        }else
        {
            sql = "update T_APP set APP_NAME="+appName+",APP_CODE="+appCode+
                    ",DBS="+dbs+",CREATE_TIME="+createTime+" where APP_ID = "+appId;
        }
        jdbc.execute(sql);
        updateAuto("T_APP","APP_ID");

        //childApp
        List<Map> childApp = (List)strMap.get("childApp");
        for(Map map:childApp)
        {
            Integer childAppId = (Integer)map.get("CHILD_APP_ID");
            Integer _appId = (Integer)map.get("APP_ID");
            String childAppName = tranSqlVal((String)map.get("CHILD_APP_NAME"));
            sql = "select count(*) from T_CONFIG_CHILD_APP where CHILD_APP_ID="+childAppId;
            size=jdbc.queryForInt(sql);
            if(size == 0)
            {
                sql = "insert into T_CONFIG_CHILD_APP set  CHILD_APP_ID = "+childAppId +
                ",CHILD_APP_NAME="+childAppName+
                ",APP_ID="+_appId;
            }else
            {
                sql = "update  T_CONFIG_CHILD_APP set  "+
                        "CHILD_APP_NAME="+childAppName+
                        ",APP_ID="+_appId+" where CHILD_APP_ID = "+childAppId;
            }
            jdbc.execute(sql);
        }
        updateAuto("T_CONFIG_CHILD_APP","CHILD_APP_ID");

        //T_CONFIG_GROUP
        List<Map> group = (List)strMap.get("group");
        for(Map map:group)
        {
            Integer groupId = (Integer)map.get("GROUP_ID");
            Integer _appId = (Integer)map.get("APP_ID");
            Integer childAppId = (Integer)map.get("CHILD_APP_ID");
            String groupName = tranSqlVal((String)map.get("GROUP_NAME"));
            String isCommon = tranSqlVal((String)map.get("IS_COMMON"));
            String del = tranSqlVal((String)map.get("_DEL"));
            sql = "select count(*) from T_CONFIG_GROUP where GROUP_ID="+groupId;
            size=jdbc.queryForInt(sql);
            if(size == 0)
            {
                sql = "insert into T_CONFIG_GROUP set "+
                        "GROUP_ID="+groupId+","+
                        "APP_ID="+_appId+","+
                        "CHILD_APP_ID="+childAppId+","+
                        "GROUP_NAME="+groupName+","+
                        "IS_COMMON="+isCommon+","+
                        "_DEL="+del;
            }else
            {
                sql = "update T_CONFIG_GROUP set "+
                        "APP_ID="+_appId+","+
                        "CHILD_APP_ID="+childAppId+","+
                        "GROUP_NAME="+groupName+","+
                        "IS_COMMON="+isCommon+","+
                        "_DEL="+del+" where GROUP_ID = "+groupId;
            }
            jdbc.execute(sql);
        }
        updateAuto("T_CONFIG_GROUP","GROUP_ID");

        //T_CONFIG
        List<Map> configs = (List)strMap.get("config");
        for(Map map :configs)
        {
            Integer configId = (Integer)map.get("CONFIG_ID");
            Integer _appId = (Integer)map.get("APP_ID");
            Integer childAppId = (Integer)map.get("CHLID_APP_ID");
            Integer groupId = (Integer)map.get("GROUP_ID");
            String conKey = tranSqlVal((String)map.get("CON_KEY"));
            String isBasic = tranSqlVal((String)map.get("IS_BASIC"));
            Integer relConfigId = ((Integer)map.get("REL_CONFIG_ID"));
            String defaultValue = tranSqlVal((String)map.get("DEFAULT_VALUE"));
            String isCommit = tranSqlVal((String)map.get("IS_COMMIT"));
            String att = tranSqlVal((String)map.get("ATT"));
            String conDesc = tranSqlVal((String)map.get("CON_DESC"));
            String del = tranSqlVal((String)map.get("_DEL"));

            sql = "select count(*) from T_CONFIG where CONFIG_ID="+configId;
            size=jdbc.queryForInt(sql);
            if(size == 0)
            {
                sql = "insert into T_CONFIG set "+
                        "CONFIG_ID="+configId+","+
                        "APP_ID="+_appId+","+
                        "CHLID_APP_ID="+childAppId+","+
                        "GROUP_ID="+groupId+","+
                        "CON_KEY="+conKey+","+
                        "IS_BASIC="+isBasic+","+
                        "REL_CONFIG_ID="+relConfigId+","+
                        "DEFAULT_VALUE="+defaultValue+","+
                        "IS_COMMIT="+isCommit+","+
                        "ATT="+att+","+
                        "CON_DESC="+conDesc+","+
                        "_DEL="+del;
            }else
            {
                sql = "update  T_CONFIG set "+
                        "APP_ID="+_appId+","+
                        "CHLID_APP_ID="+childAppId+","+
                        "GROUP_ID="+groupId+","+
                        "CON_KEY="+conKey+","+
                        "IS_BASIC="+isBasic+","+
                        "REL_CONFIG_ID="+relConfigId+","+
                        "IS_COMMIT="+isCommit+","+
                        "ATT="+att+","+
                        "CON_DESC="+conDesc+","+
                        "_DEL="+del +" where CONFIG_ID = "+configId;
            }
            jdbc.execute(sql);
        }
        updateAuto("T_CONFIG","CONFIG_ID");

        ResponseHelper.printOut(response, true, "", "");

    }

    private String tranSqlVal(Object o)
    {
        if(o == null)
        {
            return "NULL";
        }
        if(o instanceof Integer)
        {
            return o.toString();
        }

        return "'"+ o.toString() +"'";

    }

    private void updateAuto(String table,String key)
    {
        String sql = "select MAX("+key+") from "+table;
        int max = jdbc.queryForInt(sql);
        sql = "alter table "+table+" AUTO_INCREMENT="+(max+1);
        jdbc.execute(sql);
    }





}
