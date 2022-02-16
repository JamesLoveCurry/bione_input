package com.yusys.bione.plugin.detail.download.service;

import com.alibaba.fastjson.JSONObject;
import com.mysql.jdbc.UpdatableResultSet;
import com.yusys.bione.comp.service.BaseBS;
import com.yusys.bione.comp.utils.DateUtils;
import com.yusys.bione.comp.utils.FilepathValidateUtils;
import com.yusys.bione.comp.utils.RandomUtils;
import com.yusys.bione.frame.security.BioneSecurityUtils;
import com.yusys.bione.plugin.base.common.GlobalConstants4plugin;
import com.yusys.bione.plugin.detail.download.util.ExcelUtil;
import com.yusys.bione.plugin.drivercfg.utils.DriverCfgUtils;
import com.yusys.bione.plugin.yuformat.service.DataSourceCache;
import com.yusys.bione.plugin.yuformat.utils.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.yarn.webapp.hamlet.Hamlet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.dom4j.Document;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * @项目名称：明细类模块导出公共方法类
 * @类名称： DetailDownloadBS
 * @类描述:
 * @功能描述:
 * @创建人: miaokx@yusys.com.cn
 * @创建时间: 2021年10月11日 17:01
 * @修改备注:
 * @修改记录: 修改时间  修改人员  修改原因
 * ---------------------------------------------------------
 * @Version 1.0.0
 * @Copyright (c) 宇信科技-版权所有
 */
public class DetailDownloadBS extends BaseBS<Object> {
    private YuFormatUtil bsUtil = new YuFormatUtil();
    private ExcelUtil excelUtil = new ExcelUtil();
    private Logger logger = LoggerFactory.getLogger(getClass());
    private static final String EXCEL_LAST_COLUMN_NAME = "错误规则描述";
    private static final String EXCEL_UPDATE_SHEET = "修改";
    private static final String EXCEL_INSERT_SHEET = "新增";

    /**
     * <pre>
     * Title: 用于前端获取，明细数据量CSV
     * </pre>
     *
     * @author miaokx
     * @version 1.00.00
     * @date 9:44 2020/11/16
     */
    public JSONObject getMaxDataNumCsv(JSONObject _par) throws Exception {
        String type = _par.getString("type");
        String properties = GlobalConstants4plugin.DetailProperties.getValue(type);
        JSONObject jsonObject = getMaxDataNum(properties, "data.download.csv");
        return jsonObject;
    }
    /**
     * <pre>
     * Title: 用于前端获取，明细数据量Excel
     * </pre>
     *
     * @author miaokx
     * @version 1.00.00
     * @date 9:44 2020/11/16
     */
    public JSONObject getMaxDataNumExcel(JSONObject _par) throws Exception {
        String type = _par.getString("type");
        String properties = GlobalConstants4plugin.DetailProperties.getValue(type);
        JSONObject jsonObject = getMaxDataNum(properties, "data.download.excel");
        return jsonObject;
    }
    /**
     * <pre>
     * Title: 用于前端获取，明细数据量
     * </pre>
     *
     * @author miaokx
     * @version 1.00.00
     * @date 9:44 2020/11/16
     */
    public JSONObject getMaxDataNum(String propertiesPath, String propertyKey) throws Exception {
        Properties configProp = new Properties();
        InputStream inputStream = null;
        try {
            inputStream = this.getClass().getClassLoader().getResourceAsStream(propertiesPath);
            configProp.load(inputStream);
            String downloadNum = configProp.getProperty(propertyKey);
            JSONObject jso_rt = new JSONObject();
            jso_rt.put("downloadNum", downloadNum);
            return jso_rt;
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
        }
    }


