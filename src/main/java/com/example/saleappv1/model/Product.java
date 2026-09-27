package com.example.saleappv1.model;

import java.util.LinkedHashMap;
import java.util.Map;

public class Product {

    private int id;
    private String name;
    private String description;
    private String brand;
    private double price;
    private Double originalPrice; // Gia goc truoc khuyen mai, null neu san pham khong giam gia
    private String image;
    private int categoryId;
    private int stock;   // So luong ton kho
    private int sold;    // So luong da ban
    private double rating; // Diem danh gia trung binh (0 - 5)
    private Map<String, String> specs = new LinkedHashMap<>(); // Thong so ky thuat

    public Product() {
    }

    public Product(int id, String name, String description, String brand, double price, Double originalPrice,
                    String image, int categoryId, int stock, int sold, double rating, Map<String, String> specs) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.brand = brand;
        this.price = price;
        this.originalPrice = originalPrice;
        this.image = image;
        this.categoryId = categoryId;
        this.stock = stock;
        this.sold = sold;
        this.rating = rating;
        this.specs = specs;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public Double getOriginalPrice() {
        return originalPrice;
    }

    public void setOriginalPrice(Double originalPrice) {
        this.originalPrice = originalPrice;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public int getSold() {
        return sold;
    }

    public void setSold(int sold) {
        this.sold = sold;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public Map<String, String> getSpecs() {
        return specs;
    }

    public void setSpecs(Map<String, String> specs) {
        this.specs = specs;
    }

    // % giam gia so voi gia goc, tra ve 0 neu san pham khong khuyen mai
    public int getDiscountPercent() {
        if (originalPrice == null || originalPrice <= price) {
            return 0;
        }
        return (int) Math.round((originalPrice - price) / originalPrice * 100);
    }

    public boolean isOnSale() {
        return getDiscountPercent() > 0;
    }

    public boolean isOutOfStock() {
        return stock <= 0;
    }
}
