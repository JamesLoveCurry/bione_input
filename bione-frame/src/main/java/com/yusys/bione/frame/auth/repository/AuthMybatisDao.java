package com.yusys.bione.frame.auth.repository;

import com.yusys.bione.comp.repository.mybatis.MyBatisRepository;
import com.yusys.bione.frame.auth.entity.BioneAuthObjDef;
import com.yusys.bione.frame.auth.entity.BioneAuthObjResRel;
import com.yusys.bione.frame.auth.entity.BioneAuthObjResRelPK;
import com.yusys.bione.frame.auth.entity.BioneAuthResDef;
import com.yusys.bione.frame.authobj.entity.BioneDeptInfo;
import com.yusys.bione.frame.authobj.entity.BioneOrgInfo;
import com.yusys.bione.frame.authobj.entity.BioneRoleInfo;
import com.yusys.bione.frame.user.entity.BioneUserInfo;

import java.util.List;
import java.util.Map;

/**
 * 通过@MapperScannerConfigurer扫描目录中的所有接口, 动态在Spring Context中生成实现.
 * 方法名称必须与Mapper.xml中保持一致.
 *
 * @author calvin
 */
@MyBatisRepository
public interface AuthMybatisDao {
    List<BioneAuthObjDef> search();

    /**
     * 查询用户拥有的角色
     *
     * @param userId
     * @return
     */
    List<BioneRoleInfo> getBioneRoleList(String userId);

    /**
     * 查询用户拥有的部门
     *
     * @param deptId
     * @return
     */
    List<BioneDeptInfo> getBioneDept(String deptId);

    /**
     * 查询用户拥有的机构
     *
     * @param orgId
     * @return
     */
    List<BioneOrgInfo> getBioneOrgInfo(String orgId);

    /**
     * 查询角色类型
     *
     * @return
     */
    List<BioneAuthObjDef> getBioneAuthObjectInfo();

    /**
     * 查询资源类型
     *
     * @return
     */
    List<BioneAuthResDef> getBioneAuthResDefInfo(Map<String,Object> param);

    /**
     * 查询资源类型
     *
     * @return
     */
    List<BioneAuthResDef> getBioneResDefInfo(Map<String,Object> param);

    /**
     * 查询用户资源
     *
     * @return
     */
    List<BioneAuthObjResRelPK> getUserBioneResDefInfo(Map<String,Object> param);

    /**
     * 查询用户资源
     *
     * @return
     */
    List<BioneAuthObjResRelPK> getUserAllBioneResDefInfo(Map<String,Object> param);

    /**
     * 查询用户资源
     *
     * @return
     */
    List<BioneUserInfo> getBioneRoleUserList(Map<String,Object> param);

    List<BioneAuthObjResRel> getBioneAuthObjResRel(Map<String, Object> param);
}
