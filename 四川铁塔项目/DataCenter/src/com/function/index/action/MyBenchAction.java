package com.function.index.action;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.function.index.action.services.FlowCountStub;
import com.function.index.action.services.FlowCountStub.GetFlowCount;
import com.function.index.action.services.FlowCountStub.GetFlowCountResponse;
import com.system.LoginUserUtil;

import net.sf.json.JSONObject;

@Controller("com.function.index.action.MyBenchAction")
@RequestMapping(value = "/myBenchAction")
public class MyBenchAction {

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Autowired
	private LoginUserUtil loginUserUtil;

	private static String resUserName1 = "TOWERCRNOP";

	private static String resUserName2 = "RMW";

	private static String resUserName3 = "NEWIRMS";

	/*
	 * 首页.
	 * 
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping("/findIndexDatas.ilf")
	public void findIndexDatas(HttpServletRequest request, HttpServletResponse response) throws Exception {
		JSONObject jsonObject = JSONObject.fromObject("{success:true}");
		try {
			Boolean isProvince = false;
			String belongArea = "";
			if (request.getSession().getAttribute("INDEX_DATA") != null) {
				jsonObject = (JSONObject) request.getSession().getAttribute("INDEX_DATA");
			} else {
				jsonObject.put("ACCOUNT_CITY", loginUserUtil.getBelongArea(request));
				jsonObject.put("FIR_LINKS", jdbcTemplate
						.queryForList("SELECT * FROM S_SYSTEM_LINK WHERE ID IN(20001,20002,20003,20004,20005,20006)"));
				/*
				 * 获取工单系统URL.格式：IP:Port
				 * 
				 */
				jsonObject.put("GD_URL",
						jdbcTemplate.queryForMap("SELECT * FROM S_SYSTEM_PROPERTY WHERE PROPERTY_NAME = 'gd_url'")
								.get("PRO_VALUE").toString());
				Date date = new Date();
				Integer nowYears = date.getYear() + 1900;
				Integer nowMonth = date.getMonth();
				if (nowMonth == 0) {
					nowYears = nowYears - 1;
					nowMonth = 12;
				}
				String nowYearMonth = nowYears + "" + (nowMonth < 10 ? ("0" + nowMonth) : nowMonth);
				Object loginObject = request.getSession().getAttribute("LoginUserInfo");
				if (loginObject != null) {
					Map<String, Object> loginUser = (HashMap<String, Object>) loginObject;
					/*
					 * 缓存登录账户信息.<是否省级用户、所属地市>
					 * 
					 */
					jsonObject.put("LOGIN_USER", loginUser);
					if (loginUser.get("BELONG_AREA").toString().indexOf("四川") != -1
							|| loginUser.get("BELONG_AREA").toString().indexOf("省") != -1) {
						isProvince = true;
						belongArea = loginUser.get("BELONG_AREA").toString();
						jsonObject.put("IS_MANAGER_ROLE", true);
					} else if (loginUser.get("USER_NAME").toString().toUpperCase().indexOf("ROOT") != -1) {
						isProvince = true;
						belongArea = loginUser.get("BELONG_AREA").toString();
						jsonObject.put("IS_MANAGER_ROLE", true);
					} else {
						jsonObject.put("IS_MANAGER_ROLE", false);
						belongArea = loginUser.get("BELONG_AREA").toString();
					}
					if (belongArea.length() > 2) {
						belongArea = belongArea.substring(0, 2);
					}
					/*
					 * 获取区域标准化(下级)菜单
					 * 
					 */
					List<Map<String, Object>> leftUpMenus = null;
					if (request.getSession().getAttribute("LEFT_UP_MENUS") != null) {
						leftUpMenus = (List<Map<String, Object>>) request.getSession().getAttribute("LEFT_UP_MENUS");
					} else {
						String sql = "";
						if ("root".equals(loginUser.get("USER_NAME").toString())) {
							sql = "SELECT * FROM S_SYSTEM_MENU WHERE TOP_MENU_TYPE = 'LEFT_UPPER_MENU' ORDER BY MENU_SORT ASC";
						} else {
							sql += "SELECT * FROM S_SYSTEM_MENU WHERE BIND_FUNC_ID IN(";
							sql += "	  SELECT DISTINCT(FUNCID) FROM S_SYSTEM_ROLE_FUNC WHERE ROLEID IN(";
							sql += "		  SELECT ROLE_ID FROM S_SYSTEM_GROUP_ROLE WHERE GROUP_ID IN(";
							sql += "			  SELECT GROUP_ID FROM S_SYSTEM_GROUP_USER WHERE USER_ID IN(";
							sql += "				  SELECT ID AS USERID FROM S_SYSTEM_USER WHERE USER_NAME = '"
									+ loginUser.get("USER_NAME").toString() + "'";
							sql += "			  )";
							sql += "		  )";
							sql += "		  UNION ";
							sql += "		  SELECT ROLE_ID FROM S_SYSTEM_USER_ROLE WHERE USER_ID IN(";
							sql += "			  SELECT ID AS USERID FROM S_SYSTEM_USER WHERE USER_NAME = '"
									+ loginUser.get("USER_NAME").toString() + "'";
							sql += "		  )";
							sql += "	  )";
							sql += ") AND TOP_MENU_TYPE = 'LEFT_UPPER_MENU' AND IS_USING = 'Y' ORDER BY MENU_SORT ASC";
						}
						leftUpMenus = jdbcTemplate.queryForList(sql);
						request.getSession().setAttribute("LEFT_UP_MENUS", leftUpMenus);
					}
					jsonObject.put("LEFT_UP_MENUS", leftUpMenus);
					/*
					 * 项目建设辅助管理（下属菜单）
					 * 
					 */
					List<Map<String, Object>> buildMenus = null;
					if (request.getSession().getAttribute("BUILD_MENUS") != null) {
						buildMenus = (List<Map<String, Object>>) request.getSession().getAttribute("BUILD_MENUS");
					} else {
						String sql = "";
						if ("ROOT".equals(loginUser.get("USER_NAME").toString().toUpperCase())) {
							sql = "SELECT * FROM S_SYSTEM_MENU WHERE TOP_MENU_TYPE = 'BUILD_PROJECT_MANAGE' ORDER BY MENU_SORT ASC";
						} else {
							sql += "SELECT * FROM S_SYSTEM_MENU WHERE BIND_FUNC_ID IN(";
							sql += "	  SELECT DISTINCT(FUNCID) FROM S_SYSTEM_ROLE_FUNC WHERE ROLEID IN(";
							sql += "		  SELECT ROLE_ID FROM S_SYSTEM_GROUP_ROLE WHERE GROUP_ID IN(";
							sql += "			  SELECT GROUP_ID FROM S_SYSTEM_GROUP_USER WHERE USER_ID IN(";
							sql += "				  SELECT ID AS USERID FROM S_SYSTEM_USER WHERE USER_NAME = '"
									+ loginUser.get("USER_NAME").toString() + "'";
							sql += "			  )";
							sql += "		  )";
							sql += "		  UNION ";
							sql += "		  SELECT ROLE_ID FROM S_SYSTEM_USER_ROLE WHERE USER_ID IN(";
							sql += "			  SELECT ID AS USERID FROM S_SYSTEM_USER WHERE USER_NAME = '"
									+ loginUser.get("USER_NAME").toString() + "'";
							sql += "		  )";
							sql += "	  )";
							sql += ") AND TOP_MENU_TYPE = 'BUILD_PROJECT_MANAGE' AND IS_USING = 'Y' ORDER BY MENU_SORT ASC";
						}
						buildMenus = jdbcTemplate.queryForList(sql);
						request.getSession().setAttribute("BUILD_MENUS", buildMenus);
					}
					jsonObject.put("BUILD_MENUS", buildMenus);
					/*
					 * 获取门户左上角菜单
					 * 
					 */
					List<Map<String, Object>> homeMenus = null;
					if (request.getSession().getAttribute("HOME_MENUS") != null) {
						homeMenus = (List<Map<String, Object>>) request.getSession().getAttribute("HOME_MENUS");
					} else {
						String sql = "";
						if ("root".equals(loginUser.get("USER_NAME").toString())) {
							sql = "SELECT * FROM S_SYSTEM_MENU WHERE TOP_MENU_TYPE = 'MAIN_HOME' ORDER BY MENU_SORT ASC";
						} else {
							sql += "SELECT * FROM S_SYSTEM_MENU WHERE BIND_FUNC_ID IN(";
							sql += "	  SELECT DISTINCT(FUNCID) FROM S_SYSTEM_ROLE_FUNC WHERE ROLEID IN(";
							sql += "		  SELECT ROLE_ID FROM S_SYSTEM_GROUP_ROLE WHERE GROUP_ID IN(";
							sql += "			  SELECT GROUP_ID FROM S_SYSTEM_GROUP_USER WHERE USER_ID IN(";
							sql += "				  SELECT ID AS USERID FROM S_SYSTEM_USER WHERE USER_NAME = '"
									+ loginUser.get("USER_NAME").toString() + "'";
							sql += "			  )";
							sql += "		  )";
							sql += "		  UNION ";
							sql += "		  SELECT ROLE_ID FROM S_SYSTEM_USER_ROLE WHERE USER_ID IN(";
							sql += "			  SELECT ID AS USERID FROM S_SYSTEM_USER WHERE USER_NAME = '"
									+ loginUser.get("USER_NAME").toString() + "'";
							sql += "		  )";
							sql += "	  )";
							sql += ") AND TOP_MENU_TYPE = 'MAIN_HOME' AND IS_USING = 'Y' ORDER BY MENU_SORT ASC";
						}
						homeMenus = jdbcTemplate.queryForList(sql);
						request.getSession().setAttribute("HOME_MENUS", homeMenus);
					}
					jsonObject.put("HOME_MENUS", homeMenus);
					if (loginUser.get("USER_NAME").toString().toLowerCase().indexOf("root") != -1) {
						jsonObject.put("IS_INCOMEABLE", true);
						jsonObject.put("IS_PAYOUTABLE", true);
					} else {
						/*
						 * 是否具备收入指标查看权限
						 * 
						 */
						String sql = "";
						sql += "SELECT COUNT(1) FROM(";
						sql += "	  SELECT A.* FROM (";
						sql += "		  SELECT * FROM S_SYSTEM_ROLE WHERE ID IN(";
						sql += "			  SELECT ROLE_ID FROM S_SYSTEM_USER_ROLE WHERE USER_ID IN(";
						sql += "				  SELECT ID AS USERID FROM S_SYSTEM_USER WHERE USER_NAME = '"
								+ loginUser.get("USER_NAME").toString() + "'";
						sql += "			  )";
						sql += "		  )";
						sql += "	  ) A WHERE A.ROLE_NAME = '门户-收入指标查看角色'";
						sql += ")";
						Integer isIncomeSee = jdbcTemplate.queryForInt(sql);
						if (isIncomeSee > 0) {
							jsonObject.put("IS_INCOMEABLE", true);
						} else {
							jsonObject.put("IS_INCOMEABLE", false);
						}
						/*
						 * 是否具备成本指标查看权限
						 * 
						 */
						sql = "";
						sql += "SELECT COUNT(1) FROM(";
						sql += "	  SELECT A.* FROM (";
						sql += "		  SELECT * FROM S_SYSTEM_ROLE WHERE ID IN(";
						sql += "			  SELECT ROLE_ID FROM S_SYSTEM_USER_ROLE WHERE USER_ID IN(";
						sql += "				  SELECT ID AS USERID FROM S_SYSTEM_USER WHERE USER_NAME = '"
								+ loginUser.get("USER_NAME").toString() + "'";
						sql += "			  )";
						sql += "		  )";
						sql += "	  ) A WHERE A.ROLE_NAME = '门户-成本指标查看角色'";
						sql += ")";
						Integer isPayoutSee = jdbcTemplate.queryForInt(sql);
						if (isPayoutSee > 0) {
							jsonObject.put("IS_PAYOUTABLE", true);
						} else {
							jsonObject.put("IS_PAYOUTABLE", false);
						}
					}
					/*
					 * 获取各项任务待办数量
					 * 
					 */
					FlowCountStub sub = new FlowCountStub();
					GetFlowCount flowObj = new GetFlowCount();/* 类中存在工单系统的URL. */
					for (int c = 0; c <= 3; c++) {
						flowObj.setIn0(c + "");
						flowObj.setIn1(loginUser.get("USER_NAME").toString());
						GetFlowCountResponse flowResponse = sub.getFlowCount(flowObj);
						String xmlOut = flowResponse.getOut();
						Document document = Jsoup.parse(xmlOut);
						Element stateElement = document.getElementsByTag("resultCode").get(0);
						Element countElement = document.getElementsByTag("msg").get(0);
						if (Integer.parseInt(stateElement.text()) == 0) {
							if (c == 0) {
								/* 待办 */
								jsonObject.put("WAITING_JOB_STATE", "Y");
								jsonObject.put("WAITING_JOB_COUNT", countElement.text());
							} else if (c == 1) {
								/* 抄送 */
								jsonObject.put("COPY_JOB_STATE", "Y");
								jsonObject.put("COPY_JOB_COUNT", countElement.text());
							} else if (c == 2) {
								/* 协办 */
								jsonObject.put("COOPER_JOB_STATE", "Y");
								jsonObject.put("COOPER_JOB_COUNT", countElement.text());
							} else if (c == 3) {
								/* 在办 */
								jsonObject.put("DOING_JOB_STATE", "Y");
								jsonObject.put("DOING_JOB_COUNT", countElement.text());
							}
						} else {
							if (c == 0) {
								/* 待办 */
								jsonObject.put("WAITING_JOB_STATE", "N");
							} else if (c == 1) {
								/* 抄送 */
								jsonObject.put("COPY_JOB_STATE", "N");
							} else if (c == 2) {
								/* 协办 */
								jsonObject.put("COOPER_JOB_STATE", "N");
							} else if (c == 3) {
								/* 在办 */
								jsonObject.put("DOING_JOB_STATE", "N");
							}
						}
					}
				}
				/*
				 * =================================== 首页
				 * ===================================
				 * 
				 */
				Map<String, Object> indexCount = new HashMap<String, Object>();
				String sql = "";
				/*
				 * CRM出账收入
				 * 
				 */
				sql = "";
				if (isProvince) {
					sql += "SELECT T.MONTH, T.CITY_NAME, T.CI_TAX_TOTAL ";
					sql += "FROM TOWERCRNOP.YF_OUT_BILLS_YEAR_HOME T ";
					sql += "WHERE T.MONTH IN (SELECT MAX(TO_NUMBER(MONTH)) FROM TOWERCRNOP.YF_OUT_BILLS_YEAR_HOME) AND T.CITY_NAME = '四川'";
				} else {
					sql += "SELECT A.* FROM (";
					sql += "	  SELECT T.MONTH, T.CITY_NAME, T.CI_TAX_TOTAL ";
					sql += "	  FROM TOWERCRNOP.YF_OUT_BILLS_YEAR_HOME T ";
					sql += "	  WHERE T.MONTH IN (SELECT MAX(TO_NUMBER(MONTH)) FROM TOWERCRNOP.YF_OUT_BILLS_YEAR_HOME) AND T.CITY_NAME <> '四川'";
					sql += ") A WHERE A.CITY_NAME LIKE '%" + belongArea + "%'";
				}
				List<Map<String, Object>> crmCounts = jdbcTemplate.queryForList(sql);
				if (crmCounts.size() > 0) {
					indexCount.put("CRM出账收入", crmCounts.get(0).get("CI_TAX_TOTAL") == null ? "0"
							: crmCounts.get(0).get("CI_TAX_TOTAL").toString());
				} else {
					indexCount.put("CRM出账收入", "0");
				}
				/*
				 * CRM新业务收入
				 * 
				 */
				sql = "";
				if (isProvince) {
					sql += "SELECT T.MONTH,T.CITY_NAME,T.CI_TAX_EXPAND ";
					sql += "FROM TOWERCRNOP.YF_OUT_BILLS_YEAR_HOME T ";
					sql += "WHERE T.MONTH IN (SELECT MAX(TO_NUMBER(MONTH)) FROM TOWERCRNOP.YF_OUT_BILLS_YEAR_HOME) AND T.CITY_NAME = '四川'";
				} else {
					sql += "SELECT A.* FROM (";
					sql += "	  SELECT T.MONTH,T.CITY_NAME,T.CI_TAX_EXPAND ";
					sql += "	  FROM TOWERCRNOP.YF_OUT_BILLS_YEAR_HOME T ";
					sql += "	  WHERE T.MONTH IN (SELECT MAX(TO_NUMBER(MONTH)) FROM TOWERCRNOP.YF_OUT_BILLS_YEAR_HOME) AND T.CITY_NAME <> '四川' ";
					sql += ") A WHERE A.CITY_NAME LIKE '%" + belongArea + "%'";
				}
				List<Map<String, Object>> crmNewBusinessCounts = jdbcTemplate.queryForList(sql);
				if (crmNewBusinessCounts.size() > 0) {
					indexCount.put("CRM出账新业务收入", crmNewBusinessCounts.get(0).get("CI_TAX_EXPAND") == null ? "0"
							: crmNewBusinessCounts.get(0).get("CI_TAX_EXPAND").toString());
				} else {
					indexCount.put("CRM出账新业务收入", "0");
				}
				/*
				 * 经营收入
				 * 
				 */
				sql = "";
				sql += "SELECT CASE ";
				sql += "	  WHEN T.COMPANY_NAME LIKE '%攀枝花%' THEN '攀枝花'";
				sql += "	  WHEN T.COMPANY_NAME LIKE '%四川省%' THEN '全省'";
				sql += "	  ELSE";
				sql += "		  SUBSTR(T.COMPANY_NAME, 16, 2)";
				sql += "	  END CITY,";
				sql += "	  	  T.OPERATING_INCOME_TOT / 10000 INCOME,";
				sql += "	  	  T.OPERATING_COST_TOT / 10000 COST,";
				sql += "	  	  T.CB_DEPRECIATE_AMORTIZAT_COST / 10000 AMORTIZAT,";
				sql += "	  	  T.CB_MAINTENANCE_REPAIR_COST / 10000 REPAIR,";
				sql += "	  	  T.CB_SITE_RENTALS / 10000 RENT_COST,";
				sql += "	  	  T.SR_NON_TELECOM_SERVE_INCOME / 10000 SERVE_INCOME ";
				sql += "FROM TOWERCRNOP.CW_PROFIT T ";
				sql += "WHERE ";
				sql += "	  T.COMPANY_NAME <> '单位名称：中国铁塔股份有限公司四川省分公司' AND T.SORT_DATE LIKE '%本年累计数' AND T.PUT_TIME IN (SELECT MAX(TO_NUMBER(A.PUT_TIME)) FROM TOWERCRNOP.CW_PROFIT A) ";
				if (!isProvince) {
					/*
					 * 如果不是省级用户，则区分地市.
					 * 
					 */
					sql += " AND T.COMPANY_NAME LIKE '%" + belongArea + "%'";
				} else {
					sql += " AND T.COMPANY_NAME LIKE '%四川省%'";
				}
				List<Map<String, Object>> jysrDatas = jdbcTemplate.queryForList(sql);
				if (jysrDatas.size() > 0) {
					indexCount.put("经营收入", jysrDatas.get(0).get("INCOME").toString());
					indexCount.put("运营成本", jysrDatas.get(0).get("COST").toString());
					indexCount.put("折旧摊销", jysrDatas.get(0).get("AMORTIZAT").toString());
					indexCount.put("维护成本", jysrDatas.get(0).get("REPAIR").toString());
					indexCount.put("场租成本", jysrDatas.get(0).get("RENT_COST") == null ? "0"
							: jysrDatas.get(0).get("RENT_COST").toString());
					indexCount.put("新业务收入", jysrDatas.get(0).get("SERVE_INCOME").toString());
				} else {
					indexCount.put("经营收入", "0");
					indexCount.put("运营成本", "0");
					indexCount.put("折旧摊销", "0");
					indexCount.put("维护成本", "0");
					indexCount.put("场租成本", "0");
					indexCount.put("新业务收入", "0");
				}
				jsonObject.put("INDEX_COUNT", indexCount);

