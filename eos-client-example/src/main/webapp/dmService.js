"use strict";
(function (factory) {
    if (typeof define === 'function' && define.amd) {
        // AMD. Register as an anonymous module.
        define("eos.test.dmService",["eos"],factory);
    } else {
        // Browser globals
        factory(eos);
    }
}(function (eos) {

    var APP_ID = "test",SERVICE_ID = "dmService";

    eos.registerService(APP_ID,SERVICE_ID)
        .registerMethod("getAllData","1.0",["dmName"])
        .registerMethod("getMultiLabelByKeyList","1.0",["dmName","keys"]);

    return eos[APP_ID][SERVICE_ID];
}));

