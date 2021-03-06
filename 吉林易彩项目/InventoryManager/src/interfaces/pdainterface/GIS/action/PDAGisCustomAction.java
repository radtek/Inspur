package interfaces.pdainterface.GIS.action;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import manage.equt.pojo.EqutInfoBean;
import manage.generator.pojo.SiteBaseInfoBean;
import manage.generator.pojo.StationBaseInfoBean;
import manage.opticalTerminal.pojo.OpticalTerminalObj;
import manage.route.pojo.FiberBoxInfoBean;

import org.apache.log4j.Logger;

import interfaces.pdainterface.GIS.pojo.GisPojo;
import interfaces.pdainterface.GIS.service.impl.IPDAGisCustomService;
import interfaces.pdainterface.equt.pojo.EqutJiLinInfoBean;
import base.util.MapUtil;
import base.util.TextUtil;
import base.util.functions;
import base.util.pojo.Point;
import base.web.InterfaceAction;

/**
 * GIS查询统一入口
 *
 */
public class PDAGisCustomAction extends InterfaceAction{
	private static final long serialVersionUID = 2699003050087687133L;
	private static final Logger log = Logger.getLogger(PDAGisCustomAction.class);
	private IPDAGisCustomService gisServiceCustom;
	/**
	 * gis 查询统一入口
	 * @return
	 */
	public String query(){
		try{
			GisPojo obj = (GisPojo) getRequestObject(GisPojo.class);
			if(isWGS && TextUtil.isNotNull(obj.getLat()) && TextUtil.isNotNull(obj.getLon())){
				Point point = MapUtil.phone_db_encrypt(Double.parseDouble(obj.getLat()),Double.parseDouble(obj.getLon()));
				obj.setLat(point.getLat()+"");
				obj.setLon(point.getLng()+"");
			}
			if(TextUtil.isNotNull(obj.getDistance()) && obj.getDistance() <200){
				obj.setDistance(20000);
			}
			if(TextUtil.isNull(obj.getDistance())){
				obj.setDistance(20000);
			}
			List<GisPojo> list = new ArrayList<GisPojo>(); 
			String resutlt = "";
			if(obj.getType().equals("site")){
				//站点
				list = this.getStation(obj);
			}else if(obj.getType().equals("optical")){
				//光交
				list = this.getEqut(obj);
			}else if(obj.getType().equals("pipe")){
				//管井
				list = this.getPipe(obj);
			}else if(obj.getType().equals("hangWall")){
				//撑点
				list = this.getPipe(obj);
			}else if(obj.getType().equals("opticalTerminal")){
				//光终端盒
				list = this.getOpticalTerminal(obj);
			}else if(obj.getType().equals("poleLine")){
				//电杆
				list = this.getPipe(obj);
			}else if(obj.getType().equals("buried")){
				//直埋
				list = this.getPipe(obj);
			}else if(obj.getType().equals("fiberBox")){
				//分纤箱
				list = this.getFiberBox(obj);
			}else if(obj.getType().equals("leadup")){
				
			}else if(obj.getType().equals("all")){
				//站点数据
				GisPojo site = (GisPojo) obj.clone();
				site.setType("site");
				List<GisPojo> siteList = this.getStation(site);
				list.addAll(siteList);
				//光交数据
				GisPojo equt = (GisPojo) obj.clone();
				equt.setType("optical");
				List<GisPojo> optList = this.getEqut(equt);
				list.addAll(optList);
				//电杆数据
				GisPojo poleLine = (GisPojo) obj.clone();
				poleLine.setType("poleLine");
				List<GisPojo> poleLineList = this.getPipe(poleLine);
				list.addAll(poleLineList);
				//标石数据
				GisPojo stone = (GisPojo) obj.clone();
				stone.setType("buried");
				stone.setIsSys("true");
				List<GisPojo> stoneList = this.getPipe(stone);
				list.addAll(stoneList);
				//管井数据
				GisPojo pipe = (GisPojo) obj.clone();
				pipe.setType("pipe");
				List<GisPojo> pipeList = this.getPipe(pipe);
				list.addAll(pipeList);
			}else if(obj.getType().equals("sys")){
				//查询管井数据
				GisPojo well = (GisPojo) obj.clone();
				well.setType("pipe");
				well.setIsSys("true");
				List<GisPojo> wellList = this.getPipe(well);
				list.addAll(wellList);
				//杆路数据
				GisPojo pole = (GisPojo) obj.clone();
				pole.setType("poleLine");
				pole.setIsSys("true");
				List<GisPojo> poleList = this.getPipe(pole);
				list.addAll(poleList);
				//标石数据
				GisPojo stone = (GisPojo) obj.clone();
				stone.setType("buried");
				stone.setIsSys("true");
				List<GisPojo> stoneList = this.getPipe(stone);
				list.addAll(stoneList);
				//站点数据
				GisPojo site = (GisPojo) obj.clone();
				site.setType("site");
				List<GisPojo> siteList = this.getStation(site);
				list.addAll(siteList);
				//光交数据
				GisPojo equt = (GisPojo) obj.clone();
				equt.setType("optical");
				List<GisPojo> optList = this.getEqut(equt);
				list.addAll(optList);
			}
			//引上数据
			GisPojo lead = (GisPojo)obj.clone();
			lead.setType("leadup");
			List<GisPojo> leadList = this.getLeadUp(lead);
			list.addAll(leadList);
			
			
			resutlt = this.getGisStr(list, obj.getType());
			sendResponse(Integer.valueOf(0), resutlt);
		}catch(Exception e){
			e.printStackTrace();
			this.exception = e;
		    sendResponse(Integer.valueOf(3), "应用服务器异常。");
		    log.error("PDAGis.query ERROR\nJsonRequest:" + getJsonRequest() + "\n" + getJsonResponse(), e);
		}
		return "success";
	}
	
	
	
