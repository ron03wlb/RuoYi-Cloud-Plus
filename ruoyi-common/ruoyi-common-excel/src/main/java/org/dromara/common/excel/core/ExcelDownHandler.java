package org.dromara.common.excel.core;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.EnumUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.idev.excel.metadata.FieldCache;
import cn.idev.excel.metadata.FieldWrapper;
import cn.idev.excel.util.ClassUtils;
import cn.idev.excel.write.handler.SheetWriteHandler;
import cn.idev.excel.write.metadata.holder.WriteSheetHolder;
import cn.idev.excel.write.metadata.holder.WriteWorkbookHolder;
import java.lang.reflect.Field;
import java.util.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.ss.util.WorkbookUtil;
import org.apache.poi.xssf.usermodel.XSSFDataValidation;
import org.dromara.common.core.exception.ServiceException;
import org.dromara.common.core.service.DictService;
import org.dromara.common.core.utils.SpringUtils;
import org.dromara.common.core.utils.StreamUtils;
import org.dromara.common.core.utils.StringUtils;
import org.dromara.common.excel.annotation.ExcelDictFormat;
import org.dromara.common.excel.annotation.ExcelEnumFormat;

/**
 * Excel dropdown handler for creating data validation with dropdown lists.
 *
 * <p>This handler supports both simple dropdowns and cascading dropdowns. To prevent Excel from
 * opening slowly, validation is only applied to the first 1000 rows. Data beyond 1000 rows should
 * be exported in a second batch.
 *
 * @author Emil.Zhang
 */
@Slf4j
public class ExcelDownHandler implements SheetWriteHandler {

  /** Excel column name letters used for column reference parsing (DO NOT MODIFY). */
  private static final String EXCEL_COLUMN_NAME = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

  /** Sheet name for single dropdown data. */
  private static final String OPTIONS_SHEET_NAME = "options";

  /** Sheet name prefix for linked dropdown data. */
  private static final String LINKED_OPTIONS_SHEET_NAME = "linkedOptions";

  /** List of dropdown options configurations. */
  private final List<DropDownOptions> dropDownOptions;

  /** Dictionary service for retrieving dropdown values. */
  private final DictService dictService;

  /** Current progress index for single dropdown columns. */
  private int currentOptionsColumnIndex;

  /** Current progress index for linked dropdown sheets. */
  private int currentLinkedOptionsSheetIndex;

  /**
   * Constructs an ExcelDownHandler with the specified dropdown options.
   *
   * @param options list of dropdown option configurations
   */
  public ExcelDownHandler(List<DropDownOptions> options) {
    this.dropDownOptions = options;
    this.currentOptionsColumnIndex = 0;
    this.currentLinkedOptionsSheetIndex = 0;
    this.dictService = SpringUtils.getBean(DictService.class);
  }

  /**
   *
   *
   * <h2>开始创建下拉数据</h2>
   *
   * <p>1.通过解析传入的@ExcelProperty同级是否标注有@DropDown选项 如果有且设置了value值，则将其直接置为下拉可选项
   *
   * <p>2.或者在调用ExcelUtil时指定了可选项，将依据传入的可选项做下拉
   *
   * <p>3.二者并存，注意调用方式
   */
  @Override
  public void afterSheetCreate(
      WriteWorkbookHolder writeWorkbookHolder, WriteSheetHolder writeSheetHolder) {
    Sheet sheet = writeSheetHolder.getSheet();
    // 开始设置下拉框 HSSFWorkbook
    DataValidationHelper helper = sheet.getDataValidationHelper();
    Workbook workbook = writeWorkbookHolder.getWorkbook();
    FieldCache fieldCache =
        ClassUtils.declaredFields(writeWorkbookHolder.getClazz(), writeWorkbookHolder);
    for (Map.Entry<Integer, FieldWrapper> entry : fieldCache.getSortedFieldMap().entrySet()) {
      Integer index = entry.getKey();
      FieldWrapper wrapper = entry.getValue();
      Field field = wrapper.getField();
      // 循环实体中的每个属性
      // 可选的下拉值
      List<String> options = new ArrayList<>();
      if (field.isAnnotationPresent(ExcelDictFormat.class)) {
        // 如果指定了@ExcelDictFormat，则使用字典的逻辑
        ExcelDictFormat format = field.getDeclaredAnnotation(ExcelDictFormat.class);
        String dictType = format.dictType();
        String converterExp = format.readConverterExp();
        if (StringUtils.isNotBlank(dictType)) {
          // 如果传递了字典名，则依据字典建立下拉
          Collection<String> values =
              Optional.ofNullable(dictService.getAllDictByDictType(dictType))
                  .orElseThrow(() -> new ServiceException("字典 {} 不存在", dictType))
                  .values();
          options = new ArrayList<>(values);
        } else if (StringUtils.isNotBlank(converterExp)) {
          // 如果指定了确切的值，则直接解析确切的值
          List<String> strList = StringUtils.splitList(converterExp, format.separator());
          options = StreamUtils.toList(strList, s -> StringUtils.split(s, "=")[1]);
        }
      } else if (field.isAnnotationPresent(ExcelEnumFormat.class)) {
        // 否则如果指定了@ExcelEnumFormat，则使用枚举的逻辑
        ExcelEnumFormat format = field.getDeclaredAnnotation(ExcelEnumFormat.class);
        List<Object> values = EnumUtil.getFieldValues(format.enumClass(), format.textField());
        options = StreamUtils.toList(values, Convert::toStr);
      }
      if (ObjectUtil.isNotEmpty(options)) {
        // 仅当下拉可选项不为空时执行
        if (options.size() > 20) {
          // 这里限制如果可选项大于20，则使用额外表形式
          dropDownWithSheet(helper, workbook, sheet, index, options);
        } else {
          // 否则使用固定值形式
          dropDownWithSimple(helper, sheet, index, options);
        }
      }
    }
    if (CollUtil.isEmpty(dropDownOptions)) {
      return;
    }
    dropDownOptions.forEach(
        everyOptions -> {
          // 如果传递了下拉框选择器参数
          if (!everyOptions.getNextOptions().isEmpty()) {
            // 当二级选项不为空时，使用额外关联表的形式
            dropDownLinkedOptions(helper, workbook, sheet, everyOptions);
          } else if (everyOptions.getOptions().size() > 10) {
            // 当一级选项参数个数大于10，使用额外表的形式
            dropDownWithSheet(
                helper, workbook, sheet, everyOptions.getIndex(), everyOptions.getOptions());
          } else {
            // 否则使用默认形式
            dropDownWithSimple(helper, sheet, everyOptions.getIndex(), everyOptions.getOptions());
          }
        });
  }

