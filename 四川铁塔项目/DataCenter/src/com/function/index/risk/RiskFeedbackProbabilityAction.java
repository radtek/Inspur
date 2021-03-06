package com.function.index.risk;
import java.util.ArrayList;
/**
 * @author fengfeng02
 * 
 */
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.system.LoginUserUtil;
import com.systemConfig.model.DataTableResult;
@Controller("com.function.index.risk.RiskFeedbackProbabilityAction")
@RequestMapping(value="/RiskFeedbackProbabilityAction")
public class RiskFeedbackProbabilityAction{

	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	@Autowired
	private LoginUserUtil loginUserUtil;
	
	private static String resUserName = "TOWERCRNOP";
	
	/*
	 * 	查询风险点反馈率明细
	 * 
	 * */
	@RequestMapping("/findriskFeedbackProbabilityDatas.ilf")
	public void findriskFeedbackProbabilityDatas(@RequestParam String tableparam,@RequestParam String conditions,HttpServletRequest request,HttpServletResponse response)throws Exception{
		Long sEcho = 0L;
		Integer displayStart = 0;
		Integer iDisplayLength = 0;
		JSONArray jsons = JSONArray.fromObject(tableparam);
		HashMap<String,Object> conditonMap = new HashMap<String,Object>();
		if(jsons!=null && jsons.size()!=0){
			for(int i=0;i<jsons.size();i++){
				JSONObject json = JSONObject.fromObject(jsons.get(i));
				String key = json.getString("name");
				if(key.equals("sEcho")){
					sEcho = Long.parseLong(json.getString("value"));
				}else if(key.equals("iDisplayStart")){
					displayStart = Integer.parseInt(json.getString("value"));
					conditonMap.put("iDisplayStart",displayStart);
				}else if(key.equals("iDisplayLength")){
					iDisplayLength = Integer.parseInt(json.getString("value"));
					conditonMap.put("iDisplayLength",iDisplayLength);
				}
			}
		}
		JSONArray condition = JSONArray.fromObject(conditions);
		if(conditions!=null && condition.size()!=0){
			for(int i=0;i<condition.size();i++){
				JSONObject jsonObject = JSONObject.fromObject(condition.get(i));
				if(jsonObject.get("value")!=null && !"".equals(jsonObject.getString("value"))){
					conditonMap.put(jsonObject.getString("name"),jsonObject.getString("value"));
				}
			}
		}		
		Boolean IS_PROVICE = false;
		String CITY_NAME = "";
		Object loginObject = request.getSession().getAttribute("LoginUserInfo");
		if(loginObject!=null){
			Map<String,Object> loginUser = (HashMap<String,Object>)loginObject;
			if(loginUser.get("BELONG_AREA").toString().indexOf("四川")!=-1){
				IS_PROVICE = true;
				CITY_NAME = loginUser.get("BELONG_AREA").toString();
			}else{
				CITY_NAME = loginUser.get("BELONG_AREA").toString();
			}
			if(CITY_NAME.length()>2){
				CITY_NAME = CITY_NAME.substring(0,2);
			}
		}
		
		/*检索搜索参数*/
		String searchCity="";
		String searchDate="";
		if(conditonMap.containsKey("CITY") && !"".equals(conditonMap.get("CITY").toString()) 
				&& !"--".equals(conditonMap.get("CITY").toString()) 
				&& !"--请选择--".equals(conditonMap.get("CITY").toString())
				&& !"四川".equals(conditonMap.get("CITY").toString())
				&& !"全省".equals(conditonMap.get("CITY").toString())) {
			searchCity=conditonMap.get("CITY").toString();
		}
		if(conditonMap.containsKey("DATE") && !"".equals(conditonMap.get("DATE").toString())) {
			searchDate=conditonMap.get("DATE").toString();
		}
		
		String sql = "SELECT ROWNUM ID,RISK_TYPE ,CITY,MOUTH,TOTALNUM,FEEDBACKNUM,FEEDBACKPRO FROM " + 
				"(SELECT ROWNUM ID,RTD.RISK_TYPE ,B.CITY,B.MOUTH,B.TOTALNUM,B.FEEDBACKNUM , " + 
				"(ROUND(B.FEEDBACKNUM/B.TOTALNUM*100,2)||'%') FEEDBACKPRO  FROM  " + 
				"( SELECT  A.RISK_TYPE,A.CITY,A.MOUTH,   COUNT(A.ID) AS TOTALNUM , SUM(CASE WHEN  A.REASON IS NULL THEN 0 ELSE 1 END)  FEEDBACKNUM   " + 
				"FROM ( SELECT ID,RISK_TYPE,CITY,to_char(MOUTH,'yyyy-MM') MOUTH,REASON FROM  TOWERCRNOP.ORC_CGI_DETAIL   " + 
				"UNION ALL SELECT  ID,RISK_TYPE,CITY,to_char(MOUTH,'yyyy-MM')  MOUTH,REASON FROM TOWERCRNOP.ORC_CER_DETAIL    " + 
				"UNION ALL SELECT  ID,RISK_TYPE,CITY,to_char(MOUTH,'yyyy-MM')  MOUTH,REASON FROM TOWERCRNOP.ORC_OSP_DETAIL   " + 
				"UNION ALL SELECT  ID,RISK_TYPE,CITY,to_char(MOUTH,'yyyy-MM')  MOUTH,REASON FROM TOWERCRNOP.ORC_NVOTM_DETAIL     " + 
				"UNION ALL SELECT  ID,RISK_TYPE,CITY,to_char(MOUTH,'yyyy-MM')  MOUTH,REASON FROM TOWERCRNOP.ORC_RPA_DETAIL      " + 
				"UNION ALL SELECT  ID,RISK_TYPE,CITY,to_char(MOUTH,'yyyy-MM')  MOUTH,REASON FROM TOWERCRNOP.ORC_CTP_DETAIL     " + 
				"UNION ALL SELECT  ID,RISK_TYPE,CITY,to_char(MOUTH,'yyyy-MM')  MOUTH,REASON FROM TOWERCRNOP.ORC_PCOND_DETAIL      " + 
				"UNION ALL SELECT  ID,RISK_TYPE,CITY,to_char(MOUTH,'yyyy-MM')  MOUTH,REASON FROM TOWERCRNOP.ORC_OM_DETAIL     " + 
				"UNION ALL SELECT  ID,RISK_TYPE,CITY,to_char(MOUTH,'yyyy-MM')  MOUTH,REASON FROM TOWERCRNOP.ORC_CACDD_DETAIL  " + 
				"UNION ALL SELECT  ID,RISK_TYPE,CITY,to_char(MOUTH,'yyyy-MM')  MOUTH,REASON FROM TOWERCRNOP.ORC_CTNH_DETAIL   " + 
				"UNION ALL SELECT  ID,RISK_TYPE,CITY,to_char(MOUTH,'yyyy-MM')  MOUTH,REASON FROM TOWERCRNOP.ORC_POOI_DETAIL   " + 
				"UNION ALL SELECT  ID,RISK_TYPE,CITY,to_char(MOUTH,'yyyy-MM')  MOUTH,REASON FROM TOWERCRNOP.ORC_NSCEA_DETAIL   " + 
				"UNION ALL SELECT  ID,RISK_TYPE,CITY,to_char(MOUTH,'yyyy-MM')  MOUTH,REASON FROM TOWERCRNOP.ORC_TRMCD_DETAIL  " + 
				"UNION ALL SELECT  ID,RISK_TYPE,CITY,to_char(MOUTH,'yyyy-MM')  MOUTH,REASON FROM TOWERCRNOP.ORC_RAOBTI_DETAIL   " + 
				"UNION ALL SELECT  ID,RISK_TYPE,CITY,to_char(MOUTH,'yyyy-MM')  MOUTH,REASON FROM TOWERCRNOP.ORC_TFHE_DETAIL    " + 
				"UNION ALL SELECT  ID,RISK_TYPE,CITY,to_char(MOUTH,'yyyy-MM')  MOUTH,REASON FROM TOWERCRNOP.ORC_REMA_DETAIL   " + 
				"UNION ALL SELECT  ID,RISK_TYPE,CITY,to_char(MOUTH,'yyyy-MM')  MOUTH,REASON FROM TOWERCRNOP.ORC_ZCCA_DETAIL    " + 
				"UNION ALL SELECT  ID,RISK_TYPE,CITY,to_char(MOUTH,'yyyy-MM')  MOUTH,REASON FROM TOWERCRNOP.ORC_RCA_DETAIL   )  A    " + 
				"GROUP BY A.RISK_TYPE,A.CITY,A.MOUTH)  B   LEFT JOIN TOWERCRNOP.ORC_RISK_TYPE_DETAIL RTD  ON B.RISK_TYPE= RTD.ID) D  " + 
				"WHERE  1=1 ";
		

				if(!IS_PROVICE) {
					sql+=" and D.CITY='"+CITY_NAME+"'";
					if(!searchDate.equals("")) {
						sql+=" and D.MOUTH='"+searchDate+"'";
					}
				}else {
					if(!searchCity.equals("") && !searchDate.equals("")) {
						sql+=" and D.CITY='"+searchCity+"' and D.MOUTH='"+searchDate+"'";
					}else if(searchCity.equals("") && !searchDate.equals("")) {
						sql+=" and D.MOUTH='"+searchDate+"'";
					}else if(!searchCity.equals("") && searchDate.equals("")) {
						sql+=" and D.CITY='"+searchCity+"'";
					}
				}
				
		Integer count = jdbcTemplate.queryForInt("SELECT COUNT(1) FROM("+sql+")");
		Integer lastIndex = displayStart+iDisplayLength;
		String pageSql = "";
		pageSql+="SELECT X.* FROM(";
		pageSql+="	SELECT V.*,ROWNUM AS RN FROM("+sql+") V WHERE ROWNUM <= "+lastIndex;
		pageSql+=") X WHERE X.RN > "+displayStart;
		List<Map<String,Object>> objectList = jdbcTemplate.queryForList(pageSql);
		DataTableResult<Map<String,Object>> tableData = new DataTableResult<Map<String,Object>>();
		tableData.setsEcho(sEcho);
		tableData.setAaData(objectList);
		tableData.setiTotalRecords(count);
		tableData.setiTotalDisplayRecords(count);
		response.setContentType("application/json; charset=UTF-8");
		response.getWriter().print(JSONObject.fromObject(tableData).toString());
	}
	/*
	 * 显示风险点反馈率数值
	 * 
	 * */
	@RequestMapping("/findriskFeedbackProbabilitySumData.ilf")
	public void findriskFeedbackProbabilitySumData(@RequestParam String city,@RequestParam String date,HttpServletRequest request,HttpServletResponse response)throws Exception{
		JSONObject jsonObject=JSONObject.fromObject("{success:true}");
		String sql="";
		sql = "SELECT  SUM(F.TOTALNUM)  AS  SUMTOTALNUM , SUM(F.FEEDBACKNUM) AS  SUMFEEDBACKNUM , (ROUND(SUM(F.FEEDBACKNUM)/SUM(F.TOTALNUM)*100,2)||'%')  AS SUMFEEDBACKPRO  FROM " + 
				"(SELECT E.TOTALNUM,E.FEEDBACKNUM,E.FEEDBACKPRO FROM " + 
				"(SELECT ID,RISK_TYPE ,CITY,MOUTH,TOTALNUM,FEEDBACKNUM,FEEDBACKPRO FROM  " + 
				"(SELECT ROWNUM ID,RTD.RISK_TYPE ,B.CITY,B.MOUTH,B.TOTALNUM,B.FEEDBACKNUM , " + 
				"(ROUND(B.FEEDBACKNUM/B.TOTALNUM*100,2)||'%') FEEDBACKPRO  FROM  " + 
				"( SELECT  A.RISK_TYPE,A.CITY,A.MOUTH,   COUNT(A.ID) AS TOTALNUM , SUM(CASE WHEN  A.REASON IS NULL THEN 0 ELSE 1 END)  FEEDBACKNUM   " + 
				"FROM ( SELECT ID,RISK_TYPE,CITY,to_char(MOUTH,'yyyy-MM') MOUTH,REASON FROM  TOWERCRNOP.ORC_CGI_DETAIL   " + 
				"UNION ALL SELECT  ID,RISK_TYPE,CITY,to_char(MOUTH,'yyyy-MM')  MOUTH,REASON FROM TOWERCRNOP.ORC_CER_DETAIL    " + 
				"UNION ALL SELECT  ID,RISK_TYPE,CITY,to_char(MOUTH,'yyyy-MM')  MOUTH,REASON FROM TOWERCRNOP.ORC_OSP_DETAIL   " + 
				"UNION ALL SELECT  ID,RISK_TYPE,CITY,to_char(MOUTH,'yyyy-MM')  MOUTH,REASON FROM TOWERCRNOP.ORC_NVOTM_DETAIL     " + 
				"UNION ALL SELECT  ID,RISK_TYPE,CITY,to_char(MOUTH,'yyyy-MM')  MOUTH,REASON FROM TOWERCRNOP.ORC_RPA_DETAIL      " + 
				"UNION ALL SELECT  ID,RISK_TYPE,CITY,to_char(MOUTH,'yyyy-MM')  MOUTH,REASON FROM TOWERCRNOP.ORC_CTP_DETAIL     " + 
				"UNION ALL SELECT  ID,RISK_TYPE,CITY,to_char(MOUTH,'yyyy-MM')  MOUTH,REASON FROM TOWERCRNOP.ORC_PCOND_DETAIL      " + 
				"UNION ALL SELECT  ID,RISK_TYPE,CITY,to_char(MOUTH,'yyyy-MM')  MOUTH,REASON FROM TOWERCRNOP.ORC_OM_DETAIL     " + 
				"UNION ALL SELECT  ID,RISK_TYPE,CITY,to_char(MOUTH,'yyyy-MM')  MOUTH,REASON FROM TOWERCRNOP.ORC_CACDD_DETAIL  " + 
				"UNION ALL SELECT  ID,RISK_TYPE,CITY,to_char(MOUTH,'yyyy-MM')  MOUTH,REASON FROM TOWERCRNOP.ORC_CTNH_DETAIL   " + 
				"UNION ALL SELECT  ID,RISK_TYPE,CITY,to_char(MOUTH,'yyyy-MM')  MOUTH,REASON FROM TOWERCRNOP.ORC_POOI_DETAIL   " + 
				"UNION ALL SELECT  ID,RISK_TYPE,CITY,to_char(MOUTH,'yyyy-MM')  MOUTH,REASON FROM TOWERCRNOP.ORC_NSCEA_DETAIL   " + 
				"UNION ALL SELECT  ID,RISK_TYPE,CITY,to_char(MOUTH,'yyyy-MM')  MOUTH,REASON FROM TOWERCRNOP.ORC_TRMCD_DETAIL  " + 
				"UNION ALL SELECT  ID,RISK_TYPE,CITY,to_char(MOUTH,'yyyy-MM')  MOUTH,REASON FROM TOWERCRNOP.ORC_RAOBTI_DETAIL   " + 
				"UNION ALL SELECT  ID,RISK_TYPE,CITY,to_char(MOUTH,'yyyy-MM')  MOUTH,REASON FROM TOWERCRNOP.ORC_TFHE_DETAIL    " + 
				"UNION ALL SELECT  ID,RISK_TYPE,CITY,to_char(MOUTH,'yyyy-MM')  MOUTH,REASON FROM TOWERCRNOP.ORC_REMA_DETAIL   " + 
				"UNION ALL SELECT  ID,RISK_TYPE,CITY,to_char(MOUTH,'yyyy-MM')  MOUTH,REASON FROM TOWERCRNOP.ORC_ZCCA_DETAIL    " + 
				"UNION ALL SELECT  ID,RISK_TYPE,CITY,to_char(MOUTH,'yyyy-MM')  MOUTH,REASON FROM TOWERCRNOP.ORC_RCA_DETAIL   )  A    " + 
				"GROUP BY A.RISK_TYPE,A.CITY,A.MOUTH)  B   LEFT JOIN TOWERCRNOP.ORC_RISK_TYPE_DETAIL RTD  ON B.RISK_TYPE= RTD.ID) D  " + 
				"WHERE   1=1   ";
				if (!StringUtils.isEmpty(city) && !city.equals("全省")&& !city.equals("--请选择--")) {
					sql += " and D.CITY='" + city + "'";
				}

				if (!StringUtils.isEmpty(date)) {
					sql += " and D.MOUTH='" + date + "'";
				}
				sql+= ")E ) F ";
				List<Map<String, Object>> list=new ArrayList<Map<String, Object>>();
				list=jdbcTemplate.queryForList(sql);
				jsonObject.put("list",list);
				response.setContentType("application/json; charset=UTF-8");
				response.getWriter().print(jsonObject.toString());
	}

