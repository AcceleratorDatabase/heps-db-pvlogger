package xal.app.pvlogger.upload;

import java.io.File;

import javax.swing.filechooser.FileFilter;
/**
* @author  lv
* @author  chu
*/
public class MyFileFilter extends FileFilter{

	private String ext;
	
	public MyFileFilter(String extString){
		this.ext=extString;
	}
	@Override
	public boolean accept(File f) {
		if(f.isDirectory()){
			return true;
		}
		String extension=this.getExtension(f);
		if(extension.toLowerCase().equals(this.ext.toLowerCase())){
			return true;
		}
		return false;
	}

	@Override
	public String getDescription() {
		return this.ext.toUpperCase();
	}
	
	 private String getExtension(File f) {  
	        String name = f.getName();  
	        int index = name.lastIndexOf('.');  
	  
	        if (index == -1)  
	        {  
	            return "";  
	        }  
	        else  
	        {  
	            return name.substring(index + 1).toLowerCase();  
	        }  
	    }  

	
}
