package com.example.dhjarfix

import java.util.regex.Matcher
import java.util.regex.Pattern
import org.gradle.api.Project

class LJarUtils{
  static String  getCurrentFlavor(Project project) {
   def gradle = project.getGradle()
   String tskReqStr = gradle.getStartParameter().getTaskRequests().toString()
   Pattern pattern;
   if (tskReqStr.contains("assemble"))
    pattern = Pattern.compile("assemble(\\w*)(Release|Debug)")
   else
    pattern = Pattern.compile("generate(\\w*)(Release|Debug)")
   Matcher matcher = pattern.matcher(tskReqStr)
   if (matcher.find()){
    System.out.println("find")
     String flavtor =     matcher.group(1)
     if(tskReqStr.contains(flavtor+"Debug"))
        return  flavtor +"debug"
      else
       return  flavtor +"release"
   }

   else {
    println "NO MATCH FOUND"
    return "";
   }
  }

 // aspectj 扫包注入 同时实现kotlin hook
 static String  getVariantName( variant) {
  def name = ""
  for(int i=0;i<variant.productFlavors.size();i++){
   name = name +variant.productFlavors[i].name
  }
  return name
 }
}