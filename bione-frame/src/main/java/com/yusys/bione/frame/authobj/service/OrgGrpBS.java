package com.yusys.bione.frame.authobj.service;

/**
 * @项目名称： 统一监管报送
 * @类名称： OrgGrpBS
 * @类描述:
 * @功能描述:
 * @创建人: huzq1
 * @创建时间: 2022/01/06 10:44
 * @修改备注:
 * @修改记录: 修改时间  修改人员  修改原因
 * ---------------------------------------------------------
 * @Version 1.0.0
 * @Copyright (c) 宇信科技-版权所有
 */

import com.google.common.collect.Maps;
import com.yusys.bione.comp.entity.page.Pager;
import com.yusys.bione.comp.repository.mybatis.PageHelper;
import com.yusys.bione.comp.repository.mybatis.PageMyBatis;
import com.yusys.bione.comp.service.BaseBS;
import com.yusys.bione.frame.authobj.entity.BioneGrpOrgRel;
import com.yusys.bione.frame.authobj.entity.BioneOrgGrp;
import com.yusys.bione.frame.authobj.repository.OrgGrpMybatisDao;
import com.yusys.bione.frame.authobj.web.vo.BioneOrgGrpVO;
import com.yusys.bione.frame.security.BioneSecurityUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service("orgGrpBS")
@Transactional(readOnly = true)
public class OrgGrpBS extends BaseBS<BioneOrgGrp> {

    @Autowired
    private OrgGrpMybatisDao orgGrpMybatisDao;

    public PageMyBatis<BioneOrgGrpVO> list(Pager pager) {
        PageHelper.startPage(pager);
        Map<String, Object> params = Maps.newHashMap();
        if(!BioneSecurityUtils.getCurrentUserInfo().isSuperUser()){

        }
        PageMyBatis<BioneOrgGrpVO> list = (PageMyBatis<BioneOrgGrpVO>) orgGrpMybatisDao.list(params);
        return list;
    }

    public boolean checkName(String grpNm, String collectionId) {
        Map<String, String> params = Maps.newHashMap();
        params.put("collectionName", grpNm);
        List<BioneOrgGrp> list = orgGrpMybatisDao.checkName(params);
        if (list == null || list.size() == 0) {
            return true;
        } else {
            return false;
        }
    }

    public BioneOrgGrp getRptById(String groupId) {
        return orgGrpMybatisDao.getRptById(groupId);
    }

    public void editGroup(BioneOrgGrp orgGrp) {
        orgGrpMybatisDao.editGroup(orgGrp);
    }

    public void saveGroup(BioneOrgGrp orgGrp) {
        orgGrpMybatisDao.saveGroup(orgGrp);
    }

    public Boolean delOrg(String groupId) {
        boolean flag = true;
        String[] ids = StringUtils.split(groupId, ",");
        List<String> list = new ArrayList<>(Arrays.asList(ids));
        HashMap<String, Object> params = Maps.newHashMap();
        params.put("list", list);
        if(list.size()>0){
            orgGrpMybatisDao.delOrg(params);
            orgGrpMybatisDao.delObjs(params);
        }
        return flag;
    }

    public void saveorgobj(String grpId, String ids) {
        orgGrpMybatisDao.delObj(grpId);
        if (StringUtils.isNotBlank(ids)) {
            String[] objids = StringUtils.split(ids, ',');
            for (int i = 0; i < objids.length; i++) {
                BioneGrpOrgRel bioneGrpOrgRel = new BioneGrpOrgRel();
                bioneGrpOrgRel.setGrpId(grpId);
                bioneGrpOrgRel.setOrgNo(objids[i]);
                orgGrpMybatisDao.saveObj(bioneGrpOrgRel);
            }
        }
    }

    public List<String> getOrgList(String grpId) {
        List<String> list = orgGrpMybatisDao.searchbyId(grpId);
        return list;
    }
}
