package com.function.rules.util;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
public class StyleUtil{
	
	/*
	 * 	1.获取定制的：XSSFCellStyle.
	 * 
	 * */
	public static XSSFCellStyle getStyle(XSSFWorkbook workBook,Short alignPosition,Short backColor){
		XSSFFont font = workBook.createFont();
        font.setFontName("宋体");
        font.setBoldweight(HSSFFont.BOLDWEIGHT_NORMAL);
        font.setFontHeightInPoints((short)10);
        font.setColor((short)16711680);
        XSSFCellStyle style = workBook.createCellStyle();
        style.setFont(font);
        style.setAlignment(alignPosition);
        style.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
        style.setBorderTop(HSSFCellStyle.BORDER_THIN);              
        style.setBorderBottom(HSSFCellStyle.BORDER_THIN);
        style.setBorderLeft(HSSFCellStyle.BORDER_THIN);
        style.setBorderRight(HSSFCellStyle.BORDER_THIN);
        style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);           
        style.setFillForegroundColor(backColor);
        return style;
	}
}
