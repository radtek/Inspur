package com.function.rules.action;
import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import com.function.dbmanage.model.BasicDb;
import com.function.dbmanage.model.BasicDbTable;
import com.function.dbmanage.service.BasicColumnService;
import com.function.dbmanage.service.BasicDbService;
import com.function.dbmanage.service.BasicDbTableService;
import com.function.rules.model.RuleDetail;
import com.function.rules.model.RuleHistory;
import com.function.rules.model.RuleImport;
import com.function.rules.model.RuleItem;
import com.function.rules.model.RuleItemConnect;
import com.function.rules.model.RuleJob;
import com.function.rules.model.RuleQuartz;
import com.function.rules.service.DbService;
import com.function.rules.service.QuartzService;
import com.function.rules.service.ReportBuilder;
import com.function.rules.service.RuleConnectService;
import com.function.rules.service.RuleDetailService;
import com.function.rules.service.RuleHistoryService;
import com.function.rules.service.RuleImportService;
import com.function.rules.service.RuleItemService;
import com.function.rules.service.RuleJobService;
import com.function.rules.service.RuleQuartzService;
import com.function.rules.service.SqlBuilder;
import com.function.rules.service.TaskBuilder;
@Controller("com.function.rules.action.RuleEditAction")
@RequestMapping(value="/ruleEditAction")
@Scope("prototype")
public class RuleEditAction{
	
	@Autowired
	private QuartzService quartzService;
	
	/*
	 * 	DataBase
	 * 
	 * */
	@Autowired
	private BasicDbService basicDbService;
	
	@Autowired
	private BasicDbTableService basicDbTableService;
	
	@Autowired
	private BasicColumnService basicColumnService;
	
	/*
	 * 	Rule
	 * 
	 * */
	
	@Autowired
	private RuleDetailService ruleDetailService;
	
	@Autowired
	private RuleQuartzService ruleQuartzService;
	
	@Autowired
	private RuleHistoryService ruleHistoryService;
	
	@Autowired
	private RuleItemService ruleItemService;
	
	@Autowired
	private RuleConnectService ruleConnectService;
	
	@Autowired
	private RuleImportService ruleImportService;
	
	/*
	 * 	DataBase
	 * 
	 * */
	
	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	@Autowired
	private DbService dbService;
	
	/*
	 * 	Create Report
	 * 
	 * */
	
	@Autowired
	private SqlBuilder sqlBuilder;
	
	@Autowired
	private ReportBuilder reportBuilder;
	
	@Autowired
	private TaskBuilder taskBuilder;
	
	@Autowired
	private RuleJobService ruleJobService;
	
	/*
	 * 	??????????????????
	 * 
	 * */
	@RequestMapping("/findClasses.ilf")
	public void findClasses(HttpServletRequest request,HttpServletResponse response)throws Exception{
		JSONObject resultObject = JSONObject.fromObject("{success:true}");
		try{
			List<Map<String,Object>> items = jdbcTemplate.queryForList("SELECT CLASS_NAME,COUNT(1) AS RULE_NUMBER FROM RULE_DETAIL WHERE CLASS_NAME != '-' GROUP BY CLASS_NAME");
			resultObject.put("items",items);
		}catch(Exception e){
			e.printStackTrace();
			resultObject.put("success",false);
		}finally{
			response.setContentType("application/json;charset=UTF-8");
			response.getWriter().print(resultObject.toString());
		}
	}
	
	/*
	 * 	????????????:??????
	 * 
	 * */
	@RequestMapping("/findRuleByName.ilf")
	public void findRuleByName(@RequestParam String ruleName,HttpServletRequest request,HttpServletResponse response)throws Exception{
		JSONObject resultObject = JSONObject.fromObject("{success:true}");
		try{
			List<Map<String,Object>> items = jdbcTemplate.queryForList("SELECT * FROM RULE_DETAIL WHERE RULE_NAME = '"+ruleName+"'");
			if(items.size()>0){
				resultObject.put("isFinded",true);
				resultObject.put("result",items.get(0));
			}else{
				resultObject.put("isFinded",false);
			}
		}catch(Exception e){
			e.printStackTrace();
			resultObject.put("success",false);
		}finally{
			response.setContentType("application/json;charset=UTF-8");
			response.getWriter().print(resultObject.toString());
		}
	}
	
	/*
	 * 	??????????????????
	 * 
	 * */
	@RequestMapping("/findClassCount.ilf")
	public void findClassCount(@RequestParam String className,@RequestParam String pageNumber,@RequestParam String pageContext,HttpServletRequest request,HttpServletResponse response)throws Exception{
		JSONObject resultObject = JSONObject.fromObject("{success:true}");
		try{
			Integer startIndex = 1;
			Integer limitIndex = 4;
			if(Integer.parseInt(pageNumber)==1){
				startIndex = 1;
				limitIndex = Integer.parseInt(pageContext);
			}else{
				startIndex = (Integer.parseInt(pageNumber)-1)*Integer.parseInt(pageContext)+1;
				limitIndex = Integer.parseInt(pageNumber)*Integer.parseInt(pageContext);
			}
			/*????????????*/
			String countSql = "";
			countSql+="SELECT COUNT(1) AS TOTAL_COUNT FROM(";
			countSql+="	  SELECT CLASS_NAME,COUNT(1) AS RULE_NUMBER FROM RULE_DETAIL WHERE CLASS_NAME != '-' AND CREATE_USER = "+getLoginUserId(request);		
			if(className!=null && !"".equals(className)){
				countSql+=" AND CLASS_NAME LIKE '%"+className+"%' ";
			}
			countSql+="	  GROUP BY CLASS_NAME";
			countSql+=")";
			Integer totalCount = jdbcTemplate.queryForInt(countSql);
			resultObject.put("totalCount",totalCount);
			/*????????????*/
			String pageSql = "";
			pageSql+="SELECT F.* FROM (";
			pageSql+="	  SELECT T.* FROM(";
			pageSql+="		  SELECT A.*,ROWNUM AS RN FROM(";
			pageSql+="			  SELECT CLASS_NAME,COUNT(1) AS RULE_NUMBER,TO_CHAR(WM_CONCAT(ID)) AS IDS ";
			pageSql+="			  FROM RULE_DETAIL ";
			pageSql+="			  WHERE CREATE_USER = "+getLoginUserId(request)+" AND CLASS_NAME != '-'";
			if(className!=null && !"".equals(className)){
				pageSql+=" AND CLASS_NAME LIKE '%"+className+"%' ";
			}
			pageSql+="										GROUP BY CLASS_NAME";
			pageSql+="		  ) A";
			pageSql+="	  ) T WHERE T.RN <= "+limitIndex;
			pageSql+=") F WHERE F.RN >= "+startIndex;
			List<Map<String,Object>> items = jdbcTemplate.queryForList(pageSql);
			resultObject.put("items",items);
		}catch(Exception e){
			e.printStackTrace();
			resultObject.put("success",false);
		}finally{
			response.setContentType("application/json;charset=UTF-8");
			response.getWriter().print(resultObject.toString());
		}
	}
	
