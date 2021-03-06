package interfaces.pdainterface.buriedPart.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.JdbcTemplate;

import base.database.DataBase;
import base.util.MapUtil;
import base.util.TextUtil;
import interfaces.pdainterface.buriedPart.pojo.BuriedPartInfoBean;
import interfaces.pdainterface.buriedPart.service.IBuriedPartCustomService;

public class BuriedPartCustomServiceImpl extends DataBase implements IBuriedPartCustomService{
	private JdbcTemplate jdbcTemplate;
	private static String buriedPartGrid ="buriedPart.getBuriedPartGridCustom";
	private static String updateBuriedPart ="buriedPart.updateBuriedPartCustom";
	private static String deleteBuriedPart ="buriedPart.deleteBuriedPartCustom";
	private static String insertBuriedPart = "buriedPart.insertBuriedPartCustom";
	
	public List<BuriedPartInfoBean> getBuriedPartGridCustom(BuriedPartInfoBean object){
		if(TextUtil.isNotNull(object.getZh_label())){
			String partName = object.getZh_label();
			if(partName.contains(" ")){
				partName= partName.replaceAll(" +", "%");
			}
			object.setZh_label("%"+partName+"%");
		}
		List<BuriedPartInfoBean> list = getObjects(buriedPartGrid, object);
		return list;
	}
		
	/**
	 * 更改直埋段信息
	 * @param obj
	 * @return
	 */
	public int updateBuriedPartCustom(BuriedPartInfoBean obj){
		return this.update(updateBuriedPart, obj);
	}
	
	/**
	 * 设置直埋段计算长度
	 * @param obj
	 * @return
	 */
	public BuriedPartInfoBean setBuriedPartLength(BuriedPartInfoBean obj){
		String sql ="select longitude as lon,latitude as lat from"
		  		+ "  rms_landmark where int_id in ('"+obj.getStart_ponit_id()+"','"+obj.getEnd_ponit_id()+"')";
		  List<Map<String, Object>> list = this.jdbcTemplate.queryForList(sql);
		  Map<String, Object> start = list.get(0);
		  Map<String, Object> end = list.get(1);
		  String distinct = MapUtil.Distance(Double.parseDouble(start.get("lon")+""),
				  	Double.parseDouble(start.get("lat")+""),
				  	Double.parseDouble(end.get("lon")+""),
				  	Double.parseDouble(end.get("lat")+""));
		  obj.setLine_length(distinct);
		return obj;
	}
	
	/**
	 * 增加直埋段信息
	 * @param obj
	 * @return
	 */
	public int insertBuriedPartCustom(BuriedPartInfoBean obj){
		return (Integer) this.insert(insertBuriedPart, obj);
	}
	
	public int deleteBuriedPartCustom(BuriedPartInfoBean obj){
		return this.update(deleteBuriedPart, obj);
	}
	
	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}
	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}
}
