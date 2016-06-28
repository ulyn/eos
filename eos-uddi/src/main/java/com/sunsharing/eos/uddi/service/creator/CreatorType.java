/**
 * @(#)CreatorType
 * 版权声明 厦门畅享信息技术有限公司, 版权所有 违者必究
 *
 *<br> Copyright:  Copyright (c) 2016
 *<br> Company:厦门畅享信息技术有限公司
 *<br> @author ulyn
 *<br> 16-6-22 下午4:23
 *<br> @version 1.0
 *————————————————————————————————
 *修改记录
 *    修改者：
 *    修改时间：
 *    修改原因：
 *————————————————————————————————
 */
package com.sunsharing.eos.uddi.service.creator;

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
public enum CreatorType {
    JS(new JSCreator()),
    NodeJS(new NodeJSCreator()),
    ReduxJS(new ReduxJSCreator()),
    DynamicJava(new DynamicJavaCreator());

    private ICreator creator;

    CreatorType(ICreator creator) {
        this.creator = creator;
    }

    public ICreator getCreator() {
        return creator;
    }


}
