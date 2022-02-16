package com.yusys.biapp.input.suptask;

import java.util.List;
import java.util.Map;

import com.yusys.biapp.input.task.entity.DataSupSearchInfo;
import com.yusys.bione.comp.repository.mybatis.MyBatisRepository;
import com.yusys.bione.frame.mtool.entity.BioneDsInfo;

/**
 * @ClassName: DataSupDAO
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author Yaoxt
 * @date 2017年11月6日
 *
 */
@MyBatisRepository
public interface DataSupDAO {
	
	public List<DataSupSearchInfo> getTableInfoNodes(Map<String, Object> params);
	
	public List<BioneDsInfo> getDataSources(String id);

}
