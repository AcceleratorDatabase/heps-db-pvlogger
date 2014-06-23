package xal.service.pvlogger2;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 *
 * @author lv
 * @author chu
 */
public class Data2Map {
   
    public static ArrayList<HashMap> getMapData(ArrayList dataList) {
        ArrayList mapList = new ArrayList();
        ArrayList labelRow = (ArrayList) dataList.get(0);
        int colNum = labelRow.size();
        int i = 0;
        Iterator it = dataList.iterator();
        while (it.hasNext()) {
            ArrayList dataRow = (ArrayList) it.next();
            if (i > 0) {
                Map dataMap = new HashMap();
                for (int j = 0; j < colNum; j++) {
                    dataMap.put(labelRow.get(j), dataRow.get(j));
                }
                mapList.add(dataMap);
            }
            i++;
        }
        return mapList;
    }
}
