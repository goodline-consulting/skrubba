package se.goodline.skrubba.utils;

import java.io.File;
import java.util.Arrays;

import se.goodline.skrubba.repository.ParamRepository;

public class FileUtils 
{
	 
	 public static String[] scanBilagorFolder(String folderName) 
	 {
	        File folder = new File(folderName);
	        File[] files = folder.listFiles();

	        if (files != null) {
	            return Arrays.stream(files)
	                    .filter(File::isFile)
	                    .map(File::getName)
	                    .toArray(String[]::new);
	        }
	        return new String[0];
	  }			
}
