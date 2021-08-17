package com.lin.dhjar.plugin

import com.android.build.api.transform.*
import com.android.build.gradle.internal.pipeline.TransformManager
import com.android.utils.FileUtils
import javassist.ClassPath
import javassist.ClassPool
import javassist.LoaderClassPath
import org.apache.commons.codec.digest.DigestUtils
import org.gradle.api.Project
import org.gradle.internal.classloader.ClassLoaderUtils
import org.gradle.internal.classloader.VisitableURLClassLoader


public class JavassistTransform extends Transform {
    private Project project;
    private LJarConfig lJarConfig
    private long  startTime = 0;
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
        startTime = System.currentTimeMillis();
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
            Set<ClassPath> cachePath = new HashSet<ClassPath>()
            VisitableURLClassLoader jarLoader;
            URL[] urls;
            for (TransformInput input : inputs) {
                for (DirectoryInput directoryInput : input.getDirectoryInputs()) {
                    classSet.add(directoryInput)
                    cachePath.add(mClassPool.appendClassPath(directoryInput.getFile().getAbsolutePath()));
                }
                urls = new URL[input.getJarInputs().size()];
                int len = input.getJarInputs().size();
                ClassLoader parent;
                for (int i=0;i<len;i++) {
                    JarInput jarInput = input.getJarInputs().get(i);
                    jarSet.add(jarInput)
                    urls[i] = jarInput.getFile().toURI().toURL()
                }
                parent = mClassPool.getClassLoader().getParent()
                jarLoader= new VisitableURLClassLoader("third jar",urls,parent);
                mClassPool.appendClassPath(new LoaderClassPath(jarLoader))
            }
            System.out.println("class size===="+classSet.size());
            for (DirectoryInput directoryInput : classSet){
                File dest = outputProvider.getContentLocation(directoryInput.getName(),
                        directoryInput.getContentTypes(), directoryInput.getScopes(),
                        Format.DIRECTORY);
//                FileUtils.copyDirectory(directoryInput.getFile(),dest)
                    System.out.println("input class ==="+directoryInput.getFile().getAbsolutePath());
                    JavassistInject.injectDir(directoryInput.getFile().getAbsolutePath(),dest.getAbsolutePath(), mClassPool,lJarConfig);
//                processDirectoryInputs(directoryInput,outputProvider)
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
            ClassLoaderUtils.tryClose(jarLoader)

        } catch (Exception e) {
            e.printStackTrace();
        }
        project.logger.error("=================DhMTimePluginTransform finish(total "+(System.currentTimeMillis()-startTime)/1000+"====================");
    }

    void processDirectoryInputs(DirectoryInput directoryInput, TransformOutputProvider outputProvider) {
        File dest = outputProvider.getContentLocation(directoryInput.getName(),
                directoryInput.getContentTypes(), directoryInput.getScopes(),
                Format.DIRECTORY)
        // 建立文件夹
        FileUtils.forceMkdir(dest)

        // to do some transform

        // 将修改过的字节码copy到dest，就可以实现编译期间干预字节码的目的了
        FileUtils.copyDirectory(directoryInput.getFile(), dest)
    }

}