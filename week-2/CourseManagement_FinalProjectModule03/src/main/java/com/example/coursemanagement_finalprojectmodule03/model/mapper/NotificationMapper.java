package com.example.coursemanagement_finalprojectmodule03.model.mapper;

import com.example.coursemanagement_finalprojectmodule03.model.dto.res.NotificationResponse;
import com.example.coursemanagement_finalprojectmodule03.model.entity.Notification;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface NotificationMapper {

    @Mapping(target = "notificationId", source = "id")
    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "isRead", source = "read")
    NotificationResponse toResponse(Notification notification);
}