package com.example.canteenapp.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.util.List;

@Data
@Document(collection = "dishes")
public class Dish {

    @Id
    private String id;
    private String name;
    private String image; // 可选，菜品图片 URL
    private BigDecimal price;
    @DBRef
    private Canteen canteen; // 所属食堂
    private List<String> tags; // 菜品标签，例如：辣、甜、素食
}