	/**
	 * 得到站点数据
	 * @param obj
	 * @return
	 * @throws ParseException 
	 */
	List<GisPojo> getStation (GisPojo obj) throws ParseException{
		List<GisPojo> list = new ArrayList<GisPojo>();
		SiteBaseInfoBean site = new SiteBaseInfoBean();
		/*site.setSite_latitude(obj.getLat());
		site.setSite_longitude(obj.getLon());
		site.setZh_label(obj.getName());*/
		site.setStart(0);
		site.setLimit(1000);
		
		List<SiteBaseInfoBean> siteList = this.gisServiceCustom.getStation(site);
		for(SiteBaseInfoBean object: siteList){
	    	  //封装数据
	    	  GisPojo pojo = new GisPojo();
	    	  pojo.setStartId(String.valueOf(object.getInt_id()));
	    	  pojo.setStartName(object.getZh_label());
	    	  pojo.setStartLat(object.getSite_latitude().toString());
	    	  pojo.setStartLon(object.getSite_longitude().toString());
	    	  pojo.setStartType("site");
	    	  list.add(pojo);
	    }
		return list;
	}
	
	List<GisPojo> getFiberBox(GisPojo obj){
		List<GisPojo> list = new ArrayList<GisPojo>();
		FiberBoxInfoBean fiberBox = new FiberBoxInfoBean();
		fiberBox.setLatitude(obj.getLat());
		fiberBox.setLongitude(obj.getLon());
		fiberBox.setFiberboxName(obj.getName());
		if ((fiberBox.getLatitude() != null) && (!(fiberBox.getLatitude().equals(""))) && 
				(fiberBox.getLongitude()!= null) && (!(fiberBox.getLongitude().equals("")))) {
			double[] arr = functions.getAround(Double.parseDouble(fiberBox.getLatitude()), Double.parseDouble(fiberBox.getLongitude()), obj.getDistance());
			fiberBox.setLats(String.valueOf(arr[0]));
			fiberBox.setLons(String.valueOf(arr[1]));
			fiberBox.setLate(String.valueOf(arr[2]));
			fiberBox.setLone(String.valueOf(arr[3]));
		}else{
			fiberBox.setStart(0);
			fiberBox.setLimit(100);
		}
		List<FiberBoxInfoBean> fiberList = this.gisServiceCustom.getFiberBox(fiberBox);
		for(FiberBoxInfoBean object : fiberList){
			GisPojo pojo  = new GisPojo();
			 pojo.setStartId(object.getId()+"");
			 pojo.setStartName(object.getFiberboxName());
			 pojo.setStartLat(object.getLatitude());
			 pojo.setStartLon(object.getLongitude());
			 pojo.setStartType("fiberBox");
			 list.add(pojo);
		}
		return list;
	}
	