    /***
     * @方法描述: 导出excel入口
     * @创建人: miaokx@yusys.com.cn
     * @创建时间: 2021/8/29 11:32
     * @Param: par
     * @return: com.alibaba.fastjson.JSONObject
     */
    public JSONObject exportData(JSONObject par) {
        try {
            if ("EXCEL".equalsIgnoreCase(par.getString("fileType"))){
                return exportDataExcel(par);
            } else {
                return exportDataCSV(par);
            }

        } catch (Exception e) {
            e.printStackTrace();
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("code", "error");
            jsonObject.put("msg", "生成文件过程中发生异常！异常信息为:" + e.getMessage());
            return jsonObject;
        }

    }
    /***
     * @方法描述: 方法提出一个抛异常的供多线程调用
     * @创建人: miaokx@yusys.com.cn
     * @创建时间: 2021/10/20 19:49
     * @Param: par
     * @return: com.alibaba.fastjson.JSONObject
     */
    public JSONObject exportDataExcel(JSONObject par) throws Exception{
        // 填报机构
        String issuedNo = par.getString("orgNo");
        String tabName = par.getString("tabName");
        String dataDt = par.getString("dataDt");
        String detailType = par.getString("detailType");
        Map<String, String> params = null;
        params = getParams(issuedNo, dataDt.replace("-", ""), detailType);
        String dirPath = params.get("dirPath");
        String zipPath = params.get("zipPath");
        par.put("dirPath", dirPath);
        par.put("zipPath", zipPath);
        // 1为全量数据界面导出，2为错误数据界面 3为已修改数据界面
        String type = par.getString("type");
        // 检核结果查询导出
        String queryType = par.getString("queryType");
        String colSql = "select * from " + GlobalConstants4plugin.DetailFieldTab.getValue(detailType) + " where tab_name='" + tabName + "' and is_fill_export='Y' order by col_no";
        HashVO[] colList = bsUtil.getHashVOs(colSql);
        int a = 0;
        String cols = "";
        List<Integer> noChangeList = new ArrayList<>();
        for (HashVO col : colList) {
            cols += col.getStringValue("col_name_en") + ",";
            if (!"Y".equalsIgnoreCase(col.getStringValue("is_export"))) {
                noChangeList.add(a);
            }
            a++;
        }
        String webDataSql = par.getString("dataSql");
        String[] dataSqls = webDataSql.split(" from ", 2);
        cols = cols.substring(0, cols.length() - 1);
        String dataSql = "";
        if ("1".equalsIgnoreCase(type) || "3".equalsIgnoreCase(type)) {
            dataSql = " select " + cols + " from " + dataSqls[1];
        } else if ("2".equalsIgnoreCase(type)) {
            dataSql = " select " + cols + ",ruleids from " + dataSqls[1];
        }
        if (!dataSql.contains("where")) {
            dataSql += " where 1=1 ";
        }
        if("1".equals(queryType)){
            dataSql = dataSql+" and org_no='"+issuedNo+"'";
        }else if("2".equals(queryType)){
            dataSql = dataSql+" and address = '"+issuedNo+"'";
        }else if("3".equals(queryType)){
            dataSql = dataSql+" and issued_no = '"+issuedNo+"'";
        }
        Integer download = Integer.parseInt(params.get("download"));
        par.put("download", download);
        par.put("dataSql", dataSql);
        ArrayList<HashVO> itemVOList = bsUtil.getTempVoList(colList);
        return getBusinessDataByExcle(par, itemVOList, noChangeList);
    }
    /**
     * @方法描述: 下载excel
     * @创建人: miaokx@yusys.com.cn
     * @创建时间: 2021/10/12 17:43
     * @Param: par 所需的一些业务参数，如日期 机构等
     * @Param: itemVOList 模板列表，用于生成excel表头等信息
     * @Param: noChangeList 不保存字段，某些项目组需要，导入一些字段默认不更新
     * @return: com.alibaba.fastjson.JSONObject
     */
    public JSONObject getBusinessDataByExcle(JSONObject par, ArrayList<HashVO> itemVOList, List<Integer> noChangeList) throws Exception{
        JSONObject jsonObject = new JSONObject();
        Connection conn = null;
        Statement stmtQuery = null;
        ResultSet rsQuery = null;
        FileOutputStream outStreasm = null;
        try {

            int a = 0;
            String dsName = par.getString("dsName");
            String dataSql = par.getString("dataSql");
            String type = par.getString("type");
            Integer dataCount = par.getInteger("dataCount");
            String tabName = par.getString("tabName");
            String dataDt = par.getString("dataDt");
            String dirPath = par.getString("dirPath");
            String zipPath = par.getString("zipPath");
            String detailType = par.getString("detailType");
            // 获取的所有数据
            conn = bsUtil.createDatabaseConnectionByDS(dsName);
            stmtQuery = conn.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
            // 设置fetchsize  可根据实际情况调整大小。
            String dsType = bsUtil.getDsTypeByDsName(dsName);
            if (dsType.equalsIgnoreCase("oracle") || dsType.equalsIgnoreCase("oracle-rac")) {
                stmtQuery.setFetchSize(GlobalConstants4plugin.FETCH_SIZE);
            } else {
                stmtQuery.setFetchSize(Integer.MIN_VALUE);
            }
            rsQuery = stmtQuery.executeQuery(dataSql); // 查询数据

            HashVO[] itemVOs = itemVOList.toArray(new HashVO[0]);
            ArrayList<String> showItemKeys = new ArrayList<String>();
            ArrayList<String> showItemNames = new ArrayList<String>();
            ArrayList<Integer> showItemWidths = new ArrayList<Integer>();
            for (int i = 0; i < itemVOs.length; i++) {
                showItemKeys.add(itemVOs[i].getStringValue("itemkey"));
                showItemNames.add(itemVOs[i].getStringValue("itemname"));
                showItemWidths.add(itemVOs[i].getIntegerValue("list_width", 120));
            }
            // 用于存放规则id和对应字段
            Map<String, String> ruleCol = new HashMap<>();
            // 用于存放规则id和规则名称
            Map<String, String> ruleName = new HashMap<>();
            if ("2".equalsIgnoreCase(type)) {
                showItemNames.add(EXCEL_LAST_COLUMN_NAME);
                showItemWidths.add(300);// 最后一列长度
                // 获取所有规则id和规则描述和对应的字段
                String ruleSql = "select id,col_name,rule_name from " + GlobalConstants4plugin.DetailRuleTab.getValue(detailType) + " where tab_name='" + tabName + "'";
                HashVO[] ruleList = bsUtil.getHashVOs(ruleSql);
                for (HashVO hashVO : ruleList) {
                    ruleCol.put(hashVO.getStringValue("id"), hashVO.getStringValue("col_name"));
                    ruleName.put(hashVO.getStringValue("id"), hashVO.getStringValue("rule_name"));
                }
            }
            String[] str_keys = showItemKeys.toArray(new String[0]);
            String[] str_names = showItemNames.toArray(new String[0]);
            Integer[] li_widths = showItemWidths.toArray(new Integer[0]); //
            List<String[]> str_data = new ArrayList<>();
            // 用于存放字段在excel中的列数
            Map<String, Integer> colAt = new HashMap<>();
            //设置表头
            for (int i = 0; i < str_names.length; i++) {
                colAt.put(str_names[i], i);
            }

            str_data.add(str_names);
            // 用于存放行数和列数的错误，一行可能有多列，多列用，隔开
            Map<String, String> map = new HashMap<>();
            //设置内容
            int i = 0;
            int f = 1;
            // 用于存放错误行数
            int p = 1;
            // 获取一次下载条数
            Integer download = par.getInteger("download");
            int[] li_widths2 = new int[li_widths.length];
            for (int j = 0; j < li_widths2.length; j++) {
                li_widths2[j] = li_widths[j].intValue(); //
            }
            while (rsQuery.next()) {
                String[] data  = null;
                if ("1".equalsIgnoreCase(type)|| "3".equalsIgnoreCase(type)) {
                    data = new String[str_keys.length];
                } else if ("2".equalsIgnoreCase(type)) {
                    data = new String[str_keys.length + 1];
                    String ruleIdStr = rsQuery.getString("ruleids");
                    if (StringUtils.isNotEmpty(ruleIdStr)) {
                        String[] ruleIds = ruleIdStr.split(",");
                        String ruleNames = "";
                        String errorNum = "";
                        for (String ruleId : ruleIds) {
                            // 根据ruleId 获取规则描述
                            ruleNames += ruleName.get(ruleId) + ";\r\n";
                            // 根据ruleId 获取字段名，在获取字段所在位置
                            errorNum += colAt.get(ruleCol.get(ruleId)) + ",";
                        }
                        // 如果有错误描述，则在最后列打印excel
                        if (StringUtils.isNotEmpty(ruleNames)) {
                            data[str_keys.length] = ruleNames;
                            errorNum.substring(0, errorNum.length() - 1);
                            map.put(p + "", errorNum);
                        }
                    }
                }
                p++;
                for (int j = 0; j < str_keys.length; j++) {
                    Object o = rsQuery.getString(str_keys[j].toUpperCase());
                    if (o!=null) {
                        if (o.toString().startsWith(".")) {
                            data[j] = "0" + o.toString();
                        } else if (o.toString().startsWith("-.")) {
                            data[j] = "-0" + o.toString().substring(1, o.toString().length());
                        } else {
                            data[j] = rsQuery.getString(str_keys[j].toUpperCase()).toString();
                        }
                    } else {
                        data[j] = "";
                    }
                }
                str_data.add(data);
                i++;
                if (download !=dataCount && i % download == 0) {
                    System.out.println("--------------每隔" + download + "进行一存储：第" + f + "次--------------");
                    String fileName = tabName + "_" + dataDt.replace("-", "") + "_" + f + ".xlsx";
                    fileName = new String(fileName.getBytes("UTF-8"), "UTF-8");
                    File downFile = new File(dirPath + fileName);
                    outStreasm = new FileOutputStream(downFile);
                    String[][] newStrData = new String[str_data.size()][itemVOList.size() + 1];
                    str_data.toArray(newStrData);
                    SXSSFWorkbook book = null;
                    if ("3".equalsIgnoreCase(type)) {
                        book = excelUtil.exportExcelByStrArrayAsBook1(newStrData, li_widths2, map, "2"); //
                    } else {
                        book = excelUtil.exportExcelByStrArrayAsBook(newStrData, li_widths2, EXCEL_UPDATE_SHEET, map, noChangeList); //
                    }

                    book.write(outStreasm); //写流!
                    book.close();
                    str_data.clear();
                    str_data.add(str_names);
                    map.clear();
                    p = 1;
                    f++;
                } else if (i == dataCount) {
                    // 说明数据量小与5w
                    if (f == 1) {
                        String fileName = tabName + "_" + dataDt.replace("-", "") + ".xlsx";
                        FileOutputStream _servertOut =  new FileOutputStream(dirPath + fileName);
                        String[][] newStrData = new String[str_data.size()][itemVOList.size() + 1];
                        str_data.toArray(newStrData);
                        SXSSFWorkbook book = null;
                        if ("3".equalsIgnoreCase(type)) {
                            book = excelUtil.exportExcelByStrArrayAsBook1(newStrData, li_widths2, map, "2"); //
                        } else {
                            book = excelUtil.exportExcelByStrArrayAsBook(newStrData, li_widths2, EXCEL_UPDATE_SHEET, map, noChangeList); //
                        }
                        book.write(_servertOut); //写流!
                        book.close();
                        jsonObject.put("code", "success");
                        jsonObject.put("data", dirPath + fileName);
                        return jsonObject;
                    } else {
                        System.out.println("--------------每隔" + download + "进行一存储：第" + f + "次--------------");
                        String fileName = tabName + "_"+ dataDt.replace("-", "") + "_" + f + ".xlsx";
                        fileName = new String(fileName.getBytes("UTF-8"), "UTF-8");
                        File downFile = new File(dirPath + fileName);
                        outStreasm = new FileOutputStream(downFile);
                        String[][] newStrData = new String[str_data.size()][itemVOList.size() + 1];
                        str_data.toArray(newStrData);
                        SXSSFWorkbook book = null;
                        if ("3".equalsIgnoreCase(type)) {
                            book = excelUtil.exportExcelByStrArrayAsBook1(newStrData, li_widths2, map, "2"); //
                        } else {
                            book = excelUtil.exportExcelByStrArrayAsBook(newStrData, li_widths2, EXCEL_UPDATE_SHEET, map, noChangeList); //
                        }
                        book.write(outStreasm); //写流!
                        book.close();
                        str_data.clear();
                        str_data.add(str_names);
                        map.clear();
                        p = 1;
                        f++;
                    }
                }
            }
            if (dataCount > download) {
                String filepath = createZip(tabName , dirPath, zipPath);
                if (org.apache.commons.lang3.StringUtils.isNotEmpty(filepath)) {
                    jsonObject.put("code", "success");
                    jsonObject.put("data", filepath);
                    return jsonObject;
                }
            }
        } finally {
            try {
                if (rsQuery != null) {
                    rsQuery.close();
                }
            } catch (Exception _exx) {
                _exx.printStackTrace();
            }

            try {
                if (stmtQuery != null) {
                    stmtQuery.close();
                }
            } catch (Exception _exx) {
                _exx.printStackTrace();
            }

            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (Exception _exx) {
                _exx.printStackTrace();
            }
        }
        jsonObject.put("code", "error");
        jsonObject.put("msg", "导出过程中发生异常!");
        return jsonObject;
    }

