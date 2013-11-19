/*
 * BrowserWindow.java
 *
 * Created on Thu Mar 25 08:58:58 EST 2004
 *
 * Copyright (c) 2004 Spallation Neutron Source
 * Oak Ridge National Laboratory
 * Oak Ridge, TN 37830
 */

package xal.app.pvlogger.upload;

import java.awt.BorderLayout;
import java.awt.Container;
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
import javax.swing.JTextField;

import xal.service.pvlogger.PVLogger;
import xal.service.pvlogger.uploadPV.Data2DB;
import xal.smf.application.AcceleratorWindow;
import xal.tools.database.ConnectionDialog;
import xal.tools.database.ConnectionDictionary;
import xal.tools.database.DatabaseAdaptor;

/**
 * BrowserWindow is the main window for browsing the snapshots.
 * 
 * @author tap
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
		handleWindowEvents();
	}

	protected void makeContent() {
		setSize(1200, 700);		
		Box mainView = new Box(javax.swing.SwingConstants.VERTICAL);
		mainView.add(this.buildUploadView());
		getContentPane().add(mainView);
	}

	protected Container buildUploadView() {
		final Box uploadView = new Box(javax.swing.SwingConstants.HORIZONTAL);
		uploadView.setBorder(BorderFactory.createEtchedBorder());

		final JTextField textField = new JTextField();
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

		JButton uploadButton = new JButton("Upload");
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
	    
		
		uploadView.add(chooserButton,BorderLayout.CENTER);		
		uploadView.add(uploadButton,BorderLayout.CENTER);
		uploadView.add(textField,BorderLayout.CENTER);

		return uploadView;
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
