package com.sunsharing.eos.serverexample.test;

import com.sunsharing.eos.common.annotation.EosService;

/**
 * Created by criss on 14-2-14.
 */
@EosService(version = "1.0")
public interface Test2 {

    /**
     * @param abc
     * @return ${hexin}
     * 何鑫
     */
    String sayHello(String abc);
}
