package com.function.index.risk;

import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.CellRangeAddress;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.systemConfig.model.DataTableResult;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

@Controller("com.function.index.risk.RiskQuestionDataListDetailsList")
@RequestMapping(value = "/riskQuestionDataListDetailsListAction")
public class RiskQuestionDataListDetailsList {
	@Autowired
	private JdbcTemplate jdbcTemplate;

	private static String resUserName = "TOWERCRNOP";

	@SuppressWarnings("unchecked")
	@RequestMapping("/findRiskDataListDetailsListFirst.ilf")
	public void findRiskDataListDetailsListFirst(HttpServletRequest request, HttpServletResponse response, String qu_id)
			throws Exception {
		JSONObject jsonObject = JSONObject.fromObject("{success:true}");
		String sql = "";
		sql += "select ID,QU_TYPE,RISK_NAME from " + resUserName + ".ORC_QU_TYPE_DETAIL where ID='" + qu_id
				+ "' order by ID";
		String qu_type = (String) jdbcTemplate.queryForList(sql).get(0).get("QU_TYPE");
		jsonObject.put("QU_TYPE", qu_type);
		response.setContentType("application/json; charset=UTF-8");
		response.getWriter().print(jsonObject.toString());
	}

	@SuppressWarnings("unchecked")
	@RequestMapping("/findRiskDataListDetailsListTable.ilf")
	public void findRiskDataListDetailsListTable(@RequestParam String qu_id, @RequestParam String city,
			@RequestParam String mouth, @RequestParam String tableparam, @RequestParam String conditions,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		Long sEcho = 0L;
		Integer displayStart = 0;
		Integer iDisplayLength = 0;
		JSONArray jsons = JSONArray.fromObject(tableparam);
		HashMap<String, Object> conditionMap = new HashMap<String, Object>();
		if (jsons != null && jsons.size() != 0) {
			for (int i = 0; i < jsons.size(); i++) {
				JSONObject json = JSONObject.fromObject(jsons.get(i));
				String key = json.getString("name");
				if (key.equals("sEcho")) {
					sEcho = Long.parseLong(json.getString("value"));
				} else if (key.equals("iDisplayStart")) {
					displayStart = Integer.parseInt(json.getString("value"));
					conditionMap.put("iDisplayStart", displayStart);
				} else if (key.equals("iDisplayLength")) {
					iDisplayLength = Integer.parseInt(json.getString("value"));
					conditionMap.put("iDisplayLength", iDisplayLength);
				}
			}
		}
		JSONArray condition = JSONArray.fromObject(conditions);
		if (conditions != null && condition.size() != 0) {
			for (int i = 0; i < condition.size(); i++) {
				JSONObject jsonObject = JSONObject.fromObject(condition.get(i));
				if (jsonObject.get("value") != null && !"".equals(jsonObject.getString("value"))) {
					conditionMap.put(jsonObject.getString("name"), jsonObject.getString("value"));
				}
			}
		}
		/* ?????????????????? */
		String searchCity = "";
		String searchDate = "";
		if (conditionMap.containsKey("CITY") && !"".equals(conditionMap.get("CITY").toString())
				&& !"--".equals(conditionMap.get("CITY").toString())
				&& !"--?????????--".equals(conditionMap.get("CITY").toString())
				&& !"??????".equals(conditionMap.get("CITY").toString())
				&& !"??????".equals(conditionMap.get("CITY").toString())) {
			searchCity = conditionMap.get("CITY").toString();
		}
		if (conditionMap.containsKey("DATE") && !"".equals(conditionMap.get("DATE").toString())) {
			searchDate = conditionMap.get("DATE").toString();
		}
		/* ?????????????????????????????? */
		Boolean isProvince = false;
		String belongArea = "";
		isProvince = (Boolean) request.getSession().getAttribute("IS_PROVINCE");
		belongArea = (String) request.getSession().getAttribute("BELONG_AREA");
		String sql = "";
		sql += "select ID,QU_TYPE,RISK_NAME from " + resUserName + ".ORC_QU_TYPE_DETAIL where ID='" + qu_id
				+ "' order by ID";
		String tablePreName = jdbcTemplate.queryForList(sql).get(0).get("RISK_NAME") + "";
		sql = "select ROWNUM as NUM,E.* from";
		sql += "(select A.ID,A.CITY,A.COUNTY,A.MOUTH,B.RISK_TYPE,C.RISK_NAME,D.QU_TYPE from";
		sql += "(select ID,CITY,COUNTY,to_char(MOUTH,'yyyy-MM') as MOUTH,RISK_TYPE,RISK_NAME,QU_TYPE,REASON,FEE_TIME,FEE_PEOPLE from "
				+ resUserName + "." + RiskControlTable.getValueByKey(tablePreName) + ") A";
		sql += "," + resUserName + ".ORC_RISK_TYPE_DETAIL B";
		sql += "," + resUserName + ".ORC_RISK_NAME_DETAIL C";
		sql += "," + resUserName + ".ORC_QU_TYPE_DETAIL D ";
		sql += "where A.RISK_TYPE=B.ID and A.RISK_NAME=C.ID and A.QU_TYPE=D.ID and A.QU_TYPE=" + qu_id
				+ " and (trim(A.REASON) is null or trim(A.FEE_TIME) is null or trim(A.FEE_PEOPLE) is null) ";
		// sql+="and A.CITY='"+city+"' and A.MOUTH='"+mouth+"'";

		if (!isProvince) {
			sql += "and A.CITY='" + belongArea + "'";
			if (!searchDate.equals("") && !searchDate.equals(mouth)) {
				sql += " and A.MOUTH='" + searchDate + "'";
			} else {
				sql += " and A.MOUTH='" + mouth + "'";
			}
		} else {
			if (!searchCity.equals("") && !searchDate.equals("")) {
				if (!searchCity.equals(city) && !searchDate.equals(mouth)) {
					sql += " and A.CITY='" + searchCity + "' and A.MOUTH='" + searchDate + "'";
				} else if (!searchCity.equals(city) && searchDate.equals(mouth)) {
					sql += " and A.CITY='" + searchCity + "' and A.MOUTH='" + mouth + "'";
				} else if (searchCity.equals(city) && searchDate.equals(mouth)) {
					sql += " and A.CITY='" + city + "' and A.MOUTH='" + mouth + "'";
				} else if (searchCity.equals(city) && !searchDate.equals(mouth)) {
					sql += " and A.CITY='" + city + "' and A.MOUTH='" + searchDate + "'";
				}
			} else if (searchCity.equals("") && searchDate.equals("")) {
				sql += " and A.CITY='" + city + "' and A.MOUTH='" + mouth + "'";
			} else if (!searchCity.equals("") && searchDate.equals("")) {
				sql += " and A.CITY='" + searchCity + "' and A.MOUTH='" + mouth + "'";
			} else if (searchCity.equals("") && !searchDate.equals("")) {
				sql += " and A.CITY='" + city + "' and A.MOUTH='" + searchDate + "'";
			}
		}
		sql += ")E";
		Integer count = jdbcTemplate.queryForInt("SELECT COUNT(1) FROM(" + sql + ")");
		Integer lastIndex = displayStart + iDisplayLength;
		String pageSql = "";
		pageSql += "SELECT B.* FROM(";
		pageSql += "	SELECT A.*,ROWNUM AS RN FROM(" + sql + ") A WHERE ROWNUM <= " + lastIndex;
		pageSql += ") B WHERE B.RN > " + displayStart;
		List<Map<String, Object>> objectList = jdbcTemplate.queryForList(pageSql);
		DataTableResult<Map<String, Object>> tableData = new DataTableResult<Map<String, Object>>();
		tableData.setsEcho(sEcho);
		tableData.setAaData(objectList);
		tableData.setiTotalRecords(count);
		tableData.setiTotalDisplayRecords(count);
		response.setContentType("application/json; charset=UTF-8");
		response.getWriter().print(JSONObject.fromObject(tableData).toString());
	}