    /**
     * <pre>
     * Title: 导出cvs入口
     * </pre>
     *
     * @author miaokx
     * @version 1.00.00
     * @date 17:54 2020/11/12
     */
    public JSONObject exportCSV(JSONObject par) {
        JSONObject jsonObject = new JSONObject();
        try {
            return exportDataCSV(par);
        } catch (Exception e) {
            logger.info("导出发生异常!");
            e.printStackTrace();
            jsonObject.put("code", "error");
            return jsonObject;
        }
    }
    /**
     * <pre>
     * Title: 导出cvs抛异常供线程调用
     * </pre>
     *
     * @author miaokx
     * @version 1.00.00
     * @date 17:54 2020/11/12
     */
    public JSONObject exportDataCSV(JSONObject par) throws Exception{
        JSONObject jsonObject = new JSONObject();
        String orgNo = par.getString("orgNo");
        String strSql = par.getString("dataSql");
        String tabNameEn = par.getString("tabNameEn");
        String strDs = par.getString("dsName");
        String tabName = par.getString("tabName");
        String dataDt = par.getString("dataDt");
        String detailType = par.getString("detailType");
        // 获取实际机构号
        orgNo = getReportOrgNo(orgNo);
        int dataNum = par.getInteger("dataCount");
        Map<String, String> map = getParamsCsv(orgNo, dataDt, detailType);
        String dirPath = map.get("dirPath");
        String dirZipPath = map.get("dirZipPath");
        int downloadNum = Integer.parseInt(map.get("downloadNum"));
        int onerunNum = Integer.parseInt(map.get("onerunNum"));
        List<String> columnList = getColumnList(tabName, detailType);
        // 获取全量层数据
        String selectSql = packageSql(strSql, tabName,tabNameEn, detailType);
        String filePath = getBusinessDataByCSV(tabName, columnList, selectSql, dataNum, downloadNum, onerunNum, dirPath, strDs);
        if (dataNum > downloadNum) {
            filePath = createZip(tabName, dirPath, dirZipPath);
        }
        jsonObject.put("code", "success");
        jsonObject.put("data", filePath);
        return jsonObject;
    }
    /**
     * <pre>
     * Title: 获取全量层数据sql，统计条数
     * </pre>
     *
     * @author miaokx
     * @version 1.00.00
     * @date 10:00 2020/11/13
     */
    private String packageSqlByCount(String tabNameEn, String strSql) {
        StringBuffer str_sql = new StringBuffer();
        str_sql.append(" select count(*) c from " + tabNameEn + " where ");
        if (org.apache.commons.lang.StringUtils.isNotBlank(strSql)) {
            String str1 = strSql.substring(0, strSql.indexOf("where"));
            String str2 = strSql.substring(str1.length() + 5);

            str_sql.append(str2);
        }

        return str_sql.toString();
    }
    /**
     * 根据用户所属机构，查询报送机构
     * @param loginUserOrgNo
     * @param
     * @return
     */
    public String getReportOrgNo (String loginUserOrgNo) {
        String orgNo = "";

        String str_sql = "select org_no from rpt_org_info where org_type = '04' and mgr_org_no = '"+loginUserOrgNo+"'";
        HashVO[] hvs = bsUtil.getHashVOs(str_sql.toString());
        if (hvs != null && hvs.length > 0) {
            orgNo = hvs[0].getStringValue("org_no");
        }

        return orgNo;
    }

