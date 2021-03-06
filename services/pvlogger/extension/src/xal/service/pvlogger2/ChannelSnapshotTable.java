
//
//  ChannelSnapshotTable.java
//  xal
//
//  Created by Pelaia II, Tom on 10/12/06.
//  Copyright 2006 Oak Ridge National Lab. All rights reserved.
//

package xal.service.pvlogger2;

import java.sql.Array;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.List;
import java.util.ArrayList;
import java.math.BigDecimal;

import xal.tools.ArrayTool;
import xal.tools.database.DatabaseAdaptor;



/** represent the channel snapshot database table */
public class ChannelSnapshotTable {
	/** database table name */
	protected final String TABLE_NAME;

	/** time stamp column */
	protected final String TIMESTAMP_COLUMN;

	protected final String NANOSECS_COLUMN;
	
	/** machine snapshot primary key */
	protected final String MACHINE_SNAPSHOT_COLUMN;

	/** PV primary key */
	protected final String PV_COLUMN;

	/** value column */
	protected final String VALUE_COLUMN;

	/** status column */
	protected final String STATUS_COLUMN;

	/** severity column */
	protected final String SEVERITY_COLUMN;

	/** value array type (value holds an array of doubles) */
	protected final String VALUE_ARRAY_TYPE;


	/** Constructor */
	public ChannelSnapshotTable( final DBTableConfiguration configuration ) {
		TABLE_NAME = configuration.getTableName();

		MACHINE_SNAPSHOT_COLUMN = configuration.getColumn( "machineSnapshot" );
		PV_COLUMN = configuration.getColumn( "pv" );

		TIMESTAMP_COLUMN = configuration.getColumn( "timestamp" );
		NANOSECS_COLUMN = configuration.getColumn("nanoseconds");
		VALUE_COLUMN = configuration.getColumn( "value" );
		STATUS_COLUMN = configuration.getColumn( "status" );
		SEVERITY_COLUMN = configuration.getColumn( "severity" );

		VALUE_ARRAY_TYPE = configuration.getDataType( "valueArray" );
	}


