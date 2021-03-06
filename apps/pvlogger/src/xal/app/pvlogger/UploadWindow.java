/*
 * BrowserWindow.java
 *
 * Created on Thu Mar 25 08:58:58 EST 2004
 *
 * Copyright (c) 2004 Spallation Neutron Source
 * Oak Ridge National Laboratory
 * Oak Ridge, TN 37830
 */

package xal.app.pvlogger;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.sql.Connection;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.Border;

import xal.extension.application.XalWindow;
import xal.extension.application.smf.AcceleratorWindow;
import xal.service.pvlogger2.PVLogger;
import xal.service.pvlogger2.Data2DB;
import xal.tools.database.ConnectionDialog;
import xal.tools.database.ConnectionDictionary;
import xal.tools.database.DatabaseAdaptor;

/**
* @author  lv
* @author  chu
*/
public class UploadWindow extends AcceleratorWindow {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private File selectedFile;
	private Connection connection;

	public UploadWindow(UploadDocoment aDocument) {			
		super(aDocument);
		this.makeContent();
		this.remove(this.getToolBar());
		
		handleWindowEvents();
	}

	protected void makeContent() {
		setSize(600, 200);		
		Box mainView = new Box(javax.swing.SwingConstants.VERTICAL);
		mainView.add(this.buildUploadView(),BorderLayout.CENTER);
		getContentPane().add(mainView);
	}

	protected Container buildUploadView() {
		final Box mainView= new Box(javax.swing.SwingConstants.VERTICAL);	 
		
		final Box uploadView = new Box(javax.swing.SwingConstants.HORIZONTAL);	
		uploadView.setBorder(BorderFactory.createEtchedBorder());
		
		final JTextField textField = new JTextField();
		textField.setMaximumSize(new Dimension(400,30));
		final JButton chooserButton = new JButton("Choose the spreadsheet");
		chooserButton.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent event) {
				JFileChooser fileChooser = new JFileChooser();
				fileChooser.setFileFilter(new MyFileFilter("xls"));
				fileChooser.setFileFilter(new MyFileFilter("xlsx"));
				fileChooser.setDialogTitle("Choose your PV List spreadsheet");
				int i = fileChooser.showOpenDialog(getContentPane());
				if (i == JFileChooser.APPROVE_OPTION) {
					selectedFile = fileChooser.getSelectedFile();
					textField.setText(selectedFile.getPath());

				}

			}
		});

		JButton uploadButton = new JButton("Submit");
		uploadButton.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent event) {
				try {
					new Data2DB().insert(connection,
							selectedFile.getAbsolutePath());
					textField.setText("");					
				} catch (Exception e) {					
					displayError( "Uploading PV List Error", "", e );
				}
			}
		});
		
		JLabel urlLabel=new JLabel("URL:");	    
		
	    uploadView.add(urlLabel,BorderLayout.CENTER);
		uploadView.add(textField,BorderLayout.CENTER);
		uploadView.add(chooserButton,BorderLayout.CENTER);						
		//uploadView.add(uploadButton,BorderLayout.SOUTH);
		
		final Box uploadButtonView = new Box(javax.swing.SwingConstants.HORIZONTAL);	
		uploadButtonView.add(uploadButton,BorderLayout.EAST);
		
		mainView.add(uploadView);
		mainView.add(uploadButtonView);

		return mainView;
	}

	/** Handle window events. When the window opens, request a connection. */
	protected void handleWindowEvents() {
		addWindowListener(new WindowAdapter() {
			public void windowOpened(final WindowEvent event) {
				try {
					final ConnectionDictionary dictionary = PVLogger
							.newBrowsingConnectionDictionary();
					if (dictionary != null) {
						connection = dictionary.getDatabaseAdaptor()
								.getConnection(dictionary);
					} else {
						requestUserConnection();
					}
				} catch (Exception exception) {
					requestUserConnection();
				}
			}
		});
	}

	protected void requestUserConnection() {
		ConnectionDictionary dictionary = PVLogger
				.newBrowsingConnectionDictionary();
		// System.out.println("dictionary"+dictionary);
		ConnectionDialog dialog = ConnectionDialog
				.getInstance(this, dictionary);
		// ConnectionDialog dialog = new ConnectionDialog(this);
		connection = dialog.showConnectionDialog(DatabaseAdaptor.getInstance());
		// System.out.println(connection+"............");
	}
}