	/*
	 * 	??????????????????
	 * 
	 * */
	@RequestMapping("/findRuleOfClass.ilf")
	public void findRuleOfClass(@RequestParam String className,HttpServletRequest request,HttpServletResponse response)throws Exception{
		JSONObject resultObject = JSONObject.fromObject("{success:true}");
		try{
			List<Map<String,Object>> items = jdbcTemplate.queryForList("SELECT RULE_NAME,RULE_DESC FROM RULE_DETAIL WHERE CLASS_NAME = '"+className+"'");
			resultObject.put("items",items);
		}catch(Exception e){
			e.printStackTrace();
			resultObject.put("success",false);
		}finally{
			response.setContentType("application/json;charset=UTF-8");
			response.getWriter().print(resultObject.toString());
		}
	}
	
	/*
	 * 	????????????
	 * 
	 * */
	@RequestMapping("/auditRightNow.ilf")
	public void auditRightNow(
		@RequestParam String ruleId,
		@RequestParam String jobToken,
		HttpServletRequest request,
		HttpServletResponse response
	)throws Exception{
		JSONObject resultObject = JSONObject.fromObject("{success:true}");
		try{
			taskBuilder.buildCheck(request,Integer.parseInt(ruleId),jobToken);
		}catch(Exception e){
			e.printStackTrace();
			resultObject.put("success",false);
		}finally{
			response.setContentType("application/json;charset=UTF-8");
			response.getWriter().print(resultObject.toString());
		}
	}
	
	/*
	 * 	????????????
	 * 
	 * */
	@RequestMapping("/moditorProcess.ilf")
	public void moditorProcess(@RequestParam String ruleIds,HttpServletRequest request,HttpServletResponse response)throws Exception{
		JSONObject resultObject = JSONObject.fromObject("{success:true}");
		try{
			String finalArg = "";
			String[] args = ruleIds.split(",");
			if(args.length>0){
				for(int i=0;i<args.length;i++){
					if(i==0){
						finalArg = "'"+args[i]+"'";
					}else{
						finalArg+= ",'"+args[i]+"'";
					}
				}
			}
			JSONArray finishedArray = new JSONArray();
			List<RuleJob> ruleJobs = ruleJobService.selectModelsByHql("from RuleJob where TOKEN in ("+finalArg+") and IS_FINISHED = 'Y'");
			if(ruleJobs.size()>0){
				for(int i=0;i<ruleJobs.size();i++){
					RuleJob ruleJob = ruleJobs.get(i);
					JSONObject jsonObject = new JSONObject();
					jsonObject.put("RULE_ID",ruleJob.getTOKEN());
					jsonObject.put("RULE_NAME",ruleDetailService.selectModel(ruleJob.getRULE_ID()).getRULE_NAME());
					finishedArray.add(jsonObject);
				}
			}
			resultObject.put("ITEMS",finishedArray);
		}catch(Exception e){
			e.printStackTrace();
			resultObject.put("success",false);
		}finally{
			response.setContentType("application/json;charset=UTF-8");
			response.getWriter().print(resultObject.toString());
		}
	}
	
	/*
	 * 	??????????????????.
	 * 
	 * */
	@RequestMapping("/downloadTemplate.ilf")
	public void downloadTemplate(HttpServletRequest request,HttpServletResponse response)throws Exception{
		String strDirPath = request.getSession().getServletContext().getRealPath("/")+"templates/RuleEdit.xlsx";
		File templateFile = new File(strDirPath);
		if(templateFile.exists()){
			long filesize = templateFile.length();
			FileInputStream fileInputStream = new FileInputStream(templateFile);
			response.setContentType("text/html;charset=gb2312");
			response.addHeader("content-type","application/x-msdownload;");
			response.setHeader("Content-Disposition","attachment;filename=\""+new String((templateFile.getName()).getBytes(),"ISO8859-1"));
			response.addHeader("content-length",Long.toString(filesize));
			OutputStream output = response.getOutputStream();
			byte[] bytes = new byte[1024];
			int i = 0;    
			while((i=fileInputStream.read(bytes))>0){
				output.write(bytes,0,i);    
			}    
			output.flush();
			output.close();
			fileInputStream.close();
		}
	}
	
