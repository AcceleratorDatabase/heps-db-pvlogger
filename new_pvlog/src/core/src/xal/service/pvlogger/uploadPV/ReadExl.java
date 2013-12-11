/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package xal.service.pvlogger.uploadPV;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

/**
 *
 * @author lv
 * @author chu
 */
public class ReadExl {

    public static Workbook getWorkbook(String filePath) throws InvalidFormatException, IOException {
        if (filePath == null || "".equals(filePath)) {
            System.out.println("Warning: Please assign the specific path of the spreadsheet!");
            return null;
        } else {
            FileInputStream inp = FileTool.getFileInputStream(filePath);
            Workbook wb = FileTool.getWorkbook(inp);
            return wb;
        }
    }  
    
  
    
}

