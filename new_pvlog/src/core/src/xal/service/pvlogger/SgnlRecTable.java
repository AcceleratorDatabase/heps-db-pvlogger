package xal.service.pvlogger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class SgnlRecTable {
    
	protected  String TABLE_NAME;

	
	protected  String SIGNAL_COLUMN;


	protected  String SYSTEM_COLUMN;

	
	protected  String EQUIP_CAT_COLUMN;

	
	protected String GROUP_COLUMN;

	
	protected String DEVICE_COLUMN;
	
	
    protected  String RELATIVE_SGNL_COLUMN;

	
	protected String READBACK_SIGN_COLUMN;

	
	protected String ACTIVE_SIGN_COLUMN;
	
	public SgnlRecTable( ) {
		TABLE_NAME="sgnl_rec";
		SIGNAL_COLUMN="sgnl_id";
		SYSTEM_COLUMN="system_id";
		EQUIP_CAT_COLUMN="equip_cat_id";		
	    GROUP_COLUMN="group_id";		
        DEVICE_COLUMN="device_id";		
	    RELATIVE_SGNL_COLUMN="relative_sgnl_id";		
		READBACK_SIGN_COLUMN="readback_ind";		
		 ACTIVE_SIGN_COLUMN="active_ind";
	}
	
	public PreparedStatement getInsertStatement( final Connection connection ) throws SQLException {
		return connection.prepareStatement( "INSERT INTO " + TABLE_NAME + "(" + SIGNAL_COLUMN + ", " + SYSTEM_COLUMN + ", " + EQUIP_CAT_COLUMN + ", " + GROUP_COLUMN + ", " + DEVICE_COLUMN + ", " + RELATIVE_SGNL_COLUMN + ","+READBACK_SIGN_COLUMN+","+ACTIVE_SIGN_COLUMN+") VALUES (?, ?, ?, ?, ?, ?,?,?)" );
	}
	
	
}
