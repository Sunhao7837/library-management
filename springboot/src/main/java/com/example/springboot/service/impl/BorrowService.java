package com.example.springboot.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.example.springboot.controller.request.BaseRequest;
import com.example.springboot.entity.Book;
import com.example.springboot.entity.Borrow;
import com.example.springboot.entity.Restitution;
import com.example.springboot.entity.User;
import com.example.springboot.exception.ServiceException;
import com.example.springboot.mapper.BookMapper;
import com.example.springboot.mapper.BorrowMapper;
import com.example.springboot.mapper.UserMapper;
import com.example.springboot.mapper.po.BorrowRestitutionCountPO;
import com.example.springboot.service.IBorrowService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
@Slf4j

public class BorrowService implements IBorrowService {

    @Resource
    BorrowMapper borrowMapper;

    @Autowired
    UserMapper userMapper;

    @Autowired
    BookMapper bookMapper;

    @Override
    public List<Borrow> list() {
        return borrowMapper.list();
    }

    @Override
    public PageInfo<Borrow> page(BaseRequest baseRequest) {
        PageHelper.startPage(baseRequest.getPageNum(), baseRequest.getPageSize());
        List<Borrow> borrows = borrowMapper.listByCondition(baseRequest);
        for(Borrow borrow : borrows) {
            LocalDate returnDate = borrow.getReturnDate();
            LocalDate now = LocalDate.now();
            if(now.plusDays(1).isEqual(returnDate)) { // 当前日期比归还的日期少一天
                borrow.setNote("即将到期");
            } else if(now.isEqual(returnDate)) {
                borrow.setNote("已到期");
            } else if(now.isAfter(returnDate)) {
                borrow.setNote("已过期");
            } else {
                borrow.setNote("正常");
            }
        }
        return new PageInfo<>(borrows);
    }

    @Override
    @Transactional
    public void save(Borrow obj) {
        // 1. 校验用户的积分是否足够
        String userNo = obj.getUserNo();
        User user = userMapper.getByUsername(userNo);
        if(Objects.isNull(user)) {
            throw new ServiceException("用户不存在");
        }
        userMapper.getByUsername(userNo);
        // 2. 校验图书的数量是否足够
        Book book = bookMapper.getByNo(obj.getBookNo());
        if(Objects.isNull(book)) {
            throw new ServiceException("所借图书不存在");
        }
        // 3. 校验图书数量
        if(book.getNums() < 1) {
            throw new ServiceException("图书数量不足");
        }
        Integer account = user.getAccount();
        Integer score = book.getScore() * obj.getDays(); // score = 借 1本的积分 * 天数
        // 4. 校验用户账户余额
        if(score > account) {
            throw new ServiceException("用户积分不足");
        }
        // 5. 更新用户余额
        user.setAccount(user.getAccount() - score);
        userMapper.updateById(user);
        // 6. 更新图书的数量
        book.setNums((book.getNums()) - 1);
        bookMapper.updateById(book);

        obj.setReturnDate(LocalDate.now().plus(obj.getDays(), ChronoUnit.DAYS)); // 当前的日期加 days 得到归还的日期
        obj.setScore(score);
        // 7. 新增借书记录
        borrowMapper.save(obj);
    }

    @Override
    public PageInfo<Restitution> pageRestitution(BaseRequest baseRequest) {
        PageHelper.startPage(baseRequest.getPageNum(), baseRequest.getPageSize());
        return new PageInfo<>(borrowMapper.listRestitutionByCondition(baseRequest));
    }

    // 还书逻辑
    @Transactional
    @Override
    public void saveRestitution(Restitution obj) {
        // 改状态
        obj.setStatus("已归还");
        borrowMapper.updateStatus("已归还", obj.getId()); // obj.getId() 是前端传来的借书id

        // 图书数量增加
//        obj.setId(null); // 新数据
        obj.setRealDate(LocalDate.now());
        borrowMapper.saveRestitution(obj);

        bookMapper.updateNumByNo(obj.getBookNo());

        // 返还和扣除用户积分
        Book book = bookMapper.getByNo(obj.getBookNo());
        if(book != null) {
            long until = 0;
            if(obj.getRealDate().isBefore(obj.getReturnDate())) {
                until = obj.getRealDate().until(obj.getReturnDate(), ChronoUnit.DAYS);
            } else if(obj.getRealDate().isAfter(obj.getReturnDate())) {
                until = -obj.getReturnDate().until(obj.getRealDate(), ChronoUnit.DAYS);
            }
            int score = (int) until * book.getScore(); // 获取归还的积分
            User user = userMapper.getByUsername(obj.getUserNo());
            int account = user.getAccount() + score;
            user.setAccount(account);
            if(account < 0) {
                user.setStatus(false);
            }
            userMapper.updateById(user);
        }
    }

