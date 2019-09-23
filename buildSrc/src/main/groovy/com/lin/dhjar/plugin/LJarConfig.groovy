package com.lin.dhjar.plugin


class LJarConfig {
    String  calculate = "default" //close debug default

    long  logMinTime = 0l
    /**
     * 需要 拦截的jar文件
     * @param filters
     * @return
     */
    LJarConfig ctype(String value) {
        if (value != null) {
            calculate = value;
        }
        return this
    }

    LJarConfig minTime(long time){
        if(time>0){
            logMinTime = time
        }
        return this
    }
}