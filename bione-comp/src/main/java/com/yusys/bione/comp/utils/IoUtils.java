package com.yusys.bione.comp.utils;

import java.io.Closeable;
import java.io.IOException;

public class IoUtils {

    public static void closeQuietly(Closeable closeable) {
        try {
            if (closeable != null) {
                closeable.close();
            }
        } catch (IOException ioe) {
            // ignore
        }
    }
	
}
