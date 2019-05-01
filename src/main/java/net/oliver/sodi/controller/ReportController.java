package net.oliver.sodi.controller;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import net.oliver.sodi.model.*;
import net.oliver.sodi.service.IBackorderService;
import net.oliver.sodi.service.IInvoiceService;
import net.oliver.sodi.service.IItemService;
import net.oliver.sodi.service.ISoldHistoryService;
import net.oliver.sodi.util.*;
import org.apache.commons.lang3.StringUtils;
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
    private String[] deliveryColumns2 = {"Code", "Name", "Quantity", "Days", "Invoice","Stock","Coming"};
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


    private Map<String,List<String[]>> getDelivery(){

        Map<String,List<String[]>> result = new HashMap<String,List<String[]>>();
        List<Backorder> bos = backOrderService.findNotCompleted();
        for(Backorder bo : bos)
        {
            List<String[]> tL;

            if(result.containsKey(bo.getCustomName()))
            {
                tL = result.get(bo.getCustomName());
            }else{
                tL = new ArrayList<String[]>();
                result.put(bo.getCustomName(),tL);
            }

            for(Iterator iter = bo.getOrders().entrySet().iterator();iter.hasNext();)
            {
                // code -> num
                Map.Entry<String,Integer> entry = (Map.Entry<String, Integer>) iter.next();
                StringBuffer sb = new StringBuffer();
                List<Item> is = itemService.findByCode(entry.getKey());
                sb.append(entry.getKey());
                String[] desp = new String[7];
                desp[0]=entry.getKey();//code
                desp[2]= String.valueOf(entry.getValue());//code
                if(is.size()>0)
                {
                    desp[1] = is.get(0).getName();// 名称
                    if(!StringUtils.isBlank(is.get(0).getLocation())&&!"0".equals(is.get(0).getLocation()))
                        desp[0]= desp[0]+"("+is.get(0).getLocation()+")";
                    if(bo.getCreatedTime()!=null)
                    {
                        Date created = bo.getCreatedTime();
                        int n = DateUtil.calcDayOffset(created,new Date());
                        desp[3]= String.valueOf(n);// 创建多少天了
                    }else{
                        desp[3]= "N";// 创建多少天了
                    }

                    // 创建时间;
                    desp[4]= bo.getInvoiceNumber();
                    // 当前库存
                    desp[5]= String.valueOf(is.get(0).getStock());
//                            String.valueOf(MathUtil.trimDouble(entry.getValue()*is.get(0).getPrice()));
                    // 正在路上
                    desp[6] = String.valueOf(is.get(0).getComing());

                }
                tL.add(desp);
            }
        }
        return result;

    }

    private double createDeliveryTable(Document document,List<String[]> list)
    {
        double result = 0.0;
        PdfPTable table = new PdfPTable(7); // 设置表格是几列的
        float[] cls = {120,150,50,35,60,50,35};
        try {
            table.setTotalWidth(cls);//设置表格的各列宽度
            table.setLockedWidth(true);
            table.setSummary("This is summary");
            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER); // 水平对齐方式
            table.getDefaultCell().setVerticalAlignment(Element.ALIGN_CENTER); // 垂直对齐方式
            table.setSplitLate(false);

            for(String c :  deliveryColumns2)
            {
                writeHeaderCell(table,c);
            }

//            Map<String,List<String[]>> dels = this.getDelivery();
            StringBuffer sb = new StringBuffer();
            for(String[] l : list)
            {
                for(String str : l)
                    writeCell(table, str);
                if(l[5]!=null)
                result += Double.parseDouble(l[5]);
            }

            PdfPTableEvent event = new AlternatingBackground();
            table.setTableEvent(event);

            document.add(table);
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        return result;
    }
    private void generateDeliveryPDF(Document document)  throws Exception{

        document.open();
        double total = 0.0;
        Map<String,List<String[]>> dels = this.getDelivery();
        StringBuffer sb = new StringBuffer();
        for(Iterator iter = dels.entrySet().iterator();iter.hasNext();)
        {
            Map.Entry entry = (Map.Entry) iter.next();
//            Chunk chunk = new Chunk((String)entry.getKey());
//            document.add(chunk);

            Paragraph paragraph3= new Paragraph();
            paragraph3.add("           ");
            paragraph3.setAlignment(Element.ALIGN_LEFT);
            document.add(paragraph3);

            Paragraph paragraph = new Paragraph();
            paragraph.add((String)entry.getKey());
            paragraph.setAlignment(Element.ALIGN_LEFT);
            document.add(paragraph);

            Paragraph paragraph2= new Paragraph();
            paragraph2.add("           ");
            paragraph2.setAlignment(Element.ALIGN_LEFT);
            document.add(paragraph2);

            double val = this.createDeliveryTable(document,(List<String[]>)entry.getValue());
            total += val;
        }

        Paragraph paragraph4= new Paragraph();
        paragraph4.add("           ");
        paragraph4.setAlignment(Element.ALIGN_LEFT);
        document.add(paragraph4);

        Paragraph paragraph5 = new Paragraph();
        paragraph5.add("Total value: "+ MathUtil.trimDouble(total));
        paragraph5.setAlignment(Element.ALIGN_LEFT);
        document.add(paragraph5);

        Paragraph paragraph6= new Paragraph();
        paragraph6.add("           ");
        paragraph6.setAlignment(Element.ALIGN_LEFT);
        document.add(paragraph6);
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
        float[] cls = {150,50,300};
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
            String code = en.getItemCode();
            StringBuffer sb = new StringBuffer();
            sb.append(code);
            List<Item> is = itemService.findByCode(code);
            if(is.size()>0)
            {
                sb.append("\r\n").append(is.get(0).getName());

                if(!StringUtils.isBlank(is.get(0).getLocation())&&!"0".equals(is.get(0).getLocation()))
                {
                    sb.append("\r\n (").append(is.get(0).getLocation()).append(")");
                }
            }


            writeCell(table,sb.toString());
            writeCell(table,String.valueOf(en.getTotal())+"/"+en.getComing());
            StringBuilder sb2 = new StringBuilder();
            for(Iterator iter = en.getDistribute().entrySet().iterator();iter.hasNext();)
            {
                Map.Entry<String,Integer> cen = (Map.Entry<String, Integer>) iter.next();
                sb2.append(cen.getKey()+" : "+cen.getValue());
                sb2.append("\r\n");
            }
            writeCell(table,sb2.toString());
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

//        if(month > SystemStatus.getCurrentM())
//            month =  SystemStatus.getCurrentM();
        // 0.根据参数
        List<SoldHistory> result = soldHistoryService.findAllForSalesHistory(month);
        Collections.sort(result);
        Document document = new Document();
        PdfWriter.getInstance(document, response.getOutputStream());
        generateSalehistoryPDF(document,result,month);
    }

    private void generateSalehistoryPDF(Document document, List<SoldHistory> result, int month) throws DocumentException {
        document.open();
        // seq,code,desc,stock,spm,reorder,cost,margin
        // code stock avsales msoh coming invvalue

        List<String> ls = SystemStatus.getLastMonthLabel(month);

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



//        for(int k=month-1,m=1;k>=0;k--,m++)
//        {
//            colNames[m] = SystemStatus.getCurrentMPrevious(k);
//        }

        for(int k =0;k<month;k++)
        {
            colNames[k+1] = ls.get(k);
        }

        colNames[month+1]="stock";
        colNames[month+2]="Last "+month+ " Month ave";
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
            int av = total/month;

            List<Item> its = itemService.findByCode(en.getCode());
            if(its.size()>0)
            {
                writeCell(table,""+its.get(0).getStock());// stock
                writeCell(table,"  "+av);// av
                if(its.get(0).getStock()<=0 || av == 0)
                {
                    writeCell(table,"0");//msoh
                }else{
                    writeCell(table,""+ its.get(0).getStock()/av);//msoh
                }

                writeCell(table,"nil");//on order
                writeCell(table,""+its.get(0).getValue());// inv value
            }else{
                writeCell(table,"nil");// stock
                writeCell(table,"  "+av);// av
                writeCell(table,"nil");//msoh
                writeCell(table,"nil");//on order
                writeCell(table,"nil");// inv value
            }

        }

        PdfPTableEvent event = new AlternatingBackground();
        table.setTableEvent(event);

        document.add(table);
        document.close();

    }
}
