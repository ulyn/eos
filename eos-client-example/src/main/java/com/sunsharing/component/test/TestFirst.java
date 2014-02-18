package com.sunsharing.component.test;
import com.sunsharing.eos.common.annotation.ParameterNames;

import com.sunsharing.eos.common.annotation.EosService;

/**
 * Created by Administrator on 14-2-14.
 */
@EosService(version="1.0",appId="Firrari",id="testFirst")
public interface TestFirst {
    /**
     * 我是测试方法
     * @param myname 输入我的名字
     * @return
     * ${hexin} 当输入hexin时返回
     * 何鑫
     * ${luoll} 当输入luoll时返回
     * 骆梨梨
     */
@ParameterNames(value = {"myname"})
    String sayHello(String myname);

}
