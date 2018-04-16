package net.mrliuli.utils;

import net.mrliuli.annotation.Excel;
import net.mrliuli.exception.ExcelException;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class ExcelUtil {

	/**
	 * 转换Sheet
	 * @param file
	 * @param sheetName
	 * @param type
	 * @param <T>
	 * @return
	 * @throws ExcelException
	 */
	public static <T> List<T> importSheet(File file, String sheetName, Class<T> type) throws ExcelException{

		try {
			InputStream is = new FileInputStream(file);
			Workbook xssfWorkbook = new HSSFWorkbook(is);
			is.close();

			return importSheet(xssfWorkbook.getSheet(sheetName), 0, type);

		} catch (Exception e) {

			e.printStackTrace();
			throw new ExcelException("转换excel文件失败：" + e.getMessage());

		}

	}

	/**
	 * 转换Sheet
	 * @param sheet 要转换的Sheet
	 * @param headerRowNum  列头所在行号，从0开始计第一行
	 * @return
	 */
	public static <T> List<T> importSheet(Sheet sheet, int headerRowNum, Class<T> type) throws ExcelException {
		return importSheet(sheet, headerRowNum, type, false);
	}

	/**
	 * 转换Sheet
	 * @param sheet 要转换的Sheet
	 * @param headerRowNum 列头所在行号，从0开始计第一行
	 * @param ignoreBlankRowAndBelows 导入Sheet时，是否忽略空白行及以下行数据
	 * @return
	 */
	public static <T> List<T> importSheet(Sheet sheet, int headerRowNum, Class<T> type, boolean ignoreBlankRowAndBelows) throws ExcelException{

		if(sheet == null || headerRowNum < 0 || type == null){
			throw new ExcelException("导入Sheet参数错误");
		}

		Row headerRow = sheet.getRow(headerRowNum);

		Method[] methods = new Method[headerRow.getLastCellNum()];

		Field[] fields = type.getDeclaredFields();

		for(int i = 0; i < headerRow.getLastCellNum(); i++){
			String headerName = headerRow.getCell(i) == null ? "" : headerRow.getCell(i).toString();
			for(Field field : fields){
				if(field.getAnnotation(Excel.class) != null && field.getAnnotation(Excel.class).column().equals(headerName.trim())){
					try {
						methods[i] = type.getMethod("set" + field.getName().substring(0, 1).toUpperCase() + field.getName().substring(1, field.getName().length()), field.getType());
					}catch (NoSuchMethodException e){
						throw new ExcelException(e.getMessage());
					}
				}
			}
		}

		List<T> list = new ArrayList<>();

		for(int i = headerRowNum + 1; i <= sheet.getLastRowNum(); i++){
			try {

				Row row = sheet.getRow(i);

				boolean blankRow = true;

				T item = type.newInstance();

				if(row != null){
					for(int j = 0; j < headerRow.getLastCellNum(); j++){
						if(methods[j] != null){
							methods[j].invoke(item, getValue(row.getCell(j)));
						}
						blankRow = blankRow && (getValue(row.getCell(j)) == null || getValue(row.getCell(j)).equals(""));
					}
				}

				if(blankRow){
					if(ignoreBlankRowAndBelows){
						break;
					}else{
						continue;
					}
				}

				list.add(item);

			}catch (InstantiationException e){
				throw new ExcelException(e.getMessage());
			}catch (IllegalAccessException e){
				throw new ExcelException(e.getMessage());
			}catch (InvocationTargetException e){
				throw new ExcelException(e.getMessage());
			}
		}

		return list;
	}

	private static String getValue(Cell cell) {
		if (null == cell) {
			return "";
		} else if (cell.getCellTypeEnum() == CellType.BOOLEAN) {
			// 返回布尔类型的值
			return String.valueOf(cell.getBooleanCellValue());
		} else if (cell.getCellTypeEnum() == CellType.NUMERIC) {
			// 返回数值类型的值
			return String.valueOf(cell.getNumericCellValue());
		} else {
			// 返回字符串类型的值
			return String.valueOf(cell.getStringCellValue());
		}
	}
}
