package com.yusys.bione.plugin.wizard.service;

import com.yusys.bione.comp.utils.*;
import com.yusys.bione.frame.auth.entity.BioneAuthObjUserRel;
import com.yusys.bione.frame.auth.entity.BioneAuthObjUserRelPK;
import com.yusys.bione.frame.authobj.entity.BioneDeptInfo;
import com.yusys.bione.frame.authobj.entity.BioneOrgInfo;
import com.yusys.bione.frame.base.common.GlobalConstants4frame;
import com.yusys.bione.frame.base.web.BaseController;
import com.yusys.bione.frame.excel.*;
import com.yusys.bione.frame.security.BioneSecurityUtils;
import com.yusys.bione.frame.user.entity.BioneUserInfo;
import com.yusys.bione.frame.user.service.UserBS;
import com.yusys.bione.frame.validator.common.Validator;
import com.yusys.bione.frame.validator.exception.ValidateException;
import com.yusys.bione.frame.validator.relatedobj.ValidErrorInfoObj;
import com.yusys.bione.plugin.base.common.GlobalConstants4plugin;
import com.yusys.bione.plugin.wizard.web.vo.UploadResult;
import com.yusys.bione.plugin.wizard.web.vo.UserImportVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.sql.Timestamp;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <pre>
 * Title:用户信息的导入导出
 * Description: 提供用户信息上传/用户信息及用户角色关系信息的导出/用户信息保存/上传用户信息的检验等功能
 * </pre>
 * 
 * @author hubing hubing1@yusys.com.cn
 * @version 1.00.00
 * 
 *          <pre>
 * 修改记录
 *    修改后版本:     修改人：  修改日期:     修改内容:
 * </pre>
 */
@Service
public class UserWizardRequire extends BaseController implements IWizardRequire {

	@Autowired
	private UserBS userBS;

	@Autowired
	private ExcelBS excelBS;
	
