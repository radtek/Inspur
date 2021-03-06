package interfaces.pdainterface.stone.action;

import interfaces.pdainterface.stone.pojo.StoneCustomInfoBean;
import interfaces.pdainterface.stone.service.IStoneCustomService;

import java.util.List;
import org.apache.log4j.Logger;

import base.util.MapUtil;
import base.util.TextUtil;
import base.util.functions;
import base.util.pojo.Point;
import base.web.InterfaceAction;

public class PDAStoneCustomAction extends InterfaceAction{
	
	private static final long serialVersionUID = 4413460214683508556L;
	private static final Logger log = Logger.getLogger(PDAStoneCustomAction.class);
	private IStoneCustomService stoneCustomService;
	/**
	 * 得到标石列表
	 * @return
	 */
	public String getStoneCustom(){
		try{
			StoneCustomInfoBean obj = (StoneCustomInfoBean) getRequestObject(StoneCustomInfoBean.class);
			if(TextUtil.isNull(obj.getInt_id()) && TextUtil.isNotNull(obj.getLongitude()) && TextUtil.isNotNull(obj.getLatitude())){
				if(isWGS){
					Point point = MapUtil.wgs_gcj_encrypts(Double.parseDouble(obj.getLatitude()),Double.parseDouble(obj.getLongitude()));
					obj.setLatitude(point.getLat() + "");
					obj.setLongitude(point.getLng() + "");
				}
				double[] arr = functions.getAround(Double.parseDouble(obj.getLatitude()),Double.parseDouble(obj.getLongitude()), ((this.start/this.limit)+1)*Integer.parseInt(properties.getValueByKey("gisLength")));
				obj.setLats(String.valueOf(arr[0]));
				obj.setLons(String.valueOf(arr[1]));
				obj.setLate(String.valueOf(arr[2]));
				obj.setLone(String.valueOf(arr[3]));
			}else{
				obj.setStart(this.start);
				obj.setLimit(this.limit);
			}
			obj.setStateflag(0);
			List<StoneCustomInfoBean> list = stoneCustomService.getStoneGridCustom(obj);
			if(list !=null){
				for(StoneCustomInfoBean ston : list){
					if(isWGS && TextUtil.isNotNull(ston.getLatitude()) && TextUtil.isNotNull(ston.getLongitude())){
						Point point = MapUtil.wgs_gcj_encrypts(Double.parseDouble(ston.getLatitude()), Double.parseDouble(ston.getLongitude()));
						ston.setLatitude(point.getLat()+"");
						ston.setLongitude(point.getLng()+"");
					}
				}
				sendResponse(Integer.valueOf(0), list);
			}else{
				sendResponse(Integer.valueOf(2), "请求参数错误。");
			}
		}catch(Exception e){
			this.exception = e;
		    sendResponse(Integer.valueOf(3), "应用服务器异常。");
		    log.error("PDAStoneCustom.getStoneCustom ERROR\nJsonRequest:" + getJsonRequest() + "\n" + getJsonResponse(), e);
		}
		return "success";
	}
	
	/**
	 * 修改标石
	 * @return
	 */
	public String updateStoneCustom(){
		try{
			StoneCustomInfoBean obj = (StoneCustomInfoBean) getRequestObject(StoneCustomInfoBean.class);
			if(isWGS && TextUtil.isNotNull(obj.getLatitude()) && TextUtil.isNotNull(obj.getLongitude())){
				Point point = MapUtil.gcj_wgs_encrypts(Double.parseDouble(obj.getLatitude()), Double.parseDouble(obj.getLongitude()));
       			obj.setLatitude(point.getLat()+"");
       			obj.setLongitude(point.getLng()+"");
			}
			if(TextUtil.isNotNull(super.realName)){
				obj.setModifier(super.realName);
			}
			if(TextUtil.isNull(obj.getInt_id())){
				int result = stoneCustomService.insertStoneCustom(obj);
				obj.setInt_id(result);
				sendResponse(Integer.valueOf(0), "修改成功。");
			}else{
				int i = stoneCustomService.updateStoneCustom(obj);
				if (i > 0) {
					sendResponse(Integer.valueOf(0), "修改成功。");
				}
			}
		}catch(Exception e){
			this.exception = e;
		    sendResponse(Integer.valueOf(3), "应用服务器异常。");
		    log.error("PDAStoneCustom.updateStoneCustom ERROR\nJsonRequest:" + getJsonRequest() + "\n" + getJsonResponse(), e);
		}
		return "success";
	}
	
	/**
	 * 添加标石
	 * @return
	 */
	public String insertStoneCustom(){
		try{
			StoneCustomInfoBean obj = (StoneCustomInfoBean) getRequestObject(StoneCustomInfoBean.class);
			if(this.checkStone(obj.getZh_label()) > 0){
				sendResponse(Integer.valueOf(2), "标石名称已占用。");
			}else{
				if(isWGS && TextUtil.isNotNull(obj.getLatitude()) && TextUtil.isNotNull(obj.getLongitude())){
					Point point = MapUtil.gcj_wgs_encrypts(Double.parseDouble(obj.getLatitude()), Double.parseDouble(obj.getLongitude()));
	       			obj.setLatitude(point.getLat()+"");
	       			obj.setLongitude(point.getLng()+"");
				}
				obj.setCreator(realName);
				int result = stoneCustomService.insertStoneCustom(obj);
				obj.setInt_id(result);
		
				sendResponse(Integer.valueOf(0), obj);
			}
		}catch(Exception e){
			this.exception = e;
		    sendResponse(Integer.valueOf(3), "应用服务器异常。");
		    log.error("PDAStoneCustom.insertStoneCustom ERROR\nJsonRequest:" + getJsonRequest() + "\n" + getJsonResponse(), e);
		}
		return "success";
	}
	
	/**
	 * 删除标石
	 * @return
	 */
	public String deleteStoneCustom() {
		try {
			StoneCustomInfoBean stone = (StoneCustomInfoBean) getRequestObject(StoneCustomInfoBean.class);
			if (stone != null) {
				stone.setModifier(realName);
				this.stoneCustomService.deleteStoneCustom(stone);
				sendResponse(Integer.valueOf(0), "删除成功");
			} else {
				sendResponse(Integer.valueOf(2), "请求参数错误。");
			}
		} catch (Exception e) {
			this.exception = e;
			sendResponse(Integer.valueOf(3), "应用服务器异常。");
			log.error("PDAStoneCustom.deleteStoneCustom ERROR\nJsonRequest:"
					+ getJsonRequest() + "\n" + getJsonResponse(), e);
		}
		return "success";
	}
	
	/**
	 * 标石名称重复
	 * @param name
	 * @return
	 */
	public Integer checkStone(String name){
		String sql = "select count(*) from rms_landmark where zh_label='"+name+"' and stateflag='0'";
		int size = this.getJdbcTemplate().queryForInt(sql);
		return size;
	}
	
	public IStoneCustomService getStoneCustomService() {
		return stoneCustomService;
	}

	public void setStoneCustomService(IStoneCustomService stoneCustomService) {
		this.stoneCustomService = stoneCustomService;
	}
	
	
}
