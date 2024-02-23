package se.goodline.skrubba.utils;

import java.io.File;
import java.util.Arrays;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;


public class FileUtils 
{
	 
	 public  String[] scanBilagorFolder(String folderName) 
	 {
		 
		 	Resource resource = new ClassPathResource(folderName);
		 	File folder = null;
	        // Get the file from the resource
	        
			try 
			{
				folder = resource.getFile();
				//System.out.println(folder.getAbsolutePath());
			} 
			catch (Exception e) 
			{
				
				e.printStackTrace();
			}
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
