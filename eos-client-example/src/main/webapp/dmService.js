/** 
* 基础 - 代码管理 
* dmService - 813 
*/
"use strict";
(function (factory) {
    if (typeof define === 'function' && define.amd) {
        define(["eos"],factory);
    } else {
        factory(eos);
    }
}(function (eos) {

    var APP_ID = "auth",SERVICE_ID = "dmService";

    eos.registerService(APP_ID,SERVICE_ID)
        .registerMethod("getAllData2Map","1.0",["dmName"])
        .registerMethod("getAllData","1.0",["dmName"])
        .registerMethod("getMultiLabelByKeyListMap","1.0",["keys","keyColumn"])
        .registerMethod("getMultiLabelByKeyList","1.0",["dmName","keys"])
        .registerMethod("getLabelByKey","1.0",["dmName","key"])
        .registerMethod("getMultiLabels","1.0",["dmName","keys"])
        .registerMethod("refreshBM","1.0",["dmName"]);

    return eos[APP_ID][SERVICE_ID];
}));