/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package xal.service.pvlogger;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

/**
 * 
 * @author lv
 * @author chu
 */
public class FileTool {

	public static FileInputStream getFileInputStream(String filePath)
			throws FileNotFoundException {
		FileInputStream inp = null;
		inp = new FileInputStream(filePath);
		return inp;
	}

	public static Workbook getWorkbook(FileInputStream inp) throws InvalidFormatException, IOException {
		Workbook wb = null;
		wb = WorkbookFactory.create(inp);
		return wb;
	}
}
