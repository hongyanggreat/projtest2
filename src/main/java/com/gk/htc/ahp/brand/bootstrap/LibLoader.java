package com.gk.htc.ahp.brand.bootstrap;

//
//This file is part of the OpenNMS(R) Application.
//
//OpenNMS(R) is Copyright (C) 2006 The OpenNMS Group, Inc.  All rights reserved.
//OpenNMS(R) is a derivative work, containing both original code, included code and modified
//code that was published under the GNU General Public License. Copyrights for modified
//and included code are below.
//
//OpenNMS(R) is a registered trademark of The OpenNMS Group, Inc.
//
//Original code base Copyright (C) 1999-2001 Oculan Corp.  All rights reserved.
//
//This program is free software; you can redistribute it and/or modify
//it under the terms of the GNU General Public License as published by
//the Free Software Foundation; either version 2 of the License, or
//(at your option) any later version.
//
//This program is distributed in the hope that it will be useful,
//but WITHOUT ANY WARRANTY; without even the implied warranty of
//MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//GNU General Public License for more details.
//
//You should have received a copy of the GNU General Public License
//along with this program; if not, write to the Free Software
//Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA.
//
//For more information contact:
//   OpenNMS Licensing       <license@opennms.org>
//   http://www.opennms.org/
//   http://www.opennms.com/
//
import java.io.File;
import java.io.FileFilter;

import java.io.FilenameFilter;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

import java.util.LinkedList;
import java.util.StringTokenizer;

/**
 * Bootstrap application for starting OpenNMS.
 */
public class LibLoader {

    /**
     * Matches any file that is a directory.
     */
    private static FileFilter m_dirFilter = new FileFilter() {
        @Override
        public boolean accept(File pathname) {
            return pathname.isDirectory();
        }
    };
    /**
     * Matches any file that has a name ending in ".jar".
     */
    private static final FilenameFilter m_jarFilter = new FilenameFilter() {
        @Override
        public boolean accept(File dir, String name) {
            return name.endsWith(".jar");
        }
    };

    /**
     * Create a ClassLoader with the JARs found in dirStr.
     *
     * @param dirStr List of directories to search for JARs, separated by
     * {@link java.io.File#pathSeparator File.pathSeparator}
     * @param recursive Whether to recurse into subdirectories of the
     * directories in dirStr
     * @return
     * @throws java.net.MalformedURLException
     * @returns A new ClassLoader containing the found JARs
     */
    public static ClassLoader loadClasses(String dirStr, boolean recursive) throws MalformedURLException {
        LinkedList<URL> urls = new LinkedList<>();
        StringTokenizer toke = new StringTokenizer(dirStr, File.pathSeparator);
        while (toke.hasMoreTokens()) {
            // System.out.println("nnnn");
            String token = toke.nextToken();
            // System.out.println("Token:" + token);
            loadClasses(new File(token), recursive, urls);
        }
        return newClassLoader(urls);
    }

    /**
     * Create a ClassLoader with the JARs found in dir.
     *
     * @param dir Directory to search for JARs
     * @param recursive Whether to recurse into subdirectories of dir
     * @return
     * @throws java.net.MalformedURLException
     * @returns A new ClassLoader containing the found JARs
     */
    public static ClassLoader loadClasses(File dir, boolean recursive)
            throws MalformedURLException {
        LinkedList<URL> urls = new LinkedList<>();
        loadClasses(dir, recursive, urls);
        return newClassLoader(urls);
    }

    /**
     * Create a ClassLoader with the list of URLs found in urls.
     *
     * @param urls List of URLs to add to the ClassLoader's search list.
     * @return
     * @returns A new ClassLoader with the specified search list
     */
    public static ClassLoader newClassLoader(LinkedList<URL> urls) {
        URL[] urlsArray = urls.toArray(new URL[0]);
        return URLClassLoader.newInstance(urlsArray);
    }

    /**
     * Add JARs found in dir to the LinkedList urls.
     *
     * @param dir Directory to search for JARs
     * @param recursive Whether to recurse into subdirectories of the directory
     * in dir
     * @param urls LinkedList to append found JARs onto
     * @throws java.net.MalformedURLException
     */
    public static void loadClasses(File dir, boolean recursive, LinkedList<URL> urls) throws MalformedURLException {
        // Add the directory
        urls.add(dir.toURL());
//        System.out.println("parsing jar file:" + dir.toURL());
//        System.out.println("Recursive:" + recursive);
        if (recursive) {
            // Descend into sub-directories
            File[] dirlist = dir.listFiles(m_dirFilter);
            if (dirlist != null) {
                for (File childDir : dirlist) {
                    loadClasses(childDir, recursive, urls);
                }
            }
        }

        // Add individual JAR files
        File[] children = dir.listFiles(m_jarFilter);
        System.out.println("Dir:" + dir.getPath());
        if (children != null) {
            for (File childFile : children) {
                System.out.println("Load jar file:" + childFile.getName());
                System.out.println(childFile.toURL());
                urls.add(childFile.toURL());
            }
        }
    }

    public static File findBootstrapHome() {
        ClassLoader l = Thread.currentThread().getContextClassLoader();
        try {
            String classFile = LibLoader.class.getName().replace('.', '/') + ".class";
            // System.out.println("Class File:" + classFile);
            URL url = l.getResource(classFile);
            /*
             System.out.println(url);
             System.out.println(url.getProtocol()); */
            if (url.getProtocol().equals("jar")) {
                URL subUrl = new URL(url.getFile());
                if (subUrl.getProtocol().equals("file")) {
                    String filePath = subUrl.getFile();
                    int i = filePath.lastIndexOf('!');
                    File file = new File(filePath.substring(0, i));
                    return file.getParentFile();
                    // return file.getParentFile().getParentFile();
                }
            }
        } catch (MalformedURLException e) {
            return null;
        }
        return null;
    }
}
