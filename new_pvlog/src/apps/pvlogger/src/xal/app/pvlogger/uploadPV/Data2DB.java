package xal.app.pvlogger.uploadPV;


import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.poi.ss.usermodel.Workbook;

import com.mysql.jdbc.Connection;

import xal.service.pvlogger.SgnlRecTable;



public class Data2DB {

	private SgnlRecTable sgnl_rec_table;

	public Data2DB() {
		sgnl_rec_table = new SgnlRecTable();
	}

	public void insert(Connection conn, String filePath) {
		Workbook wb = ReadExl.getWorkbook(filePath);
		PreparedStatement state = null;
		boolean need_insert = false;
		ArrayList<SgnlRec> objectList = Map2Object.getSgnlRecObject(wb);

		try {
			conn.setAutoCommit(false);
			for (int i = 0; i < objectList.size(); i++) {
				SgnlRec sgnl_rec = objectList.get(i);

				state = (PreparedStatement) sgnl_rec_table
						.getInsertStatement(conn);
				state.setString(1, sgnl_rec.getSign_id());
				state.setString(2, sgnl_rec.getSystem_id());
				state.setString(3, sgnl_rec.getEquip_cat_id());
				state.setString(4, sgnl_rec.getGroup_id());
				state.setString(5, sgnl_rec.getDevice_id());
				state.setString(6, sgnl_rec.getRelative_sgnl_id());
				state.setBoolean(7, sgnl_rec.isReadback_ind());
				state.setBoolean(8, sgnl_rec.isActive_ind());

				need_insert = true;

				state.executeUpdate();
			}

			if (need_insert) {
				state.executeBatch();

				conn.commit();
				conn.setAutoCommit(true);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (need_insert) {
				DBTools.closePreparedStatement(state);
				DBTools.closeConnection();
			}
		}

	}

}
