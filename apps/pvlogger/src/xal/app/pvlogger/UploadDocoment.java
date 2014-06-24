/*
 * PVLoggerDocument.java
 *
 * Created on Wed Dec 3 15:00:00 EDT 2003
 *
 * Copyright (c) 2003 Spallation Neutron Source
 * Oak Ridge National Laboratory
 * Oak Ridge, TN 37830
 */

package xal.app.pvlogger;

import xal.extension.application.XalDocument;
import xal.extension.application.smf.AcceleratorDocument;
import xal.tools.database.*;
import xal.tools.apputils.PathPreferenceSelector;

import java.net.URL;


/**
 *
 * @author  lv
 * @author  chu
 */
public class UploadDocoment extends AcceleratorDocument {

	@Override
	protected void makeMainWindow() {
		mainWindow=new UploadWindow(this);		
	}

	@Override
	public void saveDocumentAs(URL url) {
		// TODO Auto-generated method stub
		
	}
	
	public xal.smf.Accelerator applySelectedAcceleratorWithDefaultPath( final String filePath ) {
		return null;
	}
	

}




