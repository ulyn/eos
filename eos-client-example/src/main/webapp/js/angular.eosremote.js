angular.module('eos.services', ['ngResource']).
    factory('eosRemote', ['$http',
        function ($http) {
            var eosRemoteSetup = {
                "dataType": "jsonp", //jsonp,json,默认是json,当涉及跨域使用jsonp
                "url": "http://192.168.0.169:9080/jedi/remote",
                "serviceId": "",
                "method": "",
                "mock": "",
                "data": null,
                "async": true,
                "jsonp": "eos_jsonp_callback", //当dataType是jsonp时候此参数生效，不要更改它
                "beforeSend": function (XHR) {
                },
                "success": function (data, textStatus, headers, config) {
                },
                "error": function (data, textStatus, headers, config) {
                    if (console) {
                        console.info(data, textStatus, headers, config);
                    }
                }
            };
            return function (opts) {
                var option = {};
                for (var key in eosRemoteSetup) {
                    option[key] = eosRemoteSetup[key];
                }
                for (var key in opts) {
                    option[key] = opts[key];
                }
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
                if (option.dataType == "json") {
                    option.method = "post";
                } else {
                    option.method = "jsonp";
                    option.url = option.url + "&eos_jsonp_callback=JSON_CALLBACK";
                }
                $http({
                    method: option.method,
                    url: option.url,
                    params: params,
                    beforeSend: option.beforeSend
                }).success(function (data, textStatus, headers, config) {
                        if (data.status) {
                            option.success(data.result, textStatus, headers, config);
                        } else {
                            //内部处理出错
                            option.error(data.result, textStatus, headers, config);
                        }
                    })
                    .error(function (data, textStatus, headers, config) {
                        if (console) {
                            console.info(data, textStatus, headers, config);
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
                            option.error(XMLHttpRequest, textStatus, errorThrown, a, d, s, g);
                        }
                    });
            };
        }]
    );