	/**
	 * Insert the channel snapshots.
	 * @param connection database connection
	 * @param channelSnapshots channel snapshots to insert
	 * @param machineSnapshotID machine snapshot ID
	 */
	public void insert( final Connection connection, final DatabaseAdaptor databaseAdaptor, final ChannelSnapshot[] channelSnapshots, final long machineSnapshotID ) throws SQLException {
		final PreparedStatement insertStatement = getInsertStatement( connection );
		boolean needsInsert = false;

		final DecimalFormat NANOSECOND_FORMATTER = new DecimalFormat( "000000000" );
		
		for ( final ChannelSnapshot channelSnapshot : channelSnapshots ) {
			if ( channelSnapshot != null ) {
				final Timestamp timeStamp = channelSnapshot.getTimestamp().getSQLTimestamp();
				BigDecimal tStamp = channelSnapshot.getTimestamp().getFullSeconds();
				try {
//					final Array valueArray = databaseAdaptor.getArray( VALUE_ARRAY_TYPE, connection, channelSnapshot.getValue() );
					
					insertStatement.setLong( 1, machineSnapshotID );
					insertStatement.setString( 2, channelSnapshot.getPV() );
					insertStatement.setTimestamp( 3, new java.sql.Timestamp( channelSnapshot.getTimestamp().getTime() ) );

//					insertStatement.setArray( 5, valueArray );
					
					String value = ArrayTool.asString(channelSnapshot.getValue());
					insertStatement.setString( 4, value );

					insertStatement.setInt( 5, channelSnapshot.getStatus() );
					insertStatement.setInt( 6, channelSnapshot.getSeverity() );
					insertStatement.setInt(7, tStamp.subtract( tStamp.setScale(0, BigDecimal.ROUND_DOWN) ).movePointRight(9).intValue());
					
					insertStatement.addBatch();
					needsInsert = true;
				}
				catch( Exception exception ) {
					System.err.println( "Exception publishing channel snapshot:  " + channelSnapshot );
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
	 * Fetch the channel snapshots associated with a machine snapshot given by the machine snapshot's unique identifier.
	 * @param connection database connection
	 * @param machineSnapshotID machine snapshot primary key
	 * @return The channel snapshots associated with the machine snapshop
	 */
	public ChannelSnapshot[] fetchChannelSnapshotsForMachineSnapshotID( final Connection connection, final long machineSnapshotID ) throws SQLException {
		final List<ChannelSnapshot> snapshots = new ArrayList<ChannelSnapshot>();

		final PreparedStatement snapshotQuery = getQueryByMachineSnapshotStatement( connection );
		snapshotQuery.setLong( 1, machineSnapshotID );

		final ResultSet resultSet = snapshotQuery.executeQuery();
		while ( resultSet.next() ) {
			final String pv = resultSet.getString( PV_COLUMN );
			final Timestamp timestamp = resultSet.getTimestamp( TIMESTAMP_COLUMN );
			String strValue = resultSet.getString(VALUE_COLUMN);
//			final double[] value =ArrayTool.getDoubleArrayFromString(strValue);
			final double[] value = getDoubleArrayFromString(strValue);
			
//			final BigDecimal[] bigValue = (BigDecimal[])resultSet.getArray( VALUE_COLUMN ).getArray();
//			final double[] value = toDoubleArray( bigValue );
			
			final short status = resultSet.getShort( STATUS_COLUMN );
			final short severity = resultSet.getShort( SEVERITY_COLUMN );
			final int nanosecs = resultSet.getInt(NANOSECS_COLUMN);
			snapshots.add( new ChannelSnapshot( pv, value, status, severity, new xal.ca.Timestamp( timestamp ), nanosecs ) );
		}
		if( snapshotQuery != null) {
			snapshotQuery.close();
		}
		if (resultSet != null) {
			resultSet.close();
		}


		resultSet.close();
		return snapshots.toArray( new ChannelSnapshot[snapshots.size()] );
	}


	/**
	 * Create a prepared statement for inserting new records into the channel snapshot database table.
	 * @return the prepared statement for inserting a new channel snapshot
	 * @throws java.sql.SQLException  if an exception occurs during a SQL evaluation
	 */
	protected PreparedStatement getInsertStatement( final Connection connection ) throws SQLException {
		return connection.prepareStatement( "INSERT INTO " + TABLE_NAME + "(" + MACHINE_SNAPSHOT_COLUMN + ", " + PV_COLUMN + ", " + TIMESTAMP_COLUMN + ", " + VALUE_COLUMN + ", " + STATUS_COLUMN + ", " + SEVERITY_COLUMN + ", " + NANOSECS_COLUMN + ") VALUES (?, ?, ?, ?, ?, ?, ?)" );
	}


	/**
	 * Create a prepared statement to query for channel snapshot records corresponding to a machine snapshot.
	 * @return the prepared statement to query for channel snapshots by machine snapshot
	 * @throws java.sql.SQLException  if an exception occurs during a SQL evaluation
	 */
	protected PreparedStatement getQueryByMachineSnapshotStatement( final Connection connection ) throws SQLException {
		return connection.prepareStatement( "SELECT * FROM " + TABLE_NAME + " WHERE " + MACHINE_SNAPSHOT_COLUMN + " = ?" );
	}


	/**
	 * Convert an array of numbers to an array of double values.
	 * @param numbers array of numbers to convert
	 * @return array of double values corresponding to the input array of numbers.
	 */
	static protected double[] toDoubleArray( final Number[] numbers ) {
		final double[] array = new double[numbers.length];

		for ( int index = 0; index < numbers.length; index++ ) {
			array[index] = numbers[index].doubleValue();
		}

		return array;
	}
	
	public double[] getDoubleArrayFromString(final String douString) {
		double[] douArray=null;
		int start = douString.indexOf("{");
		int end = douString.indexOf("}");
		if (start < end) {
			String newString = douString.substring(start+1, end);
			String[] strArray=newString.split(",");
			douArray=new double[strArray.length];
			for(int i=0;i<strArray.length;i++){				
				douArray[i]=Double.valueOf(strArray[i]);				
			}
		}
		return douArray;
	}

}
