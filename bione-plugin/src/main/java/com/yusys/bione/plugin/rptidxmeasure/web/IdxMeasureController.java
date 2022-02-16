package com.yusys.bione.plugin.rptidxmeasure.web;

import com.google.common.collect.Maps;
import com.yusys.bione.comp.entity.page.Pager;
import com.yusys.bione.comp.repository.jpa.SearchResult;
import com.yusys.bione.comp.utils.StringUtils2;
import com.yusys.bione.frame.base.web.BaseController;
import com.yusys.bione.plugin.rptidxmeasure.entity.RptIdxMeasure;
import com.yusys.bione.plugin.rptidxmeasure.service.IdxMeasureBS;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.util.Map;

@Controller
@RequestMapping("/rpt/frame/idxmeasure")
public class IdxMeasureController extends BaseController {
    @Autowired
    private IdxMeasureBS idxMeasureBS;

    @RequestMapping(method = RequestMethod.GET)
    public ModelAndView index() {
        return new ModelAndView("/plugin/rptidxmeasure/rpt-idx-measure-info-list");
    }

  /*  //获取度量名称
    @RequestMapping("/getIdxMeasureNm")
    @ResponseBody
    public List<RptIdxMeasure> getIdxMeasureNm(){
        List<RptIdxMeasure> list = idxMeasureBS.getIdxMeasureNm();
        return list;
    }
*/
    /**
     * 获取数据，展现在Grid
     */
    @RequestMapping("/list.*")
    @ResponseBody
    public Map<String, Object> list(Pager pager){
        SearchResult<RptIdxMeasure> searchResult=idxMeasureBS.getIdxMeasureList(pager.getPageFirstIndex(),pager.getPagesize(),  pager.getSortname(),pager.getSortorder(),pager.getSearchCondition());
        Map<String, Object> measureMap = Maps.newHashMap();
        measureMap.put("Rows",searchResult.getResult());
        measureMap.put("Total", searchResult.getTotalCount());
        return measureMap;
    }

    /**
     *添加新页面jsp
     */
    @RequestMapping(value = "/new",method = RequestMethod.GET)
    public ModelAndView editNew(){
        return new ModelAndView("/plugin/rptidxmeasure/rpt-idx-measure-editNew");
    }

    @RequestMapping(value = "/{id}/edit",method = RequestMethod.GET)
    public ModelAndView edit(@PathVariable("id") String id){
        id = StringUtils2.javaScriptEncode(id);
        return new ModelAndView("/plugin/rptidxmeasure/rpt-idx-measure-update", "id", id);
    }


    @RequestMapping(value = "/{id}",method = RequestMethod.GET)
    @ResponseBody
    public RptIdxMeasure show(@PathVariable("id") String id){
        return idxMeasureBS.getEntityById(id);
    }

    //删除某些度量信息
    @RequestMapping(value = "/id",method = RequestMethod.POST)
    @ResponseBody
    public void destroy(@PathVariable("id") String id){
        idxMeasureBS.removeEntityById(id);
    }

    //删除某些度量信息
    @RequestMapping("/destroyOwn.*")
    @ResponseBody
    public Map<String,String>destroyOwn(String ids){
        Map<String,String> resultMap=Maps.newHashMap();//用于标记删除成功或者删除失败
        if (ids != null && !"".equals(ids)) {
            String[] idArray= StringUtils.split(ids,',');

            this.idxMeasureBS.deleteIdxMeasuresByNos(idArray);
            resultMap.put("status", "success");
            resultMap.put("msg", "删除成功");
            return resultMap;
        }else{
            resultMap.put("status", "fail");
            resultMap.put("msg", "没有选择需要删除的内容");
            return resultMap;
        }

    }

    @RequestMapping("testMeasureNo")
    @ResponseBody
    public boolean testMeasureNo(String measureNo){
        return this.idxMeasureBS.checkIsMeasureNoExi(measureNo);
    }

    //添加
    @RequestMapping(method = RequestMethod.POST)
    public void create(RptIdxMeasure model){
        idxMeasureBS.updateEntity(model);
    }
}


