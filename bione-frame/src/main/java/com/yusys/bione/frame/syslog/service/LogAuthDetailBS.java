package com.yusys.bione.frame.syslog.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yusys.bione.comp.service.BaseBS;
import com.yusys.bione.frame.syslog.entity.BioneLogAuthDetail;

@Service
@Transactional(readOnly = true)
public class LogAuthDetailBS extends BaseBS<BioneLogAuthDetail>{

	
}