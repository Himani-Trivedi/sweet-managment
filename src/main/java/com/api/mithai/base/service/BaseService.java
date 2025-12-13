package com.api.mithai.base.service;

import com.api.mithai.base.constants.Urls;
import org.springframework.stereotype.Component;

@Component
public class BaseService {
    public String[] publicEndpoints = new String[]{
            Urls.BASE_URL + Urls.AUTH_URL + Urls.LOGIN_URL
    };
}