    @Override
    public Borrow getById(Integer id) {
        return borrowMapper.getById(id);
    }

    @Override
    public void update(Borrow obj) {
        obj.setUpdatetime(LocalDate.now());
        borrowMapper.updateById(obj);
    }

    @Override
    public void deleteById(Integer id) {
        borrowMapper.deleteById(id);
    }

    @Override
    public void deleteRestitutionById(Integer id) {
        borrowMapper.deleteRestitutionById(id);
    }

    @Override
    public Map<String, Object> getCountByTimeRange(String timeRange) {
        Map<String, Object> map = new HashMap<>();
        Date today = new Date();
        List<DateTime> dateRange;
        switch (timeRange) {
            case "week":
                // offsetDay 计算时间的一个工具方法
                // rangeToList 返回从开始时间到结束时间的一个时间范围
                dateRange = DateUtil.rangeToList(DateUtil.offsetDay(today, -6), today, DateField.DAY_OF_WEEK);
                break;
            case "month":
                dateRange = DateUtil.rangeToList(DateUtil.offsetDay(today, -29), today, DateField.DAY_OF_MONTH);
                break;
            case "month2":
                dateRange = DateUtil.rangeToList(DateUtil.offsetDay(today, -59), today, DateField.DAY_OF_MONTH);
                break;
            case "month3":
                dateRange = DateUtil.rangeToList(DateUtil.offsetDay(today, -89), today, DateField.DAY_OF_MONTH);
                break;
            default:
                dateRange = new ArrayList<>();
        }
        // datetimeToDateStr 把 DataTime类型的List转换成一个String的List
        List<String> dateStrRange = datetimeToDateStr(dateRange);
        map.put("date", dateStrRange); // x轴的日期数据生产完毕

        // timeRange = week month
        // getCountByTimeRange 不会统计数据库没有的日期，比如11.4没有，他不会返回date=2022-11-04,count=0 这个数据
        List<BorrowRestitutionCountPO> borrowCount = borrowMapper.getCountByTimeRange(timeRange, 1);
        List<BorrowRestitutionCountPO> returnCount = borrowMapper.getCountByTimeRange(timeRange, 2);
        map.put("borrow", countList(borrowCount, dateStrRange));
        map.put("restitution", countList(returnCount, dateStrRange));
        return map;
    }

    private List<String> datetimeToDateStr(List<DateTime> dateTimeList) {
        List<String> list = CollUtil.newArrayList();
        if (CollUtil.isEmpty(dateTimeList)) {
            return list;
        }
        for (DateTime dateTime : dateTimeList) {
            String date = DateUtil.formatDate(dateTime);
            list.add(date);
        }
        return list;
    }
    private List<Integer> countList(List<BorrowRestitutionCountPO> countPOList, List<String> dateRange) {
        List<Integer> list = CollUtil.newArrayList();
        if (CollUtil.isEmpty(countPOList)) {
            return list;
        }
        for (String date : dateRange) {
            // .map(BorrowRestitutionCountPO::getCount)取出对象里的count值
            // orElse(0) 对没匹配的数据返回0
            // “2022-11-04” 没有的话就返回0
            Integer count = countPOList.stream().filter(countPO -> date.equals(countPO.getDate()))
                    .map(BorrowRestitutionCountPO::getCount).findFirst().orElse(0);
            list.add(count);
        }
        // 最后返回的List的元素个数会跟dateRange的元素个数完全一样
//        dateRange: [
//                "2022-10-30",
//                "2022-10-31",
//                "2022-11-01",
//                "2022-11-02",
//                "2022-11-03",
//                "2022-11-04",
//                "2022-11-05",
//        ],
//        borrow: [
//                    0,
//                    0,
//                    2,
//                    1,
//                    0,
//                    1,
//                    3
//                ]
        return list;
    }

}
