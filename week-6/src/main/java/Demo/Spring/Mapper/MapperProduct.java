package Demo.Spring.Mapper;

import Demo.Spring.DTO.Request.RequestProduct;
import Demo.Spring.DTO.Response.ResponseProduct;
import Demo.Spring.Entity.Product;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface MapperProduct {
    @Mapping(source = "seller.username", target = "sellerUsername")
    ResponseProduct toResponseProduct(Product product);
//    @AfterMapping
//    public void setSellerUserName(Product product, @MappingTarget ResponseProduct responseProduct){
//        responseProduct.setSellerUsername(product.getSeller().getUsername());
//    }
    Product toProduct(RequestProduct request);
}
