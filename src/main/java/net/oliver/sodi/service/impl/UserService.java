package net.oliver.sodi.service.impl;

import net.oliver.sodi.dao.IUserDao;
import net.oliver.sodi.model.User;
import net.oliver.sodi.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService implements IUserService {
    @Autowired
    IUserDao dao;

    @Override
    public User findByName(String name) {
        return dao.findByName(name);
    }

    @Override
    public void save(User user) {
        dao.save(user);
    }
}
