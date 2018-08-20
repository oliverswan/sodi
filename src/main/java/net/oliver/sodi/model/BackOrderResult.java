package net.oliver.sodi.model;

import java.util.List;

public class BackOrderResult {


    List<Backorder> data;
    int echo;
    int filtered;

    public List<Backorder> getData() {
        return data;
    }

    public void setData(List<Backorder> data) {
        this.data = data;
    }

    public int getEcho() {
        return echo;
    }

    public void setEcho(int echo) {
        this.echo = echo;
    }

    public int getFiltered() {
        return filtered;
    }

    public void setFiltered(int filtered) {
        this.filtered = filtered;
    }
}
