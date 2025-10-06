package com.example.authserver;

import java.math.BigDecimal;

public class DefaultProductValidator implements ProductValidator {
    @Override
    public boolean isValid(Product product) {
        if (product.getName() == null || product.getName().isBlank()) {
            return false;
        }
        BigDecimal price = product.getPrice();
        if (price == null) {
            return false;
        }
        return price.compareTo(BigDecimal.ZERO) > 0;
    }
}
