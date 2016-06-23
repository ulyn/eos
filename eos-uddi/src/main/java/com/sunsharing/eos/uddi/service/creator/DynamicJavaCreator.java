/**
 * @(#)DynamicJavaCreator
 * 版权声明 厦门畅享信息技术有限公司, 版权所有 违者必究
 *
 *<br> Copyright:  Copyright (c) 2016
 *<br> Company:厦门畅享信息技术有限公司
 *<br> @author ulyn
 *<br> 16-6-22 下午4:27
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
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.sunsharing.eos.common.utils.StringUtils;
import com.sunsharing.eos.uddi.model.TApp;
import com.sunsharing.eos.uddi.model.TMethod;
import com.sunsharing.eos.uddi.model.TService;
import com.sunsharing.eos.uddi.model.TServiceVersion;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import java.io.File;
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
public class DynamicJavaCreator implements ICreator {

    Logger logger = Logger.getLogger(getClass());

    @Override
    public File create(String fileDir,TApp app, String v, List<TService> services) throws Exception{
        for (TService service : services) {
            createServiceFile(service, app, fileDir);
        }
        return new File(fileDir);
    }

    private void createServiceFile(TService service, TApp app, String path) throws Exception {
        String className = serviceCode2ClassName(service.getServiceCode());
        String file = path + "/" + className + ".java";
        TServiceVersion serviceVersion = service.getVersions().get(0);
        StringBuilder sb = new StringBuilder("/** \n");
        sb.append("* " + service.getModule() + " - " + service.getServiceName() + " \n");
        sb.append("* " + service.getServiceCode() + " - " + serviceVersion.getVersionId() + " \n*/\n");

        StringBuilder importSb = new StringBuilder();
        importSb.append("import com.sunsharing.eos.client.rpc.DynamicRpc;\n");
        importSb.append("import com.sunsharing.eos.client.sys.EosClientProp;\n");
        importSb.append("import com.sunsharing.eos.common.ServiceRequest;\n");

        resolveImports(serviceVersion,importSb);

        sb.append(importSb).append("\n");

        sb.append("public class AppUserBinder {\n" +
                "\n" +
                "    private final String appId = \""+ app.getAppCode() +"\";\n" +
                "    private final String serviceId = \""+ service.getServiceCode() +"\";\n" +
                "\n");
        for (TMethod method : serviceVersion.getMethods()) {
            if (!StringUtils.isBlank(method.getMockResult())) {
                //注释部分
                JSONArray methodMockResult = JSON.parseArray(method.getMockResult());
                if (methodMockResult.size() > 0) {
                    sb.append("   /**\n");
                    sb.append("    * @return\n");
                    for (int i = 0, l = methodMockResult.size(); i < l; i++) {
                        JSONObject jo = methodMockResult.getJSONObject(i);
                        sb.append("    *  ${");
                        sb.append(jo.getString("status"));
                        sb.append("}  " + jo.getString("desc") + "\n");
                        sb.append("    *     ");
                        sb.append(jo.getString("content"));
                        sb.append("\n");
                    }
                    sb.append("    */\n");
                }
            }
            String methodName = method.getMethodName();
            if (methodName.startsWith("void ")) {
                methodName = methodName.replace("void ", "");
            }

            String paramsStr = method.getParams();
            ParamsResolver.InOutParameter inOutParameter = ParamsResolver.toInOutParams(paramsStr);

            sb.append("    public "+ inOutParameter.getOutType() +" "+ methodName +"(");

            for(int i=0;i< inOutParameter.getInParameters().size();i++){
                if(i!=0){
                    sb.append(",");
                }
                ParamsResolver.InParameter inParameter = inOutParameter.getInParameters().get(i);
                sb.append(inParameter.getType());
                sb.append(" ");
                sb.append(inParameter.getName());
            }
            sb.append(") {\n" +
                    "        ServiceRequest.Builder builder = new ServiceRequest.Builder(\n" +
                    "                appId,serviceId,\""+ methodName +"\",\""+ method.getMethodVersion() +"\");\n");
            for(int i=0;i< inOutParameter.getInParameters().size();i++){
                ParamsResolver.InParameter inParameter = inOutParameter.getInParameters().get(i);
                sb.append("        builder.setParameter(\""+ inParameter.getName() +"\","+ inParameter.getName() +");\n" );
            }
            String outType = inOutParameter.getOutType();
            if(outType.lastIndexOf(" ")!=-1){
                outType = outType.substring(outType.lastIndexOf(" "));
            }
            sb.append("        return DynamicRpc.invoke(builder.build(), " + outType + ".class);\n" +
                    "    }");
        }

        sb.append(        "\n" +
                "\n" +
                "}");


        FileUtils.writeStringToFile(new File(file), sb.toString(), "utf-8");
    }

    private void resolveImports(TServiceVersion serviceVersion, StringBuilder importSb) {
        //不解析了 简单导入
        importSb.append("import java.util.*;\n");
        importSb.append("import com.alibaba.fastjson.*;\n");
    }

    private String serviceCode2ClassName(String serviceCode) {
        if(serviceCode.length() == 1){
            return serviceCode.toUpperCase();
        }
        return serviceCode.substring(0,1).toUpperCase() + serviceCode.substring(1);
    }


}

