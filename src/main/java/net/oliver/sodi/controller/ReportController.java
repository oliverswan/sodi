package net.oliver.sodi.controller;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import net.oliver.sodi.model.*;
import net.oliver.sodi.service.IBackorderService;
import net.oliver.sodi.service.IInvoiceService;
import net.oliver.sodi.service.IItemService;
import net.oliver.sodi.service.ISoldHistoryService;
import net.oliver.sodi.util.AlternatingBackground;
import net.oliver.sodi.util.InvoiceGenerator;
import net.oliver.sodi.util.SystemStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;
import java.util.List;

@Controller
@RequestMapping(value = "/api/reports")
public class ReportController {

    private static final Logger logger = LoggerFactory.getLogger(UploadController.class);

    @Autowired
    IItemService itemService;

    @Autowired
    IBackorderService backOrderService;

    @Autowired
    IInvoiceService invoiceService;

    @Autowired
    ISoldHistoryService soldHistoryService;

    @Autowired
    InvoiceGenerator invo;

    private Font getPdfChineseFont() throws Exception {
        BaseFont bfChinese = BaseFont.createFont("STSongStd-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);
        Font fontChinese = new Font(bfChinese, 12, Font.NORMAL);
        return fontChinese;
    }

    private String[] columns = {"Seq", "Code","Stock","SPM","Reorder"};//,"Unit Cost","Margin"
    private String[] backOrdercolumns = {"Code", "Quantity","Distribute"};
    private String[] deliveryColumns = {"Name", "Items"};
    private void generatePDF(Document document,List<Item> items,int month) throws Exception {
            document.open();
            // seq,code,desc,stock,spm,reorder,cost,margin
            PdfPTable table = new PdfPTable(5); // 设置表格是几列的
            float[] cls = {75,125,75,75,75};
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
//                writeCell(table,items.get(i-1).getName());
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
        response.setHeader("Content-Disposition", "inline;filename=reorder.pdf"); // 下载文件的默认名称
        // 0.根据参数查找所有的Item
        List<Item> result = itemService.findAllForReorder(month);
        Document document = new Document();
        PdfWriter.getInstance(document, response.getOutputStream());
        generatePDF(document,result,month);
    }

    @RequestMapping(value = "/backorder",method = RequestMethod.GET)
    public void  backorder(HttpServletRequest request, HttpServletResponse response) throws Exception {
        response.setHeader("content-Type", "application/pdf");// 告诉浏览器用什么软件可以打开此文件
        response.setHeader("Content-Disposition", "inline;filename=backorder.pdf"); // 下载文件的默认名称
        List<BackOrderReportEntry> entries = backOrderService.report();
        Collections.sort(entries);
        Document document = new Document();
        PdfWriter.getInstance(document, response.getOutputStream());
        generatePDFBackOrder(document,entries);
        response.flushBuffer();
    }


    private Map<String,List<String>> getDelivery(){

        Map<String,List<String>> result = new HashMap<String,List<String>>();
        List<Backorder> bos = backOrderService.findNotCompleted();
        for(Backorder bo : bos)
        {
            List<String> tL;

            if(result.containsKey(bo.getCustomName()))
            {
                tL = result.get(bo.getCustomName());
            }else{
                tL = new ArrayList<String>();
                result.put(bo.getCustomName(),tL);
            }

            for(Iterator iter = bo.getOrders().entrySet().iterator();iter.hasNext();)
            {
                Map.Entry<String,Integer> entry = (Map.Entry<String, Integer>) iter.next();
                StringBuffer sb = new StringBuffer();
                sb.append(entry.getKey()).append(" : ").append(entry.getValue());
                tL.add(sb.toString());
            }
        }
        return result;

    }

    private void generateDeliveryPDF(Document document)  throws Exception{

        document.open();
        // seq,code,desc,stock,spm,reorder,cost,margin
        PdfPTable table = new PdfPTable(2); // 设置表格是几列的
        float[] cls = {300,200};
        table.setTotalWidth(cls);//设置表格的各列宽度
        table.setLockedWidth(true);

//        table.setWidth(80); // 宽度
        table.setSummary("This is summary");
        table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER); // 水平对齐方式
        table.getDefaultCell().setVerticalAlignment(Element.ALIGN_CENTER); // 垂直对齐方式
        table.setSplitLate(false);

        for(String c :  deliveryColumns)
        {
            writeHeaderCell(table,c);
        }

        Map<String,List<String>> dels = this.getDelivery();
        StringBuffer sb = new StringBuffer();
        for(Iterator iter = dels.entrySet().iterator();iter.hasNext();)
        {
            sb.delete(0, sb.length());
            Map.Entry entry = (Map.Entry) iter.next();

            writeCell(table, (String) entry.getKey());
            for(String str : (List<String>)entry.getValue())
            {
                    sb.append(str).append("\r\n");
            }
            writeCell(table,sb.toString());
        }

        PdfPTableEvent event = new AlternatingBackground();
        table.setTableEvent(event);

        document.add(table);
        document.close();
    }


    @RequestMapping(value = "/backorderDelivery",method = RequestMethod.GET)
    public void  backorderDelivery(HttpServletRequest request, HttpServletResponse response) throws Exception {
        response.setHeader("content-Type", "application/pdf");// 告诉浏览器用什么软件可以打开此文件
        response.setHeader("Content-Disposition", "inline;filename=delivery.pdf"); // 下载文件的默认名称


        List<BackOrderReportEntry> entries = backOrderService.report();
//        Collections.sort(entries, new Comparator<BackOrderReportEntry>() {
//            @Override
//            public int compare(BackOrderReportEntry o1, BackOrderReportEntry o2) {
//                return o1.get;
//            }
//        });
        Document document = new Document();
        PdfWriter.getInstance(document, response.getOutputStream());
        generateDeliveryPDF(document);
        response.flushBuffer();
    }

