package net.oliver.sodi.service.impl;

import net.oliver.sodi.dao.ISoldHistoryDao;
import net.oliver.sodi.model.SoldHistory;
import net.oliver.sodi.service.ISoldHistoryService;
import net.oliver.sodi.util.MongoAutoidUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SoldHistoryService implements ISoldHistoryService {

    @Autowired
    ISoldHistoryDao dao;

    @Autowired
    MongoAutoidUtil sequence;

    @Override
    public void save(SoldHistory sh) {
        dao.save(sh);
    }

    @Override
    public void addSoldTothisMonth(String customer,String code,int month,int quantity) {
        // 0 find oderhistory
            List<SoldHistory> list = dao.findByCode(code);
            SoldHistory sh = null;
            if(list.size()>0)
            {
                sh = list.get(0);
            }else{
                sh= new SoldHistory();
                sh.setId(sequence.getNextSequence("soldhistory"));
                sh.setCode(code);
            }
            sh.updateSoldQuantity(customer,month,quantity);
            this.save(sh);
    }
}
