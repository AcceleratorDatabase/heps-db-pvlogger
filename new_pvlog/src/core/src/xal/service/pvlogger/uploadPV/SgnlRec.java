package xal.service.pvlogger.uploadPV;

/**
* @author  lv
* @author  chu
*/
public class SgnlRec {

	private String sgnl_id;
	private String system_id;
	private String sub_system_id;
	private String group_id;
	private String device_id;
	private String related_sgnl_id;
	private boolean readback_ind;
	private boolean active_ind;
	private String app_type;
	private String use_rb_ind;

	public SgnlRec() {
	}

	public SgnlRec(String _sgnl_id, String _system_id, String _sub_system_id,
			String _group_id, String _device_id, String _related_sgnl_id,
			boolean _readback_ind, boolean _active_ind, String _app_type,
			String _use_rb_ind) {
		this.sgnl_id = _sgnl_id;
		this.system_id = _system_id;
		this.sub_system_id = _sub_system_id;
		this.group_id = _group_id;
		this.device_id = _device_id;
		this.related_sgnl_id = _related_sgnl_id;
		this.readback_ind = _readback_ind;
		this.active_ind = _active_ind;
		this.app_type = _app_type;
		this.setUse_rb_ind(_use_rb_ind);
	}

	public String getSgnl_id() {
		return sgnl_id;
	}

	public String getApp_type() {
		return app_type;
	}

	public void setApp_type(String app_type) {
		this.app_type = app_type;
	}

	public void setSgnl_id(String sgnl_id) {
		this.sgnl_id = sgnl_id;
	}

	public String getSystem_id() {
		return system_id;
	}

	public void setSystem_id(String system_id) {
		this.system_id = system_id;
	}

	public String getSub_system_id() {
		return sub_system_id;
	}

	public void setSub_system_id(String sub_system_id) {
		this.sub_system_id = sub_system_id;
	}

	public String getGroup_id() {
		return group_id;
	}

	public void setGroup_id(String group_id) {
		this.group_id = group_id;
	}

	public String getDevice_id() {
		return device_id;
	}

	public void setDevice_id(String device_id) {
		this.device_id = device_id;
	}

	public String getRelated_sgnl_id() {
		return related_sgnl_id;
	}

	public void setRelated_sgnl_id(String related_sgnl_id) {
		this.related_sgnl_id = related_sgnl_id;
	}

	public boolean isReadback_ind() {
		return readback_ind;
	}

	public void setReadback_ind(boolean readback_ind) {
		this.readback_ind = readback_ind;
	}

	public boolean isActive_ind() {
		return active_ind;
	}

	public void setActive_ind(boolean active_ind) {
		this.active_ind = active_ind;
	}

	
	
	public boolean equals(SgnlRec sr){
		if(sr!=null&&sr.getClass()==SgnlRec.class){
			return this.getSgnl_id().equals(sr.getSgnl_id())&&this.getSystem_id().equals(sr.getSystem_id())
					&&this.getSub_system_id().equals(sr.getSub_system_id())&&this.getDevice_id().equals(sr.getDevice_id());
		}
		return false;
	}

	public String getUse_rb_ind() {
		return use_rb_ind;
	}

	public void setUse_rb_ind(String use_rb_ind) {
		this.use_rb_ind = use_rb_ind;
	}

}
