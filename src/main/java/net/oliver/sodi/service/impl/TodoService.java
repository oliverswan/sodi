package net.oliver.sodi.service.impl;

import net.oliver.sodi.dao.ITodoDao;
import net.oliver.sodi.model.Todo;
import net.oliver.sodi.service.ITodoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TodoService implements ITodoService {

    @Autowired
    ITodoDao dao;

    @Override
    public List<Todo> findAll() {
        return dao.findAll();
    }

    @Override
    public String save(Todo todo) {
        dao.save(todo);
        return "{\"status\"：\"ok\"}";
    }

    @Override
    public String delete(Todo todo) {
        dao.delete(todo);
        return "{\"status\"：\"ok\"}";
    }
}
