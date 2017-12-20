/*
 * @(#) ZentaoResult
 * 版权声明 厦门畅享信息技术有限公司, 版权所有 违者必究
 *
 * <br> Copyright:  Copyright (c) 2017
 * <br> Company:厦门畅享信息技术有限公司
 * <br> @author ningyp
 * <br> 2017-12-18 09:23:05
 * <br> @version 1.0
 * ————————————————————————————————
 *    修改记录
 *    修改者：
 *    修改时间：
 *    修改原因：
 * ————————————————————————————————
 */

package com.sunsharing.idea_plugin.eos;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

/**
 * <pre></pre>
 * <br>----------------------------------------------------------------------
 * <br> <b>功能描述:</b>
 * <br>   EOS API接口统一业务结果类
 * <br> 注意事项:
 * <br>
 * <br>
 * <br>----------------------------------------------------------------------
 * <br>
 */
public class EosResult<T> {

    public String msg;
    public boolean status = true;
    public T data;

    public EosResult() {
    }

    public EosResult(boolean status, String msg, T data) {
        this.msg = msg;
        this.status = status;
        this.data = data;
    }

    public String getMsg() {
        return msg;
    }

    public T getData() {
        return data;
    }

    public T checkSuccessRet() {
        if (isSuccess()) {
            return getData();
        } else {
            throw new RuntimeException("调用Api异常:" + getMsg());
        }
    }

    public boolean isSuccess() {
        return status;
    }

    public static EosResult error(String msg) {
        return new EosResult(false, msg, null);
    }


    public static <T> EosResult<T> from(String eosRes, Class<T> cls) {
        System.out.println("eosResponse : " + eosRes);
        Result result = new Gson().fromJson(eosRes, Result.class);
        return result.toEosResult(cls);
    }

    @Override
    public String toString() {
        return "EosResult{" +
            "msg='" + msg + '\'' +
            ", status=" + status +
            ", data=" + data +
            '}';
    }

    public static class Result {
        private String status;
        private String msg;
        private Object data;

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getMsg() {
            return msg;
        }

        public void setMsg(String reason) {
            this.msg = msg;
        }

        public Object getData() {
            return data;
        }

        public void setData(Object data) {
            this.data = data;
        }

        public boolean isSuccess() {
            return "true".equals(status);
        }

        public <T> EosResult<T> toEosResult(Class<T> cls) {
            if (!isSuccess()) {
                return EosResult.error(getMsg());
            }
           /* List<T> list = new ArrayList<T>();
            JsonArray array = new JsonParser().parse((String) getData()).getAsJsonArray();
            for (final JsonElement elem : array) {
                list.add(new Gson().fromJson(elem, cls));
            }
            return new EosResult(true, getMsg(), list);*/
            // new Gson().new Gson().toJson(getData())
            // TypeToken o = new Gson().fromJson(new Gson().toJson(getData()), cls);
            return new EosResult(true, getMsg(), new Gson().toJson(getData()));
        }
    }
}

