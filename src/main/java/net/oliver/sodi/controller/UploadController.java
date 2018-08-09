package net.oliver.sodi.controller;

import com.opencsv.CSVReader;
import net.oliver.sodi.dao.IInvoiceDao;
import net.oliver.sodi.model.Invoice;
import net.oliver.sodi.model.InvoiceItem;
import net.oliver.sodi.util.MongoAutoidUtil;
import org.apache.commons.lang3.StringUtils;
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

    @Autowired
    IInvoiceDao dao;

    @Autowired
    MongoAutoidUtil sequence;

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
                    String contactname =strArr[0];
                    if (!StringUtils.isBlank(contactname)) {
                        Invoice invoice = excel.get(contactname);
                        if (invoice == null) {
                            invoice = new Invoice();
                            invoice.setEmailAddress(strArr[1]);
                            invoice.setPOAddressLine1(strArr[2]);
                            invoice.setPOAddressLine2(strArr[3]);
                            invoice.setPOAddressLine3(strArr[4]);
                            invoice.setPOAddressLine4(strArr[5]);
                            invoice.setPOCity(strArr[6]);
                            invoice.setPORegion(strArr[7]);
                            invoice.setPOPostalCode(strArr[8]);
                            invoice.setPOCountry(strArr[9]);
                            invoice.setInvoiceNumber(strArr[10]);
                            invoice.setReference(strArr[11]);
                            invoice.setInvoiceDate(strArr[12]);
                            invoice.setDueDate(strArr[13]);
                            invoice.setId(sequence.getNextSequence("invoice"));
                            excel.put(contactname, invoice);
                        }
                        InvoiceItem item = new InvoiceItem();
                        item.setInventoryItemCode(strArr[14]);
                        item.setDescription(strArr[15]);
                        item.setQuantity(strArr[16]);
                        item.setUnitAmount(strArr[17]);
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

