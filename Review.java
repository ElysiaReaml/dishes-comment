//以下是评价模块的代码示例，包含了对食堂和菜品的评价功能：
//这段代码演示了评价模块的基本功能，包括评价实体类、服务接口、服务实现类和控制器。您可以根据自己的需求进行扩展，例如添加评价修改、删除、点赞等功能。
package com.example.canteenapp.model;                        //评价实体类 

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data                                                       //Lombok 注解，自动生成 getter、setter、toString 等方法。
@Document(collection = "reviews")                           //指定该实体类映射到 MongoDB 中名为 "reviews" 的集合。

public class Review {

    @Id                                                    //标识该字段为主键。
    private String id;

    private String content;
    private int rating; // 评分，例如：1-5 星

    @DBRef                                                //表示该字段引用了另一个文档，这里表示评价所属的用户、食堂或菜品。
    private User user;

    // 可以选择对食堂或菜品进行评价，但不能同时评价两者
    @DBRef
    private Canteen canteen;                              //可以为空，表示对食堂的评价。
    @DBRef
    private Dish dish;                                    //可以为空，表示对菜品的评价。

    private LocalDateTime createdAt;                      //评价创建时间。
}


package com.example.canteenapp.service;                   //评价服务接口:定义了创建评价 (createReview)、根据食堂 ID 获取评价 (getReviewsByCanteenId) 和根据菜品 ID 获取评价 (getReviewsByDishId) 的接口方法。

import com.example.canteenapp.model.Review;

import java.util.List;

public interface ReviewService {

    Review createReview(Review review);

    List<Review> getReviewsByCanteenId(String canteenId);

    List<Review> getReviewsByDishId(String dishId);
}


package com.example.canteenapp.service.impl;                            //评价服务实现类 

import com.example.canteenapp.exception.ResourceNotFoundException;
import com.example.canteenapp.model.Canteen;
import com.example.canteenapp.model.Dish;
import com.example.canteenapp.model.Review;
import com.example.canteenapp.repository.CanteenRepository;
import com.example.canteenapp.repository.DishRepository;
import com.example.canteenapp.repository.ReviewRepository;
import com.example.canteenapp.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service                                                                //标识该类为服务类，会被 Spring 容器管理。
public class ReviewServiceImpl implements ReviewService {

    @Autowired                                                          //自动注入 ReviewRepository、CanteenRepository 和 DishRepository 实例。
    private ReviewRepository reviewRepository;

    @Autowired
    private CanteenRepository canteenRepository;

    @Autowired
    private DishRepository dishRepository;

    @Override
    public Review createReview(Review review) {                         //createReview 方法: 设置评价创建时间后，将评价数据保存到数据库。
        review.setCreatedAt(LocalDateTime.now());
        return reviewRepository.save(review);
    }

    @Override
    public List<Review> getReviewsByCanteenId(String canteenId) {       //getReviewsByCanteenId 方法: 根据食堂 ID 获取评价列表。
        Canteen canteen = canteenRepository.findById(canteenId)
                .orElseThrow(() -> new ResourceNotFoundException("Canteen not found with id: " + canteenId));
        return reviewRepository.findByCanteen(canteen);
    }

    @Override
    public List<Review> getReviewsByDishId(String dishId) {            //getReviewsByDishId 方法: 根据菜品 ID 获取评价列表。
        Dish dish = dishRepository.findById(dishId)
                .orElseThrow(() -> new ResourceNotFoundException("Dish not found with id: " + dishId));
        return reviewRepository.findByDish(dish);
    }
}


package com.example.canteenapp.controller;                                                               //评价控制器

import com.example.canteenapp.model.Review;
import com.example.canteenapp.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController                                                                                          //标识该类为 RESTful 风格的控制器。
@RequestMapping("/api/reviews")                                                                          //设置该控制器的根路径为 /api/reviews。

public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    @PostMapping("")
    public ResponseEntity<Review> createReview(@RequestBody Review review) {                             //createReview 方法: 处理创建评价请求，请求路径为 /api/reviews，请求方式为 POST。
        Review createdReview = reviewService.createReview(review);
        return new ResponseEntity<>(createdReview, HttpStatus.CREATED);
    }

    @GetMapping("/canteen/{canteenId}")
    public ResponseEntity<List<Review>> getReviewsByCanteenId(@PathVariable String canteenId) {         //getReviewsByCanteenId 方法: 处理根据食堂 ID 获取评价请求，请求路径为 /api/reviews/canteen/{canteenId}，请求方式为 GET。
        List<Review> reviews = reviewService.getReviewsByCanteenId(canteenId);
        return new ResponseEntity<>(reviews, HttpStatus.OK);
    }

    @GetMapping("/dish/{dishId}")
    public ResponseEntity<List<Review>> getReviewsByDishId(@PathVariable String dishId) {              //getReviewsByDishId 方法: 处理根据菜品 ID 获取评价请求，请求路径为 /api/reviews/dish/{dishId}，请求方式为 GET。
        List<Review> reviews = reviewService.getReviewsByDishId(dishId);
        return new ResponseEntity<>(reviews, HttpStatus.OK);
    }
}
