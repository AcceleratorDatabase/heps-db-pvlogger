/*
 *  SqlStateStore.java
 *
 *  Created on Fri Dec 05 16:11:32 EST 2003
 *
 *  Copyright (c) 2003 Spallation Neutron Source
 *  Oak Ridge National Laboratory
 *  Oak Ridge, TN 37830
 */
package xal.app.score;

import xal.service.pvlogger.ChannelSnapshot;
import xal.service.pvlogger.DBTableConfiguration;
import xal.service.pvlogger.MachineSnapshot;
import xal.service.pvlogger.MachineSnapshotTable;
import xal.service.pvlogger.MachineSnapshotTableExtend;
import xal.service.pvlogger.SnapshotGroupChannelTable;
import xal.service.pvlogger.SnapshotGroupTable;
import xal.service.pvlogger.ChannelSnapshotTable;
import xal.service.pvlogger.uploadPV.Data2DB;
import xal.service.pvlogger.uploadPV.SgnlRec;
import xal.service.pvlogger.uploadPV.SgnlRecTable;
import xal.service.pvlogger.uploadPV.SnapshotTypeTable;

import xal.tools.ArrayTool;
import xal.tools.database.ConnectionDictionary;
import xal.tools.database.DatabaseAdaptor;
import xal.tools.database.DatabaseException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * SqlStateStore is an implementation of StateStore that provides persistent
 * storage / retrival of machine state to and from a SQL database.
 * 
 * @author jdg
 */
public class SqlStateStore implements StateStore {

	// database adaptor
	/** Description of the Field */
	protected DatabaseAdaptor _databaseAdaptor;

	/** the database dictionary with connection information */
	protected ConnectionDictionary _dictionary;

	/** the connection information used to attempt reconnects */
	private final String _user;
	private final String _password;
	private final String _urlSpec;

	/** the error message handler */
	HandleErrorMessage _theDoc;

	// connection state
	/** Description of the Field */
	protected Connection _connection;

	/**
	 * Primary constructor
	 * 
	 * @param dict
	 *            properties needed for database connection
	 * @param connection
	 *            A database connection
	 * @param doc
	 *            error message handler (which is the data document)
	 */
	public SqlStateStore(final ConnectionDictionary dict,
			final Connection connection, HandleErrorMessage doc) {
		_dictionary = dict;
		DatabaseAdaptor adaptor = _dictionary.getDatabaseAdaptor();
		_databaseAdaptor = (adaptor != null) ? adaptor : DatabaseAdaptor
				.getInstance();
		_connection = connection;
		_theDoc = doc;
		_user = _dictionary.getUser();
		_password = _dictionary.getPassword();
		_urlSpec = _dictionary.getURLSpec();
	}

	/**
	 * Construct an SQL state store from the specified connection and use the
	 * default database adaptor.
	 * 
	 * @param connection
	 *            A database connection
	 */
	public SqlStateStore(final Connection connection) {
		this(null, connection, null);
	}

	/**
	 * Create a database connection to the persistent data storage.
	 * 
	 * @param urlSpec
	 *            The url of the database
	 * @param user
	 *            The user to login
	 * @param password
	 *            The user's password for login
	 * @exception StateStoreException
	 *                Description of the Exception
	 * @throws gov.sns.tools.pvlogger.StateStoreException
	 *             if a SQL exception is thrown
	 */
	protected void connect(String urlSpec, String user, String password)
			throws StateStoreException {
		try {
			_connection = newConnection(_databaseAdaptor, urlSpec, user,
					password);
		} catch (DatabaseException exception) {
			throw new StateStoreException(
					"Error while connecting to the data source and preparing statements.",
					exception);
		}
	}

	/**
	 * try to reestablish a database connection to the persistent data storage.
	 * 
	 * @exception StateStoreException
	 *                Description of the Exception
	 * @throws gov.sns.tools.pvlogger.StateStoreException
	 *             if a SQL exception is thrown
	 */
	protected boolean reconnect() throws StateStoreException {
		try {
			_connection = newConnection(_databaseAdaptor, _urlSpec, _user,
					_password);

			return true;
		} catch (DatabaseException exception) {
			_theDoc.dumpErr("Cannot reestablish database connection");
			return false;
		}
	}

