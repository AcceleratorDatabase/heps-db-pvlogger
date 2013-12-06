package xal.service.pvlogger.uploadPV;


import java.lang.reflect.Array;
import java.sql.SQLException;
import java.util.ArrayList;

import org.apache.poi.ss.usermodel.Workbook;

import xal.tools.ArrayTool;



import com.mysql.jdbc.Connection;

public class Test {

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		String s="{1.0,2.0}";
		double[] a=ArrayTool.getDoubleArrayFromString(s);
	}

}
