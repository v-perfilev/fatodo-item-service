package com.persoff68.fatodo.service;

import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collections;
import java.util.List;

public class ItemServiceTest {

    @Test
    void testIsAdmin() {
        String jwt = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiIxIiwidXNlcm5hbWUiOiJ0ZXN0X3VzZXIiLCJhdXRob3JpdGllcyI6IlJPTEVfVEVTVCIsImlhdCI6MTU4MzI2ODYxMSwiZXhwIjoyNTgzMjY4NjcxfQ._gQPODOrXvpmc3WfHAli3kgbcm7mwu7SmmJAVqin8CK41v475Teeh4gUgsH-lTJqLNQCacBHmpBxPvloHEWFCw";
        List<? extends GrantedAuthority> authorityList =
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_TEST"));
        User user = new User("test_user", "", authorityList);
        Authentication authentication = new UsernamePasswordAuthenticationToken(user, jwt, authorityList);

    }
}