	/**
	 * Create a database connection to the persistent data storage.
	 * 
	 * @param adaptor
	 *            the database adaptor
	 * @param urlSpec
	 *            The url of the database
	 * @param user
	 *            The user to login
	 * @param password
	 *            The user's password for login
	 * @return a new database connection
	 * @exception StateStoreException
	 *                Description of the Exception
	 * @throws gov.sns.tools.pvlogger.StateStoreException
	 *             if a SQL exception is thrown
	 */
	protected static Connection newConnection(DatabaseAdaptor adaptor,
			String urlSpec, String user, String password)
			throws StateStoreException {
		try {
			return adaptor.getConnection(urlSpec, user, password);
		} catch (DatabaseException exception) {
			throw new StateStoreException(
					"Error while connecting to the data source and preparing statements.",
					exception);
		}
	}

	/**
	 * Publish the channel snapshot and associate it with the machine snapshot
	 * given by the machine snapshop id.
	 * 
	 * @param row
	 *            scoreRow data
	 * @param groupId
	 *            The unique id of the associated machine snapshot
	 * @param time
	 *            The timestamp when the snapshot was taken.
	 * 
	 * @exception StateStoreException
	 *                Description of the Exception
	 * @throws gov.sns.apps.score.StateStoreException
	 *             if a SQL exception is thrown
	 */
	public void publish(final ScoreRow row, final String groupId,
			final Timestamp time) throws StateStoreException {
		Map<String, DBTableConfiguration> tableConfigurations = new Data2DB()
				.getTableConfigurations();
		DBTableConfiguration machineSnapshotConf = tableConfigurations
				.get("MachineSnapshot");
		MachineSnapshotTableExtend machineSnapshotTableExtend = new MachineSnapshotTableExtend(
				machineSnapshotConf);
		DBTableConfiguration channelSnapshotConf = tableConfigurations
				.get("ChannelSnapshot");
		ChannelSnapshotTable channelSnapshotTable = new ChannelSnapshotTable(
				channelSnapshotConf);

		List<ChannelSnapshot> channelSnapshots = new ArrayList<ChannelSnapshot>();

		String sp_pv = row.getSPName();
		Object sp_val = row.getSPValue();
		String rb_pv = row.getRBName();
		Object rb_val = row.getRBValue();

		double[] sp_value = { Double.parseDouble(sp_val.toString()) };
		double[] rb_value = { Double.parseDouble(rb_val.toString()) };

		if (!"".equals(sp_pv) && sp_pv != null) {
			ChannelSnapshot channelSnapshot_sp = new ChannelSnapshot(sp_pv,
					sp_value, -1, -1, new xal.ca.Timestamp(time), 0);
			channelSnapshots.add(channelSnapshot_sp);
		}
		if (!"".equals(rb_pv) && rb_pv != null) {
			ChannelSnapshot channelSnapshot_rb = new ChannelSnapshot(rb_pv,
					rb_value, -1, -1, new xal.ca.Timestamp(time), 0);
			channelSnapshots.add(channelSnapshot_rb);
		}

		MachineSnapshot machineSnapshot = new MachineSnapshot(time, "",
				channelSnapshots.toArray(new ChannelSnapshot[channelSnapshots.size()]));
		try {
			_connection.setAutoCommit(false);
			machineSnapshotTableExtend.insert(_connection, _databaseAdaptor,
					channelSnapshotTable, machineSnapshot);
			_connection.setAutoCommit(true);
		} catch (SQLException exception) {
			_theDoc.dumpErr("Database error - try reconnecting");
			throw new StateStoreException(
					"Error publishing a channel snapshot.", exception);
		}
	}