  /**
   * Creates a simple dropdown list by directly using explicit constraint values.
   *
   * @param helper the data validation helper
   * @param sheet the target sheet
   * @param celIndex the column index
   * @param value the list of dropdown values
   */
  private void dropDownWithSimple(
      DataValidationHelper helper, Sheet sheet, Integer celIndex, List<String> value) {
    if (ObjectUtil.isEmpty(value)) {
      return;
    }
    this.markOptionsToSheet(
        helper,
        sheet,
        celIndex,
        helper.createExplicitListConstraint(ArrayUtil.toArray(value, String.class)));
  }

  /**
   * Creates cascading dropdown lists using a hidden sheet to store options.
   *
   * @param helper the data validation helper
   * @param workbook the workbook
   * @param sheet the target sheet
   * @param options the dropdown options configuration with cascading data
   */
  private void dropDownLinkedOptions(
      DataValidationHelper helper, Workbook workbook, Sheet sheet, DropDownOptions options) {
    String linkedOptionsSheetName =
        String.format("%s_%d", LINKED_OPTIONS_SHEET_NAME, currentLinkedOptionsSheetIndex);
    // 创建联动下拉数据表
    Sheet linkedOptionsDataSheet =
        workbook.createSheet(WorkbookUtil.createSafeSheetName(linkedOptionsSheetName));
    // 将下拉表隐藏
    workbook.setSheetHidden(workbook.getSheetIndex(linkedOptionsDataSheet), true);
    // 选项数据
    List<String> firstOptions = options.getOptions();
    Map<String, List<String>> secoundOptionsMap = options.getNextOptions();

    // 采用按行填充数据的方式，避免出现数据无法写入的问题
    // Attempting to write a row in the range that is already written to disk

    // 使用ArrayList记载数据，防止乱序
    List<String> columnNames = new ArrayList<>();
    // 写入第一行，即第一级的数据
    Row firstRow = linkedOptionsDataSheet.createRow(0);
    for (int columnIndex = 0; columnIndex < firstOptions.size(); columnIndex++) {
      String columnName = firstOptions.get(columnIndex);
      firstRow.createCell(columnIndex).setCellValue(columnName);
      columnNames.add(columnName);
    }

    // 创建名称管理器
    Name name = workbook.createName();
    // 设置名称管理器的别名
    name.setNameName(linkedOptionsSheetName);
    // 以横向第一行创建一级下拉拼接引用位置
    String firstOptionsFunction =
        String.format(
            "%s!$%s$1:$%s$1",
            linkedOptionsSheetName, getExcelColumnName(0), getExcelColumnName(firstOptions.size()));
    // 设置名称管理器的引用位置
    name.setRefersToFormula(firstOptionsFunction);
    // 设置数据校验为序列模式，引用的是名称管理器中的别名
    this.markOptionsToSheet(
        helper,
        sheet,
        options.getIndex(),
        helper.createFormulaListConstraint(linkedOptionsSheetName));

    // 创建二级选项的名称管理器
    for (int columIndex = 0; columIndex < columnNames.size(); columIndex++) {
      // 列名
      String firstOptionsColumnName = getExcelColumnName(columIndex);
      // 对应的一级值
      String thisFirstOptionsValue = columnNames.get(columIndex);

      // 以该一级选项值创建子名称管理器
      Name sonName = workbook.createName();
      // 设置名称管理器的别名
      sonName.setNameName(thisFirstOptionsValue);
      // 以第二行该列数据拼接引用位置
      String sonFunction =
          String.format(
              "%s!$%s$2:$%s$%d",
              linkedOptionsSheetName,
              firstOptionsColumnName,
              firstOptionsColumnName,
              // 二级选项存在则设置为(选项个数+1)行，否则设置为2行
              Math.max(
                      Optional.ofNullable(secoundOptionsMap.get(thisFirstOptionsValue))
                          .orElseGet(ArrayList::new)
                          .size(),
                      1)
                  + 1);
      // 设置名称管理器的引用位置
      sonName.setRefersToFormula(sonFunction);
      // 数据验证为序列模式，引用到每一个主表中的二级选项位置
      // 创建子项的名称管理器，只是为了使得Excel可以识别到数据
      String mainSheetFirstOptionsColumnName = getExcelColumnName(options.getIndex());
      for (int i = 0; i < 100; i++) {
        // 以一级选项对应的主体所在位置创建二级下拉
        String secondOptionsFunction =
            String.format("=INDIRECT(%s%d)", mainSheetFirstOptionsColumnName, i + 1);
        // 二级只能主表每一行的每一列添加二级校验
        markLinkedOptionsToSheet(
            helper,
            sheet,
            i,
            options.getNextIndex(),
            helper.createFormulaListConstraint(secondOptionsFunction));
      }
    }

    // 将二级数据处理为按行区分
    Map<Integer, List<String>> columnValueMap = new HashMap<>();
    int currentRow = 1;
    while (currentRow >= 0) {
      boolean flag = false;
      List<String> rowData = new ArrayList<>();
      for (String columnName : columnNames) {
        List<String> data = secoundOptionsMap.get(columnName);
        if (CollUtil.isEmpty(data)) {
          // 添加空字符串填充位置
          rowData.add(" ");
          continue;
        }
        // 取第一个
        String str = data.get(0);
        rowData.add(str);
        // 通过移除的方式避免重复
        data.remove(0);
        // 设置可以继续
        flag = true;
      }
      columnValueMap.put(currentRow, rowData);
      // 可以继续，则增加行数，否则置为负数跳出循环
      if (flag) {
        currentRow++;
      } else {
        currentRow = -1;
      }
    }

    // 填充第二级选项数据
    columnValueMap.forEach(
        (rowIndex, rowValues) -> {
          Row row = linkedOptionsDataSheet.createRow(rowIndex);
          for (int columnIndex = 0; columnIndex < rowValues.size(); columnIndex++) {
            String rowValue = rowValues.get(columnIndex);
            // 填充位置的部分不渲染
            if (StrUtil.isNotBlank(rowValue)) {
              row.createCell(columnIndex).setCellValue(rowValue);
            }
          }
        });

    currentLinkedOptionsSheetIndex++;
  }