	/*
	 * 	???????????????????????????
	 * 
	 * */
	@RequestMapping(value="/uploadDataFile.ilf",method=RequestMethod.POST)
	public void uploadDataFile(@RequestParam("TemplateUpload")MultipartFile fileToUpload,HttpServletRequest request,HttpServletResponse response)throws Exception{
		JSONObject resultObject = JSONObject.fromObject("{success:true,message:'??????????????????'}");
		try{
			Boolean isValid = true;
			if(!fileToUpload.isEmpty()){
				/*
				 * 	?????????????????????????????????
				 * 
				 * */
				String strDirPath = request.getSession().getServletContext().getRealPath("/")+"uploads/rule/"+fileToUpload.getOriginalFilename();
				File file = new File(strDirPath);
				if(file.exists()){
					file.delete();
				}
				fileToUpload.transferTo(file);
				/*
				 * 	???????????????????????????
				 * 
				 * */
				FileInputStream fis = new FileInputStream(file);
				XSSFWorkbook wk = new XSSFWorkbook(fis);
				Iterator<XSSFSheet> sheetCar = wk.iterator();
				XSSFSheet carSheet = null;
				while(sheetCar.hasNext()){
					carSheet = (XSSFSheet)sheetCar.next();
					if(carSheet.getSheetName().indexOf("Sheet")==-1){
						/*1.1.?????????????????????????????????*/
						XSSFRow targetModelRow = carSheet.getRow(2);
						if(targetModelRow==null){
							isValid = false;
						}else{
							XSSFCell targetModelCell = targetModelRow.getCell(2);
							if(targetModelCell==null || "".equals(targetModelCell.getStringCellValue())){
								isValid = false;
							}
						}
						if(!isValid){
							resultObject.put("success",false);
							resultObject.put("message","??????????????????????????????.");
							break;
						}
						/*1.1.?????????????????????????????????*/
						Integer existCount = jdbcTemplate.queryForInt("SELECT COUNT(1) FROM BASIC_DB_TABLE WHERE TABLE_NAME = '"+carSheet.getRow(2).getCell(2).getStringCellValue()+"'");
						if(existCount==0){
							isValid = false;
							resultObject.put("success",false);
							resultObject.put("message","?????????????????????["+carSheet.getRow(2).getCell(2).getStringCellValue()+"]?????????.");
							break;
						}
						/*2.????????????*/
						XSSFRow ruleNameRow = carSheet.getRow(3);
						if(ruleNameRow==null){
							isValid = false;
						}else{
							XSSFCell targetCell = ruleNameRow.getCell(2);
							if(targetCell==null || "".equals(targetCell.getStringCellValue())){
								isValid = false;
							}
						}
						if(!isValid){
							resultObject.put("success",false);
							resultObject.put("message","??????????????????????????????????????????.");
							break;
						}
						/*3.????????????*/
						XSSFRow problemDescribeRow = carSheet.getRow(5);
						if(problemDescribeRow==null){
							isValid = false;
						}else{
							XSSFCell targetCell = problemDescribeRow.getCell(2);
							if(targetCell==null || "".equals(targetCell.getStringCellValue())){
								isValid = false;
							}
						}
						if(!isValid){
							resultObject.put("success",false);
							resultObject.put("message","???????????????????????????????????????.");
							break;
						}
						/*4.????????????*/
						XSSFRow ruleTypeRow = carSheet.getRow(7);
						if(ruleTypeRow==null){
							isValid = false;
						}else{
							XSSFCell targetCell = ruleTypeRow.getCell(2);
							if(targetCell==null || "".equals(targetCell.getStringCellValue()) || (targetCell.getStringCellValue().indexOf("???????????????")==-1 && targetCell.getStringCellValue().indexOf("????????????")==-1)){
								isValid = false;
							}
						}
						if(!isValid){
							resultObject.put("success",false);
							resultObject.put("message",carSheet.getRow(3).getCell(2).getStringCellValue()+"????????????????????????????????????.");
							break;
						}
						/*5.????????????*/
						XSSFRow isUsingRow = carSheet.getRow(8);
						if(isUsingRow==null){
							isValid = false;
						}else{
							XSSFCell targetCell = isUsingRow.getCell(2);
							if(targetCell==null || "".equals(targetCell.getStringCellValue()) || (targetCell.getStringCellValue().indexOf("???")==-1 && targetCell.getStringCellValue().indexOf("???")==-1)){
								isValid = false;
							}
						}
						if(!isValid){
							resultObject.put("success",false);
							resultObject.put("message",carSheet.getRow(3).getCell(2).getStringCellValue()+"????????????????????????????????????.");
							break;
						}
						/*6.????????????*/
						XSSFRow ruleGradeRow = carSheet.getRow(9);
						if(ruleGradeRow==null){
							isValid = false;
						}else{
							XSSFCell targetCell = ruleGradeRow.getCell(2);
							if(targetCell==null || "".equals(targetCell.getStringCellValue()) || (targetCell.getStringCellValue().indexOf("??????")==-1 && targetCell.getStringCellValue().indexOf("??????")==-1 && targetCell.getStringCellValue().indexOf("????????????")==-1)){
								isValid = false;
							}
						}
						if(!isValid){
							resultObject.put("success",false);
							resultObject.put("message",carSheet.getRow(3).getCell(2).getStringCellValue()+"????????????????????????????????????.");
							break;
						}
						/*7.?????????SQL??????*/
						String ruleType = carSheet.getRow(7).getCell(2).getStringCellValue();
						if("???????????????".equals(ruleType)){
							/*????????????*/
							XSSFRow exportColumnsRow = carSheet.getRow(11);
							if(exportColumnsRow==null){
								isValid = false;
							}else{
								XSSFCell targetCell = exportColumnsRow.getCell(2);
								if(targetCell==null || "".equals(targetCell.getStringCellValue())){
									isValid = false;
								}
							}
							if(!isValid){
								resultObject.put("success",false);
								resultObject.put("message",carSheet.getRow(3).getCell(2).getStringCellValue()+"???????????????SQL?????????????????????????????????.");
								break;
							}
							/*??????SQL*/
							XSSFRow auditSqlRow = carSheet.getRow(12);
							if(auditSqlRow==null){
								isValid = false;
							}else{
								XSSFCell targetCell = auditSqlRow.getCell(2);
								if(targetCell==null || "".equals(targetCell.getStringCellValue())){
									isValid = false;
								}
							}
							if(!isValid){
								resultObject.put("success",false);
								resultObject.put("message",carSheet.getRow(3).getCell(2).getStringCellValue()+"???????????????SQL???????????????SQL????????????.");
								break;
							}
						}else if("????????????".equals(ruleType)){
							Integer lastRowNumber = carSheet.getLastRowNum();
							for(int i=15;i<=lastRowNumber;i++){
								XSSFRow xssfRow = carSheet.getRow(i);
								if(xssfRow==null){
									if(i==15){
										resultObject.put("success",false);
										resultObject.put("message","?????????????????????????????????["+carSheet.getRow(3).getCell(2).getStringCellValue()+"]??????????????????????????????.");
										break;
									}
								}else{
									/*???????????????????????????*/
									XSSFCell checkColumnCell = xssfRow.getCell(1);
									if(checkColumnCell==null || "".equals(checkColumnCell.getStringCellValue())){
										isValid = false;
									}
									if(!isValid){
										resultObject.put("success",false);
										resultObject.put("message","??????["+carSheet.getRow(3).getCell(2).getStringCellValue()+"]???????????????????????????.");
										break;
									}
									/*???????????????????????????????????????*/
									List<Map<String,Object>> tables = jdbcTemplate.queryForList("SELECT * FROM BASIC_DB_TABLE WHERE TABLE_NAME = '"+carSheet.getRow(2).getCell(2).getStringCellValue()+"'");
									if(tables.size()>0){
										Map<String,Object> editTable = tables.get(0);
										Integer columnExist = jdbcTemplate.queryForInt("SELECT COUNT(1) FROM BASIC_DB_COLUMN WHERE BELONG_TABLE = "+editTable.get("ID").toString()+" AND COLUMN_NAME = '"+xssfRow.getCell(1).getStringCellValue()+"'");
										if(columnExist==0){
											isValid = false;
											resultObject.put("success",false);
											resultObject.put("message","??????["+carSheet.getRow(3).getCell(2).getStringCellValue()+"]???????????????["+xssfRow.getCell(1).getStringCellValue()+"]?????????.");
											break;
										}
									}
									/*?????????*/
									XSSFCell checkExpressCell = xssfRow.getCell(2);
									if(checkExpressCell==null && "".equals(checkExpressCell.getStringCellValue())){
										isValid = false;
									}
									if(!isValid){
										resultObject.put("success",false);
										resultObject.put("message","??????["+carSheet.getRow(3).getCell(2).getStringCellValue()+"]??????????????????????????????.");
										break;
									}
									/*??????????????????*/
									XSSFCell isIgnoreCell = xssfRow.getCell(3);
									if(isIgnoreCell==null || "".equals(isIgnoreCell.getStringCellValue()) || (isIgnoreCell.getStringCellValue().indexOf("???")==-1 && isIgnoreCell.getStringCellValue().indexOf("???")==-1)){
										isValid = false;
									}
									if(!isValid){
										resultObject.put("success",false);
										resultObject.put("message","??????["+carSheet.getRow(3).getCell(2).getStringCellValue()+"]???[??????????????????]?????????????????????.");
										break;
									}
								}
							}
						}else{
							resultObject.put("success",false);
							resultObject.put("message","?????????????????????????????????.");
							break;
						}
					}
				}
				if(!isValid){
					file.delete();
				}else{
					sheetCar = wk.iterator();
					while(sheetCar.hasNext()){
						carSheet = (XSSFSheet)sheetCar.next();
						RuleImport ruleImport = new RuleImport();
						ruleImport.setFILE_NAME(fileToUpload.getOriginalFilename());
						ruleImport.setUPLOAD_DATE(new Date());
						/*
						 * 	?????????????????????
						 * 
						 * */
						JSONObject jsonObject = new JSONObject();
						Map<String,Object> editTable = null;
						List<Map<String,Object>> tables = jdbcTemplate.queryForList("SELECT * FROM BASIC_DB_TABLE WHERE TABLE_NAME = '"+carSheet.getRow(2).getCell(2).getStringCellValue()+"'");
						if(tables.size()>0){
							editTable = tables.get(0);
							/*????????????*/
							jsonObject.put("tableId",editTable.get("ID").toString());
							/*????????????*/
							jsonObject.put("conExpress","-");
							JSONObject quartzDetail = JSONObject.fromObject("{CIRCLE_TYPE:'???'}");
							jsonObject.put("quartzDetail",quartzDetail);
							/*????????????*/
							jsonObject.put("ruleName",carSheet.getRow(3).getCell(2).getStringCellValue());
							ruleImport.setRULE_NAME(jsonObject.getString("ruleName"));
							/*????????????*/
							jsonObject.put("ruleDesc",carSheet.getRow(5).getCell(2).getStringCellValue());
							/*????????????*/
							jsonObject.put("isUsing",carSheet.getRow(8).getCell(2).getStringCellValue().equals("???")?"Y":"N");
							/*????????????*/
							jsonObject.put("className",carSheet.getRow(4).getCell(2).getStringCellValue().equals("")?"-":carSheet.getRow(4).getCell(2).getStringCellValue());
							/*????????????*/
							if("??????".equals(carSheet.getRow(9).getCell(2).getStringCellValue())){
								jsonObject.put("ruleGradeLevel",2);
							}else if("??????".equals(carSheet.getRow(9).getCell(2).getStringCellValue())){
								jsonObject.put("ruleGradeLevel",3);
							}else if("????????????".equals(carSheet.getRow(9).getCell(2).getStringCellValue())){
								jsonObject.put("ruleGradeLevel",5);
							}
							/*????????????*/
							jsonObject.put("recovery",carSheet.getRow(6).getCell(2).getStringCellValue());
							/*????????????*/
							jsonObject.put("ruleId","-1");
							/*????????????*/
							jsonObject.put("ruleType",carSheet.getRow(7).getCell(2).getStringCellValue());
							if("???????????????".equals(jsonObject.get("ruleType").toString())){
								JSONObject sqlObject = new JSONObject();
								sqlObject.put("auditType","???????????????");
								sqlObject.put("exportColumns",carSheet.getRow(11).getCell(2).getStringCellValue());
								sqlObject.put("auditSql",carSheet.getRow(12).getCell(2).getStringCellValue());
								JSONArray nodeArray = new JSONArray();
								nodeArray.add(sqlObject);
								jsonObject.put("editedItems",nodeArray);
							}else if("????????????".equals(jsonObject.get("ruleType").toString())){
								JSONArray nodeArray = new JSONArray();
								Integer lastRowNumber = carSheet.getLastRowNum();
								for(int i=15;i<=lastRowNumber;i++){
									XSSFRow xssfRow = carSheet.getRow(i);
									if(xssfRow!=null){
										JSONObject sqlObject = new JSONObject();
										sqlObject.put("expression",xssfRow.getCell(2));
										sqlObject.put("isFilter",xssfRow.getCell(3).getStringCellValue().equals("???"));
										sqlObject.put("auditType","??????????????????");
										sqlObject.put("columnCode",Integer.parseInt(jdbcTemplate.queryForMap("SELECT * FROM BASIC_DB_COLUMN WHERE BELONG_TABLE = "+editTable.get("ID").toString()+" AND COLUMN_NAME = '"+xssfRow.getCell(1).getStringCellValue()+"'").get("ID").toString()));
										sqlObject.put("columnName",xssfRow.getCell(1).getStringCellValue());
										nodeArray.add(sqlObject);
									}
								}
								jsonObject.put("editedItems",nodeArray);
							}
							/*
							 * 	????????????
							 * 
							 * */
							RuleDetail ruleDetail = new RuleDetail(jsonObject);
							ruleDetail.setCREATE_USER(getLoginUserId(request));
							Integer newDetailCode = ruleDetailService.insertModel(ruleDetail);
							/*2.??????????????????*/
							RuleQuartz ruleQuartz = new RuleQuartz(newDetailCode,jsonObject.getJSONObject("quartzDetail"));
							ruleQuartzService.insertModel(ruleQuartz);
							/*3.????????????????????????*/
							JSONArray ruleArray = jsonObject.getJSONArray("editedItems");
							for(int i=0;i<ruleArray.size();i++){
								JSONObject itemObj = ruleArray.getJSONObject(i);				
								RuleItem ruleItem = new RuleItem(itemObj);			
								ruleItem.setBELONG_RULE(newDetailCode);				
								Integer ruleCode = ruleItemService.insertModel(ruleItem);			
								/*4.?????????????????????????????????*/
								if("??????????????????".equals(ruleItem.getAUDIT_TYPE())){
									JSONArray connectedColumns = itemObj.getJSONArray("connectedColumns");
									if(connectedColumns!=null && connectedColumns.size()>0){
										for(int j=0;j<connectedColumns.size();j++){
											JSONObject conObj = connectedColumns.getJSONObject(j);
											RuleItemConnect connect = new RuleItemConnect(conObj);
											connect.setBELONG_ITEM(ruleCode);
											ruleConnectService.insertModel(connect);					
										}
									}
								}
							}
							ruleImport.setRULE_ID(newDetailCode);
							ruleImport.setACTION_RESULT("????????????");
							ruleImportService.insertModel(ruleImport);
						}else{
							System.out.println("XUHUI???Data Model not finded.");
						}
					}
				}
			}else{
				resultObject.put("success",false);
				resultObject.put("message","?????????????????????????????????.");
			}
		}catch(Exception e){
			e.printStackTrace();
			resultObject.put("success",false);
			resultObject.put("message","?????????????????????????????????.");
		}finally{
			response.setContentType("text/html;charset=UTF-8");
			response.getWriter().print(resultObject.toString());
		}
	}
	
