package com.yusys.bione.frame.message.service;

import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yusys.bione.comp.service.BaseBS;
import com.yusys.bione.frame.message.entity.BioneMsgAuthObjRel;

@Service
@Transactional(readOnly = true)
public class MsgAuthObjRelBS extends BaseBS<BioneMsgAuthObjRel> {
	

	@Transactional(readOnly = false)
	public void batchSave(List<BioneMsgAuthObjRel> list) {
		for (BioneMsgAuthObjRel rel : list) {
			this.saveOrUpdateEntity(rel);
		}
	}
	
	public List<List<String>> change(List<String> rptList){
		List<List<String>> rptIdsParam = new ArrayList<List<String>>();
		int count = rptList.size()/1000 + (rptList.size() % 1000 == 0 ? 0 : 1);//rptList以1000为单位分割
		for(int i=0;i<count;i++){
			rptIdsParam.add(rptList.subList(i * 1000, ((i+1) * 1000 > rptList.size() ? rptList.size() : (i + 1) * 1000)));
		}
		return rptIdsParam;
	}
}
