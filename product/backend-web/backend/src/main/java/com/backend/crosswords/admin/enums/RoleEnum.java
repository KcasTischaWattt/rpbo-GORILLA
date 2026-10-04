package com.backend.crosswords.admin.enums;

import com.backend.crosswords.admin.dto.RegisterUserDTO;
import org.springframework.beans.factory.annotation.Value;

public enum RoleEnum {
    ROLE_ADMIN(AuthorityEnum.values(), new RegisterUserDTO[]{new RegisterUserDTO("Андрей", "Цейтин", null, "avtseytin@edu.hse.ru"), new RegisterUserDTO("Максим", "Пересторонин", "", "mdperestoronin@edu.hse.ru"), new RegisterUserDTO("Матвей", "Татауров", "", "mntataurov@edu.hse.ru")}),
    ROLE_MODERATOR(new AuthorityEnum[]{AuthorityEnum.EDIT_DELETE_DOCS}, new RegisterUserDTO[]{new RegisterUserDTO(null, null, null, "geka2003@mail.ru"), new RegisterUserDTO(null, null, null, "chook@mail.ru")});

    private final AuthorityEnum[] authorities;
    private final RegisterUserDTO[] usersWhiteList;

    RoleEnum(AuthorityEnum[] authorities, RegisterUserDTO[] usersWhiteList) {
        this.authorities = authorities;
        this.usersWhiteList = usersWhiteList;
    }

    public AuthorityEnum[] getAuthorities() {
        return authorities;
    }

    public RegisterUserDTO[] getUsersWhiteList() {
        return usersWhiteList;
    }
}
