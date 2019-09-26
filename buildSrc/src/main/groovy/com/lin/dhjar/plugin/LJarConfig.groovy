package com.lin.dhjar.plugin


class LJarConfig {
    List<String> exclude = new  ArrayList<String>() //exclude class like
    long  logMinTime = 0l
    /**
     * 需要 拦截的jar文件
     * @param filters
     * @return
     */
    LJarConfig getExclude(String...value) {
        if (value != null) {
            exclude.addAll(value)
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