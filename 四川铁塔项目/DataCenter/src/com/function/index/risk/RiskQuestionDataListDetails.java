package com.function.index.risk;

import java.text.SimpleDateFormat;
import java.util.Date;
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

@Controller("com.function.index.risk.RiskQuestionDataListDetails")
@RequestMapping(value="/riskQuestionDataListDetailsAction")
public class RiskQuestionDataListDetails {
	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	private static String resUserName = "TOWERCRNOP";
	
	@SuppressWarnings("unchecked")
	@RequestMapping("/findRiskDataListDetails.ilf")
	public void findRiskDataListDetailsListFirst(HttpServletRequest request,HttpServletResponse response,String id,String risk_name) throws Exception{
		JSONObject jsonObject=JSONObject.fromObject("{success:true}");
		String sql="";
		sql+="select ID from "+resUserName+".ORC_RISK_NAME_DETAIL where RISK_NAME='"+risk_name+"'";
		String tablePreName=jdbcTemplate.queryForList(sql).get(0).get("ID")+"";
		sql="select * from "+resUserName+"."+RiskControlTable.getValueByKey(tablePreName)+" where ID="+id;
		jsonObject.put("list",jdbcTemplate.queryForList(sql));
		System.out.println(jdbcTemplate.queryForList(sql).toString());
		response.setContentType("application/json; charset=UTF-8");
		response.getWriter().print(jsonObject.toString());
	}
	
	@SuppressWarnings("unchecked")
	@RequestMapping("/merageRiskDataListDetails.ilf")
	public void mergeRiskDataListDetailsList(HttpServletRequest request,HttpServletResponse response,String id,String risk_name,String reason) throws Exception{
		JSONObject jsonObject=JSONObject.fromObject("{success:true}");
		if(reason!=null && !reason.equals("")) {
			
			String sql="";
			sql+="select ID from "+resUserName+".ORC_RISK_NAME_DETAIL where RISK_NAME='"+risk_name+"'";
			String tablePreName=jdbcTemplate.queryForList(sql).get(0).get("ID")+"";
			Map<String, Object> user=(Map<String, Object>) request.getSession().getAttribute("LoginUserInfo");
			String fee_people=user.get("EMPLOYEE_NAME").toString();
			sql="update "+resUserName+"."+RiskControlTable.getValueByKey(tablePreName)+" set FEE_TIME=sysdate,FEE_PEOPLE='"+fee_people+"',REASON='"+reason+"' where ID='"+id+"'";
			//System.out.println(sql);
			int i=jdbcTemplate.update(sql);
			//System.out.println("??????:"+i+"???");
		}else {
			jsonObject=JSONObject.fromObject("{success:false}");
		}
		response.setContentType("application/json; charset=UTF-8");
		response.getWriter().print(jsonObject.toString());
	}
}
