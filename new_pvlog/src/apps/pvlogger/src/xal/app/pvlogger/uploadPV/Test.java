package xal.app.pvlogger.uploadPV;


import java.sql.SQLException;
import java.util.ArrayList;

import org.apache.poi.ss.usermodel.Workbook;

import com.mysql.jdbc.Connection;

public class Test {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		/*Workbook wb=ReadExl.getWorkbook("E:\\PV Logger\\pvloggerSample.xlsx");
		
        ArrayList a=ReadSheet.getDataList(wb);
        ArrayList b=Data2Map.getMapData(a);
        System.out.println(b);*/
		
		try {
			Connection conn=DBTools.getConnection("mysql", "jdbc:mysql://localhost:3306/new_pvlog", "root", "826529");
			String filePath="E:\\PV Logger\\pvloggerSample_ac.xlsx";
			new Data2DB().insert(conn, filePath);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
