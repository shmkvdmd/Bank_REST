package com.example.bankcards.util.mapper;

import com.example.bankcards.dto.response.UserResponseDto;
import com.example.bankcards.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {
    UserResponseDto toDto(User user);
}