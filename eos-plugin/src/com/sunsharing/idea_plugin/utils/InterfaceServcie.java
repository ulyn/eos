/*
 * @(#) InterfaceServcie
 * 版权声明 厦门畅享信息技术有限公司, 版权所有 违者必究
 *
 * <br> Copyright:  Copyright (c) 2017
 * <br> Company:厦门畅享信息技术有限公司
 * <br> @author ningyp
 * <br> 2017-12-19 11:44:19
 * <br> @version 1.0
 * ————————————————————————————————
 *    修改记录
 *    修改者：
 *    修改时间：
 *    修改原因：
 * ————————————————————————————————
 */

package com.sunsharing.idea_plugin.utils;

import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileReader;
import java.util.*;

/**
 * Created by yope on 2017/12/19.
 */
public class InterfaceServcie {

    public String getInterfaceName(String[] lines) {
        for (int i = 0; i < lines.length; i++) {
            if (lines[i].trim().startsWith("public interface")) {
                String line = lines[i].trim();
                String l = line.substring(0 + 16).trim();
                if (l.endsWith("{")) {
                    l = l.substring(0, l.length() - 1);
                }
                String name = l.trim();
                return Character.toLowerCase(name.charAt(0)) + name.substring(1);
            }
        }
        return null;
    }

    public String getInterfaceEosModule(String[] lines) {
        return getNameByKeyword(lines, "module");
    }

    public String getInterfaceEosShow(String[] lines) {
        return getNameByKeyword(lines, "desc");
    }

    public String getNameByKeyword(String[] lines, String keyword) {
        for (int i = 0; i < lines.length; i++) {
            if (lines[i].trim().startsWith("@EosService")) {
                String line = lines[i].trim();
                String value = line.substring(line.indexOf("(") + 1, line.length() - 1);
                String strArr[] = value.split(",");
                for (int j = 0; j < strArr.length; j++) {
                    String v = strArr[j];
                    if (v != null && v.indexOf(keyword) != -1) {
                        return v.replaceAll(keyword, "").replaceAll("=", "")
                            .replaceAll("\"", "").trim();
                    }
                }
            }
        }
        return null;
    }

    public static void main(String[] a) throws Exception {
        boolean str = StringUtils.isBlank("\n".trim());
        System.out.println(str);
        File f = new File("E:\\IdeaProjects\\sunsharing\\blend-basic\\blend-basic-server\\src\\main\\java\\com\\sunsharing\\blend\\basic\\server\\service\\api\\AddressService.java");
        BufferedReader reader = new BufferedReader(new FileReader(f));
        List<String> str2 = new ArrayList<String>();
        String line = "";
        while ((line = reader.readLine()) != null) {
            str2.add(line);
        }
        String[] lines = str2.toArray(new String[]{});
        InterfaceServcie service = new InterfaceServcie();
        System.out.println(service.getInterfaceEosShow(lines));
        System.out.println(service.getInterfaceEosModule(lines));
        System.out.println("-------------------");
        String name = service.getInterfaceName(lines);
        System.out.println(name);

    }
}
