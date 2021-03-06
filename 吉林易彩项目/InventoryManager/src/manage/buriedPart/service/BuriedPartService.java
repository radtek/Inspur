package manage.buriedPart.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.Region;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.springframework.jdbc.core.JdbcTemplate;

import manage.buried.pojo.BuriedInfoObj;
import manage.buried.service.impl.IburiedService;
import manage.buriedPart.pojo.BuriedPartObj;
import manage.buriedPart.service.impl.IburiedPartService;
import manage.dictionary.service.impl.IdictService;
import base.database.DataBase;
import base.util.ExcelUtil;
import base.util.MapUtil;
import base.util.TextUtil;

public class BuriedPartService extends DataBase implements IburiedPartService {
	private IdictService dictService;
	private IburiedService buriedService;
	private JdbcTemplate jdbcTemplate;
	private static String buriedPartGrid ="buriedPart.getBuriedPartGrid";
	private static String buriedPartCount = "buriedPart.getBuriedPartCount";
	private static String getBuriedPart = "buriedPart.getBuriedPart";
	private static String updateBuriedPart ="buriedPart.updateBuriedPart";
	private static String insertBuriedPart = "buriedPart.insertBuriedPart";
	private static String delBuriedPart = "buriedPart.delBuriedPart";
	
	/**
	 * 分页查询
	 * @param object
	 * @param start
	 * @param length
	 * @return
	 */
	public List<BuriedPartObj> getBuriedPartGrid(BuriedPartObj object){
		if(TextUtil.isNotNull(object.getBuriedPartName())){
			String partName = object.getBuriedPartName();
			if(partName.contains(" ")){
				partName= partName.replaceAll(" +", "%");
			}
			object.setBuriedPartName("%"+partName+"%");
		}
		if(TextUtil.isNotNull(object.getBuriedPartArea()) && object.getBuriedPartArea().contains("*")) {
		     String[] areas = object.getBuriedPartArea().split("\\*");
		     String sql = "";
		     for(String area : areas) {
		       sql +=" instr(buriedPartArea, '"+area+"') > 0 or";
		     }
		     if(TextUtil.isNotNull(sql) && sql.endsWith("or")) {
		       sql = sql.substring(0,sql.length()-2);
		       object.setBuriedPartArea(null);
		       object.setExtendsSql(sql);
		     }
		   }
		List<BuriedPartObj> list = getObjects(buriedPartGrid, object);
		return list;
	}
	
	/**
	 * 得到直埋段信息
	 * @param obj
	 * @return
	 */
	public List<BuriedPartObj> getBuriedPart(BuriedPartObj obj){
		List<BuriedPartObj> list = getObjects(getBuriedPart, obj);
		return list;
	}
	
	
	/**
	 * 删除直埋段
	 * @param ids
	 */
	public void delBuriedPart(String ids){
		this.delete(delBuriedPart, ids);
	}
	
	/**
	 * 更改直埋段信息
	 * @param obj
	 * @return
	 */
	public int updateBuriedPart(BuriedPartObj obj){
		return this.update(updateBuriedPart, obj);
	}
	
	/**
	 * 设置直埋段计算长度
	 * @param obj
	 * @return
	 */
	public BuriedPartObj setBuriedPartLength(BuriedPartObj obj){
		String sql ="select longitude as lon,latitude as lat from"
		  		+ "  stoneinfo where stoneId in ('"+obj.getBuriedPartStartId()+"','"+obj.getBuriedPartEndId()+"')";
		  List<Map<String, Object>> list = this.jdbcTemplate.queryForList(sql);
		  Map<String, Object> start = list.get(0);
		  Map<String, Object> end = list.get(1);
		  String distinct = MapUtil.Distance(Double.parseDouble(start.get("lon")+""),
				  	Double.parseDouble(start.get("lat")+""),
				  	Double.parseDouble(end.get("lon")+""),
				  	Double.parseDouble(end.get("lat")+""));
		  obj.setBuriedPartLength(distinct);
		return obj;
	}
	
	/**
	 * 增加直埋段信息
	 * @param obj
	 * @return
	 */
	public int insertBuriedPart(BuriedPartObj obj){
		return (Integer) this.insert(insertBuriedPart, obj);
	}
	