	/**
	 * ??????????????????</br>
	 * 
	 */
	@RequestMapping(value = "/exportExcel.ilf")
	@ResponseBody
	public void createExcel(@RequestParam String qu_id, @RequestParam String mouth, @RequestParam String date,
			@RequestParam String city, HttpServletRequest request, HttpServletResponse response) throws Exception {
		// city?????????????????????????????????
		city = java.net.URLDecoder.decode(city, "utf-8");

		// ?????????
		int columnCount = 7;

		// ??????HSSFWorkbook??????(excel???????????????)
		HSSFWorkbook wb = new HSSFWorkbook();

		// ????????????sheet?????????excel????????????
		HSSFSheet sheet = wb.createSheet("??????");

		// ???sheet??????????????????
		HSSFRow row1 = sheet.createRow(0);

		// ???????????????
		HSSFCell cell = row1.createCell(0);

		// 1.??????????????????
		HSSFFont font = wb.createFont();
		font.setFontHeightInPoints((short) 12);
		font.setFontName("?????????");

		// 2.??????????????????
		HSSFCellStyle style = wb.createCellStyle();
		style.setAlignment(HSSFCellStyle.ALIGN_CENTER);// ??????????????????
		style.setFont(font); // ????????????????????????
		style.setWrapText(true);

		// 3.?????????????????????
		cell.setCellStyle(style);

		// ?????????????????????
		cell.setCellValue("??????????????????");

		// ???????????????CellRangeAddress???????????????????????????????????????????????????????????? ?????????
		sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, columnCount));

		// ???sheet??????????????????
		HSSFRow row2 = sheet.createRow(1);

		// ????????????????????????????????????????????????
		HSSFCell cell0 = row2.createCell(0);
		cell0.setCellStyle(style);
		cell0.setCellValue("  ??????        ");

		HSSFCell cell1 = row2.createCell(1);
		cell1.setCellStyle(style);
		cell1.setCellValue("??????");

		HSSFCell cell2 = row2.createCell(2);
		cell2.setCellStyle(style);
		cell2.setCellValue(" ??????  ");

		HSSFCell cell3 = row2.createCell(3);
		cell3.setCellStyle(style);
		cell3.setCellValue("  ??????    ");

		HSSFCell cell4 = row2.createCell(4);
		cell4.setCellStyle(style);
		cell4.setCellValue("???????????????");

		HSSFCell cell5 = row2.createCell(5);
		cell5.setCellStyle(style);
		cell5.setCellValue(" ???????????????    ");

		HSSFCell cell6 = row2.createCell(6);
		cell6.setCellStyle(style);
		cell6.setCellValue("  ????????????        ");

		// ???????????????(???????????????????????????????????????)
		sheet.autoSizeColumn((short) 0);
		sheet.autoSizeColumn((short) 1);
		// sheet.setColumnWidth(1, 10);// ????????????????????????yyyy-mm?????????????????????????????????,??????
		sheet.autoSizeColumn((short) 2);
		sheet.autoSizeColumn((short) 3);
		sheet.autoSizeColumn((short) 4);
		sheet.autoSizeColumn((short) 5);
		sheet.autoSizeColumn((short) 6);
		sheet.autoSizeColumn((short) 7);

		// ????????????
		List<Map<String, Object>> questionDataList = queryData(qu_id, mouth, city, date, request);

		if (questionDataList == null) {
			return;// ?????????????????????????????????????????????????????????????????????????????????
		}

		// ????????????(?????????)
		for (int i = 0; i < questionDataList.size(); i++) {
			// ??????????????????????????????
			HSSFRow rowx = sheet.createRow(i + 2);

			// ????????????????????????
			Map<String, Object> map = questionDataList.get(i);

			HSSFCell cellData0 = rowx.createCell(0);// ?????????
			cellData0.setCellStyle(style);
			cellData0.setCellValue(map.get("ID").toString());// ??????????????????

			HSSFCell cellData1 = rowx.createCell(1);// ?????????
			cellData1.setCellStyle(style);
			cellData1.setCellValue(map.get("CITY").toString());// ??????????????????

			HSSFCell cellData2 = rowx.createCell(2);// ?????????
			cellData2.setCellStyle(style);
			cellData2.setCellValue(map.get("COUNTY").toString());// ??????????????????

			HSSFCell cellData3 = rowx.createCell(3);// ?????????
			cellData3.setCellStyle(style);
			cellData3.setCellValue(map.get("MOUTH").toString());// ??????????????????

			HSSFCell cellData4 = rowx.createCell(4);// ?????????
			cellData4.setCellStyle(style);
			cellData4.setCellValue(map.get("RISK_TYPE").toString());// ??????????????????

			HSSFCell cellData5 = rowx.createCell(5);// ?????????
			cellData5.setCellStyle(style);
			cellData5.setCellValue(map.get("RISK_NAME").toString());// ??????????????????

			HSSFCell cellData6 = rowx.createCell(6);// ?????????
			cellData6.setCellStyle(style);
			cellData6.setCellValue(map.get("QU_TYPE").toString());// ??????????????????
		}

		// ??????Excel??????
		OutputStream output = response.getOutputStream();
		response.reset();

		String agent = request.getHeader("USER-AGENT").toLowerCase();
		response.setContentType("application/msexcel");

		String fileName = "????????????????????????.xls";
		String codedFileName = java.net.URLEncoder.encode(fileName, "UTF-8");

		if (agent.contains("firefox")) {
			response.setCharacterEncoding("utf-8");
			response.setHeader("content-disposition",
					"attachment;filename=" + new String(fileName.getBytes(), "ISO8859-1"));
		} else {
			response.setHeader("content-disposition", "attachment;filename=" + codedFileName);
		}

		wb.write(output);
		output.close();
	}

	/**
	 * ??????????????????
	 */
	private List<Map<String, Object>> queryData(String qu_id, String mouth, String city, String date,
			HttpServletRequest request) {
		/* ?????????????????? */
		String searchCity = "";
		String searchDate = date;

		if (!"--".equals(city) && !"--?????????--".equals(city) && !"??????".equals(city) && !"??????".equals(city)) {
			searchCity = city;
		}

		/* ?????????????????????????????? */
		Boolean isProvince = false;
		String belongArea = "";
		isProvince = (Boolean) request.getSession().getAttribute("IS_PROVINCE");
		belongArea = (String) request.getSession().getAttribute("BELONG_AREA");

		String sql = "";
		sql += "select ID,QU_TYPE,RISK_NAME from " + resUserName + ".ORC_QU_TYPE_DETAIL where ID='" + qu_id
				+ "' order by ID";
		String tablePreName = jdbcTemplate.queryForList(sql).get(0).get("RISK_NAME") + "";
		sql = "select ROWNUM as NUM,E.* from";
		sql += "(select A.ID,A.CITY,A.COUNTY,A.MOUTH,B.RISK_TYPE,C.RISK_NAME,D.QU_TYPE from";
		sql += "(select ID,CITY,COUNTY,to_char(MOUTH,'yyyy-MM') as MOUTH,RISK_TYPE,RISK_NAME,QU_TYPE,REASON,FEE_TIME,FEE_PEOPLE from "
				+ resUserName + "." + RiskControlTable.getValueByKey(tablePreName) + ") A";
		sql += "," + resUserName + ".ORC_RISK_TYPE_DETAIL B";
		sql += "," + resUserName + ".ORC_RISK_NAME_DETAIL C";
		sql += "," + resUserName + ".ORC_QU_TYPE_DETAIL D ";
		sql += "where A.RISK_TYPE=B.ID and A.RISK_NAME=C.ID and A.QU_TYPE=D.ID and A.QU_TYPE=" + qu_id
				+ " and (trim(A.REASON) is null or trim(A.FEE_TIME) is null or trim(A.FEE_PEOPLE) is null) ";
		// sql+="and A.CITY='"+city+"' and A.MOUTH='"+mouth+"'";

		if (!isProvince) {
			sql += "and A.CITY='" + belongArea + "'";
			if (!searchDate.equals("") && !searchDate.equals(mouth)) {
				sql += " and A.MOUTH='" + searchDate + "'";
			} else {
				sql += " and A.MOUTH='" + mouth + "'";
			}
		} else {
			if (!searchCity.equals("") && !searchDate.equals("")) {
				if (!searchCity.equals(city) && !searchDate.equals(mouth)) {
					sql += " and A.CITY='" + searchCity + "' and A.MOUTH='" + searchDate + "'";
				} else if (!searchCity.equals(city) && searchDate.equals(mouth)) {
					sql += " and A.CITY='" + searchCity + "' and A.MOUTH='" + mouth + "'";
				} else if (searchCity.equals(city) && searchDate.equals(mouth)) {
					sql += " and A.CITY='" + city + "' and A.MOUTH='" + mouth + "'";
				} else if (searchCity.equals(city) && !searchDate.equals(mouth)) {
					sql += " and A.CITY='" + city + "' and A.MOUTH='" + searchDate + "'";
				}
			} else if (searchCity.equals("") && searchDate.equals("")) {
				sql += " and A.CITY='" + city + "' and A.MOUTH='" + mouth + "'";
			} else if (!searchCity.equals("") && searchDate.equals("")) {
				sql += " and A.CITY='" + searchCity + "' and A.MOUTH='" + mouth + "'";
			} else if (searchCity.equals("") && !searchDate.equals("")) {
				sql += " and A.CITY='" + city + "' and A.MOUTH='" + searchDate + "'";
			}
		}
		sql += ")E";

		List<Map<String, Object>> objectList = jdbcTemplate.queryForList(sql);

		if (objectList == null || objectList.size() <= 0) {
			return null;
		}

		return objectList;
	}
}
