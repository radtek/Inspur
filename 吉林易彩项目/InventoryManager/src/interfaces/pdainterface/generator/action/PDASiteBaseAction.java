package interfaces.pdainterface.generator.action;

import java.math.BigDecimal;
import java.util.List;

import org.apache.log4j.Logger;

import base.exceptions.DataAccessException;
import base.util.MapUtil;
import base.util.TextUtil;
import base.util.pojo.Point;
import base.web.InterfaceAction;
import interfaces.pdainterface.generator.service.IPDASiteBaseService;
import manage.generator.pojo.SiteBaseInfoBean;

public class PDASiteBaseAction extends InterfaceAction{
	private static final long serialVersionUID = -7536101699685019469L;
	private static final Logger log = Logger.getLogger(PDASiteBaseAction.class);
	private IPDASiteBaseService pdaSiteBaseService;
	
	/**
	 * 增加基站
	 * @return
	 */
	public String insertSiteBase() {
		try {
			SiteBaseInfoBean siteBase = (SiteBaseInfoBean) getRequestObject(SiteBaseInfoBean.class);
			if (siteBase != null) {
				if (this.checkStation(siteBase.getZh_label(),null) > 0) {
					sendResponse(Integer.valueOf(2), "基站名称重复。");
				}else{				
					if(isWGS){
						Point point = MapUtil.phone_db_encrypt(siteBase.getSite_latitude().doubleValue(), siteBase.getSite_longitude().doubleValue());
						siteBase.setSite_latitude(new BigDecimal(point.getLat()));
						siteBase.setSite_longitude(new BigDecimal(point.getLng()));
					}
					siteBase.setCreator(realName);
					int id = this.pdaSiteBaseService.insertSiteBase(siteBase);
					siteBase.setInt_id(Integer.valueOf(id));
					sendResponse(Integer.valueOf(0), siteBase);
				}
			} else {
				sendResponse(Integer.valueOf(2), "请求参数错误。");
			}
		} catch (Exception e) {
			this.exception = e;
			sendResponse(Integer.valueOf(3), "应用服务器异常。");
			log.error("pdagenerator.insertSiteBase ERROR\nJsonRequest:"
					+ getJsonRequest() + "\n" + getJsonResponse(), e);
		}
		return "success";
	}
	
	/**
	 * 检测基站
	 * @param name
	 * @return
	 */
	public Integer checkStation(String name,Integer id){
		String checkSql = "select count(*) from rms_bts where stateflag ='0' and zh_label='"
				+ name + "'";
		if(TextUtil.isNotNull(id)){
			checkSql += " and int_id !='"+id+"'";
		}
		Integer size = getJdbcTemplate().queryForInt(checkSql);
		return size;
	}
	
