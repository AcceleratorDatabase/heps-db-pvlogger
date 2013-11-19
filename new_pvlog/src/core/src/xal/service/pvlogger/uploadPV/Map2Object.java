package xal.service.pvlogger.uploadPV;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.poi.ss.usermodel.Workbook;

public class Map2Object {

	public static ArrayList<SgnlRec> getSgnlRecObject(Workbook wb) {

		ArrayList<SgnlRec> objectList = new ArrayList();

		String group_id = ReadSheet.getFirstCellCon(wb);
		ArrayList<HashMap> mapList = Data2Map.getMapData(ReadSheet
				.getDataList(wb));
		for (int i = 0; i < mapList.size(); i++) {
			SgnlRec st_sgnl = new SgnlRec();
			SgnlRec rb_sgnl = new SgnlRec();

			String device_id = null;
			String system_id = null;
			String equip_cat_id = null;
			
			String set_pt_sgnl_id = null;
			String rb_sgnl_id = null;
			boolean readback_ind;
			boolean active_ind;
			
			HashMap map = mapList.get(i);
			Iterator it = map.entrySet().iterator();
			while (it.hasNext()) {				
				Map.Entry entry = (Entry) it.next();
				String key = entry.getKey().toString().toLowerCase();
				if (key.contains("device")) {
					device_id = entry.getValue().toString();
				}
				if (key.contains("set") && key.contains("signal")) {
					set_pt_sgnl_id = entry.getValue().toString();
				}
				if (key.contains("r") && key.contains("b")
						&& key.contains("signal")) {
					rb_sgnl_id = entry.getValue().toString();
				}
				if (key.contains("equip")) {
					equip_cat_id = entry.getValue().toString();
				}
				if (key.contains("sys")) {
					system_id = entry.getValue().toString();
				}				
			}
			
			if (set_pt_sgnl_id != null && !"".equals(set_pt_sgnl_id)) {
				st_sgnl.setSgnl_id(set_pt_sgnl_id);
				st_sgnl.setSystem_id(system_id);
				st_sgnl.setEquip_cat_id(equip_cat_id);
				st_sgnl.setGroup_id(group_id);
				st_sgnl.setDevice_id(device_id);
				st_sgnl.setRelative_sgnl_id(rb_sgnl_id);
				st_sgnl.setReadback_ind(false);
				st_sgnl.setActive_ind(true);
				if (rb_sgnl_id == null || "".equals(rb_sgnl_id)) {
					st_sgnl.setRelative_sgnl_id(null);
				}
				objectList.add(st_sgnl);
			}

			if (rb_sgnl_id != null && !"".equals(rb_sgnl_id)) {
				rb_sgnl.setSgnl_id(rb_sgnl_id);
				rb_sgnl.setSystem_id(system_id);
				rb_sgnl.setEquip_cat_id(equip_cat_id);
				rb_sgnl.setGroup_id(group_id);
				rb_sgnl.setDevice_id(device_id);
				rb_sgnl.setRelative_sgnl_id(set_pt_sgnl_id);
				rb_sgnl.setReadback_ind(true);
				rb_sgnl.setActive_ind(true);
				if (set_pt_sgnl_id == null || "".equals(set_pt_sgnl_id)) {
					rb_sgnl.setRelative_sgnl_id(null);
				}
				objectList.add(rb_sgnl);
			}
		}
		

		return objectList;
	}
}
