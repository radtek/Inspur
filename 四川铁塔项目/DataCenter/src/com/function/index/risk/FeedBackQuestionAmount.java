package com.function.index.risk;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.systemConfig.model.DataTableResult;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

@Controller("com.function.index.risk.FeedBackQuestionAmount")
@RequestMapping(value="/feedBackQuestionAmountAction")
public class FeedBackQuestionAmount {
	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	private static String resUserName = "TOWERCRNOP";
	
	/**
	 * 检查登录身份，返回其身份
	 * 返回已反馈问题总数
	 * @throws Exception 
	 * */
	@RequestMapping(value="/findFeedBackQuestionFirst.ilf")
	public void findFeedBackQuestionFirst(@RequestParam String city, @RequestParam String date,HttpServletRequest request,HttpServletResponse response) throws Exception {
		Boolean isProvince=false;
		String belongArea="";
		JSONObject jsonObject=JSONObject.fromObject("{success:true}");
		try {
			isProvince=(Boolean)request.getSession().getAttribute("IS_PROVINCE");
			belongArea=(String)request.getSession().getAttribute("BELONG_AREA");
			if(isProvince) {
				jsonObject.put("IS_PROVINCE",isProvince);
			}else {
				jsonObject.put("IS_PROVINCE",isProvince);
				jsonObject.put("BELONG_AREA",belongArea);
			}
			/*获取风险已反馈页面的已反馈数量*/
			String sql="";
			sql+="select count(*) from ";
			sql+="(select MOUTH,CITY,RISK_TYPE,REASON,FEE_PEOPLE,FEE_TIME from "+resUserName+".ORC_CGI_DETAIL";
			sql+=" union all ";
			sql+="select MOUTH,CITY,RISK_TYPE,REASON,FEE_PEOPLE,FEE_TIME from "+resUserName+".ORC_CER_DETAIL";
			sql+=" union all ";
			sql+="select MOUTH,CITY,RISK_TYPE,REASON,FEE_PEOPLE,FEE_TIME from "+resUserName+".ORC_OSP_DETAIL";
			sql+=" union all ";
			sql+="select MOUTH,CITY,RISK_TYPE,REASON,FEE_PEOPLE,FEE_TIME from "+resUserName+".ORC_NVOTM_DETAIL";
			sql+=" union all ";
			sql+="select MOUTH,CITY,RISK_TYPE,REASON,FEE_PEOPLE,FEE_TIME from "+resUserName+".ORC_RPA_DETAIL";
			sql+=" union all ";
			sql+="select MOUTH,CITY,RISK_TYPE,REASON,FEE_PEOPLE,FEE_TIME from "+resUserName+".ORC_CTP_DETAIL";
			sql+=" union all ";
			sql+="select MOUTH,CITY,RISK_TYPE,REASON,FEE_PEOPLE,FEE_TIME from "+resUserName+".ORC_PCOND_DETAIL";
			sql+=" union all ";
			sql+="select MOUTH,CITY,RISK_TYPE,REASON,FEE_PEOPLE,FEE_TIME from "+resUserName+".ORC_OM_DETAIL";
			sql+=" union all ";
			sql+="select MOUTH,CITY,RISK_TYPE,REASON,FEE_PEOPLE,FEE_TIME from "+resUserName+".ORC_CACDD_DETAIL";
			sql+=" union all ";
			sql+="select MOUTH,CITY,RISK_TYPE,REASON,FEE_PEOPLE,FEE_TIME from "+resUserName+".ORC_CTNH_DETAIL";
			sql+=" union all ";
			sql+="select MOUTH,CITY,RISK_TYPE,REASON,FEE_PEOPLE,FEE_TIME from "+resUserName+".ORC_POOI_DETAIL";
			sql+=" union all ";
			sql+="select MOUTH,CITY,RISK_TYPE,REASON,FEE_PEOPLE,FEE_TIME from "+resUserName+".ORC_NSCEA_DETAIL";
			sql+=" union all ";
			sql+="select MOUTH,CITY,RISK_TYPE,REASON,FEE_PEOPLE,FEE_TIME from "+resUserName+".ORC_TRMCD_DETAIL";
			sql+=" union all ";
			sql+="select MOUTH,CITY,RISK_TYPE,REASON,FEE_PEOPLE,FEE_TIME from "+resUserName+".ORC_RAOBTI_DETAIL";
			sql+=" union all ";
			sql+="select MOUTH,CITY,RISK_TYPE,REASON,FEE_PEOPLE,FEE_TIME from "+resUserName+".ORC_TFHE_DETAIL";
			sql+=" union all ";
			sql+="select MOUTH,CITY,RISK_TYPE,REASON,FEE_PEOPLE,FEE_TIME from "+resUserName+".ORC_REMA_DETAIL";
			sql+=" union all ";
			sql+="select MOUTH,CITY,RISK_TYPE,REASON,FEE_PEOPLE,FEE_TIME from "+resUserName+".ORC_ZCCA_DETAIL";
			sql+=" union all ";
			sql+="select MOUTH,CITY,RISK_TYPE,REASON,FEE_PEOPLE,FEE_TIME from "+resUserName+".ORC_RCA_DETAIL";
			sql+=") A";
			sql+=" where trim(A.REASON) is not NULL and trim(A.FEE_PEOPLE) is not NULL and trim(A.FEE_TIME) is not NULL";
			if (!StringUtils.isEmpty(city) && !city.equals("全省")&& !city.equals("--请选择--")) {
				sql += " and A.CITY='" + city + "'";
			}

			if (!StringUtils.isEmpty(date)) {
				sql += " and to_char(A.MOUTH,'yyyy-MM')='" + date + "'";
			}
			jsonObject.put("QUESTION_AMOUNT",jdbcTemplate.queryForInt(sql));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			jsonObject=JSONObject.fromObject("{success:false}");
		}finally {
			response.setContentType("application/json; charset=UTF-8");
			response.getWriter().print(jsonObject.toString());
		}
	}
	
