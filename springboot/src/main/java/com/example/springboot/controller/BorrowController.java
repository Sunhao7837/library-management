package com.example.springboot.controller;

import com.example.springboot.common.Result;
import com.example.springboot.controller.request.BorrowPageRequest;
import com.example.springboot.entity.Borrow;
import com.example.springboot.entity.Restitution;
import com.example.springboot.service.IBorrowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/borrow")
public class BorrowController {

    @Autowired
    IBorrowService borrowService;

    @PostMapping("/save")
    public Result save(@RequestBody Borrow obj) {
        borrowService.save(obj);
        return Result.success();
    }

    @PutMapping("/update")
    public Result update(@RequestBody Borrow obj) {
        borrowService.update(obj);
        return Result.success();
    }

    @DeleteMapping("/delete/{id}")
    public Result delete(@PathVariable Integer id) {
        borrowService.deleteById(id);
        return Result.success();
    }

    @GetMapping("/{id}")
    public Result getById(@PathVariable Integer id) {
        Borrow obj = borrowService.getById(id);
        return Result.success(obj);
    }

    @GetMapping("/list")
    public Result list() {
        List<Borrow> list = borrowService.list();
        return Result.success(list);
    }

    @GetMapping("/page")
    public Result page(BorrowPageRequest pageRequest) {
        return Result.success(borrowService.page(pageRequest));
    }

    @PostMapping("/saveRestitution")
    public Result saveRestitution(@RequestBody Restitution obj) {
        borrowService.saveRestitution(obj);
        return Result.success();
    }

    @DeleteMapping("/deleteRestitution/{id}")
    public Result deleteRestitution(@PathVariable Integer id) {
        borrowService.deleteRestitutionById(id);
        return Result.success();
    }

    @GetMapping("/pageRestitution")
    public Result pageRestitution(BorrowPageRequest pageRequest) {
        return Result.success(borrowService.pageRestitution(pageRequest));
    }

    // timeRange: week month month2 month3
    @GetMapping("/lineCharts/{timeRange}")
    public Result lineCharts(@PathVariable String timeRange) {
        return Result.success(borrowService.getCountByTimeRange(timeRange));
    }
}