	/*
	 * 	??????????????????.
	 * 
	 * */
	@RequestMapping("/downloadReport.ilf")
	public void downloadReport(@RequestParam String ruleCode,HttpServletRequest request,HttpServletResponse response)throws Exception{
		RuleDetail ruleDetail = ruleDetailService.selectModel(Integer.parseInt(ruleCode));
		if(ruleDetail!=null){
			File dataFile = new File(
				jdbcTemplate.queryForMap("SELECT * FROM S_SYSTEM_PROPERTY WHERE PROPERTY_NAME = 'reportFolder'").get("PRO_VALUE").toString()+ruleDetail.getFILE_REPORT()
			);
			Boolean isExported = true;
			if(dataFile.exists()){
				isExported = true;
			}else{
				isExported = reportBuilder.createReport(Integer.parseInt(ruleCode));
			}
			if(isExported){
				FileInputStream fileInputStream = new FileInputStream(dataFile);
				long filesize = dataFile.length();
				response.setContentType("text/html;charset=gb2312");
				response.setHeader("Content-Disposition","attachment;filename=\""+new String((dataFile.getName()).getBytes(),"ISO8859-1"));
				response.addHeader("content-type","application/x-msdownload;");
				response.addHeader("content-length",Long.toString(filesize));
				OutputStream output = response.getOutputStream();
				byte[] bytes = new byte[1024];
				int i = 0;    
				while((i=fileInputStream.read(bytes))>0){
					output.write(bytes,0,i);    
				}    
				output.flush();
				output.close();
				fileInputStream.close();
			}
		}	
	}
	
