package net.oliver.sodi.controller;

import com.itextpdf.text.pdf.PdfWriter;
import com.opencsv.CSVReader;
import net.oliver.sodi.dao.IItemDao;
import net.oliver.sodi.model.*;
import net.oliver.sodi.service.IItemService;
import net.oliver.sodi.service.ISoldHistoryService;
import net.oliver.sodi.util.MathUtil;
import net.oliver.sodi.util.MongoAutoidUtil;
import net.oliver.sodi.util.SystemStatus;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping(value = "/api/items")
public class ItemController {

    @Autowired
    IItemService service;

    @Autowired
    IItemDao dao;

    @Autowired
    MongoTemplate mongoTemplate;

    @Autowired
    MongoAutoidUtil sequence;

    @Autowired
    ISoldHistoryService soldHistoryService;

    private static double totalValues = 0;
    private static boolean recal = false;

    public static void reSetFlag()
    {
        recal = true;
    }

    @GetMapping("")
    @ResponseBody
    public ItemResult getAll(@RequestParam int echo)  {

        ItemResult r = new ItemResult();
        List<Item> l = service.findAll();
        r.setEcho(echo);
        r.setFiltered(l.size());
        r.setData(l);
        return r;
    }

    @GetMapping("/{code}")
    @ResponseBody
    public List<Item> getItem(@PathVariable String code )  {

        return service.findByCode(code);
    }


    @GetMapping("/query/{criteria}")
    @ResponseBody
    public List<Item> autoComplete(@PathVariable String criteria )  {

        return service.findItemAutoComplete(criteria);
    }

    @GetMapping("/recalculate/{monthAve}")
    @ResponseBody
    public String importXX(@PathVariable int monthAve )  {

        List<Item> all = service.findAll();
        for(Item item:all)
        {
            item.reCalculateBasedOnThisYear(monthAve);
        }
        service.save(all);
        return "{'status':'ok'}";
    }

    @GetMapping("/salesmost/{number}")
    @ResponseBody
    public SalesResult autoComplete(@PathVariable int number )  {

        SalesResult result = new SalesResult();
        List<Item> r = service.findAllOrderBySoldThisYear();
        String[] l = new String[number];
        int[] d = new int[number];
        for(int i=0;i<number;i++)
        {
            l[i] = r.get(i).getCode();
            d[i] = r.get(i).getSoldThisYear();
        }
        result.setLabels(l);
        result.setDatasets(d);
        return result;
    }

    @GetMapping("/receive/{id}")
    @ResponseBody
    public String receive(@PathVariable int id )  {
        service.receiveOneItem(id);
        return "{'status':'ok'}";
    }

    @RequestMapping(value = { "/save" }, method = { RequestMethod.POST }, produces="application/json;charset=UTF-8")
    @ResponseBody
    public String addInvoices(@RequestBody Item item)  {
        service.save(item);
        return "{'status':'ok'}";
    }

    @RequestMapping(value = { "/add" }, method = { RequestMethod.POST }, produces="application/json;charset=UTF-8")
    @ResponseBody
    public String save(@RequestBody Item item)  {
        item.setId(sequence.getNextSequence("item"));
        service.save(item);
        return "{'status':'ok'}";
    }

