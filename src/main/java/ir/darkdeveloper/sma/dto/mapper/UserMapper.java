package ir.darkdeveloper.sma.dto.mapper;

import ir.darkdeveloper.sma.config.StartupConfig;
import ir.darkdeveloper.sma.dto.UserDto;
import ir.darkdeveloper.sma.model.UserModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mappings({
            @Mapping(target = "createdAt", dateFormat = StartupConfig.DATE_FORMAT),
            @Mapping(target = "updatedAt", dateFormat = StartupConfig.DATE_FORMAT),
    })
    UserDto toDto(UserModel userModel);

}
