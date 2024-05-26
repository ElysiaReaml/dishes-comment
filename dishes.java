//这段代码演示了菜品模块的基本功能，包括菜品实体类、服务接口、服务实现类和控制器。您可以根据自己的需求进行扩展，例如添加菜品信息修改、删除、图片上传等功能.
package com.example.canteenapp.model;  //菜品实体类

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.util.List;

@Data                                 //Lombok 注解，自动生成 getter、setter、toString 等方法。
@Document(collection = "dishes")      //指定该实体类映射到 MongoDB 中名为 "dishes" 的集合。
public class Dish {

    @Id                               //标识该字段为主键。
    private String id;
    private String name;
    private String image;            // 可选，菜品图片 URL
    private BigDecimal price;
    @DBRef                           //表示该字段引用了另一个文档，这里表示菜品所属的食堂。
    private Canteen canteen;         // 所属食堂
    private List<String> tags;       // 菜品标签，例如：辣、甜、素食
}


package com.example.canteenapp.service;//菜品服务接口:定义了获取所有菜品 (getAllDishes)、根据 ID 获取菜品 (getDishById)、根据食堂 ID 获取菜品 (getDishesByCanteenId)、搜索菜品 (searchDishes) 和创建菜品 (createDish) 的接口方法。

import com.example.canteenapp.model.Dish;

import java.util.List;

public interface DishService {

    List<Dish> getAllDishes();

    Dish getDishById(String id);

    List<Dish> getDishesByCanteenId(String canteenId);

    List<Dish> searchDishes(String keyword);

    Dish createDish(Dish dish);
}


package com.example.canteenapp.service.impl;                              //菜品服务实现类:各方法实现对应接口定义的功能，例如根据食堂 ID 查询菜品、模糊搜索菜品等。

import com.example.canteenapp.exception.ResourceNotFoundException;
import com.example.canteenapp.model.Canteen;
import com.example.canteenapp.model.Dish;
import com.example.canteenapp.repository.CanteenRepository;
import com.example.canteenapp.repository.DishRepository;
import com.example.canteenapp.service.DishService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.List;

@Service                                                                  //标识该类为服务类，会被 Spring 容器管理。
public class DishServiceImpl implements DishService {

    @Autowired                                                           //自动注入 DishRepository、CanteenRepository 和 MongoTemplate 实例。
    private DishRepository dishRepository;

    @Autowired
    private CanteenRepository canteenRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public List<Dish> getAllDishes() {
        return dishRepository.findAll();
    }

    @Override
    public Dish getDishById(String id) {
        return dishRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Dish not found with id: " + id));
    }

    @Override
    public List<Dish> getDishesByCanteenId(String canteenId) {
        Canteen canteen = canteenRepository.findById(canteenId)
                .orElseThrow(() -> new ResourceNotFoundException("Canteen not found with id: " + canteenId));
        return dishRepository.findByCanteen(canteen);
    }

    @Override
    public List<Dish> searchDishes(String keyword) {
        Query query = new Query();
        query.addCriteria(Criteria.where("name").regex(keyword, "i"));
        return mongoTemplate.find(query, Dish.class);
    }

    @Override
    public Dish createDish(Dish dish) {
        return dishRepository.save(dish);
    }
}


package com.example.canteenapp.controller;//菜品控制器:各方法实现对应接口定义的功能，例如获取所有菜品、根据食堂 ID 获取菜品、模糊搜索菜品等。

import com.example.canteenapp.model.Dish;
import com.example.canteenapp.service.DishService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController                           //标识该类为 RESTful 风格的控制器。
@RequestMapping("/api/dishes")            //设置该控制器的根路径为 /api/dishes。
public class DishController {

    @Autowired
    private DishService dishService;

    @GetMapping("")
    public ResponseEntity<List<Dish>> getAllDishes() {
        List<Dish> dishes = dishService.getAllDishes();
        return new ResponseEntity<>(dishes, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Dish> getDishById(@PathVariable String id) {
        Dish dish = dishService.getDishById(id);
        return new ResponseEntity<>(dish, HttpStatus.OK);
    }

    @GetMapping("/canteen/{canteenId}")
    public ResponseEntity<List<Dish>> getDishesByCanteenId(@PathVariable String canteenId) {
        List<Dish> dishes = dishService.getDishesByCanteenId(canteenId);
        return new ResponseEntity<>(dishes, HttpStatus.OK);
    }

    @GetMapping("/search")
    public ResponseEntity<List<Dish>> searchDishes(@RequestParam String keyword) {
        List<Dish> dishes = dishService.searchDishes(keyword);
        return new ResponseEntity<>(dishes, HttpStatus.OK);
    }

    @PostMapping("")
    public ResponseEntity<Dish> createDish(@RequestBody Dish dish) {
        Dish createdDish = dishService.createDish(dish);
        return new ResponseEntity<>(createdDish, HttpStatus.CREATED);
    }
}
