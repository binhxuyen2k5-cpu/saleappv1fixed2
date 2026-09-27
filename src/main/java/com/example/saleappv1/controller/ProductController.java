package com.example.saleappv1.controller;

import com.example.saleappv1.model.Category;
import com.example.saleappv1.model.Product;
import com.example.saleappv1.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Optional;

@Controller
public class ProductController {

    @Autowired
    private ProductService productService;

    // http://localhost:8080/products
    // http://localhost:8080/products?categoryId=1
    // http://localhost:8080/products?keyword=iphone
    // http://localhost:8080/products?fromPrice=5.000.000&toPrice=20.000.000
    // http://localhost:8080/products?page=2&size=4
    // http://localhost:8080/products?sort=priceAsc (hoac priceDesc, bestseller)
    @GetMapping("/products")
    public String products(
            @RequestParam(required = false) Integer categoryId,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String fromPrice,
            @RequestParam(required = false) String toPrice,
            @RequestParam(required = false) String sort,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "4") int size,
            Model model) {

        Double from = parsePrice(fromPrice);
        Double to = parsePrice(toPrice);

        // Neu nguoi dung nhap nguoc (gia tu > gia den) thi tu dong hoan doi lai cho hop ly
        if (from != null && to != null && from > to) {
            Double tmp = from;
            from = to;
            to = tmp;
        }

        List<Product> filtered = productService.filterProducts(categoryId, keyword, from, to);
        filtered = productService.sortProducts(filtered, sort);

        int safeSize = size < 1 ? 4 : size;
        int totalItems = filtered.size();
        int totalPages = totalItems == 0 ? 1 : (int) Math.ceil((double) totalItems / safeSize);
        int safePage = Math.min(Math.max(page, 1), totalPages);

        List<Product> pageItems = productService.paginate(filtered, safePage, safeSize);

        String selectedCategoryName = categoryId == null ? null
                : productService.getCategoryById(categoryId).map(Category::getName).orElse(null);

        model.addAttribute("products", pageItems);
        model.addAttribute("categories", productService.getAllCategories());
        model.addAttribute("selectedCategoryId", categoryId);
        model.addAttribute("selectedCategoryName", selectedCategoryName);
        model.addAttribute("keyword", keyword);
        // Giu nguyen chuoi nguoi dung da go de hien thi lai dung tren form
        model.addAttribute("fromPrice", fromPrice);
        model.addAttribute("toPrice", toPrice);
        model.addAttribute("sort", sort);
        model.addAttribute("currentPage", safePage);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("totalItems", totalItems);
        model.addAttribute("pageSize", safeSize);

        return "products";
    }

    // Truoc day fromPrice/toPrice duoc khai bao truc tiep la Double nen chi can
    // nguoi dung go dau cham/phay ngan cach (vi du "5.000.000") la Spring nem loi
    // 400 Bad Request va ca trang loc bi vo hieu. Ham nay nhan chuoi tho, loc bo
    // moi ky tu khong phai chu so roi moi chuyen sang Double, nen chap nhan duoc
    // ca "5000000", "5.000.000" lan "5,000,000".
    private Double parsePrice(String raw) {
        if (raw == null || raw.isBlank()) {
            return null;
        }
        String digitsOnly = raw.replaceAll("[^0-9]", "");
        if (digitsOnly.isEmpty()) {
            return null;
        }
        try {
            return Double.parseDouble(digitsOnly);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    // http://localhost:8080/products/{productId}
    @GetMapping("/products/{id}")
    public String productDetail(@PathVariable("id") int id, Model model) {
        Optional<Product> productOpt = productService.getProductById(id);

        if (productOpt.isEmpty()) {
            return "redirect:/products";
        }

        Product product = productOpt.get();
        Optional<Category> categoryOpt = productService.getCategoryById(product.getCategoryId());

        model.addAttribute("product", product);
        model.addAttribute("categoryName", categoryOpt.map(Category::getName).orElse("Khong xac dinh"));
        // De header dung chung (fragment) hien thi menu danh muc va bo sang dung
        model.addAttribute("categories", productService.getAllCategories());
        model.addAttribute("selectedCategoryId", product.getCategoryId());

        return "product-detail";
    }
}
