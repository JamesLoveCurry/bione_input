package com.yusys.bione.frame.authobj.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yusys.bione.comp.service.BaseBS;
import com.yusys.bione.frame.authobj.entity.BioneRoleInfoExt;

@Service("roleExtBS")
@Transactional(readOnly = true)
public class RoleExtBS  extends BaseBS<BioneRoleInfoExt> {

}
