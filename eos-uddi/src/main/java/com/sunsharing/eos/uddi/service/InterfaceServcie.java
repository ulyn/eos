package com.sunsharing.eos.uddi.service;

import com.sunsharing.eos.common.utils.StringUtils;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileReader;
import java.util.*;

/**
 * Created by criss on 14-2-2.
 */
public class InterfaceServcie {

    public String getVersion(String[] lines)throws Exception
    {
        for(int i=0;i<lines.length;i++)
        {
            if(lines[i].trim().startsWith("@EosService"))
            {
                String line = lines[i].trim();

                String value = line.substring(line.indexOf("(")+1,line.length()-1);
                value = value.replaceAll(",","\n");

                Properties t = new Properties();
                t.load(new ByteArrayInputStream(value.getBytes("UTF-8")));
                String v = t.getProperty("version");
                return v.trim().substring(1,v.trim().length()-1);
            }
        }
        throw new RuntimeException("取不到Version");
    }

    public String getInterfaceName(String[] lines)
    {
        for(int i=0;i<lines.length;i++)
        {
            if(lines[i].trim().startsWith("public interface"))
            {
                String line = lines[i].trim();
                int tmp = line.indexOf("public interface");
                String l = line.substring(0+16).trim();
                if(l.endsWith("{"))
                {
                    l = l.substring(0,l.length()-1);
                }
                String name = l.trim();
                return Character.toLowerCase(name.charAt(0)) + name.substring(1);
            }
        }
        throw new RuntimeException("取不到接口名");
    }

    public void addAppCode(String appCode,String[] lines) throws Exception
    {
        for(int i=0;i<lines.length;i++)
        {
            if(lines[i].trim().startsWith("@EosService"))
            {
                String line = lines[i].trim();
                String value = line.substring(12,line.length()-1);
                value = value.replaceAll(",","\n");
                value = value.replaceAll("\"","");
                Properties t = new Properties();
                t.load(new ByteArrayInputStream(value.getBytes("UTF-8")));
                t.put("appId",appCode);
                String result = "";
                for(Iterator iter = t.keySet().iterator();iter.hasNext();)
                {
                    String key = (String)iter.next();
                    String v = (String)t.get(key);
                    result+=key+"=\""+v+"\",";
                }
                if(result.length()>0)
                {
                    result = result.substring(0,result.length()-1);
                }
                int i1 = lines[i].indexOf("@EosService(");
                int i2 = lines[i].indexOf(")");
                lines[i] = lines[i].substring(0,i1+12)+result+lines[i].substring(i2);
                break;
            }
        }
    }

    public Map getFunction(String[]lines)
    {
        Map resultMap = new HashMap();
        boolean start = false;
        boolean newfunction = true;
        boolean startresult = false;
        List result = new ArrayList();
        for(int i=0;i<lines.length;i++)
        {
            if(lines[i].trim().startsWith("public interface"))
            {
               start = true;
            }
            if(!start)
            {
                continue;
            }

            if(newfunction)
            {
                if(result.size()>0)
                {
                    while(StringUtils.isBlank(lines[i].trim()))
                    {
                        i++;
                    }
                    String fun = lines[i].trim();
                    String[] aaa = fun.split(" ");
                    int notnullIndex = 0;
                    int functionIndex = 2;
                    String function = "";
                    for(int j=0;j<aaa.length;j++)
                    {
                        if(!StringUtils.isBlank(aaa[j]))
                        {
                            notnullIndex++;
                            if(notnullIndex==1 && aaa[j].equalsIgnoreCase("public"))
                            {
                                functionIndex = 3;
                            }
                            if(notnullIndex == functionIndex)
                            {
                                String l = aaa[j].trim();
                                if(l.indexOf("(")!=-1)
                                {
                                    l = l.substring(0,l.indexOf("("));
                                }
                                function = l;
                                break;
                            }
                        }
                    }
                    parseResult(function,result,resultMap);
                    result = new ArrayList();
                    newfunction = false;
                    startresult = false;
                }
            }
            if(lines[i].trim().startsWith("*") && lines[i].trim().substring(1).trim().startsWith("@return"))
            {
                if(lines[i].trim().substring(1).trim().length()>7)
                {
                    result.add(lines[i].trim().substring(1).trim().substring(7).trim());
                }
                startresult = true;
                newfunction = false;
            }
            if(startresult && lines[i].trim().startsWith("*")  && !lines[i].trim().startsWith("*/") && !lines[i].trim().substring(1).trim().startsWith("@return"))
            {
                if(!StringUtils.isBlank(lines[i].trim().substring(1).trim()))
                result.add(lines[i].trim().substring(1).trim());
                newfunction = false;
            }
            if(lines[i].trim().startsWith("*/"))
            {
                newfunction = true;
            }

        }
        return resultMap;
    }

    public void parseResult(String function,List result,Map functions)
    {
        System.out.println(function);
        String rst ="";
        Map functionMap = new HashMap();
        functions.put(function,functionMap);
        Map tmp = null;
        for(Iterator iter = result.iterator();iter.hasNext();)
        {
            String l = (String)iter.next();
            //System.out.println(l);
            if(l.startsWith("${")==true)
            {
                if(tmp!=null)
                {
                    functionMap.put(tmp.get("status"),tmp);
                }
                tmp = new HashMap();
                int index = l.indexOf("}");
                String status = l.substring(1,index);
                tmp.put("status",status);
                tmp.put("desc",l.substring(index+1));
                tmp.put("content","");
            }else
            {
                String ll = (String)tmp.get("content");
                ll+=l;
                tmp.put("content",ll);
            }

        }
        if(tmp!=null)
        {
            functionMap.put(tmp.get("status"),tmp);
        }

    }

    public static void main(String[]a)throws Exception
    {
        boolean str = StringUtils.isBlank("\n".trim());
        System.out.println(str);
        File f = new File("/Users/criss/Desktop/projectDev/eosgit/eos-server/src/test/java/TestInterfaceAnno.java");
        BufferedReader reader = new BufferedReader(new FileReader(f));
        List<String> str2 = new ArrayList<String>();
        String line = "";
        while((line = reader.readLine())!=null)
        {
            str2.add(line);
        }
        String [] lines = str2.toArray(new String[]{});
        InterfaceServcie service = new InterfaceServcie();
        String versiont = service.getVersion(lines);
        System.out.println(versiont);
        String name = service.getInterfaceName(lines);
        System.out.println(name);
        service.getFunction(lines);
    }

}
