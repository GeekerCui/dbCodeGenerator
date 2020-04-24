package com.github.geekercui.generator.service;

import com.github.geekercui.generator.dao.GeneratorDao;
import com.github.geekercui.generator.entity.ColumnEntity;
import com.github.geekercui.generator.entity.TableEntity;
import com.github.geekercui.generator.utils.PageUtils;
import com.github.geekercui.generator.utils.Query;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.poi.xwpf.model.XWPFHeaderFooterPolicy;
import org.apache.poi.xwpf.usermodel.*;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.*;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.*;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
@Slf4j
public class SysGeneratorService {

    @Resource
    private GeneratorDao generatorDao;

    public List<TableEntity> queryAllTableList(String wildTableName) {
        return generatorDao.queryList(wildTableName);
    }
    public PageUtils queryList(Query query,String wildTableName) {
        Page<?> page = PageHelper.startPage(query.getPage(), query.getLimit());
        List<TableEntity> list = generatorDao.queryList(wildTableName);

        return new PageUtils(list, (int) page.getTotal(), query.getLimit(), query.getPage());
    }

    public TableEntity queryTable(String tableName) {
        return generatorDao.queryTable(tableName);
    }

    public List<ColumnEntity> queryColumns(String tableName) {
        return generatorDao.queryColumns(tableName);
    }


    public boolean generatorDbDoc(String wildTableName,String docPath) {
        // 1、根据匹配查询出所有匹配的表信息
        List<TableEntity> tableList = queryAllTableList(wildTableName);
       
        // 2、跟表信息查询出所有的列信息

        // key-->表名  value-->列集合
        Map<String,List<ColumnEntity>> tableColumnMap = new HashMap<>(tableList.size());

        for (TableEntity tableEntity : tableList) {
            String tableName = tableEntity.getName();
            
            // 3、取出这个表里的所有列信息
            List<ColumnEntity> columnList = queryColumns(tableName);
            tableColumnMap.put(tableName,columnList);
        }
        
        // 4、根据数据库信息生成word文档
        try {
            boolean wordOk = generatorDbDoc(tableList,tableColumnMap,docPath);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            log.error("",e);
        }
        return true;
    }

    private boolean generatorDbDoc(List<TableEntity> tableList, Map<String, List<ColumnEntity>> tableColumnMap, String docPath) throws FileNotFoundException {
        FileOutputStream out = null;
        try {
            //Blank Document
            XWPFDocument document= new XWPFDocument();

            out = new FileOutputStream(new File(docPath));  // 下载路径/文件名称

            // 生成word里的标题
            generateDocTitle(document);

            for (TableEntity tableEntity : tableList) {
                // 每个表生成一个表格
                generateDocTable(document,tableEntity,tableColumnMap.get(tableEntity.getName()));
            }

            // 生成页眉
            generateDocHeader(document);

            document.write(out);
        } catch (Exception e) {
            log.error("",e);
        } finally {
            IOUtils.closeQuietly(out);
        }


        return true;
    }

    private void generateDocHeader(XWPFDocument document) {
        CTSectPr sectPr = document.getDocument().getBody().addNewSectPr();
        XWPFHeaderFooterPolicy policy = new XWPFHeaderFooterPolicy(document, sectPr);

        //添加页眉
        CTP ctpHeader = CTP.Factory.newInstance();
        CTR ctrHeader = ctpHeader.addNewR();
        CTText ctHeader = ctrHeader.addNewT();
        String headerText = "这是自动生成的数据字典，请勿手动修改！";
        ctHeader.setStringValue(headerText);
        XWPFParagraph headerParagraph = new XWPFParagraph(ctpHeader, document);
        //设置为右对齐
        headerParagraph.setAlignment(ParagraphAlignment.RIGHT);
        XWPFParagraph[] parsHeader = new XWPFParagraph[1];
        parsHeader[0] = headerParagraph;
        policy.createHeader(XWPFHeaderFooterPolicy.DEFAULT, parsHeader);
        //添加页脚
        CTP ctpFooter = CTP.Factory.newInstance();
        CTR ctrFooter = ctpFooter.addNewR();
        CTText ctFooter = ctrFooter.addNewT();
        String footerText = "https://github.com/GeekerCui";
        ctFooter.setStringValue(footerText);
        XWPFParagraph footerParagraph = new XWPFParagraph(ctpFooter, document);
        headerParagraph.setAlignment(ParagraphAlignment.CENTER);
        XWPFParagraph[] parsFooter = new XWPFParagraph[1];
        parsFooter[0] = footerParagraph;
        policy.createFooter(XWPFHeaderFooterPolicy.DEFAULT, parsFooter);
    }