  /**
   * Creates a dropdown list using a hidden sheet to store options for better performance.
   *
   * <p>When there are many dropdown values, using a hidden sheet improves Excel opening speed.
   *
   * @param helper the data validation helper
   * @param workbook the workbook
   * @param sheet the target sheet
   * @param celIndex the column index
   * @param value the list of dropdown values
   */
  private void dropDownWithSheet(
      DataValidationHelper helper,
      Workbook workbook,
      Sheet sheet,
      Integer celIndex,
      List<String> value) {
    // 由于poi的写出相关问题，超过100个会被临时写进硬盘，导致后续内存合并会出Attempting to write a row[] in the range [] that is
    // already written to disk
    String tmpOptionsSheetName = OPTIONS_SHEET_NAME + "_" + currentOptionsColumnIndex;
    // 创建下拉数据表
    Sheet simpleDataSheet =
        Optional.ofNullable(
                workbook.getSheet(WorkbookUtil.createSafeSheetName(tmpOptionsSheetName)))
            .orElseGet(
                () -> workbook.createSheet(WorkbookUtil.createSafeSheetName(tmpOptionsSheetName)));
    // 将下拉表隐藏
    workbook.setSheetHidden(workbook.getSheetIndex(simpleDataSheet), true);
    // 完善纵向的一级选项数据表
    for (int i = 0; i < value.size(); i++) {
      int finalI = i;
      // 获取每一选项行，如果没有则创建
      Row row =
          Optional.ofNullable(simpleDataSheet.getRow(i))
              .orElseGet(() -> simpleDataSheet.createRow(finalI));
      // 获取本级选项对应的选项列，如果没有则创建。上述采用多个sheet,默认索引为1列
      Cell cell = Optional.ofNullable(row.getCell(0)).orElseGet(() -> row.createCell(0));
      // 设置值
      cell.setCellValue(value.get(i));
    }

    // 创建名称管理器
    Name name = workbook.createName();
    // 设置名称管理器的别名
    String nameName = String.format("%s_%d", tmpOptionsSheetName, celIndex);
    name.setNameName(nameName);
    // 以纵向第一列创建一级下拉拼接引用位置
    String function =
        String.format(
            "%s!$%s$1:$%s$%d",
            tmpOptionsSheetName, getExcelColumnName(0), getExcelColumnName(0), value.size());
    // 设置名称管理器的引用位置
    name.setRefersToFormula(function);
    // 设置数据校验为序列模式，引用的是名称管理器中的别名
    this.markOptionsToSheet(helper, sheet, celIndex, helper.createFormulaListConstraint(nameName));
    currentOptionsColumnIndex++;
  }

