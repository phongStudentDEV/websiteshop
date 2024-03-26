package com.project.shopapp.controllers;

import com.project.shopapp.components.LocalizationUtils;
import com.project.shopapp.dtos.ProductDTO;
import com.project.shopapp.dtos.ProductImageDTO;
import com.project.shopapp.models.Product;
import com.project.shopapp.models.ProductImage;
import com.project.shopapp.responses.ProductListResponse;
import com.project.shopapp.responses.ProductResponse;
import com.project.shopapp.services.impl.ProductService;
import com.project.shopapp.utils.MessageKeys;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("${api.prefix}/products")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;
    private final LocalizationUtils localizationUtils;


    @PostMapping(value = "")
    public ResponseEntity<?> createProduct(@Valid @RequestBody ProductDTO productDTO,
                                           BindingResult result) {
        try {
            if (result.hasErrors()) {
                List<String> errorMessages =
                        result.getFieldErrors().stream().map(FieldError::getDefaultMessage).toList();
                return ResponseEntity.badRequest().body(errorMessages);
            }
            Product newProduct = productService.createProduct(productDTO);
            return ResponseEntity.ok(newProduct);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("")
    public ResponseEntity<?> getProducts(@RequestParam("page") Integer page,
                                         @RequestParam("limit") Integer limit) {
        PageRequest pageRequest = PageRequest.of(page, limit, Sort.by("id").descending());
        Page<ProductResponse> productPage = productService.pageProduct(pageRequest);

        //lấy ra tổng số trang
        Integer size = productPage.getTotalPages();
        // danh sach response trong 1 trang
        List<ProductResponse> productResponses = productPage.getContent();

        ProductListResponse productListResponse = ProductListResponse.builder()
                .productResponses(productResponses)
                .totalPages(size)
                .build();
        return ResponseEntity.ok(productListResponse);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getProductById(@PathVariable("id") Long id) {
        try {
            Product product = productService.getByIdProduct(id);
            return ResponseEntity.ok(ProductResponse.fromProduct(product));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    @PutMapping("/{id}")
    public ResponseEntity<?> updateProduct(@PathVariable("id") Long id, @RequestBody ProductDTO productDTO) {
        try {
            Product productUpdate = productService.updateProduct(id, productDTO);
            return ResponseEntity.ok(productUpdate);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
//        return ResponseEntity.ok("update product succssfully with id = " + id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable("id") Long id) {
        try {
            productService.deleteProduct(id);
            return ResponseEntity.ok("Delete product succssfully with id = " + id);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // upload anh
    @PostMapping(value = "uploads/{id}",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadImages(
            @PathVariable("id") Long productId,
            @ModelAttribute("files") List<MultipartFile> files) {
        try {
            Product existingProduct = productService.getByIdProduct(productId);
            files = files == null ? new ArrayList<MultipartFile>() : files;
            if (files.size() > ProductImage.MAXIMUM_IMAGES_PER_PRODUCT) {
                return ResponseEntity.badRequest().body(localizationUtils
                        .getLocalizedMessage(MessageKeys.UPLOAD_IMAGES_MAX_5));
            }
            List<ProductImage> productImages = new ArrayList<>();
            for (MultipartFile file : files) {
                if (file.getSize() == 0) {
                    continue;
                }
                //kiem tra dinh dang file
                if (file.getSize() > 10 * 1024 * 1024) { // kich thuoc > 10MB
                    return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE)
                            .body(localizationUtils
                                    .getLocalizedMessage(MessageKeys.UPLOAD_IMAGES_FILE_LARGE));
                }
                String contenType = file.getContentType();
                if (contenType == null || !contenType.startsWith("image/")) {
                    return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
                            .body(localizationUtils.getLocalizedMessage(MessageKeys.UPLOAD_IMAGES_FILE_MUST_BE_IMAGE));
                }
                // Lưu file và cập nhật thumbnail trong DTO
                String filename = storeFile(file); // Thay thế hàm này với code của bạn để lưu file
                //lưu vào đối tượng product trong DB
                ProductImage productImage = productService.createProductImage(
                        existingProduct.getId(),
                        ProductImageDTO.builder()
                                .imageUrl(filename)
                                .build()
                );
                productImages.add(productImage);
            }
            return ResponseEntity.ok().body(productImages);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    //function save image
    private String storeFile(MultipartFile file) throws IOException {
        // lay ra ten image
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());
        //tao ten moi cho image truoc khi luu vao db
        String uniqueFileName = UUID.randomUUID().toString() + "_" + fileName;

        //duong dan thu muc luu file
        java.nio.file.Path uploadDir = Paths.get("uploads");
        // kiem tra thu muc uploadFile nay da ton taij hay chua
        if (!Files.exists(uploadDir)) {
            Files.createDirectories(uploadDir);
        }
        // duong dan day du den file
        java.nio.file.Path destination = Paths.get(uploadDir.toString(), uniqueFileName);
        // sao chep file vao thu muc dich
        Files.copy(file.getInputStream(), destination, StandardCopyOption.REPLACE_EXISTING);
        return uniqueFileName;
    }

    //fake data product
//    @PostMapping("/genarate")
//    private ResponseEntity<?> fakeData(){
//        Faker faker = new Faker();
//        for (int i = 0; i < 100; i++) {
//            String nameProduct = faker.commerce().productName();
//            if (productService.exitstsByName(nameProduct)){
//                continue;
//            }
//            ProductDTO productDTO = ProductDTO.builder()
//                    .name(nameProduct)
//                    .price((float)faker.number().numberBetween(100, 40000000))
//                    .description(faker.lorem().sentence())
//                    .categoryId((long) faker.number().numberBetween(1, 4))
//                    .build();
//            try {
//                productService.createProduct(productDTO);
//            }catch (Exception e){
//                return ResponseEntity.badRequest().body(e.getMessage());
//            }
//        }
//        return ResponseEntity.ok("Successfully");
//    }

}
