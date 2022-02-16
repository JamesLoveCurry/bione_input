package com.yusys.bione.plugin.regulation.util;

import com.yusys.bione.comp.utils.EhcacheUtils;
import com.yusys.bione.frame.util.excel.ExcelEventListener;
import com.yusys.bione.frame.util.excel.ExcelEventReader;
import com.yusys.bione.plugin.design.web.vo.ExportReportInfoVO;
import com.yusys.bione.plugin.regulation.enums.*;
import com.yusys.bione.plugin.spreadjs.service.ExcelReaderFactory;
import com.yusys.bione.plugin.spreadjs.service.IExcelReader;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class VersionDataHandling implements Closeable, ExcelEventListener {

    private static Logger logger = LoggerFactory.getLogger(VersionDataHandling.class);
    private Map<String, ExcelEventReader> excelEventReaderMap = new HashMap<>();
    //导入文件路径
    private String excelFileDir;
    //错误日志
    private StringBuilder errorMessageBuilder;
    //导入的制度包内容信息
    List<Map<String, Map<String, String>>> rptList = new ArrayList<>();
    //报表基础信息
    Map<String, String> rptBaseInfo;
    List<ExportReportInfoVO> exportReportInfoVOList;
    //报表编号集合
    List<String> rptNums = new ArrayList<>();
    //各类指标位置信息
    Map<Integer, Map<String, List<String>>> rptCellSide;
    //校验错误数量
    int errorCount = 0;
    //是否沿用系统内报表指标配置
    private boolean fromSystemRptCfg;

	//excel公式集合缓存Key
	private String EXCEL_FORMULA_KEY = "excel_formula_key";
	
	private File[] excelFiles;

    public VersionDataHandling(String excelFileDir, Map<String, String> optionMap) {
        this.excelFileDir = excelFileDir;
        errorMessageBuilder = new StringBuilder();
        rptBaseInfo = new HashMap<>();
        exportReportInfoVOList = new ArrayList<>();
        rptCellSide = new HashMap<>();
        fromSystemRptCfg = StringUtils.isNotEmpty(optionMap.get("fromSystemRptCfg"));
    }

    /**
     * 属性值转换
     *
     * @param code 属性值
     **/
    public static String transCode(String code) {
        if ("可跑数汇总".equals(code) || "可填报汇总".equals(code) || "可修改".equals(code)) {
            return "Y";
        }
        if ("不可跑数汇总".equals(code) || "不可填报汇总".equals(code) || "不可修改".equals(code)) {
            return "N";
        }
        return null;
    }

    /**
     * 返回数据内容
     *
     * @return rptList 制度包属性内容
     * exportReportInfoVOList 制度包报表信息
     * rptCellSide 制度包各类单元格位置
     **/
    public Map<String, Object> getParams() {
        Map<String, Object> out = new HashMap<>();
        out.put("rptList", rptList);
        out.put("exportReportInfoVOList", exportReportInfoVOList);
        out.put("rptCellSide", rptCellSide);
        out.put("excelFileDir", excelFileDir);
        out.put("excelFiles", excelFiles);
        return out;
    }

    /**
     * 新制度包数据读取方法
     *
     * @return Excel数据集合
     **/
    public void loadData() throws IOException {
        File dir = new File(excelFileDir);
        excelFiles = dir.listFiles();
        if (excelFiles != null && excelFiles.length > 0) {
            for (File file : excelFiles) {
                Map<String, Map<String, String>> rptMap = new HashMap<>();

                String filePath = file.getPath();
                ExcelEventReader excelEventReader = new ExcelEventReader();
                excelEventReader.setExcelEventListener(this);
                excelEventReaderMap.put(filePath, excelEventReader);
                excelEventReader.startReadSheet(filePath, "1-报表基础信息");

                Map<String, String> rptParamMap = new HashMap<>();
                // 获取报表基础信息并进行对象赋值
                rptMap.put("rptBaseInfo", rptBaseInfo);
                ExportReportInfoVO exportReportInfoVO = initBaseInfo();
                // 获取报表表样信息
                IExcelReader reader = ExcelReaderFactory.createReader(new File(filePath));
                String sheetJson = reader.readTmpToString();
                //下面方法获取样式有问题
                //rptParamMap = excelEventReader.getSheetData(filePath, "2-报表表样");
                //String sheetJson = excelEventReader.getSheetJson(filePath, "2-报表表样");
                rptParamMap.put("sheetJson", sheetJson);
                rptMap.put("rptInfo", rptParamMap);
                // 报表基础信息校验
                rptParamMap = excelEventReader.getSheetData(filePath, "1-报表基础信息");
                if (rptParamMap.size() == 0) reportError(file.getName()+"模板中[1-报表基础信息]不存在");
                // 表样校验
                rptParamMap = excelEventReader.getSheetData(filePath, "2-报表表样");
                if (rptParamMap.size() == 0) reportError(file.getName()+"模板中[2-报表表样]不存在");
                // 获取报表指标编号信息
                rptParamMap = excelEventReader.getSheetData(filePath, "3-报表指标");
                if (rptParamMap.size() == 0) reportError(file.getName()+"模板中[3-报表指标]不存在");
                rptMap.put("rptIdx", rptParamMap);
                // 获取人行报文编号信息
                rptParamMap = excelEventReader.getSheetData(filePath, "4-人行编码");
                if (rptParamMap.size() == 0) reportError(file.getName()+"模板中[4-人行编码]不存在");
                rptMap.put("rptBusiNo", rptParamMap);
                // 获取单元格属性信息
                rptParamMap = excelEventReader.getSheetData(filePath, "5-单元格属性");
                if (rptParamMap.size() == 0) reportError(file.getName()+"模板中[5-单元格属性]不存在");
                rptMap.put("rptCell", rptParamMap);
                // 获取来源数据信息
                rptParamMap = excelEventReader.getSheetData(filePath, "6-来源数据");
                if (rptParamMap.size() == 0) reportError(file.getName()+"模板中[6-来源数据]不存在");
                rptMap.put("rptSource", rptParamMap);
                // 获取单元格名称信息
                rptParamMap = excelEventReader.getSheetData(filePath, "7-单元格名称");
                if (rptParamMap.size() == 0) reportError(file.getName()+"模板中[7-单元格名称]不存在");
                rptMap.put("rptCellNm", rptParamMap);
                // 获取单元格是否汇总||可修改信息
                rptParamMap = excelEventReader.getSheetData(filePath, "8-单元格是否跑数汇总||是否填报汇总||可修改");
                if (rptParamMap.size() == 0) reportError(file.getName()+"模板中[8-单元格是否跑数汇总||是否填报汇总||可修改]不存在");
                rptMap.put("idxSumUpt", rptParamMap);
                // 获取数据显示格式||单位||精度信息
                rptParamMap = excelEventReader.getSheetData(filePath, "9-数据显示格式||单位||精度");
                if (rptParamMap.size() == 0) reportError(file.getName()+"模板中[9-数据显示格式||单位||精度]不存在");
                rptMap.put("cellDisplayUnit", rptParamMap);
                // 获取Excel公式属性信息
                rptParamMap = excelEventReader.getSheetData(filePath, "10-Excel公式属性");
                if (rptParamMap.size() == 0) reportError(file.getName()+"模板中[10-Excel公式属性]不存在");
                rptMap.put("excelFormula", rptParamMap);
                // 获取明细特有属性信息
                rptParamMap = excelEventReader.getSheetData(filePath, "11-明细特有属性");
                if (rptParamMap.size() == 0) reportError(file.getName()+"模板中[11-明细特有属性]不存在");
                rptMap.put("detailed", rptParamMap);
                // 获取指标过滤信息
                rptParamMap = excelEventReader.getSheetData(filePath, "12-指标过滤");
                if (rptParamMap.size() == 0) reportError(file.getName()+"模板中[12-指标过滤]不存在");
                rptMap.put("rptFilter", rptParamMap);
                // 获取业务说明信息
                rptParamMap = excelEventReader.getSheetData(filePath, "13-业务说明");
                if (rptParamMap.size() == 0) reportError(file.getName()+"模板中[13-业务说明]不存在");
                rptMap.put("cellCaliberExplain", rptParamMap);
                // 获取技术口径信息
                rptParamMap = excelEventReader.getSheetData(filePath, "14-技术口径");
                if (rptParamMap.size() == 0) reportError(file.getName()+"模板中[14-技术口径]不存在");
                rptMap.put("cellCaliberTechnology", rptParamMap);

                rptList.add(rptMap);
                exportReportInfoVOList.add(exportReportInfoVO);
            }
            //导入完成后清空excel公式缓存
    		EhcacheUtils.remove(EXCEL_FORMULA_KEY, EXCEL_FORMULA_KEY);
        }
    }

    /**
     * 初始化报表基础信息
     **/
    private ExportReportInfoVO initBaseInfo() {
        ExportReportInfoVO exportReportInfoVO = new ExportReportInfoVO();
        for (String key : rptBaseInfo.keySet()) {
            switch (key) {
                case "报表编号":
                    if (StringUtils.isNotEmpty(rptBaseInfo.get(key))) {
                        exportReportInfoVO.setRptNum(rptBaseInfo.get(key));
                    } else {
                        reportError("报表编号配置异常，不允许为空值");
                    }
                    break;
                case "版本号":
                    if (StringUtils.isNotEmpty(rptBaseInfo.get(key))) {
                        exportReportInfoVO.setVerId(new BigDecimal(rptBaseInfo.get(key)));
                    } else {
                        reportError("版本配置异常，不允许为空值");
                    }
                    break;
                case "报表名称":
                    exportReportInfoVO.setRptNm(rptBaseInfo.get(key));
                    break;
                case "报表目录":
                    exportReportInfoVO.setCatalogNm(rptBaseInfo.get(key));
                    break;
                case "报表类型":
                    if (StringUtils.isNotEmpty(rptBaseInfo.get(key))) {
                        exportReportInfoVO.setTemplateType(TemplateType.get(rptBaseInfo.get(key)).toString());
                    } else {
                        reportError("模版类型配置异常，不允许为空值");
                    }
                    break;
                case "生成周期":
                    exportReportInfoVO.setRptCycle(RptCycle.get(rptBaseInfo.get(key)).toString());
                    break;
                case "报表单位":
                    exportReportInfoVO.setTemplateUnit(TemplateUnit.get(rptBaseInfo.get(key)).toString());
                    break;
                case "报表状态":
                    exportReportInfoVO.setRptSts(YesOrNo.get(rptBaseInfo.get(key)).toString());
                    break;
                case "业务类型":
                	exportReportInfoVO.setBusiType(rptBaseInfo.get(key));
                    break;
                case "联系人":
                    exportReportInfoVO.setUserName(rptBaseInfo.get(key));
                    break;
                case "升级概况":
                    exportReportInfoVO.setTemplateNm(TemplateNm.get(rptBaseInfo.get(key)).toString());
                    break;
                case "是否修改":
                    exportReportInfoVO.setIsUpt(YesOrNo.get(rptBaseInfo.get(key)).toString());
                    break;
                case "业务分库":
                    exportReportInfoVO.setBusiLibNm(rptBaseInfo.get(key));
                    break;
                case "报表业务名称":
                    exportReportInfoVO.setRptBusiNm(rptBaseInfo.get(key));
                    break;
                case "业务定义":
                    exportReportInfoVO.setRptDesc(rptBaseInfo.get(key));
                    break;
                case "填报说明":
                    exportReportInfoVO.setFillDesc(rptBaseInfo.get(key));
                    break;
                case "制度表样版本":
                    exportReportInfoVO.setTmpVersionId(rptBaseInfo.get(key));
                    break;
                case "制度表样编号":
                    exportReportInfoVO.setRepId(rptBaseInfo.get(key));
                    break;
                case "上报编码":
                    exportReportInfoVO.setReportCode(rptBaseInfo.get(key));
                    break;
                case "是否定长":
                    exportReportInfoVO.setFixedLength(YesOrNo.get(rptBaseInfo.get(key)).toString());
                    break;
                case "是否分页":
                    exportReportInfoVO.setIsPaging(YesOrNo.get(rptBaseInfo.get(key)).toString());
                    break;
                case "报表导入配置":
                    exportReportInfoVO.setImportConfig(rptBaseInfo.get(key));
                    break;
                case "合计排序SQL":
                    exportReportInfoVO.setSortSql(rptBaseInfo.get(key));
                    break;
            }
        }
        return exportReportInfoVO;
    }

    @Override
    public void rowDataReady(ExcelEventReader excelEventReader, int rowIdx, Map<Integer, String> rowDataMap) {
        //读取文件处理
        if ("1-报表基础信息".equals(excelEventReader.getSheetName())) {
            if (rowDataMap != null) rptBaseInfo.put(rowDataMap.get(1), rowDataMap.get(2));
            if (rowDataMap != null && "报表编号".equals(rowDataMap.get(1))) {
                if (rptNums.contains(rowDataMap.get(2))) {
                    reportError("报表指标编号重复:" + rowDataMap.get(2));
                } else {
                    rptNums.add(rowDataMap.get(2));
                }
            }
        }
    }

    /**
     * 错误日志返回
     **/
    public String getErrorMessage() {
        return errorMessageBuilder.toString();
    }

    /**
     * 导入数据校验
     *
     * @param conn 数据库连接
     **/
    public void verifyData(Connection conn) throws SQLException {
        //制度信息准备
        //数据校验
        logger.info("校验数据准备完成，开始进行校验");
        for (int i = 0; i < rptList.size(); i++) {
            //报表基础信息校验
            verifyBaseData(conn, exportReportInfoVOList.get(i));
        }
        //指标分类及校验
        idxCellSortAndValid(conn);
    }

    /**
     * 指标按类别拆分
     *
     * @param conn 数据库连接
     **/
    private void idxCellSortAndValid(Connection conn) throws SQLException {
        for (int i = 0; i < rptList.size(); i++) {
            //报表指标编号集合
            List<String> rptIndexNos = new ArrayList<>();
            //模型单元格坐标
            List<String> modelCells = new ArrayList<>();
            //指标单元格坐标
            List<String> idxCells = new ArrayList<>();
            //表间取数单元格坐标
            List<String> formulaCells = new ArrayList<>();
            //excel单元格坐标
            List<String> excelCells = new ArrayList<>();
            //表达式单元格
            List<String> staticeCells = new ArrayList<>();
            //可编辑单元格
            List<String> comCells = new ArrayList<>();
            Map<String, String> rptParamMap = rptList.get(i).get("rptCell");
            //单元格分类
            for (String key : rptParamMap.keySet()) {
                if ("数据模型单元格".equals(rptParamMap.get(key))) {
                    modelCells.add(key);
                    continue;
                }
                if ("指标单元格".equals(rptParamMap.get(key))) {
                    idxCells.add(key);
                    continue;
                }
                if ("表间取数单元格".equals(rptParamMap.get(key))) {
                    formulaCells.add(key);
                    continue;
                }
               
            	if ("Excel单元格".equals(rptParamMap.get(key))) {
                    excelCells.add(key);
                    continue;
                }

                if ("表达式".equals(rptParamMap.get(key))) {
                    staticeCells.add(key);
                    continue;
                }
                if ("可编辑单元格".equals(rptParamMap.get(key))) {
                    comCells.add(key);
                }
            }
            //报表指标校验
            verifyRptIdx(i, conn, idxCells, formulaCells, excelCells, staticeCells, rptIndexNos);
            //若选择沿用导入，则不进行指标配置相关校验
            if (!fromSystemRptCfg){
                //报表来源数据校验
                verifyRptSource(i, conn, modelCells, idxCells, formulaCells, staticeCells, excelCells);
                //8-单元格是否汇总||可修改
                verifyIdxSumUpt(i, modelCells, idxCells, formulaCells, excelCells);
                //9-数据显示格式||单位||精度
                verifyCellDisplayUnit(i, modelCells, idxCells, formulaCells, excelCells);
                //10-Excel公式属性
                verifyExcelFormula(i, excelCells);
                //11-明细特有属性
                verifyDetailed(i, modelCells);
                //12-指标过滤
                verifyFilter(i, idxCells, conn);
            }

            //构造返回处理属性
            Map<String, List<String>> rptSide = new HashMap<>();
            rptSide.put("rptIndexNos", rptIndexNos);
            rptSide.put("modelCells", modelCells);
            rptSide.put("idxCells", idxCells);
            rptSide.put("formulaCells", formulaCells);
            rptSide.put("excelCells", excelCells);
            rptSide.put("staticeCells", staticeCells);
            rptSide.put("comCells", comCells);
            List<String> allCells = new ArrayList<>();
            allCells.addAll(modelCells);
            allCells.addAll(idxCells);
            allCells.addAll(formulaCells);
            allCells.addAll(staticeCells);
            allCells.addAll(comCells);
            allCells.addAll(excelCells);
            rptSide.put("allCells", allCells);
            rptCellSide.put(i, rptSide);
        }
    }

    /**
     * 12-指标过滤校验
     *
     * @param connection     数据库连接
     * @param i        报表序号
     * @param idxCells 指标单元格位置
     **/
    private void verifyFilter(int i, List<String> idxCells, Connection connection) throws SQLException {
        Map<String, String> rptParamMap = rptList.get(i).get("rptFilter");
        Statement statement = connection.createStatement();
        Statement statement1 = connection.createStatement();
        for (String key : idxCells) {
            if (StringUtils.isNotEmpty(rptParamMap.get(key))) {
                String[] filterStrings = rptParamMap.get(key).split("&&");
                List<String> exitFilterType = new ArrayList<>();
                for (String rptfilter : filterStrings) {
                    if (StringUtils.isNotEmpty(rptfilter) && rptfilter.split("=").length > 1) {
                        //处理过滤大类
                        String filterType = rptfilter.split("=")[0];
                        List<String> filterTypes = new ArrayList<>();
                        //过滤明细
                        String filterItem = rptfilter.split("=")[1];
                        // 将正则公式(?<=\\()(([a-z]|[A-Z]|[0-9])+)(?=\\))替换成(?<=\\()(\\S+)(?=\\))
                        String regex = "(?<=\\()(\\S+)(?=\\))";
                        Pattern pattern = Pattern.compile(regex);
                        if (StringUtils.isNotEmpty(filterType)) {
                            Matcher matcher = pattern.matcher(filterType);
                            String type = "";
                            while (matcher.find()) {
                            	type = matcher.group();
                                filterTypes.add(matcher.group());
                            }
                            if("ORG".equals(type)){
                            	if (exitFilterType.contains(filterTypes.get(filterTypes.size() - 1))) {
                                    reportError("[" + exportReportInfoVOList.get(i).getRptNm() + "][12-指标过滤][" + key + "]:" + filterTypes.get(filterTypes.size() - 1) + "维度配置异常");
                                } else {
                                    exitFilterType.add(filterTypes.get(filterTypes.size() - 1));
                                    if (StringUtils.isNotEmpty(filterItem)) {
                                        for (String item : filterItem.split(",")) {
                                            List<String> filterItems = new ArrayList<>();
                                            Matcher matcher1 = pattern.matcher(item);
                                            while (matcher1.find()) {
                                                filterItems.add(matcher1.group());
                                            }
                                            String itemSql = "SELECT count(1) as count FROM rpt_org_info o left join bione_param_info p on o.org_type = p.param_value and p.param_type_no = 'idxTaskType' where p.param_name = '"+exportReportInfoVOList.get(i).getBusiType()+"' and o.org_no = '" + filterItems.get(filterItems.size() - 1) + "'";
                                            logger.info("执行的sql:" + itemSql);
                                            ResultSet resultSet1 = statement1.executeQuery(itemSql);
                                            while (resultSet1.next()) {
                                                int count1 = resultSet1.getInt("count");
                                                if (count1 == 0) {
                                                    reportError("[" + exportReportInfoVOList.get(i).getRptNm() + "][12-指标过滤][" + key + "]:" + filterTypes.get(filterTypes.size() - 1) + "维度中" + filterItems.get(filterItems.size() - 1) + "不存在");
                                                }
                                            }
                                        }
                                    }
                                }
                            }else{
                            	if (exitFilterType.contains(filterTypes.get(filterTypes.size() - 1))) {
                                    reportError("[" + exportReportInfoVOList.get(i).getRptNm() + "][12-指标过滤][" + key + "]:" + filterTypes.get(filterTypes.size() - 1) + "维度配置异常");
                                } else {
                                    exitFilterType.add(filterTypes.get(filterTypes.size() - 1));
                                    String sql = "select count(1) as count from rpt_dim_type_info where dim_type_no = '" + filterTypes.get(filterTypes.size() - 1) + "'";
                                    logger.info("执行的sql:" + sql);
                                    ResultSet resultSet = statement.executeQuery(sql);
                                    while (resultSet.next()) {
                                        int count = resultSet.getInt("count");
                                        if (count == 0) {
                                            reportError("[" + exportReportInfoVOList.get(i).getRptNm() + "][12-指标过滤][" + key + "]:" + filterTypes.get(filterTypes.size() - 1) + "维度类型不存在");
                                        } else {
                                            if (StringUtils.isNotEmpty(filterItem)) {
                                                for (String item : filterItem.split(",")) {
                                                    List<String> filterItems = new ArrayList<>();
                                                    Matcher matcher1 = pattern.matcher(item);
                                                    while (matcher1.find()) {
                                                        filterItems.add(matcher1.group());
                                                    }
                                                    String itemSql = "select count(1) as count from rpt_dim_item_info where dim_type_no = '" + filterTypes.get(filterTypes.size() - 1) + "' and dim_item_no = '" + filterItems.get(filterItems.size() - 1) + "'";
                                                    logger.info("执行的sql:" + itemSql);
                                                    ResultSet resultSet1 = statement1.executeQuery(itemSql);
                                                    while (resultSet1.next()) {
                                                        int count1 = resultSet1.getInt("count");
                                                        if (count1 == 0) {
                                                            reportError("[" + exportReportInfoVOList.get(i).getRptNm() + "][12-指标过滤][" + key + "]:" + filterTypes.get(filterTypes.size() - 1) + "维度中" + filterItems.get(filterItems.size() - 1) + "不存在");
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * 11-明细特有属性校验
     *
     * @param i          报表序号
     * @param modelCells 模型单元格位置
     **/
    @SuppressWarnings("serial")
	private void verifyDetailed(int i, List<String> modelCells) {
        Map<String, String> rptParamMap = rptList.get(i).get("detailed");
        for (String key : modelCells) {
            if (StringUtils.isNotEmpty(rptParamMap.get(key))) {
                if (rptParamMap.get(key).split("&&").length == 9) {
                    //扩展
                    if ("扩展".equals(rptParamMap.get(key).split("&&")[0])) {
                        if (!ExtendDirection.get(rptParamMap.get(key).split("&&")[1]).valid())
                            reportError("[" + exportReportInfoVOList.get(i).getRptNm() + "][11-明细特有属性][" + key + "]:扩展方向配置错误，请配置：行扩展,列扩展");
                        if (!ExtendMode.get(rptParamMap.get(key).split("&&")[2]).valid())
                            reportError("[" + exportReportInfoVOList.get(i).getRptNm() + "][11-明细特有属性][" + key + "]:扩展方式配置错误，请配置：插入,覆盖");
                    } else if ("不扩展".equals(rptParamMap.get(key).split("&&")[0])) {
                        if (!"--".equals(rptParamMap.get(key).split("&&")[1]))
                            reportError("[" + exportReportInfoVOList.get(i).getRptNm() + "][11-明细特有属性][" + key + "]:扩展方向配置错误，请配置：--");
                        if (!"--".equals(rptParamMap.get(key).split("&&")[2]))
                            reportError("[" + exportReportInfoVOList.get(i).getRptNm() + "][11-明细特有属性][" + key + "]:扩展方式配置错误，请配置：--");
                    } else {
                        reportError("[" + exportReportInfoVOList.get(i).getRptNm() + "][11-明细特有属性][" + key + "]:是否扩展配置错误，请配置：扩展，不扩展");
                    }
                    //排序
                    if ("排序".equals(rptParamMap.get(key).split("&&")[3])) {
                        if (!SortMode.get(rptParamMap.get(key).split("&&")[4]).valid())
                        	reportError("[" + exportReportInfoVOList.get(i).getRptNm() + "][11-明细特有属性][" + key + "]:排序方式配置错误，请配置：顺序,倒序");
                        if(StringUtils.isBlank(rptParamMap.get(key).split("&&")[5].toString()) || !StringUtils.isNumeric(rptParamMap.get(key).split("&&")[5])){
                        	reportError("[" + exportReportInfoVOList.get(i).getRptNm() + "][11-明细特有属性][" + key + "]:排序顺序配置错误，请配置：1,2,...n");
                        }
                    } else if ("不排序".equals(rptParamMap.get(key).split("&&")[3])) {
                        if (!"--".equals(rptParamMap.get(key).split("&&")[4]))
                            reportError("[" + exportReportInfoVOList.get(i).getRptNm() + "][11-明细特有属性][" + key + "]:排序方式配置错误，请配置：--");
                        if (!"--".equals(rptParamMap.get(key).split("&&")[5]))
                            reportError("[" + exportReportInfoVOList.get(i).getRptNm() + "][11-明细特有属性][" + key + "]:排序顺序配置错误，请配置：--");
                    } else {
                        reportError("[" + exportReportInfoVOList.get(i).getRptNm() + "][11-明细特有属性][" + key + "]:是否排序配置错误，请配置：排序，不排序");
                    }
                    //转码
                    List<String> isTrans = new ArrayList<String>() {{
                        add("--");
                        add("转码");
                        add("不转码");
                    }};
                    if (!isTrans.contains(rptParamMap.get(key).split("&&")[6])) {
                        reportError("[" + exportReportInfoVOList.get(i).getRptNm() + "][11-明细特有属性][" + key + "]:是否转码配置错误，请配置：转码，不转码");
                    }
                    //是否合并
                    List<String> isMerge = new ArrayList<String>() {{
                        add("--");
                        add("合并");
                        add("不合并");
                    }};
                    if (!isMerge.contains(rptParamMap.get(key).split("&&")[7])) {
                        reportError("[" + exportReportInfoVOList.get(i).getRptNm() + "][11-明细特有属性][" + key + "]:是否合并配置错误，请配置：合并，不合并");
                    }
                    //是否合并参照列
                    List<String> isMergeCol = new ArrayList<String>() {{
                        add("--");
                        add("合并参照列");
                        add("非合并参照列");
                    }};
                    if (!isMergeCol.contains(rptParamMap.get(key).split("&&")[8])) {
                        reportError("[" + exportReportInfoVOList.get(i).getRptNm() + "][11-明细特有属性][" + key + "]:是否合并参照列配置错误，请配置：合并参照列，非合并参照列");
                    }
                } else {
                    reportError("[" + exportReportInfoVOList.get(i).getRptNm() + "][11-明细特有属性][" + key + "]:明细特有属性配置格式错误");
                }
            } else {
                reportError("[" + exportReportInfoVOList.get(i).getRptNm() + "][11-明细特有属性][" + key + "]:明细特有属性不能为空");
            }
        }
    }

    /**
     * 10-Excel公式属性校验
     *
     * @param i          报表序号
     * @param excelCells Excel单元格位置
     **/
    @SuppressWarnings("serial")
	private void verifyExcelFormula(int i, List<String> excelCells) {
        Map<String, String> rptParamMap = rptList.get(i).get("excelFormula");
        for (String key : excelCells) {
            if (StringUtils.isNotEmpty(rptParamMap.get(key))) {
                List<String> isRptIdx = new ArrayList<String>() {{
                    add("报表指标");
                    add("不是报表指标");
                }};
                if (rptParamMap.get(key).split("&&").length == 3) {
                    if (!isRptIdx.contains(rptParamMap.get(key).split("&&")[0]))
                    	 reportError("[" + exportReportInfoVOList.get(i).getRptNm() + "][10-Excel公式属性][" + key + "]:是否报表指标配置错误，请配置：报表指标，不是报表指标");
                    if ("扩展".equals(rptParamMap.get(key).split("&&")[1])) {
                        if (!ExtendType.get(rptParamMap.get(key).split("&&")[2]).valid())
                            reportError("[" + exportReportInfoVOList.get(i).getRptNm() + "][10-Excel公式属性][" + key + "]:扩展方式配置错误，请配置：范围扩展(纵),自增扩展(纵),范围扩展(横),自增扩展(横)");
                    } else if ("不扩展".equals(rptParamMap.get(key).split("&&")[1])) {
                        if (!"--".equals(rptParamMap.get(key).split("&&")[2]))
                            reportError("[" + exportReportInfoVOList.get(i).getRptNm() + "][10-Excel公式属性][" + key + "]:扩展方式配置错误，请配置：--");
                    } else {
                        reportError("[" + exportReportInfoVOList.get(i).getRptNm() + "][10-Excel公式属性][" + key + "]:是否扩展配置错误，请配置：扩展，不扩展");
                    }
                } else {
                    reportError("[" + exportReportInfoVOList.get(i).getRptNm() + "][10-Excel公式属性][" + key + "]:Excel公式属性配置格式错误");
                }
            } else {
                reportError("[" + exportReportInfoVOList.get(i).getRptNm() + "][10-Excel公式属性][" + key + "]:Excel公式属性不能为空");
            }
        }
    }

    /**
     * 数据显示格式||单位||精度校验
     *
     * @param i            报表序号
     * @param excelCells   Excel单元格位置
     * @param modelCells   模型单元格位置
     * @param idxCells     指标单元格位置
     * @param formulaCells 表间取数单元格位置
     **/
    private void verifyCellDisplayUnit(int i, List<String> modelCells, List<String> idxCells, List<String> formulaCells, List<String> excelCells) {
        Map<String, String> rptParamMap = rptList.get(i).get("cellDisplayUnit");
        List<String> allIdx = new ArrayList<>();
        allIdx.addAll(modelCells);
        allIdx.addAll(idxCells);
        allIdx.addAll(formulaCells);
        allIdx.addAll(excelCells);
        for (String key : allIdx) {
            if (StringUtils.isNotEmpty(rptParamMap.get(key))) {
                if (rptParamMap.get(key).split("&&").length == 3) {
                    if (!DisplayFormat.get(rptParamMap.get(key).split("&&")[0]).valid())
                        reportError("[" + exportReportInfoVOList.get(i).getRptNm() + "][9-数据显示格式||单位||精度][" + key + "]:数据显示格式配置错误，请配置：金额，百分比，数值，文本");
                    if (!DataUnit.get(rptParamMap.get(key).split("&&")[1]).valid())
                        reportError("[" + exportReportInfoVOList.get(i).getRptNm() + "][9-数据显示格式||单位||精度][" + key + "]:数据单位配置错误，请配置：无单位，模版单位，元，百，千，万，亿");
                    if (StringUtils.isBlank(rptParamMap.get(key).split("&&")[2])) {
                    		reportError("[" + exportReportInfoVOList.get(i).getRptNm() + "][9-数据显示格式||单位||精度][" + key + "]:精度配置不能为空");
                    }
                    
                } else {
                    reportError("[" + exportReportInfoVOList.get(i).getRptNm() + "][9-数据显示格式||单位||精度][" + key + "]:单元格数据显示格式||单位||精度配置格式错误");
                }
            } else {
                reportError("[" + exportReportInfoVOList.get(i).getRptNm() + "][9-数据显示格式||单位||精度][" + key + "]:单元格数据显示格式||单位||精度不能为空");
            }
        }
    }

    /**
     * 单元格是否汇总||可修改校验
     *
     * @param i            报表序号
     * @param formulaCells 表间取数单元格位置
     * @param idxCells     指标单元格位置
     * @param modelCells   模型单元格位置
     * @param excelCells   Excel单元格位置
     **/
    @SuppressWarnings("serial")
	private void verifyIdxSumUpt(int i, List<String> modelCells, List<String> idxCells, List<String> formulaCells, List<String> excelCells) {
        Map<String, String> rptParamMap = rptList.get(i).get("idxSumUpt");
        List<String> allIdx = new ArrayList<>();
        allIdx.addAll(modelCells);
        allIdx.addAll(idxCells);
        allIdx.addAll(formulaCells);
        allIdx.addAll(excelCells);
        for (String key : allIdx) {
            if (StringUtils.isNotEmpty(rptParamMap.get(key))) {
                List<String> isSum = new ArrayList<String>() {{
                    add("--");
                    add("可跑数汇总");
                    add("不可跑数汇总");
                }};
                List<String> isFillSum = new ArrayList<String>() {{
                    add("--");
                    add("可填报汇总");
                    add("不可填报汇总");
                }};
                List<String> isUpt = new ArrayList<String>() {{
                    add("--");
                    add("可修改");
                    add("不可修改");
                }};
                if (rptParamMap.get(key).split("&&").length == 3) {
                    if (!isSum.contains(rptParamMap.get(key).split("&&")[0]))
                        reportError("[" + exportReportInfoVOList.get(i).getRptNm() + "][8-单元格是否跑数汇总||是否填报汇总||可修改][" + key + "]:单元格是否跑数汇总配置错误，请配置：--，可跑数汇总，不可跑数汇总");
                    if (!isFillSum.contains(rptParamMap.get(key).split("&&")[1]))
                        reportError("[" + exportReportInfoVOList.get(i).getRptNm() + "][8-单元格是否跑数汇总||是否填报汇总||可修改][" + key + "]:单元格是否填报汇总配置错误，请配置：--，可填报汇总，不可填报汇总");
                    if (!isUpt.contains(rptParamMap.get(key).split("&&")[2]))
                        reportError("[" + exportReportInfoVOList.get(i).getRptNm() + "][8-单元格是否跑数汇总||是否填报汇总||可修改][" + key + "]:单元格是否修改配置错误，请配置：--，可修改，不可修改");
                } else {
                    reportError("[" + exportReportInfoVOList.get(i).getRptNm() + "][8-单元格是否跑数汇总||是否填报汇总||可修改][" + key + "]:单元格是否跑数汇总||是否填报汇总||可修改配置格式错误");
                }
            } else {
                reportError("[" + exportReportInfoVOList.get(i).getRptNm() + "][8-单元格是否跑数汇总||是否填报汇总||可修改][" + key + "]:单元格是否跑数汇总||是否填报汇总||可修改不能为空");
            }
        }
    }

    /**
     * 报表来源数据校验
     *
     * @param i            报表序号
     * @param connection   数据库连接
     * @param formulaCells 表间取数单元格位置
     * @param idxCells     指标单元格位置
     * @param modelCells   模型单元格位置
     * @param excelCells 
     * @param excelCells   Excel单元格位置
     **/
    @SuppressWarnings("resource")
	private void verifyRptSource(int i, Connection connection, List<String> modelCells, List<String> idxCells, List<String> formulaCells, List<String> staticeCells, List<String> excelCells) throws SQLException {
        Map<String, String> rptParamMap = rptList.get(i).get("rptSource");
        List<String> allIdx = new ArrayList<>();
        allIdx.addAll(modelCells);
        allIdx.addAll(idxCells);
        allIdx.addAll(formulaCells);
        allIdx.addAll(staticeCells);
        allIdx.addAll(excelCells);
        for (String key : allIdx) {
            if (StringUtils.isEmpty(rptParamMap.get(key)))
                reportError("[" + exportReportInfoVOList.get(i).getRptNm() + "][6-来源数据][" + key + "]:来源数据不能为空");
        }
        //来源数据是否存在校验
        Statement statement = connection.createStatement();
        //防止由于执行两次executeQuery而关闭ResultSet问题导致报错
        Statement statement1 = connection.createStatement();
        ResultSet resultSet = null;
        ResultSet resultSet1 = null;
        try {
            //模型
            List<String> execModelNm = new ArrayList<>();
            for (String modelKey : modelCells) {
                if (StringUtils.isNotEmpty(rptParamMap.get(modelKey))) {
                    String[] sourceString = rptParamMap.get(modelKey).split("\\.");
                    if (sourceString.length == 2) {
                        String modelNm = rptParamMap.get(modelKey).split("\\.")[0];
                        String colNm = rptParamMap.get(modelKey).split("\\.")[1];
                        if (!execModelNm.contains(modelNm + colNm)) {
                            String sql = "select count(1) as count from rpt_sys_module_info where set_nm = '" + modelNm + "'";
                            logger.info("执行的sql:" + sql);
                            resultSet = statement.executeQuery(sql);
                            logger.info("resultSet:" + resultSet);
                            while (resultSet.next()) {
                                int count = resultSet.getInt("count");
                                if (count == 0) {
                                    reportError("[" + exportReportInfoVOList.get(i).getRptNm() + "][6-来源数据][" + modelKey + "]:" + modelNm + "模型不存在");
                                } else {
                                    String colSql = "select count(1) as count from rpt_sys_module_col where cn_nm = '" + colNm + "' and set_id = (select set_id from rpt_sys_module_info where set_nm = '" + modelNm + "')";
                                    logger.info("执行的sql:" + colSql);
                                    resultSet1 = statement1.executeQuery(colSql);
                                    while (resultSet1.next()) {
                                        int count1 = resultSet1.getInt("count");
                                        if (count1 == 0) {
                                            reportError("[" + exportReportInfoVOList.get(i).getRptNm() + "][6-来源数据][" + modelKey + "]:" + modelNm + "模型字段" + colNm + "不存在");
                                        }
                                    }
                                }
                            }
                            execModelNm.add(modelNm + colNm);
                        }
                    } else {
                        reportError("[" + exportReportInfoVOList.get(i).getRptNm() + "][6-来源数据][" + modelKey + "]:格式不正确");
                    }
                }
            }
            //指标
            List<String> execIndexNm = new ArrayList<>();
            for (String idxKey : idxCells) {
                if (StringUtils.isNotEmpty(rptParamMap.get(idxKey)) && !rptParamMap.get(idxKey).equals("空指标")) {
                    String indexNm = rptParamMap.get(idxKey);
                    if (!execIndexNm.contains(indexNm)) {
                        // 总账指标处理
                        if(indexNm.contains(".")){
                            indexNm = indexNm.split("\\.")[0];
                        }
                        String sql = "select count(1) as count from rpt_idx_info where index_nm = '" + indexNm + "'";
                        logger.info("执行的sql:" + sql);
                        resultSet = statement.executeQuery(sql);
                        while (resultSet.next()) {
                            int count = resultSet.getInt("count");
                            if (count == 0) {
                                reportError("[" + exportReportInfoVOList.get(i).getRptNm() + "][6-来源数据][" + idxKey + "]:" + indexNm + "指标不存在");
                            }/* else if (count > 1) {
                                reportError("[" + exportReportInfoVOList.get(i).getRptNm() + "][6-来源数据][" + idxKey + "]:" + indexNm + "指标存在多个");
                            }*/
                        }
                        execIndexNm.add(indexNm);
                    }
                }
            }
            //表间计算
            List<String> execRptNm = new ArrayList<>();
            for (String formulaKey : formulaCells) {
                if (StringUtils.isNotEmpty(rptParamMap.get(formulaKey))) {
                    String regex = "I\\('([A-Z]|[a-z]|[0-9]|[^\\x00-\\xff]|[_]|[ ]|\\.|[(]|[)]|[:]|[-])+'\\)";
                    Pattern p = Pattern.compile(regex);
                    Matcher matcher = p.matcher(rptParamMap.get(formulaKey));
                    boolean matcherFlag = true;
                    while (matcher.find()) {
                        matcherFlag = false;
                        String regex1 = "(?<=I\\(')(\\S+)(?='\\))";
                        Pattern pattern1 = Pattern.compile(regex1);
                        Matcher matcher1 = pattern1.matcher(matcher.group());
                        while (matcher1.find()) {
                        	//单元格名称可能会有.存在，这个判断不合理
                            if (matcher1.group().split("\\.").length >= 2) {
                                //表间计算报表校验
                                String rptNm = matcher1.group().split("\\.")[0];
                                if (!execRptNm.contains(rptNm) && StringUtils.isNotEmpty(rptNm)) {
                                    String sql = "select count(1) as count from rpt_mgr_report_info where rpt_nm = '" + rptNm + "'";
                                    logger.info("执行的sql:" + sql);
                                    resultSet = statement.executeQuery(sql);
                                    while (resultSet.next()) {
                                        int count = resultSet.getInt("count");
                                        if (count == 0) {
                                            boolean flag = true;
                                            for (ExportReportInfoVO exportReportInfoVO : exportReportInfoVOList) {
                                                if (StringUtils.isNotEmpty(rptNm) && rptNm.equals(exportReportInfoVO.getRptNm())) {
                                                    flag = false;
                                                }
                                            }
                                            if (flag) {
                                                reportError("[" + exportReportInfoVOList.get(i).getRptNm() + "][6-来源数据][" + formulaKey + "]:" + rptNm + "报表不存在");
                                            }
                                        }/* else if (count > 1) {
                                            reportError("[" + exportReportInfoVOList.get(i).getRptNm() + "][6-来源数据][" + formulaKey + "]:" + rptNm + "报表有多张");
                                        }*/
                                    }
                                    execRptNm.add(rptNm);
                                }
                              //表间计算指标校验,替换了"报表名称."，剩余全部是指标名称 
                                String indexNm = matcher1.group().replace(rptNm + ".", "");
                                if (StringUtils.isNotEmpty(indexNm)) {
                                    String sql = "select count(1) as count from rpt_idx_info where index_nm = '" + indexNm + "' and template_id in (select cfg_id from rpt_mgr_report_info where rpt_nm = '" + rptNm + "')";
                                    logger.info("执行的sql:" + sql);
                                    resultSet = statement.executeQuery(sql);
                                    while (resultSet.next()) {
                                        int count = resultSet.getInt("count");
                                        if (count == 0) {
                                            boolean flag = true;
                                            for (int rptCount = 0; rptCount < exportReportInfoVOList.size(); rptCount++) {
                                                if (StringUtils.isNotEmpty(rptNm) && rptNm.equals(exportReportInfoVOList.get(rptCount).getRptNm())) {
                                                    Map<String, String> rptCellNm = rptList.get(rptCount).get("rptCellNm");
                                                    for (String cellNmKey : rptCellNm.keySet()) {
                                                        if (indexNm.equals(rptCellNm.get(cellNmKey))) {
                                                            flag = false;
                                                        }
                                                    }
                                                }
                                            }
                                            if (flag) {
                                                reportError("[" + exportReportInfoVOList.get(i).getRptNm() + "][6-来源数据][" + formulaKey + "]:" + rptNm + "报表中" + indexNm + "不存在");
                                            }
                                        }/* else if (count > 1) {
                                            reportError("[" + exportReportInfoVOList.get(i).getRptNm() + "][6-来源数据][" + formulaKey + "]:" + rptNm + "报表中" + indexNm + "有多个");
                                        }*/
                                    }
                                }
                            } else {
                                reportError("[" + exportReportInfoVOList.get(i).getRptNm() + "][6-来源数据][" + formulaKey + "]:表间计算格式错误");
                            }
                        }
                    }
                    if (matcherFlag) {
                        reportError("[" + exportReportInfoVOList.get(i).getRptNm() + "][6-来源数据][" + formulaKey + "]:表间计算格式错误");
                    }
                }
            }
            //表达式
            for (String staticeKey : staticeCells) {
                if (StringUtils.isNotEmpty(rptParamMap.get(staticeKey))) {
                    String regex = "(?<=#)(\\S+)(?=#)";
                    Pattern p = Pattern.compile(regex);
                    Matcher matcher = p.matcher(rptParamMap.get(staticeKey));
                    while (matcher.find()) {
                        String sql = "select count(1) as count from rpt_sys_var_info where var_no = '" + matcher.group() + "'";
                        logger.info("执行的sql:" + sql);
                        resultSet = statement.executeQuery(sql);
                        while (resultSet.next()) {
                            int count = resultSet.getInt("count");
                            if (count == 0) {
                                reportError("[" + exportReportInfoVOList.get(i).getRptNm() + "][6-来源数据][" + staticeKey + "]:" + matcher.group() + "表达式不存在");
                            }
                        }
                    }
                }
            }
            //excel公式
            for (String excelKey : excelCells) {
            	String excelValue = rptParamMap.get(excelKey);
            	if (StringUtils.isNotEmpty(excelValue)) {
                    String flag = excelValue.substring(0,1);
                    if(!"=".equals(flag)){//判断是否以“=”开头
                    	reportError("[" + exportReportInfoVOList.get(i).getRptNm() + "][6-来源数据][" + excelKey + "]:不是excel公式");
                    }
                }
            }
        } finally {
            if (resultSet != null) {
                resultSet.close();
            }
            if (resultSet1 != null) {
                resultSet1.close();
            }
            if (statement != null) {
                statement.close();
            }
            if (statement1 != null) {
                statement1.close();
            }
        }
    }

    /**
     * 报表指标sheet页校验相关
     *
     * @param excelCells   excel单元格坐标
     * @param formulaCells 表间取数单元格坐标
     * @param idxCells     指标单元格坐标
     * @param modelCells   模型单元格坐标
     * @param staticeCells 表达式单元格
     * @param i            报表序号
     * @param connection   数据库连接
     * @param rptIndexNos
     */
    private void verifyRptIdx(int i, Connection connection, List<String> idxCells, List<String> formulaCells, List<String> excelCells, List<String> staticeCells, List<String> rptIndexNos) throws SQLException {
        Map<String, String> rptParamMap = rptList.get(i).get("rptIdx");
        List<String> allIdx = new ArrayList<>();
        allIdx.addAll(idxCells);
        allIdx.addAll(formulaCells);
    	allIdx.addAll(excelCells);
        //报表指标编号校验
        for (String key : allIdx) {
            if (StringUtils.isEmpty(rptParamMap.get(key))) {
            	if(rptParamMap.get(key).split("&&")[0] != null){
            		if("是报表指标".equals(rptParamMap.get(key).split("&&")[0])){
            			reportError("[" + exportReportInfoVOList.get(i).getRptNm() + "][3-报表指标][" + key + "]:报表指标编号不能为空");
            		}
            	}
            } else {
                if (rptIndexNos.contains(rptParamMap.get(key))) {
                    reportError("[" + exportReportInfoVOList.get(i).getRptNm() + "][3-报表指标][" + key + "]:报表指标编号重复");
                } else {
                	if(rptParamMap.get(key).contains(".")){
                		reportError("[" + exportReportInfoVOList.get(i).getRptNm() + "][3-报表指标][" + key + "]:报表指标编号不能包含.");
                	}else{
                		rptIndexNos.add(rptParamMap.get(key));
                	}
                }
            }
        }
        for (String key : staticeCells) {
            if (StringUtils.isEmpty(rptParamMap.get(key)))
                reportError("[" + exportReportInfoVOList.get(i).getRptNm() + "][3-报表指标][" + key + "]:表达式不能为空");
        }
        //数据库校验
        verifyRptIdxNo(connection, rptParamMap, exportReportInfoVOList.get(i).getRptNm(), exportReportInfoVOList.get(i).getRptNum(), allIdx);
    }

    /**
     * 校验报表指标编号是否重复
     *
     * @param connection  数据库连接
     * @param rptNm       报表名称
     * @param rptParamMap Excel数据
     * @param allIdx      指标编号集合
     * @param rptNum      报表编号
     **/
    private void verifyRptIdxNo(Connection connection, Map<String, String> rptParamMap, String rptNm, String rptNum, List<String> allIdx) throws SQLException {

        List<String> sqlParams;
        int batchCount = 1000;
        //报表指标编号校验
        for (int rowCount = 0; rowCount < allIdx.size(); rowCount = rowCount + batchCount) {
            if (rowCount + batchCount > allIdx.size()) {
                sqlParams = allIdx.subList(rowCount, allIdx.size());
            } else {
                sqlParams = allIdx.subList(rowCount, rowCount + batchCount);
            }
            execSql(sqlParams, rptNum, rptNm, connection, rptParamMap);
        }

    }

    /**
     * 校验报表指标编号是否重复sql执行处理
     *
     * @param rptNum      报表编号
     * @param rptParamMap Excel数据
     * @param rptNm       报表名称
     * @param connection  数据库连接
     * @param sqlParams   查询条件
     **/
    private void execSql(List<String> sqlParams, String rptNum, String rptNm, Connection connection, Map<String, String> rptParamMap) throws SQLException {
        Statement statement = connection.createStatement();
        ResultSet resultSet = null;
        try {
            StringBuilder sql = new StringBuilder();
            sql.append("select index_no from rpt_idx_info where index_no in (");
            for (String key : sqlParams) {
                sql.append("'").append(rptParamMap.get(key)).append("',");
            }
            sql.deleteCharAt(sql.length() - 1).append(")").append(" and template_id not in (select cfg_id from rpt_mgr_report_info where rpt_num = '").append(rptNum).append("')");
            logger.info("执行的sql:" + sql);
            resultSet = statement.executeQuery(sql.toString());
            while (resultSet.next()) {
                String indexNo = resultSet.getString("index_no");
                reportError("[" + rptNm + "][3-报表指标][" + indexNo + "]:报表指标编号重复");
            }
        } finally {
            if (resultSet != null) {
                resultSet.close();
            }
            if (statement != null) {
                statement.close();
            }
        }
    }

    /**
     * 基础数据校验方法
     *
     * @param conn               连接
     * @param exportReportInfoVO 报表基础信息
     **/
    @SuppressWarnings("resource")
	private void verifyBaseData(Connection conn, ExportReportInfoVO exportReportInfoVO) throws SQLException {
        Statement statement = conn.createStatement();
        ResultSet resultSet = null;
        try {
            if (StringUtils.isNotEmpty(exportReportInfoVO.getBusiType()) && exportReportInfoVO.getVerId() != null){
                resultSet = statement.executeQuery("select count(1) as count from frs_system_cfg t left join bione_param_info p on t.busi_type = p.param_value and p.param_type_no = 'idxTaskType' where p.param_name = '" + exportReportInfoVO.getBusiType() + "' and t.system_ver_id = '" + exportReportInfoVO.getVerId() + "'");
                while (resultSet.next()) {
                    int count = resultSet.getInt("count");
                    if (count == 0) {
                        reportError("[" + exportReportInfoVO.getRptNm() + "]:版本编号在系统中不存在");
                    }
                }
                //选择沿用指标配置新增报表模板是否存在校验
                if (fromSystemRptCfg){
                    resultSet = statement.executeQuery("select count(1) as count from rpt_design_tmp_info where template_id = (select cfg_id from rpt_mgr_report_info where rpt_num = '"+ exportReportInfoVO.getRptNum() +"') and ver_id = '" + exportReportInfoVO.getVerId() + "'");
                    while (resultSet.next()) {
                        int count = resultSet.getInt("count");
                        if (count == 0) {
                            reportError("[" + exportReportInfoVO.getRptNm() + "]:沿用报表模板不存在");
                        }
                    }
                }
            }
            //联系人校验，校验是否是系统用户
            if(StringUtils.isNotBlank(exportReportInfoVO.getUserName())){
            	resultSet = statement.executeQuery("select count(1) as count from bione_user_info where user_name = '" + exportReportInfoVO.getUserName() + "'");
                while (resultSet.next()) {
                    int count = resultSet.getInt("count");
                    if (count == 0) {
                        reportError("[" + exportReportInfoVO.getUserName() + "]:联系人在系统中不存在");
                    }
                }
            }
            //业务分库校验
            if(StringUtils.isNotBlank(exportReportInfoVO.getBusiLibNm())){
            	resultSet = statement.executeQuery("select count(1) as count from rpt_mgr_busi_lib_info where busi_lib_nm = '" + exportReportInfoVO.getBusiLibNm() + "'");
                while (resultSet.next()) {
                    int count = resultSet.getInt("count");
                    if (count == 0) {
                        reportError("[" + exportReportInfoVO.getBusiLibNm() + "]:业务分库在系统中不存在");
                    }
                }
            }
            //业务类型校验
            if(StringUtils.isNotBlank(exportReportInfoVO.getBusiType())){
            	resultSet = statement.executeQuery("select count(1) as count from bione_param_info where param_type_no = 'idxTaskType' and param_name = '" + exportReportInfoVO.getBusiType() + "'");
                while (resultSet.next()) {
                    int count = resultSet.getInt("count");
                    if (count == 0) {
                        reportError("[" + exportReportInfoVO.getBusiType() + "]:业务类型配置错误,请检查【系统参数信息】功能中的【指标类报送任务类型】码值是否正确！");
                    }
                }
            }
        } finally {
            if (resultSet != null){
                resultSet.close();
            }
            if (statement != null){
                statement.close();
            }
        }
        //报表类型校验
        if (!TemplateType.get(exportReportInfoVO.getTemplateType()).valid())
            reportError("[" + exportReportInfoVO.getRptNm() + "]:模板类型配置错误，请配置：明细报表, 单元格报表, 综合报表");
        //报表周期校验
        if (!RptCycle.get(exportReportInfoVO.getRptCycle()).valid())
            reportError("[" + exportReportInfoVO.getRptNm() + "]:报表周期配置错误，请配置：日, 月, 季, 年, 周, 旬, 半年");
        //报表单位校验
        if (!TemplateUnit.get(exportReportInfoVO.getTemplateUnit()).valid()){
            reportError("[" + exportReportInfoVO.getRptNm() + "]:报表单位配置错误，请配置：元, 百, 千, 万, 亿");
        }
        //升级概况校验
        if (!TemplateNm.get(exportReportInfoVO.getTemplateNm()).valid())
            reportError("[" + exportReportInfoVO.getRptNm() + "]:升级概况配置错误，请配置：新增, 修改, 停用, 无变化");
        //是否修改校验
        if (!YesOrNo.get(exportReportInfoVO.getIsUpt()).valid())
            reportError("[" + exportReportInfoVO.getRptNm() + "]:是否修改配置错误，请配置：是，否");
        //报表状态校验
        if (!YesOrNo.get(exportReportInfoVO.getRptSts()).valid())
            reportError("[" + exportReportInfoVO.getRptNm() + "]:报表状态配置错误，请配置：启用，停用");
        //是否定长
        if(StringUtils.isNotBlank(exportReportInfoVO.getFixedLength())){
        	 if (!YesOrNo.get(exportReportInfoVO.getFixedLength()).valid())
                 reportError("[" + exportReportInfoVO.getRptNm() + "]:是否定长配置错误，请配置：是，否");
        }
        //是否分页
        if(StringUtils.isNotBlank(exportReportInfoVO.getIsPaging())){
	        if (!YesOrNo.get(exportReportInfoVO.getIsPaging()).valid()){
	            reportError("[" + exportReportInfoVO.getRptNm() + "]:是否分页配置错误，请配置：是，否");
	        } else {
	        	if(StringUtils.isBlank(exportReportInfoVO.getFixedLength())){
	        		reportError("[" + exportReportInfoVO.getRptNm() + "]:是否定长配置错误，请配置：是，否");
	        	}else{
	        		if("Y".equals(YesOrNo.get(exportReportInfoVO.getFixedLength()).toString()) && "Y".equals(YesOrNo.get(exportReportInfoVO.getIsPaging()).toString())){
		        		reportError("[" + exportReportInfoVO.getRptNm() + "]:是否分页配置错误，【是否定长】配置为是，【是否分页】只能配置为否");
		        	}
	        	}
	        }
        }
    }

    /**
     * 错误日志打印方法
     **/
    private void reportError(String message) {
        errorMessageBuilder.append("<li>").append(message).append("</li>");
        errorCount++;
    }

    /**
     * 关闭IO
     **/
    @Override
    public void close() {
        for (Map.Entry<String, ExcelEventReader> stringExcelEventReaderEntry : excelEventReaderMap.entrySet()) {
            IOUtils.closeQuietly(stringExcelEventReaderEntry.getValue());
        }
    }

    public int getErrorCount() {
        return errorCount;
    }

}
