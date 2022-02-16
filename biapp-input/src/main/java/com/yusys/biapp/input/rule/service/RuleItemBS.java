package com.yusys.biapp.input.rule.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yusys.biapp.input.rule.entity.RptInputListRuleItemInfo;
import com.yusys.bione.comp.service.BaseBS;

@Service
@Transactional
public class RuleItemBS extends BaseBS<RptInputListRuleItemInfo> {

}
