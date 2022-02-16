package com.yusys.bione.plugin.datashow.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.yusys.bione.frame.base.web.BaseController;
import com.yusys.bione.plugin.rptidx.entity.RptIdxCatalog;
import com.yusys.bione.plugin.rptidx.service.IdxCatalogBS;

@Controller
@RequestMapping("/report/frame/datashow/idxcata")
public class IdxCataController extends BaseController {
	@Autowired
	private IdxCatalogBS cataBS;

	@RequestMapping(method = RequestMethod.GET, value = "/{id}")
	@ResponseBody
	public RptIdxCatalog info(@PathVariable("id") String id) {
		return cataBS.getEntityById(id);
	}
}
