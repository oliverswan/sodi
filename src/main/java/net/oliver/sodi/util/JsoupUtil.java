package net.oliver.sodi.util;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public class JsoupUtil {

    public static void getInvoice(String html)  {
        // [^attr]: 利用属性名前缀来查找元素，比如：可以用[^data-] 来查找带有HTML5 Dataset属性的元素
        // [attr=value]: 利用属性值来查找元素，比如：[width=500]
//        [attr^=value], [attr$=value], [attr*=value]: 利用匹配属性值开头、结尾或包含属性值来查找元素，比如：[href*=/path/]
        /*File file = new File("D:\\sodi invoice.txt");//定义一个file对象，用来初始化FileReader
        FileReader reader = new FileReader(file);//定义一个fileReader对象，用来初始化BufferedReader
        BufferedReader bReader = new BufferedReader(reader);//new一个BufferedReader对象，将文件内容读取到缓存
        StringBuilder sb = new StringBuilder();//定义一个字符串缓存，将字符串存放缓存中
        String s = "";
        while ((s =bReader.readLine()) != null) {//逐行读取文件内容，不读取换行符和末尾的空格
            sb.append(s + "\n");//将读取的字符串添加换行符后累加存放在缓存中
        }
        bReader.close();
        html = sb.toString();*/

        try {


        html = html.replaceAll("(?i)<br[^>]*>", "br2n");

        Document doc = Jsoup.parse(html);
        Elements customerInfo = doc.select("p.MsoNormal"); // class为MsoNormal的p标签

        String orderNumber = customerInfo.get(5).text();
        String date = customerInfo.get(7).text();
        Elements customerInfo2 = doc.select("span:contains(Bill to:)");
        String customerName = customerInfo2.get(0).parent().parent().text().split("br2n")[1].trim();
        Elements tables = doc.select("table.MsoNormalTable");//<table class="MsoNormalTable"
        Elements TRs = tables.get(4).select("tr");

        for(int i=1;i<TRs.size()-2;i++)
        {
            Elements tds = TRs.get(i).select("td");
            System.out.println(tds.get(0).text()+" : "+tds.get(1).text());
        }
        }catch (Exception e)
        {
            e.printStackTrace();
        }

    }

    public static void main(String[] args) {
        try {
            JsoupUtil.getInvoice("");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
