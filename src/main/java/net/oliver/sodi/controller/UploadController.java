package net.oliver.sodi.controller;

import com.opencsv.CSVReader;
import net.oliver.sodi.config.Const;
import net.oliver.sodi.dao.IInvoiceDao;
import net.oliver.sodi.model.Invoice;
import net.oliver.sodi.model.InvoiceItem;
import net.oliver.sodi.service.IBackorderService;
import net.oliver.sodi.util.MathUtil;
import net.oliver.sodi.util.MongoAutoidUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row.MissingCellPolicy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/upload")
@CrossOrigin
public class UploadController {

// application.properties文件中配置參數
//    @Value("${test.msg}")
//    private String msg;
    private static final Logger logger = LoggerFactory.getLogger(UploadController.class);

    @Autowired
    IInvoiceDao dao;

    @Autowired
    MongoAutoidUtil sequence;

    @Autowired
    IBackorderService backorderService;

    //@Value("${prop.upload-folder}")
//    private String UPLOAD_FOLDER = "D:\\upload\\";

    @PostMapping("/invoices")
    public Object singleFileUpload(MultipartFile file) {
        if (Objects.isNull(file) || file.isEmpty()) {
            return "文件为空，请重新上传";
        }

        try {
            ByteArrayInputStream ins = new ByteArrayInputStream(file.getBytes());
            InputStreamReader reader = new InputStreamReader(ins);
            Map<String, Invoice> excel = new HashMap<String, Invoice>();
            CSVReader csvReader = new CSVReader(reader);
            String[] strArr = null;
            strArr = csvReader.readNext();
            while ((strArr = csvReader.readNext()) != null) {
                // 不能以contactname区分 如果一次导入 有相同客户的多个单子呢
                String reference = strArr[11];
                if (!StringUtils.isBlank(reference)) {
                    Invoice invoice = excel.get(reference);
                    if (invoice == null) {
                        invoice = new Invoice();
                        invoice.setContactName(strArr[0]);
                        invoice.setEmailAddress(strArr[1]);
                        invoice.setPoaddressline1(strArr[2]);
                        invoice.setPoaddressline2(strArr[3]);
                        invoice.setPoaddressline3(strArr[4]);
                        invoice.setPoaddressline4(strArr[5]);
                        invoice.setPocity(strArr[6]);
                        invoice.setPoregion(strArr[7]);
                        invoice.setPopostalcode(strArr[8]);
                        invoice.setPocountry(strArr[9]);

                        invoice.setInvoiceNumber(Const.InvoiceNumerPrefix+sequence.getNextSequence("invoiceNumber"));//strArr[10]
                        invoice.setReference(strArr[11]);
                        invoice.setInvoiceDate(strArr[12]);
                        invoice.setDueDate(strArr[13]);
                        invoice.setStatus(0);
                        invoice.setId(sequence.getNextSequence("invoice"));
                        excel.put(reference, invoice);
                       /* if (!StringUtils.isBlank(strArr[30])) {
                            String[] bos = strArr[30].split(",");
                            Backorder bo = new Backorder();
                            bo.setId(sequence.getNextSequence("backorder"));
                            bo.setInvoiceNumber(invoice.getInvoiceNumber());
                            for (String str : bos) {
                                String[] detail = str.split("#");
                                int qu = Integer.parseInt(detail[0]);
                                bo.addItem(detail[1], qu);
                            }
                            backorderService.save(bo);
                        }*/
                    }
                    InvoiceItem item = new InvoiceItem();
                    item.setInventoryItemCode(strArr[14]);
                    item.setDescription(strArr[15]);
                    item.setQuantity(Integer.parseInt(strArr[16]));
                    item.setUnitAmount(Double.parseDouble(strArr[17]));
                    item.setDiscount(strArr[18]);
                    item.setAccountCode(strArr[19]);
                    item.setTaxType(strArr[20]);
                    item.setTrackingName1(strArr[21]);
                    item.setTrackingOption1(strArr[22]);
                    item.setTrackingName2(strArr[23]);
                    item.setTrackingOption2(strArr[24]);
                    item.setCurrency(strArr[25]);
                    item.setBrandingTheme(strArr[26]);
                    item.setProduct_attribute(strArr[27]);
                    item.setProduct_subtotal_discount(strArr[28]);
                    item.setProduct_quantity(strArr[29]);
                    item.setTotalamount(item.getUnitAmount().multiply(new BigDecimal(item.getQuantity() )).multiply(new BigDecimal(1.1)));
                    item.reCalculate(1);
                    invoice.addItem(item);
                }
            }
            reader.close();
            csvReader.close();


            for (Iterator iter = (Iterator) excel.values().iterator(); iter.hasNext(); ) {
                dao.save((Invoice) iter.next());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return "success";

    }

    @PostMapping("/purchase/{section}")
    //ResponseEntity<byte[]>
    public void generatePriceCsv(HttpServletRequest request, HttpServletResponse response, MultipartFile file, @PathVariable String section) {

        FileOutputStream os = null;
        try {
            os = new FileOutputStream("D:/test.png");
            os.write(file.getBytes(), 0, file.getBytes().length);
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                os.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @PostMapping("/prices")
    //ResponseEntity<byte[]>
    public void generatePriceCsv(HttpServletRequest request, HttpServletResponse response,MultipartFile file) {
//        if (Objects.isNull(file) || file.isEmpty()) {
//            return "文件为空，请重新上传";
//        }
//        response.setContentType("application/x-download");

        response.setHeader("content-Type", "octet/stream");// 告诉浏览器用什么软件可以打开此文件
        response.setHeader("Content-Disposition", "attachment;filename=price.csv"); // 下载文件的默认名称


        HttpHeaders headers = new HttpHeaders();
        headers.setContentDispositionFormData("attachment", "pricelist");
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);

        StringBuffer sb = new StringBuffer();

            try {
                PrintWriter pw = response.getWriter();
                ByteArrayInputStream ins = new ByteArrayInputStream(file.getBytes());
                HSSFWorkbook workbook = new HSSFWorkbook(ins);
                HSSFSheet sheet = workbook.getSheetAt(0);
                DataFormatter df = new DataFormatter();


//			FileOutputStream os = new FileOutputStream(csv);

//			writer.write("SKU,\"Product Name\",\"Price (Net)");

                sb.append("\"SKU\",\"Product Name\",\"Price (Net)\"").append("\r\n");
                pw.print(sb.toString());
//			writer.write("\r\n");
                for (int k = 2; k <sheet.getLastRowNum(); k++) {
                    HSSFRow row = sheet.getRow(k);
                    if(row == null)
                        continue;
                    // code
                    sb = new StringBuffer();
                    HSSFCell codeCell = row.getCell(0, MissingCellPolicy.CREATE_NULL_AS_BLANK);
                    String codeV = codeCell.getStringCellValue();

                    HSSFCell nameCell = row.getCell(2, MissingCellPolicy.CREATE_NULL_AS_BLANK);
                    String name = nameCell.getStringCellValue();

                    HSSFCell priceCell = row.getCell(11, MissingCellPolicy.CREATE_NULL_AS_BLANK);
                    double price = priceCell.getNumericCellValue();
                    sb.append("\"").append(codeV).append("\"").append(",\"").append(name).append("\"").append(",").append("\"").append(MathUtil.trimDouble(price)).append(" AUD\"").append("\r\n");
                    pw.print(sb.toString());
                }
                pw.flush();
                pw.close();


            } catch (Exception e) {
                e.printStackTrace();
            }
//        return new ResponseEntity<>(sb.toString().getBytes(), headers, HttpStatus.CREATED);

    }
}
//        try {
//            byte[] bytes = file.getBytes();
//            Path path = Paths.get(UPLOAD_FOLDER + file.getOriginalFilename());
//            //如果没有files文件夹，则创建
//            if (!Files.isWritable(path)) {
//                Files.createDirectories(Paths.get(UPLOAD_FOLDER));
//            }
//            //文件写入指定路径
//            Files.write(path, bytes);
//            return "文件上传成功";
//        } catch (IOException e) {
//            e.printStackTrace();
//            return "后端异常...";
//        }

