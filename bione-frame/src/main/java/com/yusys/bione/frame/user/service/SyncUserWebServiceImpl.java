package com.yusys.bione.frame.user.service;


import cn.com.crc.esb.sysncuser.webservice.*;
import com.yusys.bione.frame.security.BioneSecurityUtils;
import com.yusys.bione.frame.user.entity.BioneUserInfo;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.jws.WebService;
import javax.xml.ws.BindingType;
import javax.xml.ws.soap.SOAPBinding;
import java.sql.Timestamp;
import java.util.Date;
import java.util.UUID;


/**
 * 提供给LDAP的webService接口实现类
 */
/*@WebService(serviceName = "syncUserWebService",name = "syncUserWebService", targetNamespace = "http://service.user.frame.bione.yusys.com")
@BindingType(value = SOAPBinding.SOAP12HTTP_BINDING)
@Service*/
public class SyncUserWebServiceImpl extends SyncUser implements ISyncUserWebAfterService {
    private final Logger log = LoggerFactory.getLogger(SyncUserWebServiceImpl.class);

    @Autowired
    private UserBS userBS;

    /**
     * 提供给LDAP的webService接口
     * @param accpetData
     * @return
     */
    @Override
    public ReturnData synchronism(AccpetData accpetData) {
        /**
         * LDAP直接调用这个接口
         * 调用这个接口首先走isAuth进行登录验证
         * 验证之后通过SyncXmlUtils.getOpType来判断LDAP进行的是哪个方法
         */
        ReturnData returnData = super.process(accpetData);
        return returnData;
    }

    /**
     * 验证方法
     * @return
     */
    @Override
    public boolean isAuth() {
        // 验证esb的用户名和密码
//        if (!super.soapWsUser.equals("esb") || !super.soapWsPwd.equals("BC5E76C7FC5C0350968D118C7ED5F0A0")){
//            return false;
//        }
        return true;
    }

