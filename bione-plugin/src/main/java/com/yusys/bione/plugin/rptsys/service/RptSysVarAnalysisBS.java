package com.yusys.bione.plugin.rptsys.service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.yusys.bione.comp.service.BaseBS;
import com.yusys.bione.frame.mtool.entity.BioneDriverInfo;
import com.yusys.bione.frame.mtool.entity.BioneDsInfo;
import com.yusys.bione.plugin.base.common.GlobalConstants4plugin;
import com.yusys.bione.plugin.rptdim.entity.RptDimItemInfo;
import com.yusys.bione.plugin.rptdim.repository.mybatis.RptDimDao;
import com.yusys.bione.plugin.rptorg.repository.RptOrgInfoMybatisDao;
import com.yusys.bione.plugin.rptorg.web.vo.RptOrgInfoVo;
import com.yusys.bione.plugin.rptsys.entity.RptSysVarInfo;

@Service
@Transactional(readOnly = true)
public class RptSysVarAnalysisBS extends BaseBS<Object> {
	@Autowired
	private RptSysVarBS sysVarBS;
	@Autowired
	private RptDimDao rptDimDao;

	@Autowired
	private RptOrgInfoMybatisDao rptFrsOrgDao;
	// 自定义变量占位符预设为'$'
	private String tag = "$";
	// 内置变量占位符预设为'$'
	private String itag = "#";
	
	private String stag = "*";

	/**
	 * 获取当前占位符 
	 * 
	 * @return
	 */
	public String getTag() {
		return tag;
	}

	/**
	 * 修改占位符
	 * 
	 * @param tag
	 */
	public void setTag(String tag) {
		this.tag = tag;
	}

	/**
	 * 解析字符串
	 * 
	 * @param str
	 * @return
	 */
	public Map<String, Object> getResult(String str,
			Map<String, Map<String,Object>> params, String busiType,String unit) {
		Map<String, Object> result = new HashMap<String, Object>();
		str = dealExtInfo(str, params, busiType);
		str = dealInnerInfo(str, params, busiType,true,unit);
		str = dealInnerInfo(str, params, busiType,false,unit);
		result.put("str", str);
		return result;
	}

	@SuppressWarnings("unchecked")
	public String dealExtInfo(String str,Map<String, Map<String,Object>> params,
			String busiType) {
		if(StringUtils.isNotBlank(str)){
			String s = new String(str);
			String flag = StringUtils.substringBetween(s, tag);
			List<String> strList = Lists.newArrayList();
			while (flag != null) {
				strList.add(flag);
				s = StringUtils.replace(s, tag + flag + tag, "");
				flag = StringUtils.substringBetween(s, tag);
			}
			Map<String, Object> allVarMap = getValueMap(strList,params,busiType);
			Map<String, String> strVarMap = (Map<String, String>) allVarMap
					.get("strVarMap");// 字符串变量(直接替换)
			for (String val : strVarMap.keySet()) {
				str = StringUtils.replace(str, tag + val + tag, strVarMap.get(val));
			}
		}
		return str;
	}

	@SuppressWarnings("unchecked")
	public String dealInnerInfo(String str, Map<String, Map<String,Object>> params,
			String busiType,boolean isConvert,String unit) {
		String tag=itag;
		if(!isConvert){
			tag=stag;
		}
		String s = new String(str);
		String flag = StringUtils.substringBetween(s, tag);
		List<String> strList = Lists.newArrayList();
		while (flag != null) {
			strList.add(flag);
			s = StringUtils.replace(s, tag + flag + tag, "");
			flag = StringUtils.substringBetween(s, tag);
		}
		Map<String, Object> allVarMap = getInnerValueMap(strList, params,
				busiType,isConvert,unit);
		Map<String, String> strVarMap = (Map<String, String>) allVarMap
				.get("strVarMap");// 字符串变量(直接替换)
		for (String val : strVarMap.keySet()) {
			str = StringUtils.replace(str.toString(), tag + val + tag,
					strVarMap.get(val));
		}
		return str;
	}
	
	