	/**
	 * 获取基站信息
	 * @return
	 */
	public String getSiteBase() {
		   try {
			   SiteBaseInfoBean siteBase = (SiteBaseInfoBean)getRequestObject(SiteBaseInfoBean.class);
		       if (siteBase != null) {
			       if ((siteBase.getSite_latitude() != null) && (!(siteBase.getSite_latitude().equals(""))) && (siteBase.getSite_longitude() != null) && (!(siteBase.getSite_longitude().equals("")))) {
			    	  if(isWGS){
			    		//Point point = MapUtil.phone_db_encrypt(Double.parseDouble(siteBase.getSite_latitude()), Double.parseDouble(siteBase.getSite_longitude()));
			    		//siteBase.setSite_latitude(point.getLat()+"");
			    		//siteBase.setSite_longitude(point.getLng()+"");
						  Point point = MapUtil.phone_db_encrypt(siteBase.getSite_latitude().doubleValue(), siteBase.getSite_longitude().doubleValue());
						  siteBase.setSite_latitude(new BigDecimal(point.getLat()));
						  siteBase.setSite_longitude(new BigDecimal(point.getLng()));
			    	  }  
			       }else{
			    	   siteBase.setStart(this.start);
			    	   siteBase.setLimit(this.limit);
			   }
			   siteBase.setStateflag(new BigDecimal(0));
		       List<SiteBaseInfoBean> siteBaseList = this.pdaSiteBaseService.getSiteBase(siteBase);
		       for(SiteBaseInfoBean obj: siteBaseList){
		    	   if(isWGS){
		    		   //Point point = MapUtil.db_phone_encrypt(Double.parseDouble(obj.getSite_latitude()), Double.parseDouble(obj.getSite_longitude()));
		      		   //obj.setSite_latitude(point.getLat()+"");
		      		   //obj.setSite_longitude(point.getLng()+"");
					   Point point = MapUtil.phone_db_encrypt(obj.getSite_latitude().doubleValue(), obj.getSite_longitude().doubleValue());
					   obj.setSite_latitude(new BigDecimal(point.getLat()));
					   obj.setSite_longitude(new BigDecimal(point.getLng()));
		    	   }
		       }
		       sendResponse(Integer.valueOf(0), siteBaseList); 
		     }else{
		        sendResponse(Integer.valueOf(2), "请求参数错误。");
		     }
		   }
		   catch (Exception e) {
		     this.exception = e;
		     sendResponse(Integer.valueOf(3), "应用服务器异常。");
		     log.error("pdagenerator.getSiteBase ERROR\nJsonRequest:" + getJsonRequest() + "\n" + getJsonResponse(), e);
		   }
		   return "success";
		 }
	
	
	/**
	 * 更新基站
	 * @return
	 */
	public String updateSiteBase() {
		try {
			SiteBaseInfoBean siteBase = (SiteBaseInfoBean) getRequestObject(SiteBaseInfoBean.class);
			if (siteBase != null) {
				if(this.checkStation(siteBase.getZh_label(),siteBase.getInt_id()) >0){
					sendResponse(Integer.valueOf(2), "基站名称重复。");
				}else{
					if(isWGS){
						//Point point = MapUtil.phone_db_encrypt(Double.parseDouble(siteBase.getSite_latitude()), Double.parseDouble(siteBase.getSite_longitude()));
			    		//siteBase.setSite_latitude(point.getLat()+"");
			    		//siteBase.setSite_longitude(point.getLng()+"");
						Point point = MapUtil.phone_db_encrypt(siteBase.getSite_latitude().doubleValue(), siteBase.getSite_longitude().doubleValue());
						siteBase.setSite_latitude(new BigDecimal(point.getLat()));
						siteBase.setSite_longitude(new BigDecimal(point.getLng()));
					}
					if(TextUtil.isNotNull(siteBase.getInt_id())){
						siteBase.setUpdate_person(realName);
						siteBase.setModifier(realName);
						this.pdaSiteBaseService.updateSiteBase(siteBase);
						sendResponse(Integer.valueOf(0), "修改成功");
					}else{
						int id = this.pdaSiteBaseService.insertSiteBase(siteBase);
						siteBase.setInt_id(id);
						sendResponse(Integer.valueOf(0), siteBase);
					}
				}
			} else {
				sendResponse(Integer.valueOf(2), "请求参数错误。");
			}
		} catch (Exception e) {
			this.exception = e;
			sendResponse(Integer.valueOf(3), "应用服务器异常。");
			log.error("pdagenerator.updateSiteBase ERROR\nJsonRequest:"
					+ getJsonRequest() + "\n" + getJsonResponse(), e);
		}
		return "success";
	}
	
	/**
	 * 删除基站
	 * @return
	 */
	public String deleteSiteBase() {
		try {
			SiteBaseInfoBean siteBase = (SiteBaseInfoBean) getRequestObject(SiteBaseInfoBean.class);
			if (siteBase != null) {
				if(TextUtil.isNotNull(siteBase.getInt_id())){
					this.pdaSiteBaseService.deleteSiteBase(siteBase);
					sendResponse(Integer.valueOf(0), "删除成功。");
				}
			} else {
				sendResponse(Integer.valueOf(2), "请求参数错误。");
			}
		} catch (DataAccessException e) {
			this.exception = e;
			sendResponse(Integer.valueOf(3), "应用服务器异常。");
			log.error("pdagenerator.deleteSiteBase ERROR\nJsonRequest:"
					+ getJsonRequest() + "\n" + getJsonResponse(), e);
		}
		return "success";
	}
	
	public IPDASiteBaseService getPdaSiteBaseService() {
		return pdaSiteBaseService;
	}
	public void setPdaSiteBaseService(IPDASiteBaseService pdaSiteBaseService) {
		this.pdaSiteBaseService = pdaSiteBaseService;
	}
		
}
