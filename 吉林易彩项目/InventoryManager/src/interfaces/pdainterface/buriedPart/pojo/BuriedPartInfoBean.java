package interfaces.pdainterface.buriedPart.pojo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;


public class BuriedPartInfoBean implements Serializable,Cloneable{
	private static final long serialVersionUID = 6028964457461970972L;
	
	private String zh_label;//直埋段名称
	private Integer city_id;//地市
	private Integer county_id;//区县

	private String start_ponit_id;//起点名称
	private BigDecimal startLon;
	private BigDecimal startLat;
	private String start_PointName;//起点名称

	private String end_ponit_id;//末端名称
	private BigDecimal endLon;
	private BigDecimal endLat;
	private String end_PointName;//末端名称


	private String ownership;//产权
	private String line_length;//线段长度
	private String maintain_company;//维护单位
	private String maintain_area;//维护区域
	private String maintain_manager;//维护区域组长
	private String maintain_manager_phone;//组长电话
	private String remark;//备注
	private String change_reason;//变更原因
	private Date time_stamp;//时间戳
	private Integer stateflag;//删除标识
	private Integer int_id;//系统内部标识
	private String creator;//创建人
	private Date creat_time;//创建时间
	private String modifier;//修改人
	private Date modify_time;//修改时间
	private Integer total;
    private String dir;
    private String sort;
    private Integer start;
    private Integer limit;
    private String extendSql;//扩展查询语句
    private List<BuriedPartInfoBean> buriedPartList;
    public Object clone(){
		Object o = null;
		try{
			o = (BuriedPartInfoBean)super.clone();
		}catch(Exception e){
			e.printStackTrace();
		}
		return o;
	}
	
    
    
    
    
    
    public String getStart_PointName() {
		return start_PointName;
	}






	public void setStart_PointName(String start_PointName) {
		this.start_PointName = start_PointName;
	}






	public String getEnd_PointName() {
		return end_PointName;
	}






	public void setEnd_PointName(String end_PointName) {
		this.end_PointName = end_PointName;
	}






	public String getZh_label() {
		return zh_label;
	}
	public void setZh_label(String zh_label) {
		this.zh_label = zh_label;
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
	public String getStart_ponit_id() {
		return start_ponit_id;
	}
	public void setStart_ponit_id(String start_ponit_id) {
		this.start_ponit_id = start_ponit_id;
	}
	public String getEnd_ponit_id() {
		return end_ponit_id;
	}
	public void setEnd_ponit_id(String end_ponit_id) {
		this.end_ponit_id = end_ponit_id;
	}
	public String getOwnership() {
		return ownership;
	}
	public void setOwnership(String ownership) {
		this.ownership = ownership;
	}
	public String getLine_length() {
		return line_length;
	}
	public void setLine_length(String line_length) {
		this.line_length = line_length;
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
	public Date getTime_stamp() {
		return time_stamp;
	}
	public void setTime_stamp(Date time_stamp) {
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
	public Date getCreat_time() {
		return creat_time;
	}
	public void setCreat_time(Date creat_time) {
		this.creat_time = creat_time;
	}
	public String getModifier() {
		return modifier;
	}
	public void setModifier(String modifier) {
		this.modifier = modifier;
	}
	public Date getModify_time() {
		return modify_time;
	}
	public void setModify_time(Date modify_time) {
		this.modify_time = modify_time;
	}
	public Integer getTotal() {
		return total;
	}
	public void setTotal(Integer total) {
		this.total = total;
	}
	public String getDir() {
		return dir;
	}
	public void setDir(String dir) {
		this.dir = dir;
	}
	public String getSort() {
		return sort;
	}
	public void setSort(String sort) {
		this.sort = sort;
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
	public String getExtendSql() {
		return extendSql;
	}
	public void setExtendSql(String extendSql) {
		this.extendSql = extendSql;
	}
	public List<BuriedPartInfoBean> getBuriedPartList() {
		return buriedPartList;
	}
	public void setBuriedPartList(List<BuriedPartInfoBean> buriedPartList) {
		this.buriedPartList = buriedPartList;
	}

	public BigDecimal getStartLon() {
		return startLon;
	}

	public void setStartLon(BigDecimal startLon) {
		this.startLon = startLon;
	}

	public BigDecimal getStartLat() {
		return startLat;
	}

	public void setStartLat(BigDecimal startLat) {
		this.startLat = startLat;
	}

	public BigDecimal getEndLon() {
		return endLon;
	}

	public void setEndLon(BigDecimal endLon) {
		this.endLon = endLon;
	}

	public BigDecimal getEndLat() {
		return endLat;
	}

	public void setEndLat(BigDecimal endLat) {
		this.endLat = endLat;
	}
}