    /**
     * <pre>
     * Title: 获取所有报送列
     * </pre>
     *
     * @author miaokx
     * @version 1.00.00
     * @date 11:06 2020/11/13
     */
    private List<String> getColumnList(String tabNameEn, String detailType) {
        List<String> columnList = new ArrayList<String>();
        HashVO[] hvs = bsUtil.getHashVOs("select col_name from " + GlobalConstants4plugin.DetailFieldTab.getValue(detailType) + " where tab_name = '" + tabNameEn + "'  and is_fill_export='Y' order by col_no");
        if (hvs != null && hvs.length > 0) {
            for (int i = 0; i < hvs.length; i++) {
                columnList.add(hvs[i].getStringValue("col_name"));
            }
        }

        return columnList;
    }
    /**
     * <pre>
     * Title: 获取全量层数据sql
     * </pre>
     *
     * @author miaokx
     * @version 1.00.00
     * @date 11:15 2020/11/13
     */
    private String packageSql(String strSql, String tabName, String tabNameEn, String detailType) {
        StringBuffer str_sql = new StringBuffer();
        String columns = "";
        HashVO[] hvs = bsUtil.getHashVOs("select col_name_en from " + GlobalConstants4plugin.DetailFieldTab.getValue(detailType) + " where tab_name = '" + tabName + "' and is_fill_export='Y' order by col_no");
        if (hvs != null && hvs.length > 0) {
            for (int i = 0; i < hvs.length; i++) {
                columns += hvs[i].getStringValue("col_name_en") + ",";
            }
        }
        if (!columns.isEmpty()) {
            columns = columns.substring(0, columns.length() - 1);
        }
        str_sql.append(" select " + columns + " ");
        if (org.apache.commons.lang.StringUtils.isNotBlank(strSql)) {
            String str1 = strSql.substring(0, strSql.indexOf("from"));
            String str2 = strSql.substring(str1.length());

            str_sql.append(str2);
        }
        return str_sql.toString();
    }
    /***
     * @方法描述: 生成CSV方法
     * @创建人: miaokx@yusys.com.cn
     * @创建时间: 2021/10/12 13:57
     * @Param: tabName 表名
     * @Param: columnList 导出字段
     * @Param: selectSql 查询sql
     * @Param: dataNum 总数据量
     * @Param: downloadNum 单文件数据库
     * @Param: onerunNum 数据库批处理条数
     * @Param: dirPath 路径
     * @Param: strDs 数据源
     * @return: java.lang.String
     */
    public String getBusinessDataByCSV(String tabName, List<String> columnList, String selectSql, int dataNum, int downloadNum, int onerunNum, String dirPath, String strDs) {
        Connection conn = null;
        Statement stmtQuery = null;
        ResultSet rsSelectQuery = null;
        // 分页从oracle中获取错误数据
        try {
            conn  = bsUtil.createDatabaseConnectionByDS(strDs);
            stmtQuery = conn.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
            // 设置fetchsize  可根据实际情况调整大小。
            String dsType = bsUtil.getDsTypeByDsName(strDs);
            if (dsType.equalsIgnoreCase("oracle") || dsType.equalsIgnoreCase("oracle-rac")) {
                stmtQuery.setFetchSize(GlobalConstants4plugin.FETCH_SIZE);
            } else {
                stmtQuery.setFetchSize(Integer.MIN_VALUE);
            }
            rsSelectQuery = stmtQuery.executeQuery(selectSql); // 查询数据
            String filepath = generateCSV(rsSelectQuery, columnList, dirPath, dataNum, onerunNum, downloadNum, tabName);
            return filepath;
        } catch (Exception _ex) {
            logger.debug("发生异常,发生时间" + new SimpleDateFormat("yyyy-MM-dd hh:mm:sss").format(new Date()));
            _ex.printStackTrace();
            return null;
        } finally {
            try {
                if (rsSelectQuery != null) {
                    rsSelectQuery.close();
                }
            } catch (Exception _exx) {
                _exx.printStackTrace();
            }

            try {
                if (stmtQuery != null) {
                    stmtQuery.close();
                }
            } catch (Exception _exx) {
                _exx.printStackTrace();
            }

            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (Exception _exx) {
                _exx.printStackTrace();
            }
        }
    }
    /**
     * <pre>
     * Title: 真正下载csv的方法
     * </pre>
     *
     * @author miaokx
     * @version 1.00.00
     * @date 11:25 2020/11/13
     */
    private String generateCSV(ResultSet rs, List<String> columnList, String dirPath, int dataNum, int onerunNum, int downloadNum, String tabName) throws Exception {
        List<Object[]> objlist = new ArrayList<Object[]>();
        FileOutputStream outStreasm = null;

        ResultSetMetaData md = rs.getMetaData(); // 获取键名
        int columnCount = md.getColumnCount(); // 获取行的数量

        int k = 1;
        int j = 1;


        try {
            while (rs.next()) {
                Object objArr[] = new Object[columnCount];
                for (int i = 1; i <= columnCount; i++) {
                    objArr[i - 1] = rs.getObject(i) == null || rs.getObject(i) == "" ? "" : rs.getObject(i).toString() + "\t";
                }
                objlist.add(objArr);
                if (k % downloadNum == 0) {
                    logger.info("--------------每隔" + downloadNum + "进行一存储：第" + j + "次--------------");
                    String fileName = tabName + "_" + j + ".csv";
                    fileName = new String(fileName.getBytes("UTF-8"), "UTF-8");
                    if(FilepathValidateUtils.validateFilepath(new String((dirPath + fileName).getBytes("GBK")))) {
                        File downFile = new File(dirPath + fileName);
                        outStreasm = new FileOutputStream(downFile);
                        ExportCsvUtils.simpleExport(true, "\n", columnList.toArray(new String[columnList.size()]), objlist, fileName, outStreasm, onerunNum);
                        objlist.clear();
                        j++;
                    }
                } else if (k == dataNum) {
                    // 说明数据量小与50w
                    if (j == 1) {
                        logger.info("--------------数据量小与" + downloadNum + "--------------");
                        String fileName = tabName + ".csv";
                        fileName = new String(fileName.getBytes("UTF-8"), "UTF-8");
                        File downFile = new File(dirPath + fileName);
                        outStreasm = new FileOutputStream(downFile);
                        ExportCsvUtils.simpleExport(true, "\n", columnList.toArray(new String[columnList.size()]), objlist, fileName, outStreasm, onerunNum);
                        objlist.clear();
                        return dirPath + fileName;
                    } else {
                        logger.info("--------------每隔" + downloadNum + "进行一存储（最后）：第" + j + "次--------------");
                        String fileName = tabName + "_" + j + ".csv";
                        fileName = new String(fileName.getBytes("UTF-8"), "UTF-8");
                        if(FilepathValidateUtils.validateFilepath(new String((dirPath + fileName).getBytes("GBK")))) {
                            File downFile = new File(dirPath + fileName);
                            outStreasm = new FileOutputStream(downFile);
                            ExportCsvUtils.simpleExport(true, "\n", columnList.toArray(new String[columnList.size()]), objlist, fileName, outStreasm, onerunNum);

                            objlist.clear();
                        }
                    }
                }
                k++;
                objArr = null;
            }

        } finally {
            if (outStreasm != null) {
                outStreasm.close();
            }
        }
        return null;

    }

