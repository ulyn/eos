/**
 * Created with IntelliJ IDEA.
 * User: ulyn
 * Date: 14-2-11
 * Time: 上午1:02
 * To change this template use File | Settings | File Templates.
 */

jQuery.eosRemote = function (opts) {
    var defaults = {
        "url": "/remote",
        "id": "",
        "method": "",
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
    }
    var option = jQuery.extend({}, defaults, opts);
    var vars = "eos_service_id=" + option.id + "&eos_method_name=" + option.method + "&eos_mock=" + option.mock;
    if (option.url.indexOf("?") != -1) {
        option.url = option.url + "&" + vars;
    } else {
        option.url = option.url + "?" + vars;
    }
    jQuery.ajax({
        type: "post",
        url: option.url,
        dataType: "json",
        data: option.data,
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
