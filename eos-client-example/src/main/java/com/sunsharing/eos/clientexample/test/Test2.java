package com.sunsharing.eos.clientexample.test;

import com.sunsharing.eos.common.annotation.EosService;
import com.sunsharing.eos.common.annotation.ParameterNames;
import com.sunsharing.eos.common.annotation.Version;

/**
 * Created by criss on 14-2-14.
 */
@EosService
public interface Test2 {

    /**
     * @param abc
     * @return ${hexin}
     * 何鑫
     */
    @Version(value = "1.0")
    String sayHello(String abc);
}
