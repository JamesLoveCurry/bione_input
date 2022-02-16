package com.yusys.bione.frame.user.entity;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.sql.Timestamp;
/**
 * The persistent class for the BIONE_USER_INFO database table.
 *
 */
@Entity
@Table(name="BIONE_USER_INFO")
public class BioneUserInfo implements Serializable {
	private static final long serialVersionUID = 1L;
	@Id
	@Column(name="USER_ID", unique=true, nullable=false, length=32)
	private String userId;
	@Column(length=500)
	private String address;
	@Column(length=10)
	private String birthday;
	@Column(name="DEPT_NO", length=32)
	private String deptNo;
	@Column(length=100)
	private String email;

	@Column(name="LOGIC_SYS_NO", nullable=false, length=32)
	private String logicSysNo;
	@Column(name="LAST_PWD_UPDATE_TIME")
	private Timestamp lastPwdUpdateTime;
	@Column(name="LAST_UPDATE_TIME")
	private Timestamp lastUpdateTime;
	@Column(name="LAST_UPDATE_USER", length=100)
	private String lastUpdateUser;
	@Column(length=20)
	private String mobile;
	@Column(name="ORG_NO", nullable=false, length=32)
	private String orgNo;
	@Column(length=10)
	private String postcode;
	@Column(length=500)
	private String remark;
	@Column(length=10)
	private String sex;
	@Column(length=20)
	private String tel;
	@Column(name="USER_NAME", length=100)
	private String userName;
	@Column(name="USER_NO", nullable=false, length=32)
	private String userNo;
	@Column(name="USER_PWD", nullable=false, length=100)
	private String userPwd;
	@Column(name="USER_STS", length=1)
	private String userSts;

	@Column(name="IS_BUILTIN", length=1)
	private String isBuiltin;

	@Column(name="USER_ICON", length=500)
	private String userIcon;

	@Column(name="USER_AGNAME", length=100)
	private String userAgname;

	@Column(name="IS_MANAGER", length=1)
	private String isManager;
	@Column(name="ID_CARD", length = 50)
	private String idCard;
	@Column(name="GUID", length = 50)
	private String guid;//LDAP标识
	@Column(name="SET_ID", length = 50)
	private String setId;//组织编号
	@Column(name="BUID", length = 50)
	private String buid;//利润中心编码
	@Column(name="PD_LAST4", length = 50)
	private String nationalIDLast4;//身份证后4位
	@Column(name="DISPLAY_NAME", length = 50)
	private String displayName;//显示名
	@Column(name="LAST_NAME", length = 50)
	private String lastName;//姓氏
	@Column(name="LAST_NAME_CHN", length = 50)
	private String lastNameCHN;//姓拼音
	@Column(name="FIRST_NAME", length = 50)
	private String firstName;//名字
	@Column(name="FIRST_NAME_CHN", length = 50)
	private String firstNameCHN;//名拼音
	//private String name;//姓氏+名字
	@Column(name="USER_CODE", length = 50)
	private String userCode;//员工编号
	public BioneUserInfo() {
	}
	public String getUserId() {
		return this.userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getAddress() {
		return this.address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getBirthday() {
		return this.birthday;
	}
	public void setBirthday(String birthday) {
		this.birthday = birthday;
	}
	public String getDeptNo() {
		return this.deptNo;
	}
	public void setDeptNo(String deptNo) {
		this.deptNo = deptNo;
	}
	public String getEmail() {
		return this.email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public Timestamp getLastPwdUpdateTime() {
		return this.lastPwdUpdateTime;
	}
	public void setLastPwdUpdateTime(Timestamp lastPwdUpdateTime) {
		this.lastPwdUpdateTime = lastPwdUpdateTime;
	}
	public Timestamp getLastUpdateTime() {
		return this.lastUpdateTime;
	}
	public void setLastUpdateTime(Timestamp lastUpdateTime) {
		this.lastUpdateTime = lastUpdateTime;
	}
	public String getLastUpdateUser() {
		return this.lastUpdateUser;
	}
	public void setLastUpdateUser(String lastUpdateUser) {
		this.lastUpdateUser = lastUpdateUser;
	}
	public String getMobile() {
		return this.mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	public String getOrgNo() {
		return this.orgNo;
	}
	public void setOrgNo(String orgNo) {
		this.orgNo = orgNo;
	}
	public String getPostcode() {
		return this.postcode;
	}
	public void setPostcode(String postcode) {
		this.postcode = postcode;
	}
	public String getRemark() {
		return this.remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public String getSex() {
		return this.sex;
	}
	public void setSex(String sex) {
		this.sex = sex;
	}
	public String getTel() {
		return this.tel;
	}
	public void setTel(String tel) {
		this.tel = tel;
	}
	public String getUserName() {
		return this.userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getUserNo() {
		return this.userNo;
	}
	public void setUserNo(String userNo) {
		this.userNo = userNo;
	}
	public String getUserPwd() {
		return this.userPwd;
	}
	public void setUserPwd(String userPwd) {
		this.userPwd = userPwd;
	}
	public String getUserSts() {
		return this.userSts;
	}
	public void setUserSts(String userSts) {
		this.userSts = userSts;
	}
	public String getIsBuiltin() {
		return isBuiltin;
	}
	public void setIsBuiltin(String isBuiltin) {
		this.isBuiltin = isBuiltin;
	}
	public String getUserIcon() {
		return userIcon;
	}
	public void setUserIcon(String userIcon) {
		this.userIcon = userIcon;
	}
	public String getLogicSysNo() {
		return logicSysNo;
	}
	public void setLogicSysNo(String logicSysNo) {
		this.logicSysNo = logicSysNo;
	}
	public String getUserAgname() {
		return userAgname;
	}
	public void setUserAgname(String userAgname) {
		this.userAgname = userAgname;
	}


	public String getIsManager() {
		return isManager;
	}
	public void setIsManager(String isManager) {
		this.isManager = isManager;
	}
	public String getGuid() {
		return guid;
	}
	public void setGuid(String guid) {
		this.guid = guid;
	}
	public String getSetId() {
		return setId;
	}
	public void setSetId(String setId) {
		this.setId = setId;
	}
	public String getBuid() {
		return buid;
	}
	public void setBuid(String buid) {
		this.buid = buid;
	}
	public String getNationalIDLast4() {
		return nationalIDLast4;
	}
	public void setNationalIDLast4(String nationalIDLast4) {
		this.nationalIDLast4 = nationalIDLast4;
	}
	public String getDisplayName() {
		return displayName;
	}
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public String getLastNameCHN() {
		return lastNameCHN;
	}
	public void setLastNameCHN(String lastNameCHN) {
		this.lastNameCHN = lastNameCHN;
	}
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getFirstNameCHN() {
		return firstNameCHN;
	}
	public void setFirstNameCHN(String firstNameCHN) {
		this.firstNameCHN = firstNameCHN;
	}
	public String getUserCode() {
		return userCode;
	}
	public void setUserCode(String userCode) {
		this.userCode = userCode;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((userId == null) ? 0 : userId.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		BioneUserInfo other = (BioneUserInfo) obj;
		if (userId == null) {
			if (other.userId != null)
				return false;
		} else if (!userId.equals(other.userId))
			return false;
		return true;
	}
	public String getIdCard() {
		return idCard;
	}
	public void setIdCard(String idCard) {
		this.idCard = idCard;
	}
}