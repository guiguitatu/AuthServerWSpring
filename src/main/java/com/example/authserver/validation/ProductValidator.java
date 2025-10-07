package com.example.authserver.validation;

import com.example.authserver.domain.Product;

public interface ProductValidator {
    boolean isValid(Product product);
}
