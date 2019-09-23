package com.lin.dhjar.plugin

import com.android.tools.r8.code.S
import com.google.common.io.ByteStreams
import com.google.common.io.Files
import javassist.CannotCompileException
import javassist.ClassPool
import javassist.CtClass
import javassist.CtField
import javassist.CtMethod
import javassist.CtNewMethod
import javassist.Modifier
import org.apache.commons.io.FileUtils
import org.bouncycastle.math.raw.Mod

import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream


class JavassistInject {
    private static Map<String, Class> map = new HashMap<>()


    private static Class getAnnotationClass(String className, ClassPool mClassPool) {
        if (!map.containsKey(className)) {
            CtClass mCtClass = mClassPool.getCtClass(className)
            if (mCtClass.isFrozen()) {
                mCtClass.defrost()
            }
            map.put(className, mCtClass.toClass())
            mCtClass.detach()
        }
        return map.get(className)
    }


    static void injectDir(String inputPath, String outPutPath, ClassPool mClassPool,LJarConfig lJarConfig) {
        File dir = new File(inputPath)
        if (dir.isDirectory()) {
            dir.eachFileRecurse { File file ->
                String filePath = file.absolutePath
                if (file.isFile()) {
                    File outPutFile = new File(outPutPath + filePath.substring(inputPath.length()))
                    Files.createParentDirs(outPutFile)
                    if (filePath.endsWith(".class")
                            && !filePath.contains('R$')
                            && !filePath.contains('R.class')
                            && !filePath.contains("DataBinder")
                            && !filePath.contains("databinding")
                            && !filePath.contains("BuildConfig.class")) {
                        FileInputStream inputStream = new FileInputStream(file)
                        FileOutputStream outputStream = new FileOutputStream(outPutFile)
                        System.out.println("out put ==========="+outPutFile.getAbsolutePath())
                        transform(inputStream, outputStream, mClassPool,lJarConfig)
                    } else {
                        FileUtils.copyFile(file, outPutFile)
                    }
                }
            }
        }
    }

    static void injectJar(File inputFile, File outFile, ClassPool mClassPool,LJarConfig lJarConfig) throws IOException {
        ArrayList entries = new ArrayList()
        Files.createParentDirs(outFile)
        FileInputStream fis = null
        ZipInputStream zis = null
        FileOutputStream fos = null
        ZipOutputStream zos = null
        try {
            fis = new FileInputStream(inputFile)
            zis = new ZipInputStream(fis)
            fos = new FileOutputStream(outFile)
            zos = new ZipOutputStream(fos)
            ZipEntry entry = zis.getNextEntry()
            while (entry != null) {
                String fileName = entry.getName()
                if (!entries.contains(fileName)) {
                    entries.add(fileName)
                    zos.putNextEntry(new ZipEntry(fileName))
                    if (!entry.isDirectory() && fileName.endsWith(".class")
                            && !fileName.contains('R$')
                            && !fileName.contains('R.class')
                            && !fileName.contains("BuildConfig.class"))
                        transform(zis, zos, mClassPool,lJarConfig)
                    else {
                        ByteStreams.copy(zis, zos)
                    }
                }
                entry = zis.getNextEntry()
            }
        } catch (Exception e) {
            e.printStackTrace()
        } finally {
            if (zos != null)
                zos.close()
            if (fos != null)
                fos.close()
            if (zis != null)
                zis.close()
            if (fis != null)
                fis.close()
        }
    }


    static void transform(InputStream input, OutputStream out, ClassPool mClassPool,LJarConfig lJarConfig) {
        try {
            CtClass c = mClassPool.makeClass(input)
            transformClass(c, mClassPool,lJarConfig)
            out.write(c.toBytecode())
            c.detach()
        } catch (Exception e) {
            e.printStackTrace()
            input.close()
            out.close()
            throw new RuntimeException(e.getMessage())
        }
    }

