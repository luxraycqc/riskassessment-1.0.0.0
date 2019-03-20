package org.mark.lib.utils;

import java.io.File;
import java.io.IOException;

import org.springframework.util.ResourceUtils;

public final class LibTool {
    public static File getDataFile(final String path) {
    	File f = null;
		try {
			f = ResourceUtils.getFile(path);
		} catch (IOException e) {
			e.printStackTrace();
		}
    	return f;
    }
    
    public static String getDataFilePath(final String path) {
    	return getDataFile(path).getAbsolutePath();
    }
    
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
