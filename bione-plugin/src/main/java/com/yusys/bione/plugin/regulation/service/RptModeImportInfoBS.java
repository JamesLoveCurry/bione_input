package com.yusys.bione.plugin.regulation.service;

import com.yusys.bione.plugin.regulation.repository.RptModelImportDao;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class RptModeImportInfoBS {

    @Autowired
    RptModelImportDao rptModelImportDao;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void insertImportDesignInfoLog(int sort, String uuid, String content) {
        if (StringUtils.isNotEmpty(uuid)) {
            Map<String, Object> params = new HashMap<>();
            params.put("id", uuid);
            params.put("sortNo", sort);
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            dateFormat.format(new Date());
            params.put("content", "<li>" + Timestamp.valueOf(dateFormat.format(new Date())) + " - " + content + "</li>");
            rptModelImportDao.insertImportDesignInfoLog(params);
        }
    }

    public int getTotalImportDesignInfoLogCount() {
        return rptModelImportDao.getTotalImportDesignInfoLogCount();
    }

    public void cleanImportDesignInfoLog() {
        rptModelImportDao.cleanImportDesignInfoLog();
    }
}
