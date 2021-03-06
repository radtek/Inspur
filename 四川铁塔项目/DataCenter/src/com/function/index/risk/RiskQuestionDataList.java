package com.function.index.risk;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
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

@Controller("com.function.index.risk.RiskQuestionDataList")
@RequestMapping(value = "/riskQuestionDataListAction")
public class RiskQuestionDataList {
	@Autowired
	private JdbcTemplate jdbcTemplate;

	private static String resUserName = "TOWERCRNOP";

	@SuppressWarnings("unchecked")
	@RequestMapping("/findRiskDataListFirst.ilf")
	public void findRiskDataListFirst(HttpServletRequest request, HttpServletResponse response, String qu_id)
			throws Exception {
		JSONObject jsonObject = JSONObject.fromObject("{success:true}");
		String tablename = RiskControlTable.getValueByKey(qu_id);
		String sql = "";
		sql += "select ID,QU_TYPE from " + resUserName + ".ORC_QU_TYPE_DETAIL where RISK_NAME='" + qu_id
				+ "' order by ID";
		List<Map<String, Object>> list = jdbcTemplate.queryForList(sql);
		sql = "select RISK_NAME from " + resUserName + ".ORC_RISK_NAME_DETAIL where ID='" + qu_id + "'";
		String risk_name = (String) jdbcTemplate.queryForList(sql).get(0).get("RISK_NAME");
		jsonObject.put("th_info", list);
		jsonObject.put("RISK_NAME", risk_name);
		response.setContentType("application/json; charset=UTF-8");
		response.getWriter().print(jsonObject.toString());
	}

