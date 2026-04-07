package com.example.springboot.mapper;

import com.example.springboot.controller.request.BaseRequest;
import com.example.springboot.entity.Borrow;
import com.example.springboot.entity.Restitution;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface BorrowMapper {
    List<Borrow> list();

    List<Borrow> listByCondition(BaseRequest baseRequest);

    List<Restitution> listRestitutionByCondition(BaseRequest baseRequest);

    void save(Borrow obj);

    void saveRestitution(Restitution obj);

    Borrow getById(Integer id);

    void updateById(Borrow user);

    void deleteById(Integer id);

    void deleteRestitutionById(Integer id);

    void updateStatus(String status, String bookNo, String userNo);
}
