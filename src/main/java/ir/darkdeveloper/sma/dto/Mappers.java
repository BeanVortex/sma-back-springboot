package ir.darkdeveloper.sma.dto;

import ir.darkdeveloper.sma.config.StartupConfig;
import ir.darkdeveloper.sma.model.CommentModel;
import ir.darkdeveloper.sma.model.PostModel;
import ir.darkdeveloper.sma.model.UserModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface Mappers {

    @Mappings({
            @Mapping(target = "createdAt", dateFormat = StartupConfig.DATE_FORMAT),
            @Mapping(target = "updatedAt", dateFormat = StartupConfig.DATE_FORMAT),
    })
    UserDto toDto(UserModel userModel);

    @Mappings({
            @Mapping(target = "userId", source = "user.id"),
            @Mapping(target = "createdAt", dateFormat = StartupConfig.DATE_FORMAT),
            @Mapping(target = "updatedAt", dateFormat = StartupConfig.DATE_FORMAT),
    })
    PostDto toDto(PostModel postModel);

    @Mappings({
            @Mapping(target = "userId", source = "user.id"),
            @Mapping(target = "postId", source = "post.id"),
            @Mapping(target = "createdAt", dateFormat = StartupConfig.DATE_FORMAT),
            @Mapping(target = "updatedAt", dateFormat = StartupConfig.DATE_FORMAT),
    })
    CommentDto toDto(CommentModel commentModel);

}
