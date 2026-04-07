package com.example.springboot.service;

import com.example.springboot.controller.request.BaseRequest;
import com.example.springboot.entity.Borrow;
import com.example.springboot.entity.Restitution;
import com.github.pagehelper.PageInfo;

import java.util.List;

public interface IBorrowService {
    List<Borrow> list();

    PageInfo<Borrow> page(BaseRequest baseRequest);

    void save(Borrow obj);

    PageInfo<Restitution> pageRestitution(BaseRequest baseRequest);

    void saveRestitution(Restitution obj);
    Borrow getById(Integer id);

    void update(Borrow user);

    void deleteById(Integer id);

    void deleteRestitutionById(Integer id);
}
