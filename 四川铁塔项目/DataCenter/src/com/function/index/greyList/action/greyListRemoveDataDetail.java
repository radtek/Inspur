package com.function.index.greyList.action;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.function.index.greyList.service.GreyListService;
import com.function.index.greyList.service.IGreyListService;

import net.sf.json.JSONObject;

@Controller("com.function.index.greyList.action.greyListRemoveDataDetail")
@RequestMapping(value="/greyListRemoveDataDetail")
public class greyListRemoveDataDetail {

	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	@Autowired
	private IGreyListService iGreyListService;
     

	/*
	 * 	展示灰名单数据列表
	 * 
	 * */
	@RequestMapping("/showDataDetail.ilf")
	public void  showDataDetail(
		@RequestParam String id,
		HttpServletRequest request,
		HttpServletResponse response
	)throws Exception{	
		JSONObject jsonObject=JSONObject.fromObject("{success:true}");
	/*
		 * 	获取灰名单数据详情的sql语句
		 * 
		 * */
		String sql = "SELECT T. ID,T.COUNTY,T.SA_NAME,SA_CODE,T.CITY,T.TABLE_SOURCE,T.ORIGINATOR,T.GL_TYPE,T.GL_DESCRIBE,T.ATTRIBUTION,T.DATA_SOURCE,"
				+ "T.GL_RULE,to_char(T.GLS_TIME,'yyyy-MM-DD')  GLS_TIME,T.VALID_TIME,to_char(EXPIRE_TIME,'yyyy-MM-DD')  EXPIRY_TIME,  REMARKS   FROM GL_GREY_LIST  T "
				+ "WHERE T.ID="+id;
		jsonObject.put("list",jdbcTemplate.queryForList(sql));
		System.out.println(jdbcTemplate.queryForList(sql).toString());
		response.setContentType("application/json; charset=UTF-8");
		response.getWriter().print(jsonObject.toString());
	}	
	
	/*
	 * 	删除灰名单数据列表
	 * 
	 * */
	@RequestMapping("/removeData.ilf")
	public void  removeData(
		@RequestParam String id,
		HttpServletRequest request,
		HttpServletResponse response
	)throws Exception{	
		
	/*
		 * 	发起解除灰名单数据详情
		 * 
		 * */
		JSONObject jsonObject=JSONObject.fromObject("{success:true}");
		int i= iGreyListService.dataBackup(Integer.parseInt(id), "remove");
	 	
	   if(i==0){
		    jsonObject=JSONObject.fromObject("{success:false}");
	   }
	   response.setContentType("application/json; charset=UTF-8");
		response.getWriter().print(jsonObject.toString());
	}	
	
	
	
}

	
	
	
	
	
	