    @GetMapping("/import")
    @ResponseBody
    public String importXX( )  {


        CSVReader reader;
        try {
            File file = new File("D://InventoryItems-20180919.csv");
            reader = new CSVReader(new FileReader(file));

            String[] nextLine = reader.readNext();
            while ((nextLine = reader.readNext()) != null) {

                String code = nextLine[0];
                if(StringUtils.isBlank(code))
                {
                    continue;
                }
                if(code.equals("AC614.030"))
                {
                    System.out.println("xx");
                }

                List<Item> l = service.findByCode(code.trim());
                if(l.size()>0)
                {
                    Query query=new Query(Criteria.where("code").is(code));
                    if(!StringUtils.isBlank(nextLine[1]))
                    {
                        String name = nextLine[1].trim();
                        Update update = Update.update("name",name);
                        mongoTemplate.updateFirst(query, update, Item.class);
                    }

                    if(!StringUtils.isBlank(nextLine[3]))
                    {
                        String cprice = nextLine[3].replaceAll(",","");
                        Update update = Update.update("cprice", Double.parseDouble(MathUtil.df.format(Double.parseDouble(cprice))));
                        mongoTemplate.updateFirst(query, update, Item.class);

//                        String cprice = nextLine[3].replaceAll(",","");
//                        l.get(0).setCprice(Double.parseDouble(MathUtil.df.format(Double.parseDouble(cprice))));
                    }
                    if(!StringUtils.isBlank(nextLine[7]))
                    {
                        String price = nextLine[7].replaceAll(",","");
                        double pr = Double.parseDouble(MathUtil.df.format(Double.parseDouble(price)));
                        Update update = Update.update("price",pr);
                        mongoTemplate.updateFirst(query, update, Item.class);
                    }


                    System.out.println("##############Update : "+ code);
                }else{
                    Item item = new Item();
                    item.setId(sequence.getNextSequence("item"));

                    if(!StringUtils.isBlank(nextLine[1]))
                    {
                        String name = nextLine[1];
                        item.setName(name);
                    }else{
                        item.setName(code);
                    }

                    if(!StringUtils.isBlank(nextLine[3]))
                    {
                        String cprice = nextLine[3].replaceAll(",","");
                        item.setCprice(Double.parseDouble(MathUtil.df.format(Double.parseDouble(cprice))));
                    }
                    if(!StringUtils.isBlank(nextLine[7]))
                    {
                        String price = nextLine[7].replaceAll(",","");
                        item.setPrice(Double.parseDouble(MathUtil.df.format(Double.parseDouble(price))));
                    }
                    item.setCode(code);
                    service.save(item);
                    System.out.println("?????????????????Save : "+ code);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "OK";
    }

    @GetMapping("/import2")
    @ResponseBody
    public String importXX2( )  {


        CSVReader reader;
        try {
            File file = new File("D://InventoryItems-20180919.csv");
            reader = new CSVReader(new FileReader(file));

            String[] nextLine = reader.readNext();
            while ((nextLine = reader.readNext()) != null) {

                String code = nextLine[0];

                List<Item> l = service.findByCode(code.trim());
                if(l.size()>0)
                {
                    if(!StringUtils.isBlank(nextLine[1]))
                    {
                        String name = nextLine[1];
                        l.get(0).setName(name.trim());
                    }

                    if(!StringUtils.isBlank(nextLine[3]))
                    {
                        String cprice = nextLine[3].replaceAll(",","");
                        l.get(0).setCprice(Double.parseDouble(MathUtil.df.format(Double.parseDouble(cprice))));
                    }
                    if(!StringUtils.isBlank(nextLine[7]))
                    {
                        String price = nextLine[7].replaceAll(",","");
                        l.get(0).setPrice(Double.parseDouble(MathUtil.df.format(Double.parseDouble(price))));
                    }
                    l.get(0).setCode(code.trim());

                    service.save(l.get(0));
                }else{
                    Item item = new Item();
                    item.setId(sequence.getNextSequence("item"));

                    if(!StringUtils.isBlank(nextLine[1]))
                    {
                        String name = nextLine[1];
                        item.setName(name.trim());
                    }else{
                        item.setName(code.trim());
                    }

                    if(!StringUtils.isBlank(nextLine[3]))
                    {
                        String cprice = nextLine[3].replaceAll(",","");
                        item.setCprice(Double.parseDouble(MathUtil.df.format(Double.parseDouble(cprice))));
                    }
                    if(!StringUtils.isBlank(nextLine[7]))
                    {
                        String price = nextLine[7].replaceAll(",","");
                        item.setPrice(Double.parseDouble(MathUtil.df.format(Double.parseDouble(price))));
                    }
                    item.setCode(code.trim());
                    service.save(item);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "OK";
    }

    @GetMapping("/search")
    @ResponseBody
    public List<Item> search(String criteria,String operator,String criteriaValue )  {
            if(operator.equals(">"))
            {
                if(criteria.equals("msoh"))
                {
                    return dao.findByMsohGreaterThanEqual(Double.parseDouble(criteriaValue));
                }else if(criteria.equals("spm"))
                {
                    return dao.findBySpmGreaterThanEqual(Double.parseDouble(criteriaValue));
                }
            }else if(operator.equals("="))
            {
                if(criteria.equals("msoh"))
                {
                    return dao.findByMsohEquals(Double.parseDouble(criteriaValue));
                }else if(criteria.equals("spm"))
                {
                    return dao.findBySpmEquals(Double.parseDouble(criteriaValue));
                }
            }else if(operator.equals("<"))
            {
                if(criteria.equals("msoh"))
                {
                    return dao.findByMsohLessThanEqual(Double.parseDouble(criteriaValue));
                }else if(criteria.equals("spm"))
                {
                    return dao.findBySpmLessThanEqual(Double.parseDouble(criteriaValue));
                }
            }
            return new ArrayList(0);
    }

    @GetMapping("/price")
    @ResponseBody
    public String price(double rate,double freight, double duty )  {

        List<Item> all = service.findAll();
        for(Item item : all)
        {
            item.updateProfit(0, 0,rate,freight, duty);
            service.save(item);
        }
        return "ok";
    }

    @GetMapping("/statistic")
    @ResponseBody
    public Statistic statistic()  {
        if(totalValues == 0 || recal == true)
        {
            totalValues = 0;
            List<Item> all = service.findAll();
            for(Item item : all)
            {
                item.reCalValue();
                totalValues += item.getValue();
            }
            recal = false;
        }
        Statistic s = new Statistic();
        s.setTotalvalues(totalValues);
        return s;
    }

    @GetMapping("/recaltotalvalues")
    @ResponseBody
    public String recaltotalvalues()  {
            totalValues=0;
            List<Item> all = service.findAll();
            for(Item item : all)
            {
                item.reCalValue();
                totalValues += item.getValue();
            }
            service.save(all);

        return "ok";
    }

    //  restock 主要是重新计算msoh
    @GetMapping("/restock")
    @ResponseBody
    public String restock()  {
        List<Item> all = service.findAll();
        for(Item item : all)
        {
            int stock = item.getStock();
            item.setStock(stock);
        }
        dao.save(all);
        return "ok";
    }

    @GetMapping("/del1")
    @ResponseBody
    public String del1()  {
        List<Item> items = service.findForDel1();
        service.delete(items);
        return "ok";
    }

    @GetMapping("/landed")
    @ResponseBody
    public String landed()  {
        List<Item> items = service.findForLandedZero();
        for(Item item : items)
        {
            System.out.println(item.getCode());
        }
        return "ok";
    }

    // 销售历史报告
    @RequestMapping(value = "/updatesales/{month}",method = RequestMethod.GET)
    public void  salehistory( @PathVariable int month/*@RequestParam int num*/) throws Exception {
        // 0.根据参数
        List<SoldHistory> result = soldHistoryService.findAllForSalesHistory(month);
        List<String> ls = SystemStatus.getLastMonthLabel(month);
        List<Item> updates = new ArrayList<Item>();
        for(SoldHistory en : result)
        {
            Map<Integer,Integer> curHis = en.getHis();

            int total = 0;
            for(int x=1;x<=month;x++)
            {
                Integer v = curHis.get(Integer.parseInt(ls.get(x-1)));
                if(v == null)
                    v = 0;

                total+=v;
            }
            int av = total/month;

            List<Item> its = service.findByCode(en.getCode());

            if(its.size()>0)
            {
                Item item = its.get(0);
                // av
                item.setSpm(av);
                // mosh
                if(item.getStock()<=0 || av == 0)
                {
                    item.setMsoh(0);
                }else{
                    item.setMsoh(its.get(0).getStock()/av);//msoh
                }
                updates.add(item);
                System.out.println(item.getCode()+"   "+item.getSpm());
            }
        }
        service.save(updates);

    }
}
