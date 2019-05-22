package net.oliver.sodi.controller;

import net.oliver.sodi.model.Backorder;
import net.oliver.sodi.model.PoTracking;
import net.oliver.sodi.model.TableResult;
import net.oliver.sodi.service.IPoTrackingService;
import org.junit.experimental.theories.PotentialAssignment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping(value = "/potracking")
@CrossOrigin
public class PoTrackingController {

    @Autowired
    IPoTrackingService service;

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

    @RequestMapping(value = { "/update" }, method = { RequestMethod.POST }, produces="application/json;charset=UTF-8")
    @ResponseBody
    public String update(@RequestBody PoTracking po)  {
//        service.save(bo);
        return "{'status':'ok'}";
    }


    @PostMapping("/upload/{section}/{id}")
    //ResponseEntity<byte[]>
    public void generatePriceCsv(HttpServletRequest request, HttpServletResponse response, MultipartFile file, @PathVariable String section,@PathVariable int id) {

        // 0.find tracking object
        List<PoTracking> pos = service.findById(id);
        if(pos!=null&&pos.size()>1)
        {
            PoTracking po = pos.get(0);
            //1.determine file name
            StringBuffer sb = new StringBuffer();
//            /home
            sb.append("/home/sodi/upload/").append(String.valueOf(id)).append("/").append(section).append("/");
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
                    break;
                case "depposit":
                    po.getDepositPaymentUrls().add(sb.toString());
                    break;
                case "balance":
                    po.getBalancePaymentUrls().add(sb.toString());
                    break;
                case "shippingalert":
                    po.getShippingPreAlertUrls().add(sb.toString());
            }

        }
    }
}
