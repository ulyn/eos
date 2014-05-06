/**
 * @(#)EosRemoteJsValidate
 * 版权声明 厦门畅享信息技术有限公司, 版权所有 违者必究
 *
 *<br> Copyright:  Copyright (c) 2014
 *<br> Company:厦门畅享信息技术有限公司
 *<br> @author ulyn
 *<br> 14-4-17 下午8:56
 *<br> @version 1.0
 *————————————————————————————————
 *修改记录
 *    修改者：
 *    修改时间：
 *    修改原因：
 *————————————————————————————————
 */
package com.sunsharing.eos.common.utils;

import com.sunsharing.component.utils.file.FileUtil;

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
public class EosRemoteJsValidate {

    public static void main(String[] args) throws Exception {
        String js = FileUtil.readTxt(args[0], "utf-8");
        int i = js.indexOf("eosRemoteSetup");
        int j = js.indexOf("serviceId");
        js = js.substring(i, j);

        js = js.substring(js.indexOf(":"));
        String dataType = js.substring(js.indexOf("\"") + 1, js.indexOf(","));
        if (dataType.indexOf("\"") != -1) {
            dataType = dataType.substring(0, dataType.indexOf("\""));
        }
        System.out.println("dataType:" + dataType);
        String url = js.substring(js.indexOf("\"url\""));
        url = url.substring(url.indexOf(":") + 1, url.indexOf(","));
        url = url.substring(url.indexOf("\"") + 1, url.lastIndexOf("\""));
        System.out.println("url:" + url);

        if (!"json".equalsIgnoreCase(dataType)) {
            System.out.println("eos的文件配置有问题吧？？？路径：" + args[0]);
            System.out.println("dataType：" + dataType);
            System.out.println("请把dataType配置成json,并注意更改url参数！！！！");
            System.exit(0);
        }
    }
}

