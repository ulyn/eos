/**
 * @(#)TestServiceImpl
 * 版权声明 厦门畅享信息技术有限公司, 版权所有 违者必究
 *
 *<br> Copyright:  Copyright (c) 2014
 *<br> Company:厦门畅享信息技术有限公司
 *<br> @author ulyn
 *<br> 14-1-31 下午9:39
 *<br> @version 1.0
 *————————————————————————————————
 *修改记录
 *    修改者：
 *    修改时间：
 *    修改原因：
 *————————————————————————————————
 */
package com.sunsharing.eos.server.test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <pre></pre>
 * <br>----------------------------------------------------------------------
 * <br> <b>功能描述:</b>
 * <br>
 * <br> 注意事项:
 * <br>
 * <br>
 * <br>----------------------------------------------------------------------
 * <br>
 */
public class TestServiceImpl implements TestService {
    @Override
    public String sayHello(String name) {
        return name + "，你好，我是ulyn";
    }

    @Override
    public List getList(int num) {
        List list = new ArrayList();
        for (int i = 0; i < num; i++) {
            Map map = new HashMap();
            map.put("test1", "2323");
            map.put("test2", "2323");
            map.put("test3", "2323");
            map.put("test4", "2323");
            map.put("test5", "2323");
            list.add(map);
        }
        return list;
    }
}

