package com.yusys.bione.frame.mtool.repository.mybatis;

import java.util.List;
import java.util.Map;

import com.yusys.bione.comp.repository.mybatis.MyBatisRepository;
import com.yusys.bione.frame.logicsys.entity.BioneLogicSysInfo;
import com.yusys.bione.frame.mtool.entity.BioneDriverInfo;
import com.yusys.bione.frame.mtool.entity.BioneDsInfo;

@MyBatisRepository
public interface DataSourceDao {
	public  List<BioneDsInfo> search();
	public  List<BioneDsInfo> checkedDsName(Map<String,String> params);
	public  List<BioneLogicSysInfo> getBioneLogicSysInfoList();
	public  List<BioneDriverInfo> getDriverList();
	public  BioneDriverInfo  getURLDataByDriverId(String driverId);
	public  void  dsBatchDel(List<String>  list);
	public  BioneDriverInfo   findDriverInfoById(String   id);
	public BioneDsInfo findDataSourceById(String  id);
	public  void  updateDS(BioneDsInfo model);
	public  void  saveDS(BioneDsInfo model);
	public Integer getDataSetCountByDsId(Map<String,Object> param);
}