	/**
	 * 得到光终端盒
	 * @param obj
	 * @return
	 */
	List<GisPojo> getOpticalTerminal(GisPojo obj){
		List<GisPojo> list = new ArrayList<GisPojo>();
		OpticalTerminalObj optTerminal = new OpticalTerminalObj();
		optTerminal.setLatitude(obj.getLat());
		optTerminal.setLongitude(obj.getLon());
		optTerminal.setTerminalName(obj.getName());
		if ((optTerminal.getLatitude() != null) && (!(optTerminal.getLatitude().equals(""))) && 
				(optTerminal.getLongitude()!= null) && (!(optTerminal.getLongitude().equals("")))) {
			double[] arr = functions.getAround(Double.parseDouble(optTerminal.getLatitude()), Double.parseDouble(optTerminal.getLongitude()), obj.getDistance());
			optTerminal.setLats(String.valueOf(arr[0]));
			optTerminal.setLons(String.valueOf(arr[1]));
			optTerminal.setLate(String.valueOf(arr[2]));
			optTerminal.setLone(String.valueOf(arr[3]));
		}else{
			optTerminal.setStart(0);
			optTerminal.setLimit(100);
		}
		List<OpticalTerminalObj> otoList = this.gisServiceCustom.getOpticalTerminal(optTerminal);
		for(OpticalTerminalObj object : otoList){
			 GisPojo pojo  = new GisPojo();
			 pojo.setStartId(object.getId()+"");
			 pojo.setStartName(object.getTerminalName());
			 pojo.setStartLat(object.getLatitude());
			 pojo.setStartLon(object.getLongitude());
			 pojo.setStartType("opticalTerminal");
			 list.add(pojo);
		 }
		return list;
	}
	
	
	/**
	 * 得到光交箱信息
	 * @param obj
	 * @return
	 */
	List<GisPojo> getEqut(GisPojo obj){
		List<GisPojo> list = new ArrayList<GisPojo>();
		EqutJiLinInfoBean equt = new EqutJiLinInfoBean();
		/*equt.setLatitude(obj.getLat());
		equt.setLongitude(obj.getLon());
		equt.setZh_label(obj.getName());*/
		 List<EqutJiLinInfoBean> equtList = this.gisServiceCustom.getEqut(equt);
		 for(EqutJiLinInfoBean object : equtList){
			 GisPojo pojo  = new GisPojo();
			 pojo.setStartId(object.getInt_id()+"");
			 pojo.setStartName(object.getZh_label());
			 pojo.setStartLat(object.getLatitude());
			 pojo.setStartLon(object.getLongitude());
			 pojo.setStartType("optical");
			 list.add(pojo);
		 }
		return list;
	}
	
	
	/**
	 * 得到管线
	 * @param obj
	 * @return
	 */
	List<GisPojo> getPipe(GisPojo obj){
		List<GisPojo> list = new ArrayList<GisPojo>();
		if ((obj.getLat() != null) && (!(obj.getLat().equals(""))) && 
		   (obj.getLon() != null) && (!(obj.getLon().equals("")))) {
			   double[] arr = functions.getAround(Double.parseDouble(obj.getLat()), Double.parseDouble(obj.getLon()), obj.getDistance());
			   obj.setLatl(String.valueOf(arr[0]));
			   obj.setLonl(String.valueOf(arr[1]));
			   obj.setLath(String.valueOf(arr[2]));
			   obj.setLonh(String.valueOf(arr[3]));
		}
		list = this.gisServiceCustom.getGisLine(obj);
		return list;
	}
	
	
	/**
	 * 得到引上
	 * @param obj
	 * @return
	 */
	List<GisPojo> getLeadUp(GisPojo obj){
		List<GisPojo> list = new ArrayList<GisPojo>();
		if ((obj.getLat() != null) && (!(obj.getLat().equals(""))) && 
			(obj.getLon() != null) && (!(obj.getLon().equals("")))) {
			double[] arr = functions.getAround(Double.parseDouble(obj.getLat()), Double.parseDouble(obj.getLon()), obj.getDistance());
			obj.setLatl(String.valueOf(arr[0]));
			obj.setLonl(String.valueOf(arr[1]));
			obj.setLath(String.valueOf(arr[2]));
			obj.setLonh(String.valueOf(arr[3]));
		}
		obj.setType("leadup");
		list = this.gisServiceCustom.getLeadUp(obj);
		return list;
	}
	