				/*
				 * 单站收益排名TOP5
				 * 
				 */
				List<String> dzsyCities = new ArrayList<String>();
				List<Integer> dzsyDatas = new ArrayList<Integer>();
				sql = "";
				if (isProvince) {
					/*
					 * 如果是省级用户登录，则取地市单站受益排名前五位.
					 * 
					 */
					sql += "SELECT W.*,ROWNUM AS RN FROM(";
					sql += "	  SELECT B.CITY_NAME AS DIMENSION_NAME, A.A AS DIMENSION_VALUE FROM ( ";
					sql += "		  SELECT T.CITY_ID, ROUND(SUM(SS_GROSS_PROFIT) / SUM(TOI_CUMULATIVE_TOTAL),3) A ";
					sql += "		  FROM TOWERCRNOP.CW_SIG_TOWER_ACCOUNT T ";
					sql += "		  WHERE CITY_ID IS NOT NULL AND T.PUT_TIME IN( ";
					sql += "			  SELECT MAX(TO_NUMBER(PUT_TIME)) FROM TOWERCRNOP.CW_SIG_TOWER_ACCOUNT ";
					sql += "		  )GROUP BY CITY_ID ";
					sql += "	  ) A,( ";
					sql += "		  SELECT CITY_ID, CITY_NAME FROM TOWERCRNOP.RMS_CITY ";
					sql += "	  ) B WHERE A.CITY_ID = B.CITY_ID ORDER BY A DESC ";
					sql += ") W WHERE ROWNUM <= 5 ";
				} else {
					/*
					 * 如果是<地市>用户登录，则取<区县>单站受益排名前五位.
					 * 
					 */
					sql += "SELECT W.*,ROWNUM AS RN FROM( ";
					sql += "	  SELECT A.COUNTY_NAME AS DIMENSION_NAME,A.A AS DIMENSION_VALUE FROM ( ";
					sql += "		  SELECT T.COUNTY_NAME,ROUND(SUM(SS_GROSS_PROFIT) / SUM(TOI_CUMULATIVE_TOTAL),3) A ";
					sql += "	  	  FROM TOWERCRNOP.CW_SIG_TOWER_ACCOUNT T ";
					sql += "		  WHERE CITY_NAME LIKE '%" + belongArea + "%' AND T.PUT_TIME IN( ";
					sql += "		  	  SELECT MAX(TO_NUMBER(PUT_TIME)) FROM TOWERCRNOP.CW_SIG_TOWER_ACCOUNT ";
					sql += "		  ) GROUP BY COUNTY_NAME ";
					sql += "	  ) A  ORDER BY A DESC ";
					sql += ") W WHERE ROWNUM <= 5 ";
				}
				List<Map<String, Object>> dzsyCount = jdbcTemplate.queryForList(sql);
				if (dzsyCount.size() != 0) {
					for (int i = (dzsyCount.size() - 1); i >= 0; i--) {
						dzsyCities.add(dzsyCount.get(i).get("DIMENSION_NAME").toString());
						dzsyDatas.add(new Double(Double.parseDouble(dzsyCount.get(i).get("DIMENSION_VALUE") == null
								? "0.0" : dzsyCount.get(i).get("DIMENSION_VALUE").toString()) * 100.0).intValue());
					}
				}
				JSONObject dzsyChart = new JSONObject();
				dzsyChart.put("CITY_LIST", dzsyCities);
				dzsyChart.put("COUNT_LIST", dzsyDatas);
				jsonObject.put("DZSY_CHART", dzsyChart);

				/*
				 * 拆站数量TOP5：目前只有地市数据
				 * 
				 */
				List<String> cityNames = new ArrayList<String>();
				List<Integer> siteCount = new ArrayList<Integer>();
				sql = "";
				sql += "SELECT A.*,ROWNUM AS RN FROM(";
				sql += "	  SELECT DIMENSION_NAME,DIMENSION_VALUE FROM QUARTZ_INDEX_CHART WHERE DATA_TYPE = '拆站数量' AND COUNT_DIMENSION = '地市' ORDER BY DIMENSION_VALUE DESC";
				sql += ") A WHERE ROWNUM <= 5";
				if (!isProvince) {
					/*
					 * 如果不是省级用户，则区分地市.
					 * 
					 */
					sql += " AND DIMENSION_NAME LIKE '%" + belongArea + "%'";
				}
				List<Map<String, Object>> unFixSort = jdbcTemplate.queryForList(sql);
				if (unFixSort.size() > 0) {
					for (int i = (unFixSort.size() - 1); i >= 0; i--) {
						cityNames.add(unFixSort.get(i).get("DIMENSION_NAME").toString());
						siteCount.add(Integer.parseInt(unFixSort.get(i).get("DIMENSION_VALUE").toString()));
					}
				}
				JSONObject unFixChart = new JSONObject();
				unFixChart.put("CITY_LIST", cityNames);
				unFixChart.put("COUNT_LIST", siteCount);
				jsonObject.put("UN_FIX_CHART", unFixChart);

