package xal.service.pvlogger.uploadPV;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
/**
* @author  lv
* @author  chu
*/
public class SgnlRecTable {

	protected String TABLE_NAME;

	protected String SIGNAL_COLUMN;

	protected String SYSTEM_COLUMN;

	protected String SUB_SYSTEM_COLUMN;

	protected String DEVICE_COLUMN;

	protected String GROUP_COLUMN;

	protected String RELATED_SGNL_COLUMN;

	protected String READBACK_SIGN_COLUMN;

	protected String ACTIVE_SIGN_COLUMN;

	protected String APPLICATION_COLUMN;

	protected String USE_RB_FOR_SP_COLUMN;

	public SgnlRecTable() {
		TABLE_NAME = "new_pvlog.sgnl_rec";
		SIGNAL_COLUMN = "sgnl_id";
		SYSTEM_COLUMN = "system_id";
		SUB_SYSTEM_COLUMN = "sub_system_id";
		DEVICE_COLUMN = "device_id";
		GROUP_COLUMN = "group_id";
		RELATED_SGNL_COLUMN = "related_sgnl_id";
		READBACK_SIGN_COLUMN = "readback_ind";
		ACTIVE_SIGN_COLUMN = "active_ind";
		APPLICATION_COLUMN = "app_type";
		USE_RB_FOR_SP_COLUMN = "use_rb_ind";
	}

	public PreparedStatement getInsertStatement(final Connection connection)
			throws SQLException {
		return connection.prepareStatement("INSERT INTO " + TABLE_NAME + "("
				+ SIGNAL_COLUMN + ", " + SYSTEM_COLUMN + ", "
				+ SUB_SYSTEM_COLUMN + ", " + DEVICE_COLUMN + ", "
				+ RELATED_SGNL_COLUMN + "," + READBACK_SIGN_COLUMN + ","
				+ ACTIVE_SIGN_COLUMN + "," + USE_RB_FOR_SP_COLUMN
				+ ") VALUES (?, ?, ?, ?, ?,?,?,?)");
	}

	public PreparedStatement getQueryByIdStatement(final Connection connection)
			throws SQLException {
		return connection.prepareStatement("SELECT * FROM " + TABLE_NAME
				+ " WHERE " + SIGNAL_COLUMN + " =?");
	}

	public void batchInsert(Connection connection, ArrayList<SgnlRec> sgnl_recs) {
		boolean need_insert = false;
		try {
			PreparedStatement state = this.getInsertStatement(connection);
			for (int i = 0; i < sgnl_recs.size(); i++) {
				SgnlRec sgnl_rec = sgnl_recs.get(i);

				state.setString(1, sgnl_rec.getSgnl_id());
				state.setString(2, sgnl_rec.getSystem_id());
				state.setString(3, sgnl_rec.getSub_system_id());

				state.setString(4, sgnl_rec.getDevice_id());
				state.setString(5, sgnl_rec.getRelated_sgnl_id());
				state.setBoolean(6, sgnl_rec.isReadback_ind());
				state.setBoolean(7, sgnl_rec.isActive_ind());
				state.setString(8, sgnl_rec.getUse_rb_ind());

				need_insert = true;

				state.executeUpdate();
			}
			if (need_insert) {
				state.executeBatch();
			}
			DBTools.closePreparedStatement(state);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public SgnlRec fetchSgnlRecById(Connection connection, String sgnl_id)
			throws SQLException {
		SgnlRec sgnl_rec = null;
		final PreparedStatement sgnlQuery = this
				.getQueryByIdStatement(connection);
		sgnlQuery.setString(1, sgnl_id);
		final ResultSet resultSet = sgnlQuery.executeQuery();

		while (resultSet.next()) {
			String system_id = resultSet.getString(SYSTEM_COLUMN);
			String sub_system_id = resultSet.getString(SUB_SYSTEM_COLUMN);
			String device_id = resultSet.getString(DEVICE_COLUMN);
			String relative_sgnl_id = resultSet.getString(RELATED_SGNL_COLUMN);
			Boolean rb_ind = resultSet.getBoolean(READBACK_SIGN_COLUMN);
			Boolean active_ind = resultSet.getBoolean(ACTIVE_SIGN_COLUMN);
			String use_rb_ind = resultSet.getString(USE_RB_FOR_SP_COLUMN);
			sgnl_rec = new SgnlRec(sgnl_id, system_id, sub_system_id, null,
					device_id, relative_sgnl_id, rb_ind, active_ind, null,
					use_rb_ind);
		}
		return sgnl_rec;

	}

}
