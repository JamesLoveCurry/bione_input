package com.yusys.bione.comp.bigdata.utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NavigableMap;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.CellUtil;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.util.Bytes;

public abstract class CommonHbaseUtil {

	static Log log = LogFactory.getLog(HBaseUtil.class);
	// 声明静态配置
	static Configuration conf = null;

	static {
		conf = HBaseConfig.getConf();
	}

	

	/**
	 * 查询单条记录
	 **/
	public Map<String, String> getSingleResult(HBaseQueryCondition condition)
			throws Exception {
		Map<String, String> map = new HashMap<String, String>();
		// HTable table;
		// try {
		// table = HbaseQueryUtils.getHTable(param.getTableName());
		// Result result= table.get(new
		// Get(Bytes.toBytes(param.getStartRowKey())));
		// for (KeyValue kv : result.raw()) {
		// for(Data
		// d:trade.getSingleRecordHDTables().get(Constants.XMLTAG_FILTER_PRIMARYTAB).getDataList()){
		// if(d.getRefColumn().equals(Bytes.toString(kv.getQualifier()))){
		// map.put(Bytes.toString(kv.getQualifier()),
		// Bytes.toString(kv.getValue()));
		// }
		// }
		// }
		// } catch (IllegalArgumentException e) {
		// throw new Exception("没有查到数据",e);
		// } catch (Exception e) {
		// throw new Exception("查询单条数据出错",e);
		// }

		return map;
	}

	/**
	 * 查询所有记录
	 * 
	 * @param condition
	 *            查询条件
	 * @return 查询结果集
	 * @throws Exception
	 */
	public List<Map<String, String>> getList(HBaseQueryCondition condition)
			throws Exception {

		List<Map<String, String>> rows = new ArrayList<Map<String, String>>();

		return rows;
	}




	/**
	 * 根据rowKey查询
	 * 
	 * @param tableName
	 *            表名
	 * @param rowKey
	 *            rowkey
	 * @return list
	 * @throws IOException
	 */
	public static List<Map<String, Object>> getResult(String tableName,
			String rowKey) throws IOException {
		List<Map<String, Object>> rowsList = null;
		Connection conn = null;
		Table table = null;
		try {
			rowsList = new ArrayList<Map<String, Object>>();
			Map<String, Object> map = new HashMap<String, Object>();
			Get get = new Get(Bytes.toBytes(rowKey));
			conn = ConnectionFactory.createConnection(conf);
			table = conn.getTable(TableName.valueOf(tableName));
			Result result = table.get(get);
			map.put("rowkey", Bytes.toString(result.getRow()));
			List<Cell> cells = result.listCells();
			for (Cell c : cells) {
				log.debug("family:" + Bytes.toString(CellUtil.cloneFamily(c)));
				log.debug("qualifier:"
						+ Bytes.toString(CellUtil.cloneQualifier(c)));
				log.debug("value:" + Bytes.toString(CellUtil.cloneValue(c)));
				log.debug("Timestamp:" + c.getTimestamp());
				map.put(Bytes.toString(CellUtil.cloneQualifier(c))
						.toUpperCase(), Bytes.toString(CellUtil.cloneValue(c)));
			}
			rowsList.add(map);
		} catch (IOException e) {
			throw new IOException(e.getMessage());
		} finally {
			IOUtils.closeQuietly(table);
			IOUtils.closeQuietly(conn);
		}
		return rowsList;
	}

	/**
	 * 多值rowkey查询
	 * 
	 * @param tableName
	 * @param rowKeys
	 * @return
	 * @throws Exception
	 */
	public static List<Map<String, Object>> getResults(String tableName,
			String[] rowKeys) throws IOException {
		List<Map<String, Object>> rowsList = null;
		Connection conn = null;
		Table table = null;
		try {
			rowsList = new ArrayList<Map<String, Object>>();
			List<Get> getList = new ArrayList<Get>();
			for (String rowkey : rowKeys) {
				Get get = new Get(Bytes.toBytes(rowkey));
				getList.add(get);

			}
			conn = ConnectionFactory.createConnection(conf);
			table = conn.getTable(TableName.valueOf(tableName));
			Result[] results = table.get(getList);
			for (Result result : results) {
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("rowkey", Bytes.toString(result.getRow()));
				List<Cell> cells = result.listCells();
				for (Cell c : cells) {
					log.debug("family:"
							+ Bytes.toString(CellUtil.cloneFamily(c)));
					log.debug("qualifier:"
							+ Bytes.toString(CellUtil.cloneQualifier(c)));
					log.debug("value:" + Bytes.toString(CellUtil.cloneValue(c)));
					log.debug("Timestamp:" + c.getTimestamp());
					map.put(Bytes.toString(CellUtil.cloneQualifier(c))
							.toUpperCase(), Bytes.toString(CellUtil
							.cloneValue(c)));
				}
				rowsList.add(map);
			}
		} catch (IOException e) {
			throw new IOException("tableName: " + tableName + "rowKeys: "
					+ rowKeys, e);
		} finally {
			IOUtils.closeQuietly(table);
			IOUtils.closeQuietly(conn);
		}
		return rowsList;
	}

