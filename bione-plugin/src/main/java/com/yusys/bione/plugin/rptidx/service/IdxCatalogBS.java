package com.yusys.bione.plugin.rptidx.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yusys.bione.comp.service.BaseBS;
import com.yusys.bione.plugin.rptidx.entity.RptIdxCatalog;

@Service
@Transactional(readOnly = true)
public class IdxCatalogBS extends BaseBS<RptIdxCatalog> {
}