	/**
	 * 用户信息上传
	 * @param file 文件对象
	 * @return 上传文件结果
	 */
	@SuppressWarnings("unchecked")
	@Override
	public UploadResult upload(File file) {
		AbstractExcelImport xlsImport = new ExcelImporter(UserImportVO.class, file);
		UploadResult result = new UploadResult();
		try {
			List<UserImportVO> vos = (List<UserImportVO>) xlsImport.ReadExcel();
			String ehcacheId = RandomUtils.uuid2();
			EhcacheUtils.put(BioneSecurityUtils.getCurrentUserId(), ehcacheId, vos);
			result.setEhcacheId(ehcacheId);
			result.setFileName(file.getName());
			if (vos != null && vos.size() > 0) {
				for (UserImportVO vo : vos) {
					result.setInfo(vo.getUserNo(), vo.getUserName());
				}
			}
		} catch (InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
		}
		return result;
	}
	/**
	 * 用户信息及用户角色关系信息的导出
	 * @param ids 选中的用户编号
	 * @return 文件名称
	 */
	@Override
	public String export(String ids) {
		String fileName = "";
		//查询要导出的用户信息
		List<BioneUserInfo> infos = new ArrayList<>();
		if (StringUtils.isNotBlank(ids)) {
			//选择用户导出
			List<String> usrIdList = ArrayUtils.asList(ids, ",");
			infos = this.userBS.getUserInofByIds(usrIdList);
		} else {
			//全量导出
			String logicSysNo = BioneSecurityUtils.getCurrentUserInfo().getCurrentLogicSysNo();
			infos = this.userBS.getEntityListByProperty(BioneUserInfo.class,"logicSysNo", logicSysNo);
//			infos = this.userBS.getAllEntityList(BioneUserInfo.class);
		}
		Map<String,String> deptMap = getCheckObjMap(BioneDeptInfo.class,"dept","deptNo","orgNo","deptName");
		List<UserImportVO> vos = new ArrayList<UserImportVO>();//初始化一个list，用于存放导出工作簿中第一个sheet页的内容

		if(infos != null && infos.size() >0){//循环遍历查询的用户信息对象，放入到excel模板对象UserImportVO中
			for(BioneUserInfo info :infos){
				UserImportVO vo = new UserImportVO();
				vo.setUserId(info.getUserId());
				vo.setUserNo(info.getUserNo());
				vo.setUserName(info.getUserName());
				vo.setUserAgname(info.getUserAgname());
				vo.setOrgNo(info.getOrgNo());
				vo.setDeptName(deptMap.get(info.getOrgNo()+info.getDeptNo()) == null ? "" : deptMap.get(info.getOrgNo()+info.getDeptNo()));
				vo.setUserSts(info.getUserSts());
				vo.setEmail(info.getEmail());
				vo.setTel(info.getTel());
				vo.setAddr(info.getAddress());
				vo.setBirthday(info.getBirthday());
				vo.setMobile(info.getMobile());
				vo.setPostCode(info.getPostcode());
				vo.setRemark(info.getRemark());
				vo.setSex(info.getSex());
				vo.setIsManager(info.getIsManager());
				vo.setIdCard(info.getIdCard());
				vos.add(vo);
			}
		}
		List<List<?>> list = new ArrayList<List<?>>();
		list.add(vos);//放入第一个sheet页
		XlsExcelTemplateExporter fe = null;//初始化一个导出模板数据的对象
		try {
			fileName = this.getRealPath() + GlobalConstants4plugin.DESIGN_EXPORT_PATH
					+ File.separator + RandomUtils.uuid2() + ".xls";//去工程路径下取模板的文件名
			//定义一个导出模板数据的对象，传入文件名，模板路径，要导出的实体对象list
			fe = new XlsExcelTemplateExporter(fileName, this.getRealPath()
					+ GlobalConstants4plugin.DESIGN_EXPORT_PATH + File.separator
					+ GlobalConstants4plugin.EXPORT_USER_TEMPLATE_PATH, list);
			fe.run();//执行导出
		} catch (Exception e) {
			fileName = "";
			e.printStackTrace();
		} finally {
			try {
				if (fe != null) {
					fe.destory();
				}
			} catch (BioneExporterException e) {
				e.printStackTrace();
			}
		}
		return fileName;
	}
	/*原先的导出
	@Override
	public String export(String ids) {
		String fileName = "";
		//查询要导出的用户信息
		List<String> usrIdList = ArrayUtils.asList(ids, ",");
		List<BioneUserInfo> infos = this.userBS.getUserInofByIds(usrIdList);
		List<BioneAuthObjUserRel> rels = this.userBS.getUserRolesByIds(usrIdList);
		Map<String,String> deptMap = getCheckObjMap(BioneDeptInfo.class,"dept","deptNo","orgNo","deptName");
		Map<String,String> relMap = Maps.newHashMap();
		List<UserImportVO> vos = new ArrayList<UserImportVO>();//初始化一个list，用于存放导出工作簿中第一个sheet页的内容
		if(null != rels && rels.size() > 0){
			for(BioneAuthObjUserRel rel : rels){
				if(null != relMap.get(rel.getId().getUserId())){
					String val = relMap.get(rel.getId().getUserId());
					relMap.put(rel.getId().getUserId(), val+";"+rel.getId().getObjId());
				}else{
					relMap.put(rel.getId().getUserId(), rel.getId().getObjId());
				}
			}
		}
		
		if(infos != null && infos.size() >0){//循环遍历查询的用户信息对象，放入到excel模板对象UserImportVO中
			for(BioneUserInfo info :infos){
				UserImportVO vo = new UserImportVO();
				vo.setUserNo(info.getUserNo());
				vo.setUserName(info.getUserName());
				vo.setUserAlias(info.getUserAgname());
				vo.setRoleId(relMap.get(info.getUserId()) == null ? "" : relMap.get(info.getUserId()));
				vo.setOrgNo(info.getOrgNo());
				vo.setDeptName(deptMap.get(info.getOrgNo()+info.getDeptNo()) == null ? "" : deptMap.get(info.getOrgNo()+info.getDeptNo()));
				vo.setUserSts(info.getUserSts());
				vo.setEmail(info.getEmail());
				vo.setTel(info.getTel());
				vos.add(vo);
			}
		}
		List<List<?>> list = new ArrayList<List<?>>();
		list.add(vos);//放入第一个sheet页
		XlsExcelTemplateExporter fe = null;//初始化一个导出模板数据的对象
		try {
			fileName = this.getRealPath() + GlobalConstants4plugin.DESIGN_EXPORT_PATH
					+ File.separator + RandomUtils.uuid2() + ".xls";//去工程路径下取模板的文件名
			//定义一个导出模板数据的对象，传入文件名，模板路径，要导出的实体对象list
			fe = new XlsExcelTemplateExporter(fileName, this.getRealPath()
					+ GlobalConstants4plugin.DESIGN_EXPORT_PATH + File.separator
					+ GlobalConstants4plugin.EXPORT_USER_TEMPLATE_PATH, list);
			fe.run();//执行导出
		} catch (Exception e) {
			fileName = "";
			e.printStackTrace();
		} finally {
			try {
				if (fe != null) {
					fe.destory();
				}
			} catch (BioneExporterException e) {
				e.printStackTrace();
			}
		}
		return fileName;
	}*/
	/**
	 * 用户信息保存
	 * @param ehcacheId 缓存标志符
	 * @param dsId 数据源ID
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void saveData(String ehcacheId, String dsId) {
		List<List<?>> lists = (List<List<?>>) EhcacheUtils.get(
				BioneSecurityUtils.getCurrentUserId(), ehcacheId);
		List<BioneUserInfo> users = (List<BioneUserInfo>) lists.get(0);//用户信息
		List<BioneAuthObjUserRel> usrRels = (List<BioneAuthObjUserRel>) lists.get(1);//用户角色关系信息
		List<String> usrFields = new ArrayList<String>();
		List<String> usrRelFields = new ArrayList<String>();
		usrFields.add("userId");
		usrRelFields.add("id.logicSysNo");
		usrRelFields.add("id.userId");
		usrRelFields.add("id.objDefNo");
		this.excelBS.deleteEntityJdbcBatch(users, usrFields); 
		this.excelBS.saveEntityJdbcBatch(users);
		this.excelBS.deleteEntityJdbcBatch(usrRels, usrRelFields);
		this.excelBS.saveEntityJdbcBatch(usrRels);
	}
	/**
	 * 上传用户信息的检验
	 * @param ehcacheId 缓存标志符
	 * @param dsId 数据源ID
	 * @return 检验信息对象
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<ValidErrorInfoObj> validateInfo(String dsId, String ehcacheId) {
		List<ValidErrorInfoObj> errors = new ArrayList<ValidErrorInfoObj>();
		List<BioneUserInfo> users = new ArrayList<BioneUserInfo>();
		List<BioneAuthObjUserRel> usrRels = new ArrayList<BioneAuthObjUserRel>();//用户机构关系信息
		List<UserImportVO> vos = (List<UserImportVO>) EhcacheUtils.get(BioneSecurityUtils.getCurrentUserId(), ehcacheId);

		if (vos != null && vos.size() > 0) {
			Map<String,String> userNos = getCheckObjMap(BioneUserInfo.class,"user","userNo","userId","");
			Map<String,String> orgNos = getCheckObjMap(BioneOrgInfo.class,"org","orgNo","","");
			Map<String,String> deptIds = getCheckObjMap(BioneDeptInfo.class,"dept","deptName","orgNo","deptId");
			Map<String,String> deptNos = getCheckObjMap(BioneDeptInfo.class,"dept","deptName","orgNo","deptNo");
			Map<String,String> pwdMap = getCheckObjMap(BioneUserInfo.class,"user","userNo","userPwd","");

			for (UserImportVO vo : vos) {
				try {//其余校验
					Validator.validate(vo);
				} catch (ValidateException e) {
					errors.addAll(e.getErrorInfoObjs());
				}

				String uuidUserId = "";//导入保存前生成用户表的user_id，用于两张表的保存
				if("inExcel".equals(userNos.get(vo.getUserNo()))){
					//在循环遍历excell记录时，将记录的工号逐条与map变量的key比较，
					//判断此条记录的工号在excell内部是否存在重复，只校验excel内部，如果与数据库重复，就保留user_id更新库用户信息
					ValidErrorInfoObj obj=new ValidErrorInfoObj();
					obj.setSheetName(vo.getSheetName());
					obj.setExcelRowNo(vo.getExcelRowNo());
					obj.setExcelColNo(2);
					obj.setValidTypeNm("唯一性校验");
					obj.setErrorMsg("导入用户账号存在重复");
					errors.add(obj);
				}else{//不存在重复就放入到map变量中，确保能全部校验出所有的重复记录
					if(StringUtils.isNotBlank(userNos.get(vo.getUserNo()))){
						uuidUserId = userNos.get(vo.getUserNo());
					}else{
						if(StringUtils.isNotBlank(vo.getUserId())){
							uuidUserId = vo.getUserId();
						}else{
							uuidUserId = RandomUtils.uuid2();
						}
					}
					userNos.put(vo.getUserNo(), "inExcel");
				}
				//校验机构
				if(null == orgNos.get(vo.getOrgNo())){
					ValidErrorInfoObj obj=new ValidErrorInfoObj();
					obj.setSheetName(vo.getSheetName());
					obj.setExcelRowNo(vo.getExcelRowNo());
					obj.setExcelColNo(9);
					obj.setValidTypeNm("存在性校验");
					obj.setErrorMsg("该机构不存在");
					errors.add(obj);
				}
				//校验部门
				if(StringUtils.isNotBlank(vo.getDeptName()) && null == deptIds.get(vo.getOrgNo()+vo.getDeptName())){
					ValidErrorInfoObj obj=new ValidErrorInfoObj();
					obj.setSheetName(vo.getSheetName());
					obj.setExcelRowNo(vo.getExcelRowNo());
					obj.setExcelColNo(11);
					obj.setValidTypeNm("存在性校验");
					obj.setErrorMsg("该部门不存在");
					errors.add(obj);
				}
				//校验性别
				String[] sex = {"0", "1"};
				if (StringUtils.isNotBlank(vo.getSex()) && Arrays.binarySearch(sex, vo.getSex()) < 0) {
					ValidErrorInfoObj obj=new ValidErrorInfoObj();
					obj.setSheetName(vo.getSheetName());
					obj.setExcelRowNo(vo.getExcelRowNo());
					obj.setExcelColNo(5);
					obj.setValidTypeNm("合法性校验");
					obj.setErrorMsg("性别值错误");
					errors.add(obj);
				}
				//用户状态校验
				String[] userSts = {"0","1"};
				if (StringUtils.isNotBlank(vo.getUserSts()) && Arrays.binarySearch(userSts, vo.getUserSts()) < 0) {
					ValidErrorInfoObj obj=new ValidErrorInfoObj();
					obj.setSheetName(vo.getSheetName());
					obj.setExcelRowNo(vo.getExcelRowNo());
					obj.setExcelColNo(13);
					obj.setValidTypeNm("合法性校验");
					obj.setErrorMsg("用户状态值错误");
					errors.add(obj);
				}
				//校验生日
				if (StringUtils.isNotBlank(vo.getBirthday())) {
					Pattern pattern = Pattern.compile("(([0-9]{3}[1-9]|[0-9]{2}[1-9][0-9]{1}|[0-9]{1}[1-9][0-9]{2}|[1-9][0-9]{3})-"+
							"(((0[13578]|1[02])-(0[1-9]|[12][0-9]|3[01]))|((0[469]|11)-(0[1-9]|[12][0-9]|30))|(02-(0[1-9]|[1][0-9]|2[0-8]))))|((([0-9]{2})(0[48]|[2468][048]|[13579][26])|((0[48]|[2468][048]|[3579][26])00))-02-29)");
					Matcher matcher = pattern.matcher(vo.getBirthday());
					if (!matcher.find()) {
						ValidErrorInfoObj obj = new ValidErrorInfoObj();
						obj.setSheetName(vo.getSheetName());
						obj.setExcelRowNo(vo.getExcelRowNo());
						obj.setExcelColNo(6);
						obj.setValidTypeNm("合法性校验");
						obj.setErrorMsg("生日格式错误");
						errors.add(obj);
					}
				}
				//校验邮箱
				if (StringUtils.isNotBlank(vo.getEmail())) {
					Pattern pattern = Pattern.compile("^\\s*\\w+(?:\\.{0,1}[\\w-]+)*@[a-zA-Z0-9]+(?:[-.][a-zA-Z0-9]+)*\\.[a-zA-Z]+\\s*$");
					Matcher matcher = pattern.matcher(vo.getEmail());
					if (!matcher.find()) {
						ValidErrorInfoObj obj = new ValidErrorInfoObj();
						obj.setSheetName(vo.getSheetName());
						obj.setExcelRowNo(vo.getExcelRowNo());
						obj.setExcelColNo(7);
						obj.setValidTypeNm("合法性校验");
						obj.setErrorMsg("邮箱格式错误");
						errors.add(obj);
					}
				}
				//校验手机号
				if (StringUtils.isNotBlank(vo.getMobile())) {
					Pattern pattern = Pattern.compile("^(((13[0-9]{1})|(15[0-9]{1})|(18[0-9]{1}))+\\d{8})$");//此为前端判断正则
					Matcher matcher = pattern.matcher(vo.getMobile());
					if (!matcher.find()) {
						ValidErrorInfoObj obj = new ValidErrorInfoObj();
						obj.setSheetName(vo.getSheetName());
						obj.setExcelRowNo(vo.getExcelRowNo());
						obj.setExcelColNo(8);
						obj.setValidTypeNm("合法性校验");
						obj.setErrorMsg("手机号格式错误");
						errors.add(obj);
					}
				}

				BioneUserInfo user = new BioneUserInfo();
				user.setUserNo(vo.getUserNo());//用户编号
				user.setUserName(vo.getUserName());//姓名
				user.setUserAgname(vo.getUserAgname());//客户经理柜员号
				user.setDeptNo(deptNos.get(vo.getOrgNo()+vo.getDeptName()));//所属部门/取部门ID
				user.setOrgNo(vo.getOrgNo());//所属机构
				user.setUserSts(vo.getUserSts());//账号状态
				user.setEmail(vo.getEmail());//邮箱
				user.setTel(vo.getTel());//电话
				user.setLastUpdateTime(new Timestamp(System.currentTimeMillis()));//修改时间
				user.setLastPwdUpdateTime(new Timestamp(System.currentTimeMillis()));//初始化密码修改时间
				user.setLastUpdateUser(BioneSecurityUtils.getCurrentUserId());;//修改用户
				user.setLogicSysNo(BioneSecurityUtils.getCurrentUserInfo().getCurrentLogicSysNo());//登录系统
				user.setUserId(uuidUserId);//用户id
				user.setUserPwd(null != pwdMap.get(vo.getUserNo()) ? pwdMap.get(vo.getUserNo())	: "1v5hX1gr+LwWZwWckSuDaC71tsmwUuVfm4saAWbH5Wc=");// 初始化密码
				user.setUserIcon("/images/classics/usericons/userhead.png");
				user.setIsBuiltin("0");
				user.setSex(vo.getSex());//性别
				user.setBirthday(vo.getBirthday());//生日
				user.setMobile(vo.getMobile());//手机号
				user.setPostcode(vo.getPostCode());//邮编
				user.setAddress(vo.getAddr());//地址
				user.setIsManager(vo.getIsManager());//是否管理者
				user.setIdCard(vo.getIdCard());//身份证号
				user.setRemark(vo.getRemark());//备注
				users.add(user);
				BioneAuthObjUserRel userRel = new BioneAuthObjUserRel();
				BioneAuthObjUserRelPK userRelPk = new BioneAuthObjUserRelPK();
				userRelPk.setLogicSysNo(BioneSecurityUtils.getCurrentUserInfo().getCurrentLogicSysNo());
				userRelPk.setObjDefNo(GlobalConstants4frame.AUTH_OBJ_DEF_ID_ORG);
				userRelPk.setObjId(user.getOrgNo());
				userRelPk.setUserId(user.getUserId());
				userRel.setId(userRelPk);
				usrRels.add(userRel);
			}
		}
		List<List<?>> lists = new ArrayList<List<?>>();
		lists.add(users);//用户信息
		lists.add(usrRels);//用户机构关系
		EhcacheUtils.put(BioneSecurityUtils.getCurrentUserId(), ehcacheId,lists);
		return errors;
	}
	/*@Override 原有的校验
	public List<ValidErrorInfoObj> validateInfo(String dsId, String ehcacheId) {
		List<ValidErrorInfoObj> errors = new ArrayList<ValidErrorInfoObj>();
		List<BioneUserInfo> users = new ArrayList<BioneUserInfo>();
		List<BioneAuthObjUserRel> userRels = new ArrayList<BioneAuthObjUserRel>();
		List<UserImportVO> vos = (List<UserImportVO>) EhcacheUtils.get(BioneSecurityUtils.getCurrentUserId(), ehcacheId);
		
		List<BioneAuthObjSysRel> sysRels = userBS.getEntityListByProperty(BioneAuthObjSysRel.class,
				"id.logicSysNo",BioneSecurityUtils.getCurrentUserInfo().getCurrentLogicSysNo());//查授权
		Map<String,String> sysRelMap = Maps.newHashMap();
		if(null != sysRels && sysRels.size() > 0){//遍历授权对象
			for(BioneAuthObjSysRel sysRel : sysRels){
				sysRelMap.put(sysRel.getId().getObjDefNo(), sysRel.getId().getLogicSysNo());
			}
		}
		
		if (vos != null && vos.size() > 0) {
			Map<String,String> userNos = getCheckObjMap(BioneUserInfo.class,"user","userNo","userId","");
			Map<String,String> orgNos = getCheckObjMap(BioneOrgInfo.class,"org","orgNo","","");
			Map<String,String> deptIds = getCheckObjMap(BioneDeptInfo.class,"dept","deptName","orgNo","deptId");
			Map<String,String> deptNos = getCheckObjMap(BioneDeptInfo.class,"dept","deptName","orgNo","deptNo");
			Map<String,String> roleNos = getCheckObjMap(BioneRoleInfo.class,"role","roleId","","");
			Map<String,String> pwdMap = getCheckObjMap(BioneUserInfo.class,"user","userNo","userPwd","");
			
			for (UserImportVO vo : vos) {
				try {//其余校验
					Validator.validate(vo);
				} catch (ValidateException e) {
					errors.addAll(e.getErrorInfoObjs());
				}
				
				String uuidUserId = "";//导入保存前生成用户表的user_id，用于两张表的保存
				if("inExcel".equals(userNos.get(vo.getUserNo()))){
					//在循环遍历excell记录时，将记录的工号逐条与map变量的key比较，
					//判断此条记录的工号在excell内部是否存在重复，只校验excel内部，如果与数据库重复，就保留user_id更新库用户信息
					ValidErrorInfoObj obj=new ValidErrorInfoObj();
					obj.setSheetName(vo.getSheetName());
					obj.setExcelRowNo(vo.getExcelRowNo());
					obj.setExcelColNo(2);
					obj.setValidTypeNm("唯一性校验");
					obj.setErrorMsg("导入工号存在重复");
					errors.add(obj);
				}else{//不存在重复就放入到map变量中，确保能全部校验出所有的重复记录
					if(StringUtils.isNotBlank(userNos.get(vo.getUserNo()))){
						uuidUserId = userNos.get(vo.getUserNo());
					}else{
						uuidUserId = RandomUtils.uuid2();
					}
					userNos.put(vo.getUserNo(), "inExcel");
				}
				//校验机构
				if(null == orgNos.get(vo.getOrgNo())){
					ValidErrorInfoObj obj=new ValidErrorInfoObj();
					obj.setSheetName(vo.getSheetName());
					obj.setExcelRowNo(vo.getExcelRowNo());
					obj.setExcelColNo(6);
					obj.setValidTypeNm("存在性校验");
					obj.setErrorMsg("该机构不存在");
					errors.add(obj);
				}else if(null != sysRelMap && null != sysRelMap.get(GlobalConstants4frame.AUTH_OBJ_DEF_ID_ORG)){//如果机构存在且机构有授权，就生成用户机构关系对象
					BioneAuthObjUserRel userRel = new BioneAuthObjUserRel();
					BioneAuthObjUserRelPK pk = new BioneAuthObjUserRelPK();
					pk.setObjId(vo.getOrgNo());
					pk.setUserId(uuidUserId);
					pk.setLogicSysNo(BioneSecurityUtils.getCurrentUserInfo().getCurrentLogicSysNo());
					pk.setObjDefNo(GlobalConstants4frame.AUTH_OBJ_DEF_ID_ORG);
					userRel.setId(pk);
					userRels.add(userRel);
				}
				//校验部门
				if(StringUtils.isNotBlank(vo.getDeptName()) && null == deptIds.get(vo.getOrgNo()+vo.getDeptName())){
					ValidErrorInfoObj obj=new ValidErrorInfoObj();
					obj.setSheetName(vo.getSheetName());
					obj.setExcelRowNo(vo.getExcelRowNo());
					obj.setExcelColNo(7);
					obj.setValidTypeNm("存在性校验");
					obj.setErrorMsg("该部门不存在");
					errors.add(obj);
				}else if(null != sysRelMap && null != sysRelMap.get(GlobalConstants4frame.AUTH_OBJ_DEF_ID_DEPT)){//如果部门存在且已授权，就生成用户部门关系对象
					if(null != deptIds.get(vo.getOrgNo()+vo.getDeptName())) {
						BioneAuthObjUserRel userRel = new BioneAuthObjUserRel();
						BioneAuthObjUserRelPK pk = new BioneAuthObjUserRelPK();
						pk.setObjId(deptIds.get(vo.getOrgNo()+vo.getDeptName()));
						pk.setUserId(uuidUserId);
						pk.setLogicSysNo(BioneSecurityUtils.getCurrentUserInfo().getCurrentLogicSysNo());
						pk.setObjDefNo(GlobalConstants4frame.AUTH_OBJ_DEF_ID_DEPT);
						userRel.setId(pk);
						userRels.add(userRel);
					}
				}
				//校验角色
				if(null != sysRelMap && null != sysRelMap.get(GlobalConstants4frame.AUTH_OBJ_DEF_ID_ROLE) 
						&& StringUtils.isNotBlank(vo.getRoleId())){//先判断是否授权角色，已授权且存在就生成用户角色关系对象
					String[] roleIdArr = StringUtils.split(vo.getRoleId(), ";");
					for(String roleId : roleIdArr){
						if(null == roleNos.get(roleId)){//角色校验
							ValidErrorInfoObj obj=new ValidErrorInfoObj();
							obj.setSheetName(vo.getSheetName());
							obj.setExcelRowNo(vo.getExcelRowNo());
							obj.setExcelColNo(5);
							obj.setValidTypeNm("存在性校验");
							obj.setErrorMsg("含有角色不存在！");
							errors.add(obj);
							break;
						}
						BioneAuthObjUserRel userRel = new BioneAuthObjUserRel();
						BioneAuthObjUserRelPK pk = new BioneAuthObjUserRelPK();
						pk.setObjId(roleId);
						pk.setUserId(uuidUserId);
						pk.setLogicSysNo(BioneSecurityUtils.getCurrentUserInfo().getCurrentLogicSysNo());
						pk.setObjDefNo(GlobalConstants4frame.AUTH_OBJ_DEF_ID_ROLE);
						userRel.setId(pk);
						userRels.add(userRel);
					}
				}
				
				BioneUserInfo user = new BioneUserInfo();
				user.setUserNo(vo.getUserNo());//工号
				user.setUserName(vo.getUserName());//姓名
				user.setUserAgname(vo.getUserAlias());//用户别名
				user.setDeptNo(deptNos.get(vo.getOrgNo()+vo.getDeptName()));//所属部门/取部门ID
				user.setOrgNo(vo.getOrgNo());//所属机构
				user.setUserSts(vo.getUserSts());//账号状态
				user.setEmail(vo.getEmail());//邮箱
				user.setTel(vo.getTel());//电话
				user.setLastUpdateTime(new Timestamp(System.currentTimeMillis()));
				user.setLastPwdUpdateTime(new Timestamp(System.currentTimeMillis()));//初始化密码修改时间
				user.setLastUpdateUser(BioneSecurityUtils.getCurrentUserId());;
				user.setLogicSysNo(BioneSecurityUtils.getCurrentUserInfo().getCurrentLogicSysNo());
				user.setUserId(uuidUserId);
				user.setUserPwd(null != pwdMap.get(vo.getUserNo()) ? pwdMap
						.get(vo.getUserNo())
						: "1v5hX1gr+LwWZwWckSuDaC71tsmwUuVfm4saAWbH5Wc=");// 初始化密码
				user.setUserIcon("images/classics/usericons/userhead.png");
				user.setIsBuiltin("0");
				users.add(user);
			}
		}
		List<List<?>> lists = new ArrayList<List<?>>();
		lists.add(users);//用户信息
		lists.add(userRels);//用户角色关系信息
		EhcacheUtils.put(BioneSecurityUtils.getCurrentUserId(), ehcacheId,lists);
		return errors;
	}*/
	
