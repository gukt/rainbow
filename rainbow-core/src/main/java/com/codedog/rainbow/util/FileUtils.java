/*
 * Copyright 2018-2021 codedog996.com, The rainbow Project.
 */

package com.codedog.rainbow.util;

import java.io.File;
import java.util.Objects;

/**
 * FileUtils class
 *
 * @author https://github.com/gukt
 */
public class FileUtils {

    /** Prevents to construct an instance. */
    private FileUtils() {
        throw new AssertionError("No FileUtils instances for you!");
    }

    public static boolean ensurePath(File path) {
        if (!path.exists()) {
            return path.mkdirs();
        }
        return true;
    }


    public static String ensureUrlPrefix(String url, boolean sslEnabled) {
        if (!url.startsWith("http")) {
            return (sslEnabled ? "https://" : "http://") + url;
        }
        return url;
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static File ensureParentPath(File file) {
        Objects.requireNonNull(file, "文件不能为null");
        File parent = file.getParentFile();
        if (parent != null && !parent.exists()) {
            parent.mkdirs();
        }
        return file;
    }

    public static String getFileSuffix(String filename) {
        if (filename == null || filename.isEmpty()) {
            return "";
        }
        return filename.substring(filename.lastIndexOf("."));
    }

}