    /***
     * @方法描述: 生成zip的方法
     * @创建人: miaokx@yusys.com.cn
     * @创建时间: 2021/10/12 13:58
     * @Param: tabName 表名
     * @Param: dirPath 文件路径
     * @Param: zipPath 生成zip的路径
     * @return: java.lang.String
     */
    public String createZip(String tabName, String dirPath, String zipPath) throws Exception {
        ZipOutputStream zos = null;
        FileInputStream fis = null;
        BufferedInputStream bis = null;
        FileOutputStream fos = null;
        String zipFileNm = tabName + ".zip";
        zipFileNm = new String(zipFileNm.getBytes("UTF-8"), "UTF-8");
        String zipRealPath = zipPath + zipFileNm;
        try {
            fos = new FileOutputStream(zipRealPath);
            zos = new ZipOutputStream(fos);
            File dirFile = null;
            if(FilepathValidateUtils.validateFilepath(dirPath)) {
                dirFile = new File(dirPath);
                File[] files = dirFile.listFiles();
                ZipEntry zipEntry = null;
                for (int i = 0; i < files.length; i++) {
                    // 先不设置编码,不然部署到服务器上回乱码
                    zipEntry = new ZipEntry(files[i].getName());
                    zos.putNextEntry(zipEntry);

                    fis = new FileInputStream(files[i]); //获取输入流
                    bis = new BufferedInputStream(fis, 1024 * 10);

                    int length = 0;
                    byte[] buff = new byte[1024 * 10]; //每次读取的字节数
                    while ((length = bis.read(buff)) > 0) {
                        zos.write(buff, 0, length);
                        zos.flush();
                    }
                    if (fis != null) {
                        fis.close();
                    }
                    files[i].delete();
                }
            }
            // 删除目录
            dirFile.delete();
            logger.info("~~~~~~~~~ 生成zip完成");
            return zipRealPath;
        } catch (Exception e) {
            logger.error("从服务器下载ZIP包出错", e);
            return null;
        } finally {
            if (bis != null) {
                bis.close();
            }
            if (fis != null) {
                fis.close();
            }
            if (zos != null) {
                zos.close();
            }

        }
    }

