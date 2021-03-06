package interfaces.pdainterface.equt.pojo;

import java.io.Serializable;

/**
 * 光交接箱 吉林
 * @author Administrator
 *
 */
public class EqutJiLinInfoBean implements Serializable,Cloneable{

	private static final long serialVersionUID = -5908468471314786378L;
	private Integer int_id;//资源id
	private String zh_label;//中文名称
	private String fibercab_no;//编号
	private Integer city_id;//地市
	private Integer county_id;//所属区县
	//private String area;//所属区域
	private String states;//设备状态
	private String vendor;//设备厂家
	private String location;//具体位置
	private String longitude;//经度
	private String latitude;//经度
	private String model; //型号
	private String ownership; //产权
	private String purpose;//用途
	private String cab_user;//使用单位
	private String res_owner;//所有权人
	private String design_capacity;//容量
	private String maintain_company;//维护单位
	private String maintain_area;//综合维护区域
	private String maintain_manager;//综合维护区域组长
	private String maintain_manager_phone;//组长电话
	private String remark;// 备注
	private String change_reason;// 变更原因
	private String time_stamp;//时间戳
	private Integer stateflag;  //删除标识
	private String creator; //创建人
	private String creat_time; //创建时间
	private String modifier; //修改人
	private String modify_time; //修改时间

	private String extendsSql;//扩展语句
	private String lats;
	private String lons;
	private String late;
	private String lone;
	private Integer start;
	private Integer limit;

	@Override
	public Object clone(){
		Object o = null;
		try{
			o = (EqutJiLinInfoBean)super.clone();
		}catch(Exception e){
			e.printStackTrace();
		}
		return o;
	}
	//public String getArea() {
	//	return area;
	//}
    //
	//public void setArea(String area) {
	//	this.area = area;
	//}

	public String getExtendsSql() {
		return extendsSql;
	}

	public void setExtendsSql(String extendsSql) {
		this.extendsSql = extendsSql;
	}

	public String getLats() {
		return lats;
	}

	public void setLats(String lats) {
		this.lats = lats;
	}

	public String getLons() {
		return lons;
	}

	public void setLons(String lons) {
		this.lons = lons;
	}

	public String getLate() {
		return late;
	}

	public void setLate(String late) {
		this.late = late;
	}

	public String getLone() {
		return lone;
	}

	public void setLone(String lone) {
		this.lone = lone;
	}

	public Integer getStart() {
		return start;
	}

	public void setStart(Integer start) {
		this.start = start;
	}

	public Integer getLimit() {
		return limit;
	}

	public void setLimit(Integer limit) {
		this.limit = limit;
	}

	public Integer getInt_id() {
		return int_id;
	}

	public void setInt_id(Integer int_id) {
		this.int_id = int_id;
	}

	public String getZh_label() {
		return zh_label;
	}

	public void setZh_label(String zh_label) {
		this.zh_label = zh_label;
	}

	public String getFibercab_no() {
		return fibercab_no;
	}

	public void setFibercab_no(String fibercab_no) {
		this.fibercab_no = fibercab_no;
	}

	public Integer getCity_id() {
		return city_id;
	}

	public void setCity_id(Integer city_id) {
		this.city_id = city_id;
	}

	public Integer getCounty_id() {
		return county_id;
	}

	public void setCounty_id(Integer county_id) {
		this.county_id = county_id;
	}

	public String getStates() {
		return states;
	}

	public void setStates(String states) {
		this.states = states;
	}

	public String getVendor() {
		return vendor;
	}

	public void setVendor(String vendor) {
		this.vendor = vendor;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getLongitude() {
		return longitude;
	}

	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}

	public String getLatitude() {
		return latitude;
	}

	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}

	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public String getOwnership() {
		return ownership;
	}

	public void setOwnership(String ownership) {
		this.ownership = ownership;
	}

	public String getPurpose() {
		return purpose;
	}

	public void setPurpose(String purpose) {
		this.purpose = purpose;
	}

	public String getCab_user() {
		return cab_user;
	}

	public void setCab_user(String cab_user) {
		this.cab_user = cab_user;
	}

	public String getRes_owner() {
		return res_owner;
	}

	public void setRes_owner(String res_owner) {
		this.res_owner = res_owner;
	}

	public String getDesign_capacity() {
		return design_capacity;
	}

	public void setDesign_capacity(String design_capacity) {
		this.design_capacity = design_capacity;
	}

	public String getMaintain_company() {
		return maintain_company;
	}

	public void setMaintain_company(String maintain_company) {
		this.maintain_company = maintain_company;
	}

	public String getMaintain_area() {
		return maintain_area;
	}

	public void setMaintain_area(String maintain_area) {
		this.maintain_area = maintain_area;
	}

	public String getMaintain_manager() {
		return maintain_manager;
	}

	public void setMaintain_manager(String maintain_manager) {
		this.maintain_manager = maintain_manager;
	}

	public String getMaintain_manager_phone() {
		return maintain_manager_phone;
	}

	public void setMaintain_manager_phone(String maintain_manager_phone) {
		this.maintain_manager_phone = maintain_manager_phone;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getChange_reason() {
		return change_reason;
	}

	public void setChange_reason(String change_reason) {
		this.change_reason = change_reason;
	}

	public String getTime_stamp() {
		return time_stamp;
	}

	public void setTime_stamp(String time_stamp) {
		this.time_stamp = time_stamp;
	}

	public Integer getStateflag() {
		return stateflag;
	}

	public void setStateflag(Integer stateflag) {
		this.stateflag = stateflag;
	}

	public String getCreator() {
		return creator;
	}

	public void setCreator(String creator) {
		this.creator = creator;
	}

	public String getCreat_time() {
		return creat_time;
	}

	public void setCreat_time(String creat_time) {
		this.creat_time = creat_time;
	}

	public String getModifier() {
		return modifier;
	}

	public void setModifier(String modifier) {
		this.modifier = modifier;
	}

	public String getModify_time() {
		return modify_time;
	}

	public void setModify_time(String modify_time) {
		this.modify_time = modify_time;
	}
}
