# DhMethodTime

## 简介

DhMethodTime 是一个再debug下统计所有安卓方法耗时的插件，引用方便,利用javassist在编译时完成统计代码注入，realase版本下不会执行注入， 方便开发时测试，分析方法耗时，性能

An android gradle plugin for calculating time of all Android methods

##  怎么使用
Gradle->buildsrc->upload->uploadArchive 

mkdir in app/plugins.  copy app/repo/.../dhmtime-1.0.0.jar to plugins/
```python
    repositories {
        google()
        jcenter()
        flatDir name: 'localRepository', dir: './app/plugins'
    }
```
在项目build.gradle 加入
```python
classpath 'com.lin.dhmtime:dhmtime:1.0.0'
classpath 'org.javassist:javassist:3.25.0-GA'
```
App build.gradle 
```python

apply plugin: "dhmtime"
...

...
dhMCConfig {
    logMinTime(50l)//log method will more than 50 millseconds
}
```
Log filter ::::::::
![image](https://user-images.githubusercontent.com/4986104/113126882-b7928480-924a-11eb-9ff0-7ed3447a357a.png)




### 参考
* [Javassist 介绍](http://www.javassist.org/tutorial/tutorial.html)
* [插件介绍](https://www.cnblogs.com/dikeboy/p/11570076.html)