	private Map<String,String> getCheckObjMap(Class<?> cla,String type,String method1,String method2,String method3){
		Map<String,String> params = new HashMap<String, String>();
		List<?> objs = new ArrayList<>();
		if("org".equals(type)){
			objs = userBS.getEntityList(cla);
		} else {
			objs = userBS.getEntityListByProperty(cla, "logicSysNo", BioneSecurityUtils.getCurrentUserInfo().getCurrentLogicSysNo());
		}
		if(null != objs && objs.size() > 0){
			for(Object obj : objs){
				String str1 = (String) ReflectionUtils.invokeGetter(obj, method1);
				if("dept".equals(type)){
					String str2 = (String) ReflectionUtils.invokeGetter(obj, method2);
					String str3 = (String) ReflectionUtils.invokeGetter(obj, method3);
					params.put(str2+str1,str3);
				}else if("user".equals(type)){
					String str4 = (String) ReflectionUtils.invokeGetter(obj, method2);
					params.put(str1, str4);
				}else{
					params.put(str1, str1);
				}
			}
		}
		return params;
	}
	@Override
	public String exportAll(String ids) {
		return null;
	}
	@Override
	public List<ValidErrorInfoObj> validateVerInfo(String dsId, String ehcacheId) {
		return null;
	}
	@Override
	public void saveData(HttpServletRequest request, String ehcacheId, String dsId) {
		
	}
	
}
