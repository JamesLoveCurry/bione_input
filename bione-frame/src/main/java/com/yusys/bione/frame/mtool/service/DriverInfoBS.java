package com.yusys.bione.frame.mtool.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yusys.bione.comp.service.BaseBS;
import com.yusys.bione.frame.mtool.entity.BioneDriverInfo;
@Service
@Transactional(readOnly = true)
public class DriverInfoBS extends BaseBS<BioneDriverInfo> {

}
