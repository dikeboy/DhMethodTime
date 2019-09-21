package com.lin.dhjar.plugin


class LJarConfig {
    List<String> jarPath = new ArrayList<>()
    Map<String,List<String>> cutList = new HashMap<String,List<String>>()

    /**
     * 需要 拦截的jar文件
     * @param filters
     * @return
     */
    LJarConfig jarFiles(String...filters) {
        if (filters != null) {
            this.jarPath.addAll(filters)
        }
        return this
    }

    /**
     * 需要处理的class和方法
     * @param filters
     * @return
     */
    LJarConfig cutFiles(String...filters) {
        if (filters != null) {
            int len = filters.length
            for(int j=0;j<len;j++){
                String[] str = filters[j].split(":")
                if(str.length>1){
                    List<String> list = new ArrayList<String>()
                    for(int i=1;i<str.length;i++){
                        list.add(str[i])
                    }
                    cutList.put(str[0],list)
                }
            }

        }
        return this
    }


}