//以下是食堂模块的代码示例，包含了食堂信息获取和搜索功能的实现：
//这段代码演示了食堂模块的基本功能，包括食堂实体类、服务接口、服务实现类和控制器。您可以根据自己的需求进行扩展，例如添加食堂信息修改、删除等功能。
package com.example.canteenapp.model;                        //食堂实体类 

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data                                                        //Lombok 注解，自动生成 getter、setter、toString 等方法。
@Document(collection = "canteens")                           //指定该实体类映射到 MongoDB 中名为 "canteens" 的集合。
public class Canteen {

    @Id//标识该字段为主键。
    private String id;
    private String name;
    private String location;
    private String openTime;
    private String image; // 可选，食堂图片 URL
    private String description;
}


package com.example.canteenapp.service;                    //食堂服务接口:定义了获取所有食堂 (getAllCanteens)、根据 ID 获取食堂 (getCanteenById) 和搜索食堂 (searchCanteens) 的接口方法。

import com.example.canteenapp.model.Canteen;

import java.util.List;

public interface CanteenService {

    List<Canteen> getAllCanteens();

    Canteen getCanteenById(String id);

    List<Canteen> searchCanteens(String keyword);
}


package com.example.canteenapp.service.impl;            //食堂服务实现类

import com.example.canteenapp.exception.ResourceNotFoundException;
import com.example.canteenapp.model.Canteen;
import com.example.canteenapp.repository.CanteenRepository;
import com.example.canteenapp.service.CanteenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.List;

@Service                                              //标识该类为服务类，会被 Spring 容器管理。
public class CanteenServiceImpl implements CanteenService {

    @Autowired                                       //自动注入 CanteenRepository 和 MongoTemplate 实例。
    private CanteenRepository canteenRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public List<Canteen> getAllCanteens() {
        return canteenRepository.findAll();
    }

    @Override
    public Canteen getCanteenById(String id) {
        return canteenRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Canteen not found with id: " + id));
    }

    @Override
    public List<Canteen> searchCanteens(String keyword) {
        Query query = new Query();
        query.addCriteria(Criteria.where("name").regex(keyword, "i")); // 模糊查询，忽略大小写
        return mongoTemplate.find(query, Canteen.class);
    }
}
//getAllCanteens 方法: 调用 CanteenRepository 的 findAll 方法获取所有食堂信息。
//getCanteenById 方法: 根据 ID 查询食堂，如果食堂不存在则抛出异常。
//searchCanteens 方法: 使用 MongoTemplate 进行模糊查询，根据关键字搜索食堂名称。


package com.example.canteenapp.controller;                            //食堂控制器 

import com.example.canteenapp.model.Canteen;
import com.example.canteenapp.service.CanteenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController                                                      //标识该类为 RESTful 风格的控制器。
@RequestMapping("/api/canteens")                                     //设置该控制器的根路径为 /api/canteens。
public class CanteenController {

    @Autowired
    private CanteenService canteenService;

    @GetMapping("")                                                 //处理获取所有食堂请求，请求路径为 /api/canteens，请求方式为 GET。
    public ResponseEntity<List<Canteen>> getAllCanteens() {
        List<Canteen> canteens = canteenService.getAllCanteens();
        return new ResponseEntity<>(canteens, HttpStatus.OK);
    }

    @GetMapping("/{id}")                                            //处理根据 ID 获取食堂请求，请求路径为 /api/canteens/{id}，请求方式为 GET。
    public ResponseEntity<Canteen> getCanteenById(@PathVariable String id) {
        Canteen canteen = canteenService.getCanteenById(id);
        return new ResponseEntity<>(canteen, HttpStatus.OK);
    }

    @GetMapping("/search")                                         //处理搜索食堂请求，请求路径为 /api/canteens/search?keyword={keyword}，请求方式为 GET。
    public ResponseEntity<List<Canteen>> searchCanteens(@RequestParam String keyword) {
        List<Canteen> canteens = canteenService.searchCanteens(keyword);
        return new ResponseEntity<>(canteens, HttpStatus.OK);
    }
}
