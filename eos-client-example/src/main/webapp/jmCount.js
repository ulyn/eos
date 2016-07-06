/** 
* 基础 - 居民统计 
* jmCount - 816 
*/
"use strict";
(function (factory) {
    if (typeof define === 'function' && define.amd) {
        define(["eos"],factory);
    } else {
        factory(eos);
    }
}(function (eos) {

    var APP_ID = "building",SERVICE_ID = "jmCount";

    eos.registerService(APP_ID,SERVICE_ID)
        .registerMethod("getJmDateType","1.0",[])
        .registerMethod("basicCount","1.0",[])
        .registerMethod("jmDateCount","1.2",["countType","dateRange"]);

    return eos[APP_ID][SERVICE_ID];
}));