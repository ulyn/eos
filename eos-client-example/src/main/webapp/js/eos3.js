"use strict";
(function (factory) {
    if (typeof exports === 'object') {
        // Node. Does not work with strict CommonJS, but
        // only CommonJS-like enviroments that support module.exports,
        // like Node.
        module.exports = factory();
    } else if (typeof define === 'function' && define.amd) {
        // AMD. Register as an anonymous module.
        define("EosRequest",factory);
    } else {
        // Browser globals
        window.Eos = factory();
    }
}(function () {
    var requestUrl = getContextPath("/static") + "/remote";
    var methodCache = {};

    function log(){ console && console.log && console.log(arguments); }
    function noop(){}
    function ajax(opt) {
        return new Promise(function(resolve, reject){
            var url = opt.url || "";
            var async = opt.async !== false,
                method = opt.type || 'GET',
                data = opt.data || null,
                dataType = opt.dataType,
                beforeSend = opt.beforeSend || noop,
                complete = opt.complete || noop,
                success = function (data) {
                    if (data.status) {
                        resolve(data.result);
                    } else {
                        //内部处理出错
                        reject(data.result);
                    }
                },
                error = function (XMLHttpRequest, textStatus, errorThrown) {
                    if (console) {
                        console.info("XMLHttpRequest:", XMLHttpRequest);
                    }
                    if (textStatus == "parsererror") {
                        //解析异常，尝试eval转换
                        try {
                            eval("var eval_data=" + XMLHttpRequest.responseText);
                            if (eval_data.status) {
                                resolve(eval_data.result, eval_data.status);
                            } else {
                                //内部处理出错
                                reject(eval_data.result);
                            }
                        } catch (e) {
                            reject("解析返回数据异常！");
                        }
                    } else {
                        reject(textStatus);
                    }
                };
            if(data){
                var args = "";
                if(typeof data == 'string'){
                    args = data;
                }else if(typeof data == 'object'){
                    var argcount = 0;
                    for (var key in data) {
                        if (data.hasOwnProperty(key)) {
                            if (argcount++) {
                                args += '&';
                            }
                            args += encodeURIComponent(key) + '=' + encodeURIComponent(data[key]);
                        }
                    }
                }
                method = method.toUpperCase();
                if (method == 'GET') {
                    url += (url.indexOf('?') == -1 ? '?' : '&') + args;
                    data = null;
                }else{
                    data = args;
                }
            }
            var xhr = window.XMLHttpRequest ? new XMLHttpRequest()
                : new ActiveXObject('Microsoft.XMLHTTP');
            //执行before
            beforeSend(xhr);
            xhr.onreadystatechange = function() {
                if (xhr.readyState == 4) {
                    var s = xhr.status;
                    if (s >= 200 && s < 300) {
                        var txt = xhr.responseText;
                        if(dataType == 'json' && txt){
                            success(JSON.parse(txt));
                        }else{
                            success(txt);
                        }
                    } else {
                        error(xhr,s);
                    }
                    complete(s,xhr);
                } else {
                }
            };
            xhr.open(method, url, async);
            if (method == 'POST') {
                xhr.setRequestHeader('Content-type',
                    'application/x-www-form-urlencoded;');
            }
            xhr.send(data);
            return xhr;
        });
    }
    function extend(obj) {
        var length = arguments.length;
        if (length < 2 || obj == null) return obj;
        for (var index = 1; index < length; index++) {
            var source = arguments[index];
            for (var key in source){
                if(source[key] !== undefined && source !== ''){
                    obj[key] = source[key];
                }
            }
        }
        return obj;
    }
    function getContextPath(baseUrl){
        //遍历获取
        var scripts = document.getElementsByTagName("script");
        if(scripts && scripts.length > 0){
            for(var i=0;i<scripts.length;i++){
                var nodeScript = scripts[i];
                var jsPath = nodeScript.hasAttribute ? // non-IE6/7
                    nodeScript.src :
                    // see http://msdn.microsoft.com/en-us/library/ms536429(VS.85).aspx
                    nodeScript.getAttribute("src", 4);
                if(jsPath.indexOf(baseUrl)!==-1) {
                    return jsPath.substring(0,jsPath.indexOf(baseUrl));
                }
            }
            return "ERROR:BASEURL_UNKNOWN";
        }else{
            return "";
        }
    }
    function toMethodCacheKey(appId,serviceId,method){
        return appId + "-" + serviceId + "-" + method;
    }
    function guessMock(success,error,mock){
        if(mock){
            return mock;
        }else if(success && typeof success == 'string'){
            return success;
        }else if(error && typeof error == 'string'){
            return error;
        }else return "";
    }
    function guessSuccess(success,error,mock){
        if(success && typeof success == 'function'){
            return success;
        }else return null;
    }
    function guessError(success,error,mock){
        if(error && typeof error == 'function'){
            return error;
        }else return null;
    }

    function eosPromise(opts){
        //外部不让传版本，只能先调用register注册
        var methodCacheKey = toMethodCacheKey(opts.appId,opts.serviceId,opts.method);
        if(opts.version){
            throw new Error("服务方法["+ methodCacheKey +"]版本号不允许外部传递，请使用用Eos.register注册");
        }
        opts.version = methodCache[methodCacheKey];
        if(!opts.version){
            throw new Error("请先使用用Eos.register注册["+ methodCacheKey +"]版本号");
        }
        var option = extend({
            "dataType": "json",
            "url": requestUrl,
            "appId": "",
            "serviceId": "",
            "method": "",
            "version": "",
            "mock": "",
            "data": null,
            "async": true,
            "beforeSend": null,
            "complete": null,
            "success": null,
            "error":null
        }, opts);
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
        var promise = ajax({
            type: "post",
            url: option.url + "&r=" + Math.random(),
            dataType: option.dataType,
            data: params,
            async: option.async,
            beforeSend: option.beforeSend,
            complete: option.complete
        });
        if(option.success){
            promise.then(option.success,option.error);
        }else{
            return promise;
        }
    }

    function Eos(){
    }
    //eos版本
    Eos.prototype.version = 3;
    Eos.prototype.rewriteUrl = function(url){
        requestUrl = url
    }
    Eos.prototype.register = function(appId,serviceId,method,v){
        var methodCacheKey = toMethodCacheKey(appId,serviceId,method);
        if(methodCache[methodCacheKey]){
            throw new Error("不允许重复注册["+ methodCacheKey +"]版本号");
        }
        methodCache[methodCacheKey] = v;
    }
    Eos.prototype.newRequest = function(opts){
        return eosPromise(opts);
    }
    Eos.prototype.utils = {
        getContextPath:getContextPath,
        extend:extend,
        guessMock:guessMock,
        guessSuccess:guessSuccess,
        guessError:guessError
    };

    // Browser globals
    var instance = window.Eos = new Eos();
    return instance;
}));

