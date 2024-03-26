package com.project.shopapp.services;

import com.project.shopapp.dtos.ProductDTO;
import com.project.shopapp.dtos.ProductImageDTO;
import com.project.shopapp.models.Product;
import com.project.shopapp.models.ProductImage;
import com.project.shopapp.responses.ProductResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

public interface IProductService {
    Product createProduct(ProductDTO productDTO) throws Exception;

    Product getByIdProduct(Long idProduct) throws Exception;

    Page<ProductResponse> pageProduct(PageRequest pageRequest);

    Product updateProduct(Long id, ProductDTO productDTO)  throws Exception;

    void deleteProduct(Long id);

    Boolean exitstsByName(String nameProduct);

    ProductImage createProductImage(
            Long productId,
         ProductImageDTO productImageDTO) throws Exception;
}
