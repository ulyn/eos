/**
 * Created with IntelliJ IDEA.
 * User: ulyn
 * Date: 14-2-11
 * Time: 上午1:02
 * To change this template use File | Settings | File Templates.
 */
jQuery.eosRemoteSetup = {
    "dataType": "json", //jsonp,json,默认是json,当涉及跨域使用jsonp
    "url": "/remote",
    "serviceId": "",
    "method": "",
    "mock": "",
    "data": null,
    "async": true,
    "jsonp": "eos_jsonp_callback", //当dataType是jsonp时候此参数生效，不要更改它
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
    var option = jQuery.extend({}, jQuery.eosRemoteSetup, opts);
    var vars = "eos_service_id=" + option.serviceId + "&eos_method_name=" + option.method + "&eos_mock=" + option.mock;
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
        url: option.url,
        dataType: option.dataType,
        data: params,
        async: option.async,
        jsonp: option.jsonp,
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