	/**
	 * 得到数据条数
	 * @param object
	 * @return
	 */
	public int getBuriedPartCount(BuriedPartObj object){
		return getCount(buriedPartCount, object);
	}
	
	/**
	 * 得到直埋段
	 * @param obj
	 * @return
	 */
	public BuriedPartObj getBuriedPartPojo(BuriedPartObj obj){
		return (BuriedPartObj) getObject(getBuriedPart, obj);
	}
	
	/**
	 * 查询敷设信息
	 * @param obj
	 * @return
	 */
	public BuriedPartObj getBuriedlay(BuriedPartObj obj){
		String sql ="";
		if(TextUtil.isNotNull(obj.getResNum())){
			sql = "select distinct cableId,cableName  from opticab_lay "
					+ " where spanId ='"+obj.getResNum()+"' and spanType in(4,9111) and deleteFlag = '0'"
					+ " union all ";
		}
		sql+= "select distinct cableId,cableName  from opticab_lay "
		   + " where spanId ='"+obj.getId()+"' and spanType in (4,9111) and deleteFlag = '0'";
		List<Map<String, Object>> list = this.jdbcTemplate.queryForList(sql);
		String cableName = super.getStrList(list, "cableName");
		String cableId = super.getStrList(list, "cableId");
		obj.setBuriedPartOptical(cableName);
		obj.setBuriedPartOpticalId(cableId);
		return obj;
	}
	
	
	
