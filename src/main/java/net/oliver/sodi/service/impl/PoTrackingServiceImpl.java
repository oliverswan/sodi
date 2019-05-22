package net.oliver.sodi.service.impl;

import net.oliver.sodi.dao.IPoTrackingDao;
import net.oliver.sodi.model.PoTracking;
import net.oliver.sodi.service.IPoTrackingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PoTrackingServiceImpl implements IPoTrackingService {

    @Autowired
    private IPoTrackingDao dao;

    @Override
    public List<PoTracking> findAll() {
        return dao.findAll();
    }

    @Override
    public List<PoTracking> findById(int id) {
        return dao.findById(id);
    }

    @Override
    public void save(PoTracking po) {
        dao.save(po);
    }
}
