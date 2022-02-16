package com.yusys.biapp.input.inputTable.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yusys.biapp.input.inputTable.entity.RptInputListTableConstraint;
import com.yusys.bione.comp.service.BaseBS;

@Service
@Transactional(readOnly = true)
public class TableConstrainBS extends BaseBS<RptInputListTableConstraint> {
}