    /**
     * 新增
     * @param syncUserInfo
     * @return
     */
    @Override
    public ReturnData createAccount(SyncUserInfo syncUserInfo) {
        ReturnData responseParm = new ReturnData();
        // 判断数据是否为空
        log.info("新增用户,{}",syncUserInfo.getLogin() + ":" + syncUserInfo.getPassword());
        if (syncUserInfo.getLogin() == null || "".equals(syncUserInfo.getLogin())){
            responseParm.setReturn_code(Response.ESB_TOKEN_ERROR);
            responseParm.setReturn_desc("必填数据不能为空");
            return responseParm;
        }
        //if ((syncUserInfo.getFirstName()+syncUserInfo.getLastName()))
//        if (syncUserInfo.getDisplayName()==null||"".equals(syncUserInfo.getDisplayName())){
//            responseParm.setReturn_code(Response.ESB_TOKEN_ERROR);
//            responseParm.setReturn_desc("必填数据不能为空");
//            return responseParm;
//        }
        if(syncUserInfo.getPassword()==null||"".equals(syncUserInfo.getPassword())){
            responseParm.setReturn_code(Response.ESB_TOKEN_ERROR);
            responseParm.setReturn_desc("必填数据不能为空");
            return responseParm;
        }
//        if (syncUserInfo.getEmpno()==null||"".equals(syncUserInfo.getEmpno())){
//            responseParm.setReturn_code(Response.ESB_TOKEN_ERROR);
//            responseParm.setReturn_desc("必填数据不能为空");
//            return responseParm;
//        }
//        if (syncUserInfo.getDeptID()==null||"".equals(syncUserInfo.getDeptID())){
//            responseParm.setReturn_code(Response.ESB_TOKEN_ERROR);
//            responseParm.setReturn_desc("必填数据不能为空");
//            return responseParm;
//        }
        BioneUserInfo authUser1 = null;
        authUser1 = userBS.getEntityById(BioneUserInfo.class , syncUserInfo.getLogin().toLowerCase());
        BioneUserInfo uaaUser = new BioneUserInfo();
        if(authUser1 != null){
            log.info("新增用户已存在,{}",syncUserInfo.getLogin() + ":" + syncUserInfo.getPassword());
            responseParm.setReturn_code(Response.USER_SEQUENE_GENERATE_ERROR);
            responseParm.setReturn_desc("用户已经存在");
            uaaUser.setUserSts("A");
            // 覆盖
            uaaUser.setUserNo(syncUserInfo.getLogin().toLowerCase());//用户名称
            String password = syncUserInfo.getPassword();
            password = BioneSecurityUtils.getHashedPasswordBase64(password);
            uaaUser.setUserPwd(password);//密码
            uaaUser.setUserName(syncUserInfo.getLastName()+syncUserInfo.getFirstName());//用户名称
            uaaUser.setLastName(syncUserInfo.getLastName());//姓氏
            uaaUser.setLastNameCHN(syncUserInfo.getLastNameCHN());//姓拼音
            uaaUser.setFirstName(syncUserInfo.getFirstName());////名字
            uaaUser.setFirstNameCHN(syncUserInfo.getFirstNameCHN());//名拼音
            uaaUser.setNationalIDLast4(syncUserInfo.getNationalIDLast4());//身份证后4位
            uaaUser.setEmail(syncUserInfo.getEmail()); //电子邮箱
            uaaUser.setBuid(syncUserInfo.getBuid());//利润中心编码
            uaaUser.setGuid(syncUserInfo.getGuid());//LDAP标识
            uaaUser.setSetId(syncUserInfo.getSetID());//组织编码
            //String id = UUID.randomUUID().toString().replaceAll("-", "");
            uaaUser.setUserId(authUser1.getUserId());//账户ID
            uaaUser.setDeptNo(syncUserInfo.getDeptID());
            uaaUser.setOrgNo(syncUserInfo.getDeptID());//部门编号
            uaaUser.setMobile(syncUserInfo.getTelephone());//手机
            uaaUser.setUserCode(syncUserInfo.getEmpno());//员工编号

            uaaUser.setLogicSysNo("FRS");
            uaaUser.setLastPwdUpdateTime(Timestamp.valueOf(DateFormatUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss")));
            //设置用户状态
            uaaUser.setUserSts("1");
            uaaUser.setIsBuiltin("0");
            uaaUser.setLastUpdateTime(Timestamp.valueOf(DateFormatUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss")));//最新变更时间
            uaaUser.setLastUpdateUser(syncUserInfo.getLogin());//更改人
            log.info("插入库中ussUser------------------------>" + uaaUser.toString());
            userBS.saveUserInfo(uaaUser);

            responseParm.setReturn_code(Response.SUCCESS);
            responseParm.setReturn_desc("创建用户成功");
            return responseParm;
        }
        //创建用户
        log.info("新增用户不存在，执行新增,{}",syncUserInfo.getLogin() + ":" + syncUserInfo.getPassword());
        uaaUser.setUserNo(syncUserInfo.getLogin().toLowerCase());//用户名称
        String password = syncUserInfo.getPassword();
        password = BioneSecurityUtils.getHashedPasswordBase64(password);
        uaaUser.setUserPwd(password);//密码
        uaaUser.setUserName(syncUserInfo.getLastName()+syncUserInfo.getFirstName());//用户名称
        uaaUser.setLastName(syncUserInfo.getLastName());//姓氏
        uaaUser.setLastNameCHN(syncUserInfo.getLastNameCHN());//姓拼音
        uaaUser.setFirstName(syncUserInfo.getFirstName());////名字
        uaaUser.setFirstNameCHN(syncUserInfo.getFirstNameCHN());//名拼音
        uaaUser.setNationalIDLast4(syncUserInfo.getNationalIDLast4());//身份证后4位
        uaaUser.setEmail(syncUserInfo.getEmail()); //电子邮箱
        uaaUser.setBuid(syncUserInfo.getBuid());//利润中心编码
        uaaUser.setGuid(syncUserInfo.getGuid());//LDAP标识
        uaaUser.setSetId(syncUserInfo.getSetID());//组织编码
        //String id = UUID.randomUUID().toString().replaceAll("-", "");
        uaaUser.setUserId(syncUserInfo.getLogin().toLowerCase());//账户ID
        uaaUser.setDeptNo(syncUserInfo.getDeptID());
        uaaUser.setOrgNo(syncUserInfo.getDeptID());//部门编号
        uaaUser.setMobile(syncUserInfo.getTelephone());//手机
        uaaUser.setUserCode(syncUserInfo.getEmpno());//员工编号

        uaaUser.setLogicSysNo("FRS");
        uaaUser.setLastPwdUpdateTime(Timestamp.valueOf(DateFormatUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss")));
        //设置用户状态
        uaaUser.setUserSts("1");
        uaaUser.setIsBuiltin("0");
        uaaUser.setLastUpdateTime(Timestamp.valueOf(DateFormatUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss")));//最新变更时间
        uaaUser.setLastUpdateUser(syncUserInfo.getLogin());//更改人
        //uaaUser.setLastChgDt(DateFormatUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss"));//最新变更时间
//        uaaUserMapper.updateUserLoginInfo(uaaUser);
        log.info("插入库中ussUser------------------------>" + uaaUser.toString());
        userBS.saveUserInfo(uaaUser);
        responseParm.setReturn_code(Response.SUCCESS);
        responseParm.setReturn_desc("创建用户成功");
        return responseParm;
    }

    /**
     * 修改
     * @param syncUserInfo
     * @return
     */
    @Override
    public ReturnData editAccount(SyncUserInfo syncUserInfo) {
        ReturnData responseParm = new ReturnData();
        // 判断数据是否为空
        if(syncUserInfo.getLogin()==null||"".equals(syncUserInfo.getLogin())){
            responseParm.setReturn_code(Response.ESB_TOKEN_ERROR);
            responseParm.setReturn_desc("必填数据不能为空");
            return responseParm;
        }
//        if (syncUserInfo.getDisplayName()==null||"".equals(syncUserInfo.getDisplayName())){
//            responseParm.setReturn_code(Response.ESB_TOKEN_ERROR);
//            responseParm.setReturn_desc("必填数据不能为空");
//            return responseParm;
//        }
        if(syncUserInfo.getPassword()==null||"".equals(syncUserInfo.getPassword())){
            responseParm.setReturn_code(Response.ESB_TOKEN_ERROR);
            responseParm.setReturn_desc("必填数据不能为空");
            return responseParm;
        }
//        if (syncUserInfo.getEmpno()==null||"".equals(syncUserInfo.getEmpno())){
//            responseParm.setReturn_code(Response.ESB_TOKEN_ERROR);
//            responseParm.setReturn_desc("必填数据不能为空");
//            return responseParm;
//        }
//        if (syncUserInfo.getDeptID()==null||"".equals(syncUserInfo.getDeptID())){
//            responseParm.setReturn_code(Response.ESB_TOKEN_ERROR);
//            responseParm.setReturn_desc("必填数据不能为空");
//            return responseParm;
//        }
        BioneUserInfo uaaUser = new BioneUserInfo();
        BioneUserInfo authUser2 = userBS.getEntityById(BioneUserInfo.class , syncUserInfo.getLogin().toLowerCase());
        if (authUser2 == null) {
            responseParm.setReturn_code(Response.USER_UNEXIST);
            responseParm.setReturn_desc("用户不存在");
            return responseParm;
        }
        BeanUtils.copyProperties(authUser2,uaaUser);
        if(syncUserInfo.getPassword()!=null&&!"".equals(syncUserInfo.getPassword())){
            String password = syncUserInfo.getPassword();
            password = BioneSecurityUtils.getHashedPasswordBase64(password);
            uaaUser.setUserPwd(password);//密码
            uaaUser.setLastPwdUpdateTime(Timestamp.valueOf(DateFormatUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss")));
        }
        if(syncUserInfo.getLastName()!=null&&!"".equals(syncUserInfo.getLastName())){
            uaaUser.setLastName(syncUserInfo.getLastName()); //姓氏
        }
        if(syncUserInfo.getLastNameCHN()!=null&&!"".equals(syncUserInfo.getLastNameCHN())){
            uaaUser.setLastNameCHN(syncUserInfo.getLastNameCHN());//姓拼音
        }
        if(syncUserInfo.getFirstName()!=null&&!"".equals(syncUserInfo.getFirstName())){
            uaaUser.setFirstName(syncUserInfo.getFirstName());//名字
        }
        if(syncUserInfo.getFirstNameCHN()!=null&&!"".equals(syncUserInfo.getFirstNameCHN())){
            uaaUser.setFirstNameCHN(syncUserInfo.getFirstNameCHN()); //名拼音
        }
//        if(syncUserInfo.getLastName()!=null&&!"".equals(syncUserInfo.getLastName())&&syncUserInfo.getFirstName()!=null
//                &&!"".equals(syncUserInfo.getFirstName())){
//            uaaUser.setName(syncUserInfo.getLastName()+syncUserInfo.getFirstName());//姓氏+名字
//        }
        if(syncUserInfo.getNationalIDLast4()!=null&&!"".equals(syncUserInfo.getNationalIDLast4())){
            uaaUser.setNationalIDLast4(syncUserInfo.getNationalIDLast4());//身份证后4位
        }
        if(syncUserInfo.getEmail()!=null&&!"".equals(syncUserInfo.getEmail())){
            uaaUser.setEmail(syncUserInfo.getEmail());//电子邮箱
        }
        if(syncUserInfo.getBuid()!=null&&!"".equals(syncUserInfo.getBuid())){
            uaaUser.setBuid(syncUserInfo.getBuid()); //利润中心编码
        }
        if (syncUserInfo.getGuid()!=null&&!"".equals(syncUserInfo.getGuid())){
            uaaUser.setGuid(syncUserInfo.getGuid());//账户ID
        }
        if (syncUserInfo.getSetID()!=null&&!"".equals(syncUserInfo.getSetID())){
            uaaUser.setSetId(syncUserInfo.getSetID());//组织编号
        }
        if (syncUserInfo.getTelephone()!=null&&!"".equals(syncUserInfo.getTelephone())){
            uaaUser.setMobile(syncUserInfo.getTelephone());//手机
        }
        if (syncUserInfo.getDeptID()!= null && !"".equals(syncUserInfo.getDeptID())){
            uaaUser.setOrgNo(syncUserInfo.getDeptID());
        }
        //uaaUser.setLastUpdateTime(DateFormatUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss"));//最近一次修改密码时间
        //uaaUser.setLastLoginTime(DateFormatUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss"));//最近登录时间
//        uaaUser.setOffenIP("192.168.45.27");//常用IP，逗号分隔
        uaaUser.setLastUpdateUser(syncUserInfo.getLogin());//最新变更用户
        uaaUser.setLastUpdateTime(Timestamp.valueOf(DateFormatUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss")));//最新变更时间
        uaaUser.setLogicSysNo("FRS");
        log.info("插入库中ussUser------------------------>" + uaaUser.toString());
        userBS.saveUserInfo(uaaUser);
        responseParm.setReturn_code(Response.SUCCESS);
        responseParm.setReturn_desc("修改用户成功");
        return responseParm;
    }

    /**
     * 禁用
     * @param syncUserInfo
     * @return
     */
    @Override
    public ReturnData disableAccount(SyncUserInfo syncUserInfo) {
        ReturnData responseParm = new ReturnData();
        if(syncUserInfo.getLogin()==null||"".equals(syncUserInfo.getLogin())){
            responseParm.setReturn_code(Response.ESB_TOKEN_ERROR);
            responseParm.setReturn_desc("必填数据不能为空");
            return responseParm;
        }
        BioneUserInfo uaaUser = new BioneUserInfo();
        BioneUserInfo authUser3 = userBS.getEntityById(BioneUserInfo.class , syncUserInfo.getLogin().toLowerCase());
        if (authUser3 == null){
            responseParm.setReturn_code(Response.USER_UNEXIST);
            responseParm.setReturn_desc("用户不存在");
            return responseParm;
        }
        //uaaUser.setUserId(authUser3.getUserId());
        //uaaUser.setLoginCode(syncUserInfo.getLogin());
        //uaaUser.setLastChgUsr(syncUserInfo.getLogin());
        //uaaUser.setUserSts("0");
        userBS.changeUserSts(authUser3.getUserId(), "0");
        responseParm.setReturn_code(Response.SUCCESS);
        responseParm.setReturn_desc("禁用成功");
        return responseParm;

    }

    /**
     * 启动
     * @param syncUserInfo
     * @return
     */
    @Override
    public ReturnData enableAccount(SyncUserInfo syncUserInfo) {
        ReturnData responseParm = new ReturnData();
        if (syncUserInfo.getLogin()==null || "".equals(syncUserInfo.getLogin())){//用户登录
            responseParm.setReturn_code(Response.ESB_TOKEN_ERROR);
            responseParm.setReturn_desc("必填数据不能为空");
            return responseParm;
        }
        BioneUserInfo authUser4 = userBS.getEntityById(BioneUserInfo.class , syncUserInfo.getLogin().toLowerCase());
        if (authUser4 == null) {
            responseParm.setReturn_code(Response.USER_UNEXIST);
            responseParm.setReturn_desc("用户不存在");
            return responseParm;
        }
      /*  BioneUserInfo uaaUser = new BioneUserInfo();
        uaaUser.setLoginCode(syncUserInfo.getLogin());
        uaaUser.setLastChgUsr(syncUserInfo.getLogin());
        uaaUser.setUserSts("1");*/
        userBS.changeUserSts(authUser4.getUserId(), "1");
        responseParm.setReturn_code(Response.SUCCESS);
        responseParm.setReturn_desc("启用成功");
        return responseParm;
    }

    /**
     * 删除
     * @param syncUserInfo
     * @return
     */
    @Override
    public ReturnData deleteAccount(SyncUserInfo syncUserInfo) {
        ReturnData responseParm = new ReturnData();
        if (syncUserInfo.getLogin()==null || "".equals(syncUserInfo.getLogin())){
            responseParm.setReturn_code(Response.ESB_TOKEN_ERROR);
            responseParm.setReturn_desc("必填数据不能为空");
            return responseParm;
        }
        BioneUserInfo authUser5 = userBS.getEntityById(BioneUserInfo.class , syncUserInfo.getLogin().toLowerCase());
        if (authUser5 == null){
            responseParm.setReturn_code(Response.USER_UNEXIST);
            responseParm.setReturn_desc("用户不存在");
            return responseParm;
        }
        BioneUserInfo uaaUser = new BioneUserInfo();
        //uaaUser.setUserId(authUser5.getUserId());
       /* uaaUser.setLoginCode(syncUserInfo.getLogin());
        uaaUser.setLastChgUsr(syncUserInfo.getLogin());
        uaaUser.setUserSts("I");*/
        userBS.changeUserSts(authUser5.getUserId(), "0");
        responseParm.setReturn_code(Response.SUCCESS);
        responseParm.setReturn_desc("删除成功");
        return responseParm;
    }



}
