package com.project.shopapp.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Data
public class OrderDetailDTO {
    @JsonProperty("order_id")
    @Min(value = 1, message = "Order's id must be >0")
    private Long orderId;

    @JsonProperty("product_id")
    @Min(value = 1, message = "Product's id must be >0")
    private Long productId;

    @Min(value = 0, message = "Price must be >=0")
    private Float price;

    @JsonProperty("number_of_products")
    @Min(value = 1, message = "Price must be >=1")
    private Integer numberOfProducts;

    @JsonProperty("total_money")
    @Min(value = 0, message = "Total money must be >= 0")
    private Float totalMoney;

    private String color;


}