	/**
	 * Publish the machine snapshot.
	 * 
	 * @param machineSnapshot
	 *            -The machine snapshot to publish.
	 * @return true if no exception thrown
	 * 
	 * @exception StateStoreException
	 *                Description of the Exception
	 * @throws gov.sns.apps.score.StateStoreException
	 *             if a SQL exception is thrown
	 */
	public boolean publish(final ScoreSnapshot scoreSnapshot)
			throws StateStoreException {
		Map<String, DBTableConfiguration> tableConfigurations = new Data2DB()
				.getTableConfigurations();
		DBTableConfiguration machineSnapshotConf = tableConfigurations
				.get("MachineSnapshot");
		MachineSnapshotTableExtend machineSnapshotTableExtend = new MachineSnapshotTableExtend(
				machineSnapshotConf);
		DBTableConfiguration channelSnapshotConf = tableConfigurations
				.get("ChannelSnapshot");
		ChannelSnapshotTable channelSnapshotTable = new ChannelSnapshotTable(
				channelSnapshotConf);

		ScoreRow[] rows = scoreSnapshot.getScoreRows();
		Timestamp time = scoreSnapshot.getTimestamp();
		String coment = scoreSnapshot.getComment();
		String type = scoreSnapshot.getType();
		String user = _dictionary.getUser();
		List<ChannelSnapshot> channelSnapshots = new ArrayList<ChannelSnapshot>();

		for (int index = 0; index < rows.length; index++) {
			ScoreRow row = rows[index];
			if (row != null) {
				String sp_pv = row.getSPName();
				Object sp_val = row.getSPValue();
				String rb_pv = row.getRBName();
				Object rb_val = row.getRBValue();
				double[] sp_value = { Double.parseDouble(sp_val.toString()) };
				double[] rb_value = { Double.parseDouble(rb_val.toString()) };

				if (!"".equals(sp_pv) && sp_pv != null) {
					ChannelSnapshot channelSnapshot_sp = new ChannelSnapshot(
							sp_pv, sp_value, -1, -1,
							new xal.ca.Timestamp(time), 0);
					channelSnapshots.add(channelSnapshot_sp);
				}
				if (!"".equals(rb_pv) && rb_pv != null) {
					ChannelSnapshot channelSnapshot_rb = new ChannelSnapshot(
							rb_pv, rb_value, -1, -1,
							new xal.ca.Timestamp(time), 0);
					channelSnapshots.add(channelSnapshot_rb);
				}
			}
		}

		MachineSnapshot machineSnapshot = new MachineSnapshot(0, type, time,
				coment, user,
				channelSnapshots.toArray(new ChannelSnapshot[channelSnapshots
						.size()]));
		try {
			_connection.setAutoCommit(false);
			machineSnapshotTableExtend.insert(_connection, _databaseAdaptor,
					channelSnapshotTable, machineSnapshot);
			_connection.setAutoCommit(true);
		} catch (SQLException exception) {
			_theDoc.dumpErr("Database error - try reconnecting");
			throw new StateStoreException(
					"Error publishing a channel snapshot.", exception);
		}

		return false;
	}