	/*
	 * 	??????????????????
	 * 
	 * */
	@RequestMapping("/findOneDetail.ilf")
	public void findOneDetail(@RequestParam String ruleId,HttpServletResponse response)throws Exception{
		JSONObject jsonObj = new JSONObject();
		//1.??????????????????
		RuleDetail ruleDetail = ruleDetailService.selectModel(Integer.parseInt(ruleId));
		jsonObj.put("tableId",ruleDetail.getBIND_TABLE().toString());
		jsonObj.put("ruleName",ruleDetail.getRULE_NAME());
		jsonObj.put("ruleDesc",ruleDetail.getRULE_DESC());
		jsonObj.put("recovery",ruleDetail.getRESOLVE_METHOD());
		jsonObj.put("conExpress",ruleDetail.getCON_EXPRESSION());
		jsonObj.put("isUsing",ruleDetail.getRULE_STATE());
		jsonObj.put("className",ruleDetail.getCLASS_NAME());
		jsonObj.put("ruleGrade",ruleDetail.getRULE_GRADE());
		jsonObj.put("ruleVersion",ruleDetail.getRULE_VERSION());
		//2.????????????
		RuleQuartz ruleQuartz = ruleQuartzService.selectModelByHql("from RuleQuartz where BELONG_RULE = "+ruleDetail.getID());
		JSONObject quartzObj = new JSONObject();
		quartzObj.put("CIRCLE_TYPE",ruleQuartz.getCIRCLE_TYPE());
		if("???".equals(ruleQuartz.getCIRCLE_TYPE())){
			quartzObj.put("DAY_OF_WEEK",ruleQuartz.getDAY_OF_WEEK());
		}else if("???".equals(ruleQuartz.getCIRCLE_TYPE())){
			quartzObj.put("DAY_OF_MONTH",ruleQuartz.getDAY_OF_MONTH());
		}
		quartzObj.put("HOUR_VAR",ruleQuartz.getHOUR_VAR());
		quartzObj.put("MINUTE_VAR",ruleQuartz.getMINUTE_VAR());
		quartzObj.put("SECOND_VAR",ruleQuartz.getSECOND_VAR());	
		jsonObj.put("quartzDetail",quartzObj);
		//3.????????????
		List<RuleItem> items = ruleItemService.selectModelsByHql("from RuleItem where BELONG_RULE = "+ruleId+" order by AUDIT_TYPE desc,ID ASC");
		JSONArray itemArray = new JSONArray();
		if(items.size()>0){
			for(int j=0;j<items.size();j++){
				RuleItem item = items.get(j);
				JSONObject itemObj = new JSONObject();
				itemObj.put("auditType",item.getAUDIT_TYPE());
				if("???????????????".equals(item.getAUDIT_TYPE())){
					itemObj.put("exportColumns",item.getEXPORT_COLUMNS());
					itemObj.put("auditSql",item.getAUDIT_SQL());
				}else{
					itemObj.put("columnCode",item.getCOLUMN_ID().toString());
					itemObj.put("columnName",basicColumnService.selectModel(item.getCOLUMN_ID()).getCOLUMN_NAME());
					itemObj.put("isFilter","Y".equals(item.getIS_FILTER()));
					itemObj.put("expression",item.getEXPRESSION());
					itemObj.put("expressKey",new Date().getTime());				
					if("??????????????????".equals(item.getAUDIT_TYPE())){
						itemObj.put("tableCode",item.getVALUE_TABLE().toString());
						itemObj.put("glassTableName",basicDbTableService.selectModel(item.getVALUE_TABLE()).getTABLE_ALIAS());
						itemObj.put("valueColumn",item.getVALUE_COLUMN().toString());
						itemObj.put("valueCoName",basicColumnService.selectModel(item.getVALUE_COLUMN()).getCOLUMN_NAME());					
						if("????????????????????????".equals(item.getEXPRESSION())){
							itemObj.put("minNumber",item.getMIN_NUMBER().toString());
							itemObj.put("maxNumber",item.getMAX_NUMBER().toString());
						}else if("???????????????".equals(item.getEXPRESSION())){
							itemObj.put("checkedValues",item.getCOLUMN_IDS());
							itemObj.put("matchType",item.getMATCH_TYPE());
							itemObj.put("dimension",item.getDIMENSION_TYPE());
						}
						//3.1.????????????
						JSONArray conns = new JSONArray();
						List<RuleItemConnect> connects = ruleConnectService.selectModelsByHql("from RuleItemConnect where BELONG_ITEM = "+item.getID());
						if(connects.size()>0){
							for(int w=0;w<connects.size();w++){
								JSONObject connObj = new JSONObject();
								connObj.put("keyOne",connects.get(w).getCHECK_COLUMN_ID().toString());
								connObj.put("keyTwo",connects.get(w).getGLASS_COLUMN_ID().toString());
								connObj.put("backColor",connects.get(w).getBACK_COLOR());
								conns.add(connObj);
							}
						}
						itemObj.put("connectedColumns",conns);
					}
				}				
				itemArray.add(itemObj);
			}			
		}
		jsonObj.put("editedItems",itemArray);
		response.setContentType("application/json; charset=UTF-8");
		response.getWriter().print(jsonObj.toString());
	}
	
