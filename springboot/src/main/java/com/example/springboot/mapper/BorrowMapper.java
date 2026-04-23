package com.example.springboot.mapper;

import com.example.springboot.controller.request.BaseRequest;
import com.example.springboot.entity.Borrow;
import com.example.springboot.entity.Restitution;
import com.example.springboot.mapper.po.BorrowRestitutionCountPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

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

    void updateStatus(@Param("status") String status, @Param("id") Integer id);

    List<BorrowRestitutionCountPO> getCountByTimeRange(@Param("timeRange") String timeRange, @Param("type") int type);  // 1 borrow  2 return
}
