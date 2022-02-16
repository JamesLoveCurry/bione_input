package com.yusys.bione.plugin.wizard.service;

import com.google.common.collect.Maps;
import com.yusys.bione.comp.utils.EhcacheUtils;
import com.yusys.bione.comp.utils.RandomUtils;
import com.yusys.bione.comp.utils.SpringContextHolder;
import com.yusys.bione.frame.base.web.BaseController;
import com.yusys.bione.frame.excel.*;
import com.yusys.bione.frame.security.BioneSecurityUtils;
import com.yusys.bione.frame.validator.common.Validator;
import com.yusys.bione.frame.validator.exception.ValidateException;
import com.yusys.bione.frame.validator.relatedobj.ValidErrorInfoObj;
import com.yusys.bione.plugin.base.common.GlobalConstants4plugin;
import com.yusys.bione.plugin.frsorg.entity.RptFimAddrInfo;
import com.yusys.bione.plugin.rptorg.entity.RptOrgInfo;
import com.yusys.bione.plugin.rptorg.entity.RptOrgInfoPK;
import com.yusys.bione.plugin.rptorg.service.RptOrgInfoBS;
import com.yusys.bione.plugin.wizard.web.vo.OrgImportVO;
import com.yusys.bione.plugin.wizard.web.vo.UploadResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class OrgWizardRequire extends BaseController implements
        IWizardRequire {
    private RptOrgInfoBS rptOrgInfoBS = SpringContextHolder.getBean("rptOrgInfoBS");
    @Autowired
    private ExcelBS excelBS;

    @SuppressWarnings("unchecked")
    @Override
    public UploadResult upload(File file) {
        AbstractExcelImport xlsImport = new ExcelImporter(OrgImportVO.class, file);
        UploadResult result = new UploadResult();
        try {
            List<OrgImportVO> vos = (List<OrgImportVO>) xlsImport
                    .ReadExcel();
            String ehcacheId = RandomUtils.uuid2();
            EhcacheUtils.put(BioneSecurityUtils.getCurrentUserId(), ehcacheId,
                    vos);
            result.setEhcacheId(ehcacheId);
            result.setFileName(file.getName());
            if (vos != null && vos.size() > 0) {
                for (OrgImportVO vo : vos) {
                    result.setInfo(vo.getOrgNo(), vo.getOrgNm());
                }
            }
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return result;
    }

    //导出监管机构信息
    @Override
    public String export(String orgType) {
        String fileName = "";
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("orgType", orgType);
        List<OrgImportVO> vos = this.rptOrgInfoBS.getImportOrg(params);
        List<List<?>> list = new ArrayList<List<?>>();
        list.add(vos);
        XlsExcelTemplateExporter fe = null;
        try {
            fileName = this.getRealPath() + GlobalConstants4plugin.DESIGN_EXPORT_PATH
                    + File.separator + RandomUtils.uuid2() + ".xls";
            fe = new XlsExcelTemplateExporter(fileName, this.getRealPath()
                    + GlobalConstants4plugin.DESIGN_EXPORT_PATH + File.separator
                    + GlobalConstants4plugin.EXPORT_ORG_TEMPLATE_PATH, list);
            fe.run();
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

    @SuppressWarnings("unchecked")
    @Override
    public void saveData(String ehcacheId, String dsId) {
        // TODO Auto-generated method stub
        List<List<OrgImportVO>> lists = (List<List<OrgImportVO>>) EhcacheUtils.get(
                BioneSecurityUtils.getCurrentUserId(), ehcacheId);
        List<OrgImportVO> orgs = (List<OrgImportVO>) lists.get(0);

        //监管机构表的list集合
        List<RptOrgInfo> roiList=new ArrayList<>();
        List<String> fields = new ArrayList<String>();
        fields.add("id.orgNo");
        fields.add("id.orgType");

        //人行特有表
        List<RptFimAddrInfo> rfaiList=new ArrayList<>();
        List<String> fieldrh = new ArrayList<String>();
        fieldrh.add("orgNo");

        for(OrgImportVO org:orgs){
            if(org.getOrgType().equals("03") && org.getRhOrgNo()!=null){//人行大集中且地区编码（人行）不为空时才插到人行特有表
                RptFimAddrInfo vo=new RptFimAddrInfo();//人行大集中特有表 Rpt_Fim_Addr_Info
                vo.setAddrNo(org.getRhOrgNo());
                vo.setAddrNm(org.getRhOrgNm());
                vo.setUpAddrNo(org.getUpOrgNo());
                vo.setDtrctNo(org.getDtrctNo());
                vo.setOrgNm(org.getOrgNm());
                vo.setOrgNo(org.getOrgNo());
                vo.setNamespace(org.getNamespace());
                rfaiList.add(vo);
            }
                RptOrgInfo info=new RptOrgInfo();//监管机构表Rpt_Org_Info，存入公共字段信息，人行特有信息不存
                RptOrgInfoPK pk = new RptOrgInfoPK();
                pk.setOrgNo(org.getOrgNo());
                pk.setOrgType(org.getOrgType());
                info.setMgrOrgNo(org.getMgrOrgNo());
                info.setOrgNm(org.getOrgNm());
                info.setId(pk);
                info.setUpOrgNo(org.getUpOrgNo());
                info.setFinanceOrgNo(org.getFinanceOrgNo());
                info.setOrgSumType(org.getOrgSumType());
                info.setIsVirtualOrg(org.getIsVirtualOrg());
                info.setOrgClass(org.getOrgClass());
                info.setOrgLevel(org.getOrgLevel());
                info.setIsOrgReport(org.getIsOrgReport());
                info.setLeiNo(org.getLeiNo());
                info.setAddr(org.getAddr());
                info.setNamespace(org.getNamespace());
                roiList.add(info);
        }
        this.excelBS.deleteEntityJdbcBatch(roiList, fields);
        this.excelBS.saveEntityJdbcBatch(roiList);

        if(rfaiList.size()>0){
            this.excelBS.deleteEntityJdbcBatch(rfaiList, fieldrh);
            this.excelBS.saveEntityJdbcBatch(rfaiList);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<ValidErrorInfoObj> validateInfo(String dsId, String ehcacheId) {
        // TODO Auto-generated method stub
        List<ValidErrorInfoObj> errors = new ArrayList<ValidErrorInfoObj>();
        List<OrgImportVO> orgs = new ArrayList<OrgImportVO>();
        List<OrgImportVO> vos = (List<OrgImportVO>) EhcacheUtils.get(
                BioneSecurityUtils.getCurrentUserId(), ehcacheId);
        if (vos != null && vos.size() > 0) {
            Map<String, String> orgIdMap = Maps.newHashMap();//验证机构编号和机构类型为标识是否唯一
            Map<String, String> orgNmMap = Maps.newHashMap();//验证机构名称和机构类型为标识是否唯一
            List<String> bioneOrgList = rptOrgInfoBS.getBioneOrgList();//存放所有的机构信息
            List<String> orgTypeList = rptOrgInfoBS.getOrgTypeList();//存放所有的机构类型
            List<String> orgClassList = rptOrgInfoBS.getOrgClassList();//存放所有的机构定义类型
            List<String> addrList = rptOrgInfoBS.getaddrList();//存放所有的关联归属地
            Map<String,String> rhOrgNoMap=rptOrgInfoBS.getrhOrgNoMap();//根据orgNo查询rhorgNo,防止修改这条人行记录时，查出校验问题
            List<String> rhOrgNoList = new ArrayList<>();//防止excel导入多个相同地区编码
            for (OrgImportVO vo : vos) {
                try {
                    Validator.validate(vo);
                } catch (ValidateException e) {
                    errors.addAll(e.getErrorInfoObjs());
                }

                //不能为空的字段：对应行内机构编号（当不是虚拟机构），机构编码,机构名称,机构类型，上级机构，是否参与汇总，namespace
                //当不是虚拟机构，对应行内机构编号不可以为空
                if (vo.getIsVirtualOrg().equals("N") || vo.getIsVirtualOrg() == null) {//不是虚拟机构
                    if (vo.getMgrOrgNo() == null) {//不是虚拟机构，对应行内机构编号不能为空
                        ValidErrorInfoObj vali = new ValidErrorInfoObj();
                        vali.setSheetName(vo.getSheetName());
                        vali.setExcelColNo(1);
                        vali.setExcelRowNo(vo.getExcelRowNo());
                        vali.setValidTypeNm("对应行内机构编号空值校验");
                        vali.setErrorMsg("A列：如果不是虚拟机构，对应行内机构编号不能为空！");
                        errors.add(vali);
                    }else if (!bioneOrgList.contains(vo.getMgrOrgNo())) {
                        ValidErrorInfoObj vali = new ValidErrorInfoObj();
                        vali.setSheetName(vo.getSheetName());
                        vali.setExcelColNo(1);
                        vali.setExcelRowNo(vo.getExcelRowNo());
                        vali.setValidTypeNm("对应行内机构编号来源校验");
                        vali.setErrorMsg("A列：对应行内机构编号【" + vo.getMgrOrgNo() + "】不是来源于现有的机构！");
                        errors.add(vali);
                    }
                }

                //机构名称都不可以为空
                if (vo.getOrgNm() == null) {
                    ValidErrorInfoObj vali = new ValidErrorInfoObj();
                    vali.setSheetName(vo.getSheetName());
                    vali.setExcelColNo(2);
                    vali.setExcelRowNo(vo.getExcelRowNo());
                    vali.setValidTypeNm("机构名称空值校验");
                    vali.setErrorMsg("B列：机构名称不能为空！");
                    errors.add(vali);
                }else{
                    //判断机构名称是否唯一，要求同一机构类型下的机构名称不可以重复
                    if (orgNmMap.containsKey(vo.getOrgNm())) {
                        if (vo.getOrgType().equals(orgNmMap.get(vo.getOrgNm()))) {
                            ValidErrorInfoObj vali = new ValidErrorInfoObj();
                            vali.setSheetName(vo.getSheetName());
                            vali.setExcelColNo(2);
                            vali.setExcelRowNo(vo.getExcelRowNo());
                            vali.setValidTypeNm("机构名称重复校验");
                            vali.setErrorMsg("B列：该机构类型【" + vo.getOrgType() + "】下的机构名称【" + vo.getOrgNm() + "】重复");
                            errors.add(vali);
                        } else {
                            orgNmMap.put(vo.getOrgNm(), vo.getOrgType());
                        }
                    } else {
                        orgNmMap.put(vo.getOrgNm(), vo.getOrgType());
                    }
                }

                //不论是不是虚拟机构，机构编号都不可以为空
                if (vo.getOrgNo() == null) {
                    ValidErrorInfoObj vali = new ValidErrorInfoObj();
                    vali.setSheetName(vo.getSheetName());
                    vali.setExcelColNo(3);
                    vali.setExcelRowNo(vo.getExcelRowNo());
                    vali.setValidTypeNm("机构编号空值校验");
                    vali.setErrorMsg("C列：机构编号不能为空！");
                    errors.add(vali);
                }else{
                    //判断机构编号是否唯一，要求同一机构类型下的机构编号不可以重复
                    if (orgIdMap.containsKey(vo.getOrgNo())) {
                        if (vo.getOrgType().equals(orgIdMap.get(vo.getOrgNo()))) {
                            ValidErrorInfoObj vali = new ValidErrorInfoObj();
                            vali.setSheetName(vo.getSheetName());
                            vali.setExcelColNo(3);
                            vali.setExcelRowNo(vo.getExcelRowNo());
                            vali.setValidTypeNm("机构编号重复校验");
                            vali.setErrorMsg("C列：该机构类型【" + vo.getOrgType() + "】下的机构编号【" + vo.getOrgNo() + "】重复");
                            errors.add(vali);
                        } else {
                            orgIdMap.put(vo.getOrgNo(), vo.getOrgType());
                        }
                    } else {
                        orgIdMap.put(vo.getOrgNo(), vo.getOrgType());
                    }
                }

                //对应行内机构编号必须等于机构编号
                if (vo.getOrgNo()!=null&&vo.getMgrOrgNo()!=null) {
                    if (!vo.getOrgNo().equals(vo.getMgrOrgNo())) {
                        ValidErrorInfoObj vali = new ValidErrorInfoObj();
                        vali.setSheetName(vo.getSheetName());
                        vali.setExcelColNo(3);
                        vali.setExcelRowNo(vo.getExcelRowNo());
                        vali.setValidTypeNm("对应行内机构编号与机构编号是否相同校验");
                        vali.setErrorMsg("如果不是虚拟机构，对应行内机构编号【" + vo.getMgrOrgNo() + "】与机构编号【" + vo.getOrgNo() + "】必须相同！");
                        errors.add(vali);
                    }
                }

                //机构类型不可以为空
                if (vo.getOrgType() == null) {
                    ValidErrorInfoObj vali = new ValidErrorInfoObj();
                    vali.setSheetName(vo.getSheetName());
                    vali.setExcelColNo(4);
                    vali.setExcelRowNo(vo.getExcelRowNo());
                    vali.setValidTypeNm("机构类型空值校验");
                    vali.setErrorMsg("D列：机构类型不能为空！");
                    errors.add(vali);
                } else {
                    //机构类型要来源于现有机构类型
                    if (!orgTypeList.contains(vo.getOrgType())) {
                        ValidErrorInfoObj vali = new ValidErrorInfoObj();
                        vali.setSheetName(vo.getSheetName());
                        vali.setExcelColNo(4);
                        vali.setExcelRowNo(vo.getExcelRowNo());
                        vali.setValidTypeNm("机构类型来源校验");
                        vali.setErrorMsg("D列：机构类型【" + vo.getOrgType() + "】不是来源于现有的机构类型！");
                        errors.add(vali);
                    }
                    //如果不是是大集中，这几个大集中特有字段应该为空
                    if(!vo.getOrgType().equals("03") && (vo.getDtrctNo()!=null || vo.getRhOrgNm()!=null || vo.getRhOrgNo()!=null)){
                        ValidErrorInfoObj vali = new ValidErrorInfoObj();
                        vali.setSheetName(vo.getSheetName());
                        vali.setExcelColNo(4);
                        vali.setExcelRowNo(vo.getExcelRowNo());
                        vali.setValidTypeNm("机构类型大集中特有字段校验");
                        vali.setErrorMsg("D列：机构类型不为大集中时，地区名称、地区编码、人行机构编码应该为空！");
                        errors.add(vali);
                    }
                }

                //上级机构不可以为空
                if (vo.getUpOrgNo() == null) {
                    ValidErrorInfoObj vali = new ValidErrorInfoObj();
                    vali.setSheetName(vo.getSheetName());
                    vali.setExcelColNo(5);
                    vali.setExcelRowNo(vo.getExcelRowNo());
                    vali.setValidTypeNm("上级机构编号空值校验");
                    vali.setErrorMsg("E列：上级机构编号不能为空！");
                    errors.add(vali);
                }

                //是否参与汇总不可以为空
                if (vo.getOrgSumType() == null) {
                    ValidErrorInfoObj vali = new ValidErrorInfoObj();
                    vali.setSheetName(vo.getSheetName());
                    vali.setExcelColNo(7);
                    vali.setExcelRowNo(vo.getExcelRowNo());
                    vali.setValidTypeNm("是否参与汇总空值校验");
                    vali.setErrorMsg("G列：是否参与汇总不能为空！");
                    errors.add(vali);
                }

                //机构定义类型要来源于现有的机构定义类型
                if (vo.getOrgClass() != null) {
                    if (!orgClassList.contains(vo.getOrgClass())) {
                        ValidErrorInfoObj vali = new ValidErrorInfoObj();
                        vali.setSheetName(vo.getSheetName());
                        vali.setExcelColNo(9);
                        vali.setExcelRowNo(vo.getExcelRowNo());
                        vali.setValidTypeNm("机构定义类型来源校验");
                        vali.setErrorMsg("I列：机构定义类型【" + vo.getOrgClass() + "】不是来源于现有的机构定义类型！");
                        errors.add(vali);
                    }
                    if(vo.getOrgLevel()!=null) {
                        //机构层级要与机构定义类型相对应上
                        if (vo.getOrgClass().equals("总行") && !vo.getOrgLevel().equals("1")) {
                            ValidErrorInfoObj vali = new ValidErrorInfoObj();
                            vali.setSheetName(vo.getSheetName());
                            vali.setExcelColNo(10);
                            vali.setExcelRowNo(vo.getExcelRowNo());
                            vali.setValidTypeNm("机构层级校验");
                            vali.setErrorMsg("J列：机构定义类型为【总行】的机构层级应该为【1】！");
                            errors.add(vali);
                        }
                        if (vo.getOrgClass().equals("分行") && !vo.getOrgLevel().equals("2")) {
                            ValidErrorInfoObj vali = new ValidErrorInfoObj();
                            vali.setSheetName(vo.getSheetName());
                            vali.setExcelColNo(10);
                            vali.setExcelRowNo(vo.getExcelRowNo());
                            vali.setValidTypeNm("机构层级校验");
                            vali.setErrorMsg("J列：机构定义类型为【分行】的机构层级应该为【2】！");
                            errors.add(vali);
                        }
                        if (vo.getOrgClass().equals("支行") && !vo.getOrgLevel().equals("3")) {
                            ValidErrorInfoObj vali = new ValidErrorInfoObj();
                            vali.setSheetName(vo.getSheetName());
                            vali.setExcelColNo(10);
                            vali.setExcelRowNo(vo.getExcelRowNo());
                            vali.setValidTypeNm("机构层级校验");
                            vali.setErrorMsg("J列：机构定义类型为【支行】的机构层级应该为【3】！");
                            errors.add(vali);
                        }
                    }else{
                        ValidErrorInfoObj vali = new ValidErrorInfoObj();
                        vali.setSheetName(vo.getSheetName());
                        vali.setExcelColNo(10);
                        vali.setExcelRowNo(vo.getExcelRowNo());
                        vali.setValidTypeNm("机构层级空值校验");
                        vali.setErrorMsg("J列：当设置了机构定义类型，必须填入机构层级，【总行】【分行】【支行】对应【1】【2】【3】！");
                        errors.add(vali);
                    }
                }

                //关联归属地来源于现有的关联归属地
                if (vo.getAddr() != null && !addrList.contains(vo.getAddr())) {
                    ValidErrorInfoObj vali = new ValidErrorInfoObj();
                    vali.setSheetName(vo.getSheetName());
                    vali.setExcelColNo(13);
                    vali.setExcelRowNo(vo.getExcelRowNo());
                    vali.setValidTypeNm("关联归属地来源校验");
                    vali.setErrorMsg("M列：关联归属地【" + vo.getAddr() + "】不是来源于现有的关联归属地！");
                    errors.add(vali);
                }

                //namespace不能为空，也就是机构编号层级不能为空
                if(vo.getNamespace()==null){
                    ValidErrorInfoObj vali = new ValidErrorInfoObj();
                    vali.setSheetName(vo.getSheetName());
                    vali.setExcelColNo(14);
                    vali.setExcelRowNo(vo.getExcelRowNo());
                    vali.setValidTypeNm("机构编号层级空值校验");
                    vali.setErrorMsg("N列：机构编号层级不能为空值");
                    errors.add(vali);
                }

                //地区编码（人行）不能重复
                if(vo.getRhOrgNo()!=null){
                    if(rhOrgNoMap.containsKey(vo.getRhOrgNo())){
                        String orgNoget=rhOrgNoMap.get(vo.getRhOrgNo());//获取RhOrgNo对应的orgNo
                        if(!vo.getOrgNo().equals(orgNoget)){//如果地区编码在数据库存在且不是其对应的机构编号，则说明不是同一条机构的地区编码，地区编码重复了
                            ValidErrorInfoObj vali = new ValidErrorInfoObj();
                            vali.setSheetName(vo.getSheetName());
                            vali.setExcelColNo(16);
                            vali.setExcelRowNo(vo.getExcelRowNo());
                            vali.setValidTypeNm("地区编码（人行）重复值校验");
                            vali.setErrorMsg("P列：地区编码（人行）【" + vo.getRhOrgNo() + "】在数据库已存在！");
                            errors.add(vali);
                        }
                    }
                    if(rhOrgNoList.contains(vo.getRhOrgNo())){
                        ValidErrorInfoObj vali = new ValidErrorInfoObj();
                        vali.setSheetName(vo.getSheetName());
                        vali.setExcelColNo(16);
                        vali.setExcelRowNo(vo.getExcelRowNo());
                        vali.setValidTypeNm("地区编码（人行）重复值校验");
                        vali.setErrorMsg("P列：本次导入的地区编码（人行）【" + vo.getRhOrgNo() + "】有多条相同！");
                        errors.add(vali);
                    }
                    rhOrgNoList.add(vo.getRhOrgNo());
                }

                OrgImportVO org = new OrgImportVO();
                org.setOrgNo(vo.getOrgNo());
                org.setOrgType(vo.getOrgType());
                org.setMgrOrgNo(vo.getMgrOrgNo());
                org.setOrgNm(vo.getOrgNm());
                org.setUpOrgNo(vo.getUpOrgNo());
                org.setFinanceOrgNo(vo.getFinanceOrgNo());
                org.setOrgSumType(vo.getOrgSumType());
                //如果虚拟机构未写，视为不是虚拟机构
                if (org.getIsVirtualOrg() == null) {
                    org.setIsVirtualOrg("N");
                } else {
                    org.setIsVirtualOrg(vo.getIsVirtualOrg());
                }
                org.setOrgClass(vo.getOrgClass());
                org.setOrgLevel(vo.getOrgLevel());
                org.setIsOrgReport(vo.getIsOrgReport());
                org.setLeiNo(vo.getLeiNo());
                org.setAddr(vo.getAddr());
                org.setNamespace(vo.getNamespace());
                //如果地区编码人行（字段名为addr_no,或者rhorgno)不为空且是人行大集中报送，则存值
                //如果地区编码人行（字段名为addr_no,或者rhorgno)为空（即使是人行大集中报送模式），也自动不存这三条人行特有字段信息，前台不显示报错
                if(vo.getRhOrgNo()!=null &&vo.getOrgType().equals("03")) {
                    org.setRhOrgNo(vo.getRhOrgNo());
                    org.setRhOrgNm(vo.getRhOrgNm());
                    org.setDtrctNo(vo.getDtrctNo());
                }
                orgs.add(org);
            }
        }
        List<List<?>> lists = new ArrayList<List<?>>();
        lists.add(orgs);
        EhcacheUtils.put(BioneSecurityUtils.getCurrentUserId(), ehcacheId,
                lists);
        return errors;
    }

    @Override
    public String exportAll(String ids) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<ValidErrorInfoObj> validateVerInfo(String dsId, String ehcacheId) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void saveData(HttpServletRequest request, String ehcacheId, String dsId) {
        // TODO Auto-generated method stub

    }
}
