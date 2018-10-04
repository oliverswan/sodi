package net.oliver.sodi.service;

import net.oliver.sodi.model.SoldHistory;

public interface ISoldHistoryService {

    public void save(SoldHistory sh);
    public void addSoldTothisMonth(String customer,String code,int month,int quantity);
}
