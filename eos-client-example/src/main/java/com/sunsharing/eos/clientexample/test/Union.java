package com.sunsharing.eos.clientexample.test;
import com.sunsharing.eos.common.annotation.ParameterNames;

import com.sunsharing.eos.common.annotation.EosService;

import java.util.Map;

/**
 * 工会组织服务
 * Created by criss on 15/9/18.
 * version 1.1
 * 1.修改用工名册的入参
 * version 1.2
 * 1.增加行政区划
 * 2.detail函数企业登记日期改成DJRQ
 *
 */
@EosService(version="1.4",appId="ceshi",id="union")
public interface Union {

    /**
     * 查询列表
     * @param reqData
     * {
     *     "linesPerPage":"15",
     *     "currentPage":"1",
     *     "FRMC":"",//单位名称
     *     "SFCLGH":"",//0 表示否 1 表示是
     *     "BEGIN_DJRQ":"",//企业查询开始登记时间，格式:20150115
     *     "END_DJRQ":"",//企业查询登记时间，格式:20150115
     *     "QYYGRS_begin":"10", //员工人数最小值
     *     "QYYGRS_end":"100", //员工人数最大值
     *     "XZQH":"" //行政区划 如果社区ID不为空 传社区ID，如果社区为空，传街道ID
     * }
     * @return
     *
     *
     */
@ParameterNames(value = {"reqData"})
    Map list(Map reqData);


    /**
     * 获取详细
     * @param frbh 法人编号
     * @return
     * ${success}
     * {
     *     "FRMC":"企业名称",
     *     "HYDM":"所属行业",
     *     "ZCDZ":"注册地址",
     *     "DJRQ":"企业登记日期",
     *     "ZZZB":"注册资本",
     *     "LXFS":"联系电话",
     *     "XM":"法人代表",
     *     "QYYGRS":"企业用工人数",
     *     "SFCLGH":"是否成立工会",
     *     "GHCLSJ":"工会成立日期",
     *     "GHFZR":"工会负责人",
     *     "GHLXDH":"工会联系电话",
     * }
     */
@ParameterNames(value = {"frbh"})
    Map detail(String frbh);


    /**
     * 用工名册
     * @param reqData
     * {
     *     "linesPerPage":"15",
     *     "currentPage":"1",
     *     "frbh":"法人编号"
     * }
     * @return
     * ${success}
     *
     * {
     *     "currentPage":"",
     *     "linesPerPage":"",
     *     "totalNum":"",
     *     "YGBBRS":"用工报备人数",
     *     "QYCBRS","缴交社保人数",
     *     "list":[{
     *          "XM":"姓名",
     *          "SFZH":"身份证号",
     *          "XB":"性别",
     *          "CSRQ":"出生日期",
     *          "HJSZD":"户籍地详址",
     *          "JYDJSJ":"就业登记时间",
     *          "SFYGBB":"是否用工报备",
     *          "SFCB":"是否参保"
     *     }]
     * }
     *
     *
     */
@ParameterNames(value = {"reqData"})
    Map roster(Map  reqData);


    /**
     * 更新组织
     * @param frbh 法人编号
     * @param unionCreateTime 建立时间
     * @param unionCreateUser 创建人
     * @param unionTel 创建人电话
     * @return
     * ${success}成功返回
     * true
     */
@ParameterNames(value = {"frbh","unionCreateTime","unionCreateUser","unionTel"})
    boolean updateUnion(String frbh,String unionCreateTime,String unionCreateUser,String unionTel);




}