	/**
	 * 获取表格数据
	 */
	@RequestMapping(value="/findTableData.ilf")
	public void findTableData(@RequestParam String tableparam,@RequestParam String conditions,HttpServletRequest request,HttpServletResponse response) throws Exception {
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
		/*检索搜索参数*/
		String searchCity="";
		String searchDate="";
		if(conditionMap.containsKey("CITY") && !"".equals(conditionMap.get("CITY").toString()) 
				&& !"--".equals(conditionMap.get("CITY").toString()) 
				&& !"--请选择--".equals(conditionMap.get("CITY").toString())
				&& !"四川".equals(conditionMap.get("CITY").toString())
				&& !"全省".equals(conditionMap.get("CITY").toString())) {
			searchCity=conditionMap.get("CITY").toString();
		}
		if(conditionMap.containsKey("DATE") && !"".equals(conditionMap.get("DATE").toString())) {
			searchDate=conditionMap.get("DATE").toString();
		}
		/*获取已反馈问题的集合*/
		Boolean isProvince=false;
		String belongArea="";
			isProvince=(Boolean)request.getSession().getAttribute("IS_PROVINCE");
			belongArea=(String)request.getSession().getAttribute("BELONG_AREA");
			
			
			String sql="select ROWNUM as ID,C.* from";
			sql+="(";
			sql+="select B.RISK_TYPE,A.CITY,to_char(A.MOUTH,'yyyy-MM') as MOUTH,count(*) as AMOUNT from";
			sql+="(select MOUTH,CITY,RISK_TYPE,REASON,FEE_PEOPLE,FEE_TIME from "+resUserName+".ORC_CGI_DETAIL";
			sql+=" union all ";
			sql+="select MOUTH,CITY,RISK_TYPE,REASON,FEE_PEOPLE,FEE_TIME from "+resUserName+".ORC_CER_DETAIL";
			sql+=" union all ";
			sql+="select MOUTH,CITY,RISK_TYPE,REASON,FEE_PEOPLE,FEE_TIME from "+resUserName+".ORC_OSP_DETAIL";
			sql+=" union all ";
			sql+="select MOUTH,CITY,RISK_TYPE,REASON,FEE_PEOPLE,FEE_TIME from "+resUserName+".ORC_NVOTM_DETAIL";
			sql+=" union all ";
			sql+="select MOUTH,CITY,RISK_TYPE,REASON,FEE_PEOPLE,FEE_TIME from "+resUserName+".ORC_RPA_DETAIL";
			sql+=" union all ";
			sql+="select MOUTH,CITY,RISK_TYPE,REASON,FEE_PEOPLE,FEE_TIME from "+resUserName+".ORC_CTP_DETAIL";
			sql+=" union all ";
			sql+="select MOUTH,CITY,RISK_TYPE,REASON,FEE_PEOPLE,FEE_TIME from "+resUserName+".ORC_PCOND_DETAIL";
			sql+=" union all ";
			sql+="select MOUTH,CITY,RISK_TYPE,REASON,FEE_PEOPLE,FEE_TIME from "+resUserName+".ORC_OM_DETAIL";
			sql+=" union all ";
			sql+="select MOUTH,CITY,RISK_TYPE,REASON,FEE_PEOPLE,FEE_TIME from "+resUserName+".ORC_CACDD_DETAIL";
			sql+=" union all ";
			sql+="select MOUTH,CITY,RISK_TYPE,REASON,FEE_PEOPLE,FEE_TIME from "+resUserName+".ORC_CTNH_DETAIL";
			sql+=" union all ";
			sql+="select MOUTH,CITY,RISK_TYPE,REASON,FEE_PEOPLE,FEE_TIME from "+resUserName+".ORC_POOI_DETAIL";
			sql+=" union all ";
			sql+="select MOUTH,CITY,RISK_TYPE,REASON,FEE_PEOPLE,FEE_TIME from "+resUserName+".ORC_NSCEA_DETAIL";
			sql+=" union all ";
			sql+="select MOUTH,CITY,RISK_TYPE,REASON,FEE_PEOPLE,FEE_TIME from "+resUserName+".ORC_TRMCD_DETAIL";
			sql+=" union all ";
			sql+="select MOUTH,CITY,RISK_TYPE,REASON,FEE_PEOPLE,FEE_TIME from "+resUserName+".ORC_RAOBTI_DETAIL";
			sql+=" union all ";
			sql+="select MOUTH,CITY,RISK_TYPE,REASON,FEE_PEOPLE,FEE_TIME from "+resUserName+".ORC_TFHE_DETAIL";
			sql+=" union all ";
			sql+="select MOUTH,CITY,RISK_TYPE,REASON,FEE_PEOPLE,FEE_TIME from "+resUserName+".ORC_REMA_DETAIL";
			sql+=" union all ";
			sql+="select MOUTH,CITY,RISK_TYPE,REASON,FEE_PEOPLE,FEE_TIME from "+resUserName+".ORC_ZCCA_DETAIL";
			sql+=" union all ";
			sql+="select MOUTH,CITY,RISK_TYPE,REASON,FEE_PEOPLE,FEE_TIME from "+resUserName+".ORC_RCA_DETAIL";
			sql+=") A,"+resUserName+".ORC_RISK_TYPE_DETAIL B where A.RISK_TYPE=B.ID and trim(A.REASON) is not NULL and trim(A.FEE_PEOPLE) is not NULL and trim(A.FEE_TIME) is not NULL";
			sql+=" group by B.RISK_TYPE,A.CITY,to_char(A.MOUTH,'yyyy-MM')";
			sql+=" order by B.RISK_TYPE,A.CITY,to_char(A.MOUTH,'yyyy-MM')";
			sql+=") C";
			if(!isProvince) {
				sql+=" where C.CITY='"+belongArea+"'";
				if(!searchDate.equals("")) {
					sql+=" and C.MOUTH='"+searchDate+"'";
				}
			}else {
				if(!searchCity.equals("") && !searchDate.equals("")) {
					sql+=" where C.CITY='"+searchCity+"' and C.MOUTH='"+searchDate+"'";
				}else if(searchCity.equals("") && !searchDate.equals("")) {
					sql+=" where C.MOUTH='"+searchDate+"'";
				}else if(!searchCity.equals("") && searchDate.equals("")) {
					sql+=" where C.CITY='"+searchCity+"'";
				}
			}
			
			
			Integer count = jdbcTemplate.queryForInt("SELECT COUNT(1) FROM("+sql+")");
			Integer lastIndex = displayStart+iDisplayLength;
			String pageSql = "";
			pageSql+="SELECT B.* FROM(";
			pageSql+="	SELECT A.*,ROWNUM AS RN FROM("+sql+") A WHERE ROWNUM <= "+lastIndex;
			pageSql+=") B WHERE B.RN > "+displayStart;
			List<Map<String,Object>> objectList = jdbcTemplate.queryForList(pageSql);
			DataTableResult<Map<String,Object>> tableData = new DataTableResult<Map<String,Object>>();
			
			tableData.setsEcho(sEcho);
			tableData.setAaData(objectList);
			tableData.setiTotalRecords(count);
			tableData.setiTotalDisplayRecords(count);
			response.setContentType("application/json; charset=UTF-8");
			response.getWriter().print(JSONObject.fromObject(tableData).toString());
	}
	
