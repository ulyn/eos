/**
 * @(#)SpringPropReaderConverter
 * 版权声明 厦门畅享信息技术有限公司, 版权所有 违者必究
 *
 *<br> Copyright:  Copyright (c) 2016
 *<br> Company:厦门畅享信息技术有限公司
 *<br> @author ulyn
 *<br> 16-5-24 下午2:49
 *<br> @version 1.0
 *————————————————————————————————
 *修改记录
 *    修改者：
 *    修改时间：
 *    修改原因：
 *————————————————————————————————
 */
package com.sunsharing.eos.common.config.loader;

import com.alibaba.fastjson.JSONObject;
import com.sunsharing.component.resvalidate.config.ext.SpringHttpConfig;
import com.sunsharing.component.resvalidate.config.loader.JsonConfigConverter;
import com.sunsharing.component.resvalidate.exception.LoadConfigException;

import java.util.List;

/**
 * <pre></pre>
 * <br>----------------------------------------------------------------------
 * <br> <b>功能描述:</b>
 * <br> 基于配置框架的spring扩展
 * <br> 注意事项:
 * <br>
 * <br>
 * <br>----------------------------------------------------------------------
 * <br>
 */
public class SpringPropReaderConverter
        extends JsonConfigConverter implements PropReaderConverter {

    @Override
    public List<String> loadConfigText(Object refBean, String[] values) throws LoadConfigException {
        return SpringHttpConfig.getTexts();
    }
}