	/*
	 * 	??????/??????
	 * 
	 * */
	@RequestMapping("/saveAudit.ilf")
	public void saveAudit(@RequestParam String params,HttpServletRequest request,HttpServletResponse response)throws Exception{
		JSONObject resultObject = JSONObject.fromObject("{success:true,message:'??????????????????.'}");
		Integer newDetailCode = -1;
		try{
			final JSONObject jsonObject = JSONObject.fromObject(params);
			/*
			 * 	??????????????????
			 * 
			 * */
			RuleDetail ruleDetail = new RuleDetail(jsonObject);
			ruleDetail.setCREATE_USER(getLoginUserId(request));
			if(Integer.parseInt(jsonObject.get("ruleId").toString())!=-1){
				RuleDetail oldDetail = ruleDetailService.selectModel(Integer.parseInt(jsonObject.get("ruleId").toString()));
				/*
				 * 	????????????????????????
				 * 
				 * */
				newDetailCode = Integer.parseInt(jsonObject.get("ruleId").toString());				
				ruleDetail.setID(newDetailCode);
				ruleDetail.setLAST_ACTION_TIME(oldDetail.getLAST_ACTION_TIME());
				ruleDetailService.updateModel(ruleDetail);
				/*
				 * 	????????????????????????????????????????????????????????????
				 * 
				 * */
				jdbcTemplate.execute("DELETE FROM RULE_EXEC_QUARTZ WHERE BELONG_RULE = "+newDetailCode);
				jdbcTemplate.execute("DELETE FROM RULE_ITEMS_CONNECT WHERE BELONG_ITEM IN(SELECT ID FROM RULE_ITEMS WHERE BELONG_RULE = "+newDetailCode+")");
				jdbcTemplate.execute("DELETE FROM RULE_ITEMS WHERE BELONG_RULE = "+newDetailCode);
				/*
				 * 	??????????????????
				 * 
				 * */
				if(!"-".equals(oldDetail.getCON_EXPRESSION()) && "Y".equals(oldDetail.getRULE_STATE())){
					quartzService.stopJob(newDetailCode);
				}
			}else{
				newDetailCode = ruleDetailService.insertModel(ruleDetail);
			}	
			//2.??????????????????
			JSONObject quartzObj = jsonObject.getJSONObject("quartzDetail");
			RuleQuartz ruleQuartz = new RuleQuartz(newDetailCode,quartzObj);
			ruleQuartzService.insertModel(ruleQuartz);
			//3.????????????????????????
			JSONArray ruleArray = jsonObject.getJSONArray("editedItems");
			for(int i=0;i<ruleArray.size();i++){
				JSONObject itemObj = ruleArray.getJSONObject(i);				
				RuleItem ruleItem = new RuleItem(itemObj);			
				ruleItem.setBELONG_RULE(newDetailCode);				
				Integer ruleCode = ruleItemService.insertModel(ruleItem);			
				//4.?????????????????????????????????
				if("??????????????????".equals(ruleItem.getAUDIT_TYPE())){
					JSONArray connectedColumns = itemObj.getJSONArray("connectedColumns");
					if(connectedColumns!=null && connectedColumns.size()>0){
						for(int j=0;j<connectedColumns.size();j++){
							JSONObject conObj = connectedColumns.getJSONObject(j);
							RuleItemConnect connect = new RuleItemConnect(conObj);
							connect.setBELONG_ITEM(ruleCode);
							ruleConnectService.insertModel(connect);					
						}
					}
				}
			}
			/*
			 * 	????????????
			 * 
			 * */
			if("Y".equals(ruleDetail.getRULE_STATE()) && !"-".equals(ruleDetail.getCON_EXPRESSION())){
				quartzService.createJob(newDetailCode,ruleDetail.getCON_EXPRESSION());
				System.out.println("===????????????????????????.");
			}
		}catch(Exception e){
			e.printStackTrace();
			resultObject = JSONObject.fromObject("{success:false,message:'????????????,??????????????????.'}");
		}finally{
			response.setContentType("application/json; charset=UTF-8");
			response.getWriter().print(resultObject.toString());
		}
	}
	