	/**
	 *选择条件进行查询后，调用此方法改变查询后的总数按钮 
	 * 
	*/
	@RequestMapping("/findAfterSearchNumData.ilf")
	public void findAfterSearchNumData(HttpServletRequest request,HttpServletResponse response,String date,String city)throws Exception{
		JSONObject jsonObject=JSONObject.fromObject("{success:true}");
		String sql="";
		sql+="select count(*) from ";
		sql+="(select to_char(MOUTH,'yyyy-MM') as MOUTH,CITY,RISK_TYPE,REASON,FEE_PEOPLE,FEE_TIME from "+resUserName+".ORC_CGI_DETAIL";
		sql+=" union all ";
		sql+="select to_char(MOUTH,'yyyy-MM') as MOUTH,CITY,RISK_TYPE,REASON,FEE_PEOPLE,FEE_TIME from "+resUserName+".ORC_CER_DETAIL";
		sql+=" union all ";
		sql+="select to_char(MOUTH,'yyyy-MM') as MOUTH,CITY,RISK_TYPE,REASON,FEE_PEOPLE,FEE_TIME from "+resUserName+".ORC_OSP_DETAIL";
		sql+=" union all ";
		sql+="select to_char(MOUTH,'yyyy-MM') as MOUTH,CITY,RISK_TYPE,REASON,FEE_PEOPLE,FEE_TIME from "+resUserName+".ORC_NVOTM_DETAIL";
		sql+=" union all ";
		sql+="select to_char(MOUTH,'yyyy-MM') as MOUTH,CITY,RISK_TYPE,REASON,FEE_PEOPLE,FEE_TIME from "+resUserName+".ORC_RPA_DETAIL";
		sql+=" union all ";
		sql+="select to_char(MOUTH,'yyyy-MM') as MOUTH,CITY,RISK_TYPE,REASON,FEE_PEOPLE,FEE_TIME from "+resUserName+".ORC_CTP_DETAIL";
		sql+=" union all ";
		sql+="select to_char(MOUTH,'yyyy-MM') as MOUTH,CITY,RISK_TYPE,REASON,FEE_PEOPLE,FEE_TIME from "+resUserName+".ORC_PCOND_DETAIL";
		sql+=" union all ";
		sql+="select to_char(MOUTH,'yyyy-MM') as MOUTH,CITY,RISK_TYPE,REASON,FEE_PEOPLE,FEE_TIME from "+resUserName+".ORC_OM_DETAIL";
		sql+=" union all ";
		sql+="select to_char(MOUTH,'yyyy-MM') as MOUTH,CITY,RISK_TYPE,REASON,FEE_PEOPLE,FEE_TIME from "+resUserName+".ORC_CACDD_DETAIL";
		sql+=" union all ";
		sql+="select to_char(MOUTH,'yyyy-MM') as MOUTH,CITY,RISK_TYPE,REASON,FEE_PEOPLE,FEE_TIME from "+resUserName+".ORC_CTNH_DETAIL";
		sql+=" union all ";
		sql+="select to_char(MOUTH,'yyyy-MM') as MOUTH,CITY,RISK_TYPE,REASON,FEE_PEOPLE,FEE_TIME from "+resUserName+".ORC_POOI_DETAIL";
		sql+=" union all ";
		sql+="select to_char(MOUTH,'yyyy-MM') as MOUTH,CITY,RISK_TYPE,REASON,FEE_PEOPLE,FEE_TIME from "+resUserName+".ORC_NSCEA_DETAIL";
		sql+=" union all ";
		sql+="select to_char(MOUTH,'yyyy-MM') as MOUTH,CITY,RISK_TYPE,REASON,FEE_PEOPLE,FEE_TIME from "+resUserName+".ORC_TRMCD_DETAIL";
		sql+=" union all ";
		sql+="select to_char(MOUTH,'yyyy-MM') as MOUTH,CITY,RISK_TYPE,REASON,FEE_PEOPLE,FEE_TIME from "+resUserName+".ORC_RAOBTI_DETAIL";
		sql+=" union all ";
		sql+="select to_char(MOUTH,'yyyy-MM') as MOUTH,CITY,RISK_TYPE,REASON,FEE_PEOPLE,FEE_TIME from "+resUserName+".ORC_TFHE_DETAIL";
		sql+=" union all ";
		sql+="select to_char(MOUTH,'yyyy-MM') as MOUTH,CITY,RISK_TYPE,REASON,FEE_PEOPLE,FEE_TIME from "+resUserName+".ORC_REMA_DETAIL";
		sql+=" union all ";
		sql+="select to_char(MOUTH,'yyyy-MM') as MOUTH,CITY,RISK_TYPE,REASON,FEE_PEOPLE,FEE_TIME from "+resUserName+".ORC_ZCCA_DETAIL";
		sql+=" union all ";
		sql+="select to_char(MOUTH,'yyyy-MM') as MOUTH,CITY,RISK_TYPE,REASON,FEE_PEOPLE,FEE_TIME from "+resUserName+".ORC_RCA_DETAIL";
		sql+=") A";
		sql+=" where trim(A.REASON) is not NULL and trim(A.FEE_PEOPLE) is not NULL and trim(A.FEE_TIME) is not NULL";
		if(!city.equals("") && !city.equals("全省") && !city.equals("--") && !city.equals("--请选择--") && !city.equals("四川") && !date.equals("")) {
			sql+=" and A.CITY='"+city+"' and A.MOUTH='"+date+"'";
		}else if((city.equals("") ||city.equals("") || city.equals("全省") || city.equals("--") || city.equals("--请选择--") || city.equals("四川") )&& !date.equals("")) {
			sql+=" and A.MOUTH='"+date+"'";
		}else if(!city.equals("") && !city.equals("全省") && !city.equals("--") && !city.equals("--请选择--") && !city.equals("四川") && date.equals("")) {
			sql+=" and A.CITY='"+city+"'";
		}
		int amount=jdbcTemplate.queryForInt(sql);
		jsonObject.put("AMOUNT",amount);
		response.setContentType("application/json; charset=UTF-8");
		response.getWriter().print(jsonObject.toString());
	}
	