    private void generateDocTable(XWPFDocument document,TableEntity tableEntity, List<ColumnEntity> columnEntities) {

        this.generateTableInfo(document,tableEntity);

        // 数据库表格
        XWPFTable comTable = document.createTable();

        //列宽自动分割
        CTTblWidth comTableWidth = comTable.getCTTbl().addNewTblPr().addNewTblW();
        comTableWidth.setType(STTblWidth.DXA);
        comTableWidth.setW(BigInteger.valueOf(9072));


        generateDocTableHeader(comTable);

        generateDocTableContent(comTable,tableEntity,columnEntities);

        generateCRLF(document);
    }

    private void generateDocTableContent(XWPFTable comTable, TableEntity tableEntity, List<ColumnEntity> columnEntities) {
        if(null == tableEntity || null == columnEntities ) return;
        for (int i = 0; i < columnEntities.size(); i++) {
            XWPFTableRow comTableRowTwo = comTable.createRow();
            comTableRowTwo.getCell(0).setText(String.valueOf(columnEntities.get(i).getName()));
            comTableRowTwo.getCell(1).setText(String.valueOf(columnEntities.get(i).getType()));
            comTableRowTwo.getCell(2).setText(String.valueOf(columnEntities.get(i).getComment()));
            comTableRowTwo.getCell(3).setText(String.valueOf(columnEntities.get(i).getDefaultValue()));
            comTableRowTwo.getCell(4).setText(String.valueOf(columnEntities.get(i).getKey()));
            comTableRowTwo.getCell(5).setText(String.valueOf(columnEntities.get(i).getIsNullable()));
        }

    }

    private void generateDocTableHeader(XWPFTable comTable) {
        //表格第一行
        XWPFTableRow comTableRowOne = comTable.getRow(0);
        comTableRowOne.getCell(0).setText("属性");
        comTableRowOne.addNewTableCell().setText("类型");
        comTableRowOne.addNewTableCell().setText("注释");
        comTableRowOne.addNewTableCell().setText("默认值");
        comTableRowOne.addNewTableCell().setText("主键");
        comTableRowOne.addNewTableCell().setText("是否为空");
    }

    private void generateTableInfo(XWPFDocument document,TableEntity tableEntity) {
        XWPFParagraph titleParagraph = document.createParagraph();
        titleParagraph.setAlignment(ParagraphAlignment.LEFT);

        XWPFRun titleParagraphRun = titleParagraph.createRun();
        titleParagraphRun.setText(tableEntity.getName()+" : "+tableEntity.getComment());
        titleParagraphRun.setColor("000000");
        titleParagraphRun.setFontSize(14);


        titleParagraph = document.createParagraph();
        titleParagraph.setAlignment(ParagraphAlignment.LEFT);

        titleParagraphRun = titleParagraph.createRun();
        titleParagraphRun.setText(" 现有数据量是："+tableEntity.getRowNum());
        titleParagraphRun.setColor("000000");
        titleParagraphRun.setFontSize(14);

        //换行
        generateCRLF(document);
    }
    private void generateDocTitle(XWPFDocument document) {
        //添加标题
        XWPFParagraph titleParagraph = document.createParagraph();
        //设置段落居中
        titleParagraph.setAlignment(ParagraphAlignment.CENTER);

        XWPFRun titleParagraphRun = titleParagraph.createRun();
        titleParagraphRun.setText("数据库字典");
        titleParagraphRun.setColor("000000");
        titleParagraphRun.setFontSize(20);

        //换行
        generateCRLF(document);
    }

    private void generateCRLF(XWPFDocument document) {
        XWPFParagraph paragraph1 = document.createParagraph();
        XWPFRun paragraphRun1 = paragraph1.createRun();
        paragraphRun1.setText("\r");
    }

//    public boolean generatorCode(String[] tableNames) {
//        try {
//            for (String tableName : tableNames) {
//                //查询表信息
//                Map<String, String> table = queryTable(tableName);
//                //查询列信息
//                List<Map<String, String>> columns = queryColumns(tableName);
//                //生成代码
//                GenUtils.generatorCode(table, columns);
//            }
//        }catch (Exception e){
//            e.printStackTrace();
//            return false;
//        }
//        return true;
//    }
}
