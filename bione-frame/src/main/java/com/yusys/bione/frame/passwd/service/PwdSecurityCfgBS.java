package com.yusys.bione.frame.passwd.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yusys.bione.comp.service.BaseBS;
import com.yusys.bione.frame.passwd.entity.BionePwdSecurityCfg;

@Service("pwdSecurityCfgBS")
@Transactional(readOnly = true)
public class PwdSecurityCfgBS extends BaseBS<BionePwdSecurityCfg>{

}
