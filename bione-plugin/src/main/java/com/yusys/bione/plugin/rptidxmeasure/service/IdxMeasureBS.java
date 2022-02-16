package com.yusys.bione.plugin.rptidxmeasure.service;

import com.yusys.bione.comp.repository.jpa.SearchResult;
import com.yusys.bione.comp.service.BaseBS;
import com.yusys.bione.plugin.rptidxmeasure.entity.RptIdxMeasure;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 <pre>
 * Title:度量管理的业务逻辑类
 * Description: 提供度量管理相关业务逻辑处理功能，提供事务控制
 * </pre>
 *
 * @author jiangsh@yusys.com.cn
 *
 * <pre>
 * 修改记录
 *    修改后版本:     修改人：  修改日期:     修改内容:
 * </pre>
 */

@Service("idxMeasureBS")
@Transactional(readOnly = true)
public class IdxMeasureBS extends BaseBS<RptIdxMeasure> {
    protected static Logger log= LoggerFactory.getLogger(IdxMeasureBS.class);
    /**
     * 分页查询度量信息
     *
     * @param firstResult
     *            分页的开始索引,第一条记录
     * @param pageSize
     *            每页记录数
     * @param orderBy
     *            排序字段
     * @param orderType
     *            排序方式
     * @param conditionMap
     *            搜索条件
     */
    @SuppressWarnings("unchecked")
    public SearchResult<RptIdxMeasure> getIdxMeasureList(int firstResult,
    int pageSize, String orderBy, String orderType,
    Map<String, Object> conditionMap) {
        SearchResult<RptIdxMeasure> roleList;
        StringBuilder sql=new StringBuilder("");
        Map<String,Object> values=(Map<String, Object>)conditionMap.get("params");

        sql.append("select measure from RptIdxMeasure measure where measure.measureType='03'");
        if (!conditionMap.get("jql").equals("")) {
            sql.append(" and " + conditionMap.get("jql"));
        }
        if (!StringUtils.isEmpty(orderBy)) {
            sql.append(" order by " + orderBy + " " + orderType);
        }
        roleList=this.baseDAO.findPageWithNameParam(firstResult,pageSize,sql.toString(),values);
        return roleList;
    }

    /**
     * 根据度量标识删除度量字段
     * @param nos 度量标识
     */
    @Transactional(readOnly = false)
    public void deleteIdxMeasuresByNos(String[] nos){
        if(nos!=null){
            List<String> idList = new ArrayList<>();
            for (int i = 0; i < nos.length; i++) {
                idList.add(nos[i]);
            }
            if (idList.size() > 0) {
            String sql="delete from RptIdxMeasure measure where measure.measureNo in (?0)";
            this.baseDAO.batchExecuteWithIndexParam(sql,idList);
            }
        }
    }

    public boolean checkIsMeasureNoExi(String measureNo){
        boolean check=true;//度量标识不存在
        String jql="select measure from RptIdxMeasure measure where measureNo=?0 ";
        List<RptIdxMeasure> list = this.baseDAO.findWithIndexParam(jql,measureNo);
        if(list !=null && list.size()>0){
            check=false;
        }
        return check;
    }

}
