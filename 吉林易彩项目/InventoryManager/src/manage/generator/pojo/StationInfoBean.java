package manage.generator.pojo;

import java.util.Date;
import java.util.List;

public class StationInfoBean{
	private Integer e_nodeb_id;//eNodeBID
	private String related_site_addr;//所属站址编码
	private String related_site_addr_id;//所属站址名称
	private String tower_site_addr_code;//所属铁塔站址编码
	private String tower_site_addr_id;//所属铁塔站址名称
	private String province;//省
	private Integer city_id;//市
	private Integer county_id;//县区
	private String countryside;//乡/镇/街道
	private String unit;//划小单元
	private String dept_area;//营服中心/营业部
	private String district;//片区
	private String clusters;//簇
	private String reseau;//网格
	private String zh_label;//基站名称
	private String enodeb_gap_name;//eNodeB采集名称
	private String equipment_vendor;//设备厂家
	private String equipment_type;//设备型号
	private String ipv4_addr;//IPV4地址
	private String subnet_mask;//子网掩码
	private String gateway;//网关地址
	private String bandwidth;//S1配置带宽(Mbps)
	private String mme1;//所属MME-1标识
	private String mme2;//所属MME-2标识
	private String enodeb_version;//eNodeB软件版本
	private String duplex_mode;//双工模式
	private Integer cell_num;//小区数量
	private String omc_site_status;//omc中基站运行状态
	private String site_esn;//基站电子序列号
	private String site_type;//基站类型
	private String site_level;//基站等级
	private float site_longitude;//基站经度
	private float site_latitude;//基站纬度
	private float ant_azimuth;//天线方向角（度）
	private float an_advanceangle;//预置下倾角（度）
	private String project_no;//工程编码
	private String is_site_shared;//是否共享基站
	private String omcid;//OMCID
	private String net_date;//入网日期
	private String remark;//beizhu
	private String update_time;//数据最后更新时间
	private String update_person;//数据最后更新人
	private String maintain_company;//维护单位
	private String maintain_area;//综合维护区域
	private String maintain_manager;//综合维护区域组长
	private String maintain_manager_phone;//维护组长电话
	private String maintain_area_zy;//电信自有综合维护区域
	private String time_stamp;//时间戳
	private Integer stateflag;//删除标识
	private Integer int_id;//系统内部编码
	private String creator;//创建人
	private String creat_time;//创建时间
	private String modifier;//修改人
	private String modify_time;//修改时间
	public Integer getE_nodeb_id() {
		return e_nodeb_id;
	}
	public void setE_nodeb_id(Integer e_nodeb_id) {
		this.e_nodeb_id = e_nodeb_id;
	}
	public String getRelated_site_addr() {
		return related_site_addr;
	}
	public void setRelated_site_addr(String related_site_addr) {
		this.related_site_addr = related_site_addr;
	}
	public String getRelated_site_addr_id() {
		return related_site_addr_id;
	}
	public void setRelated_site_addr_id(String related_site_addr_id) {
		this.related_site_addr_id = related_site_addr_id;
	}
	public String getTower_site_addr_code() {
		return tower_site_addr_code;
	}
	public void setTower_site_addr_code(String tower_site_addr_code) {
		this.tower_site_addr_code = tower_site_addr_code;
	}
	public String getTower_site_addr_id() {
		return tower_site_addr_id;
	}
	public void setTower_site_addr_id(String tower_site_addr_id) {
		this.tower_site_addr_id = tower_site_addr_id;
	}
	public String getProvince() {
		return province;
	}
	public void setProvince(String province) {
		this.province = province;
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
	public String getCountryside() {
		return countryside;
	}
	public void setCountryside(String countryside) {
		this.countryside = countryside;
	}
	public String getUnit() {
		return unit;
	}
	public void setUnit(String unit) {
		this.unit = unit;
	}
	public String getDept_area() {
		return dept_area;
	}
	public void setDept_area(String dept_area) {
		this.dept_area = dept_area;
	}
	public String getDistrict() {
		return district;
	}
	public void setDistrict(String district) {
		this.district = district;
	}
	public String getClusters() {
		return clusters;
	}
	public void setClusters(String clusters) {
		this.clusters = clusters;
	}
	public String getReseau() {
		return reseau;
	}
	public void setReseau(String reseau) {
		this.reseau = reseau;
	}
	public String getZh_label() {
		return zh_label;
	}
	public void setZh_label(String zh_label) {
		this.zh_label = zh_label;
	}
	public String getEnodeb_gap_name() {
		return enodeb_gap_name;
	}
	public void setEnodeb_gap_name(String enodeb_gap_name) {
		this.enodeb_gap_name = enodeb_gap_name;
	}
	public String getEquipment_vendor() {
		return equipment_vendor;
	}
	public void setEquipment_vendor(String equipment_vendor) {
		this.equipment_vendor = equipment_vendor;
	}
	public String getEquipment_type() {
		return equipment_type;
	}
	public void setEquipment_type(String equipment_type) {
		this.equipment_type = equipment_type;
	}
	public String getIpv4_addr() {
		return ipv4_addr;
	}
	public void setIpv4_addr(String ipv4_addr) {
		this.ipv4_addr = ipv4_addr;
	}
	public String getSubnet_mask() {
		return subnet_mask;
	}
	public void setSubnet_mask(String subnet_mask) {
		this.subnet_mask = subnet_mask;
	}
	public String getGateway() {
		return gateway;
	}
	public void setGateway(String gateway) {
		this.gateway = gateway;
	}
	public String getBandwidth() {
		return bandwidth;
	}
	public void setBandwidth(String bandwidth) {
		this.bandwidth = bandwidth;
	}
	public String getMme1() {
		return mme1;
	}
	public void setMme1(String mme1) {
		this.mme1 = mme1;
	}
	public String getMme2() {
		return mme2;
	}
	public void setMme2(String mme2) {
		this.mme2 = mme2;
	}
	public String getEnodeb_version() {
		return enodeb_version;
	}
	public void setEnodeb_version(String enodeb_version) {
		this.enodeb_version = enodeb_version;
	}
	public String getDuplex_mode() {
		return duplex_mode;
	}
	public void setDuplex_mode(String duplex_mode) {
		this.duplex_mode = duplex_mode;
	}
	public Integer getCell_num() {
		return cell_num;
	}
	public void setCell_num(Integer cell_num) {
		this.cell_num = cell_num;
	}
	public String getOmc_site_status() {
		return omc_site_status;
	}
	public void setOmc_site_status(String omc_site_status) {
		this.omc_site_status = omc_site_status;
	}
	public String getSite_esn() {
		return site_esn;
	}
	public void setSite_esn(String site_esn) {
		this.site_esn = site_esn;
	}
	public String getSite_type() {
		return site_type;
	}
	public void setSite_type(String site_type) {
		this.site_type = site_type;
	}
	public String getSite_level() {
		return site_level;
	}
	public void setSite_level(String site_level) {
		this.site_level = site_level;
	}
	public float getSite_longitude() {
		return site_longitude;
	}
	public void setSite_longitude(float site_longitude) {
		this.site_longitude = site_longitude;
	}
	public float getSite_latitude() {
		return site_latitude;
	}
	public void setSite_latitude(float site_latitude) {
		this.site_latitude = site_latitude;
	}
	public float getAnt_azimuth() {
		return ant_azimuth;
	}
	public void setAnt_azimuth(float ant_azimuth) {
		this.ant_azimuth = ant_azimuth;
	}
	public float getAn_advanceangle() {
		return an_advanceangle;
	}
	public void setAn_advanceangle(float an_advanceangle) {
		this.an_advanceangle = an_advanceangle;
	}
	public String getProject_no() {
		return project_no;
	}
	public void setProject_no(String project_no) {
		this.project_no = project_no;
	}
	public String getIs_site_shared() {
		return is_site_shared;
	}
	public void setIs_site_shared(String is_site_shared) {
		this.is_site_shared = is_site_shared;
	}
	public String getOmcid() {
		return omcid;
	}
	public void setOmcid(String omcid) {
		this.omcid = omcid;
	}
	public String getNet_date() {
		return net_date;
	}
	public void setNet_date(String net_date) {
		this.net_date = net_date;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public String getUpdate_time() {
		return update_time;
	}
	public void setUpdate_time(String update_time) {
		this.update_time = update_time;
	}
	public String getUpdate_person() {
		return update_person;
	}
	public void setUpdate_person(String update_person) {
		this.update_person = update_person;
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
	public String getMaintain_area_zy() {
		return maintain_area_zy;
	}
	public void setMaintain_area_zy(String maintain_area_zy) {
		this.maintain_area_zy = maintain_area_zy;
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
	public Integer getInt_id() {
		return int_id;
	}
	public void setInt_id(Integer int_id) {
		this.int_id = int_id;
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