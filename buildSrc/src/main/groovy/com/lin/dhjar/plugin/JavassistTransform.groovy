package com.lin.dhjar.plugin

import com.android.build.api.transform.*
import com.android.build.gradle.internal.pipeline.TransformManager
import com.android.utils.FileUtils
import javassist.ClassPool
import javassist.JarClassPath
import org.apache.commons.codec.digest.DigestUtils
import org.gradle.api.Project


public class JavassistTransform extends Transform {
    private Project project;
    private LJarConfig lJarConfig
    public JavassistTransform(Project project) {
        this.project = project;
        this.lJarConfig = project.dhMCConfig
        if(lJarConfig==null){
            lJarConfig = new LJarConfig()
        }
    }


    @Override
    public String getName() {
        return "dhmtimePlugin"
    }

    @Override
    public Set<QualifiedContent.ContentType> getInputTypes() {
        return TransformManager.CONTENT_CLASS;
    }


    @Override
    public Set<? super QualifiedContent.Scope> getScopes() {
        Set<QualifiedContent.Scope> sets = new HashSet<QualifiedContent.Scope>()

        sets.add(QualifiedContent.Scope.EXTERNAL_LIBRARIES)
        sets.add(QualifiedContent.Scope.PROJECT)
        sets.add(QualifiedContent.Scope.SUB_PROJECTS)
        return sets;
    }

    @Override
    Set<? super QualifiedContent.Scope> getReferencedScopes() {
        Set<QualifiedContent.Scope> sets = new HashSet<QualifiedContent.Scope>()
        sets.add(QualifiedContent.Scope.EXTERNAL_LIBRARIES)
        sets.add(QualifiedContent.Scope.PROJECT)
        sets.add(QualifiedContent.Scope.SUB_PROJECTS)
        return sets
    }

    @Override
    public boolean isIncremental() {
        return true;
    }

    @Override
    public void transform(TransformInvocation transformInvocation) throws IOException {
        System.out.println("lin transform==="+ transformInvocation.isIncremental())
        project.logger.error("=================DhMTimePluginTransform start1=====================");

        try {
            Collection<TransformInput> inputs = transformInvocation.getInputs();
            TransformOutputProvider outputProvider = transformInvocation.getOutputProvider();
            if(outputProvider==null){
                return
            }
            outputProvider.deleteAll();

            ClassPool mClassPool = new ClassPool(ClassPool.getDefault());
            // 添加android.jar目录
            mClassPool.appendClassPath(project.android.bootClasspath[0].toString());
            mClassPool.importPackage("android.os.Bundle");
            Set<DirectoryInput> classSet = new HashSet<DirectoryInput>()
            Set<JarInput> jarSet =new  HashSet<JarInput>()
            for (TransformInput input : inputs) {
                for (DirectoryInput directoryInput : input.getDirectoryInputs()) {
                    classSet.add(directoryInput)
                    mClassPool.appendClassPath(directoryInput.getFile().getAbsolutePath());
                }
                for (JarInput jarInput : input.getJarInputs()) {
                    jarSet.add(jarInput)
                    mClassPool.appendClassPath(new JarClassPath(jarInput.getFile().getAbsolutePath()));
                }
            }
            System.out.println("class size===="+classSet.size());
            for (DirectoryInput directoryInput : classSet){
                File dest = outputProvider.getContentLocation(directoryInput.getName(),
                        directoryInput.getContentTypes(), directoryInput.getScopes(),
                        Format.DIRECTORY);
//                FileUtils.copyDirectory(directoryInput.getFile(),dest)
                    System.out.println("input class ==="+directoryInput.getFile().getAbsolutePath());
                    JavassistInject.injectDir(directoryInput.getFile().getAbsolutePath(),dest.getAbsolutePath(), mClassPool,lJarConfig);

          }
            for(JarInput jarInput : jarSet){
                String jarName = jarInput.getName();
                String md5Name = DigestUtils.md5Hex(jarInput.getFile().getAbsolutePath());
                if (jarName.endsWith(".jar")) {
                    jarName = jarName.substring(0, jarName.length() - 4);
                }
                System.out.println("input jar==="+jarInput.getFile().getAbsolutePath())
                File dest = outputProvider.getContentLocation(jarName + md5Name,
                        jarInput.getContentTypes(), jarInput.getScopes(), Format.JAR);
                if(jarInput.getScopes().toString().contains("SUB_PROJECTS")){
                    JavassistInject.injectJar(jarInput.getFile(),dest, mClassPool,lJarConfig);
                }
                else{
                    FileUtils.copyFile(jarInput.getFile(),dest)
                }
                System.out.println("output jar==="+dest.getAbsolutePath())
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        project.logger.error("=================DhMTimePluginTransform finish====================");
    }

}