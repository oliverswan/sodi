package net.oliver.sodi.service;

import net.oliver.sodi.model.User;

public interface IUserService {
     User findByName(String name);
     void save(User user);
}