	/**
	 * 
	 * @param list
	 * @param type
	 * @return
	 */
	String getGisStr(List<GisPojo> list,String type){
		StringBuffer jsonStr = new StringBuffer();
		jsonStr .append("[");
		for (GisPojo pojo : list) {
			if(isWGS){
				Point startPoint = MapUtil.db_phone_encrypt(Double.parseDouble(pojo.getStartLat()), Double.parseDouble(pojo.getStartLon()));
				pojo.setStartLat(startPoint.getLat()+"");
				pojo.setStartLon(startPoint.getLng()+"");
				if(TextUtil.isNotNull(pojo.getEndId())){
					Point endPoint =  MapUtil.db_phone_encrypt(Double.parseDouble(pojo.getEndLat()), Double.parseDouble(pojo.getEndLon()));
					pojo.setEndLat(endPoint.getLat()+"");
					pojo.setEndLon(endPoint.getLng()+"");
				}
			}
			jsonStr.append("{");
			if(TextUtil.isNotNull(pojo.getLineId())){
				jsonStr.append("'id':'"+(pojo.getLineId() == null ? "" : pojo.getLineId())+"',");
				jsonStr.append(" 'name':'"+(pojo.getLineName() == null ? "" : pojo.getLineName())+"',");
				jsonStr.append("'type':'"+(pojo.getLineType() == null ? "" : pojo.getLineType())+"',");
			}
			/*if(TextUtil.isNull(pojo.getStartSys()) || !pojo.getStartSys().equals("false")) {
				pojo.setStartSys("true");
			}
			if(TextUtil.isNull(pojo.getEndSys()) || !pojo.getEndSys().equals("false")) {
				pojo.setEndSys("true");
			}*/
			jsonStr.append("start:{'id':'" + pojo.getStartId() + "',");
			jsonStr.append("'name':'" + pojo.getStartName() + "',");
			jsonStr.append("'lat':'" + pojo.getStartLat()+ "',");
			jsonStr.append("'lon':'" + pojo.getStartLon() + "',");
			//jsonStr.append("'isSys':'" + pojo.getStartSys() + "',");
			jsonStr.append("'type':'" + pojo.getStartType()+ "'}");
			if(TextUtil.isNotNull(pojo.getEndId())){
				jsonStr.append(",end:{");
				jsonStr.append("'id':'" + pojo.getEndId() + "',");
				jsonStr.append("'name':'" + pojo.getEndName() + "',");
				jsonStr.append("'lat':'" + pojo.getEndLat()+ "',");
				jsonStr.append("'lon':'" + pojo.getEndLon() + "',");
				//jsonStr.append("'isSys':'" + pojo.getEndSys() + "',");
				jsonStr.append("'type':'" + pojo.getEndType()+ "'}");
			}
			jsonStr.append("},");
		}
		if (jsonStr.toString().endsWith(",")) {
			jsonStr.delete(jsonStr.length() - 1, jsonStr.length());
		}
		jsonStr.append("]");
		return jsonStr.toString();
	}
	
	/**
	 * 删除资源点信息
	 * @return
	 */
	public String delRes(){
		try{
			List<GisPojo> list = (List<GisPojo>) getRequestObject(GisPojo.class);
			if(list != null && TextUtil.isNotNull(list)){
				gisServiceCustom.delRes(list,super.realName);
				sendResponse(Integer.valueOf(0), "删除成功。");
			}else{
				sendResponse(Integer.valueOf(2), "请求参数错误。");
			}
		}catch(Exception e){
			this.exception = e;
		    sendResponse(Integer.valueOf(3), "应用服务器异常。");
		    log.error("PDAGis.delRes ERROR\nJsonRequest:" + getJsonRequest() + "\n" + getJsonResponse(), e);
		}
		return "success";
	}
	
	
	/**
	 * 截断段信息
	 * @return
	 */
	public String cutOffSeg(){
		try{
			GisPojo obj = (GisPojo) getRequestObject(GisPojo.class);
			if(obj != null){
				String result = this.gisServiceCustom.cutoffSeg(obj);
				if(result.equals("success")){
					sendResponse(Integer.valueOf(0), "截取成功!");
				}else{
					sendResponse(Integer.valueOf(1), "截取失败!");
				}
			}else{
				sendResponse(Integer.valueOf(2), "请求参数错误。");	
			}
		}catch(Exception e){
			this.exception = e;
		    sendResponse(Integer.valueOf(3), "应用服务器异常。");
		    log.error("PDAGis.cutOffSeg ERROR\nJsonRequest:" + getJsonRequest() + "\n" + getJsonResponse(), e);
		}
		return "success";
	}
	
	public static Logger getLog() {
		return log;
	}

	public IPDAGisCustomService getGisServiceCustom() {
		return gisServiceCustom;
	}

	public void setGisServiceCustom(IPDAGisCustomService gisServiceCustom) {
		this.gisServiceCustom = gisServiceCustom;
	}

}