	/*
	 * 	??????
	 * 
	 * */
	@RequestMapping("/deleteItem.ilf")
	public void deleteItem(@RequestParam String ruleId,HttpServletResponse response)throws Exception{
		JSONObject resultObject = JSONObject.fromObject("{success:true}");
		try{
			RuleDetail ruleDetail = ruleDetailService.selectModel(Integer.parseInt(ruleId));
			/*
			 * 	QUARTZ
			 * 
			 * */
			if("Y".equals(ruleDetail.getRULE_STATE()) && !"-".equals(ruleDetail.getCON_EXPRESSION())){
				quartzService.stopJob(Integer.parseInt(ruleId));
			}
			/*
			 * 	RULE_JOB_DATA
			 * 
			 * */
			String dataDelete = "";
			dataDelete+="DELETE FROM RULE_JOB_DATA WHERE JOB_ITEM_ID IN(";
			dataDelete+="	SELECT ID AS ITEM_ID FROM RULE_JOB_ITEM WHERE JOB_ID IN(";
			dataDelete+="		SELECT ID AS JOB_ID FROM RULE_JOB WHERE RULE_ID = "+ruleId;
			dataDelete+="	)";
			dataDelete+=")";
			jdbcTemplate.execute(dataDelete);
			/*
			 * 	RULE_JOB_ITEM
			 * 
			 * */
			String itemDelete = "";
			itemDelete+="DELETE FROM RULE_JOB_ITEM WHERE JOB_ID IN(";
			itemDelete+="	SELECT ID AS JOB_ID FROM RULE_JOB WHERE RULE_ID = "+ruleId;
			itemDelete+=")";
			jdbcTemplate.execute(itemDelete);
			/*
			 * 	RULE_JOB
			 * 
			 * */
			jdbcTemplate.execute("DELETE FROM RULE_JOB WHERE RULE_ID = "+ruleId);
			/*
			 * 	RULE_EXEC_QUARTZ
			 * 
			 * */		
			jdbcTemplate.execute("DELETE FROM RULE_EXEC_QUARTZ WHERE BELONG_RULE = "+ruleId);
			/*
			 * 	RULE_HISTORY
			 * 
			 * */
			jdbcTemplate.execute("DELETE FROM RULE_HISTORY WHERE RULE_ID = "+ruleId);
			/*
			 * 	RULE_ITEMS_CONNECT
			 * 
			 * */
			String connectDelete = "";
			connectDelete+="DELETE FROM RULE_ITEMS_CONNECT WHERE BELONG_ITEM IN(";
			connectDelete+="	SELECT ID AS ITEM_ID FROM RULE_ITEMS WHERE BELONG_RULE = "+ruleId;
			connectDelete+=")";
			jdbcTemplate.execute(connectDelete);
			/*
			 * 	RULE_ITEMS
			 * 
			 * */
			jdbcTemplate.execute("DELETE FROM RULE_ITEMS WHERE BELONG_RULE = "+ruleId);
			/*
			 * 	RULE_DETAIL???Report
			 * 
			 * */
			if(ruleDetail!=null && ruleDetail.getFILE_REPORT()!=null && !"".equals(ruleDetail.getFILE_REPORT())){
				File file = new File(
					jdbcTemplate.queryForMap("SELECT * FROM S_SYSTEM_PROPERTY WHERE PROPERTY_NAME = 'reportFolder'").get("PRO_VALUE").toString()+ruleDetail.getFILE_REPORT()
				);
				if(file.exists()){
					file.delete();
				}
			}
			/*
			 * 	RULE_IMPORT
			 * 
			 * */
			jdbcTemplate.execute("DELETE FROM RULE_IMPORT WHERE RULE_ID = "+ruleId);
			/*
			 * 	RULE_DETAIL
			 * 
			 * */
			ruleDetailService.deleteModel(Integer.parseInt(ruleId));
		}catch(Exception e){
			e.printStackTrace();
			resultObject.put("success",false);
		}finally{
			response.setContentType("application/json; charset=UTF-8");
			response.getWriter().print(resultObject.toString());
		}
	}
	
