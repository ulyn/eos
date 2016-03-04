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

//    public String getVersion(String[] lines)throws Exception
//    {
//        for(int i=0;i<lines.length;i++)
//        {
//            if(lines[i].trim().startsWith("@EosService"))
//            {
//                String line = lines[i].trim();
//
//                String value = line.substring(line.indexOf("(")+1,line.length()-1);
//                value = value.replaceAll(",","\n");
//
//                Properties t = new Properties();
//                t.load(new ByteArrayInputStream(value.getBytes("UTF-8")));
//                String v = t.getProperty("version");
//                return v.trim().substring(1,v.trim().length()-1);
//            }
//        }
//        throw new RuntimeException("取不到Version");
//    }

    public Map getFuntionVersion(String[] lines)
    {
        Map result = new HashMap();
        for(int i=0;i<lines.length;i++) {
            String line = lines[i].trim();
            if (lines[i].trim().startsWith("@Version"))
            {
                int start = lines[i].trim().indexOf("=");
                int end = lines[i].trim().indexOf(")");
                String v = line.substring(start,end).trim();
                if(v.startsWith("\""))
                {
                    v = v.substring(1,v.length()-1);
                }
                int next = 1;
                while(true)
                {
                    int nextLine = i+(next++);
                    if(nextLine>=lines.length)
                    {
                        break;
                    }
                    if(!StringUtils.isBlank(lines[nextLine].trim())) {
                        String functionLine = lines[nextLine].trim();
                        int startIndex = functionLine.indexOf("(");
                        int m = 0;
                        for(m=startIndex;m>0;m--)
                        {
                            if(functionLine.charAt(m)==' ')
                            {
                                break;
                            }
                        }
                        String functionName = functionLine.substring(m,startIndex);
                        result.put(functionName,v.trim());
                        break;
                    }
                }

            }
        }
        return result;
    }

    public boolean isVoid(String methodName,String[] lines)
    {
        for(int i=0;i<lines.length;i++)
        {
            if(lines[i].indexOf(" "+methodName)!=-1
                    && lines[i].indexOf(" void ")!=-1
                    )
            {
                return true;
            }
        }
        return false;
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
                t.put("id",getInterfaceName(lines));
                String result = "";
                for(Iterator iter = t.keySet().iterator();iter.hasNext();)
                {
                    String key = (String)iter.next();
                    String v = (String)t.get(key);
                    if ("timeout".equals(key)) {
                        result += key + "=" + v + ",";
                    } else {
                        result += key + "=\"" + v + "\",";
                    }
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
    //添加入参的注解
    public String[] addParams(String[] lines,Map params) throws Exception
    {
        boolean start = false;
        boolean functionstart = false;
        StringBuffer functions = new StringBuffer();
        Map parMap = new HashMap();
        List<Map> funs = new ArrayList<Map>();
        for(int i=0;i<lines.length;i++)
        {
            if(lines[i].trim().startsWith("public interface"))
            {
                start = true;
            }
            if(start && !lines[i].trim().startsWith("public interface"))
            {
                if(functionstart)
                {
                    if(!StringUtils.isBlank(lines[i]))
                    functions.append(lines[i]);
                }
                //开始处理
                if(functionstart==false && !StringUtils.isBlank(lines[i].trim()) &&
                        !lines[i].trim().startsWith("/*") && !lines[i].trim().startsWith("*"))
                {
                    functionstart = true;
                    functions.append(lines[i]);
                    parMap.put("index",i);
                }


                if(lines[i].trim().endsWith(");"))
                {
                    functionstart = false;
                    if(lines[i].trim().indexOf("(")==-1)
                    {
                        functions.append(lines[i]);
                    }

                    String str = functions.toString();
                    System.out.println(str);
                    int startIndex = str.indexOf("(");
                    int m = 0;
                    for(m=startIndex;m>0;m--)
                    {
                        if(str.charAt(m)==' ')
                        {
                            break;
                        }
                    }
                    String functionName = str.substring(m,startIndex);


                    int endIndex = str.indexOf(")");
                    String pars = str.substring(startIndex+1,endIndex);
                    String result = "";
                    String [] arr = pars.split(",");
                    for(int j=0;j<arr.length;j++)
                    {
                        String tmp[] = arr[j].split(" ");
                        List lll = new ArrayList();
                        for(int k=0;k<tmp.length;k++)
                        {
                            if(!StringUtils.isBlank(tmp[k]))
                            {
                                lll.add(tmp[k]);
                            }
                        }
                        if(lll.size()==2)
                        result+="\""+lll.get(1)+"\",";
                    }
                    if(!StringUtils.isBlank(result))
                    {
                        result = result.substring(0,result.length()-1);
                    }
                    parMap.put("result",result);
                    params.put(functionName.trim(), StringUtils.isBlank(result) ? "" : result.replaceAll("\"", ""));
                    funs.add(parMap);
                    //params.add(params);
                    parMap = new HashMap();
                    functions = new StringBuffer();
                }
            }
            if(lines[i].trim().endsWith("}") && !lines[i].trim().startsWith("/*") &&
                 !lines[i].trim().startsWith("*"))
            {
                start = false;
            }
        }
        List<String> kkk = new ArrayList();
        for(int i=0;i<lines.length;i++)
        {
            kkk.add(lines[i]);
        }
        for(int i=0;i<funs.size();i++)
        {
            Map m = (Map)funs.get(i);
            int sourceInt = (Integer)m.get("index");
            kkk.add(i+sourceInt,"@ParameterNames(value = {"+(String)m.get("result")
            +"})"
            );


        }
        int index = 0;
        for(int i=0;i<kkk.size();i++)
        {
            if(((String)kkk.get(i)).trim().startsWith("package"))
            {
                index=i;
            }
        }

        kkk.add(++index,"import com.sunsharing.eos.common.annotation.ParameterNames;");

        return kkk.toArray(new String[]{});

    }

    public String[] synMock(String methodName,String[] lines,String []mocks)
    {
        boolean start = false;
        boolean functionstart = false;

        StringBuffer functions = new StringBuffer();
        int mockBeginLine = 0;
        int mockEndLine=0;
        int blankNum = 0;

        for(int i=0;i<lines.length;i++)
        {
            if(lines[i].trim().startsWith("public interface"))
            {
                start = true;
            }

            if(start && !lines[i].trim().startsWith("public interface"))
            {
                if(functionstart)
                {
                    if(!StringUtils.isBlank(lines[i]))
                    {
                        functions.append(lines[i]);
                    }
                }

                if(functionstart==false && !StringUtils.isBlank(lines[i].trim())
                 && lines[i].trim().startsWith("*"))
                {
                    String line = lines[i].trim();
                    int index = line.indexOf("*");
                    blankNum = lines[i].indexOf("*");
                    line = line.substring(index+1).trim();
                    if(line.startsWith("@") &&
                            !line.startsWith("@ParameterNames"))
                    {
                        mockBeginLine = 0;
                        mockEndLine=0;
                    }else if(line.trim().startsWith("${") || line.trim().startsWith("/") ||
                            line.trim().equals(""))
                    {
                        if(mockBeginLine == 0)
                        {
                            mockBeginLine = i;
                        }
                    }
                }

                //开始处理
                if(functionstart==false && !StringUtils.isBlank(lines[i].trim()) &&
                        !lines[i].trim().startsWith("/*") &&
                        !lines[i].trim().startsWith("*")
                        )
                {
                    functionstart = true;
                    functions.append(lines[i]);
                    mockEndLine = i;
                }


                if(lines[i].trim().endsWith(");"))
                {

                    functionstart = false;
                    if(lines[i].trim().indexOf("(")==-1)
                    {
                        functions.append(lines[i]);
                    }
                    String str = functions.toString();
                    str = str.replaceAll("@ParameterNames\\(.*?\\)","");
                    functions = new StringBuffer();
                    System.out.println(str);
                    int startIndex = str.indexOf("(");
                    int m = 0;
                    for(m=startIndex;m>0;m--)
                    {
                        if(str.charAt(m)==' ')
                        {
                            break;
                        }
                    }
                    String functionName = str.substring(m,startIndex);

                    if(functionName.trim().equals(methodName))
                    {
                        if(mockBeginLine==0)
                        {
                            return lines;
                        }
                        for(int k=0;k<mocks.length;k++)
                        {
                            mocks[k] = "* "+mocks[k];
                            for(int j=0;j<blankNum;j++)
                            {
                                mocks[k] = " "+mocks[k];
                            }
                        }
                        String[] mocks2 = new String[mocks.length+1];
                        System.arraycopy(mocks,0,mocks2,0,mocks.length);
                        mocks2[mocks2.length-1] = "*/";
                        for(int j=0;j<blankNum;j++)
                        {
                            mocks2[mocks2.length-1] = " "+mocks2[mocks2.length-1];
                        }

                        String [] newStr1 = Arrays.copyOfRange(lines, 0, mockBeginLine);
                        String [] newStr2 = Arrays.copyOfRange(lines, mockEndLine, lines.length);
                        String[] result = new String[newStr1.length + newStr2.length + mocks2.length];
                        System.arraycopy(newStr1,0,result,0,newStr1.length);
                        System.arraycopy(mocks2,0,result,newStr1.length,mocks2.length);
                        System.arraycopy(newStr2,0,result,newStr1.length+mocks2.length,newStr2.length);
                        return result;
                    }else
                    {
                        mockBeginLine = 0;
                        mockEndLine = 0;
                    }

                }
            }
            if(lines[i].trim().endsWith("}") && !lines[i].trim().startsWith("/*") &&
                    !lines[i].trim().startsWith("*"))
            {
                start = false;
            }
        }
        return null;
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
            if(startresult && lines[i].trim().startsWith("*")  && !lines[i].trim().startsWith("*/")
                    && !lines[i].trim().substring(1).trim().startsWith("@return"))
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

    public List getAllVoidFuntions(String[] lines)
    {
        List allFunction = new ArrayList();
        boolean start = false;
        for(int i=0;i<lines.length;i++)
        {
            if(start && !lines[i].trim().startsWith("public interface"))
            {
                if(!lines[i].trim().startsWith("/*") && !lines[i].trim().startsWith("*") &&
                        !lines[i].trim().startsWith("{") && !lines[i].trim().startsWith("}"))
                {
                    if(lines[i].indexOf(" void ")==-1)
                    {
                        continue;
                    }
                    String line = lines[i].trim();
                    int index = line.indexOf("(");
                    int start2 = 0;
                    for(int j=index;j>=0;j--)
                    {
                        if(line.charAt(j)==' ')
                        {
                            allFunction.add(line.substring(start2,index).trim());
                            break;
                        }
                    }
                }
            }
            if(lines[i].trim().startsWith("public interface"))
            {
                start = true;
            }

        }
        return allFunction;
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
                int index = l.lastIndexOf("}");
                String status = l.substring(2,index);
                System.out.println(status);
                tmp.put("status",status);
                tmp.put("desc",l.substring(index+1));
                tmp.put("content","");
            }else
            {
                if(tmp==null)
                {
                    continue;
                }
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
        File f = new File("/Users/criss/Desktop/projectDev/eos_git/eos-client-example/src" +
                "/main/java/com/sunsharing/eos/clientexample/test/Union.java");
        BufferedReader reader = new BufferedReader(new FileReader(f));
        List<String> str2 = new ArrayList<String>();
        String line = "";
        while((line = reader.readLine())!=null)
        {
            str2.add(line);
        }
        String [] lines = str2.toArray(new String[]{});
        InterfaceServcie service = new InterfaceServcie();
//        String versiont = service.getVersion(lines);
//        System.out.println(versiont);
//        String name = service.getInterfaceName(lines);
//        System.out.println(name);
//        service.getFunction(lines);
        String[] mocks = new String[2];
        mocks[0] = "1111111111";
        mocks[1] = "\n";
        String[] array = service.synMock("detail",lines,mocks);
        for(int i=0;i<array.length;i++)
        {
            System.out.println(array[i]);
        }

    }

}
