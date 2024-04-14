package com.project.shopapp.services.impl;

import com.project.shopapp.components.LocalizationUtils;
import com.project.shopapp.dtos.ProductDTO;
import com.project.shopapp.dtos.ProductImageDTO;
import com.project.shopapp.exceptions.DataNotFoundException;
import com.project.shopapp.models.Category;
import com.project.shopapp.models.Product;
import com.project.shopapp.models.ProductImage;
import com.project.shopapp.repositories.CategoryRepository;
import com.project.shopapp.repositories.ProductImageRepository;
import com.project.shopapp.repositories.ProductRepository;
import com.project.shopapp.responses.ProductResponse;
import com.project.shopapp.services.IProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService implements IProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductImageRepository productImageRepository;
    private final LocalizationUtils localizationUtils;

    @Override
    @Transactional
    public Product createProduct(ProductDTO productDTO) throws Exception {
        Category existingCategory = categoryRepository.findById(
                productDTO.getCategoryId())
                .orElseThrow(() -> new DataNotFoundException("Category not found"));
        Product newProduct = Product.builder()
                .name(productDTO.getName())
                .price(productDTO.getPrice())
                .thumbnail(productDTO.getThumbnail())
                .description(productDTO.getDescription())
                .category(existingCategory)
                .build();
        return productRepository.save(newProduct);
    }

    @Override
    public Product getByIdProduct(Long idProduct) throws Exception {
        return productRepository.findById(idProduct)
                .orElseThrow(() -> new DataNotFoundException(
                        "Cannot find product with id = " + idProduct));
    }

    @Override
    public Page<ProductResponse> getAllProducts(String keyword,
                                                Long categoryId, PageRequest pageRequest) {
        // Lấy danh sách sản phẩm theo trang (page), giới hạn (limit), và categoryId (nếu có)
        Page<Product> productsPage;
        productsPage = productRepository.searchProducts(categoryId, keyword, pageRequest);
        return productsPage.map(ProductResponse::fromProduct);
    }

    @Override
    @Transactional
    public Product updateProduct(Long id, ProductDTO productDTO) throws Exception {
        Product existingProduct = getByIdProduct(id);
        if (existingProduct != null) {
            Category existingCategory = categoryRepository.findById(productDTO.getCategoryId())
                    .orElseThrow(() -> new DataNotFoundException(
                            "Cannot find category with id: " + productDTO.getCategoryId()));
            existingProduct.setName(productDTO.getName());
            existingProduct.setCategory(existingCategory);
            existingProduct.setPrice(productDTO.getPrice());
            existingProduct.setDescription(productDTO.getDescription());
            existingProduct.setThumbnail(productDTO.getThumbnail());

            return productRepository.save(existingProduct);
        }
        return null;
    }

    @Override
    public void deleteProduct(Long id) {
        Boolean checkIdProduct = productRepository.existsById(id);
        if (checkIdProduct) {
            productRepository.deleteById(id);
        }
    }

    @Override
    @Transactional
    public Boolean exitstsByName(String nameProduct) {
        return productRepository.existsByName(nameProduct);
    }

    @Override
    @Transactional
    public ProductImage createProductImage(Long productId, ProductImageDTO productImageDTO) throws Exception {
        Product existingProduct = getByIdProduct(productId);
        ProductImage productImage = ProductImage.builder()
                .product(existingProduct)
                .imageUrl(productImageDTO.getImageUrl())
                .build();
        Integer size = productImageRepository.findByProductId(productId).size();
        if (size > ProductImage.MAXIMUM_IMAGES_PER_PRODUCT) {
            throw new DataNotFoundException("Number of images must be <= " + ProductImage.MAXIMUM_IMAGES_PER_PRODUCT);
        }

        return productImageRepository.save(productImage);
    }
    @Override
    public List<Product> findProductsByIds(List<Long> productIds) {
        return productRepository.findProductsByIds(productIds);
    }
}
