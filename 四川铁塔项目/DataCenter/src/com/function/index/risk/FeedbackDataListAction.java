package com.function.index.risk;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.systemConfig.model.DataTableResult;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

@Controller("com.function.index.risk.FeedbackDataListAction")
@RequestMapping(value="/FeedbackDataListAction")
public class FeedbackDataListAction {
	@Autowired
	private JdbcTemplate jdbcTemplate;
	private static String resUserName = "TOWERCRNOP";
	
	
	
	@SuppressWarnings("unchecked")
	@RequestMapping("/findFeedbackDataListFirst.ilf")
	public void findFeedbackDataListFirst(HttpServletRequest request,HttpServletResponse response,String qu_id) throws Exception{
		JSONObject jsonObject=JSONObject.fromObject("{success:true}");
		String tablename=RiskControlTable.getValueByKey(qu_id);
		String sql="";
		sql="select RISK_NAME from "+resUserName+".ORC_RISK_NAME_DETAIL where ID='"+qu_id+"'";
		String risk_name=(String)jdbcTemplate.queryForList(sql).get(0).get("RISK_NAME");
		
		String sqlbutton="";
		sqlbutton=" select  b.RISK_TYPE  from    "+resUserName+"."+RiskControlTable.getValueByKey(qu_id)+"    A,  "+resUserName+". ORC_RISK_TYPE_DETAIL B "
				               + " where A.RISK_TYPE=B.ID  AND ROWNUM=1";
		String risk_type=(String)jdbcTemplate.queryForList(sqlbutton).get(0).get("RISK_TYPE");
		jsonObject.put("RISK_NAME", risk_name);
		jsonObject.put("RISK_TYPE", risk_type);
		
		
		response.setContentType("application/json; charset=UTF-8");
		response.getWriter().print(jsonObject.toString());
	}
	
	
	
	@SuppressWarnings("unchecked")
	@RequestMapping("/findFeedbackDataListTable.ilf")
	public void findFeedbackDataListTable(@RequestParam String qu_id,@RequestParam String tableparam,@RequestParam String conditions,HttpServletRequest request,HttpServletResponse response) throws Exception{
		Long sEcho = 0L;
		Integer displayStart = 0;
		Integer iDisplayLength = 0;
		JSONArray jsons = JSONArray.fromObject(tableparam);
		HashMap<String,Object> conditionMap = new HashMap<String,Object>();
		if(jsons!=null && jsons.size()!=0){
			for(int i=0;i<jsons.size();i++){
				JSONObject json = JSONObject.fromObject(jsons.get(i));
				String key = json.getString("name");
				if(key.equals("sEcho")){
					sEcho = Long.parseLong(json.getString("value"));
				}else if(key.equals("iDisplayStart")){
					displayStart = Integer.parseInt(json.getString("value"));
					conditionMap.put("iDisplayStart",displayStart);
				}else if(key.equals("iDisplayLength")){
					iDisplayLength = Integer.parseInt(json.getString("value"));
					conditionMap.put("iDisplayLength",iDisplayLength);
				}
			}
		}
		JSONArray condition = JSONArray.fromObject(conditions);
		if(conditions!=null && condition.size()!=0){
			for(int i=0;i<condition.size();i++){
				JSONObject jsonObject = JSONObject.fromObject(condition.get(i));
				if(jsonObject.get("value")!=null && !"".equals(jsonObject.getString("value"))){
					conditionMap.put(jsonObject.getString("name"),jsonObject.getString("value"));
				}
			}
		}
		/*??????????????????*/
		String searchCity="";
		String searchDate="";
		if(conditionMap.containsKey("CITY") && !"".equals(conditionMap.get("CITY").toString()) 
				&& !"--".equals(conditionMap.get("CITY").toString()) 
				&& !"--?????????--".equals(conditionMap.get("CITY").toString())
				&& !"??????".equals(conditionMap.get("CITY").toString())
				&& !"??????".equals(conditionMap.get("CITY").toString())) {
			searchCity=conditionMap.get("CITY").toString();
		}
		if(conditionMap.containsKey("DATE") && !"".equals(conditionMap.get("DATE").toString())) {
			searchDate=conditionMap.get("DATE").toString();
		}
		/*?????????????????????????????????????????????????????????*/
		Boolean isProvince=false;
		String belongArea="";
		isProvince=(Boolean)request.getSession().getAttribute("IS_PROVINCE");
		belongArea=(String)request.getSession().getAttribute("BELONG_AREA");
		String sql="";
		sql="select ROWNUM as NUM,E.* from";
		sql+="(select A.ID,A.CITY,A.MOUTH,A.REASON,A.FEE_PEOPLE,A.FEE_TIME,B.RISK_TYPE,C.RISK_NAME,D.QU_TYPE from";
		sql+="(select ID,CITY,to_char(MOUTH,'yyyy-MM') as MOUTH,RISK_TYPE,RISK_NAME,QU_TYPE,REASON,FEE_PEOPLE ,to_char(FEE_TIME,'yyyy-MM-DD') as FEE_TIME  from "+resUserName+"."+RiskControlTable.getValueByKey(qu_id)+") A";
		sql+=","+resUserName+".ORC_RISK_TYPE_DETAIL B";
		sql+=","+resUserName+".ORC_RISK_NAME_DETAIL C";
		sql+=","+resUserName+".ORC_QU_TYPE_DETAIL D ";
		sql+="where A.RISK_TYPE=B.ID and A.RISK_NAME=C.ID and A.QU_TYPE=D.ID  AND  (trim(A.REASON) is  not NULL OR trim(A.FEE_PEOPLE) is  not NULL OR TRIM(A.FEE_TIME) IS NOT NULL    )   ";
		if(!isProvince) {
			sql+=" and A.CITY='"+belongArea+"'";
			if(!searchDate.equals("")) {
				sql+=" and A.MOUTH='"+searchDate+"'";
			}
		}else {
			if(!searchCity.equals("") && !searchDate.equals("")) {
				sql+=" and A.CITY='"+searchCity+"' and A.MOUTH='"+searchDate+"'";
			}else if(searchCity.equals("") && !searchDate.equals("")) {
				sql+=" and A.MOUTH='"+searchDate+"'";
			}else if(!searchCity.equals("") && searchDate.equals("")) {
				sql+=" and A.CITY='"+searchCity+"'";
			}
		}
		sql+=")E";
		Integer count = jdbcTemplate.queryForInt("SELECT COUNT(1) FROM("+sql+")");
		Integer lastIndex = displayStart+iDisplayLength;
		String pageSql = "";
		pageSql+="SELECT G.* FROM(";
		pageSql+="	SELECT F.*,ROWNUM AS RN FROM("+sql+") F WHERE ROWNUM <= "+lastIndex;
		pageSql+=") G WHERE G.RN > "+displayStart;
		List<Map<String,Object>> objectList = jdbcTemplate.queryForList(pageSql);
		DataTableResult<Map<String,Object>> tableData = new DataTableResult<Map<String,Object>>();
		tableData.setsEcho(sEcho);
		tableData.setAaData(objectList);
		tableData.setiTotalRecords(count);
		tableData.setiTotalDisplayRecords(count);
		response.setContentType("application/json; charset=UTF-8");
		response.getWriter().print(JSONObject.fromObject(tableData).toString());

		
	}
}
