package xal.service.pvlogger.uploadPV;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import xal.service.pvlogger.*;

public class SnapshotTypeTable extends SnapshotGroupTable {

	public SnapshotTypeTable(DBTableConfiguration configuration,
			SnapshotGroupChannelTable groupChannelTable) {
		super(configuration, groupChannelTable);
	}

	protected PreparedStatement getInsertStatement(final Connection connection)
			throws SQLException {
		return connection.prepareStatement("INSERT INTO " + TABLE_NAME + "("
				+ this.PRIMARY_KEY + ", " + this.DESCRIPTION_COLUMN + ", "
				+ this.PERIOD_COLUMN + ", " + this.RETENTION_COLUMN + ", "
				+ this.SERVICE_COLUMN + ") VALUES (?, ?, ?, ?, ?)");
	}

	protected void batchInsertBySgnlRecs(final Connection conn,
			ArrayList<SgnlRec> sgnl_recs) throws SQLException {
		String group_id = sgnl_recs.get(0).getGroup_id();
		PreparedStatement state = null;
		boolean need_insert = false;

		state = this.getInsertStatement(conn);
		state.setString(1, group_id);
		state.setString(2, "SCORE snapshots");
		state.setInt(3, 0);
		state.setInt(4, 0);
		state.setString(5, "PHYSICS");
		state.executeUpdate();

		need_insert = true;

		if (need_insert) {
			state.executeBatch();
		}

		if (need_insert) {
			DBTools.closePreparedStatement(state);
		}

		ArrayList<String> channel_names = new ArrayList();
		for (int i = 0; i < sgnl_recs.size(); i++) {
			channel_names.add(sgnl_recs.get(i).getSgnl_id());
		}

		SnapshotGroupChannelTable snap_type_sgnl = this.SNAPSHOT_GROUP_CHANNEL_TABLE;
		try {
			snap_type_sgnl.insertChannels(conn, channel_names, group_id);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