    private static void transformClass(CtClass c, ClassPool mClassPool,LJarConfig lJarConfig) {
//        Set<String> keys = lJarConfig.cutList.keySet()
//        for(String s: keys){
//            if(c.getName().startsWith(s)){
//                modify(c,mClassPool,lJarConfig.cutList.get(s))
//                break;
//            }
//        }
        logMethodTime(c,mClassPool,lJarConfig)
    }
    private static void  logMethodTime(CtClass c, ClassPool mClassPool,LJarConfig lJarConfig) {
        if (c.isFrozen()) {
            c.defrost()
        }
        if(c.isInterface())
            return
        if(c.getName().contains("\$"))
            return
//        if(!c.getName().contains("MainActivity"))
//            return

        System.out.println(c.getName()+"===================="+c.isFrozen())
        int l = c.getDeclaredMethods().length
        Set<String> currentMethod = new HashSet<String>()
        for(int i=0;i<l;i++){
            CtMethod  m = c.getDeclaredMethods()[i]

                if(m.isEmpty())
                    continue
                if(m.getName().contains("\$"))
                    continue
                if(m.getModifiers()== 25||m.getModifiers()== 9||m.getModifiers() ==8||m.getModifiers()==24)
                    continue

                String param = "m"+m.getName()+"_"+i+"_"+System.nanoTime()%1000
                if(currentMethod.contains(param)){
                    param = param+"a1"
                }
                currentMethod.add(param)
                CtField ctField = new CtField(CtClass.longType,param,c)
                ctField.setModifiers(9)
                c.addField(ctField,"0l")


               String localName = param+"_local"

                m.addLocalVariable(localName,CtClass.longType)


                m.insertBefore(param+" =  System.currentTimeMillis();")
                String userTime =" System.currentTimeMillis() - "+param +""
                String line = "  System.out.println(\""+c.getName()+"::::"+m.getMethodInfo().getName()+"=======\"+("+userTime+"));"
                try{
                    int lineNum = 0;
                    if(m.getReturnType().getName().contains("void")) {
                         lineNum =  m.getMethodInfo().getLineNumber(m.getMethodInfo().codeAttribute.length())
                    }else {
                         lineNum =  m.getMethodInfo().getLineNumber(m.getMethodInfo().codeAttribute.length()) -1
                    }

                    if(lineNum>0){
                        String resultTime = " if (" + userTime + " >=" + lJarConfig.logMinTime + ")" + line
//                        System.out.println(resultTime)
                        m.insertAt(lineNum ,resultTime)
                    }

                }catch(CannotCompileException ex){
//                    ex.printStackTrace()
                    System.out.println(param+" get Error")
                }

//                m.insertAfter( "System.out.println("+param+");")
//                m.insertAfter()
        }

    }

    private static void  modify(CtClass c, ClassPool mClassPool,List<String> methods) {
        if (c.isFrozen()) {
            c.defrost()
        }
        System.out.println("find class==============="+c.getName())
        for(String method : methods){
            CtMethod ctMethod = c.getDeclaredMethod(method)
            String method2 = method+"DhCut"
            CtMethod ctMethod2 = CtNewMethod.copy(ctMethod,method2,c,null)
            c.addMethod(ctMethod2)
            int methodLen = ctMethod.getParameterTypes().length
            StringBuffer sb  = new StringBuffer()
            sb.append("{try{")
            if(!ctMethod.getReturnType().getName().contains("void")){
                sb.append("return ")
            }
            sb.append(method2)
            sb.append("(")
            for(int i = 0; i<methodLen;i++){
                sb.append("\$"+(i+1))
                if(i!=methodLen-1){
                    sb.append(",")
                }
            }
            sb.append(");}catch(Exception ex){ System.out.println(ex.toString());ex.printStackTrace();}")
            if(!ctMethod.getReturnType().getName().contains("void")){
                sb.append("return ")
                String result = getReturnValue(ctMethod.getReturnType().getName())
                sb.append(result)
                sb.append(";")
            }
            sb.append("}")
           System.out.println("return type  =======" +ctMethod.getReturnType().getName())
            ctMethod.setBody(sb.toString())
        }
    }
    private static boolean checkMethod(int modifiers) {
        return !Modifier.isStatic(modifiers) && !Modifier.isNative(modifiers) && !Modifier.isAbstract(modifiers) && !Modifier.isEnum(modifiers) && !Modifier.isInterface(modifiers)
    }



    static  String getReturnValue(String type){
        String result = "null"
        switch (type){
            case "int":
                result = "0"
                break;
            case "long":
                result = "0l"
                break;
            case "double":
                result = "0d"
                break;
            case "float":
                result = "0f"
                break;
            case "boolean":
                result = "true"
                break;
            case "char":
                result = "\'a\'"
                break;
            case "short":
                result = "0"
                break;
            case "byte":
                result = "0"
                break;
            default:
                break;
        }
        return result
    }
}