	@SuppressWarnings("unchecked")
	@RequestMapping("/findRiskDataListTable.ilf")
	public void findRiskDataListTable(@RequestParam String qu_id, @RequestParam String tableparam,
			@RequestParam String conditions, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
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
		String tablename = RiskControlTable.getValueByKey(qu_id);
		String sql = "";
		sql += "select ID,QU_TYPE from " + resUserName + ".ORC_QU_TYPE_DETAIL where RISK_NAME='" + qu_id
				+ "' order by ID";
		List<Map<String, Object>> first_list = jdbcTemplate.queryForList(sql);
		sql = "select ROWNUM as ID,e.* from(";
		sql += "select to_char(a.MOUTH,'yyyy-MM') as MOUTH,a.CITY,";
		for (int i = 0; i < first_list.size(); i++) {
			Map<String, Object> map = first_list.get(i);
			if (i == (first_list.size() - 1)) {
				sql += "'" + map.get("ID") + "'||'~'||(select count(*) from " + resUserName + "."
						+ RiskControlTable.getValueByKey(qu_id)
						+ " where CITY=a.CITY and to_char(MOUTH,'yyyy-MM')=to_char(a.MOUTH,'yyyy-MM') and QU_TYPE='"
						+ map.get("ID")
						+ "' and (trim(REASON) is null or trim(FEE_TIME) is null or trim(FEE_PEOPLE) is null)) as A"
						+ map.get("ID");
			} else {
				sql += "'" + map.get("ID") + "'||'~'||(select count(*) from " + resUserName + "."
						+ RiskControlTable.getValueByKey(qu_id)
						+ " where CITY=a.CITY and to_char(MOUTH,'yyyy-MM')=to_char(a.MOUTH,'yyyy-MM') and QU_TYPE='"
						+ map.get("ID")
						+ "' and (trim(REASON) is null or trim(FEE_TIME) is null or trim(FEE_PEOPLE) is null)) as A"
						+ map.get("ID") + ",";
			}
		}
		sql += " FROM " + resUserName + "." + RiskControlTable.getValueByKey(qu_id)
				+ " a group by to_char(a.MOUTH,'yyyy-MM'),a.CITY ) e ";
		if (!isProvince) {
			sql += "where CITY='" + belongArea + "'";
			if (!searchDate.equals("")) {
				sql += " and MOUTH='" + searchDate + "'";
			}
		} else {
			if (!searchCity.equals("") && !searchDate.equals("")) {
				sql += " where CITY='" + searchCity + "' and MOUTH='" + searchDate + "'";
			} else if (searchCity.equals("") && !searchDate.equals("")) {
				sql += " where MOUTH='" + searchDate + "'";
			} else if (!searchCity.equals("") && searchDate.equals("")) {
				sql += " where CITY='" + searchCity + "'";
			}
		}

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
	 * ?????????????????? ?????????
	 * 
	 */
	private List<Map<String, Object>> findRiskDataListFirst(String qu_id) {
		String sql = "";
		sql += "select ID,QU_TYPE from " + resUserName + ".ORC_QU_TYPE_DETAIL where RISK_NAME='" + qu_id
				+ "' order by ID";
		List<Map<String, Object>> list = jdbcTemplate.queryForList(sql);

		if (list == null || list.size() <= 0) {
			return null;
		}

		return list;
	}

	/**
	 * ????????????????????????
	 */
	private List<Map<String, Object>> queryData(String qu_id, String city, String date, HttpServletRequest request) {
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
		sql += "select ID,QU_TYPE from " + resUserName + ".ORC_QU_TYPE_DETAIL where RISK_NAME='" + qu_id
				+ "' order by ID";
		List<Map<String, Object>> first_list = jdbcTemplate.queryForList(sql);
		sql = "select ROWNUM as ID,e.* from(";
		sql += "select to_char(a.MOUTH,'yyyy-MM') as MOUTH,a.CITY,";

		for (int i = 0; i < first_list.size(); i++) {
			Map<String, Object> map = first_list.get(i);
			if (i == (first_list.size() - 1)) {
				sql += "'" + map.get("ID") + "'||'~'||(select count(*) from " + resUserName + "."
						+ RiskControlTable.getValueByKey(qu_id)
						+ " where CITY=a.CITY and to_char(MOUTH,'yyyy-MM')=to_char(a.MOUTH,'yyyy-MM') and QU_TYPE='"
						+ map.get("ID")
						+ "' and (trim(REASON) is null or trim(FEE_TIME) is null or trim(FEE_PEOPLE) is null)) as A"
						+ map.get("ID");
			} else {
				sql += "'" + map.get("ID") + "'||'~'||(select count(*) from " + resUserName + "."
						+ RiskControlTable.getValueByKey(qu_id)
						+ " where CITY=a.CITY and to_char(MOUTH,'yyyy-MM')=to_char(a.MOUTH,'yyyy-MM') and QU_TYPE='"
						+ map.get("ID")
						+ "' and (trim(REASON) is null or trim(FEE_TIME) is null or trim(FEE_PEOPLE) is null)) as A"
						+ map.get("ID") + ",";
			}
		}
		sql += " FROM " + resUserName + "." + RiskControlTable.getValueByKey(qu_id)
				+ " a group by to_char(a.MOUTH,'yyyy-MM'),a.CITY ) e ";
		if (!isProvince) {
			sql += "where CITY='" + belongArea + "'";
			if (!searchDate.equals("")) {
				sql += " and MOUTH='" + searchDate + "'";
			}
		} else {
			if (!searchCity.equals("") && !searchDate.equals("")) {
				sql += " where CITY='" + searchCity + "' and MOUTH='" + searchDate + "'";
			} else if (searchCity.equals("") && !searchDate.equals("")) {
				sql += " where MOUTH='" + searchDate + "'";
			} else if (!searchCity.equals("") && searchDate.equals("")) {
				sql += " where CITY='" + searchCity + "'";
			}
		}

		List<Map<String, Object>> objectList = jdbcTemplate.queryForList(sql);

		if (objectList == null || objectList.size() <= 0) {
			return null;
		}

		return objectList;
	}

	/**
	 * ???????????????????????? </br>
	 * 
	 * ???????????????????????????????????????????????????????????????????????????????????????
	 */
	@RequestMapping(value = "/exportExcel.ilf")
	@ResponseBody
	public void createExcel(@RequestParam String qu_id, @RequestParam String date, @RequestParam String city,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		// city?????????????????????????????????
		city = java.net.URLDecoder.decode(city, "utf-8");

		// ?????????????????????(????????????)
		List<Map<String, Object>> titleList = findRiskDataListFirst(qu_id);

		if (titleList == null) {
			throw new Exception("????????????????????????????????????????????????");
		}

		// ?????????
		int columnCount = titleList.size() + 1;

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
		cell.setCellValue("??????");

		// ???????????????CellRangeAddress???????????????????????????????????????????????????????????? ?????????
		sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, columnCount));