	/**
	 * Fetch an array of logger types
	 * 
	 * @return an array of available logger types
	 * @throws SQLException
	 * @throws gov.sns.apps.score.StateStoreException
	 *             if a SQL exception is thrown
	 */
	public String[] fetchTypes() {
		Map<String, DBTableConfiguration> tableConfigurations = new Data2DB()
				.getTableConfigurations();
		DBTableConfiguration snapshotGroupConf = tableConfigurations
				.get("SnapshotGroup");
		DBTableConfiguration snapshotGroupChannelConf = tableConfigurations
				.get("SnapshotGroupChannel");
		SnapshotGroupChannelTable snapshotGroupChannelTable = new SnapshotGroupChannelTable(
				snapshotGroupChannelConf);
		SnapshotTypeTable snapshotTypeTable = new SnapshotTypeTable(
				snapshotGroupConf, snapshotGroupChannelTable);
		try {
			return snapshotTypeTable.fetchTypesForApp(_connection, "score");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Fetch an empty ScoreGroup by name. T his can be used to construct the
	 * underlying structure (PVData) used in score)
	 * 
	 * @param type
	 *            the score type
	 * @return a score group
	 * @exception StateStoreException
	 *                Description of the Exception
	 * @throws gov.sns.apps.score.StateStoreException
	 *             if a SQL exception is thrown
	 */
	public ScoreGroup fetchGroup(final String type) throws StateStoreException {
		// final List<ScoreRow> scoreRows = new ArrayList<ScoreRow>();
		final Set<ScoreRow> scoreRows = new HashSet<ScoreRow>();
		Map<String, DBTableConfiguration> tableConfigurations = new Data2DB()
				.getTableConfigurations();
		DBTableConfiguration snapshotGroupChannelConf = tableConfigurations
				.get("SnapshotGroupChannel");
		SnapshotGroupChannelTable snapshotGroupChannelTable = new SnapshotGroupChannelTable(
				snapshotGroupChannelConf);

		SgnlRecTable sgnlRecTable = new SgnlRecTable();
		try {
			String[] pvs = snapshotGroupChannelTable.fetchPVsByType(
					_connection, type);
			for (int i = 0; i < pvs.length; i++) {
				SgnlRec sgnl_rec = sgnlRecTable.fetchSgnlRecById(_connection,
						pvs[i]);

				String spName;
				String rbName;

				if (sgnl_rec.isReadback_ind() == false) {
					spName = sgnl_rec.getSgnl_id();
					rbName = sgnl_rec.getRelated_sgnl_id();
				} else {
					spName = sgnl_rec.getRelated_sgnl_id();
					rbName = sgnl_rec.getSgnl_id();
				}

				// final String sys, final String subSystem, final
				// DataTypeAdaptor dataTypeAdaptor, final String rbName, final
				// String spName, final String useRB )
				ScoreRow row = new ScoreRow(sgnl_rec.getSystem_id(),
						sgnl_rec.getSub_system_id(), null, rbName, spName,
						sgnl_rec.getUse_rb_ind());
				scoreRows.add(row);
			}
			return new ScoreGroup(type,
					scoreRows.toArray(new ScoreRow[scoreRows.size()]));
		} catch (SQLException exception) {
			// check for connection timeout:
			if (exception.getErrorCode() == 2396) {
				_theDoc.dumpErr("Database error - try reconnecting");
				// attempt 1 reconnect
				if (reconnect()) {
					_theDoc.dumpErr("Reconnection successful");
					return fetchGroup(type);
				} else {
					_theDoc.dumpErr("Reconnection unsuccessful");
					throw new StateStoreException(
							"Error fetching the machine snapshots in range.",
							exception);
				}
			} else {
				throw new StateStoreException(
						"Error fetching pvlogger group for the specified type.",
						exception);
			}
		}

	}

	/**
	 * Fetch the specific snapshot associated with a time and equipment_id
	 * 
	 * @param type
	 *            - the score group label
	 * @param time
	 *            - the timestamp for the desired snapshot
	 * @return The score snapshop read from the persistent store.
	 * @exception StateStoreException
	 *                - Description of the Exception
	 * @throws gov.sns.apps.score.StateStoreException
	 *             if a SQL exception is thrown
	 */
	public ScoreSnapshot fetchScoreSnapshot(final String type,
			final Timestamp time) throws StateStoreException {
		Map<String, DBTableConfiguration> tableConfigurations = new Data2DB()
				.getTableConfigurations();
		DBTableConfiguration machineSnapshotConf = tableConfigurations
				.get("MachineSnapshot");
		MachineSnapshotTableExtend machineSnapshotTableExtend = new MachineSnapshotTableExtend(
				machineSnapshotConf);
		DBTableConfiguration channelSnapshotConf = tableConfigurations
				.get("ChannelSnapshot");

		ChannelSnapshotTable channelSnapshotTable = new ChannelSnapshotTable(
				channelSnapshotConf);
		SgnlRecTable sgnlRecTable = new SgnlRecTable();

		int machine_snapshot_id;
		MachineSnapshot machineSnapshot;
		try {
			machine_snapshot_id = machineSnapshotTableExtend
					.fetchMachineSnapshotIdByTypeAndTime(_connection, type,
							time);
			machineSnapshot = machineSnapshotTableExtend.fetchMachineSnapshot(
					_connection, machine_snapshot_id);
		} catch (SQLException exception) {
			// check for connection timeout:
			if (exception.getErrorCode() == 2396) {
				_theDoc.dumpErr("Database error - try reconnecting");
				// attempt 1 reconnect
				if (reconnect()) {
					_theDoc.dumpErr("Reconnection successful");
					return fetchScoreSnapshot(type, time);
				} else {
					_theDoc.dumpErr("Reconnection unsuccessful");
					throw new StateStoreException(
							"Error fetching the machine snapshots in range.",
							exception);
				}
			} else {
				throw new StateStoreException(
						"Error fetching the machine goup info for a snapshot.",
						exception);
			}
		}
		final Set<ScoreRow> scoreRows = new HashSet<ScoreRow>();
		try {
			ChannelSnapshot[] channelSnapshots = channelSnapshotTable
					.fetchChannelSnapshotsForMachineSnapshotID(_connection,
							machine_snapshot_id);
			Map<String, ChannelSnapshot> channelSnapshotMap = new HashMap<String, ChannelSnapshot>();
			for (int i = 0; i < channelSnapshots.length; i++) {
				ChannelSnapshot channelSnapshot = channelSnapshots[i];
				String sgnl_id = channelSnapshot.getPV();
				channelSnapshotMap.put(sgnl_id, channelSnapshot);
			}
			for (int i = 0; i < channelSnapshots.length; i++) {
				ChannelSnapshot channelSnapshot = channelSnapshots[i];
				SgnlRec sgnlRec = sgnlRecTable.fetchSgnlRecById(_connection,
						channelSnapshot.getPV());
				String sys = sgnlRec.getSystem_id();
				String subSys = sgnlRec.getSub_system_id();
				String useRB = sgnlRec.getUse_rb_ind();
				String rbName;
				double[] rbValue = null;

				String spName;
				double[] spValue = null;

				if (sgnlRec.isReadback_ind()) {
					rbName = sgnlRec.getSgnl_id();
					spName = sgnlRec.getRelated_sgnl_id();
					rbValue = channelSnapshot.getValue();
					if (channelSnapshotMap.containsKey(spName)) {
						spValue = channelSnapshotMap.get(spName).getValue();
					}

				} else {
					rbName = sgnlRec.getRelated_sgnl_id();
					spName = sgnlRec.getSgnl_id();
					spValue = channelSnapshot.getValue();
					if (channelSnapshotMap.containsKey(rbName)) {
						rbValue = channelSnapshotMap.get(rbName).getValue();
					}
				}
				Object sp_value, rb_value;
				if (spValue == null) {
					sp_value = null;
				} else {
					sp_value = spValue[0];
				}
				if (rbValue == null) {
					rb_value = null;
				} else {
					rb_value = rbValue[0];
				}
				final ScoreRow row = new ScoreRow(sys, subSys,
						new DoubleTypeAdaptor(), rbName, rb_value, spName,
						sp_value, useRB);

				scoreRows.add(row);
			}

			String comment = machineSnapshot.getComment();
			return new ScoreSnapshot(type, time, comment,
					scoreRows.toArray(new ScoreRow[scoreRows.size()]));
		} catch (SQLException exception) {
			_theDoc.dumpErr("Database error - try reconnecting");
			throw new StateStoreException(
					"Error fetching the machine snapshot rows.", exception);
		}

	}

	/**
	 * Fetch the golden snapshot associated with an equipment_id
	 * 
	 * @param type
	 *            - the score group label
	 * @return The score snapshop read from the persistent store. or null if
	 *         there is no golden set yet.
	 * @exception StateStoreException
	 *                - Description of the Exception
	 * @throws gov.sns.apps.score.StateStoreException
	 *             if a SQL exception is thrown
	 */
	public ScoreSnapshot fetchGoldenSnapshot(final String type)
			throws StateStoreException {
		Map<String, DBTableConfiguration> tableConfigurations = new Data2DB()
				.getTableConfigurations();
		DBTableConfiguration machineSnapshotConf = tableConfigurations
				.get("MachineSnapshot");
		MachineSnapshotTableExtend machineSnapshotTableExtend = new MachineSnapshotTableExtend(
				machineSnapshotConf);
		Timestamp time;
		try {
			int snapshot_id = machineSnapshotTableExtend
					.fetchGoldenMachineSnapshotIdByType(_connection, type);
			MachineSnapshot machineSnapshot = machineSnapshotTableExtend
					.fetchMachineSnapshot(_connection, snapshot_id);
			time = (Timestamp) machineSnapshot.getTimestamp();
		} catch (SQLException exception) {
			// check for connection timeout:
			if (exception.getErrorCode() == 2396) {
				_theDoc.dumpErr("Database error - try reconnecting");
				// attempt 1 reconnect
				if (reconnect()) {
					_theDoc.dumpErr("Reconnection successful");
					return fetchGoldenSnapshot(type);
				} else {
					_theDoc.dumpErr("Reconnection unsuccessful");
					throw new StateStoreException(
							"Error fetching the machine snapshots in range.",
							exception);
				}
			} else {
				throw new StateStoreException(
						"Error fetching the machine goup info for a snapshot.",
						exception);
			}
		}
		try {
			return fetchScoreSnapshot(type, time);
		} catch (Exception exception) {
			throw new StateStoreException(
					"Error fetching the machine snapshot rows.", exception);
		}
	}

	/**
	 * Fetch the machine snapshots
	 * 
	 * @param type
	 *            the score group label
	 * @param date1
	 *            initial time of time interval
	 * @param date2
	 *            final time of time interval
	 * @return The score snapshop read from the persistent store.
	 * 
	 * @throws gov.sns.apps.score.StateStoreException
	 *             if a SQL exception is thrown
	 */
	public List<ScoreSnapshot> fetchScoreSnapshotsInRange(final String type,
			final java.util.Date date1, final java.util.Date date2)
			throws StateStoreException {
		Map<String, DBTableConfiguration> tableConfigurations = new Data2DB()
				.getTableConfigurations();
		DBTableConfiguration machineSnapshotConf = tableConfigurations
				.get("MachineSnapshot");
		MachineSnapshotTable machineSnapshotTable = new MachineSnapshotTable(
				machineSnapshotConf);
		Timestamp time;

		final List<ScoreSnapshot> snapshots = new ArrayList<ScoreSnapshot>();
		try {
			MachineSnapshot[] mach_snapshots = machineSnapshotTable
					.fetchMachineSnapshotsInRange(_connection, type, date1,
							date2);
			for (int i = 0; i < mach_snapshots.length; i++) {
				MachineSnapshot machineSnapshot = mach_snapshots[i];
				time = (Timestamp) machineSnapshot.getTimestamp();
				snapshots.add(new ScoreSnapshot(machineSnapshot.getType(),
						time, machineSnapshot.getComment()));
			}
			return snapshots;
		} catch (SQLException exception) {
			// check for connection timeout:
			if (exception.getErrorCode() == 2396) {
				_theDoc.dumpErr("Database error - try reconnecting");
				// attempt 1 reconnect
				if (reconnect()) {
					_theDoc.dumpErr("Reconnection successful");
					return fetchScoreSnapshotsInRange(type, date1, date2);
				} else {
					_theDoc.dumpErr("Reconnection unsuccessful");
					throw new StateStoreException(
							"Error fetching the machine snapshots in range.",
							exception);
				}
			} else {
				throw new StateStoreException(
						"Error fetching the machine snapshots in range.",
						exception);
			}
		}

	}
}
