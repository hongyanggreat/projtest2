package com.gk.htc.ahp.brand.bootstrap;

import com.gk.htc.ahp.brand.common.Tool;
import java.io.File;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;

public class Bootstrap {

    ClassLoader cl;
    Class<?> mainClass;

    public Bootstrap() {
    }

    public static void main(String[] args) throws Exception {
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.start(args);
    }

    public void start(String[] args) throws Exception {
        this.cl = this.loadLib();
        if (cl != null) {
            final String classToExec = "com.gk.htc.ahp.brand.app.AppStart";
            final String methodExec = "main";
            final String[] argsExec = args;
            //-
            final String className = classToExec;
            final Class<?>[] classes = new Class[]{argsExec.getClass()};
            final Object[] methodArgs = new Object[]{argsExec};
            mainClass = cl.loadClass(className);
            final Method method = mainClass.getMethod(methodExec, classes);
            Runnable execer = () -> {
                try {
                    method.invoke(null, methodArgs);
                } catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException e) {
                    Tool.debug(Tool.getLogMessage(e));
                    System.exit(1);
                }
            };
            Thread bootstrapper = new Thread(execer, "main");
            bootstrapper.setContextClassLoader(cl);
            bootstrapper.start();
        }
        File parent = LibLoader.findBootstrapHome();
        String bundle_dir = parent.getParentFile().getPath() + File.separator + "bundles";
        System.out.println("bundle_dir :" + bundle_dir);
    }

    public ClassLoader loadLib() {
        File parent = LibLoader.findBootstrapHome();
        if (parent != null) {
            System.out.println("Parent Path:" + parent.getPath());
            String lib_dir = parent.getPath() + File.pathSeparator
                    + parent.getParentFile().getPath() + File.separator + "bundles";
//        lib_dir = lib_dir + File.pathSeparator + parent.getParentFile().getPath() + File.separator + "config";
            System.out.println("lib Path:" + lib_dir);
            System.out.println("************AAA*****************");
            try {
                ClassLoader _cl = LibLoader.loadClasses(lib_dir, false);
                return _cl;
            } catch (MalformedURLException e) {
                Tool.debug(Tool.getLogMessage(e));
                return null;
            }
        } else {
            return null;
        }
    }
}
