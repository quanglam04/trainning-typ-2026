package Demo.Spring.Mapper;

import Demo.Spring.DTO.Response.ResponseUser;
import Demo.Spring.Entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface MapperUser {
    ResponseUser toResponseUser(User user);
}