	/*
	 * 	查询地市风险点反馈率
	 * 
	 * */
	@RequestMapping("/findriskFeedbackProbabilityNumData.ilf")
	public void findriskFeedbackProbabilityNumData(HttpServletRequest request,HttpServletResponse response,String date,String city)throws Exception{
		JSONObject jsonObject=JSONObject.fromObject("{success:true}");
		String sql="";
		sql = "SELECT  SUM(F.TOTALNUM)  AS  SUMTOTALNUM , SUM(F.FEEDBACKNUM) AS  SUMFEEDBACKNUM , (ROUND(SUM(F.FEEDBACKNUM)/SUM(F.TOTALNUM)*100,2)||'%')  AS SUMFEEDBACKPRO  FROM " + 
				"(SELECT E.TOTALNUM,E.FEEDBACKNUM,E.FEEDBACKPRO FROM " + 
				"(SELECT ID,RISK_TYPE ,CITY,MOUTH,TOTALNUM,FEEDBACKNUM,FEEDBACKPRO FROM  " + 
				"(SELECT ROWNUM ID,RTD.RISK_TYPE ,B.CITY,B.MOUTH,B.TOTALNUM,B.FEEDBACKNUM , " + 
				"(ROUND(B.FEEDBACKNUM/B.TOTALNUM*100,2)||'%') FEEDBACKPRO  FROM  " + 
				"( SELECT  A.RISK_TYPE,A.CITY,A.MOUTH,   COUNT(A.ID) AS TOTALNUM , SUM(CASE WHEN  A.REASON IS NULL THEN 0 ELSE 1 END)  FEEDBACKNUM   " + 
				"FROM ( SELECT ID,RISK_TYPE,CITY,to_char(MOUTH,'yyyy-MM') MOUTH,REASON FROM  TOWERCRNOP.ORC_CGI_DETAIL   " + 
				"UNION ALL SELECT  ID,RISK_TYPE,CITY,to_char(MOUTH,'yyyy-MM')  MOUTH,REASON FROM TOWERCRNOP.ORC_CER_DETAIL    " + 
				"UNION ALL SELECT  ID,RISK_TYPE,CITY,to_char(MOUTH,'yyyy-MM')  MOUTH,REASON FROM TOWERCRNOP.ORC_OSP_DETAIL   " + 
				"UNION ALL SELECT  ID,RISK_TYPE,CITY,to_char(MOUTH,'yyyy-MM')  MOUTH,REASON FROM TOWERCRNOP.ORC_NVOTM_DETAIL     " + 
				"UNION ALL SELECT  ID,RISK_TYPE,CITY,to_char(MOUTH,'yyyy-MM')  MOUTH,REASON FROM TOWERCRNOP.ORC_RPA_DETAIL      " + 
				"UNION ALL SELECT  ID,RISK_TYPE,CITY,to_char(MOUTH,'yyyy-MM')  MOUTH,REASON FROM TOWERCRNOP.ORC_CTP_DETAIL     " + 
				"UNION ALL SELECT  ID,RISK_TYPE,CITY,to_char(MOUTH,'yyyy-MM')  MOUTH,REASON FROM TOWERCRNOP.ORC_PCOND_DETAIL      " + 
				"UNION ALL SELECT  ID,RISK_TYPE,CITY,to_char(MOUTH,'yyyy-MM')  MOUTH,REASON FROM TOWERCRNOP.ORC_OM_DETAIL     " + 
				"UNION ALL SELECT  ID,RISK_TYPE,CITY,to_char(MOUTH,'yyyy-MM')  MOUTH,REASON FROM TOWERCRNOP.ORC_CACDD_DETAIL  " + 
				"UNION ALL SELECT  ID,RISK_TYPE,CITY,to_char(MOUTH,'yyyy-MM')  MOUTH,REASON FROM TOWERCRNOP.ORC_CTNH_DETAIL   " + 
				"UNION ALL SELECT  ID,RISK_TYPE,CITY,to_char(MOUTH,'yyyy-MM')  MOUTH,REASON FROM TOWERCRNOP.ORC_POOI_DETAIL   " + 
				"UNION ALL SELECT  ID,RISK_TYPE,CITY,to_char(MOUTH,'yyyy-MM')  MOUTH,REASON FROM TOWERCRNOP.ORC_NSCEA_DETAIL   " + 
				"UNION ALL SELECT  ID,RISK_TYPE,CITY,to_char(MOUTH,'yyyy-MM')  MOUTH,REASON FROM TOWERCRNOP.ORC_TRMCD_DETAIL  " + 
				"UNION ALL SELECT  ID,RISK_TYPE,CITY,to_char(MOUTH,'yyyy-MM')  MOUTH,REASON FROM TOWERCRNOP.ORC_RAOBTI_DETAIL   " + 
				"UNION ALL SELECT  ID,RISK_TYPE,CITY,to_char(MOUTH,'yyyy-MM')  MOUTH,REASON FROM TOWERCRNOP.ORC_TFHE_DETAIL    " + 
				"UNION ALL SELECT  ID,RISK_TYPE,CITY,to_char(MOUTH,'yyyy-MM')  MOUTH,REASON FROM TOWERCRNOP.ORC_REMA_DETAIL   " + 
				"UNION ALL SELECT  ID,RISK_TYPE,CITY,to_char(MOUTH,'yyyy-MM')  MOUTH,REASON FROM TOWERCRNOP.ORC_ZCCA_DETAIL    " + 
				"UNION ALL SELECT  ID,RISK_TYPE,CITY,to_char(MOUTH,'yyyy-MM')  MOUTH,REASON FROM TOWERCRNOP.ORC_RCA_DETAIL   )  A    " + 
				"GROUP BY A.RISK_TYPE,A.CITY,A.MOUTH)  B   LEFT JOIN TOWERCRNOP.ORC_RISK_TYPE_DETAIL RTD  ON B.RISK_TYPE= RTD.ID) D  " + 
				"WHERE  1=1 ";
				
				if(!city.equals("") && !city.equals("全省") && !city.equals("--") && !city.equals("--请选择--") && !city.equals("四川") && !date.equals("")) {
					sql+=" and D.CITY='"+city+"' and D.MOUTH='"+date+"'";
				}else if((city.equals("") ||city.equals("") || city.equals("全省") || city.equals("--") || city.equals("--请选择--") || city.equals("四川") )&& !date.equals("")) {
					sql+=" and D.MOUTH='"+date+"'";
				}else if(!city.equals("") && !city.equals("全省") && !city.equals("--") && !city.equals("--请选择--") && !city.equals("四川") && date.equals("")) {
					sql+=" and D.CITY='"+city+"'";
				}
				sql+="  )E ) F ";
				List<Map<String, Object>> list=new ArrayList<Map<String, Object>>();
				list=jdbcTemplate.queryForList(sql);
				jsonObject.put("list",list);
				response.setContentType("application/json; charset=UTF-8");
				response.getWriter().print(jsonObject.toString());
	}
}
