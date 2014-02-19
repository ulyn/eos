package com.sunsharing.component.test;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Created by criss on 14-2-11.
 */
@Service
public class TestServiceImpl implements TestType {
    @Override
    public int testInt(int i) {
        return 0;
    }

    @Override
    public double testDouble(double d) {
        return 0;
    }

    @Override
    public float testFloat(float f) {
        return 0;
    }

    @Override
    public String testString(String s, String sw) {
        return "我是正真的服务方";
    }

    @Override
    public Map testMap(Map m, String l2) {
        return null;
    }

    @Override
    public List testListMap(List list) {
        return null;
    }

    public void testVoid(String name)
    {

    }
}