/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package xal.app.pvlogger.uploadPV;

import java.io.FileInputStream;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

/**
 *
 * @author lv
 * @author chu
 */
public class ReadExl {

    public static Workbook getWorkbook(String filePath) {
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