	/*
	 * 	??????????????????????????????Table????????????????????????????????????????????????????????????????????????Table??????.
	 * 
	 * */
	@RequestMapping("/findOneTable.ilf")
	public void findOneTable(@RequestParam String tableId,HttpServletResponse response)throws Exception{
		BasicDbTable basicDbTable = basicDbTableService.selectModel(Integer.parseInt(tableId));
		JSONObject jsonObject = JSONObject.fromObject(basicDbTable);
		jsonObject.put("SUCCESS",true);
		BasicDb basicDb = basicDbService.selectModel(basicDbTable.getBELONG_DB());
		jsonObject.put("DB_NAME",basicDb.getDB_NAME());	
		response.setContentType("application/json; charset=UTF-8");
		response.getWriter().print(JSONObject.fromObject(jsonObject).toString());
	}
	
	/*
	 * 	????????????????????????
	 * 
	 * */
	@RequestMapping("/findTableData.ilf")
	public void findTableData(HttpServletResponse response)throws Exception{
		JSONArray nodes = new JSONArray();	
		List<Map<String,Object>> dbList = jdbcTemplate.queryForList("SELECT ID,DB_NAME FROM BASIC_DB WHERE USE_STATE = 'Y'");
		if(dbList.size()>0){
			for(int i=0;i<dbList.size();i++){
				/*???????????????*/
				Map<String,Object> dbObj = dbList.get(i);
				JSONObject topNode = new JSONObject();
				topNode.put("id","ROOT-"+dbObj.get("ID").toString());
				topNode.put("pId","ROOT-0");
				topNode.put("name",dbObj.get("DB_NAME").toString());
				topNode.put("leaf",false);
				if(i==0){
					topNode.put("open",true);
				}else{
					topNode.put("open",false);
				}
				nodes.add(topNode);			
				/*?????????*/
				List<Map<String,Object>> leafTables = jdbcTemplate.queryForList("SELECT * FROM BASIC_DB_TABLE WHERE BELONG_DB = "+dbObj.get("ID").toString());
				if(leafTables.size()>0){
					for(int w=0;w<leafTables.size();w++){
						JSONObject leafNode = new JSONObject();
						leafNode.put("id",Integer.parseInt(leafTables.get(w).get("ID").toString()));
						leafNode.put("pId","ROOT-"+dbObj.get("ID").toString());
						leafNode.put("leaf",true);
						leafNode.put("name",leafTables.get(w).get("TABLE_ALIAS").toString());
						nodes.add(leafNode);
					}
				}							
			}
		}	
		response.setContentType("application/json; charset=UTF-8");
		response.getWriter().print(nodes.toString());
	}
	
	@RequestMapping("/lineHistoryChart.ilf")
	public void lineHistoryChart(
		@RequestParam String ruleCode,
		HttpServletResponse response
	)throws Exception{
		List<RuleHistory> histories = ruleHistoryService.selectModelsPage("from RuleHistory where RULE_ID = "+ruleCode+" order by EXEC_DATE asc",0,6);
		List<String> columnTitle = new ArrayList<String>();
		List<Integer> columnValue = new ArrayList<Integer>();
		if(histories.size()>0){
			for(int i=0;i<histories.size();i++){
				RuleHistory history = histories.get(i);
				columnTitle.add(history.getDATE_TOKEN());
				columnValue.add(history.getPROBLEM_COUNT());
			}
		}
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("success",true);
		jsonObject.put("titles",columnTitle);
		jsonObject.put("values",columnValue);		
		response.setContentType("application/json; charset=UTF-8");
		response.getWriter().print(JSONObject.fromObject(jsonObject).toString());
	}
	
	@RequestMapping("/barCountChart.ilf")
	public void barCountChart(
		@RequestParam String demoCode,
		HttpServletResponse response
	)throws Exception{
		RuleDetail ruleDetail = ruleDetailService.selectModel(Integer.parseInt(demoCode));
		List<String> columnTitle = new ArrayList<String>();
		List<Integer> columnValue = new ArrayList<Integer>();
		List<Map<String,Object>> ruleList = jdbcTemplate.queryForList("SELECT A.*,ROWNUM AS RN FROM RULE_DETAIL A WHERE A.CLASS_NAME = '"+ruleDetail.getCLASS_NAME()+"' AND ROWNUM <= 10");
		if(ruleList.size()>0){
			for(int i=0;i<ruleList.size();i++){
				Map<String,Object> ruleObject = ruleList.get(i);
				/*
				 * 	??????????????????
				 * 
				 * */
				columnTitle.add(ruleObject.get("RULE_NAME").toString());
				/*
				 * 	?????????????????????????????????
				 * 
				 * */
				String countSql = "";
				countSql+="SELECT COUNT(1) FROM (";
				countSql+="	  SELECT PRIMARY_VALUE,NAME_VALUE,TO_CHAR(WM_CONCAT(PROBLEM_DESC)) AS PROBLEM_DESCRIBE FROM(";
				countSql+="		  SELECT * FROM RULE_JOB_DATA WHERE JOB_ITEM_ID IN(";
				countSql+="			  SELECT ID AS ITEM_ID FROM RULE_JOB_ITEM WHERE JOB_ID IN(";
				countSql+="				  SELECT ID FROM RULE_JOB WHERE RULE_ID = "+ruleObject.get("ID").toString();
				countSql+="			  )";
				countSql+="		  )ORDER BY JOB_ITEM_ID ASC,PRIMARY_VALUE ASC";
				countSql+="	  )GROUP BY PRIMARY_VALUE,NAME_VALUE ORDER BY PRIMARY_VALUE ASC";
				countSql+=")";
				Integer problemNumber = jdbcTemplate.queryForInt(countSql);
				columnValue.add(problemNumber);
			}
		}	
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("success",true);
		jsonObject.put("titles",columnTitle);
		jsonObject.put("values",columnValue);		
		response.setContentType("application/json; charset=UTF-8");
		response.getWriter().print(JSONObject.fromObject(jsonObject).toString());
	}
	
	@SuppressWarnings("unchecked")
	public Integer getLoginUserId(HttpServletRequest request){
		Object loginObj = request.getSession().getAttribute("LoginUserInfo");
		if(loginObj!=null){
			Map<String,Object> userObject = (Map<String,Object>)loginObj;
			return Integer.parseInt(userObject.get("ID").toString());
		}else{
			return null;
		}
	}
}
