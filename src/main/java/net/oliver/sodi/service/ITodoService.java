package net.oliver.sodi.service;

import net.oliver.sodi.model.Todo;

import java.util.List;

public interface ITodoService {

    List<Todo> findAll();
    String save(Todo todo);
    String delete(Todo todo);
}