	@SuppressWarnings("unchecked")
	public Map<String, Object> getInnerValueMap(List<String> strList,
			Map<String,  Map<String,Object>> params, String busiType,boolean convertFlag,String unit) {
		Map<String, String> strMap = Maps.newHashMap();
		for (String str : strList) {
			Map<String,Object> info= params.get(str.toUpperCase());
			if(info!=null){
				if(info.get("isDim").equals(true)){
					String dimTypeNo=info.get("dimTypeNo").toString();
					if (dimTypeNo.toUpperCase().equals(GlobalConstants4plugin.RPT_INNER_SYS_VAR_ORG.toUpperCase())) {
						
						List<Map<String,Object>> datas=(List<Map<String, Object>>) info.get("data");
						String strInfo="";
						strMap.put(str, "");
						if(datas!=null&&datas.size()>0){
							for(Map<String,Object> data:datas){
								String op=data.get("op").toString();
								Object value=data.get("value");
								Map<String,String> orgMap=new HashMap<String, String>();
								if("=".equals(op)){
									if(value instanceof String){
										if(convertFlag){
											orgMap = getOrgInnerVarInfo(busiType,value.toString());
										}
										if(convertFlag)
											strInfo+=(orgMap.get(value.toString())!=null?orgMap.get(value.toString()):"")+",";
										else
											strInfo+=value.toString()!=null?value.toString():""+",";
									}
									else{
										List<String> values=(List<String>) value;
										String vals="";
										if(convertFlag){
											orgMap = getOrgInnerVarInfo(busiType,values);
										}
										for(int i=0;i<values.size();i++){
											if(convertFlag){
												vals+=(orgMap.get(values.get(i))!=null?orgMap.get(values.get(i)):"");
											}
											else
												vals+=values.get(i).toString()!=null?values.get(i).toString():"";
											if(i<values.size()-1)
												vals+=",";
										}
										strInfo+=vals+",";
									}
								}
							}
							strMap.put(str, StringUtils.substring(strInfo,0,strInfo.length()-1));
						}
						
					}  else if (dimTypeNo.toUpperCase().equals(GlobalConstants4plugin.RPT_INNER_SYS_VAR_DATE.toUpperCase())) {
						List<Map<String,Object>> datas=(List<Map<String, Object>>) info.get("data");
						String strInfo="";
						strMap.put(str, "");
						if(datas!=null&&datas.size()>0){
							for(Map<String,Object> data:datas){
								String op=data.get("op").toString();
								Object value=data.get("value");
								if("=".equals(op)){
									if(value instanceof String){
										SimpleDateFormat sd=new SimpleDateFormat("yyyyMMdd");
										SimpleDateFormat sdf=new SimpleDateFormat("yyyy年MM月dd日");
										try {
											strInfo+=sdf.format(sd.parse(value.toString()))+",";
										} catch (ParseException e) {
											// TODO Auto-generated catch block
											strInfo+=",";
										}
									}
								}
								if(">=".equals(op)){
									if(value instanceof String){
										SimpleDateFormat sd=new SimpleDateFormat("yyyyMMdd");
										SimpleDateFormat sdf=new SimpleDateFormat("yyyy年MM月dd日");
										try {
											strInfo+=sdf.format(sd.parse(value.toString()))+"~";
										} catch (ParseException e) {
											// TODO Auto-generated catch block
											strInfo+="~";
										}
									}
									else{
										List<String> vals=(List<String>) value;
										SimpleDateFormat sd=new SimpleDateFormat("yyyyMMdd");
										SimpleDateFormat sdf=new SimpleDateFormat("yyyy年MM月dd日");
										try {
											strInfo+=sdf.format(sd.parse(vals.get(0)))+"~";
										} catch (ParseException e) {
											// TODO Auto-generated catch block
											strInfo+="~";
										}
									}
								}
								if("<=".equals(op)){
									if(value instanceof String){
										SimpleDateFormat sd=new SimpleDateFormat("yyyyMMdd");
										SimpleDateFormat sdf=new SimpleDateFormat("yyyy年MM月dd日");
										try {
											strInfo+=sdf.format(sd.parse(value.toString()))+",";
										} catch (ParseException e) {
											// TODO Auto-generated catch block
											strInfo+=",";
										}
									}
									else{
										List<String> vals=(List<String>) value;
										SimpleDateFormat sd=new SimpleDateFormat("yyyyMMdd");
										SimpleDateFormat sdf=new SimpleDateFormat("yyyy年MM月dd日");
										try {
											strInfo+=sdf.format(sd.parse(vals.get(0).toString()))+",";
										} catch (ParseException e) {
											// TODO Auto-generated catch block
											strInfo+=",";
										}
									}
								}
							}
							strMap.put(str, StringUtils.substring(strInfo,0,strInfo.length()-1));
						}
					} 
					else{
						List<Map<String,Object>> datas=(List<Map<String, Object>>) info.get("data");
						String strInfo="";
						for(Map<String,Object> data:datas){
							String op=data.get("op").toString();
							Object value=data.get("value");
							if("=".equals(op)){
								if(value instanceof String){
									if(convertFlag)
										strInfo+=this.getDimVarInfo(dimTypeNo, value.toString());
									else
										strInfo+=value.toString()!=null?value.toString():"";
								}
								else{
									List<String> values=(List<String>) value;
									String vals="";
									for(int i=0;i<values.size();i++){
										if(convertFlag)
											vals+=this.getDimVarInfo(dimTypeNo,values.get(i));
										else
											vals+=values.get(i).toString()!=null?values.get(i).toString():"";
										if(i<values.size()-1)
											vals+=",";
									}
									strInfo+=vals;
								}
								strInfo+=",";
							}
						}
						strMap.put(str, StringUtils.substring(strInfo,0,strInfo.length()-1));
					}
				}
				else{
					List<Map<String,Object>> datas=(List<Map<String, Object>>) info.get("data");
					String strInfo="";
					for(Map<String,Object> data:datas){
						String op=data.get("op").toString();
						Object value=data.get("value");
						if(value instanceof String){
							strInfo+=op+value.toString()+",";
						}
					}
					strMap.put(str, StringUtils.substring(strInfo,0,strInfo.length()-1));
				} 
			}
			else{
				if(str.equals("tempUnit")){
					if(unit.equals("01")){
						strMap.put(str, "");
					}
					if(unit.equals("02")){
						strMap.put(str, "百");
					}
					if(unit.equals("03")){
						strMap.put(str, "千");
					}
					if(unit.equals("04")){
						strMap.put(str, "万");
					}
					if(unit.equals("05")){
						strMap.put(str, "亿");
					}
				}
				else{
					strMap.put(str, "");
				}
				
			}
			
		}
		Map<String, Object> result = Maps.newConcurrentMap();
		result.put("strVarMap", strMap);// 常量或SQL型变量(字符串)
		return result;
	}

