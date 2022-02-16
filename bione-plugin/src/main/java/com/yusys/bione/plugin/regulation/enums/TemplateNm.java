package com.yusys.bione.plugin.regulation.enums;

import org.apache.commons.lang3.StringUtils;

public enum TemplateNm {
    ADD,UPDATE,NONE,STOP,ERROR,BLANK;
    public static TemplateNm get(String name){
        name = StringUtils.trimToEmpty(name);
        if ("01".equals(name) || "新增".equals(name)) {
            return ADD;
        }
        if ("02".equals(name) || "修改".equals(name)) {
            return UPDATE;
        }
        if ("03".equals(name) || "停用".equals(name)) {
            return STOP;
        }
        if ("04".equals(name) || "无变化".equals(name)) {
            return NONE;
        }
        if (StringUtils.isEmpty(name)){
            return BLANK;
        }
        return ERROR;
    }


    @Override
    public String toString() {
        switch (this) {
            case ADD:
                return "01";
            case UPDATE:
                return "02";
            case STOP:
                return "03";
            case NONE:
                return "04";
            case ERROR:
                return "ERROR";
            default:
                return "";
        }
    }

    public boolean valid(){
        switch (this) {
            case ADD:
            case UPDATE:
            case STOP:
            case NONE:
            case BLANK:
                return true;
            case ERROR:
            default:
                return false;
        }
    }
}
