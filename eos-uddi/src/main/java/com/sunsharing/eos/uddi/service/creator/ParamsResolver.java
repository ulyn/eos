/**
 * @(#)ParamsResolver
 * 版权声明 厦门畅享信息技术有限公司, 版权所有 违者必究
 *
 *<br> Copyright:  Copyright (c) 2016
 *<br> Company:厦门畅享信息技术有限公司
 *<br> @author ulyn
 *<br> 16-6-23 下午3:17
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
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.sunsharing.eos.common.utils.StringUtils;

import java.util.ArrayList;
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
public class ParamsResolver {

    /**
     * 对应存储的params字段进行解析出出入参
     * @param paramsStr
     * @return
     */
    public static InOutParameter toInOutParams(String paramsStr){
        InOutParameter inOutParameter = new InOutParameter();
        inOutParameter.setOutType("Object");
        if(StringUtils.isBlank(paramsStr)){
            return inOutParameter;
        }
        String[] arr = paramsStr.split("\\|");
        if(arr.length == 2){
            inOutParameter.setOutType(arr[1].trim());
        }
        if(!StringUtils.isBlank(arr[0].trim())){
            ArrayList<String> list = new ArrayList<String>();
            char[] charArray = arr[0].trim().toCharArray();
            int ltCount = 0;
            int gtCount = 0;
            StringBuilder sb = new StringBuilder();
            for(char ch : charArray){
                if(ch == '<'){
                    ltCount++;
                    sb.append(ch);
                }else if(ch == '>'){
                    gtCount++;
                    sb.append(ch);
                }else if(ch == ','){
                    if( ltCount == gtCount ){
                        list.add(sb.toString());
                        sb = new StringBuilder();
                    }else{
                        sb.append(ch);
                    }
                }else{
                    sb.append(ch);
                }
            }
            if(sb.length() >0){
                list.add(sb.toString());
            }

            for(String p : list){
                inOutParameter.addInParameter(new InParameter(p));
            }
        }
        return inOutParameter;
    }

    public static class InParameter{
        private String oriStr = "";
        private String type = "";
        private String name = "";

        public InParameter(String oriStr) {
            this.oriStr = oriStr = oriStr.trim();
            //解析
            int i = oriStr.lastIndexOf(">");
            if(i != -1){
                //Map<String,String[]> test
                this.type = oriStr.substring(0,i+1).trim();
                this.name = oriStr.substring(i + 1).trim();
            }else if(oriStr.endsWith("]")){
                //String test[]
                oriStr = oriStr.substring(0,oriStr.lastIndexOf("["));
                String[] arr = org.apache.commons.lang3.StringUtils.split(oriStr," ");
                if(arr.length >= 2){
                    this.name = arr[arr.length - 1].trim();
                    for(int j=0;j<arr.length -1 ;j++){
                        if(j!=0){
                            this.type += " ";
                        }
                        this.type += arr[j].trim();
                    }
                }else{
                    this.name = oriStr;
                    this.type = "Object[]";
                }
            }else{
                i = oriStr.lastIndexOf("]");
                if(i != -1){
                    this.type = oriStr.substring(0,i+1).trim();
                    this.name = oriStr.substring(i + 1).trim();
                }else{
                    String[] arr = org.apache.commons.lang3.StringUtils.split(oriStr," ");
                    if(arr.length >= 2){
                        this.name = arr[arr.length - 1].trim();
                        for(int j=0;j<arr.length -1 ;j++){
                            if(j!=0){
                                this.type += " ";
                            }
                            this.type += arr[j].trim();
                        }
                    }else{
                        this.name = oriStr;
                        this.type = "Object";
                    }
                }
            }
        }

        public String getOriStr() {
            return oriStr;
        }

        public String getType() {
            return type;
        }

        public String getName() {
            return name;
        }
    }
    public static class InOutParameter{
        private String outType;
        private List<InParameter> inParameters = new ArrayList<InParameter>();

        public InOutParameter() {
        }

        public InOutParameter(List<InParameter> inParameters, String outType) {
            this.inParameters = inParameters;
            this.outType = outType;
        }


        public String getOutType() {
            return outType;
        }

        public InOutParameter setOutType(String outType) {
            this.outType = outType;
            return this;
        }

        public List<InParameter> getInParameters() {
            return inParameters;
        }

        public InOutParameter setInParameters(List<InParameter> inParameters) {
            this.inParameters = inParameters;
            return this;
        }

        public InOutParameter addInParameter(InParameter inParameter) {
            this.inParameters.add(inParameter);
            return this;
        }
    }

    public static void main(String[] args) {
        System.out.println(JSON.toJSONString(toInOutParams(
                "Map<String,Map<String,Object>> testMap," +
                        "final String s," +
                        "String[] sb," +
                        "String []sb," +
                        "List<String> list," +
                        "List<Map<String,Map<String,String>>> listmap"
        ), SerializerFeature.PrettyFormat));
        System.out.println(JSON.toJSONString(toInOutParams(
                "String s|Map"
        ), SerializerFeature.PrettyFormat));
    }
}