		// ???sheet??????????????????
		HSSFRow row2 = sheet.createRow(1);

		// ????????????????????????????????????????????????
		HSSFCell cityCell = row2.createCell(0);
		cityCell.setCellStyle(style);
		cityCell.setCellValue("??????");// ?????????????????????????????????????????????????????????????????????

		HSSFCell dateCell = row2.createCell(1);
		dateCell.setCellStyle(style);
		dateCell.setCellValue("??????");

		for (int i = 0; i < titleList.size(); i++) {
			HSSFCell titleCell = row2.createCell(i + 2);// ???????????????
			titleCell.setCellStyle(style);
			titleCell.setCellValue(titleList.get(i).get("QU_TYPE").toString());// ???????????????
		}

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
		sheet.autoSizeColumn((short) 8);
		sheet.autoSizeColumn((short) 9);

		// ????????????
		List<Map<String, Object>> questionDataList = queryData(qu_id, city, date, request);

		if (questionDataList == null) {
			throw new Exception("??????????????????????????????????????????????????????");
		}

		// ????????????(?????????)
		for (int i = 0; i < questionDataList.size(); i++) {
			// ??????????????????????????????
			HSSFRow rowx = sheet.createRow(i + 2);

			// ????????????????????????
			Map<String, Object> map = questionDataList.get(i);

			// ??????key????????????????????????????????????????????????
			List<String> dataList = new ArrayList<String>();
			dataList.add(map.get("CITY").toString());// ???????????????????????????????????????????????????????????????????????????????????????????????????????????????
			dataList.add(map.get("MOUTH").toString());// ??????????????????

			Iterator<String> iterator = map.keySet().iterator();
			while (iterator.hasNext()) {
				String key = iterator.next();
				if (!"ID".equals(key) && !"CITY".equals(key) && !"MOUTH".equals(key)) {
					String data = map.get(key).toString();// ??????????????????
					dataList.add(data.substring(data.lastIndexOf("~") + 1));// ???????????????????????????????????????
				}

			}

			// ????????????(?????????)
			for (int k = 0; k < dataList.size(); k++) {
				HSSFCell cellData = rowx.createCell(k);// ?????????
				cellData.setCellStyle(style);
				cellData.setCellValue(dataList.get(k));// ??????????????????
			}
		}

		// ??????Excel??????
		OutputStream output = response.getOutputStream();
		response.reset();

		String agent = request.getHeader("USER-AGENT").toLowerCase();
		response.setContentType("application/msexcel");

		String fileName = queryRiskName(qu_id, date);
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

	private String queryRiskName(String qu_id, String date) {
		String riskName = "??????????????????";
		String sql = "select RISK_NAME from " + resUserName + ".ORC_RISK_NAME_DETAIL where ID='" + qu_id + "'";
		String result = (String) jdbcTemplate.queryForList(sql).get(0).get("RISK_NAME");

		if (StringUtils.isNotEmpty(result)) {
			riskName = result;
		}

		if (StringUtils.isNotEmpty(date)) {
			riskName = riskName + "(" + date + ")";// ???????????????????????????????????????
		}

		riskName = riskName + ".xls";// ????????????

		return riskName;
	}
}
