package net.oliver.sodi.service;

import net.oliver.sodi.model.SoldHistory;

import java.util.List;

public interface ISoldHistoryService {

    public void save(SoldHistory sh);
    public void save(List<SoldHistory> sh);
    public void addSoldTothisMonth(String customer,String code,int month,int quantity);
    public SoldHistory getSoldHistory(String customer,String code,int month,int quantity);
    List<SoldHistory> findAllForSalesHistory(int month);
}
