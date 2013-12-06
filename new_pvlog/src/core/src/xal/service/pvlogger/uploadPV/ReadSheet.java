package xal.service.pvlogger.uploadPV;

import java.util.ArrayList;
import java.util.Iterator;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

public class ReadSheet {
	
	public static int getColNum(Workbook wb){
		Sheet sheet=wb.getSheetAt(0);
		Row row=sheet.getRow(getFirstLabelCellNum(wb)[0]);
		return row.getLastCellNum();		
	}
	
	public static int[] getFirstCellNum(Workbook wb){
		int[] row_col_num={-1,-1};
		Sheet sheet=wb.getSheetAt(0);
		for(int i=0;i<10;i++){
			Row row=sheet.getRow(i);
			Iterator<Cell> it=row.cellIterator();
			int col_num=-1;
			while(it.hasNext()){
				col_num++;
				Cell cell=it.next();
				if(cell.getCellType()==Cell.CELL_TYPE_STRING){
					row_col_num[0]=i;	
					row_col_num[1]=col_num;
					return row_col_num;
				}
			}
		}
		return row_col_num;
	}
	
	public static String getFirstCellCon(Workbook wb){
		int[] row_col_num=getFirstCellNum(wb);
		Sheet sheet=wb.getSheetAt(0);
		Cell cell= sheet.getRow(row_col_num[0]).getCell(row_col_num[1]);
		return cell.getStringCellValue();
	}
	
	public static String getSecondCellCon(Workbook wb){
		int[] row_col_num=getFirstCellNum(wb);
		Sheet sheet=wb.getSheetAt(0);
		Cell cell= sheet.getRow(row_col_num[0]).getCell(row_col_num[1]+1);
		return cell.getStringCellValue();
	}
	
	/*
	 * int[0]:row number
	 * int[1]:column number
	 * */
	public static int[] getFirstLabelCellNum(Workbook wb){
		int[] row_col_num={-1,-1};
		int colNum=-1;
		Sheet sheet=wb.getSheetAt(0);
		for(int i=0;i<10;i++){
			Row row=sheet.getRow(i);
			ArrayList<String> rowList=new ArrayList();
			boolean sign=false;
			for(int j=0;j<row.getLastCellNum();j++){				
				Cell cell=row.getCell(j);
				if(cell!=null){
					if(!sign){
					  colNum=j;
					  sign=true;
					 }
					if(cell.getCellType()==Cell.CELL_TYPE_STRING){
						if(cell.getStringCellValue().toLowerCase().equals("system")){
							row_col_num[0]=i;
							row_col_num[1]=colNum;
							return row_col_num;
						}
						
					}
					
				}
				
			}
			
		}
		return row_col_num;
	}
	
	public static ArrayList getDataList(Workbook wb){
		ArrayList dataList=new ArrayList();
		Sheet sheet=wb.getSheetAt(0);
		int[] start_cell_num=getFirstLabelCellNum(wb);
		for(Iterator<Row> it=sheet.rowIterator();it.hasNext();){
			Row row=it.next();			
			if(row.getRowNum()>=start_cell_num[0]){				
				ArrayList oneRow=new ArrayList();
				for(int i=start_cell_num[1];i<getColNum(wb);i++){
					Object o="";
					try {
                        Cell cell = row.getCell(i);
                        if (!"".equals(cell) && cell != null) {
                            switch (cell.getCellType()) {
                                case Cell.CELL_TYPE_STRING:
                                    o = cell.getStringCellValue();
                                    break;
                                case Cell.CELL_TYPE_NUMERIC:
                                    o = cell.getNumericCellValue();
                                    break;
                                case Cell.CELL_TYPE_BOOLEAN:
                                    o = cell.getBooleanCellValue();
                                    break;
                                case Cell.CELL_TYPE_FORMULA:
                                    o = cell.getNumericCellValue();                               
                                    break;
                                case Cell.CELL_TYPE_BLANK:
                                    o = "";
                                    break;
                                case Cell.CELL_TYPE_ERROR:
                                    System.out.println("Error");
                                    break;
                            }
                        } else {
                            o = "";
                        }
                    } catch (NullPointerException e) {
                        o = "";
                    }
                    oneRow.add(o);
				}
           dataList.add(oneRow);				
			}
		}
		
		return dataList;
	}
	
	
	
}
