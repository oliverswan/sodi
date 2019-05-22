package net.oliver.sodi.model;

import java.util.List;

public class TableResult<T> {
    List<T> data;
    int echo;
    int filtered;

    public List<T> getData() {
        return data;
    }

    public void setData(List<T> data) {
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
