package com.nailshop.nailborhood.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Role {
    ROLE_USER,
    ROLE_OWNER,
    ROLE_ADMIN
}
