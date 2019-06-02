package net.oliver.sodi.controller;

import net.oliver.sodi.model.Backorder;
import net.oliver.sodi.model.PoTracking;
import net.oliver.sodi.model.TableResult;
import net.oliver.sodi.service.IPoTrackingService;
import net.oliver.sodi.util.MongoAutoidUtil;
import org.junit.experimental.theories.PotentialAssignment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.util.List;

@Controller
@RequestMapping(value = "/potracking")
@CrossOrigin
public class PoTrackingController {

    @Value("${system.uploadfolder}") private String uploadpath;

    @Autowired
    IPoTrackingService service;

    @Autowired
    MongoAutoidUtil sequence;

    //echo=5
    @GetMapping("")
    @ResponseBody
    public TableResult getAll(@RequestParam int echo)  {

        TableResult<PoTracking> r = new TableResult<PoTracking>();
        List<PoTracking> l = service.findAll();
        r.setEcho(echo);
        r.setFiltered(l.size());
        r.setData(l);
        return r;
    }

    @RequestMapping(value = { "/save" }, method = { RequestMethod.POST }, produces="application/json;charset=UTF-8")
    @ResponseBody
    public String save(@RequestBody PoTracking po)  {
//        service.save(bo);
        if(po.getId()==0)
        {
            po.setId(sequence.getNextSequence("potracking"));
            service.save(po);
        }
        return "{'status':'ok'}";
    }

    @PostMapping("/upload/{section}/{id}")
    //ResponseEntity<byte[]>
    public String generatePriceCsv(HttpServletRequest request, HttpServletResponse response, MultipartFile file, @PathVariable String section,@PathVariable int id) {

        // 0.find tracking object
        List<PoTracking> pos = service.findById(id);
        if(pos!=null&&pos.size()>0)
        {
            PoTracking po = pos.get(0);
            //1.determine file name
            StringBuffer sb = new StringBuffer();
//            /home
            sb.append(this.uploadpath).append(String.valueOf(id)).append("/").append(section).append("/");
            //.append(file.getOriginalFilename())
            File folder = new File(sb.toString());
            if(!folder.exists())
            {
                folder.mkdirs();
            }

            FileOutputStream os = null;
            try {
                os = new FileOutputStream(sb.append(file.getOriginalFilename()).toString());
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
            // 2. update file urls
            switch (section)
            {
                case "proforma":
                    po.getProFormaFileUrls().add(sb.toString());
                    service.save(po);
                    break;
                case "depposit":
                    po.getDepositPaymentUrls().add(sb.toString());
                    service.save(po);
                    break;
                case "balance":
                    po.getBalancePaymentUrls().add(sb.toString());
                    service.save(po);
                    break;
                case "shippingalert":
                    po.getShippingPreAlertUrls().add(sb.toString());
                    service.save(po);
            }

        }
        return "{'status':'ok'}";
    }

    @GetMapping("/download")
    public String remove(HttpServletRequest request, HttpServletResponse response, String url)  {
            File file = new File(url);
            String filename = url.substring(url.lastIndexOf("/")+1,url.length());
            // 如果文件名存在，则进行下载
            if (file.exists()) {

                // 配置文件下载
                response.setHeader("content-type", "application/octet-stream");
                response.setContentType("application/octet-stream");
                response.setHeader("Content-Disposition", "attachment;filename=" + filename);

                byte[] buffer = new byte[1024];
                FileInputStream fis = null;
                BufferedInputStream bis = null;
                try {
                    fis = new FileInputStream(file);
                    bis = new BufferedInputStream(fis);
                    OutputStream os = response.getOutputStream();
                    int i = bis.read(buffer);
                    while (i != -1) {
                        os.write(buffer, 0, i);
                        i = bis.read(buffer);
                    }
                    System.out.println("Download the file successfully!");
                }
                catch (Exception e) {
                    System.out.println("Download the file failed!");
                }
                finally {
                    if (bis != null) {
                        try {
                            bis.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    if (fis != null) {
                        try {
                            fis.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        return "{'status':'ok'}";
    }
}