    private void generatePDFBackOrder(Document document, List<BackOrderReportEntry> entries)  throws Exception{

        document.open();
        // seq,code,desc,stock,spm,reorder,cost,margin
        PdfPTable table = new PdfPTable(3); // 设置表格是几列的
        float[] cls = {100,50,350};
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

        for(BackOrderReportEntry en : entries)
        {
            writeCell(table,en.getItemCode());
            writeCell(table,String.valueOf(en.getTotal()));
            StringBuilder sb = new StringBuilder();
            for(Iterator iter = en.getDistribute().entrySet().iterator();iter.hasNext();)
            {
                Map.Entry<String,Integer> cen = (Map.Entry<String, Integer>) iter.next();
                sb.append(cen.getKey()+" : "+cen.getValue());
                sb.append("\r\n");
            }
            writeCell(table,sb.toString());
        }

        PdfPTableEvent event = new AlternatingBackground();
        table.setTableEvent(event);

        document.add(table);
        document.close();
    }

    @RequestMapping(value = "/invoice/{id}",method = RequestMethod.GET)
    public void  createInvoice(HttpServletRequest request, HttpServletResponse response, @PathVariable int id/*@RequestParam int num*/) throws Exception {
        response.setHeader("content-Type", "application/pdf");// 告诉浏览器用什么软件可以打开此文件
        response.setHeader("Content-Disposition", "inline;filename=invoice.pdf"); // 下载文件的默认名称
        Invoice invoice = invoiceService.findById(id);
        Document document = new Document();
        try {
            PdfWriter writer = PdfWriter.getInstance(document, response.getOutputStream());
            invo.createInvoice(document,writer,invoice);
            response.flushBuffer();
        } catch (DocumentException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @RequestMapping(value = "/delivery/{id}",method = RequestMethod.GET)
    public void  createDelviery(HttpServletRequest request, HttpServletResponse response, @PathVariable int id/*@RequestParam int num*/) throws Exception {
        response.setHeader("content-Type", "application/pdf");// 告诉浏览器用什么软件可以打开此文件
        response.setHeader("Content-Disposition", "inline;filename=delivery.pdf"); // 下载文件的默认名称
        Invoice invoice = invoiceService.findById(id);
        Document document = new Document();
        try {
            PdfWriter writer = PdfWriter.getInstance(document, response.getOutputStream());
            invo.createDelivery(document,writer,invoice);
            response.flushBuffer();
        } catch (DocumentException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 销售历史报告
    @RequestMapping(value = "/salehistory/{month}",method = RequestMethod.GET)
    public void  salehistory(HttpServletRequest request, HttpServletResponse response, @PathVariable int month/*@RequestParam int num*/) throws Exception {

        response.setHeader("content-Type", "application/pdf");// 告诉浏览器用什么软件可以打开此文件
        response.setHeader("Content-Disposition", "inline;filename=reorder.pdf"); // 下载文件的默认名称
        SystemStatus.getCurrentYM();

        if(month > SystemStatus.getCurrentM())
            month =  SystemStatus.getCurrentM();
        // 0.根据参数
        List<SoldHistory> result = soldHistoryService.findAllForSalesHistory(month);
        Document document = new Document();
        PdfWriter.getInstance(document, response.getOutputStream());
        generateSalehistoryPDF(document,result,month);
    }

    private void generateSalehistoryPDF(Document document, List<SoldHistory> result, int month) throws DocumentException {
        document.open();
        // seq,code,desc,stock,spm,reorder,cost,margin
        // code stock avsales msoh coming invvalue
        int colsNum =month+6;

        PdfPTable table = new PdfPTable(colsNum); // 设置表格是几列的

        float[] cls = new float[colsNum];
        cls[0]=80;

        for(int i=1;i<cls.length;i++)
        {
            cls[i] = 550/colsNum;
        }

        table.setTotalWidth(cls);//设置表格的各列宽度
        table.setLockedWidth(true);

//        table.setWidth(80); // 宽度
        table.setSummary("Summary");
        table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER); // 水平对齐方式
        table.getDefaultCell().setVerticalAlignment(Element.ALIGN_CENTER); // 垂直对齐方式
        table.setSplitLate(false);

        // 处理列头
        String[] colNames =new  String[colsNum];
        colNames[0] = "Code";

        for(int k=month-1,m=1;k>=0;k--,m++)
        {
            colNames[m] = SystemStatus.getCurrentMPrevious(k);
        }

        colNames[month+1]="stock";
        colNames[month+2]="Last "+month+ "Av Sales";
        colNames[month+3]="Msoh";
        colNames[month+4]="On order";
        colNames[month+5]="Inv Value";

        for(String c :  colNames)
        {
            if(c.startsWith("2")&&c.length() < 6)
            {
                String nc = c.substring(0,4)+"0"+c.substring(4);
                writeHeaderCell(table,nc);
            }else{
                writeHeaderCell(table,c);
            }
        }

        for(SoldHistory en : result)
        {
            writeCell(table,en.getCode());
            Map<Integer,Integer> curHis = en.getHis();
            int total = 0;
            for(int x=1;x<=month;x++)
            {
                Integer v = curHis.get(Integer.parseInt(colNames[x]));
                if(v == null)
                    v = 0;
                 writeCell(table,String.valueOf(v));

                total+=v;
            }
            float av = total/(month);

            writeCell(table,"nil");// stock
            writeCell(table,"  "+av);// av
            writeCell(table,"nil");//msoh
            writeCell(table,"nil");//on order
            writeCell(table,"nil");// inv value
        }

        PdfPTableEvent event = new AlternatingBackground();
        table.setTableEvent(event);

        document.add(table);
        document.close();

    }
}
