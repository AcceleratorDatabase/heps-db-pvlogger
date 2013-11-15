package xal.app.pvlogger.uploadPV;

/**
 * @author admin
 *
 */
public class SgnlRec {
	
	private String sign_id;
	private String system_id;
	private String equip_cat_id;
	private String group_id;
	private String device_id;
	private String relative_sgnl_id;
	private boolean readback_ind;
	private boolean active_ind;
	public String getSign_id() {
		return sign_id;
	}
	public void setSign_id(String sign_id) {
		this.sign_id = sign_id;
	}
	public String getSystem_id() {
		return system_id;
	}
	public void setSystem_id(String system_id) {
		this.system_id = system_id;
	}
	public String getEquip_cat_id() {
		return equip_cat_id;
	}
	public void setEquip_cat_id(String equip_cat_id) {
		this.equip_cat_id = equip_cat_id;
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
	public String getRelative_sgnl_id() {
		return relative_sgnl_id;
	}
	public void setRelative_sgnl_id(String relative_sgnl_id) {
		this.relative_sgnl_id = relative_sgnl_id;
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
	
	

}
