package net.oliver.sodi.model;

import java.util.HashMap;
import java.util.Map;

public class BackOrderReportEntry  implements Comparable<BackOrderReportEntry> {
    private String itemCode;
    private int total;
    private Map<String,Integer> distribute = new HashMap<String,Integer>();

    public void addDistributeForCustomer(String customName,Integer value)
    {
        int x =value;
        if(this.distribute.containsKey(customName))
        {
            x += this.distribute.get(customName);
        }
        this.distribute.put(customName,x);
    }

    public void addToTal(int v)
    {
        this.total +=v;
    }

    public String getItemCode() {
        return itemCode;
    }

    public void setItemCode(String itemCode) {
        this.itemCode = itemCode;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public Map<String, Integer> getDistribute() {
        return distribute;
    }

    public void setDistribute(Map<String, Integer> distribute) {
        this.distribute = distribute;
    }

    @Override
    public int compareTo(BackOrderReportEntry o) {
        return this.getItemCode().compareTo(o.getItemCode());
    }
}
