package net.oliver.sodi.service;

import net.oliver.sodi.model.PoTracking;

import java.util.List;

public interface IPoTrackingService {
     List<PoTracking> findAll();
     List<PoTracking> findById(int id);
     void save(PoTracking po);
}
