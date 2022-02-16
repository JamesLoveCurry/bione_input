package com.yusys.bione.plugin.detail.download.controller;

import com.yusys.bione.comp.utils.DownloadUtils;
import com.yusys.bione.frame.base.web.BaseController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;

/**
 * <pre>
 * Title:报表填报数据接口
 * Description:【主要包含导入导出等功能】
 * </pre>
 *
 * @author bfk
 * @version 1.00.00
 * @date 2021/06/08
 */
@Controller
@RequestMapping("/detail/download/data")
public class DetailDownloadController extends BaseController {

    /**
     *
     * <pre>
     * Title: 导出 删除源文件和外层文件夹
     * </pre>
     * @author miaokx
     * @version 1.00.00
     * @date 17:53 2020/11/11
     */
    @GetMapping("/downloadData")
    public void downloadData(HttpServletRequest request, HttpServletResponse response,
                             String filepath) throws Exception {
        File file=new File(filepath);
        DownloadUtils.download(response, file);
        file.delete();
        // 根据path获取上一层，删除上一层
        String directory = filepath.substring(0, filepath.lastIndexOf("/"));
        File fileDir = new File(directory);
        fileDir.delete();
    }
    /**
     *
     * <pre>
     * Title: 导出不删除源文件和外层文件夹
     * </pre>
     * @author miaokx
     * @version 1.00.00
     * @date 17:53 2020/11/11
     */
    @GetMapping("/downloadDataNoDel")
    public void downloadDataNoDel(HttpServletRequest request, HttpServletResponse response,
                             String filepath) throws Exception {
        File file=new File(filepath);
        DownloadUtils.download(response, file);
    }
}
