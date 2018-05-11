package com.gk.htc.ahp.brand.bootstrap;

import java.util.*;
import java.io.*;

public abstract class DirWatcher extends TimerTask {

    private String path;
    private File filesArray[];
    private final HashMap dir = new HashMap();
    private DirFilterWatcher dfw;

    public DirWatcher(String path) {
        this(path, "");
    }

    public DirWatcher(String path, String filter) {
        this.path = path;
        dfw = new DirFilterWatcher(filter);
        filesArray = new File(path).listFiles(dfw);

        for (File oneFile : filesArray) {
            dir.put(oneFile, oneFile.lastModified());
        }
    }

    @Override
    public final void run() {
        HashSet checkedFiles = new HashSet();
        filesArray = new File(path).listFiles(dfw);

        for (File oneFile : filesArray) {
            Long current = (Long) dir.get(oneFile);
            checkedFiles.add(oneFile);
            if (current == null) {
                // new file
                dir.put(oneFile, oneFile.lastModified());
                onChange(oneFile, "add");
            } else if (current != oneFile.lastModified()) {
                // modified file
                dir.put(oneFile, oneFile.lastModified());
                onChange(oneFile, "modify");
            }
        }

        // now check for deleted files
        Set ref = ((HashMap) dir.clone()).keySet();
        ref.removeAll((Set) checkedFiles);
        Iterator it = ref.iterator();
        while (it.hasNext()) {
            File deletedFile = (File) it.next();
            dir.remove(deletedFile);
            onChange(deletedFile, "delete");
        }
    }

    protected abstract void onChange(File file, String action);
}
