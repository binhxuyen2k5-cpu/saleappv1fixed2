package com.example.saleappv1.service;

import com.example.saleappv1.model.Category;
import com.example.saleappv1.model.Product;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    private List<Product> products = new ArrayList<>();
    private List<Category> categories = new ArrayList<>();

    // Doc du lieu tu file JSON khi ung dung khoi dong
    @PostConstruct
    public void loadData() {
        ObjectMapper mapper = new ObjectMapper();
        try (InputStream productStream = new ClassPathResource("data/products.json").getInputStream();
             InputStream categoryStream = new ClassPathResource("data/categories.json").getInputStream()) {

            products = mapper.readValue(productStream, mapper.getTypeFactory()
                    .constructCollectionType(List.class, Product.class));

            categories = mapper.readValue(categoryStream, mapper.getTypeFactory()
                    .constructCollectionType(List.class, Category.class));

        } catch (Exception e) {
            throw new RuntimeException("Khong the doc du lieu san pham/danh muc: " + e.getMessage(), e);
        }
    }

    public List<Product> getAllProducts() {
        return products;
    }

    public List<Category> getAllCategories() {
        return categories;
    }

    public Optional<Product> getProductById(int id) {
        return products.stream().filter(p -> p.getId() == id).findFirst();
    }

    public Optional<Category> getCategoryById(int id) {
        return categories.stream().filter(c -> c.getId() == id).findFirst();
    }

    // Loc va tim kiem san pham theo danh muc, tu khoa va khoang gia
    public List<Product> filterProducts(Integer categoryId, String keyword, Double fromPrice, Double toPrice) {
        String normalizedKeyword = normalize(keyword);
        return products.stream()
                .filter(p -> categoryId == null || p.getCategoryId() == categoryId)
                .filter(p -> normalizedKeyword.isBlank()
                        || normalize(p.getName()).contains(normalizedKeyword))
                .filter(p -> fromPrice == null || p.getPrice() >= fromPrice)
                .filter(p -> toPrice == null || p.getPrice() <= toPrice)
                .toList();
    }

    // Cat mot trang (page) tu danh sach da loc, danh cho phan trang o trang danh sach
    public List<Product> paginate(List<Product> source, int page, int size) {
        int safeSize = size < 1 ? 1 : size;
        int fromIndex = Math.max(0, (page - 1) * safeSize);
        if (fromIndex >= source.size()) {
            return List.of();
        }
        int toIndex = Math.min(source.size(), fromIndex + safeSize);
        return source.subList(fromIndex, toIndex);
    }

    // Danh sach san pham noi bat cho trang chu: uu tien san pham ban chay nhat
    public List<Product> getFeaturedProducts(int limit) {
        return products.stream()
                .sorted(Comparator.comparingInt(Product::getSold).reversed())
                .limit(limit)
                .toList();
    }

    // Sap xep danh sach da loc theo lua chon cua nguoi dung (gia tang/giam, ban chay)
    public List<Product> sortProducts(List<Product> source, String sort) {
        if (sort == null) {
            return source;
        }
        Comparator<Product> comparator = switch (sort) {
            case "priceAsc" -> Comparator.comparingDouble(Product::getPrice);
            case "priceDesc" -> Comparator.comparingDouble(Product::getPrice).reversed();
            case "bestseller" -> Comparator.comparingInt(Product::getSold).reversed();
            default -> null;
        };
        if (comparator == null) {
            return source;
        }
        return source.stream().sorted(comparator).toList();
    }

    // Chuan hoa chuoi de tim kiem de dai hon: chuyen ve chu thuong va coi dau
    // gach ngang/gach duoi nhu khoang trang, de go "xiaomi-pad-6" van khop voi
    // ten san pham "Xiaomi Pad 6".
    private String normalize(String text) {
        if (text == null) {
            return "";
        }
        return text.toLowerCase().replaceAll("[-_]+", " ").trim();
    }
}
