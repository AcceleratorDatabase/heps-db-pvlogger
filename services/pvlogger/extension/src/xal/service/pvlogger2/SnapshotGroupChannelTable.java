//
//  SnapshotGroupChannelTable.java
//  xal
//
//  Created by Pelaia II, Tom on 10/12/06.
//  Copyright 2006 Oak Ridge National Lab. All rights reserved.
//

package xal.service.pvlogger2;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.List;
import java.util.ArrayList;


/** represent the snapshot group (type) - PV relationship database table */
public class SnapshotGroupChannelTable {
	/** database table name */
	protected final String TABLE_NAME;
	
	/** Group primary key */
	protected final String GROUP_COLUMN;
	
	/** PV primary key */
	protected final String CHANNEL_COLUMN;
	
	
	/** Constructor */
	public SnapshotGroupChannelTable( final DBTableConfiguration configuration ) {
		TABLE_NAME = configuration.getTableName();
		
		GROUP_COLUMN = configuration.getColumn( "group" );
		CHANNEL_COLUMN = configuration.getColumn( "channel" );
	}
	
	
	/**
	 * Fetch an array of PVs corresponding to the specified channel group
	 * @param connection database connection
	 * @param type channel group type
	 * @return array of PVs
	 */
	public String[] fetchPVsByType( final Connection connection, final String type ) throws SQLException {
		final PreparedStatement queryStatement = getGroupChannelQueryByGroupStatement( connection );
		queryStatement.setString( 1, type );
		
		final List<String> pvs = new ArrayList<String>();
		final ResultSet resultSet = queryStatement.executeQuery();
		while ( resultSet.next() ) {
			pvs.add( resultSet.getString( CHANNEL_COLUMN ) );
		}
		resultSet.close();
		return pvs.toArray( new String[pvs.size()] );
	}
	
	
	/**
	 * Insert the channels for the specified group.
	 * @param connection database connection
	 * @param channelNames PVs to insert
	 * @param groupID Channel Group ID
	 */
	public void insertChannels( final Connection connection, final List<String> channelNames, final String groupID ) throws SQLException {
		final PreparedStatement insertStatement = getInsertStatement( connection );
		boolean needsInsert = false;
		
		for ( final String channelName : channelNames ) {
			if ( channelName != null ) {
				try {
					insertStatement.setString( 1, groupID );
					insertStatement.setString( 2, channelName );
					
					insertStatement.addBatch();
					needsInsert = true;
				}
				catch( Exception exception ) {
					System.err.println( "Exception publishing channel:  " + channelName );
					System.err.println( exception );
				}
			}
		}
		
		if ( needsInsert ) {
			insertStatement.executeBatch();
		}
		if(insertStatement != null) {
			insertStatement.close();
		}
	}
	
	
	/**
	 * Get a prepared statement to get the relationships by group.
	 * @return the prepared statement to query for machine snapshot type-PV record by type
	 * @throws java.sql.SQLException  if an exception occurs during a SQL evaluation
	 */
	protected PreparedStatement getGroupChannelQueryByGroupStatement( final Connection connection ) throws SQLException {
		return connection.prepareStatement( "SELECT * FROM " + TABLE_NAME + " WHERE " + GROUP_COLUMN + " = ?" );
	}
	
	
	/**
	 * Create a prepared statement for inserting new records into the channel snapshot database table.
	 * @return the prepared statement for inserting a new channel snapshot
	 * @throws java.sql.SQLException  if an exception occurs during a SQL evaluation
	 */
	protected PreparedStatement getInsertStatement( final Connection connection ) throws SQLException {
		return connection.prepareStatement( "INSERT INTO " + TABLE_NAME + "(" + GROUP_COLUMN + ", " + CHANNEL_COLUMN + ") VALUES (?, ?)" );
	}	
}