				/*
				 * 亏损站数量TOP5
				 * 
				 */
				List<String> cityIdNames = new ArrayList<String>();
				List<Integer> unEarnCount = new ArrayList<Integer>();
				sql = "";
				if (isProvince) {
					/*
					 * 如果是省级用户登录，则取地市单站受益排名前五位.
					 * 
					 */
					sql += "SELECT A.*,ROWNUM AS RN FROM(";
					sql += "	  SELECT substr(DIMENSION_NAME,0,2) AS DIMENSION_NAME,DIMENSION_VALUE FROM QUARTZ_INDEX_CHART WHERE DATA_TYPE = '亏损站数量' AND COUNT_DIMENSION = '地市' ORDER BY DIMENSION_VALUE DESC";
					sql += ") A WHERE ROWNUM <= 5";
				} else {
					/*
					 * 如果是<地市>用户登录，则取<区县>单站受益排名前五位.
					 * 
					 */
					sql += "SELECT A.DIMENSION_NAME,A.DIMENSION_VALUE,ROWNUM AS RN FROM(";
					sql += "	  SELECT * FROM QUARTZ_INDEX_CHART WHERE DATA_TYPE = '亏损站数量' AND COUNT_DIMENSION = '区县' AND DIMENSION_NAME IN(";
					sql += "		  SELECT DISTINCT(COUNTY_NAME) FROM " + resUserName1
							+ ".CW_SIG_TOWER_ACCOUNT WHERE COUNTY_NAME IS NOT NULL AND CITY_NAME LIKE '" + belongArea
							+ "%'";
					sql += "	  ) ORDER BY DIMENSION_VALUE DESC";
					sql += ") A WHERE ROWNUM <= 5";
				}
				List<Map<String, Object>> unEarnSort = jdbcTemplate.queryForList(sql);
				if (unEarnSort.size() > 0) {
					for (int i = (unEarnSort.size() - 1); i >= 0; i--) {
						cityIdNames.add(unEarnSort.get(i).get("DIMENSION_NAME").toString());
						unEarnCount.add(Integer.parseInt(unEarnSort.get(i).get("DIMENSION_VALUE").toString()));
					}
				}
				JSONObject unEarnChart = new JSONObject();
				unEarnChart.put("CITY_LIST", cityIdNames);
				unEarnChart.put("COUNT_LIST", unEarnCount);
				jsonObject.put("UN_EARN_CHART", unEarnChart);
				/*
				 * 收入与成本分析
				 * 
				 */
				sql = "";
				sql += "SELECT W.* FROM(";
				sql += "	  SELECT F.ID,A.PUT_TIME,F.CITY,A.INCOME,B.COST,C.AMORTIZAT,D.REPAIR,E.SITE,F.KS_NUM";
				sql += "	  FROM(";
				sql += "		  SELECT T.PUT_TIME, CASE WHEN T.COMPANY_NAME LIKE '%攀枝花%' THEN '攀枝花' WHEN T.COMPANY_NAME LIKE '%四川省%' THEN '全省' ELSE SUBSTR(T.COMPANY_NAME, 16, 2) END CITY,T.OPERATING_INCOME_TOT / 10000 INCOME ";
				sql += "		  FROM TOWERCRNOP.CW_PROFIT T";
				sql += "		  WHERE T.COMPANY_NAME <> '单位名称：中国铁塔股份有限公司四川省分公司' AND T.SORT_DATE LIKE '%本月发生数' AND T.PUT_TIME IN(";
				sql += "			  SELECT MAX(TO_NUMBER(A.PUT_TIME)) FROM TOWERCRNOP.CW_PROFIT A";
				sql += "		  )";
				sql += "	  ) A,(";
				sql += "		  SELECT CASE WHEN T.COMPANY_NAME LIKE '%攀枝花%' THEN '攀枝花' WHEN T.COMPANY_NAME LIKE '%四川省%' THEN '全省' ELSE SUBSTR(T.COMPANY_NAME, 16, 2) END CITY,T.OPERATING_COST_TOT / 10000 COST ";
				sql += "		  FROM TOWERCRNOP.CW_PROFIT T";
				sql += "		  WHERE T.COMPANY_NAME <> '单位名称：中国铁塔股份有限公司四川省分公司' AND T.SORT_DATE LIKE '%本月发生数' AND T.PUT_TIME IN(";
				sql += "			  SELECT MAX(TO_NUMBER(A.PUT_TIME)) FROM TOWERCRNOP.CW_PROFIT A";
				sql += "		  )";
				sql += "	  ) B,(";
				sql += "		  SELECT CASE WHEN T.COMPANY_NAME LIKE '%攀枝花%' THEN '攀枝花' WHEN T.COMPANY_NAME LIKE '%四川省%' THEN '全省' ELSE SUBSTR(T.COMPANY_NAME, 16, 2) END CITY,T.CB_DEPRECIATE_AMORTIZAT_COST / 10000 AMORTIZAT";
				sql += "		  FROM TOWERCRNOP.CW_PROFIT T ";
				sql += "		  WHERE T.COMPANY_NAME <> '单位名称：中国铁塔股份有限公司四川省分公司' AND T.SORT_DATE LIKE '%本月发生数' AND T.PUT_TIME IN(";
				sql += "			  SELECT MAX(TO_NUMBER(A.PUT_TIME)) FROM TOWERCRNOP.CW_PROFIT A";
				sql += "		  )";
				sql += "	  ) C,(";
				sql += "		  SELECT CASE WHEN T.COMPANY_NAME LIKE '%攀枝花%' THEN '攀枝花' WHEN T.COMPANY_NAME LIKE '%四川省%' THEN '全省' ELSE SUBSTR(T.COMPANY_NAME, 16, 2) END CITY,T.CB_MAINTENANCE_REPAIR_COST / 10000 REPAIR ";
				sql += "		  FROM TOWERCRNOP.CW_PROFIT T";
				sql += "		  WHERE T.COMPANY_NAME <> '单位名称：中国铁塔股份有限公司四川省分公司' AND T.SORT_DATE LIKE '%本月发生数' AND T.PUT_TIME IN(";
				sql += "			  SELECT MAX(TO_NUMBER(A.PUT_TIME)) FROM TOWERCRNOP.CW_PROFIT A";
				sql += "		  )";
				sql += "	  ) D,(";
				sql += "		  SELECT CASE WHEN T.COMPANY_NAME LIKE '%攀枝花%' THEN '攀枝花' WHEN T.COMPANY_NAME LIKE '%四川省%' THEN '全省' ELSE SUBSTR(T.COMPANY_NAME, 16, 2) END CITY,T.CB_SITE_RENTALS / 10000 SITE";
				sql += "		  FROM TOWERCRNOP.CW_PROFIT T";
				sql += "		  WHERE T.COMPANY_NAME <> '单位名称：中国铁塔股份有限公司四川省分公司' AND T.SORT_DATE LIKE '%本月发生数' AND T.PUT_TIME IN(";
				sql += "			  SELECT MAX(TO_NUMBER(A.PUT_TIME)) FROM TOWERCRNOP.CW_PROFIT A";
				sql += "		  )";
				sql += "	  ) E,(";
				sql += "		  SELECT '1' ID,S.CITY, COUNT(*) KS_NUM FROM(";
				sql += "			  SELECT CASE WHEN T.CITY_NAME LIKE '%攀枝花%' THEN '攀枝花' ELSE SUBSTR(T.CITY_NAME, 0, 2) END CITY,T.PUT_TIME";
				sql += "			  FROM TOWERCRNOP.CW_SIG_TOWER_ACCOUNT T";
				sql += "			  WHERE T.SS_PROFIT < 0 AND CITY_NAME IS NOT NULL AND T.PUT_TIME IN(";
				sql += "				  SELECT MAX(TO_NUMBER(A.PUT_TIME)) FROM TOWERCRNOP.CW_SIG_TOWER_ACCOUNT A";
				sql += "			  )";
				sql += "		  ) S GROUP BY S.CITY";
				sql += "		  UNION ALL ";
				sql += "		  SELECT '0' ID, '全省' CITY, SUM(KS_NUM) KS_NUM FROM (";
				sql += "			  SELECT S.CITY, COUNT(*) KS_NUM";
				sql += "			  FROM (";
				sql += "				  SELECT CASE WHEN T.CITY_NAME LIKE '%攀枝花%' THEN '攀枝花' ELSE SUBSTR(T.CITY_NAME, 0, 2) END CITY,T.PUT_TIME";
				sql += "				  FROM TOWERCRNOP.CW_SIG_TOWER_ACCOUNT T";
				sql += "				  WHERE T.SS_PROFIT < 0 AND CITY_NAME IS NOT NULL AND T.PUT_TIME IN(SELECT MAX(TO_NUMBER(A.PUT_TIME))";
				sql += "				  FROM TOWERCRNOP.CW_SIG_TOWER_ACCOUNT A";
				sql += "			  )";
				sql += "		  ) S GROUP BY S.CITY";
				sql += "	  )) F WHERE A.CITY = B.CITY(+) AND A.CITY = C.CITY(+) AND A.CITY = D.CITY(+) AND A.CITY = E.CITY(+) AND A.CITY = F.CITY(+) ORDER BY ID,CITY";
				sql += ") W";
				if (!isProvince) {
					sql += " WHERE W.CITY LIKE '%" + belongArea + "%'";
				}
				List<Map<String, Object>> countByCity = jdbcTemplate.queryForList(sql);
				jsonObject.put("COUNT_BY_CITY", countByCity);
				sql = "";
				sql += "SELECT MAX(TO_NUMBER(A.PUT_TIME)) AS INCOME_OUT_MAX_DATE FROM TOWERCRNOP.CW_PROFIT A";
				Map<String, Object> maxPutTimeQuery = jdbcTemplate.queryForMap(sql);
				jsonObject.put("COUNT_BY_CITY_MAX_DATE", maxPutTimeQuery.get("INCOME_OUT_MAX_DATE").toString());
				/*
				 * 收入与成本
				 * 
				 */
				List<String> monthNames = new ArrayList<String>();
				List<Integer> maxNumbers = new ArrayList<Integer>();
				List<Integer> minNumbers = new ArrayList<Integer>();
				for (int m = (date.getMonth() - 6); m <= (date.getMonth() - 1); m++) {// 1,12,11,10,9,8
					Integer currentMonth = m;// -5,-4,-3,-2,-1,0,1
					Integer fullYear = date.getYear() + 1900;
					if (currentMonth < 0) {
						currentMonth = 12 + (currentMonth) + 1;// 8,9,10,11,12
						fullYear = fullYear - 1;// 2017,2017,2017,2017,2017
					} else if (currentMonth == 0) {
						currentMonth = 1;// 1
					} else {
						currentMonth++;// 2
					}
					String monthDesign = fullYear + "年 " + (currentMonth < 10 ? "0" + currentMonth : currentMonth)
							+ "月";
					monthNames.add(monthDesign);
					sql = "";
					if (isProvince) {
						sql += "SELECT OPERATING_INCOME_TOT / 10000 INCOME,OPERATING_COST_TOT / 10000 PAY_OUT ";
						sql += "FROM TOWERCRNOP.CW_PROFIT ";
						sql += "WHERE SORT_DATE LIKE '" + monthDesign
								+ "%' AND SORT_DATE LIKE '%本月发生数' AND COMPANY_NAME LIKE '%四川省%' AND COMPANY_NAME LIKE '%合并%'";
					} else {
						sql += "SELECT OPERATING_INCOME_TOT / 10000 INCOME,OPERATING_COST_TOT / 10000 PAY_OUT ";
						sql += "FROM TOWERCRNOP.CW_PROFIT ";
						sql += "WHERE SORT_DATE LIKE '" + monthDesign
								+ "%' AND SORT_DATE LIKE '%本月发生数' AND COMPANY_NAME LIKE '%" + belongArea + "%'";
					}
					List<Map<String, Object>> countMaps = jdbcTemplate.queryForList(sql);
					if (countMaps.size() == 0) {
						maxNumbers.add(0);
						minNumbers.add(0);
					} else {
						Double incomeValue = Double.parseDouble(countMaps.get(0).get("INCOME") == null ? "0.0"
								: countMaps.get(0).get("INCOME").toString());
						maxNumbers.add(incomeValue.intValue());
						Double outValue = Double.parseDouble(countMaps.get(0).get("PAY_OUT") == null ? "0.0"
								: countMaps.get(0).get("PAY_OUT").toString());
						minNumbers.add(outValue.intValue());
					}
				}
				JSONObject monthlyChart = new JSONObject();
				monthlyChart.put("MONTH_LIST", monthNames);
				monthlyChart.put("MAX_NUMBER", maxNumbers);
				monthlyChart.put("MIN_NUMBER", minNumbers);
				jsonObject.put("MONTHLY_CHART", monthlyChart);
				if (request.getSession().getAttribute("HB_MONTHLY_CHART") == null) {
					request.getSession().setAttribute("HB_MONTHLY_CHART", monthlyChart);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			jsonObject = JSONObject.fromObject("{success:false}");
		} finally {
			if (request.getSession().getAttribute("INDEX_DATA") == null) {
				request.getSession().setAttribute("INDEX_DATA", jsonObject);
			}
			response.setContentType("application/json; charset=UTF-8");
			response.getWriter().print(jsonObject.toString());
		}
	}

	/*
	 * 区域标准化
	 * 
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping("/findRegionStandard.ilf")
	public void findRegionStandard(HttpServletRequest request, HttpServletResponse response) throws Exception {
		JSONObject jsonObject = JSONObject.fromObject("{success:true}");
		Boolean isProvince = false;
		String belongArea = "";
		try {
			if (request.getSession().getAttribute("REGION_STANDARD_DATA") != null) {
				jsonObject = (JSONObject) request.getSession().getAttribute("REGION_STANDARD_DATA");
			} else {
				Date date = new Date();
				String nowYearMonth = (date.getYear() + 1900) + ""
						+ (date.getMonth() < 10 ? ("0" + date.getMonth()) : date.getMonth());
				Object loginObject = request.getSession().getAttribute("LoginUserInfo");
				if (loginObject != null) {
					Map<String, Object> loginUser = (HashMap<String, Object>) loginObject;
					if (loginUser.get("BELONG_AREA").toString().indexOf("四川") != -1) {
						isProvince = true;
						belongArea = loginUser.get("BELONG_AREA").toString();
					} else {
						belongArea = loginUser.get("BELONG_AREA").toString();
					}
					if (belongArea.length() > 2) {
						belongArea = belongArea.substring(0, 2);
					}
				}
				/*
				 * 场租续签数
				 * 
				 */
				String sql = "";
				// sql+="SELECT SUM(F.CITY_COUNT) AS MY_VIEW_COUNT FROM(";
				// sql+=" SELECT S.CITY, COUNT(*) AS CITY_COUNT FROM (";
				// sql+=" SELECT CASE WHEN T.CITY_COMPANY LIKE '%攀枝花%' THEN
				// '攀枝花' WHEN T.CITY_COMPANY LIKE '%四川省%' THEN SUBSTR(T.CITY, 0,
				// 2) ELSE SUBSTR(T.CITY_COMPANY, 11, 2) END CITY";
				// sql+=" FROM RMW.WY_CONTRACT_INFO T";
				// sql+=" WHERE T.CITY_COMPANY IS NOT NULL AND
				// TO_DATE(T.TERMINATION,'YYYY-MM-DD') <= SYSDATE+180 AND
				// TO_DATE(T.TERMINATION,'YYYY-MM-DD')>=SYSDATE AND
				// T.CONTRACT_STATUS = '有效'";
				// sql+=" ) S GROUP BY S.CITY";
				// sql+=") F ";
				sql += "SELECT * FROM TOWERCRNOP.WY_CONTRACT_INFO T ";
				sql += "WHERE T.CITY_COMPANY IS NOT NULL AND TO_DATE(T.TERMINATION, 'YYYY-MM-DD') <= SYSDATE + 180 AND TO_DATE(T.TERMINATION, 'YYYY-MM-DD') >= SYSDATE AND T.CONTRACT_STATUS = '有效' ";
				if (!isProvince) {
					sql += " AND T.CITY_COMPANY LIKE '%" + belongArea + "%'";
				}
				Integer czxqsInt = jdbcTemplate.queryForInt("SELECT COUNT(*) FROM (" + sql + ")");
				jsonObject.put("RENT_CONTINUE", czxqsInt);
				/*
				 * 已确认电费数
				 * 
				 */
				sql = "";
				sql += "SELECT SUM(F.CITY_COUNT) AS MY_VIEW_COUNT FROM(";
				sql += "	  SELECT CITY, COUNT(*) AS CITY_COUNT FROM (";
				sql += "		  SELECT T.CITY,T.SITE_CODE,SUBSTR(T.ELECTRI_FEE,0,4) || SUBSTR(T.ELECTRI_FEE,6,2) MONTH FROM RMW.WY_ELECTRI_FEE_DS T WHERE T.MOVE_PAYQR = 0 AND T.TELECOM_PAYQR = 0 AND T.UNICOM_PAYQR = 0 AND T.STATEFLAG = 1";
				sql += "		  UNION ALL ";
				sql += "		  SELECT T.CITY,T.SITE_CODE,SUBSTR(T.ELECTRI_FEE,0,4) || SUBSTR(T.ELECTRI_FEE,6,2) MONTH FROM RMW.WY_ELECTRI_FEE_TURN T WHERE T.MOVE_PAYQR = 0 AND T.TELECOM_PAYQR = 0 AND T.UNICOM_PAYQR = 0 AND T.STATEFLAG = 1";
				sql += "		  UNION ALL ";
				sql += "		  SELECT T.CITY,T.SITE_CODE,SUBSTR(T.ELECTRI_FEE,0,4) || SUBSTR(T.ELECTRI_FEE,6,2) MONTH FROM RMW.WY_ELECTRI_FEE_YFHX T WHERE T.MOVE_PAYQR = 0 AND T.TELECOM_PAYQR = 0 AND T.UNICOM_PAYQR = 0 AND T.STATEFLAG = 1";
				sql += "	  ) S WHERE S.MONTH IN (";
				sql += "		  SELECT MAX(TO_NUMBER(R.MONTH)) FROM ((";
				sql += "			  SELECT SUBSTR(T.ELECTRI_FEE,0,4) || SUBSTR(T.ELECTRI_FEE, 6, 2) MONTH FROM RMW.WY_ELECTRI_FEE_DS T WHERE T.MOVE_PAYQR = 0 AND T.TELECOM_PAYQR = 0 AND T.UNICOM_PAYQR = 0 AND T.STATEFLAG = 1";
				sql += "			  UNION ALL ";
				sql += "		  	  SELECT SUBSTR(T.ELECTRI_FEE,0,4) || SUBSTR(T.ELECTRI_FEE, 6, 2) MONTH FROM RMW.WY_ELECTRI_FEE_TURN T WHERE T.MOVE_PAYQR = 0 AND T.TELECOM_PAYQR = 0 AND T.UNICOM_PAYQR = 0 AND T.STATEFLAG = 1";
				sql += "			  UNION ALL ";
				sql += "			  SELECT SUBSTR(T.ELECTRI_FEE,0,4) || SUBSTR(T.ELECTRI_FEE, 6, 2) MONTH FROM RMW.WY_ELECTRI_FEE_YFHX T WHERE T.MOVE_PAYQR = 0 AND T.TELECOM_PAYQR = 0 AND T.UNICOM_PAYQR = 0 AND T.STATEFLAG = 1";
				sql += "	  )) R) GROUP BY S.CITY";
				sql += ") F";
				if (!isProvince) {
					sql += " WHERE F.CITY LIKE '%" + belongArea + "%'";
				}
				Integer dfjnsInt = jdbcTemplate.queryForInt(sql);
				jsonObject.put("ELEC_MONEY_SUBMIT", dfjnsInt);
				/*
				 * 巡检站数
				 * 
				 */
				sql = "";
				if (isProvince) {
					sql += "SELECT COUNT(*) FROM(";
					sql += "	  SELECT DISTINCT T.CITY,T.REGION_ID,T.SITE_CODE,T.SITE_NAME FROM " + resUserName2
							+ ".WH_BASIC_INSPECT T WHERE T.CITY IS NOT NULL AND T.REGION_ID IS NOT NULL";
					sql += ")";
				} else {
					sql += "SELECT COUNT(*) FROM(";
					sql += "	  SELECT DISTINCT T.CITY,T.REGION_ID,T.SITE_CODE,T.SITE_NAME FROM " + resUserName2
							+ ".WH_BASIC_INSPECT T WHERE T.CITY IS NOT NULL AND T.REGION_ID IS NOT NULL AND T.CITY LIKE '%"
							+ belongArea + "%'";
					sql += ")";
				}
				Integer xjzsInt = jdbcTemplate.queryForInt(sql);
				jsonObject.put("XJZS_COUNT", xjzsInt);
				/*
				 * 活动告警数
				 * 
				 */
				sql = "";
				if (isProvince) {
					sql += "SELECT COUNT(*) AS TOTAL_COUNT FROM TOWERCRNOP.YWJK_ACTIVE_ALARM WHERE ALARM_LEVEL IS NOT NULL ";
				} else {
					sql += "SELECT COUNT(*) AS TOTAL_COUNT FROM TOWERCRNOP.YWJK_ACTIVE_ALARM WHERE ALARM_LEVEL IS NOT NULL AND CITY_NAME LIKE '%"
							+ belongArea + "%'";
				}
				jsonObject.put("ACTIVITY_ALARM_NUMBER", jdbcTemplate.queryForInt(sql));

				/*
				 * 客户问题数
				 * 
				 */
				sql = "SELECT COUNT(*) FROM " + resUserName3 + ".T_CUSTOMER_PROBLEM_INFO WHERE CITYID IS NOT NULL ";
				if (!isProvince) {
					sql += " AND CITYID LIKE '%" + belongArea + "%'";
				}
				Integer khwtsInt = jdbcTemplate.queryForInt(sql);
				jsonObject.put("CLIENT_PROBLEM_NUMBER", khwtsInt);
				/*
				 * 各地市站址规模
				 * 
				 */
				sql = "SELECT CITY,COUNT(*) AS SITE_SIZE FROM " + resUserName2
						+ ".ZG_SITE WHERE STATEFLAG = '0' GROUP BY CITY";
				List<Map<String, Object>> sitesSize = jdbcTemplate.queryForList(sql);
				jsonObject.put("SITE_SIZE", sitesSize);
				request.getSession().setAttribute("REGION_STANDARD_DATA", jsonObject);
			}
		} catch (Exception e) {
			e.printStackTrace();
			jsonObject = JSONObject.fromObject("{success:false}");
		} finally {
			response.setContentType("application/json; charset=UTF-8");
			response.getWriter().print(jsonObject.toString());
		}
	}

	/*
	 * 新业务链接
	 * 
	 */
	@RequestMapping("/findNewBusinessUrl.ilf")
	public void findNewBusinessUrl(@RequestParam String keyName, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		JSONObject jsonObject = JSONObject.fromObject("{success:true}");
		try {
			jsonObject
					.put("PROPERTY_URL",
							jdbcTemplate
									.queryForMap(
											"SELECT * FROM S_SYSTEM_PROPERTY WHERE PROPERTY_NAME = '" + keyName + "'")
									.get("PRO_VALUE").toString());
		} catch (Exception e) {
			e.printStackTrace();
			jsonObject = JSONObject.fromObject("{success:false}");
		} finally {
			response.setContentType("application/json; charset=UTF-8");
			response.getWriter().print(jsonObject.toString());
		}
	}

	/*
	 * 工单系统链接
	 * 
	 */
	@RequestMapping("/findGdHttpLink.ilf")
	public void findGdHttpLink(@RequestParam String GD_DATA_TYPE, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		JSONObject jsonObject = JSONObject.fromObject("{SUCCESS:true}");
		try {
			List<Map<String, Object>> links = jdbcTemplate.queryForList(
					"SELECT * FROM S_SYSTEM_PROPERTY WHERE PROPERTY_NAME = 'GD_LINK_HTTP' AND PRO_DESC = '"
							+ GD_DATA_TYPE + "'");
			if (links.size() > 0 && links.get(0).get("PRO_VALUE") != null) {
				jsonObject.put("MENU_URL", links.get(0).get("PRO_VALUE").toString());
			} else {
				jsonObject.put("MENU_URL", "");
			}
		} catch (Exception e) {
			e.printStackTrace();
			jsonObject = JSONObject.fromObject("{SUCCESS:false}");
		} finally {
			response.setContentType("application/json; charset=UTF-8");
			response.getWriter().print(jsonObject.toString());
		}
	}

	/*
	 * 新业务系统
	 * 
	 */
	@RequestMapping("/findBusinessUrlInJsp.ilf")
	public void findBusinessUrlInJsp(HttpServletRequest request, HttpServletResponse response) throws Exception {
		JSONObject jsonObject = JSONObject.fromObject("{SUCCESS:true}");
		try {
			jsonObject.put("BUSINESS_URL",
					jdbcTemplate.queryForList("SELECT * FROM S_SYSTEM_NEW_BUSINESS_LINK WHERE IS_USING = 'Y'"));
		} catch (Exception e) {
			e.printStackTrace();
			jsonObject = JSONObject.fromObject("{SUCCESS:false}");
		} finally {
			response.setContentType("application/json; charset=UTF-8");
			response.getWriter().print(jsonObject.toString());
		}
	}

	/*
	 * 建设项目辅助管理
	 * 
	 */
	@RequestMapping("/helpProjectUrls.ilf")
	public void helpProjectUrls(@RequestParam String codePool, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		JSONObject jsonObject = JSONObject.fromObject("{success:true}");
		try {
			jsonObject.put("LOGIN_USER", loginUserUtil.getUserAccount(request));
			jsonObject.put("MENU_URLS",
					jdbcTemplate.queryForList("SELECT * FROM S_SYSTEM_LINK WHERE ID IN(" + codePool + ")"));
		} catch (Exception e) {
			e.printStackTrace();
			jsonObject = JSONObject.fromObject("{success:false}");
		} finally {
			response.setContentType("application/json; charset=UTF-8");
			response.getWriter().print(jsonObject.toString());
		}
	}

	/*
	 * 获取用户登录信息
	 * 
	 */
	@RequestMapping("/findLoginInfo.ilf")
	public void findLoginInfo(HttpServletRequest request, HttpServletResponse response) throws Exception {
		JSONObject jsonObject = JSONObject.fromObject("{success:true}");
		try {
			Object loginObject = request.getSession().getAttribute("LoginUserInfo");
			if (loginObject != null) {
				Map<String, Object> loginUser = (HashMap<String, Object>) loginObject;
				if (loginUser.get("BELONG_AREA").toString().indexOf("四川") != -1
						|| loginUser.get("BELONG_AREA").toString().indexOf("省") != -1) {
					loginUser.put("IS_PROVINCE", true);
				} else if (loginUser.get("USER_NAME").toString().toUpperCase().indexOf("ROOT") != -1) {
					loginUser.put("IS_PROVINCE", true);
				} else {
					loginUser.put("IS_PROVINCE", false);
				}
				jsonObject.put("LOGIN_USER", loginUser);
			}
		} catch (Exception e) {
			e.printStackTrace();
			jsonObject = JSONObject.fromObject("{success:false}");
		} finally {
			if (request.getSession().getAttribute("REGION_STANDARD") == null) {
				request.getSession().setAttribute("REGION_STANDARD", jsonObject);
			}
			response.setContentType("application/json; charset=UTF-8");
			response.getWriter().print(jsonObject.toString());
		}
	}

	/*
	 * ===================================== 单击地图刷新右侧四个报表数据
	 * =====================================
	 * 
	 */
	/*
	 * 根据地市获取其下属区县单站收益排名TOP5
	 * 
	 */
	@RequestMapping("/findSingleEarnSort.ilf")
	public void findSingleEarnSort(@RequestParam String CITY_NAME, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		JSONObject jsonObject = JSONObject.fromObject("{SUCCESS:true}");
		try {
			/*
			 * 单站收益排名TOP5
			 * 
			 */
			List<String> dzsyCities = new ArrayList<String>();
			List<Integer> dzsyDatas = new ArrayList<Integer>();
			String sql = "";
			sql += "SELECT A.DIMENSION_NAME,A.DIMENSION_VALUE,ROWNUM AS RN FROM(";
			sql += "	  SELECT * FROM QUARTZ_INDEX_CHART WHERE DATA_TYPE = '单站收益排名' AND COUNT_DIMENSION = '区县' AND DIMENSION_NAME IN(";
			sql += "		  SELECT DISTINCT(COUNTY_NAME) FROM " + resUserName1
					+ ".CW_SIG_TOWER_ACCOUNT WHERE COUNTY_NAME IS NOT NULL AND CITY_NAME LIKE '%" + CITY_NAME + "%'";
			sql += "	  ) ORDER BY DIMENSION_VALUE DESC";
			sql += ") A WHERE ROWNUM <= 5";
			List<Map<String, Object>> dzsyCount = jdbcTemplate.queryForList(sql);
			if (dzsyCount.size() != 0) {
				for (int i = (dzsyCount.size() - 1); i >= 0; i--) {
					dzsyCities.add(dzsyCount.get(i).get("DIMENSION_NAME").toString());
					dzsyDatas.add(Integer.parseInt(dzsyCount.get(i).get("DIMENSION_VALUE").toString()));
				}
			}
			JSONObject dzsyChart = new JSONObject();
			dzsyChart.put("CITY_LIST", dzsyCities);
			dzsyChart.put("COUNT_LIST", dzsyDatas);
			jsonObject.put("DZSY_CHART", dzsyChart);
		} catch (Exception e) {
			e.printStackTrace();
			jsonObject = JSONObject.fromObject("{SUCCESS:false}");
		} finally {
			response.setContentType("application/json; charset=UTF-8");
			response.getWriter().print(jsonObject.toString());
		}
	}

	/*
	 * 根据地市获取其下属区县亏损站数量排名TOP5
	 * 
	 */
	@RequestMapping("/findSinglePayloseSort.ilf")
	public void findSinglePayloseSort(@RequestParam String CITY_NAME, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		JSONObject jsonObject = JSONObject.fromObject("{SUCCESS:true}");
		try {
			/*
			 * 亏损站数量排名TOP5
			 * 
			 */
			List<String> cityIdNames = new ArrayList<String>();
			List<Integer> unEarnCount = new ArrayList<Integer>();
			String sql = "";
			sql += "SELECT A.DIMENSION_NAME,A.DIMENSION_VALUE,ROWNUM AS RN FROM(";
			sql += "	  SELECT * FROM QUARTZ_INDEX_CHART WHERE DATA_TYPE = '亏损站数量' AND COUNT_DIMENSION = '区县' AND DIMENSION_NAME IN(";
			sql += "		  SELECT DISTINCT(COUNTY_NAME) FROM " + resUserName1
					+ ".CW_SIG_TOWER_ACCOUNT WHERE COUNTY_NAME IS NOT NULL AND CITY_NAME LIKE '%" + CITY_NAME + "%'";
			sql += "	  ) ORDER BY DIMENSION_VALUE DESC";
			sql += ") A WHERE ROWNUM <= 5";
			List<Map<String, Object>> unEarnSort = jdbcTemplate.queryForList(sql);
			if (unEarnSort.size() > 0) {
				for (int i = (unEarnSort.size() - 1); i >= 0; i--) {
					cityIdNames.add(unEarnSort.get(i).get("DIMENSION_NAME").toString());
					unEarnCount.add(Integer.parseInt(unEarnSort.get(i).get("DIMENSION_VALUE").toString()));
				}
			}
			JSONObject unEarnChart = new JSONObject();
			unEarnChart.put("CITY_LIST", cityIdNames);
			unEarnChart.put("COUNT_LIST", unEarnCount);
			jsonObject.put("UN_EARN_CHART", unEarnChart);
		} catch (Exception e) {
			e.printStackTrace();
			jsonObject = JSONObject.fromObject("{SUCCESS:false}");
		} finally {
			response.setContentType("application/json; charset=UTF-8");
			response.getWriter().print(jsonObject.toString());
		}
	}

	/*
	 * 根据地市获取其下属区县（拆站）数量排名TOP5
	 * 
	 */
	@RequestMapping("/findUnfixSort.ilf")
	public void findUnfixSort(@RequestParam String CITY_NAME, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		JSONObject jsonObject = JSONObject.fromObject("{SUCCESS:true}");
		try {
			List<String> cityNames = new ArrayList<String>();
			List<Integer> siteCount = new ArrayList<Integer>();
			String sql = "";
			sql = "";
			sql += "SELECT A.*,ROWNUM AS RN FROM(";
			sql += "	  SELECT DIMENSION_NAME,DIMENSION_VALUE FROM QUARTZ_INDEX_CHART WHERE DATA_TYPE = '拆站数量' AND COUNT_DIMENSION = '地市' ORDER BY DIMENSION_VALUE DESC";
			sql += ") A WHERE ROWNUM <= 5 AND DIMENSION_NAME LIKE '%" + CITY_NAME + "%'";
			List<Map<String, Object>> unFixSort = jdbcTemplate.queryForList(sql);
			if (unFixSort.size() > 0) {
				for (int i = (unFixSort.size() - 1); i >= 0; i--) {
					cityNames.add(unFixSort.get(i).get("DIMENSION_NAME").toString());
					siteCount.add(Integer.parseInt(unFixSort.get(i).get("DIMENSION_VALUE").toString()));
				}
			}
			JSONObject unFixChart = new JSONObject();
			unFixChart.put("CITY_LIST", cityNames);
			unFixChart.put("COUNT_LIST", siteCount);
			jsonObject.put("UN_FIX_CHART", unFixChart);
		} catch (Exception e) {
			e.printStackTrace();
			jsonObject = JSONObject.fromObject("{SUCCESS:false}");
		} finally {
			response.setContentType("application/json; charset=UTF-8");
			response.getWriter().print(jsonObject.toString());
		}
	}

	/*
	 * 根据地市（收入与成本）
	 * 
	 */
	@RequestMapping("/earnAndPayForCity.ilf")
	public void earnAndPayForCity(@RequestParam String CITY_NAME, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		JSONObject jsonObject = JSONObject.fromObject("{SUCCESS:true}");
		Date date = new Date();
		try {
			List<String> monthNames = new ArrayList<String>();
			List<Integer> maxNumbers = new ArrayList<Integer>();
			List<Integer> minNumbers = new ArrayList<Integer>();
			for (int m = (date.getMonth() - 5); m <= date.getMonth(); m++) {
				String monthDesign = (date.getYear() + 1900) + "" + (m < 10 ? "0" + m : m);
				monthNames.add(monthDesign);
				String sql = "SELECT * FROM QUARTZ_IN_OUT_CHART WHERE DIMENSION_VALUE LIKE '%" + CITY_NAME
						+ "%' AND COUNT_DATE = '" + monthDesign + "'";
				List<Map<String, Object>> countMaps = jdbcTemplate.queryForList(sql);
				if (countMaps.size() == 0) {
					maxNumbers.add(0);
					minNumbers.add(0);
				} else {
					maxNumbers.add(Integer.parseInt(
							countMaps.get(0).get("ALL_IN") == null ? "0" : countMaps.get(0).get("ALL_IN").toString()));
					minNumbers.add(Integer.parseInt(countMaps.get(0).get("ALL_OUT") == null ? "0"
							: countMaps.get(0).get("ALL_OUT").toString()));
				}
			}
			JSONObject monthlyChart = new JSONObject();
			monthlyChart.put("MONTH_LIST", monthNames);
			monthlyChart.put("MAX_NUMBER", maxNumbers);
			monthlyChart.put("MIN_NUMBER", minNumbers);
			jsonObject.put("MONTHLY_CHART", monthlyChart);
		} catch (Exception e) {
			e.printStackTrace();
			jsonObject = JSONObject.fromObject("{SUCCESS:false}");
		} finally {
			response.setContentType("application/json; charset=UTF-8");
			response.getWriter().print(jsonObject.toString());
		}
	}

	/*
	 * 数据中心首页.
	 * 
	 */
	@RequestMapping("/dataCenterPage.ilf")
	public void dataCenterPage(@RequestParam String cityName, @RequestParam String thisMonth,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		JSONObject jsonObject = JSONObject.fromObject("{SUCCESS:true}");
		Date date = new Date();
		try {
			/*
			 * 地市信息
			 * 
			 */
			jsonObject.put("IS_PROVINCE", loginUserUtil.isProvince(request));
			jsonObject.put("MY_CITY",
					(loginUserUtil.isProvince(request) ? "全省" : loginUserUtil.getBelongArea(request)));
			String sql = "SELECT DISTINCT(CITY_NAME) FROM COMPARE_SUMMARY2";
			if (!loginUserUtil.isProvince(request)) {
				String CITY_NAME = loginUserUtil.getBelongArea(request);
				if (CITY_NAME != null && CITY_NAME.length() > 2) {
					CITY_NAME = CITY_NAME.substring(0, 2);
				}
				sql += " WHERE CITY_NAME LIKE '%" + CITY_NAME + "%'";
			}

			List<Map<String, Object>> cityList = jdbcTemplate.queryForList(sql);

			// 调整数据的排列
			List<Map<String, Object>> sortList = new ArrayList<Map<String, Object>>();
			if (cityList != null && cityList.size() > 1) {
				Map<String, Object> map1 = new HashMap<String, Object>();
				map1.put("CITY_NAME", "--请选择--");
				sortList.add(map1);// 第一个值

				Map<String, Object> map2 = new HashMap<String, Object>();
				map2.put("CITY_NAME", "全省");
				sortList.add(map2);// 第二个值

				for (Map<String, Object> map : cityList) {
					if (!"全省".equals(map.get("CITY_NAME")) && !"--".equals(map.get("CITY_NAME"))
							&& !"--请选择--".equals(map.get("CITY_NAME")) && !"四川".equals(map.get("CITY_NAME"))) {
						sortList.add(map);
					}
				}

			} else {
				sortList = cityList;// 单个数据无需排序（地市账号）
			}

			jsonObject.put("CITY_LIST", sortList);
			/*
			 * SC比对数据.
			 * 
			 */
			String dateNow = thisMonth;
			if ("-1".equals(dateNow) || "".equals(dateNow)) {
				dateNow = (date.getYear() + 1900) + "-"
						+ ((date.getMonth() + 1) < 10 ? "0" + (date.getMonth() + 1) : (date.getMonth() + 1)) + "-"
						+ (date.getDate() < 10 ? "0" + date.getDate() : date.getDate());
			}
			String belongCity = "全省";
			if (!loginUserUtil.isProvince(request)) {
				belongCity = loginUserUtil.getBelongArea(request);
			} else {
				if ("-1".equals(cityName) || "".equals(cityName)) {
					belongCity = "全省";
				} else {
					belongCity = cityName;
				}
			}
			sql = "";
			sql += "SELECT A.ID 序号,A.DATA 运营数据,A.S S标准数据来源系统,A.C1 稽核系统,A.C2 统一业务接入平台,A.C3 CRM,A.C4 规划选址,A.C5 PMS,A.C6 资源系统,A.C7 运维监控,A.C8 物业系统,A.C9 商务平台,A.C10 合同系统,A.C11 财务收入成本,A.C12 财务资产卡片 ";
			sql += "FROM COMPARE_SUMMARY2 A ";
			sql += "WHERE A.CITY_NAME LIKE '%" + belongCity + "%' AND A.MONTH = '" + dateNow
					+ "' ORDER BY TO_NUMBER(A.ID) ASC";
			List<Map<String, Object>> scCount = jdbcTemplate.queryForList(sql);
			jsonObject.put("SC_COUNT", scCount);
		} catch (Exception e) {
			e.printStackTrace();
			jsonObject = JSONObject.fromObject("{SUCCESS:false}");
		} finally {
			response.setContentType("application/json; charset=UTF-8");
			response.getWriter().print(jsonObject.toString());
		}
	}

	/*
	 * 同比（第二个）.
	 * 
	 */
	@RequestMapping("/findTbChartData.ilf")
	public void findTbChartData(HttpServletRequest request, HttpServletResponse response) throws Exception {
		JSONObject jsonObject = JSONObject.fromObject("{SUCCESS:true}");
		List<String> monthNames = new ArrayList<String>();
		List<Integer> maxNumbers = new ArrayList<Integer>();
		List<Integer> minNumbers = new ArrayList<Integer>();
		try {
			Boolean IS_PROVINCE = loginUserUtil.isProvince(request);
			String CITY_NAME = "";
			if (!IS_PROVINCE) {
				CITY_NAME = loginUserUtil.getBelongArea(request);
			} else {
				CITY_NAME = "全省";
			}
			if (request.getSession().getAttribute("TB_MONTHLY_CHART") != null) {
				jsonObject.put("MONTHLY_CHART", request.getSession().getAttribute("TB_MONTHLY_CHART"));
			} else {
				Date nowDate = new Date();
				Integer preMonthOfYear = nowDate.getMonth();
				for (int i = (preMonthOfYear - 5); i <= preMonthOfYear; i++) {
					Integer carMonth = i;
					Integer carYears = nowDate.getYear() + 1900;
					if (i < 0) {
						carMonth = 12 + i + 1;
						carYears = carYears - 1;
					} else if (i == 0) {
						carMonth = 12;
						carYears = carYears - 1;
					}
					String monthOfYearStr = carMonth < 10 ? "0" + carMonth : carMonth + "";
					String nowDateFormate = carYears + "" + monthOfYearStr;
					/*
					 * 月份.
					 * 
					 */
					monthNames.add(nowDateFormate);
					/*
					 * 收入同比.
					 * 
					 */
					String aNumberStr = "0.0";
					String inComeSql1 = "";
					inComeSql1 += "SELECT C.* FROM (";
					inComeSql1 += "	SELECT A.PUT_TIME, A.CITY, A.COST - B.COST AS IN_TB_VALUE";
					inComeSql1 += "	FROM (";
					inComeSql1 += "		SELECT ";
					inComeSql1 += "			T.PUT_TIME, CASE WHEN T.COMPANY_NAME LIKE '%攀枝花%' THEN '攀枝花' WHEN T.COMPANY_NAME LIKE '%四川省%' THEN '全省' ELSE SUBSTR(T.COMPANY_NAME, 16, 2) END CITY,";
					inComeSql1 += "			T.OPERATING_INCOME_TOT / 10000 COST ";
					inComeSql1 += "		FROM TOWERCRNOP.CW_PROFIT T WHERE T.COMPANY_NAME <> '单位名称：中国铁塔股份有限公司四川省分公司' AND T.SORT_DATE LIKE '%本月发生数') A,(";
					inComeSql1 += "			SELECT T.PUT_TIME,CASE WHEN T.COMPANY_NAME LIKE '%攀枝花%' THEN '攀枝花' WHEN T.COMPANY_NAME LIKE '%四川省%' THEN '全省' ELSE SUBSTR(T.COMPANY_NAME, 16, 2) END CITY,T.OPERATING_INCOME_TOT / 10000 COST";
					inComeSql1 += "			FROM TOWERCRNOP.CW_PROFIT T ";
					inComeSql1 += "			WHERE T.COMPANY_NAME <> '单位名称：中国铁塔股份有限公司四川省分公司' AND T.SORT_DATE LIKE '%上年同期数') B";
					inComeSql1 += "		WHERE A.PUT_TIME = B.PUT_TIME AND A.CITY = B.CITY";
					inComeSql1 += ") C WHERE C.CITY LIKE '%" + CITY_NAME + "%' AND PUT_TIME = " + nowDateFormate;
					List<Map<String, Object>> inComeNow = jdbcTemplate.queryForList(inComeSql1);
					if (inComeNow.size() > 0 && inComeNow.get(0).get("IN_TB_VALUE") != null) {
						aNumberStr = inComeNow.get(0).get("IN_TB_VALUE").toString();
					}
					Double incomeTbDouble = Double.parseDouble(aNumberStr);
					maxNumbers.add(incomeTbDouble.intValue());
					/*
					 * 支出同比.
					 * 
					 */
					String cNumberStr = "0.0";
					String outComeSql1 = "";
					outComeSql1 += "SELECT C.* FROM (";
					outComeSql1 += "	SELECT A.PUT_TIME, A.CITY, A.COST - B.COST AS OUT_TB_VALUE";
					outComeSql1 += "	FROM (";
					outComeSql1 += "		SELECT ";
					outComeSql1 += "			T.PUT_TIME, CASE WHEN T.COMPANY_NAME LIKE '%攀枝花%' THEN '攀枝花' WHEN T.COMPANY_NAME LIKE '%四川省%' THEN '全省' ELSE SUBSTR(T.COMPANY_NAME, 16, 2) END CITY,";
					outComeSql1 += "			T.OPERATING_COST_TOT / 10000 COST ";
					outComeSql1 += "		FROM TOWERCRNOP.CW_PROFIT T WHERE T.COMPANY_NAME <> '单位名称：中国铁塔股份有限公司四川省分公司' AND T.SORT_DATE LIKE '%本月发生数') A,(";
					outComeSql1 += "			SELECT T.PUT_TIME,CASE WHEN T.COMPANY_NAME LIKE '%攀枝花%' THEN '攀枝花' WHEN T.COMPANY_NAME LIKE '%四川省%' THEN '全省' ELSE SUBSTR(T.COMPANY_NAME, 16, 2) END CITY,T.OPERATING_COST_TOT / 10000 COST";
					outComeSql1 += "			FROM TOWERCRNOP.CW_PROFIT T ";
					outComeSql1 += "			WHERE T.COMPANY_NAME <> '单位名称：中国铁塔股份有限公司四川省分公司' AND T.SORT_DATE LIKE '%上年同期数') B";
					outComeSql1 += "		WHERE A.PUT_TIME = B.PUT_TIME AND A.CITY = B.CITY";
					outComeSql1 += ") C WHERE C.CITY LIKE '%" + CITY_NAME + "%' AND PUT_TIME = " + nowDateFormate;
					List<Map<String, Object>> outComeCount = jdbcTemplate.queryForList(outComeSql1);
					if (outComeCount.size() > 0 && outComeCount.get(0).get("OUT_TB_VALUE") != null) {
						cNumberStr = outComeCount.get(0).get("OUT_TB_VALUE").toString();
					}
					Double outcomeTbDouble = Double.parseDouble(cNumberStr);
					minNumbers.add(outcomeTbDouble.intValue());
				}
				JSONObject monthlyChart = new JSONObject();
				monthlyChart.put("MONTH_LIST", monthNames);
				monthlyChart.put("MAX_NUMBER", maxNumbers);
				monthlyChart.put("MIN_NUMBER", minNumbers);
				jsonObject.put("MONTHLY_CHART", monthlyChart);
				if (request.getSession().getAttribute("TB_MONTHLY_CHART") == null) {
					request.getSession().setAttribute("TB_MONTHLY_CHART", monthlyChart);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			jsonObject = JSONObject.fromObject("{SUCCESS:false}");
		} finally {
			response.setContentType("application/json; charset=UTF-8");
			response.getWriter().print(jsonObject.toString());
		}
	}

	/*
	 * 环比（第一个）.
	 * 
	 */
	@RequestMapping("/findHbChartData.ilf")
	public void findHbChartData(HttpServletRequest request, HttpServletResponse response) throws Exception {
		JSONObject jsonObject = JSONObject.fromObject("{SUCCESS:true}");
		try {
			jsonObject.put("MONTHLY_CHART", request.getSession().getAttribute("HB_MONTHLY_CHART"));
		} catch (Exception e) {
			e.printStackTrace();
			jsonObject = JSONObject.fromObject("{SUCCESS:false}");
		} finally {
			response.setContentType("application/json; charset=UTF-8");
			response.getWriter().print(jsonObject.toString());
		}
	}

	/*
	 * 
	 * 在线风控
	 * 
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping("/findRiskControlOnlineData.ilf")
	public void findRiskControlOnlineData(HttpServletRequest request, HttpServletResponse response, String city,
			String mouth) throws Exception {
		String searchCity = "";
		String searchDate = "";
		if (mouth != null && !mouth.equals("")) {
			searchDate = mouth;
		}
		if (city != null && !city.equals("") && !city.equals("全省") && !city.equals("--") && !city.equals("--请选择--")
				&& !city.equals("四川")) {
			searchCity = city;
		}

		JSONObject jsonObject = JSONObject.fromObject("{success:true}");
		Boolean isProvince = false;
		String belongArea = "";
		Boolean isProvinceITAdmin= false;
		int userId=0;
		try {
			Object loginObject = request.getSession().getAttribute("LoginUserInfo");
			if (loginObject != null) {
				/*判断省份跟地市*/
				Map<String, Object> loginUser = (HashMap<String, Object>) loginObject;
				if (loginUser.get("BELONG_AREA").toString().indexOf("四川") != -1
						|| loginUser.get("BELONG_AREA").toString().indexOf("省") != -1) {
					isProvince = true;
					belongArea = loginUser.get("BELONG_AREA").toString();
				} else if (loginUser.get("USER_NAME").toString().toUpperCase().indexOf("ROOT") != -1) {
					isProvince = true;
					belongArea = loginUser.get("BELONG_AREA").toString();
				} else {
					belongArea = loginUser.get("BELONG_AREA").toString();
				}
				if (belongArea.length() > 2) {
					belongArea = belongArea.substring(0, 2);
				}
				jsonObject.put("IS_PROVINCE", isProvince);
				jsonObject.put("BELONG_AREA", belongArea);
				/*获取userID*/
				userId=loginUserUtil.getLoginUserId(request);
			}
			/*
			 *判断是否为省份IT管理员 
			 */
			String sql = "";
			sql+="select * from S_SYSTEM_USER_ROLE where USER_ID='"+userId+"'";
			List<Map<String, Object>> list=jdbcTemplate.queryForList(sql);
			if(list.size()>0) {
				
				for(Map<String, Object> map:list) {
					if(map.get("ROLE_ID").toString().equals("5723")) {
						isProvinceITAdmin=true;
					}
				}
			}
			jsonObject.put("IS_PROVINCEITADMIN", isProvinceITAdmin);
			/*
			 * 风险问题总数
			 */
			sql = "";
			sql += "select count(*) from ";
			sql += "(select MOUTH,CITY,RISK_TYPE from " + resUserName1 + ".ORC_CGI_DETAIL";
			sql += " union all ";
			sql += "select MOUTH,CITY,RISK_TYPE from " + resUserName1 + ".ORC_CER_DETAIL";
			sql += " union all ";
			sql += "select MOUTH,CITY,RISK_TYPE from " + resUserName1 + ".ORC_OSP_DETAIL";
			sql += " union all ";
			sql += "select MOUTH,CITY,RISK_TYPE from " + resUserName1 + ".ORC_NVOTM_DETAIL";
			sql += " union all ";
			sql += "select MOUTH,CITY,RISK_TYPE from " + resUserName1 + ".ORC_RPA_DETAIL";
			sql += " union all ";
			sql += "select MOUTH,CITY,RISK_TYPE from " + resUserName1 + ".ORC_CTP_DETAIL";
			sql += " union all ";
			sql += "select MOUTH,CITY,RISK_TYPE from " + resUserName1 + ".ORC_PCOND_DETAIL";
			sql += " union all ";
			sql += "select MOUTH,CITY,RISK_TYPE from " + resUserName1 + ".ORC_OM_DETAIL";
			sql += " union all ";
			sql += "select MOUTH,CITY,RISK_TYPE from " + resUserName1 + ".ORC_CACDD_DETAIL";
			sql += " union all ";
			sql += "select MOUTH,CITY,RISK_TYPE from " + resUserName1 + ".ORC_CTNH_DETAIL";
			sql += " union all ";
			sql += "select MOUTH,CITY,RISK_TYPE from " + resUserName1 + ".ORC_POOI_DETAIL";
			sql += " union all ";
			sql += "select MOUTH,CITY,RISK_TYPE from " + resUserName1 + ".ORC_NSCEA_DETAIL";
			sql += " union all ";
			sql += "select MOUTH,CITY,RISK_TYPE from " + resUserName1 + ".ORC_TRMCD_DETAIL";
			sql += " union all ";
			sql += "select MOUTH,CITY,RISK_TYPE from " + resUserName1 + ".ORC_RAOBTI_DETAIL";
			sql += " union all ";
			sql += "select MOUTH,CITY,RISK_TYPE from " + resUserName1 + ".ORC_TFHE_DETAIL";
			sql += " union all ";
			sql += "select MOUTH,CITY,RISK_TYPE from " + resUserName1 + ".ORC_REMA_DETAIL";
			sql += " union all ";
			sql += "select MOUTH,CITY,RISK_TYPE from " + resUserName1 + ".ORC_ZCCA_DETAIL";
			sql += " union all ";
			sql += "select MOUTH,CITY,RISK_TYPE from " + resUserName1 + ".ORC_RCA_DETAIL";
			sql += ") A";
			if (!isProvince) {
				sql += " where A.CITY='" + belongArea + "'";
				if (!searchDate.equals("")) {
					sql += " and to_char(A.MOUTH,'yyyy-MM')='" + searchDate + "'";
				}
			} else {
				if (!searchCity.equals("")) {
					sql += " where A.CITY='" + searchCity + "'";
					if (!searchDate.equals("")) {
						sql += " and to_char(A.MOUTH,'yyyy-MM')='" + searchDate + "'";
					}
				} else {
					if (!searchDate.equals("")) {
						sql += " where to_char(A.MOUTH,'yyyy-MM')='" + searchDate + "'";
					}
				}

			}
			// System.out.println(sql);
			jsonObject.put("RISK_QUESTION_TOTAL", jdbcTemplate.queryForInt(sql));
			// System.out.println(jdbcTemplate.queryForInt(sql));
			/*
			 * 问题已反馈总数
			 */
			sql = "";
			sql += "select count(*) from ";
			sql += "(select MOUTH,CITY,RISK_TYPE,REASON,FEE_PEOPLE,FEE_TIME from " + resUserName1 + ".ORC_CGI_DETAIL";
			sql += " union all ";
			sql += "select MOUTH,CITY,RISK_TYPE,REASON,FEE_PEOPLE,FEE_TIME from " + resUserName1 + ".ORC_CER_DETAIL";
			sql += " union all ";
			sql += "select MOUTH,CITY,RISK_TYPE,REASON,FEE_PEOPLE,FEE_TIME from " + resUserName1 + ".ORC_OSP_DETAIL";
			sql += " union all ";
			sql += "select MOUTH,CITY,RISK_TYPE,REASON,FEE_PEOPLE,FEE_TIME from " + resUserName1 + ".ORC_NVOTM_DETAIL";
			sql += " union all ";
			sql += "select MOUTH,CITY,RISK_TYPE,REASON,FEE_PEOPLE,FEE_TIME from " + resUserName1 + ".ORC_RPA_DETAIL";
			sql += " union all ";
			sql += "select MOUTH,CITY,RISK_TYPE,REASON,FEE_PEOPLE,FEE_TIME from " + resUserName1 + ".ORC_CTP_DETAIL";
			sql += " union all ";
			sql += "select MOUTH,CITY,RISK_TYPE,REASON,FEE_PEOPLE,FEE_TIME from " + resUserName1 + ".ORC_PCOND_DETAIL";
			sql += " union all ";
			sql += "select MOUTH,CITY,RISK_TYPE,REASON,FEE_PEOPLE,FEE_TIME from " + resUserName1 + ".ORC_OM_DETAIL";
			sql += " union all ";
			sql += "select MOUTH,CITY,RISK_TYPE,REASON,FEE_PEOPLE,FEE_TIME from " + resUserName1 + ".ORC_CACDD_DETAIL";
			sql += " union all ";
			sql += "select MOUTH,CITY,RISK_TYPE,REASON,FEE_PEOPLE,FEE_TIME from " + resUserName1 + ".ORC_CTNH_DETAIL";
			sql += " union all ";
			sql += "select MOUTH,CITY,RISK_TYPE,REASON,FEE_PEOPLE,FEE_TIME from " + resUserName1 + ".ORC_POOI_DETAIL";
			sql += " union all ";
			sql += "select MOUTH,CITY,RISK_TYPE,REASON,FEE_PEOPLE,FEE_TIME from " + resUserName1 + ".ORC_NSCEA_DETAIL";
			sql += " union all ";
			sql += "select MOUTH,CITY,RISK_TYPE,REASON,FEE_PEOPLE,FEE_TIME from " + resUserName1 + ".ORC_TRMCD_DETAIL";
			sql += " union all ";
			sql += "select MOUTH,CITY,RISK_TYPE,REASON,FEE_PEOPLE,FEE_TIME from " + resUserName1 + ".ORC_RAOBTI_DETAIL";
			sql += " union all ";
			sql += "select MOUTH,CITY,RISK_TYPE,REASON,FEE_PEOPLE,FEE_TIME from " + resUserName1 + ".ORC_TFHE_DETAIL";
			sql += " union all ";
			sql += "select MOUTH,CITY,RISK_TYPE,REASON,FEE_PEOPLE,FEE_TIME from " + resUserName1 + ".ORC_REMA_DETAIL";
			sql += " union all ";
			sql += "select MOUTH,CITY,RISK_TYPE,REASON,FEE_PEOPLE,FEE_TIME from " + resUserName1 + ".ORC_ZCCA_DETAIL";
			sql += " union all ";
			sql += "select MOUTH,CITY,RISK_TYPE,REASON,FEE_PEOPLE,FEE_TIME from " + resUserName1 + ".ORC_RCA_DETAIL";
			sql += ") A";
			sql += " where trim(A.REASON) is not NULL and trim(A.FEE_PEOPLE) is not NULL and trim(A.FEE_TIME) is not NULL";
			if (!isProvince) {
				sql += " and A.CITY='" + belongArea + "'";
				if (!searchDate.equals("")) {
					sql += " and to_char(A.MOUTH,'yyyy-MM')='" + searchDate + "'";
				}
			} else {
				if (!searchCity.equals("")) {
					sql += " and A.CITY='" + searchCity + "'";
					if (!searchDate.equals("")) {
						sql += " and to_char(A.MOUTH,'yyyy-MM')='" + searchDate + "'";
					}
				} else {
					if (!searchDate.equals("")) {
						sql += " and to_char(A.MOUTH,'yyyy-MM')='" + searchDate + "'";
					}
				}

			}
			jsonObject.put("FEEDBACK_QUESTION_TOTAL", jdbcTemplate.queryForInt(sql));
			/*
			 * 资金问题总数
			 */
			sql = "";
			sql += "select count(*) from ";
			sql += "(select MOUTH,CITY,RISK_TYPE,REASON,FEE_PEOPLE,FEE_TIME from " + resUserName1 + ".ORC_CGI_DETAIL";
			sql += " union all ";
			sql += "select MOUTH,CITY,RISK_TYPE,REASON,FEE_PEOPLE,FEE_TIME from " + resUserName1 + ".ORC_CER_DETAIL";
			sql += " union all ";
			sql += "select MOUTH,CITY,RISK_TYPE,REASON,FEE_PEOPLE,FEE_TIME from " + resUserName1 + ".ORC_OSP_DETAIL";
			sql += " union all ";
			sql += "select MOUTH,CITY,RISK_TYPE,REASON,FEE_PEOPLE,FEE_TIME from " + resUserName1 + ".ORC_NVOTM_DETAIL";
			sql += " union all ";
			sql += "select MOUTH,CITY,RISK_TYPE,REASON,FEE_PEOPLE,FEE_TIME from " + resUserName1 + ".ORC_RPA_DETAIL";
			sql += " union all ";
			sql += "select MOUTH,CITY,RISK_TYPE,REASON,FEE_PEOPLE,FEE_TIME from " + resUserName1 + ".ORC_CTP_DETAIL";
			sql += " union all ";
			sql += "select MOUTH,CITY,RISK_TYPE,REASON,FEE_PEOPLE,FEE_TIME from " + resUserName1 + ".ORC_PCOND_DETAIL";
			sql += " union all ";
			sql += "select MOUTH,CITY,RISK_TYPE,REASON,FEE_PEOPLE,FEE_TIME from " + resUserName1 + ".ORC_OM_DETAIL";
			sql += ") A";
			if (!isProvince) {
				sql += " where A.CITY='" + belongArea + "'";
				if (!searchDate.equals("")) {
					sql += " and to_char(A.MOUTH,'yyyy-MM')='" + searchDate + "'";
				}
			} else {
				if (!searchCity.equals("")) {
					sql += " where A.CITY='" + searchCity + "'";
					if (!searchDate.equals("")) {
						sql += " and to_char(A.MOUTH,'yyyy-MM')='" + searchDate + "'";
					}
				} else {
					if (!searchDate.equals("")) {
						sql += " where to_char(A.MOUTH,'yyyy-MM')='" + searchDate + "'";
					}
				}

			}
			jsonObject.put("FUND_QUESTION_TOTAL", jdbcTemplate.queryForInt(sql));
			/*
			 * 收入问题总数
			 */
			sql = "";
			sql += "select count(*) from ";
			sql += "(select MOUTH,CITY,RISK_TYPE from " + resUserName1 + ".ORC_CACDD_DETAIL";
			sql += " union all ";
			sql += "select MOUTH,CITY,RISK_TYPE from " + resUserName1 + ".ORC_CTNH_DETAIL";
			sql += ") A";
			if (!isProvince) {
				sql += " where A.CITY='" + belongArea + "'";
				if (!searchDate.equals("")) {
					sql += " and to_char(A.MOUTH,'yyyy-MM')='" + searchDate + "'";
				}
			} else {
				if (!searchCity.equals("")) {
					sql += " where A.CITY='" + searchCity + "'";
					if (!searchDate.equals("")) {
						sql += " and to_char(A.MOUTH,'yyyy-MM')='" + searchDate + "'";
					}
				} else {
					if (!searchDate.equals("")) {
						sql += " where to_char(A.MOUTH,'yyyy-MM')='" + searchDate + "'";
					}
				}

			}
			jsonObject.put("INCOME_QUESTION_TOTAL", jdbcTemplate.queryForInt(sql));
			/*
			 * 基站电费总数
			 */
			sql = "";
			sql += "select count(*) from ";
			sql += "(select MOUTH,CITY,RISK_TYPE from " + resUserName1 + ".ORC_TFHE_DETAIL";
			sql += " union all ";
			sql += "select MOUTH,CITY,RISK_TYPE from " + resUserName1 + ".ORC_REMA_DETAIL";
			sql += ") A";
			if (!isProvince) {
				sql += " where A.CITY='" + belongArea + "'";
				if (!searchDate.equals("")) {
					sql += " and to_char(A.MOUTH,'yyyy-MM')='" + searchDate + "'";
				}
			} else {
				if (!searchCity.equals("")) {
					sql += " where A.CITY='" + searchCity + "'";
					if (!searchDate.equals("")) {
						sql += " and to_char(A.MOUTH,'yyyy-MM')='" + searchDate + "'";
					}
				} else {
					if (!searchDate.equals("")) {
						sql += " where to_char(A.MOUTH,'yyyy-MM')='" + searchDate + "'";
					}
				}

			}
			jsonObject.put("STAND_ELECTRIC_TOTAL", jdbcTemplate.queryForInt(sql));
			/*
			 * 场租费总数
			 */
			sql = "";
			sql += "select count(*) from ";
			sql += "(select MOUTH,CITY,RISK_TYPE from " + resUserName1 + ".ORC_ZCCA_DETAIL";
			sql += " union all ";
			sql += "select MOUTH,CITY,RISK_TYPE from " + resUserName1 + ".ORC_RCA_DETAIL";
			sql += ") A";
			if (!isProvince) {
				sql += " where A.CITY='" + belongArea + "'";
				if (!searchDate.equals("")) {
					sql += " and to_char(A.MOUTH,'yyyy-MM')='" + searchDate + "'";
				}
			} else {
				if (!searchCity.equals("")) {
					sql += " where A.CITY='" + searchCity + "'";
					if (!searchDate.equals("")) {
						sql += " and to_char(A.MOUTH,'yyyy-MM')='" + searchDate + "'";
					}
				} else {
					if (!searchDate.equals("")) {
						sql += " where to_char(A.MOUTH,'yyyy-MM')='" + searchDate + "'";
					}
				}

			}
			jsonObject.put("COLOCATION_RENT_TOTAL", jdbcTemplate.queryForInt(sql));
			/*
			 * 工程类总数
			 */
			sql = "";
			sql += "select count(*) from ";
			sql += "(select MOUTH,CITY,RISK_TYPE from " + resUserName1 + ".ORC_POOI_DETAIL";
			sql += " union all ";
			sql += "select MOUTH,CITY,RISK_TYPE from " + resUserName1 + ".ORC_NSCEA_DETAIL";
			sql += " union all ";
			sql += "select MOUTH,CITY,RISK_TYPE from " + resUserName1 + ".ORC_TRMCD_DETAIL";
			sql += " union all ";
			sql += "select MOUTH,CITY,RISK_TYPE from " + resUserName1 + ".ORC_RAOBTI_DETAIL";
			sql += ") A";
			if (!isProvince) {
				sql += " where A.CITY='" + belongArea + "'";
				if (!searchDate.equals("")) {
					sql += " and to_char(A.MOUTH,'yyyy-MM')='" + searchDate + "'";
				}
			} else {
				if (!searchCity.equals("")) {
					sql += " where A.CITY='" + searchCity + "'";
					if (!searchDate.equals("")) {
						sql += " and to_char(A.MOUTH,'yyyy-MM')='" + searchDate + "'";
					}
				} else {
					if (!searchDate.equals("")) {
						sql += " where to_char(A.MOUTH,'yyyy-MM')='" + searchDate + "'";
					}
				}

			}
			jsonObject.put("PROJECT_TOTAL", jdbcTemplate.queryForInt(sql));
			/*
			 * 代维问题总数
			 */
			sql = "";
			if (!isProvince) {
				sql += "";
			}
			jsonObject.put("ASHABURNA_QUESTION_TOTAL", 666);

			/* 资金问题列表数据 */
			List<Map<String, Object>> fund_list = new ArrayList<Map<String, Object>>();
			sql = "";
			sql += "select ROWNUM as ID,C.* from";
			sql += "(";
			sql += "select B.RISK_NAME,B.ID as RISK_ID,SUM(case when trim(A.REASON) is NULL or trim(A.FEE_PEOPLE) is NULL or trim(A.FEE_TIME) is NULL THEN 1 ELSE 0 END) QUNUM,SUM(case when trim(A.REASON) is NULL and trim(A.FEE_PEOPLE) is NULL and trim(A.FEE_TIME) is NULL THEN 0 ELSE 1 END) FEEDNUM from ";
			sql += "(";
			sql += "select CITY,MOUTH,RISK_NAME,REASON,FEE_PEOPLE,FEE_TIME from " + resUserName1 + ".ORC_CGI_DETAIL ";
			sql += " union all ";
			sql += "select CITY,MOUTH,RISK_NAME,REASON,FEE_PEOPLE,FEE_TIME from " + resUserName1 + ".ORC_CER_DETAIL ";
			sql += " union all ";
			sql += " select CITY,MOUTH,RISK_NAME,REASON,FEE_PEOPLE,FEE_TIME from " + resUserName1 + ".ORC_OSP_DETAIL ";
			sql += " union all ";
			sql += " select CITY,MOUTH,RISK_NAME,REASON,FEE_PEOPLE,FEE_TIME from " + resUserName1
					+ ".ORC_NVOTM_DETAIL ";
			sql += " union all ";
			sql += " select CITY,MOUTH,RISK_NAME,REASON,FEE_PEOPLE,FEE_TIME from " + resUserName1 + ".ORC_RPA_DETAIL ";
			sql += " union all ";
			sql += " select CITY,MOUTH,RISK_NAME,REASON,FEE_PEOPLE,FEE_TIME from " + resUserName1 + ".ORC_CTP_DETAIL ";
			sql += " union all ";
			sql += " select CITY,MOUTH,RISK_NAME,REASON,FEE_PEOPLE,FEE_TIME from " + resUserName1
					+ ".ORC_PCOND_DETAIL ";
			sql += " union all ";
			sql += " select CITY,MOUTH,RISK_NAME,REASON,FEE_PEOPLE,FEE_TIME from " + resUserName1 + ".ORC_OM_DETAIL ";
			sql += ") A," + resUserName1 + ".ORC_RISK_NAME_DETAIL B";
			sql += " where A.RISK_NAME=B.ID ";
			if (!isProvince) {
				sql += " and A.CITY='" + belongArea + "'";
				if (!searchDate.equals("")) {
					sql += " and to_char(A.MOUTH,'yyyy-MM')='" + searchDate + "'";
				}
			} else {
				if (!searchCity.equals("")) {
					sql += " and A.CITY='" + searchCity + "'";
					if (!searchDate.equals("")) {
						sql += " and to_char(A.MOUTH,'yyyy-MM')='" + searchDate + "'";
					}
				} else {
					if (!searchDate.equals("")) {
						sql += " and to_char(A.MOUTH,'yyyy-MM')='" + searchDate + "'";
					}
				}

			}
			sql += " GROUP BY B.RISK_NAME,B.ID ";
			sql += ") C ";
			fund_list = jdbcTemplate.queryForList(sql);
			jsonObject.put("FUND_QUESTION_DETAIL", fund_list);

			/* 工程类问题数据列表 */
			/* 问题列表数据 */
			List<Map<String, Object>> pro_list = new ArrayList<Map<String, Object>>();
			sql = "";
			sql += "select ROWNUM as ID,C.* from";
			sql += "(";
			sql += "select B.RISK_NAME,B.ID as RISK_ID,count(*) as QUNUM,SUM(case when trim(A.REASON) is NULL and trim(A.FEE_PEOPLE) is NULL and trim(A.FEE_TIME) is NULL THEN 0 ELSE 1 END) FEEDNUM from ";
			sql += "(";
			sql += "select CITY,MOUTH,RISK_NAME,REASON,FEE_PEOPLE,FEE_TIME from " + resUserName1 + ".ORC_POOI_DETAIL ";
			sql += " union all ";
			sql += "select CITY,MOUTH,RISK_NAME,REASON,FEE_PEOPLE,FEE_TIME from " + resUserName1 + ".ORC_NSCEA_DETAIL ";
			sql += " union all ";
			sql += " select CITY,MOUTH,RISK_NAME,REASON,FEE_PEOPLE,FEE_TIME from " + resUserName1
					+ ".ORC_TRMCD_DETAIL ";
			sql += " union all ";
			sql += " select CITY,MOUTH,RISK_NAME,REASON,FEE_PEOPLE,FEE_TIME from " + resUserName1
					+ ".ORC_RAOBTI_DETAIL ";
			sql += ") A," + resUserName1 + ".ORC_RISK_NAME_DETAIL B";
			sql += " where A.RISK_NAME=B.ID ";
			if (!isProvince) {
				sql += " and A.CITY='" + belongArea + "'";
				if (!searchDate.equals("")) {
					sql += " and to_char(A.MOUTH,'yyyy-MM')='" + searchDate + "'";
				}
			} else {
				if (!searchCity.equals("")) {
					sql += " and A.CITY='" + searchCity + "'";
					if (!searchDate.equals("")) {
						sql += " and to_char(A.MOUTH,'yyyy-MM')='" + searchDate + "'";
					}
				} else {
					if (!searchDate.equals("")) {
						sql += " and to_char(A.MOUTH,'yyyy-MM')='" + searchDate + "'";
					}
				}

			}
			sql += " GROUP BY B.RISK_NAME,B.ID ";
			sql += ") C ";
			pro_list = jdbcTemplate.queryForList(sql);
			jsonObject.put("PROJECT_QUESTION_DETAIL", pro_list);

			request.getSession().setAttribute("IS_PROVINCE", isProvince);
			request.getSession().setAttribute("BELONG_AREA", belongArea);
		} catch (Exception e) {
			e.printStackTrace();
			jsonObject = JSONObject.fromObject("{success:false}");
		} finally {
			response.setContentType("application/json; charset=UTF-8");
			response.getWriter().print(jsonObject.toString());
		}
	}
}