  /**
   * Applies dropdown validation to a column (first-level options only).
   *
   * @param helper the data validation helper
   * @param sheet the target sheet
   * @param celIndex the column index
   * @param constraint the validation constraint
   */
  private void markOptionsToSheet(
      DataValidationHelper helper,
      Sheet sheet,
      Integer celIndex,
      DataValidationConstraint constraint) {
    // 设置数据有效性加载在哪个单元格上,四个参数分别是：起始行、终止行、起始列、终止列
    CellRangeAddressList addressList = new CellRangeAddressList(1, 1000, celIndex, celIndex);
    markDataValidationToSheet(helper, sheet, constraint, addressList);
  }

  /**
   * Applies dropdown validation to a specific cell (second-level options only).
   *
   * @param helper the data validation helper
   * @param sheet the target sheet
   * @param rowIndex the row index
   * @param celIndex the column index
   * @param constraint the validation constraint
   */
  private void markLinkedOptionsToSheet(
      DataValidationHelper helper,
      Sheet sheet,
      Integer rowIndex,
      Integer celIndex,
      DataValidationConstraint constraint) {
    // 设置数据有效性加载在哪个单元格上,四个参数分别是：起始行、终止行、起始列、终止列
    CellRangeAddressList addressList =
        new CellRangeAddressList(rowIndex, rowIndex, celIndex, celIndex);
    markDataValidationToSheet(helper, sheet, constraint, addressList);
  }

  /**
   * Applies data validation to the specified cell range.
   *
   * @param helper the data validation helper
   * @param sheet the target sheet
   * @param constraint the validation constraint
   * @param addressList the cell range address list
   */
  private void markDataValidationToSheet(
      DataValidationHelper helper,
      Sheet sheet,
      DataValidationConstraint constraint,
      CellRangeAddressList addressList) {
    // 数据有效性对象
    DataValidation dataValidation = helper.createValidation(constraint, addressList);
    // 处理Excel兼容性问题
    if (dataValidation instanceof XSSFDataValidation) {
      // 数据校验
      dataValidation.setSuppressDropDownArrow(true);
      // 错误提示
      dataValidation.setErrorStyle(DataValidation.ErrorStyle.STOP);
      dataValidation.createErrorBox("提示", "此值与单元格定义数据不一致");
      dataValidation.setShowErrorBox(true);
      // 选定提示
      dataValidation.createPromptBox("填写说明：", "填写内容只能为下拉中数据，其他数据将导致导入失败");
      dataValidation.setShowPromptBox(true);
      sheet.addValidationData(dataValidation);
    } else {
      dataValidation.setSuppressDropDownArrow(false);
    }
    sheet.addValidationData(dataValidation);
  }

  /**
   * Converts a column index to Excel column name (e.g., 0->A, 26->AA, 27->AB).
   *
   * @param columnIndex the zero-based column index
   * @return the Excel column name
   */
  private String getExcelColumnName(int columnIndex) {
    // 26一循环的次数
    int columnCircleCount = columnIndex / 26;
    // 26一循环内的位置
    int thisCircleColumnIndex = columnIndex % 26;
    // 26一循环的次数大于0，则视为栏名至少两位
    String columnPrefix =
        columnCircleCount == 0
            ? StrUtil.EMPTY
            : StrUtil.subWithLength(EXCEL_COLUMN_NAME, columnCircleCount - 1, 1);
    // 从26一循环内取对应的栏位名
    String columnNext = StrUtil.subWithLength(EXCEL_COLUMN_NAME, thisCircleColumnIndex, 1);
    // 将二者拼接即为最终的栏位名
    return columnPrefix + columnNext;
  }
}