    /**
     * @方法描述: 导出功能线程启动方法
     * @创建人: miaokx@yusys.com.cn
     * @创建时间: 2021/10/13 16:28
     * @Param: par
     * @return: void
     */
    public JSONObject exportDataThread(JSONObject par) {
        JSONObject jsonObject = new JSONObject();
        // 获取参数
        Map<String, String> params = getParams(par);
        params.put("userName", BioneSecurityUtils.getCurrentUserInfo().getUserName());
        try {
            String rid = "";
            params.put("status", "1");
            params.put("remark", "");
            if ("success".equalsIgnoreCase(par.getString("code"))) {
                // 如果信息返回的成功则代表新增
                rid = saveDataInsert(params);
            } else {
                rid = par.getString("rid");
                params.put("rid", rid);
                // 警告则代表修改。
                saveDataUpdate(params);
            }
            DetailDownloadMonitor.getInstance().putDataMap(rid, par);
            // 调用启动线程的方法
            DetailDownloadMonitor.getInstance().addCreateFileWaitTaskOrStart(rid);
            jsonObject.put("code", "success");
        } catch (Exception e) {
            logger.info("保存下载列表失败!");
            e.printStackTrace();
            jsonObject.put("code", "error");
            jsonObject.put("msg", "导出过程中发生错误：" + e.getMessage());
        }
        return jsonObject;
    }
    /**
     * @方法描述: 判断任务是否存在，节省资源。
     * @创建人: miaokx@yusys.com.cn
     * @创建时间: 2021/10/21 15:36
     * @Param: par
     * @return: com.alibaba.fastjson.JSONObject
     */
    public JSONObject showMsg(JSONObject par) {
        JSONObject jsonObject = new JSONObject();
        // 获取参数
        Map<String, String> params = getParams(par);
        //首先判断已生成的符合参数的数据存不存在
        List<String> list = new ArrayList<>();
        list.add("status");
        list.add("rid");
        params.put("userName", BioneSecurityUtils.getCurrentUserInfo().getUserName());
        JSONObject result = exists(params, list);
        // 先判断自己是否有任务存在
        if ("Y".equalsIgnoreCase(result.getString("code")) && !"4".equalsIgnoreCase(result.getString("status"))) {
            // 如果存在，处理中则返回等待
            if ("2".equalsIgnoreCase(result.getString("status"))) {
                jsonObject.put("code", "error");
                jsonObject.put("msg", "该导出内容正在生成中，请稍后前往下载列表下载。");

            } else if ("3".equalsIgnoreCase(result.getString("status"))) {
                jsonObject.put("code", "warn");
                jsonObject.put("rid", result.getString("rid"));
            }
            return jsonObject;
        }
        params.remove("userName");
        params.put("status", "3");
        list = new ArrayList<>();
        list.add("file_path");
        result = exists(params, list);
        if ("Y".equalsIgnoreCase(result.getString("code"))) {
            jsonObject.put("code", "warn");
            try {
                // 如果存在，则新增
                String filepath = result.getString("file_path");
                params.put("filepath", filepath);
                params.put("userName", BioneSecurityUtils.getCurrentUserInfo().getUserName());
                params.put("remark", "文件生成成功!");
                String rid = saveDataInsert(params);
                jsonObject.put("rid", rid);
                return jsonObject;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        jsonObject.put("code", "success");
        return jsonObject;
    }

    /***
     * @方法描述: 根据查询条件判断是否存在
     * @创建人: miaokx@yusys.com.cn
     * @创建时间: 2021/10/20 13:57
     * @Param: params
     * @return: boolean
     */
    public JSONObject exists(Map<String, String> params, List<String> returnFieldList) {
        String tabNameEn = params.get("tabNameEn");
        String dataDt = params.get("dataDt").replace("-", "");
        String reportType = params.get("reportType");
        String fileType = params.get("fileType");
        String paramsEn = params.get("paramsEn");
        String userName = params.get("userName");
        String status = params.get("status");
        String downloadType = params.get("downloadType");
        String sql = "select * from detail_download_list where tab_name_en='" + tabNameEn +"' and data_dt = '" + dataDt + "' and download_type='" + downloadType +
                "' and report_type='" + reportType + "' and file_type='" + fileType + "' and params_en='" + paramsEn.replace("'", "\"") + "'";
        if (StringUtils.isNotBlank(userName)) {
            sql += " and create_by = '" + userName + "'";
        }
        if (StringUtils.isNotBlank(status)) {
            sql += " and status = '" + status + "'";
        }
        HashVO[] hashVOS = bsUtil.getHashVOs(sql);
        JSONObject jsonObject = new JSONObject();
        if (hashVOS.length > 0) {
            HashVO hashVO = hashVOS[0];
            for (String returnField: returnFieldList) {
                String fieldValue = hashVO.getStringValue(returnField);
                jsonObject.put(returnField, fieldValue);
            }
            jsonObject.put("code", "Y");
        } else {
            jsonObject.put("code", "N");
        }
        return jsonObject;
    }
    /**
     * @方法描述: 删除
     * @创建人: miaokx@yusys.com.cn
     * @创建时间: 2021/10/21 18:46
     * @Param: par
     * @return: com.alibaba.fastjson.JSONObject
     */
    public JSONObject deleteZip(JSONObject par) {
        JSONObject jsonObject = new JSONObject();
        String rid = par.getString("rid");
        String path = par.getString("filepath");
        // 删除队列任务
        DetailDownloadMonitor.getInstance().removeQueue(rid);
        DetailDownloadMonitor.getInstance().removedataMap(rid);
        try {
            String deleteSql = " delete from detail_download_list where rid = '" + rid + "'";
            bsUtil.executeUpdate(deleteSql);
            String countSql = "select count(1) c from detail_download_list where file_path='" + path + "'";
            HashVO[] hashVOS = bsUtil.getHashVOs(countSql);
            Integer count = hashVOS[0].getIntegerValue("c");
            if (count == 0) {
                if (StringUtils.isNotBlank(path)) {
                    File file=new File(path);
                    file.delete();
                    // 根据path获取上一层，删除上一层
                    String directory = path.substring(0, path.lastIndexOf("/") );
                    File fileDir = new File(directory);
                    fileDir.delete();
                }
            }
            jsonObject.put("code", "success");
        } catch (Exception e) {
            jsonObject.put("code", "error");
            jsonObject.put("msg", "删除失败，失败原因：" + e.getMessage());
            e.printStackTrace();
        }
        return jsonObject;
    }
    /***
     * @方法描述:根据rid获取任务状态
     * @创建人: miaokx@yusys.com.cn
     * @创建时间: 2021/10/22 9:51
     * @Param: par
     * @return: com.alibaba.fastjson.JSONObject
     */
    public JSONObject getStatus(JSONObject par) {
        JSONObject jsonObject = new JSONObject();
        String rid = par.getString("rid");
        String sql = " select status from detail_download_list where rid='" + rid + "'";
        HashVO[] hashVOS = bsUtil.getHashVOs(sql);
        String status = "";
        if (hashVOS.length > 0) {
            HashVO hashVO = hashVOS[0];
            status = hashVO.getStringValue("status");
        }
        jsonObject.put("code", "success");
        jsonObject.put("data", status);
        return jsonObject;
    }
    /**
     * @方法描述: 保存数据
     * @创建人: miaokx@yusys.com.cn
     * @创建时间: 2021/10/20 14:59
     * @Param: param
     * @return: void
     */
    public String saveDataInsert(Map<String, String> param) throws Exception {
        String tabNameEn = param.get("tabNameEn");
        String dataDt = param.get("dataDt");
        if (StringUtils.isNotBlank(dataDt)) {
            dataDt = dataDt.replace("-", "");
        }
        String reportType = param.get("reportType");
        String fileType = param.get("fileType");
        String paramsEn = param.get("paramsEn").replace("'", "\"");
        String userName = param.get("userName");
        String tabName = param.get("tabName");
        String params = param.get("params");
        String status = param.get("status");
        String filepath = param.get("filepath");
        String remark = param.get("remark");
        String downloadType = param.get("downloadType");
        String rid = RandomUtils.uuid2();
        InsertSQLBuilder insertSQLBuilder = new InsertSQLBuilder("detail_download_list");
        insertSQLBuilder.putFieldValue("rid", rid);
        insertSQLBuilder.putFieldValue("data_dt", dataDt);
        insertSQLBuilder.putFieldValue("tab_name", tabName);
        insertSQLBuilder.putFieldValue("tab_name_en", tabNameEn);
        insertSQLBuilder.putFieldValue("report_type", reportType);
        insertSQLBuilder.putFieldValue("file_type", fileType);
        insertSQLBuilder.putFieldValue("params", params);
        insertSQLBuilder.putFieldValue("params_en", paramsEn);
        insertSQLBuilder.putFieldValue("file_path", filepath);
        insertSQLBuilder.putFieldValue("download_type", downloadType);
        if (StringUtils.isNotBlank(status)) {
            insertSQLBuilder.putFieldValue("status", status);
        }
        insertSQLBuilder.putFieldValue("create_by", userName);
        insertSQLBuilder.putFieldValue("create_time", DateUtils.getYYYY_MM_DD_HH_mm_ss());
        insertSQLBuilder.putFieldValue("remark", remark);
        bsUtil.executeUpdate(insertSQLBuilder.getSQL());
        return rid;
    }
    /**
     * @方法描述: 保存数据
     * @创建人: miaokx@yusys.com.cn
     * @创建时间: 2021/10/20 14:59
     * @Param: param
     * @return: void
     */
    public String saveDataUpdate(Map<String, String> param) throws Exception {
        String status = param.get("status");
        String rid = param.get("rid");
        String remark = param.get("remark");
        UpdateSQLBuilder updateSQLBuilder = new UpdateSQLBuilder("detail_download_list", "rid='" + rid +"'");
        updateSQLBuilder.putFieldValue("status", status);
        updateSQLBuilder.putFieldValue("remark", remark);
        bsUtil.executeUpdate(updateSQLBuilder.getSQL());
        return rid;
    }
    /***
     * @方法描述: 获取入库的参数
     * @创建人: miaokx@yusys.com.cn
     * @创建时间: 2021/10/19 11:17
     * @Param: par
     * @return: java.util.Map<java.lang.String,java.lang.String>
     */
    public Map<String, String> getParams (JSONObject par) {
        // 获取导出信息
        String fileType = par.getString("fileType");
        String reportType = par.getString("detailType");
        String dataSql = par.getString("dataSql");
        String dataDt = par.getString("dataDt");
        String tabName = par.getString("tabName");
        String tabNameEn = par.getString("tabNameEn");
        String downloadType = par.getString("downloadType");
        // 英文参数 用于存入数据库中
        String paramsEn = par.getString("params");
        if (StringUtils.isBlank(paramsEn)) {
            // 检核结果页面需要传参，因为检核结果无法通过sql获取参数。而且检核结果只需要日期 法人和表
            // 其他页面通过sql获取参数
            paramsEn = dataSql.substring(dataSql.indexOf("where") + 5);
        }
        if (paramsEn.contains("not exists")) {
            // east报送特殊处理sql,从exists 最后开始截取为参数。
            paramsEn = dataSql.substring(dataSql.indexOf(")") + 1);
        }
        String [] params = paramsEn.split(" and ");
        for (String param: params){
            if (param.contains("(issued_no in (")){
                // 如果是填报机构的授权机构，则获取用户所在机构
                String sql = " select org_no from rpt_org_info where mgr_org_no='" + BioneSecurityUtils.getCurrentUserInfo().getOrgNo() + "' and org_type='" + GlobalConstants4plugin.DetailOrgType.getValue(reportType) + "'";
                HashVO[] hashVOS = bsUtil.getHashVOs(sql);
                String issuedNo = "";
                if (hashVOS.length > 0) {
                    issuedNo = hashVOS[0].getStringValue("org_no");
                }
                paramsEn = paramsEn.replace(param, "issued_no = '" + issuedNo + "'");
            }
        }
        String[] paramsArr = paramsEn.replace("'", "").replace("(","").
                replace(")", "").split(" and | or ");
        Map<String, String> paramCnMap = new HashMap<>();
        // 根据reportType 获取字段表存储表名
        String colName  = GlobalConstants4plugin.DetailFieldTab.getValue(reportType);
        String colNameSql = " select col_name_en,col_name from " + colName + " where tab_name='" + tabName + "'";
        HashVO[] hashVOS = bsUtil.getHashVOs(colNameSql);
        Map<String,String> colNameMap = new HashMap<>();
        for (HashVO hashVO: hashVOS) {
            colNameMap.put(hashVO.getStringValue("col_name_en"), hashVO.getStringValue("col_name"));
        }
        //List<String> orgNoList = new ArrayList<>();
        //List<String> rptOrgNoList = new ArrayList<>();
        // 获取中文参数，用于列表展示。
        for (String param: paramsArr) {
            if (StringUtils.isNotBlank(param)) {
                param = param.trim();
                if (!"1=1".equals(param)) {
                    String[] paramArr = param.split("=|in");
                    if (paramArr.length == 2) {
                        if (StringUtils.isNotBlank(paramArr[0]) && StringUtils.isNotBlank(paramArr[1])) {
                            String colNameCn  = colNameMap.get(paramArr[0].trim().toUpperCase());
                            String value = paramArr[1].trim();
                            paramCnMap.put(colNameCn, value);
                            /*if ("org_no".equalsIgnoreCase(paramArr[0])) {
                                String[] valueArr = value.split(",");
                                List<String> orgNo = Arrays.asList(valueArr);
                                orgNoList.addAll(orgNo);
                            }
                            if ("rpt_org_no".equalsIgnoreCase(paramArr[0])) {
                                String[] valueArr = value.split(",");
                                List<String> rptOrgNo = Arrays.asList(valueArr);
                                rptOrgNo.addAll(rptOrgNo);
                            }*/
                        }

                    }

                }
            }
        }
        //Collections.sort(orgNoList);
        //Collections.sort(rptOrgNoList);
        // 获取中文参数
        String paramsCn = JSONObject.toJSONString(paramCnMap);
        // 组装入库所需参数
        Map<String, String> param = new HashMap<>();
        param.put("tabName", tabName);
        param.put("tabNameEn", tabNameEn);
        param.put("dataDt", dataDt);
        param.put("reportType", reportType);
        param.put("fileType", fileType);
        param.put("paramsEn", paramsEn);
        param.put("params", paramsCn);
        param.put("downloadType", downloadType);
        return param;
    }



    /**
     * <pre>
     * Title: 获取文件存放路径
     * </pre>
     *
     * @author baifk
     * @version 1.00.00
     * @date 0:17 2020/10/31
     */
    public Map<String, String> getParams(String orgNo, String dataDt, String detailType) throws Exception {
        Map<String, String> params = new HashMap<>();
        Properties configProp = new Properties();
        if (StringUtils.isNotBlank(detailType)) {
            configProp.load(this.getClass().getClassLoader().getResourceAsStream(GlobalConstants4plugin.DetailProperties.getValue(detailType)));
        }
        String str_downfile = configProp.getProperty("data.download.filepath");
        String str_downzip = configProp.getProperty("data.download.zippath");
        String download = configProp.getProperty("data.download.excel");
        String dirPath = str_downfile + "/"  + orgNo + "/" + dataDt + "/" + RandomUtils.uuid2() + "/";
        String zipPath = str_downzip + "/"  + orgNo + "/" + dataDt + "/"+ RandomUtils.uuid2() + "/";
        File dirFile = new File(dirPath);
        File zipFile = new File(zipPath);
        if (!dirFile.exists()) {
            dirFile.mkdirs();
        } else {
            File[] files = dirFile.listFiles();
            if (files != null) {
                for (int i = 0; i < files.length; i++) {
                    files[i].delete();
                }
            }
        }
        if (!zipFile.exists()) {
            zipFile.mkdirs();
        }
        params.put("dirPath", dirPath);
        params.put("zipPath", zipPath);
        params.put("download", download);
        return params;
    }

    /**
     * <pre>
     * Title: 获取文件临时存放路径
     * </pre>
     *
     * @author miaokx
     * @version 1.00.00
     * @date 10:10 2020/11/13
     */
    public Map<String, String> getParamsCsv(String orgNo, String dataDt, String detailType) throws Exception {
        Map<String, String> map = new HashMap<>();
        Properties configProp = new Properties();
        InputStream inputStream = null;
        try {
            if (StringUtils.isNotBlank(detailType)) {
                inputStream = this.getClass().getClassLoader().getResourceAsStream(GlobalConstants4plugin.DetailProperties.getValue(detailType));
            }
            configProp.load(inputStream);
            String str_downfile = configProp.getProperty("data.download.filepath");
            String dirZipPath = configProp.getProperty("data.download.zippath");
            String downloadNum = configProp.getProperty("data.download.csv");
            String onerunNum = configProp.getProperty("data.onerun.csv");
            String dirPath = str_downfile + "/"  + orgNo + "/" + dataDt + "/" + RandomUtils.uuid2() + "/";
            dirZipPath = dirZipPath + "/"  + orgNo + "/" + dataDt + "/"+ RandomUtils.uuid2() + "/";
            File dirFile = null;
            if(FilepathValidateUtils.validateFilepath(dirPath)) {
                dirFile = new File(dirPath);
                if (!dirFile.exists()) {
                    dirFile.mkdirs();
                } else {
                    File[] files = dirFile.listFiles();
                    if (files != null) {
                        for (int i = 0; i < files.length; i++) {
                            files[i].delete();
                        }
                    }
                }
            }
            if(FilepathValidateUtils.validateFilepath(dirZipPath)) {
                File dirFile1 = new File(dirZipPath);
                if (!dirFile1.exists()) {
                    dirFile1.mkdirs();
                }
            }

            map.put("dirPath", dirPath);
            map.put("dirZipPath", dirZipPath);
            map.put("downloadNum", downloadNum);
            map.put("onerunNum", onerunNum);
            return map;
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
        }

    }

    /**
     * @方法描述: 获取查询条件自己只能看自己的
     * @创建人: miaokx@yusys.com.cn
     * @创建时间: 2021/10/21 20:44
     * @Param: par
     * @return: com.alibaba.fastjson.JSONObject
     */
    public JSONObject getWhereSql(JSONObject par) {
        JSONObject jsonObject = new JSONObject();
        String whereSql = "";
        if (BioneSecurityUtils.getCurrentUserInfo().isSuperUser()) {
            whereSql = " 1=1";
        } else {
            whereSql = " create_by='" + BioneSecurityUtils.getCurrentUserInfo().getUserName() + "'";
        }
        jsonObject.put("code", "success");
        jsonObject.put("data", whereSql);
        return jsonObject;
    }
    /**
     * @方法描述: 获取错误数据的sql
     * @创建人: miaokx@yusys.com.cn
     * @创建时间: 2021/11/12 10:27
     * @Param: par
     * @return: com.alibaba.fastjson.JSONObject
     */
    public JSONObject getErrorSql(JSONObject par) {
        String type = par.getString("type");
        String tabNameEn = par.getString("tabNameEn");
        String errorTableName = par.getString("errorTableName");
        String detailType = par.getString("detailType");
        String dataSql = "";
        String countSql = "";
        String dsType = DriverCfgUtils.getDriverType();
        if ("1".equals(type)) {
            String taskId = par.getString("taskId");
            if (dsType.equalsIgnoreCase("oracle")) {
                dataSql = "select * from " + tabNameEn + "_r r left  join (select listagg(ruleids,',')within GROUP(order BY rid) as ruleids ,rid as id from "+ errorTableName + " GROUP BY rid ) e on e.id=r.rid where  task_id='"+ taskId +"' and PROC_STS = '0'";
            } else if (dsType.equalsIgnoreCase("mysql")) {
                dataSql = "select * from " + tabNameEn + "_r r left  join (select group_concat(distinct RULEIDS order by rid separator ',') as ruleids ,rid as id from "+ errorTableName + " GROUP BY rid ) e on e.id=r.rid where  task_id='"+ taskId +"' and PROC_STS = '0'";
            } else if (dsType.equalsIgnoreCase("postgresql")) {
                dataSql = "select * from " + tabNameEn + "_r r left  join (select listagg(ruleids,',')within GROUP(order BY rid) as ruleids ,rid as id from "+ errorTableName + " GROUP BY rid ) e on e.id=r.rid where  task_id='"+ taskId +"' and PROC_STS = '0'";
            }
            // 二维数组存储数据
            countSql = "select count(1) c from " + tabNameEn + "_r where task_id='"+ taskId +"' and PROC_STS = '0'";
        } else {
            String whereSql = par.getString("whereSql");
            String orgNo = par.getString("orgNo");
            if (dsType.equalsIgnoreCase("oracle")) {
                dataSql = "select * from " + tabNameEn + " r inner  join (select listagg(ruleids,',')within GROUP(order BY rid) as ruleids ,rid as id from "+ errorTableName + " where " + whereSql +" and rpt_org_no ='"+orgNo+"' GROUP BY rid ) e on e.id=r.rid";
                countSql = "select  count(count(1)) c from " + errorTableName + " where " + whereSql +" and rpt_org_no ='"+orgNo+"' GROUP BY rid";
            } else if (dsType.equalsIgnoreCase("mysql")) {
                dataSql = "select * from " + tabNameEn + " r inner  join (select group_concat(distinct RULEIDS order by rid separator ',') as ruleids ,rid as id from "+ errorTableName + " where " + whereSql +" and rpt_org_no ='"+orgNo+"' GROUP BY rid ) e on e.id=r.rid";
                countSql = "select  count(1) c from " + tabNameEn + " r inner  join (select group_concat(distinct RULEIDS order by rid separator ',') as ruleids ,rid from "+ errorTableName + " where " + whereSql +" and rpt_org_no ='"+orgNo+"' GROUP BY rid ) e on e.id=r.rid";

            } else if (dsType.equalsIgnoreCase("postgresql")) {
                dataSql = "select * from " + tabNameEn + " r inner  join (select listagg(ruleids,',')within GROUP(order BY rid) as ruleids ,rid as id from "+ errorTableName + " where " + whereSql +" and rpt_org_no ='"+orgNo+"' GROUP BY rid ) e on e.id=r.rid";
                countSql = "select count(c) c from (select count(1) c from " + errorTableName + " where " + whereSql +" and rpt_org_no ='"+orgNo+"' GROUP BY rid) a";
            }

        }
        JSONObject result = new JSONObject();
        String dsSql = "select ds_name from " + GlobalConstants4plugin.DetailTab.getValue(detailType) + " where tab_name_en='" + tabNameEn + "'";
        HashVO[] hashVOs = bsUtil.getHashVOs(dsSql);
        if (hashVOs.length > 0) {
            String dsName = hashVOs[0].getStringValue("ds_name");
            result.put("dsName", dsName);
        }

        Integer count = bsUtil.getHashVOs(countSql)[0].getIntegerValue("c");
        result.put("count", count);
        result.put("dataSql", dataSql);
        return result;
    }
}
