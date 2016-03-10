/**
 * Created with IntelliJ IDEA.
 * User: ulyn
 * Date: 14-2-11
 * Time: 上午1:02
 * To change this template use File | Settings | File Templates.
 */
jQuery.eosRemoteSetup = {
    "dataType": "json",
    "url": "/remote",
    "appId": "",
    "serviceId": "",
    "method": "",
    "version": "",
    "mock": "",
    "data": null,
    "async": true,
    "beforeSend": function (XHR) {
    },
    "success": function (data, textStatus) {
    },
    "error": function (XMLHttpRequest, textStatus, errorThrown) {
        if (console) {
            console.info(XMLHttpRequest);
        }
    }
};
jQuery.eosRemote = function (opts) {

    //增加全局控制是否使用mock的参数
    var use_mock = true;
    if (!use_mock) {
        opts.mock = "";
    }

    var option = jQuery.extend({}, jQuery.eosRemoteSetup, opts);
    var vars = "eos_appid="+option.appId+"&eos_service_id=" + option.serviceId
        + "&eos_method_name=" + option.method + "&eos_version=" + option.version + "&eos_mock=" + option.mock;
    if (option.url.indexOf("?") != -1) {
        option.url = option.url + "&" + vars;
    } else {
        option.url = option.url + "?" + vars;
    }
    //处理data,将value为object的转换为json串
    var params = option.data;
    if (params != null && typeof params == "object") {
        for (var key in params) {
            if (params[key] != null && typeof params[key] == "object") {
                params[key] = JSON.stringify(params[key]);
            }
        }
    }
    jQuery.ajax({
        type: "post",
        url: option.url + "&r=" + Math.random(),
        dataType: option.dataType,
        data: params,
        async: option.async,
        beforeSend: option.beforeSend,
        success: function (data, status) {
            if (data.status) {
                option.success(data.result, status);
            } else {
                //内部处理出错
                option.error(null, data.result, new Error(data.result));
            }
        },
        error: function (XMLHttpRequest, textStatus, errorThrown) {
            if (console) {
                console.info("XMLHttpRequest:", XMLHttpRequest);
            }
            if (textStatus == "parsererror") {
                //解析异常，尝试eval转换
                try {
                    eval("var eval_data=" + XMLHttpRequest.responseText);
                    if (eval_data.status) {
                        option.success(eval_data.result, eval_data.status);
                    } else {
                        //内部处理出错
                        option.error(XMLHttpRequest, eval_data.result, eval_data.status);
                    }
                } catch (e) {
                    option.error(XMLHttpRequest, "解析返回数据异常！", e);
                }
            } else {
                option.error(XMLHttpRequest, textStatus, errorThrown);
            }
        }
    });
}
