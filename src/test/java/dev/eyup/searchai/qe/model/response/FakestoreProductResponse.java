package dev.eyup.searchai.qe.model.response;

import java.math.BigDecimal;

public class FakestoreProductResponse {
    private Integer id;
    private String title;
    private String description;
    private String category;
    private String image;
    private BigDecimal price;

    public Integer getId() {
        return id;
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
}
