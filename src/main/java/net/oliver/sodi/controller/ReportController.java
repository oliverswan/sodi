package net.oliver.sodi.controller;

import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
@RequestMapping(value = "/api/reports")
public class ReportController {

    private Font getPdfChineseFont() throws Exception {
        BaseFont bfChinese = BaseFont.createFont("STSongStd-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);
        Font fontChinese = new Font(bfChinese, 12, Font.NORMAL);
        return fontChinese;
    }

    private void generatePDF(Document document) throws Exception {
            document.open();
            PdfPTable table = new PdfPTable(3); // 设置表格是几列的
//        table.setWidth(80); // 宽度
//        table.setBorder(1); // 边框
            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER); // 水平对齐方式
            table.getDefaultCell().setVerticalAlignment(Element.ALIGN_TOP); // 垂直对齐方式
            table.setLockedWidth(true);
            table.setTotalWidth(458);
            table.setHorizontalAlignment(Element.ALIGN_LEFT);

            Object[][] datas = {{"区域", "总销售额(万元)", "总利润(万元)简单的表格"}, {"江苏省" , 9045,  2256}, {"广东省", 3000, 690}};
            for(int i = 0; i < datas.length; i++) {
                for(int j = 0; j < datas[i].length; j++) {
                    PdfPCell pdfCell = new PdfPCell();
                    pdfCell.setMinimumHeight(30);//设置表格行高
                    Paragraph paragraph = new Paragraph(""+datas[i][j],getPdfChineseFont());
                    pdfCell.setPhrase(paragraph);
                    table.addCell(pdfCell);
                }
            }
            document.add(table);
    }

    @RequestMapping(value = "/reorder",method = RequestMethod.GET)
    public void  reorder(HttpServletRequest request, HttpServletResponse response, @RequestParam int num) throws Exception {

        System.out.println(num);
        // 告诉浏览器用什么软件可以打开此文件
        response.setHeader("content-Type", "application/pdf");
        // 下载文件的默认名称
        response.setHeader("Content-Disposition", "attachment;filename=aaa.pdf");
        Document document = new Document();
        PdfWriter.getInstance(document, response.getOutputStream());
        generatePDF(document);
        document.close();
    }
}
