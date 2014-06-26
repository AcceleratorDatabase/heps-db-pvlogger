package xal.service.pvlogger2;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import xal.service.pvlogger2.DBTableConfiguration;
import xal.service.pvlogger2.MachineSnapshotTable;
/**
* @author  lv
* @author  chu
*/
public class MachineSnapshotTableExtend extends MachineSnapshotTable {

	public MachineSnapshotTableExtend(final DBTableConfiguration configuration) {
		super(configuration);
	}

	public int fetchMachineSnapshotIdByTypeAndTime(final Connection connection,
			final String type, final Timestamp time) throws SQLException {
		final PreparedStatement queryStatement = this
				.getQueryByTypeAndTimeStatement(connection);
		queryStatement.setString(1, type);
		queryStatement.setTimestamp(2, time);
		final ResultSet record = queryStatement.executeQuery();
		if (record.next()) {
			final int snapshot_id = record.getInt(PRIMARY_KEY);
			if (queryStatement != null) {
				queryStatement.close();
			}
			if (record != null) {
				record.close();
			}
			return snapshot_id;
		} else {
			if (queryStatement != null) {
				queryStatement.close();
			}
			if (record != null) {
				record.close();
			}
			return -1;
		}
		
	}
	
	public int fetchGoldenMachineSnapshotIdByType(final Connection connection,final String type) throws SQLException{
		final PreparedStatement queryStatement=this.getQueryForGoldenByTypeStatement(connection);
		queryStatement.setString(1, type);
		final ResultSet record = queryStatement.executeQuery();
		if (record.next()) {
			final int snapshot_id = record.getInt(PRIMARY_KEY);
			if (queryStatement != null) {
				queryStatement.close();
			}
			if (record != null) {
				record.close();
			}
			return snapshot_id;
		} else {
			if (queryStatement != null) {
				queryStatement.close();
			}
			if (record != null) {
				record.close();
			}
			return -1;
		}
	}

	protected PreparedStatement getQueryByTypeAndTimeStatement(
			final Connection connection) throws SQLException {
		return connection.prepareStatement("SELECT * FROM " + TABLE_NAME
				+ " WHERE " + TYPE_COLUMN + " = ? AND " + TIMESTAMP_COLUMN
				+ "= ?");
	}
	
	protected PreparedStatement getQueryForGoldenByTypeStatement(
			final Connection connection) throws SQLException {
		return connection.prepareStatement("SELECT * FROM " + TABLE_NAME
				+ " WHERE " + TYPE_COLUMN + " = ? AND " + "golden_ind=Y");				
	}


}
