package com.lin.dhjar.plugin

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
                        transformFile(inputStream, outputStream, mClassPool,lJarConfig)
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


    static void transformFile(InputStream input, OutputStream out, ClassPool mClassPool,LJarConfig lJarConfig) {
        try {
            CtClass c = mClassPool.makeClass(input)
            transformClass(c, mClassPool,lJarConfig)
            out.write(c.toBytecode())
            c.detach()
            c.freeze()
        } catch (Exception e) {
            e.printStackTrace()
            throw new RuntimeException(e.getMessage())
        }finally{
            if(input!=null){
                input.close()
            }
            if(out!=null){
                out.close()
            }
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
            out.close()
            input.close()
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
        if(c.isInterface()||c.isEnum())
            return
        if(c.isEnum()){
            return;
        }
//        if(c.getName().contains("\$"))
//            return

        if(lJarConfig.include.size()>0){
            boolean  has = false;
            for(String s : lJarConfig.include){
                if(c.getName().contains(s)){
                    has=true;
                    break;
                }
            }
            if(!has){
                return;
            }
        }
        if(lJarConfig.exclude.size()>0){
            for(String s : lJarConfig.exclude){
                if(c.getName().contains(s))
                    return
            }
        }

        int l = c.getDeclaredMethods().length
        Set<String> currentMethod = new HashSet<String>()
        for(int i=0;i<l;i++){
            CtMethod  m = c.getDeclaredMethods()[i]
                if(m.isEmpty())
                    continue
                if(m.getName().contains("\$"))
                    continue
//                if(m.getModifiers()== 25||m.getModifiers()==24)
//                    continue

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
            try{
                m.insertBefore(param+" =  System.currentTimeMillis();")
                String userTime =" System.currentTimeMillis() - "+param +""
                String log = lJarConfig.logType==null?LJarConfig.LOG_TYPE_LOG:lJarConfig.logType
                String line = log+"(\""+lJarConfig.logFilter+"\",\""+c.getName()+"::::"+m.getMethodInfo().getName()+"====\"+("+userTime+"));"
//                String line = "  System.out.println(\""+lJarConfig.logFilter+c.getName()+"::::"+m.getMethodInfo().getName()+"====\"+("+userTime+"));"
                    int lineNum = 0;
                    if(m.getReturnType().getName().contains("void")) {
                         lineNum =  m.getMethodInfo().getLineNumber(m.getMethodInfo().codeAttribute.length())
                    }else {
                         lineNum =  m.getMethodInfo().getLineNumber(m.getMethodInfo().codeAttribute.length()) -1
                    }
                    if(lineNum>=0){
                        String resultTime = " if (" + userTime + " >=" + lJarConfig.logMinTime + ")" + line
                        m.insertAfter(resultTime)
                    }

                }catch(CannotCompileException ex){
//                    ex.printStackTrace()
                    System.out.println(param+" get Error")
                }

//                m.insertAfter( "System.out.println("+param+");")
//                m.insertAfter()
        }

    }


}
