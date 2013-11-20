package xal.service.pvlogger.uploadPV;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Workbook;

import xal.tools.data.DataAdaptor;
import xal.tools.xml.XmlDataAdaptor;

import xal.service.pvlogger.DBTableConfiguration;
import xal.service.pvlogger.SnapshotGroupChannelTable;

public class Data2DB {

	private SgnlRecTable sgnl_rec_table;

	public Data2DB() {
		sgnl_rec_table = new SgnlRecTable();
	}

	public Map<String, DBTableConfiguration> getTableConfigurations() {
		Map<String, DBTableConfiguration> tableConfigurations = new HashMap();
		URL configurationURL = getClass().getResource("configuration.xml");
		DataAdaptor configurationAdaptor = XmlDataAdaptor.adaptorForUrl(
				configurationURL, false).childAdaptor("Configuration");
		DataAdaptor persistentStoreAdaptor = configurationAdaptor
				.childAdaptor("persistentStore");
		List<DataAdaptor> tableAdaptors = (List<DataAdaptor>) persistentStoreAdaptor
				.childAdaptors("dbtable");

		for (DataAdaptor tableAdaptor : tableAdaptors) {
			DBTableConfiguration configuration = DBTableConfiguration
					.getInstance(tableAdaptor);
			String entity = tableAdaptor.stringValue("entity");
			tableConfigurations.put(entity, configuration);
		}
		return tableConfigurations;
	}

	public void insert(Connection conn, String filePath) throws Exception {
		Workbook wb = ReadExl.getWorkbook(filePath);
		ArrayList<SgnlRec> sgnlRecs = Map2Object.getSgnlRecObject(wb);
		Map<String, DBTableConfiguration> tableConfigurations = this
				.getTableConfigurations();
		DBTableConfiguration snapshotGroupConf = tableConfigurations
				.get("SnapshotGroup");
		DBTableConfiguration snapshotGroupChannelConf = tableConfigurations
				.get("SnapshotGroupChannel");

		SnapshotGroupChannelTable snapshotGroupChannelTable = new SnapshotGroupChannelTable(
				snapshotGroupChannelConf);
		SnapshotTypeTable snapshotTypeTable = new SnapshotTypeTable(
				snapshotGroupConf, snapshotGroupChannelTable);
		SgnlRecTable sgnlRecTable = new SgnlRecTable();

		conn.setAutoCommit(false);
		sgnlRecTable.batchInsert(conn, sgnlRecs);
		snapshotTypeTable.batchInsertBySgnlRecs(conn, sgnlRecs);
		conn.setAutoCommit(true);

	}
}