	/**
	 * 遍历查询hbase表
	 * 
	 * @param tableName
	 * @throws IOException
	 */
	public static List<Map<String, Object>> getResultScann(String tableName)
			throws IOException {
		List<Map<String, Object>> rows = new ArrayList<Map<String, Object>>();
		Scan scan = new Scan();
		Connection conn = null;
		Table table = null;
		ResultScanner rs = null;
		try {
			conn = ConnectionFactory.createConnection(conf);
			table = conn.getTable(TableName.valueOf(tableName));
			rs = table.getScanner(scan);
			for (Result r : rs) {
				Map<String, Object> rowMap = new HashMap<String, Object>();
				List<Cell> cells = r.listCells();
				for (Cell c : cells) {
					log.debug("row:" + Bytes.toString(CellUtil.cloneRow(c)));
					log.debug("family:"
							+ Bytes.toString(CellUtil.cloneFamily(c)));
					log.debug("qualifier:"
							+ Bytes.toString(CellUtil.cloneQualifier(c)));
					log.debug("value:" + Bytes.toString(CellUtil.cloneValue(c)));
					log.debug("timestamp:" + c.getTimestamp());
					rowMap.put(Bytes.toString(CellUtil.cloneQualifier(c)),
							Bytes.toString(CellUtil.cloneValue(c)));
				}
				rowMap.put("rowkey", Bytes.toString(r.getRow()));
				rows.add(rowMap);
			}
		} finally {
			IOUtils.closeQuietly(rs);
			IOUtils.closeQuietly(table);
			IOUtils.closeQuietly(conn);
		}
		return rows;
	}

	/**
	 * 遍历查询hbase表
	 * 
	 * @param tableName
	 * @param start_rowkey
	 * @param stop_rowkey
	 * @throws IOException
	 */
	public static void getResultScann(String tableName, String start_rowkey,
			String stop_rowkey) throws IOException {
		Scan scan = new Scan();
		scan.setStartRow(Bytes.toBytes(start_rowkey));
		scan.setStopRow(Bytes.toBytes(stop_rowkey));
		ResultScanner rs = null;
		Connection conn = null;
		Table table = null;
		try {
			conn = ConnectionFactory.createConnection(conf);
			table = conn.getTable(TableName.valueOf(tableName));
			rs = table.getScanner(scan);
			for (Result r : rs) {
				List<Cell> cells = r.listCells();
				for (Cell c : cells) {
					log.debug("row:" + Bytes.toString(CellUtil.cloneRow(c)));
					log.debug("family:"
							+ Bytes.toString(CellUtil.cloneFamily(c)));
					log.debug("qualifier:"
							+ Bytes.toString(CellUtil.cloneQualifier(c)));
					log.debug("value:" + Bytes.toString(CellUtil.cloneValue(c)));
					log.debug("timestamp:" + c.getTimestamp());
					log.debug("-------------------------------------------");
				}
			}
		} finally {
			IOUtils.closeQuietly(rs);
			IOUtils.closeQuietly(table);
			IOUtils.closeQuietly(conn);
		}
	}

	/**
	 * 按rowkey查询多个列族数据
	 * 
	 * @param tableName
	 * @param rowKey
	 * @param columnfamily
	 * @return
	 * @throws IOException
	 */
	public static Map<String, Object> getResultByColumnFamily(String tableName,
			String rowKey, ArrayList<String> columnfamily) throws IOException {
		Connection conn = null;
		Table table = null;
		Result result = null;
		HashMap<String, Object> resultMap = null;
		try {
			conn = ConnectionFactory.createConnection(conf);
			table = conn.getTable(TableName.valueOf(tableName));
			Get get = new Get(Bytes.toBytes(rowKey));
			for (String family : columnfamily) {
				get.addFamily(Bytes.toBytes(family));
			}
			result = table.get(get);
			resultMap = new HashMap<String, Object>();
			for (String family : columnfamily) {
				NavigableMap<byte[], byte[]> map = result.getFamilyMap(Bytes
						.toBytes(family));
				Map<String, Object> colomMap = new HashMap<String, Object>();
				for (Entry<byte[], byte[]> me : map.entrySet()) {
					colomMap.put(Bytes.toString(me.getKey()),
							Bytes.toString(me.getValue()));
				}
				resultMap.put(family, colomMap);
			}
		} catch (IOException e) {
			throw new IOException("tableName: " + tableName + ", rowKey:"
					+ rowKey + "columnfamily: " + columnfamily.toString(), e);
		} finally {
			IOUtils.closeQuietly(table);
			IOUtils.closeQuietly(conn);
		}
		return resultMap;
	}

