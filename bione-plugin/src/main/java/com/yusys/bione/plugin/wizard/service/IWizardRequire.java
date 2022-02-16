package com.yusys.bione.plugin.wizard.service;

import java.io.File;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.yusys.bione.frame.validator.relatedobj.ValidErrorInfoObj;
import com.yusys.bione.plugin.wizard.web.vo.UploadResult;

public interface IWizardRequire {
	public UploadResult upload(File file);
	public void saveData(String ehcacheId,String dsId);
	public String export(String ids);
	public String exportAll(String ids);
	public List<ValidErrorInfoObj> validateInfo(String dsId,String ehcacheId);
	public List<ValidErrorInfoObj> validateVerInfo(String dsId,String ehcacheId);
	public void saveData(HttpServletRequest request,String ehcacheId,String dsId);
}
