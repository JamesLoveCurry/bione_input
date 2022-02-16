package com.yusys.bione.comp.bigdata.utils;


public class HBaseUtil extends CommonHbaseUtil{

	
	/**
	 * 获得符合条件结果总数及数据
	 * 
	 * @author guochi@yusys.com.cn
	 * @param page
	 *            分页参数
	 * @param condition
	 *            查询条件
	 */
//	public static Map<String, Object> getResultAndCount(PagingInfo page,
//			HBaseQueryCondition condition) {
//		Map<String, Object> json = new HashMap<String, Object>();
//		try {
//			json.put("data", getList(page, condition));
//			json.put("count",getTotalNumber(QueryUtil.getScan(condition),
//							condition.getTableName()));
//		} catch (Throwable e) {
//			log.error(e.getMessage());
//		}
//
//		return json;
//	};
	
	/**
	 * 获得符合条件结果总数
	 * 
	 * @author guochi@yusys.com.cn
	 * @param scan
	 *            查询参数
	 * @param ableName
	 *            表名
	 * @throws Throwable 
	 */
//	public static int getTotalNumber(Scan scan, String tableName) throws Throwable {
//		AggregationClient aggregationClient = new AggregationClient(conf);
//		Long rowCount = Long.valueOf(0);
// 			rowCount = aggregationClient.rowCount(QueryUtil.getHTable(tableName),
//					new LongColumnInterpreter(), scan);
//		// rowCount = aggregationClient.rowCount(TableName.valueOf(tableName),
//		// new LongColumnInterpreter(), scan);
//		return rowCount.intValue();
//	}
}