	/**
	 * 获取详情表的数据
	 */
	@RequestMapping(value="/findDetail.ilf")
	public void findDetail(HttpServletRequest request,HttpServletResponse response,String riskType,String city,String mouth) throws Exception {
		JSONObject jsonObject=JSONObject.fromObject("{success:true}");
		String sql="";
		if(riskType!=null) {
			sql+="select A.MOUTH,A.CITY,B.RISK_TYPE,C.RISK_NAME,count(*) as AMOUNT from";
			sql+="(";
			sql+="select to_char(MOUTH,'yyyy-MM') as MOUTH,CITY,RISK_TYPE,RISK_NAME,REASON,FEE_PEOPLE,FEE_TIME from "+resUserName+".ORC_CGI_DETAIL";
			sql+=" union all ";
			sql+="select to_char(MOUTH,'yyyy-MM') as MOUTH,CITY,RISK_TYPE,RISK_NAME,REASON,FEE_PEOPLE,FEE_TIME from "+resUserName+".ORC_CER_DETAIL";
			sql+=" union all ";
			sql+="select to_char(MOUTH,'yyyy-MM') as MOUTH,CITY,RISK_TYPE,RISK_NAME,REASON,FEE_PEOPLE,FEE_TIME from "+resUserName+".ORC_OSP_DETAIL";
			sql+=" union all ";
			sql+="select to_char(MOUTH,'yyyy-MM') as MOUTH,CITY,RISK_TYPE,RISK_NAME,REASON,FEE_PEOPLE,FEE_TIME from "+resUserName+".ORC_NVOTM_DETAIL";
			sql+=" union all ";
			sql+="select to_char(MOUTH,'yyyy-MM') as MOUTH,CITY,RISK_TYPE,RISK_NAME,REASON,FEE_PEOPLE,FEE_TIME from "+resUserName+".ORC_RPA_DETAIL";
			sql+=" union all ";
			sql+="select to_char(MOUTH,'yyyy-MM') as MOUTH,CITY,RISK_TYPE,RISK_NAME,REASON,FEE_PEOPLE,FEE_TIME from "+resUserName+".ORC_CTP_DETAIL";
			sql+=" union all ";
			sql+="select to_char(MOUTH,'yyyy-MM') as MOUTH,CITY,RISK_TYPE,RISK_NAME,REASON,FEE_PEOPLE,FEE_TIME from "+resUserName+".ORC_PCOND_DETAIL";
			sql+=" union all ";
			sql+="select to_char(MOUTH,'yyyy-MM') as MOUTH,CITY,RISK_TYPE,RISK_NAME,REASON,FEE_PEOPLE,FEE_TIME from "+resUserName+".ORC_OM_DETAIL";
			sql+=" union all ";
			sql+="select to_char(MOUTH,'yyyy-MM') as MOUTH,CITY,RISK_TYPE,RISK_NAME,REASON,FEE_PEOPLE,FEE_TIME from "+resUserName+".ORC_CACDD_DETAIL";
			sql+=" union all ";
			sql+="select to_char(MOUTH,'yyyy-MM') as MOUTH,CITY,RISK_TYPE,RISK_NAME,REASON,FEE_PEOPLE,FEE_TIME from "+resUserName+".ORC_CTNH_DETAIL";
			sql+=" union all ";
			sql+="select to_char(MOUTH,'yyyy-MM') as MOUTH,CITY,RISK_TYPE,RISK_NAME,REASON,FEE_PEOPLE,FEE_TIME from "+resUserName+".ORC_POOI_DETAIL";
			sql+=" union all ";
			sql+="select to_char(MOUTH,'yyyy-MM') as MOUTH,CITY,RISK_TYPE,RISK_NAME,REASON,FEE_PEOPLE,FEE_TIME from "+resUserName+".ORC_NSCEA_DETAIL";
			sql+=" union all ";
			sql+="select to_char(MOUTH,'yyyy-MM') as MOUTH,CITY,RISK_TYPE,RISK_NAME,REASON,FEE_PEOPLE,FEE_TIME from "+resUserName+".ORC_TRMCD_DETAIL";
			sql+=" union all ";
			sql+="select to_char(MOUTH,'yyyy-MM') as MOUTH,CITY,RISK_TYPE,RISK_NAME,REASON,FEE_PEOPLE,FEE_TIME from "+resUserName+".ORC_RAOBTI_DETAIL";
			sql+=" union all ";
			sql+="select to_char(MOUTH,'yyyy-MM') as MOUTH,CITY,RISK_TYPE,RISK_NAME,REASON,FEE_PEOPLE,FEE_TIME from "+resUserName+".ORC_TFHE_DETAIL";
			sql+=" union all ";
			sql+="select to_char(MOUTH,'yyyy-MM') as MOUTH,CITY,RISK_TYPE,RISK_NAME,REASON,FEE_PEOPLE,FEE_TIME from "+resUserName+".ORC_REMA_DETAIL";
			sql+=" union all ";
			sql+="select to_char(MOUTH,'yyyy-MM') as MOUTH,CITY,RISK_TYPE,RISK_NAME,REASON,FEE_PEOPLE,FEE_TIME from "+resUserName+".ORC_ZCCA_DETAIL";
			sql+=" union all ";
			sql+="select to_char(MOUTH,'yyyy-MM') as MOUTH,CITY,RISK_TYPE,RISK_NAME,REASON,FEE_PEOPLE,FEE_TIME from "+resUserName+".ORC_RCA_DETAIL";
			sql+=") A,"+resUserName+".ORC_RISK_TYPE_DETAIL B,"+resUserName+".ORC_RISK_NAME_DETAIL C ";
			sql+=" where A.RISK_TYPE=B.ID and A.RISK_NAME=C.ID and A.CITY='"+city+"' and A.MOUTH='"+mouth+"' and B.RISK_TYPE='"+riskType+"'";
			sql+=" and trim(A.REASON) is not NULL and trim(A.FEE_PEOPLE) is not NULL and trim(A.FEE_TIME) is not NULL";
			sql+=" group by B.RISK_TYPE,C.RISK_NAME,A.CITY,A.MOUTH";
			sql+=" order by B.RISK_TYPE,C.RISK_NAME,A.CITY,A.MOUTH";
			List<Map<String, Object>> list=jdbcTemplate.queryForList(sql);
			jsonObject.put("list", list);
		}else {
			jsonObject=JSONObject.fromObject("{success:false}");
		}
		response.setContentType("application/json; charset=UTF-8");
		response.getWriter().print(JSONObject.fromObject(jsonObject).toString());
	}
}
