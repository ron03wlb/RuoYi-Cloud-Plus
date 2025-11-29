package org.dromara.common.excel.handler;

import cn.hutool.core.collection.CollUtil;
import cn.idev.excel.annotation.ExcelProperty;
import cn.idev.excel.metadata.data.DataFormatData;
import cn.idev.excel.metadata.data.WriteCellData;
import cn.idev.excel.util.StyleUtil;
import cn.idev.excel.write.handler.CellWriteHandler;
import cn.idev.excel.write.handler.SheetWriteHandler;
import cn.idev.excel.write.handler.context.CellWriteHandlerContext;
import cn.idev.excel.write.metadata.holder.WriteSheetHolder;
import cn.idev.excel.write.metadata.style.WriteCellStyle;
import cn.idev.excel.write.metadata.style.WriteFont;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFClientAnchor;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.dromara.common.excel.annotation.ExcelNotation;
import org.dromara.common.excel.annotation.ExcelRequired;

/**
 * Excel data write handler for processing cell annotations and required field styling.
 *
 * @author guzhouyanyu
 */
public class DataWriteHandler implements SheetWriteHandler, CellWriteHandler {

  /** Map of field names to annotation content. */
  private final Map<String, String> notationMap;

  /** Map of header column names to font colors for required fields. */
  private final Map<String, Short> headColumnMap;

  /**
   * Constructs a DataWriteHandler with the specified class.
   *
   * @param clazz the class containing Excel annotations
   */
  public DataWriteHandler(Class<?> clazz) {
    notationMap = getNotationMap(clazz);
    headColumnMap = getRequiredMap(clazz);
  }

  @Override
  public void afterCellDispose(CellWriteHandlerContext context) {
    if (CollUtil.isEmpty(notationMap) && CollUtil.isEmpty(headColumnMap)) {
      return;
    }
    // 第一行
    WriteCellData<?> cellData = context.getFirstCellData();
    // 第一个格子
    WriteCellStyle writeCellStyle = cellData.getOrCreateStyle();

    if (context.getHead()) {
      DataFormatData dataFormatData = new DataFormatData();
      // 单元格设置为文本格式
      dataFormatData.setIndex((short) 49);
      writeCellStyle.setDataFormatData(dataFormatData);
      Cell cell = context.getCell();
      WriteSheetHolder writeSheetHolder = context.getWriteSheetHolder();
      Sheet sheet = writeSheetHolder.getSheet();
      Workbook workbook = writeSheetHolder.getSheet().getWorkbook();
      Drawing<?> drawing = sheet.createDrawingPatriarch();
      // 设置标题字体样式
      WriteFont headWriteFont = new WriteFont();
      // 加粗
      headWriteFont.setBold(true);
      if (CollUtil.isNotEmpty(headColumnMap)
          && headColumnMap.containsKey(cell.getStringCellValue())) {
        // 设置字体颜色
        headWriteFont.setColor(headColumnMap.get(cell.getStringCellValue()));
      }
      writeCellStyle.setWriteFont(headWriteFont);
      CellStyle cellStyle = StyleUtil.buildCellStyle(workbook, null, writeCellStyle);
      cell.setCellStyle(cellStyle);

      if (CollUtil.isNotEmpty(notationMap) && notationMap.containsKey(cell.getStringCellValue())) {
        // 批注内容
        String notationContext = notationMap.get(cell.getStringCellValue());
        // 创建绘图对象
        Comment comment =
            drawing.createCellComment(
                new XSSFClientAnchor(0, 0, 0, 0, (short) cell.getColumnIndex(), 0, (short) 5, 5));
        comment.setString(new XSSFRichTextString(notationContext));
        cell.setCellComment(comment);
      }
    }
  }

  /**
   * Retrieves the map of required fields with their font colors.
   *
   * @param clazz the class to extract required field information from
   * @return map of header column names to font color indices
   */
  private static Map<String, Short> getRequiredMap(Class<?> clazz) {
    Map<String, Short> requiredMap = new HashMap<>();
    Field[] fields = clazz.getDeclaredFields();
    for (Field field : fields) {
      if (!field.isAnnotationPresent(ExcelRequired.class)) {
        continue;
      }
      ExcelRequired excelRequired = field.getAnnotation(ExcelRequired.class);
      ExcelProperty excelProperty = field.getAnnotation(ExcelProperty.class);
      requiredMap.put(excelProperty.value()[0], excelRequired.fontColor().getIndex());
    }
    return requiredMap;
  }

  /**
   * Retrieves the map of field annotations (comments).
   *
   * @param clazz the class to extract annotation information from
   * @return map of header column names to annotation content
   */
  private static Map<String, String> getNotationMap(Class<?> clazz) {
    Map<String, String> notationMap = new HashMap<>();
    Field[] fields = clazz.getDeclaredFields();
    for (Field field : fields) {
      if (!field.isAnnotationPresent(ExcelNotation.class)) {
        continue;
      }
      ExcelNotation excelNotation = field.getAnnotation(ExcelNotation.class);
      ExcelProperty excelProperty = field.getAnnotation(ExcelProperty.class);
      notationMap.put(excelProperty.value()[0], excelNotation.value());
    }
    return notationMap;
  }
}
