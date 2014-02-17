package com.sunsharing.component.test;

import org.springframework.stereotype.Service;

/**
 * Created by criss on 14-2-14.
 */
@Service
public class Test2Impl implements Test2 {


    @Override
    public String sayHello(String abc) {
        return "我是真实的";
    }
}