	/**
	 * 导出数据模板
	 * @param request
	 * @param response
	 */
	public void expTemp(HttpServletRequest request,HttpServletResponse response){
		try{
			String caption = "直埋段数据模板";
			HSSFWorkbook workbook = new HSSFWorkbook();
			HSSFSheet sheet=workbook.createSheet();// 生成一个表格
			sheet.setDefaultColumnWidth(15);
			//创建一个隐藏列
			HSSFSheet hiddensheet=ExcelUtil.createHiddenSheet(workbook,"hiddensheet");
			int k=1;
			//创建表头
			HSSFRow row=sheet.createRow(0);
			HSSFCell cell=row.createCell(0);
			//创建一个字段标题
			int col=0;
			int cur=0;
			cell.setCellStyle(ExcelUtil.getTitleStyle(workbook));
			cell=row.createCell(col++);
		    cell.setCellValue("直埋段名称");
		    cur++;
		    cell=row.createCell(col++);
		    cell.setCellValue("所属直埋");
		    ExcelUtil.setContent(hiddensheet,0,cur,"所属直埋");
		    sheet.addValidationData(ExcelUtil.setHiddenCell(cur, col, "所属直埋", workbook, buriedService.getBuriedMap(new BuriedInfoObj()), hiddensheet));
		    cur++;
		    cell=row.createCell(col++);
		    cell.setCellValue("维护区域");
		    cur++;
		    cell=row.createCell(col++);
		    cell.setCellValue("直埋段长度");
		    cur++;
		    cell=row.createCell(col++);
		    cell.setCellValue("开始设施");
		    cur++;
		    cell=row.createCell(col++);
		    cell.setCellValue("终止设施");
		    cur++;
		    cell=row.createCell(col++);
		    cell.setCellValue("承载光缆段");
		    cur++;
		    cell=row.createCell(col++);
		    cell.setCellValue("产权性质");
		    Map<String, String> map = dictService.getDicMap("propertyNature");
		    ExcelUtil.setContent(hiddensheet,0,cur,"产权性质");
		    sheet.addValidationData(ExcelUtil.setHiddenCell(cur, col, "产权性质", workbook, map, hiddensheet));
		    cur++;
		    cell=row.createCell(col++);
		    cell.setCellValue("产权单位");
		    map = dictService.getDicMap("propertyComp");
		    ExcelUtil.setContent(hiddensheet,0,cur,"产权单位");
		    sheet.addValidationData(ExcelUtil.setHiddenCell(cur, col, "产权单位", workbook, map, hiddensheet));
		    cur++;
		    cell=row.createCell(col++);
		    cell.setCellValue("数据质量责任人");
		    cur++;
		    cell=row.createCell(col++);
		    cell.setCellValue("一线维护人");
		    cur++;
	        
	      //下载附件
	      ExcelUtil.downloadFile(caption, workbook, response);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	
	/**
	 * 导出数据
	 * @param request
	 * @param response
	 */
	public void expData(BuriedPartObj obj ,HttpServletRequest request,HttpServletResponse response){
		try{
			String caption = "直埋段数据";
			HSSFWorkbook workbook = new HSSFWorkbook();
			HSSFSheet sheet=workbook.createSheet();// 生成一个表格
			sheet.setDefaultColumnWidth(15);
			
			List<BuriedPartObj> list = this.getBuriedPart(obj);
			HSSFRow row=sheet.createRow(0);
			HSSFCell cell=row.createCell(0);
			cell.setCellStyle(ExcelUtil.getTitleStyle(workbook));
			int col=0;
			cell=row.createCell(col++);
		    cell.setCellValue("直埋段名称");
		    cell=row.createCell(col++);
		    cell.setCellValue("所属直埋");
		    cell=row.createCell(col++);
		    cell.setCellValue("维护区域");
		    cell=row.createCell(col++);
		    cell.setCellValue("直埋段长度");
		    cell=row.createCell(col++);
		    cell.setCellValue("开始设施");
		    cell=row.createCell(col++);
		    cell.setCellValue("终止设施");
		    cell=row.createCell(col++);
		    cell.setCellValue("承载光缆段");
		    cell=row.createCell(col++);
		    cell.setCellValue("产权性质");
		    cell=row.createCell(col++);
		    cell.setCellValue("产权单位");
		    cell=row.createCell(col++);
		    cell.setCellValue("数据质量责任人");
		    cell=row.createCell(col++);
		    cell.setCellValue("一线维护人");
		    cell=row.createCell(col++);
		    cell.setCellValue("创建时间");
		    cell=row.createCell(col++);
		    cell.setCellValue("创建人");
		    
		    if(TextUtil.isNotNull(list)){
		    	for(int i=0;i<list.size();i++){
		    		BuriedPartObj object = list.get(i);
		    		HSSFRow rows=sheet.createRow(i+1);
		    		ExcelUtil.createCell(rows, 0, TextUtil.isNull(object.getBuriedPartName()) ? " " : object.getBuriedPartName().toString(), ExcelUtil.getValueStyle(workbook));
		    		ExcelUtil.createCell(rows, 1, TextUtil.isNull(object.getBuriedStr()) ? " " : object.getBuriedStr().toString(), ExcelUtil.getValueStyle(workbook));
		    		ExcelUtil.createCell(rows, 2, TextUtil.isNull(object.getBuriedPartArea()) ? " " : object.getBuriedPartArea().toString(), ExcelUtil.getValueStyle(workbook));
		    		ExcelUtil.createCell(rows, 3, TextUtil.isNull(object.getBuriedPartLength()) ? " " : object.getBuriedPartLength().toString(), ExcelUtil.getValueStyle(workbook));
		    		ExcelUtil.createCell(rows, 4, TextUtil.isNull(object.getBuriedPartStart()) ? " " : object.getBuriedPartStart().toString(), ExcelUtil.getValueStyle(workbook));
		    		ExcelUtil.createCell(rows, 5, TextUtil.isNull(object.getBuriedPartEnd()) ? " " : object.getBuriedPartEnd().toString(), ExcelUtil.getValueStyle(workbook));
		    		ExcelUtil.createCell(rows, 6, TextUtil.isNull(object.getBuriedPartOptical()) ? " " : object.getBuriedPartOptical().toString(), ExcelUtil.getValueStyle(workbook));
		    		ExcelUtil.createCell(rows, 7, TextUtil.isNull(object.getPropertyRightStr()) ? " " : object.getPropertyRightStr().toString(), ExcelUtil.getValueStyle(workbook));
		    		ExcelUtil.createCell(rows, 8, TextUtil.isNull(object.getPropertyDeptStr()) ? " " : object.getPropertyDeptStr().toString(), ExcelUtil.getValueStyle(workbook));
		    		ExcelUtil.createCell(rows, 9, TextUtil.isNull(object.getDataQualitier()) ? " " : object.getDataQualitier().toString(), ExcelUtil.getValueStyle(workbook));
		    		ExcelUtil.createCell(rows, 10, TextUtil.isNull(object.getMaintainer()) ? " " : object.getMaintainer().toString(), ExcelUtil.getValueStyle(workbook));
		    		ExcelUtil.createCell(rows, 11, TextUtil.isNull(object.getCreateTime()) ? " " : object.getCreateTime().toString(), ExcelUtil.getValueStyle(workbook));
		    		ExcelUtil.createCell(rows, 12, TextUtil.isNull(object.getCreater()) ? " " : object.getCreater().toString(), ExcelUtil.getValueStyle(workbook));

		    	}
		    }
			
			ExcelUtil.downloadFile(caption, workbook, response);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	/**
	 * 导入数据
	 * @param file
	 * @param fileName
	 * @return
	 */
	public String impData(File file,String fileName){
		int succ=0;
	    String jsonstr="";
	    try{
	    	HSSFWorkbook workBook = this.getWorkbook(file);
			HSSFSheet sheet=workBook.getSheetAt(0);
			int maxRow=sheet.getLastRowNum();
			HSSFRow row=sheet.getRow(1);
			int maxCell=row.getLastCellNum();
			HSSFCell cell=null;
			StringBuffer sb=null;
			String value="";
			NumberFormat nf=NumberFormat.getNumberInstance(Locale.CHINA);
			nf.setGroupingUsed(false);
			nf.setMaximumFractionDigits(0);
			Date date = new Date();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String time=sdf.format(date);
			for(int i = 1; i <= maxRow; i++){
				sb = new StringBuffer("INSERT into buriedPartInfo("
						+ "buriedPartName,buriedId,buriedPartArea,buriedPartLength,buriedPartStart,"
						+ "buriedPartEnd,buriedPartOptical,propertyRight,propertyDept,"
						+ "dataQualitier,maintainer,deleteflag,createTime,creater)"
						+ "VALUES(");
				row=sheet.getRow(i);
				for(int j=0; j<maxCell; j++){
					cell=row.getCell(j);
					switch(cell.getCellType()){
					 case HSSFCell.CELL_TYPE_NUMERIC:value=nf.format(cell.getNumericCellValue());break;
					 case HSSFCell.CELL_TYPE_STRING:value=cell.getStringCellValue(); break;
					 case HSSFCell.CELL_TYPE_BLANK:value=" ";break;
					}
					if(j == 1){
						BuriedInfoObj buried = new BuriedInfoObj();
						buried.setBuriedName(value);
						List<BuriedInfoObj> list = buriedService.getBuried(buried);
						if(TextUtil.isNotNull(list)){
							value = list.get(0).getBuriedId()+"";
						}else{
							value ="0";
						}
					}else if(j==7){
						value = dictService.getDicValue("propertyNature", value);
					}else if(j ==8){
						value = dictService.getDicValue("propertyComp", value);
					}
					
					if(j == maxCell -1){
						sb.append("'"+value+"','0','"+time+"','root'");
					}else{
						sb.append("'"+value+"',");
					}
				}
				sb.append(")");
				this.jdbcTemplate.execute(sb.toString());
			}
	    }catch(Exception e){
	    	e.printStackTrace();
	    }
	    return jsonstr;
	}
	
	
	private HSSFWorkbook getWorkbook(File file){
		FileInputStream fis=null;
		try{
			fis=new FileInputStream(file);//获得流文件
			POIFSFileSystem poifs=new POIFSFileSystem(fis);//解析
			HSSFWorkbook workbook=new HSSFWorkbook(poifs);//HSSFWorkbook读取excel
			
			return workbook;
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}finally{
			if(fis!=null)
				try {
					fis.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
	}
	public IdictService getDictService() {
		return dictService;
	}
	public void setDictService(IdictService dictService) {
		this.dictService = dictService;
	}
	public IburiedService getBuriedService() {
		return buriedService;
	}
	public void setBuriedService(IburiedService buriedService) {
		this.buriedService = buriedService;
	}
	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}
	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}
}
