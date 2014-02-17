/**
 * @(#)AdviceResult
 * 版权声明 厦门畅享信息技术有限公司, 版权所有 违者必究
 *
 *<br> Copyright:  Copyright (c) 2014
 *<br> Company:厦门畅享信息技术有限公司
 *<br> @author ulyn
 *<br> 14-2-17 下午11:45
 *<br> @version 1.0
 *————————————————————————————————
 *修改记录
 *    修改者：
 *    修改时间：
 *    修改原因：
 *————————————————————————————————
 */
package com.sunsharing.eos.common.aop;

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
public class AdviceResult {
    //是否马上返回值，不再继续往下执行
    private boolean rightNowRet = false;
    //返回值
    private Object returnVal;

    public AdviceResult() {
    }

    public AdviceResult(boolean rightNowRet, Object returnVal) {
        this.rightNowRet = rightNowRet;
        this.returnVal = returnVal;
    }

    public boolean isRightNowRet() {
        return rightNowRet;
    }

    public void setRightNowRet(boolean rightNowRet) {
        this.rightNowRet = rightNowRet;
    }

    public Object getReturnVal() {
        return returnVal;
    }

    public void setReturnVal(Object returnVal) {
        this.returnVal = returnVal;
    }
}

