/**
 * @(#)ReduxJSCreator
 * 版权声明 厦门畅享信息技术有限公司, 版权所有 违者必究
 *
 *<br> Copyright:  Copyright (c) 2016
 *<br> Company:厦门畅享信息技术有限公司
 *<br> @author ulyn
 *<br> 16-6-22 下午4:28
 *<br> @version 1.0
 *————————————————————————————————
 *修改记录
 *    修改者：
 *    修改时间：
 *    修改原因：
 *————————————————————————————————
 */
package com.sunsharing.eos.uddi.service.creator;

import com.sunsharing.eos.common.utils.StringUtils;
import com.sunsharing.eos.uddi.model.TApp;
import com.sunsharing.eos.uddi.model.TMethod;
import com.sunsharing.eos.uddi.model.TService;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
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
public class ReduxJSCreator implements ICreator {
    @Override
    public File create(String fileDir,TApp app, String v, List<TService> services) throws Exception{
        createPackageJsonFile(app, v, fileDir);
        createReadme(app, v, fileDir, services);
        createAppJsx(app, fileDir);
        createIndexJsx(services, fileDir);

        createServiceFile(fileDir, services);

        return new File(fileDir);
    }

    private void createServiceFile(String fileDir, List<TService> services) throws IOException {
        StringBuilder sb = new StringBuilder();
        for(TService service : services){
            sb.append("import { methodConstName, getMethodDispatch } from 'zeus-eos-middleware';\n" +
                    "import { APP_ID } from './app';\n" +
                    "\n" +
                    "export const SERVICE_ID = \""+ service.getServiceCode() +"\";\n" +
                    "\n" +
                    "export const serviceEos = {\n" +
                    "    appId : APP_ID,\n" +
                    "    serviceId : SERVICE_ID\n" +
                    "};\n\n");
            for(TMethod method : service.getVersions().get(0).getMethods()){
                String dispatchConstName = toConstName(method.getMethodName());
                ParamsResolver.InOutParameter inOutParameter = ParamsResolver.toInOutParams(method.getParams());
                List<ParamsResolver.InParameter> inParameters = inOutParameter.getInParameters();
                String paramstr = "";
                for(ParamsResolver.InParameter p : inParameters){
                    paramstr += p.getName() + ",";
                }
                String datastr = paramstr;
                if(!StringUtils.isBlank(datastr)){
                    datastr = datastr.substring(0,datastr.length() - 1);
                    paramstr += "mock";
                }else{
                    paramstr = "mock";
                }
                 sb.append("export const "+ dispatchConstName +" =  methodConstName(serviceEos, '"+ method.getMethodName() +"');\n" +
                         "export function "+ method.getMethodName() +"("+ paramstr +") {\n" +
                         "    return getMethodDispatch("+ dispatchConstName +", serviceEos, {\n" +
                         "        method: '"+ method.getMethodName() +"',\n" +
                         "        version: '"+ method.getMethodVersion() +"',\n" +
                         "        data: { "+ datastr +" }\n" +
                         "    }, mock);\n" +
                         "}\n\n");
            }
            FileUtils.writeStringToFile(new File(fileDir + "/" + service.getServiceCode() + ".jsx"), sb.toString(), "utf-8");
        }
    }

    private String toConstName(String methodName) {
        return ParamsResolver.underscoreName(methodName).toUpperCase();
    }

    private void createAppJsx(TApp app, String fileDir) throws IOException {
        String str = "export const APP_ID = \""+ app.getAppCode() +"\";";
        FileUtils.writeStringToFile(new File(fileDir + "/app.jsx"), str, "utf-8");
    }

    private void createIndexJsx(List<TService> services, String fileDir) throws IOException {
        StringBuilder sb = new StringBuilder();
        for(TService service : services){
            sb.append("import * as " + service.getServiceCode() + " from \"./" + service.getServiceCode() + "\";\n" +
                "export const "+ service.getServiceCode() +"Action = "+ service.getServiceCode() +";\n\n");
        }
        FileUtils.writeStringToFile(new File(fileDir + "/index.jsx"), sb.toString(), "utf-8");
    }

    private void createReadme(TApp app, String v, String fileDir, List<TService> services) throws IOException {
        String str = "# " + app.getAppName() + "\n" +
                "zeus eos应用 提供给redux的action： " + app.getAppName() + " 应用   \n" +
                "    \n" +
                "# 使用\n" +
                "\n" +
                "\n" +
                "## 接口调用说明\n" +
                "\n" +
                "[项目中如何调用 eos 服务](http://192.168.0.62:88/components/zeus-eos-middleware#%E9%A1%B9%E7%9B%AE%E4%B8%AD%E5%A6%82%E4%BD%95%E8%B0%83%E7%94%A8-eos-%E6%9C%8D%E5%8A%A1)\n" +
                "\n" +
                "# API\n" +
                "\n" +
                "请查看EOS版本信息\n" +
                "\n" +
                "[http://192.168.0.235:8100/index.html#/servicelist/"+ app.getAppId() +"/0](http://192.168.0.235:8100/index.html#/servicelist/"+ app.getAppId() +"/0)";
        FileUtils.writeStringToFile(new File(fileDir + "/README.md"), str, "utf-8");
    }

    private void createPackageJsonFile(TApp app, String v, String fileDir) throws IOException {
        String str = "{\n" +
                "  \"version\": \"" + v + "\",\n" +
                "  \"description\": \"zeus eos应用 提供给redux的action：" + app.getAppName() + "\",\n" +
                "  \"name\": \"zeus-eos-app-"+ app.getAppCode() +"\",\n" +
                "  \"main\": \"index.jsx\",\n" +
                "  \"dependencies\": [\n" +
                "    \"zeus-eos-middleware\"\n" +
                "  ]\n" +
                "}";
        FileUtils.writeStringToFile(new File(fileDir + "/component.json"), str, "utf-8");
    }
}