	/**
	 * 查询一列
	 * 
	 * @param tableName
	 * @param rowKey
	 * @param familyName
	 * @param columnName
	 * @throws IOException
	 */
	public static List<Map<String, Object>> getResultByColumn(String tableName,
			String rowKey, String familyName, String columnName)
			throws IOException {
		List<Map<String, Object>> rows = new ArrayList<Map<String, Object>>();
		Map<String, Object> map = null;
		Connection conn = null;
		Table table = null;
		try {
			conn = ConnectionFactory.createConnection(conf);
			table = conn.getTable(TableName.valueOf(tableName));
			Get get = new Get(Bytes.toBytes(rowKey));
			get.addColumn(Bytes.toBytes(familyName), Bytes.toBytes(columnName)); // 获取指定列族和列修饰符对应的列
			Result result = table.get(get);
			List<Cell> cells = result.listCells();
			for (Cell c : cells) {
				map = new HashMap<String, Object>();
				map.put("rowkey", Bytes.toString(CellUtil.cloneRow(c)));
				log.debug("family:" + Bytes.toString(CellUtil.cloneFamily(c)));
				log.debug("qualifier:"
						+ Bytes.toString(CellUtil.cloneQualifier(c)));
				log.debug("value:" + Bytes.toString(CellUtil.cloneValue(c)));
				log.debug("Timestamp:" + c.getTimestamp());
				log.debug("-------------------------------------------");
				map.put(Bytes.toString(CellUtil.cloneQualifier(c)),
						Bytes.toString(CellUtil.cloneValue(c)));
				rows.add(map);
			}
		} catch (IOException e) {
			throw new IOException("tableName: " + tableName + ", rowKey: "
					+ rowKey + ", familyName: " + familyName + " columnName: "
					+ columnName, e);
		} finally {
			IOUtils.closeQuietly(table);
			IOUtils.closeQuietly(conn);
		}
		return rows;
	}

	/**
	 * 创建表
	 * 
	 * @param tableName
	 *            表名
	 * @param columnFamilyList
	 *            列簇List
	 * @throws IOException
	 */
	public static void createTable(String tableName,
			List<String> columnFamilyList) throws IOException {
		Connection conn = null;
		Admin admin = null;
		try {
			conn = ConnectionFactory.createConnection(conf);
			admin = conn.getAdmin();
			if (admin.tableExists(TableName.valueOf(tableName))) {
				log.error("TABLE EXISTS!");
			} else {
				HTableDescriptor tableDesc = new HTableDescriptor(
						TableName.valueOf(tableName));
				for (int i = 0, len = columnFamilyList.size(); i < len; i++) {
					tableDesc.addFamily(new HColumnDescriptor(columnFamilyList
							.get(i)));
				}
				admin.createTable(tableDesc);
				log.debug("CREATE TABLE SUCCESS!");
			}
		} catch (IOException e) {
			throw new IOException("tableName: " + tableName
					+ ", columnFamilyList: " + columnFamilyList.toString(), e);
		} finally {
			IOUtils.closeQuietly(admin);
			IOUtils.closeQuietly(conn);
		}

	}

	/**
	 * DROP表
	 * 
	 * @param tableName
	 *            表名
	 * @throws IOException
	 */
	public static void dropTable(String tableName) throws IOException {
		Connection conn = null;
		Admin admin = null;
		try {
			conn = ConnectionFactory.createConnection(conf);
			admin = conn.getAdmin();
			TableName tableNameObj = TableName.valueOf(tableName);
			if (admin.tableExists(tableNameObj)) {
				admin.disableTable(tableNameObj);
				admin.deleteTable(tableNameObj);
			} else {
				log.error("TABLE NOT EXISTS!");
			}
		} catch (IOException e) {
			throw new IOException("tableName: " + tableName, e);
		} finally {
			IOUtils.closeQuietly(admin);
			IOUtils.closeQuietly(conn);
		}
	}

	/**
	 * PUT 一个列簇下的列修饰符值
	 * 
	 * @param tableName
	 * @param rowKey
	 * @param columnFamily
	 * @param columns
	 * @throws IOException
	 */
	public static void putRowCfData(String tableName, String rowKey,
			String columnFamily, Map<String, Object> columns)
			throws IOException {
		Connection conn = null;
		Table table = null;
		try {
			conn = ConnectionFactory.createConnection(conf);
			table = conn.getTable(TableName.valueOf(tableName));
			Put pt = new Put(Bytes.toBytes(rowKey));
			for (Map.Entry<String, Object> entry : columns.entrySet()) {
				String key = (String) entry.getKey();
				String value = (String) entry.getValue();
				pt.addColumn(Bytes.toBytes(columnFamily), Bytes.toBytes(key),
						Bytes.toBytes(value));
			}
			table.put(pt);
		} catch (IOException e) {
			throw new IOException("tableName: " + tableName + ", rowKey: "
					+ rowKey + ", columnFamily: " + columnFamily, e);
		} finally {
			IOUtils.closeQuietly(table);
			IOUtils.closeQuietly(conn);
		}
	}
}

