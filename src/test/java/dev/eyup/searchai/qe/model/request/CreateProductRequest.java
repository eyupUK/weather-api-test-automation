package dev.eyup.searchai.qe.model.request;

import java.math.BigDecimal;


public class CreateProductRequest {
    private String title;
    private String description;
    private String category;
    private String image;
    private BigDecimal price;

    public CreateProductRequest() {
    }

    public CreateProductRequest(String title, String description, String category, String image, BigDecimal price) {
        this.title = title;
        this.description = description;
        this.category = category;
        this.image = image;
        this.price = price;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getCategory() {
        return category;
    }

    public String getImage() {
        return image;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public String toString() {
        return         """
        {
          "title": "%s",
          "description": "%s",
          "category": "%s",
          "image": "%s",
          "price": %s
        }
        """.formatted(title, description, category, image, price);
    }
}
