package base.web;

import base.session.SessionContext;
import base.util.Base64;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import interfaces.pdainterface.interfaceUtil.ReturnData;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.jdbc.core.JdbcTemplate;

import weblogic.j2eeclient.java.javaURLContextFactory;
import manage.user.pojo.UserInfoBean;

public abstract class InterfaceAction extends PaginationAction {
	private static final long serialVersionUID = -5091430962158346627L;
	private ReturnData jsonResponse;
	private String jsonRequest;
	private JdbcTemplate jdbcTemplate;
	private String UID;
	protected Exception exception;
	protected static final String EXCUTE_SUCCESS = "success";
	public Integer start = 0;
	public Integer limit = 7;
	public String longiner;//记录当前登录人
	public String realName;
	public String invokTime;
	public String areaName;//记录登录人的区县
	public static boolean isWGS;
	public static boolean toIrms;
	public static boolean fromIrms;
	public static boolean forceDel;
	public static boolean isSecret; 
	static {
		try {
			String gpsType = "gcj";
			String irmsType = "false";
			String dataFrom = "false";
			String forceDelStr = "true";
			String secretType = "false";
			if (gpsType.equals("wgs") || gpsType.equals("gcj")) {
				isWGS = true;
			} else {
				isWGS = false;
			}
			if(forceDelStr.equals("true")){
				forceDel=true;
			}else{
				forceDel=false;
			}
			if (irmsType.equals("true")) {
				toIrms = true;
			} else {
				toIrms = false;
			}
			if(dataFrom.equals("true")){
				fromIrms =true;
			}else{
				fromIrms = false;
			}
			if(secretType.equals("true")){
				isSecret = true;
			}else{
				isSecret = false;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 得到手机端传递的参数
	 * @param classOfT
	 * @return
	 * @throws Exception 
	 */
	protected Object getRequestObject(Class classOfT)  {
		List requestlist = new ArrayList();
		try{
			if((this.jsonRequest==null) || (this.jsonRequest.equals(""))){
				return null;
			}else{	
				Date date = new Date();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				invokTime = sdf.format(date);
				/*if(this.start!=0){
					this.start = this.start*this.limit;
				}*/
				if(this.areaName == null || this.areaName.equals(properties.getValueByKey("province"))){
					this.areaName = null;
				}
				//进行解密处理
				if(isSecret){
					String decodedString = java.net.URLDecoder.decode(jsonRequest);
					jsonRequest = Base64.getFromBase64(decodedString);
				}
				if(getInterfaceSession() != null ){
					UserInfoBean user = (UserInfoBean)getInterfaceSession().getAttribute("userBean");
					realName = user.getRealname();
				}
				Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss Z").create();
				if(!(jsonRequest.startsWith("["))) {
					return gson.fromJson(jsonRequest, classOfT);
				}
				JsonParser parser = new JsonParser();
				JsonArray Jarray = parser.parse(jsonRequest).getAsJsonArray();
				for(JsonElement obj:Jarray){
					Object cse = gson.fromJson(obj,classOfT);
					requestlist.add(cse);
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return requestlist;
	}
	
	
	/**
	 * 返回手机端
	 * @param result
	 * @param info
	 */
	protected void sendResponse(Integer result, String info){
		if(this.jsonResponse==null){
			this.jsonResponse = new ReturnData();
		}
		this.jsonResponse.setResult(result);
		//进行加密
		if(isSecret){
			try{
				//info = java.net.URLEncoder.encode(info,"UTF-8");
			}catch(Exception e){
				e.printStackTrace();
			}
			info = Base64.getBase64(info);
		}
		this.jsonResponse.setInfo(info);
		UserInfoBean user = null;
		if (getInterfaceSession() != null) {
			user = (UserInfoBean) getInterfaceSession().getAttribute("userBean");
		}
	}

	/**
	 * 返回手机端
	 * @param result
	 * @param object
	 */
	protected void sendResponse(Integer result, Object object) {
		if (this.jsonResponse == null)
			this.jsonResponse = new ReturnData();
		String objectjson = setResponseObject(object);
		
		sendResponse(result, objectjson);
	}


	public HttpSession getInterfaceSession() {
		SessionContext sc = SessionContext.getInstance();
		return sc.getSession(this.UID);
	}

	public void setInterfaceSessionAttribute(String UID, String attrName,
			Object attrObj) {
		SessionContext sc = SessionContext.getInstance();
		sc.getSession(UID).setAttribute(attrName, attrObj);
	}

	protected String setResponseObject(Object object) {
		Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss Z")
				.create();
		return gson.toJson(object);
	}

	protected String getInfo() {
		return this.jsonResponse.getInfo();
	}

	protected void setInfo(String info) {
		if (this.jsonResponse == null)
			this.jsonResponse = new ReturnData();
		this.jsonResponse.setInfo(info);
	}

	protected Integer getResult() {
		return this.jsonResponse.getResult();
	}

	protected void setResult(Integer result) {
		if (this.jsonResponse == null)
			this.jsonResponse = new ReturnData();
		this.jsonResponse.setResult(result);
	}

	public String getLonginer() {
		return longiner;
	}

	public void setLonginer(String longiner) {
		this.longiner = longiner;
	}
	public ReturnData getJsonResponse() {
		return this.jsonResponse;
	}
	public String getRealName() {
		return realName;
	}
	public void setRealName(String realName) {
		this.realName = realName;
	}
	public void setJsonResponse(ReturnData jsonResponse) {
		this.jsonResponse = jsonResponse;
	}

	public String getJsonRequest() {
		return this.jsonRequest;
	}

	public void setJsonRequest(String jsonRequest) {
		this.jsonRequest = jsonRequest;
	}

	public String getUID() {
		return this.UID;
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
	
	
	/**
	 * 
	 * @param city
	 * @return
	 */
	public String getCityStr(String city){
		int loc = 0 ;
		if(city.contains("省")){
			loc = city.indexOf("省");
		}else if(city.contains("市")){
			loc = city.indexOf("市");
		}else if(city.contains("县")){
			loc = city.indexOf("县");
		}else if(city.contains("区")){
			loc = city.indexOf("区");
		}else if(city.contains("自治")){
			loc = city.indexOf("自治");
		}else if(city.contains("分")){
			loc = city.indexOf("分");
		}else if(city.contains("州")){
			loc = city.indexOf("州");
		}else if(city.contains("移动")){
			loc = city.indexOf("移动");
		}else{
			loc = city.length();
		}
		String newStr = city.substring(0, loc);
		return newStr;
	}

	public void setUID(String uID) {
		if (uID.contains(","))
			uID = uID.substring(0, uID.indexOf(","));
		this.UID = uID;
	}

	public Exception getException() {
		return this.exception;
	}

	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	public String getInvokTime() {
		return invokTime;
	}

	public void setInvokTime(String invokTime) {
		this.invokTime = invokTime;
	}
	public void setException(Exception exception) {
		this.exception = exception;
	}
	public String getAreaName() {
		return areaName;
	}
	public void setAreaName(String areaName) {
		this.areaName = this.getCityStr(areaName);
	}
}