	/**
	 * 通过变量集合获取其变量-值的Map
	 * 
	 * @param strList
	 * @return
	 */
	public Map<String, Object> getValueMap(List<String> strList,Map<String, Map<String,Object>> params,
			String busiType) {
		Map<String, String> strMap = Maps.newHashMap();
		Map<String, List<RptSysVarInfo>> dsMap = Maps.newHashMap();
		for (String str : strList) {
			List<RptSysVarInfo> infoList = this.sysVarBS
					.getSysVarInfosByNo(str,GlobalConstants4plugin.SYS_DEF_TYPE_CUSTOM);
			if (infoList != null && infoList.size() > 0) {
				RptSysVarInfo info = infoList.get(0);
				String varType = info.getVarType();
				if (GlobalConstants4plugin.BIONE_SYS_VAR_TYPE_CONSTANT.equals(varType)) {
					strMap.put(str, info.getVarVal());
				} else if (GlobalConstants4plugin.BIONE_SYS_VAR_TYPE_SQL
						.equals(varType)) {
					String dsId = info.getSourceId();
					if (dsId != null) {
						if (dsMap.containsKey(dsId)) {
							List<RptSysVarInfo> objList = dsMap.get(dsId);
							objList.add(info);
						} else {
							List<RptSysVarInfo> objList = Lists.newArrayList();
							objList.add(info);
							dsMap.put(dsId, objList);
						}
					}
				}
			}

		}
		for (String key : dsMap.keySet()) {
			BioneDsInfo ds = this.sysVarBS.findDataSourceById(key);
			if (ds != null) {
				BioneDriverInfo driver = this.sysVarBS.findDriverInfoById(ds
						.getDriverId());
				if (driver != null) {
					String url = ds.getConnUrl();
					String usrId = ds.getConnUser();
					String passwd = ds.getConnPwd();
					String driverName = driver.getDriverName();
					if (driverName != null && !"".equals(driverName)
							&& url != null && !"".equals(url) && usrId != null
							&& !"".equals(usrId)) {
						Connection conn = null;
						ResultSet rs = null;
						PreparedStatement state = null;
						List<RptSysVarInfo> objList = dsMap.get(key);
						try {
							Class.forName(driverName);
							conn = DriverManager.getConnection(url, usrId,
									passwd);
							state = conn.prepareStatement("");
							for (RptSysVarInfo obj : objList) {
								String sql = dealInnerInfo(obj.getVarVal(), params, busiType,true,"");
								rs = this.doSQL(state, sql, rs);
								if (rs != null
										&& rs.getMetaData().getColumnCount() == 1
										&& rs.next()) {
									int rowCount = rs.getRow();
									if (rowCount == 1) {
										String result = rs.getString(1);
										// if (!rs.next()) {
										strMap.put(obj.getVarNo(), result);
										// }
									}
								} else {
									strMap.put(obj.getVarNo(), "?");
								}
							}
						} catch (ClassNotFoundException e) {
						} catch (SQLException e) {
						} finally {
							if (rs != null) {
								try {
									rs.close();
								}
								catch (SQLException ex) {
								}
								catch (Throwable ex) {
									// We don't trust the JDBC driver: It might throw RuntimeException or Error.
								}
							}
							if (state != null) {
								try {
									rs.close();
								}
								catch (SQLException ex) {
								}
								catch (Throwable ex) {
									// We don't trust the JDBC driver: It might throw RuntimeException or Error.
								}
							}
							if (conn != null) {
								try {
									conn.close();
								}
								catch (SQLException ex) {
								}
								catch (Throwable ex) {
									// We don't trust the JDBC driver: It might throw RuntimeException or Error.
								}
							}
						}
					}
				}
			}
		}
		Map<String, Object> result = Maps.newConcurrentMap();
		result.put("strVarMap", strMap);// 常量或SQL型变量(字符串)
		return result;
	}

