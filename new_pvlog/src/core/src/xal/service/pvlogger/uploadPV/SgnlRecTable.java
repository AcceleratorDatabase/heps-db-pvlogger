package xal.service.pvlogger.uploadPV;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SgnlRecTable {
    
	protected  String TABLE_NAME;
	
	protected  String SIGNAL_COLUMN;

	protected  String SYSTEM_COLUMN;
	
	protected  String EQUIP_CAT_COLUMN;
	
	protected String DEVICE_COLUMN;
	
	protected String GROUP_COLUMN;
		
    protected  String RELATIVE_SGNL_COLUMN;
	
	protected String READBACK_SIGN_COLUMN;
	
	protected String ACTIVE_SIGN_COLUMN;
	
	public SgnlRecTable( ) {
		TABLE_NAME="new_pvlog.sgnl_rec";
		SIGNAL_COLUMN="sgnl_id";
		SYSTEM_COLUMN="system_id";
		EQUIP_CAT_COLUMN="equip_cat_id";				
        DEVICE_COLUMN="device_id";	
        GROUP_COLUMN="group_id";
	    RELATIVE_SGNL_COLUMN="relative_sgnl_id";		
		READBACK_SIGN_COLUMN="readback_ind";		
		 ACTIVE_SIGN_COLUMN="active_ind";
	}
	
	public PreparedStatement getInsertStatement( final Connection connection ) throws SQLException {
		return connection.prepareStatement( "INSERT INTO " + TABLE_NAME + "(" + SIGNAL_COLUMN + ", " + SYSTEM_COLUMN + ", " + EQUIP_CAT_COLUMN + ", " +  DEVICE_COLUMN + ", " + RELATIVE_SGNL_COLUMN + ","+READBACK_SIGN_COLUMN+","+ACTIVE_SIGN_COLUMN+") VALUES (?, ?, ?, ?, ?,?,?)" );
	}
	
	public void batchInsert(Connection connection, ArrayList<SgnlRec> sgnl_recs){
		boolean need_insert=false;
		try {
			PreparedStatement state=this.getInsertStatement(connection);
			for (int i = 0; i < sgnl_recs.size(); i++) {
				SgnlRec sgnl_rec = sgnl_recs.get(i);
				
				state.setString(1, sgnl_rec.getSgnl_id());
				state.setString(2, sgnl_rec.getSystem_id());
				state.setString(3, sgnl_rec.getEquip_cat_id());

				state.setString(4, sgnl_rec.getDevice_id());
				state.setString(5, sgnl_rec.getRelative_sgnl_id());
				state.setBoolean(6, sgnl_rec.isReadback_ind());
				state.setBoolean(7, sgnl_rec.isActive_ind());

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
		
}
