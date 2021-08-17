# DhMethodTime

## What's this

DhMethodTime 是一个再debug下统计所有安卓方法耗时的插件，引用方便,利用javassist在编译时完成统计代码注入，realase版本下不会执行注入， 方便开发时测试，分析方法耗时，性能

An android gradle plugin for calculating time of all Android methods

jcentery  is closed  and it move to local file

## How to use
Gradle->buildsrc->upload->uploadArchive 

mkdir in app/plugins.  copy app/repo/.../dhmtime-1.0.0.jar to plugins/
```python
    repositories {
        google()
        jcenter()
        flatDir name: 'localRepository', dir: './app/plugins'
    }
```
base build.gradle:
```python
classpath 'com.lin.dhmtime:dhmtime:1.0.0'
classpath 'org.javassist:javassist:3.28.0-GA'
```
App build.gradle 
```python

apply plugin: "dhmtime"
...

...

dhMCConfig {
    minTime(0l)   //>= second will log print
    setLogFilter("testlin")  //android log filter name
    setLogEngine("android.util.Log.e") // default android.util.Log.i  you can use your self class
}
```
Log print

 ![Imag](https://github.com/dikeboy/DhMethodTime/blob/master/image/img.png)



### 参考
* [Javassist 介绍](http://www.javassist.org/tutorial/tutorial.html)
* [插件介绍](https://www.cnblogs.com/dikeboy/p/11570076.html)
