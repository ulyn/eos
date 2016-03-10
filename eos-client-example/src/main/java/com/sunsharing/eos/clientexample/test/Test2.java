package com.sunsharing.eos.clientexample.test;

import com.sunsharing.eos.common.annotation.EosService;
import com.sunsharing.eos.common.annotation.ParameterNames;

/**
 * Created by criss on 14-2-14.
 */
@EosService( appId = "test", id = "test2")
public interface Test2 {

    /**
     * @param abc
     * @return ${hexin}
     * 何鑫
     */
    @ParameterNames(value = {"abc"})
    String sayHello(String abc);
}
