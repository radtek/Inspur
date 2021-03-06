package interfaces.pdainterface.supportingRing.action;

import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;

import base.exceptions.DataAccessException;
import base.util.MapUtil;
import base.util.TextUtil;
import base.util.pojo.Point;
import base.web.InterfaceAction;
import interfaces.pdainterface.supportingRing.pojo.SupportingRingInfoBean;
import interfaces.pdainterface.supportingRing.service.IPDASupportingRingService;

public class PDASupportingRingAction extends InterfaceAction{
	
	private static final long serialVersionUID = 6282554256283526096L;
	private static final Logger log = Logger.getLogger(PDASupportingRingAction.class);
	private IPDASupportingRingService PDASupportingRingService;
	
	/**
	 * 添加动环配套
	 * @return
	 */
	public String insertSupportingRing() {
		try {
			SupportingRingInfoBean supportingRing = (SupportingRingInfoBean) getRequestObject(SupportingRingInfoBean.class);
			if (supportingRing != null) {
				/*if(this.checkRing(supportingRing.getZh_label() , null ) >0){
					sendResponse(Integer.valueOf(2), "动环配套名称重复。");
				}else{
					if(isWGS){
						 Point point = MapUtil.phone_db_encrypt(Double.parseDouble(supportingRing.getLatitude()), Double.parseDouble(supportingRing.getLongitude()));
						 supportingRing.setLatitude(point.getLat()+"");
						 supportingRing.setLongitude(point.getLng()+"");
					}
					supportingRing.setCreator(realName);
					int id = this.PDASupportingRingService.insertSupportingRing(supportingRing);
					supportingRing.setInt_id(id);
					supportingRing.setStateflag(0);
					sendResponse(Integer.valueOf(0), supportingRing);
				}*/
				if(isWGS){
					 Point point = MapUtil.phone_db_encrypt(Double.parseDouble(supportingRing.getLatitude()), Double.parseDouble(supportingRing.getLongitude()));
					 supportingRing.setLatitude(point.getLat()+"");
					 supportingRing.setLongitude(point.getLng()+"");
				}
				supportingRing.setCreator(realName);
				int id = this.PDASupportingRingService.insertSupportingRing(supportingRing);
				supportingRing.setInt_id(id);
				supportingRing.setStateflag(0);
				sendResponse(Integer.valueOf(0), supportingRing);
				
			} else {
				sendResponse(Integer.valueOf(2), "请求参数错误。");
			}
		} catch (Exception e) {
			this.exception = e;
			sendResponse(Integer.valueOf(3), "应用服务器异常。");
			log.error("pdaSupportingRing.insertSupportingRing ERROR\nJsonRequest:"
					+ getJsonRequest() + "\n" + getJsonResponse(), e);
		}
		return "success";
	}
	/**
	 * 检查动环配套名称重复
	 * @param name 名称
	 * @param id 
	 * @return
	 */
	public Integer checkRing(String name,String id) {
		String checkSql = "select count(*) from rms_power where stateflag ='0' and zh_label ='"
				+ name + "'";
		if(TextUtil.isNotNull(id)){
			checkSql += " and int_id !='"+id+"'";
		}
		int size = this.getJdbcTemplate().queryForInt(checkSql);
		return size;
	}
	
	/**
	 * 动环配套查询入口
	 * @return
	 */
	public String getSupportingRing() {
		try {
			SupportingRingInfoBean supportingRing = (SupportingRingInfoBean) getRequestObject(
					SupportingRingInfoBean.class);
			if (supportingRing != null) {
				// 增加地市限制
				if (TextUtil.isNotNull(super.getAreaName())) {
					// supportingRing.setCity(this.getAreaName());
				}
				supportingRing.setStart(this.start);
				supportingRing.setLimit(this.limit);
				supportingRing.setStateflag(0);
				List<SupportingRingInfoBean> ringList = new LinkedList<SupportingRingInfoBean>();
				ringList = this.PDASupportingRingService.getSupportingRing(supportingRing);
				sendResponse(Integer.valueOf(0), ringList);
			} else {
				sendResponse(Integer.valueOf(2), "请求参数错误。");
			}
		} catch (Exception e) {
			this.exception = e;
			sendResponse(Integer.valueOf(3), "应用服务器异常。");
			log.error("pdaSupportingRing.getSupportingRing ERROR\nJsonRequest:" + getJsonRequest() + "\n" + getJsonResponse(), e);
		}
		return "success";
	}
	
	/**
	 * 更新动环配套
	 * @return
	 */
	public String updateSupportingRing() {
		try {
			SupportingRingInfoBean ringInfoBean = (SupportingRingInfoBean) getRequestObject(SupportingRingInfoBean.class);
			if (ringInfoBean != null) {
				if(TextUtil.isNotNull(ringInfoBean.getInt_id())){
					ringInfoBean.setModifier(realName);
					this.PDASupportingRingService.updateSupportingRing(ringInfoBean);
					sendResponse(Integer.valueOf(0), "修改成功。");
				}
			} else {
				sendResponse(Integer.valueOf(2), "请求参数错误。");
			}
		} catch (Exception e) {
			this.exception = e;
			sendResponse(Integer.valueOf(3), "应用服务器异常。");
			log.error("pdaSupportingRing.updateSupportingRing ERROR\nJsonRequest:"
					+ getJsonRequest() + "\n" + getJsonResponse(), e);
		}
		return "success";
	}
	
	/**
	 * 彻底删除
	 * @return
	 */
	public String deleteSupportingRing() {
		try {
			SupportingRingInfoBean ringInfoBean = (SupportingRingInfoBean) getRequestObject(SupportingRingInfoBean.class);
			if (ringInfoBean != null) {
				if(TextUtil.isNotNull(ringInfoBean.getInt_id())){
					this.PDASupportingRingService.deleteSupportingRing(ringInfoBean);
					sendResponse(Integer.valueOf(0), "删除成功。");
				}
			} else {
				sendResponse(Integer.valueOf(2), "请求参数错误。");
			}
		} catch (DataAccessException e) {
			this.exception = e;
			sendResponse(Integer.valueOf(3), "应用服务器异常。");
			log.error("pdaSupportingRing.deleteSupportingRing ERROR\nJsonRequest:"
					+ getJsonRequest() + "\n" + getJsonResponse(), e);
		}
		return "success";
	}
	
	/**
	 * 更新删除标志
	 * @return
	 */
	public String updateDeleteFlag() {
		try {
			SupportingRingInfoBean ringInfoBean = (SupportingRingInfoBean) getRequestObject(SupportingRingInfoBean.class);
			if (ringInfoBean != null) {
				if(TextUtil.isNotNull(ringInfoBean.getInt_id())){
					this.PDASupportingRingService.updateDeleteFlag(ringInfoBean);
					sendResponse(Integer.valueOf(0), "删除成功。");
				}
			} else {
				sendResponse(Integer.valueOf(2), "请求参数错误。");
			}
		} catch (DataAccessException e) {
			this.exception = e;
			sendResponse(Integer.valueOf(3), "应用服务器异常。");
			log.error("pdaSupportingRing.updateDeleteFlag ERROR\nJsonRequest:"
					+ getJsonRequest() + "\n" + getJsonResponse(), e);
		}
		return "success";
	}
	
	public IPDASupportingRingService getPDASupportingRingService() {
		return PDASupportingRingService;
	}
	public void setPDASupportingRingService(IPDASupportingRingService pDASupportingRingService) {
		PDASupportingRingService = pDASupportingRingService;
	}
}
