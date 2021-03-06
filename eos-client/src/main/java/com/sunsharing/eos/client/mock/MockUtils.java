package com.sunsharing.eos.client.mock;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.sunsharing.eos.client.sys.EosClientProp;
import com.sunsharing.eos.common.rpc.RpcException;
import com.sunsharing.eos.common.zookeeper.PathConstant;
import com.sunsharing.eos.common.zookeeper.ZookeeperUtils;
import org.apache.log4j.Logger;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by criss on 16/1/14.
 */
public class MockUtils {

    Logger logger  = Logger.getLogger(MockUtils.class);
    static ScriptEngineManager mgr = new ScriptEngineManager();
    static ScriptEngine engine = mgr.getEngineByName("JavaScript");

    private static String evalStr(String a) throws Exception {
        Object o = engine.eval(a);
        if (o == null) {
            return "";
        } else {
            return o.toString();
        }
    }
    /**
     * 获取模拟数据
     * @return
     */
    public String transMockMatch(String appId,String serviceId,String methodVersion,String method,String mockName,Map params) throws RpcException
    {
        try {
            JSONArray array = getTestCode(appId, serviceId,
                    methodVersion,method);
            if(array==null)
            {
                String error = "服务接口" + appId + "-"
                        + serviceId + "-"
                        + method + "-"
                        + methodVersion + "没有审批通过";
                logger.error(error);
                throw new RpcException(RpcException.MOCK_EXCEPTION, error);
            }
            if (array != null) {
                //先走入参匹配
                for (int i = 0; i < array.size(); i++) {
                    JSONObject jo = array.getJSONObject(i);
                    String status = jo.getString("status");
                    if(status.indexOf("&&") != -1 ||
                            status.indexOf("==") != -1 ||
                            status.indexOf("||") != -1) {
                        //替换，如果${}没有前后的'"自动加上。

//                        try {
//                            DynamicString ds = new DynamicString(status);
//                            status = ds.convert(params);
//                        }catch (Exception e)
//                        {
//                            logger.error("模拟参数转换参数报错，参数信息："+status,e);
//                            throw new RuntimeException("模拟参数转换参数报错，参数信息："+status);
//                        }
                        String vars = "";
                        for(Iterator iter = params.keySet().iterator();
                                iter.hasNext();)
                        {
                            String param = (String)iter.next();
                            String value = (String)params.get(param);
                            if(!(value.startsWith("{") || value.startsWith("[") ))
                            {
                                value = "\""+value+"\"";
                            }
                            vars+=" var "+param+"="+value+"; ";
                        }
                        status = vars +status;

                    }
                    try {
                        if (status.indexOf("&&") != -1 || status.indexOf("==") != -1 || status.indexOf("||") != -1) {
                            if ("true".equals(evalStr(status))) {
                                String content = jo.getString("content");
                                return chineseReplace(content);
                            }
                        }
                    }catch (Exception e)
                    {
                        logger.error("模拟参数JS处理报错，参数信息："+status,e);
                        throw new RuntimeException("模拟参数JS处理报错，参数信息："+status);
                    }


                }
                //再走名字匹配
                for (int i = 0; i < array.size(); i++) {
                    JSONObject jo = array.getJSONObject(i);
                    String status = jo.getString("status");
                    //走原来的模式
                    if (mockName.equals(jo.getString("status"))) {
                        String content = jo.getString("content");
                        return chineseReplace(content);
                    }
                }

            }
                String error = "服务接口" + appId + "-"
                        + serviceId + "-"
                        + methodVersion + "-"
                        + method + "没有配置指定的mock:" + mockName+"或者入参无法匹配";
                logger.error(error);
                throw new RpcException(RpcException.MOCK_EXCEPTION, error);
        } catch (Exception e) {
            String error = "获取模拟测试值异常！" + appId + "-"
                    + serviceId + "-"
                    + methodVersion + "-"
                    + method + "-"
                    + mockName+",错误信息:"+e.getMessage();
            logger.error(error, e);
            throw new RpcException(RpcException.MOCK_EXCEPTION, error);
        }

    }

    /**
     * 获得测试代码
     * @param appId
     * @param serviceId
     * @param version
     * @param method 方法名
     * @return
     * [{"content":"{\"error\":\"错误了3\"}","desc":"当入参为其他时为错误输出","status":"error"},
     * {"content":"{\"success\":\"成功了2\",\"haha\":\"haha2\"}","desc":"当入参name=\"criss\"为成功输出",
     * "status":"success"}]
     */
    private JSONArray getTestCode(String appId,String serviceId,String version,String method)throws Exception
    {
        logger.info("appId:"+appId);
        logger.info("serviceId:"+serviceId);
        logger.info("version:"+version);
        logger.info("method:"+method);

        ZookeeperUtils utils = ZookeeperUtils.getInstance();
        if(utils.isExists(PathConstant.ACL+"/"+appId+serviceId+method+version,false))
        {
            String obj = new String(utils.getData(PathConstant.ACL+"/"+appId+serviceId+method+version,false),"UTF-8");
            logger.info("obj:"+obj);
            return JSONArray.parseArray(obj);
        }
        return null;
    }

    public String chineseReplace(String content)
    {
        return content.replaceAll("，",",").replaceAll("“","\"").replaceAll("：",":");
    }

    public static void main(String[]a) throws Exception
    {
//        String abc = JSON.toJSONString("{\"abc\":\"hehe\"}");
//        //System.out.println(abc);
//        String script = " var eos_appid=\"test\";  var eos_method_name=\"getAppGroup\";  var favMenuFlag=\"1\";  var eos_service_id=\"userAppMenus\";  " +
//                "var eos_mock=\"two\";  var eos_version=\"1.1\"; favMenuFlag == 1";
//        Object o = MockUtils.evalStr(script);
//        if (o == null) {
//            // "";
//        } else {
//            System.out.println(o.toString());
//        }


        System.out.println("{“：，,}".replaceAll("，", ",").replaceAll("“","\""));
    }
}
