/**
 * @(#)MainServiceImpl
 * 版权声明 厦门畅享信息技术有限公司, 版权所有 违者必究
 *
 *<br> Copyright:  Copyright (c) 2016
 *<br> Company:厦门畅享信息技术有限公司
 *<br> @author ulyn
 *<br> 16-3-2 下午3:37
 *<br> @version 1.0
 *————————————————————————————————
 *修改记录
 *    修改者：
 *    修改时间：
 *    修改原因：
 *————————————————————————————————
 */
package test.sunsharing.eos.server;

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
public class MainServiceImpl implements MainService {


    @Override
    public void testEmptyArgs() {
        System.out.println("testEmptyArgs");
    }

    @Override
    public int testInt(int i) {
        return i;
    }

    @Override
    public double testDouble(double d) {
        return d;
    }

    @Override
    public float testFloat(float f) {
        return f;
    }

    @Override
    public String testString(String s, String sw) {
        return "from server : " + s + " sw:" + sw;
    }

    @Override
    public Map testMap(Map m, String l2) {
        Map map = new HashMap();
        map.put("m",m);
        map.put("l2",l2);
        return map;
    }

    @Override
    public List testListMap(List list) {
        return list;
    }

    @Override
    public void testVoid(String name) {
        System.out.println("testVoid:"+name);
    }
}

