package com.yusys.bione.comp.common;

public class DataSourceTypeManager {

    private static final ThreadLocal<DataSourceType> dataSourceTypes = new ThreadLocal<DataSourceType>(){
        @Override
        protected DataSourceType initialValue(){
            return getDefault();
        }
    };
    
    public static DataSourceType get(){
        return dataSourceTypes.get();
    }
    
    public static DataSourceType getDefault(){
        return DataSourceType.DATA_SOURCE_1;
    }
    
    public static void set(DataSourceType dataSourceType){
        dataSourceTypes.set(dataSourceType);
    }
    
    public static void reset(){
        dataSourceTypes.set(getDefault());
    }
}
