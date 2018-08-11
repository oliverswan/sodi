package net.oliver.sodi.controller;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import net.oliver.sodi.model.Item;
import net.oliver.sodi.service.IBackorderService;
import net.oliver.sodi.service.IItemService;
import net.oliver.sodi.util.AlternatingBackground;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping(value = "/api/reports")
public class ReportController {

    private static final Logger logger = LoggerFactory.getLogger(UploadController.class);

    @Autowired
    IItemService itemService;

    @Autowired
    IBackorderService backOrderService;

    private Font getPdfChineseFont() throws Exception {
        BaseFont bfChinese = BaseFont.createFont("STSongStd-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);
        Font fontChinese = new Font(bfChinese, 12, Font.NORMAL);
        return fontChinese;
    }

    private String[] columns = {"Seq", "Code", "Name","Stock","SPM","Reorder"};//,"Unit Cost","Margin"
    private String[] backOrdercolumns = {"Code", "Quantity"};
    private void generatePDF(Document document,List<Item> items,int month) throws Exception {
            document.open();
            // seq,code,desc,stock,spm,reorder,cost,margin
            PdfPTable table = new PdfPTable(6); // 设置表格是几列的
            float[] cls = {75,125,75,75,75,75};
            table.setTotalWidth(cls);//设置表格的各列宽度
            table.setLockedWidth(true);

//        table.setWidth(80); // 宽度
            table.setSummary("This is summary");
            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER); // 水平对齐方式
            table.getDefaultCell().setVerticalAlignment(Element.ALIGN_CENTER); // 垂直对齐方式
            table.setSplitLate(false);
            for(String c :  columns)
            {
                writeHeaderCell(table,c);

            }
            for (int i=1;i<=items.size();i++)
            {
                writeCell(table,String.valueOf(i));
                writeCell(table,items.get(i-1).getCode());
                writeCell(table,items.get(i-1).getName());
                writeCell(table,""+items.get(i-1).getStock());
                writeCell(table,""+items.get(i-1).getSpm());
                // reorder
                int reorder =  (int)Math.rint(month*items.get(i-1).getSpm()-items.get(i-1).getStock());
                writeCell(table,""+reorder);
                //writeCell(table,"");
                //writeCell(table,"");
            }
            PdfPTableEvent event = new AlternatingBackground();
            table.setTableEvent(event);

            document.add(table);
            document.close();
    }
    private PdfPCell writeCell(PdfPTable table,String  content)
    {
        PdfPCell pdfCell = new PdfPCell();//  pdfCell.setMinimumHeight(30);//设置表格行高
        Paragraph paragraph = new Paragraph(content);
        pdfCell.setPhrase(paragraph);
        table.addCell(pdfCell);
        return pdfCell;
    }

    private void writeHeaderCell(PdfPTable table,String  content)
    {
        PdfPCell pdfCell = new PdfPCell();//  pdfCell.setMinimumHeight(30);//设置表格行高
        Paragraph paragraph = new Paragraph(content);
        pdfCell.setPhrase(paragraph);
        pdfCell.setBackgroundColor(new BaseColor(128, 255, 255));
        table.addCell(pdfCell);
    }


    @RequestMapping(value = "/reorder/{month}",method = RequestMethod.GET)
    public void  reorder(HttpServletRequest request, HttpServletResponse response, @PathVariable int month/*@RequestParam int num*/) throws Exception {
        response.setHeader("content-Type", "application/pdf");// 告诉浏览器用什么软件可以打开此文件
        response.setHeader("Content-Disposition", "attachment;filename=reorder.pdf"); // 下载文件的默认名称
        // 0.根据参数查找所有的Item
        List<Item> result = itemService.findAllForReorder(month);
        Document document = new Document();
        PdfWriter.getInstance(document, response.getOutputStream());
        generatePDF(document,result,month);
    }

    @RequestMapping(value = "/backorder",method = RequestMethod.GET)
    public void  backorder(HttpServletRequest request, HttpServletResponse response) throws Exception {
        response.setHeader("content-Type", "application/pdf");// 告诉浏览器用什么软件可以打开此文件
        response.setHeader("Content-Disposition", "attachment;filename=backorder.pdf"); // 下载文件的默认名称

        Map<String,Integer> orders = backOrderService.report();

        Document document = new Document();
        PdfWriter.getInstance(document, response.getOutputStream());
        generatePDFBackOrder(document,orders);
    }

    private void generatePDFBackOrder(Document document, Map<String, Integer> orders)  throws Exception{

        document.open();
        // seq,code,desc,stock,spm,reorder,cost,margin
        PdfPTable table = new PdfPTable(2); // 设置表格是几列的
        float[] cls = {250,250};
        table.setTotalWidth(cls);//设置表格的各列宽度
        table.setLockedWidth(true);

//        table.setWidth(80); // 宽度
        table.setSummary("This is summary");
        table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER); // 水平对齐方式
        table.getDefaultCell().setVerticalAlignment(Element.ALIGN_CENTER); // 垂直对齐方式
        table.setSplitLate(false);

        for(String c :  backOrdercolumns)
        {
            writeHeaderCell(table,c);
        }
        for(Iterator iter = orders.entrySet().iterator(); iter.hasNext();) {

            Map.Entry<String, Integer> entry = (Map.Entry<String, Integer>) iter.next();
            writeCell(table,entry.getKey());
            writeCell(table,String.valueOf(entry.getValue()));
        }
        PdfPTableEvent event = new AlternatingBackground();
        table.setTableEvent(event);

        document.add(table);
        document.close();
    }
}
