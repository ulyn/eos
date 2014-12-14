package com.sunsharing.eos.clientproxy.ws;

import org.apache.axis.client.Call;
import org.apache.axis.client.Service;
import org.apache.axis.description.OperationDesc;

import javax.xml.namespace.QName;
import java.util.HashMap;
import java.util.Map;

public class CallWs {

    /**
     * axis调用cxf  -- 供调用协同平台的使用
     *
     * @param url        入参
     * @param requestXml 入参
     * @return
     * @author: ulyn
     */
    public static String requestToAdapter(String url, String requestXml) throws Exception {
        Map paramMap = new HashMap();
        paramMap.put("nameObjs", new Object[]{"msg"});
        paramMap.put("paramOjbs", new Object[]{requestXml});
        return requestToCxf(url, "synRequest", paramMap, "http://service.cobweb.sunsharing.com/");
    }

    /**
     * axis调用cxf，返回String字符串类型的数据
     *
     * @param endpointURL   调用全局参数的WSDL地址
     * @param operationName 方法名
     * @param paramMap      参数列表｛nameObjs，paramOjbs｝
     * @param qName
     * @return
     * @author:ulyn
     */
    public static String requestToCxf(String endpointURL, String operationName, Map paramMap, String qName) throws Exception {
        String string = null;
        if (endpointURL.toLowerCase().endsWith("?wsdl")) {
            endpointURL = endpointURL.substring(0, endpointURL.length() - 5);
        }

        Service service = new Service();
        Call call = (Call) service.createCall();
        call.setTargetEndpointAddress(new java.net.URL(endpointURL));
        call.setOperationName(operationName);
        call.setUseSOAPAction(true);

        Object[] nameObjs = paramMap.get("nameObjs") == null ? null : (Object[]) paramMap.get("nameObjs");
        Object[] paramOjbs = paramMap.get("paramOjbs") == null ? null : (Object[]) paramMap.get("paramOjbs");

        OperationDesc oper = new OperationDesc();
        if (nameObjs != null && nameObjs.length > 0) {
            for (int i = 0; i < nameObjs.length; i++) {
                oper.addParameter(new QName(qName, (String) nameObjs[i]),
                        new QName("http://www.w3.org/2001/XMLSchema", "string"),
                        String.class, org.apache.axis.description.ParameterDesc.IN, false, false);
            }
        }
        oper.setReturnType(new QName("http://www.w3.org/2001/XMLSchema", "string"));
        call.setOperation(oper);

        //响应超时为40秒
        call.setTimeout(new Integer(40000));

        Object object = call.invoke(paramOjbs);
        string = (String) object;

        return string;
    }

    /**
     * 调用wsdl
     *
     * @param endpointURL
     * @param operAtion
     * @param obj
     * @return
     * @throws Exception
     */
    public static Object send(String endpointURL, String operAtion, Object[] obj) throws CallWsException {
        try {
            Object object = null;

            Service service = new Service();
            Call call = (Call) service.createCall();
            call.setTargetEndpointAddress(new java.net.URL(endpointURL));
            call.setOperationName(operAtion);

            call.setUseSOAPAction(true);
            //        call.setSOAPActionURI("http://tempuri.org/Authorization");
            //响应超时为40秒
            call.setTimeout(new Integer(40000));

            object = call.invoke(obj);

            return object;
        } catch (Exception e) {
            throw new CallWsException(e);
        }
    }

    /**
     * @param args
     * @author: ulyn
     */
    public static void main(String[] args) {
        String rcbw = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                "<Request>" +
                "	<SenderId>XM.GOV.SQ.RS.ZX.RKJTGX</SenderId>" +
                "	<ServiceId>XM.GOV.YZ.ZX.RKJTGX</ServiceId>" +
                "	<Inputs>" +
                "		<Input name=\"arg0\" type=\"string\">3505241988101315341</Input>" +
                "	</Inputs>" +
                "</Request>";
        try {
//            String s = CallWs.requestToAdapter("http://192.168.0.143:10006/request",rcbw);
//            System.out.println(s);
//           Object o = CallWs.send("http://192.168.0.224:11111/FwglService.asmx","Authorization",new Object[]{"1","1"});
            Object o = CallWs.send("http://192.168.100.176:9080/PPCS/services/SoakageService", "query",
                    new Object[]{"padmin", "123456", "4028802e3f08c7d3013f178e022e0006", "SFZH='1340102194209132018'"
                            , "<ROOT><BASIC_INFO><SERVICE_TYPE>查询目的</SERVICE_TYPE></BASIC_INFO><EXT_INFO/></ROOT>"});
            System.out.println(o);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
