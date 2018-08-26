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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
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
    private String UPLOAD_FOLDER = "D:\\upload\\";

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
                        invoice.setPOAddressLine1(strArr[2]);
                        invoice.setPOAddressLine2(strArr[3]);
                        invoice.setPOAddressLine3(strArr[4]);
                        invoice.setPOAddressLine4(strArr[5]);
                        invoice.setPOCity(strArr[6]);
                        invoice.setPORegion(strArr[7]);
                        invoice.setPOPostalCode(strArr[8]);
                        invoice.setPOCountry(strArr[9]);

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
                    item.setTotalamount(MathUtil.trimDouble(item.getQuantity() * item.getUnitAmount() * 1.1));
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