	private ResultSet doSQL(PreparedStatement st, String sql, ResultSet rs) {
		try {
			rs = st.executeQuery(sql);
		} catch (SQLException e) {
			return null;
		}
		return rs;
	}

	public String getDimVarInfo(String dimTypeNo, String dimItemNo) {
		Map<String, String> map = new HashMap<String, String>();
		map.put("dimTypeNo", dimTypeNo);
		map.put("dimItemNo", dimItemNo);
		RptDimItemInfo dimInfo = rptDimDao.findDimItemInfoByPkId(map);
		if (dimInfo != null) {
			return dimInfo.getDimItemNm();
		}
		return "";
	}

	public Map<String,String> getOrgInnerVarInfo(String busiType) {
		Map<String, String> orgMap = new HashMap<String, String>();
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("orgType", busiType);
		List<RptOrgInfoVo> orgVo = rptFrsOrgDao.getOrgList(map);
		if (orgVo != null&&orgVo.size()>0) {
			for(RptOrgInfoVo vo:orgVo){
				orgMap.put(vo.getId().getOrgNo(), vo.getOrgNm());
			}
		}
		return orgMap;
	}
	
	public Map<String,String> getOrgInnerVarInfo(String busiType,List<String> ids) {
		Map<String, String> orgMap = new HashMap<String, String>();
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("orgType", busiType);
		map.put("orgNos", ids);
		List<RptOrgInfoVo> orgVo = rptFrsOrgDao.getOrgList(map);
		if (orgVo != null&&orgVo.size()>0) {
			for(RptOrgInfoVo vo:orgVo){
				orgMap.put(vo.getId().getOrgNo(), vo.getOrgNm());
			}
		}
		return orgMap;
	}
	
	public Map<String,String> getOrgInnerVarInfo(String busiType,String id) {
		Map<String, String> orgMap = new HashMap<String, String>();
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("orgType", busiType);
		map.put("orgNo", id);
		List<RptOrgInfoVo> orgVo = rptFrsOrgDao.getOrgList(map);
		if (orgVo != null&&orgVo.size()>0) {
			for(RptOrgInfoVo vo:orgVo){
				orgMap.put(vo.getId().getOrgNo(), vo.getOrgNm());
			}
		}
		return orgMap;
	}
	
	public String getDimInfoByDsId(String enNm, String dsId) {
		String jql="select dimTypeNo from RptSysModuleCol col where setId=?0 and enNm=?1";
		List<String> dimTypeNo=this.baseDAO.findWithIndexParam(jql, dsId,enNm);
		if(dimTypeNo!=null&&dimTypeNo.size()>0){
			return dimTypeNo.get(0);
		}
		return "";
	}
	
	public List<String> getEnNmByDsId(String dimTypeNo, String dsId) {
		String jql="select enNm from RptSysModuleCol col where setId=?0 and dimTypeNo=?1";
		List<String> enNms=this.baseDAO.findWithIndexParam(jql, dsId,dimTypeNo);
		return enNms;
	}
}
