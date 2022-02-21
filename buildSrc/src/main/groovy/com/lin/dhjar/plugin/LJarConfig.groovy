package com.lin.dhjar.plugin


class LJarConfig {
    List<String> exclude = new  ArrayList<String>() //exclude class like
    List<String> include =new ArrayList<String>();
    long  logMinTime = 0l
    String logFilter = "[tencentCloudGame] "
    public static String LOG_TYPE_LOG ="android.util.Log.i"
    String pluginState = "debug"
    String logType =LOG_TYPE_LOG


    /**
     * 需要 拦截的jar文件
     * @param filters
     * @return
     */
    LJarConfig setExclude(String...value) {
        if (value != null) {
            exclude.addAll(value)
        }
        return this
    }

    LJarConfig setInclude(String...value) {
        if (value != null) {
            include.addAll(value)
        }
        return this
    }

    LJarConfig minTime(long time){
        if(time>0){
            logMinTime = time
        }
        return this
    }

    LJarConfig setLogFilter(String mLogFilter){
        if(mLogFilter!=null&&mLogFilter.size()>0){
            logFilter = mLogFilter
        }
        return this
    }

    LJarConfig setLogType(String mLogType){
        if(mLogType!=null&&mLogType.size()>1){
            logType = mLogType
        }
        return this
    }

    LJarConfig setPluginState(String state){
        if(state!=null){
            pluginState = state
        }
        return this
    }
}
