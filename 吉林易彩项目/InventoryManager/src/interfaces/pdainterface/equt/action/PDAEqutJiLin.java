package interfaces.pdainterface.equt.action;

import base.util.MapUtil;
import base.util.TextUtil;
import base.util.functions;
import base.util.pojo.Point;
import base.web.InterfaceAction;
import interfaces.pdainterface.equt.pojo.EqutJiLinInfoBean;
import interfaces.pdainterface.equt.service.PDAEqutInfoJiLinService;
import manage.pipe.pojo.WellCustomInfoBean;

import org.apache.log4j.Logger;

import java.util.List;

public class PDAEqutJiLin extends InterfaceAction {

	private static final long serialVersionUID = -3486300674796123267L;
	private static final Logger log = Logger.getLogger(PDAEqutJiLin.class);
	private PDAEqutInfoJiLinService pdaEqutInfoJiLinService;

	/**
	 * 得到光交接箱列表
	 * @return
	 */
	public String getEqutList(){	
		try{
			EqutJiLinInfoBean equt = (EqutJiLinInfoBean) getRequestObject(EqutJiLinInfoBean.class);
			equt.setStart(this.start);
			equt.setLimit(this.limit);
		    equt.setStateflag(0);
			List<EqutJiLinInfoBean> list = pdaEqutInfoJiLinService.getEqutList(equt);
			if(list !=null){
				//for(EqutJiLinInfoBean equt : list){
				//	if(isWGS && TextUtil.isNotNull(equt.getLatitude()) && TextUtil.isNotNull(equt.getLongitude())){
				//		Point point = MapUtil.db_phone_encrypt(Double.parseDouble(equt.getLatitude()), Double.parseDouble(equt.getLongitude()));
				//		equt.setLatitude(point.getLat()+"");
				//		equt.setLongitude(point.getLng()+"");
				//	}
				//}
			
				
				sendResponse(Integer.valueOf(0), list);
				
			}else{
				sendResponse(Integer.valueOf(2), "请求参数错误。");
			}
		}catch(Exception e){
			this.exception = e;
			sendResponse(Integer.valueOf(3), "应用服务器异常。");
			log.error("PDAEqutJiLin.getEqut ERROR\nJsonRequest:" + getJsonRequest() + "\n" + getJsonResponse(), e);
		}
		return "success";
	}

	/**
	 * 添加光交接箱
	 * @return
	 */
	public String insertEqut(){
		try{
			EqutJiLinInfoBean obj = (EqutJiLinInfoBean) getRequestObject(EqutJiLinInfoBean.class);
			if(this.checkEqut(obj.getZh_label()) > 0){
				sendResponse(Integer.valueOf(2), "名称已占用。");
			}else{
				// 删除标识
				obj.setStateflag(0);
				//登陆用户
				obj.setCreator(this.longiner);
				// 添加
				pdaEqutInfoJiLinService.insertEqut(obj);
				//成功
				sendResponse(Integer.valueOf(0), obj);
			}
		}catch(Exception e){
			this.exception = e;
			sendResponse(Integer.valueOf(3), "应用服务器异常。");
			log.error("PDAEqutJiLin.insertEqut ERROR\nJsonRequest:" + getJsonRequest() + "\n" + getJsonResponse(), e);
		}
		return "success";
	}

	/**
	 * 得到光交接箱详情
	 * @return
	 */
	public String getEqut(){

		try{
			EqutJiLinInfoBean obj = (EqutJiLinInfoBean) getRequestObject(EqutJiLinInfoBean.class);
			EqutJiLinInfoBean obj_result = pdaEqutInfoJiLinService.getEqut(obj);
			if(obj_result !=null){
				sendResponse(Integer.valueOf(0), obj_result);
			}else{
				sendResponse(Integer.valueOf(2), "请求参数错误。");
			}
		}catch(Exception e){
			this.exception = e;
			sendResponse(Integer.valueOf(3), "应用服务器异常。");
			log.error("PDAEqutJiLin.getEqut ERROR\nJsonRequest:" + getJsonRequest() + "\n" + getJsonResponse(), e);
		}
		return "success";

	}
	/**
	 * 更新光交接箱
	 * @return
	 */
	public String updateEqut(){
		try{
			EqutJiLinInfoBean obj = (EqutJiLinInfoBean) getRequestObject(EqutJiLinInfoBean.class);
			if(TextUtil.isNotNull(obj.getInt_id())) {
				//添加
				pdaEqutInfoJiLinService.updateEqut(obj);
				//成功
				sendResponse(Integer.valueOf(0), obj);
			} else {
				sendResponse(Integer.valueOf(2), "非法操作。");
			}
		}catch(Exception e){
			this.exception = e;
			sendResponse(Integer.valueOf(3), "应用服务器异常。");
			log.error("PDAEqutJiLin.updateEqut ERROR\nJsonRequest:" + getJsonRequest() + "\n" + getJsonResponse(), e);
		}
		return "success";
	}

	/**
	 * 删除光交接箱
	 * @return
	 */
	public String deleteEqut(){
		try{
			EqutJiLinInfoBean obj = (EqutJiLinInfoBean) getRequestObject(EqutJiLinInfoBean.class);

			if(TextUtil.isNotNull(obj.getInt_id())) {
				pdaEqutInfoJiLinService.deleteEqut(obj);
				//成功
				sendResponse(Integer.valueOf(0), obj);
			} else {
				sendResponse(Integer.valueOf(2), "非法操作。");
			}
		}catch(Exception e){
			this.exception = e;
			sendResponse(Integer.valueOf(3), "应用服务器异常。");
			log.error("PDAEqutJiLin.deleteEqut ERROR\nJsonRequest:" + getJsonRequest() + "\n" + getJsonResponse(), e);
		}
		return "success";
	}

	/**
	 * 检查光交接箱名称是否重复
	 * @param name
	 * @return
	 */
	public Integer checkEqut(String name){
		String sql = "select count(*) from rms_optele_case where zh_label='"+name+"' and stateflag='0'";
		int size = this.getJdbcTemplate().queryForInt(sql);
		return size;
	}

	public PDAEqutInfoJiLinService getPdaEqutInfoJiLinService() {
		return pdaEqutInfoJiLinService;
	}

	public void setPdaEqutInfoJiLinService(PDAEqutInfoJiLinService pdaEqutInfoJiLinService) {
		this.pdaEqutInfoJiLinService = pdaEqutInfoJiLinService;
	}

}