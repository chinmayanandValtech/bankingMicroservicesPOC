package com.bank.financialintelligence.service;

import com.bank.financialintelligence.dto.SpendingCategory;
import org.springframework.stereotype.Service;

@Service
public class SpendingCategoryService {

    public SpendingCategory categorize(String remarks) {

        if (remarks == null || remarks.isBlank()) {
            return SpendingCategory.OTHER;
        }

        String text = remarks.toLowerCase();

        if (text.contains("grocery")
                || text.contains("groceries")
                || text.contains("restaurant")
                || text.contains("food")
                || text.contains("swiggy")
                || text.contains("zomato")) {
            return SpendingCategory.FOOD;
        }

        if (text.contains("rent")
                || text.contains("house")
                || text.contains("housing")) {
            return SpendingCategory.HOUSING;
        }

        if (text.contains("shopping")
                || text.contains("amazon")
                || text.contains("flipkart")) {
            return SpendingCategory.SHOPPING;
        }

        if (text.contains("uber")
                || text.contains("ola")
                || text.contains("transport")
                || text.contains("fuel")
                || text.contains("petrol")) {
            return SpendingCategory.TRANSPORTATION;
        }

        if (text.contains("electricity")
                || text.contains("water")
                || text.contains("internet")
                || text.contains("utility")) {
            return SpendingCategory.UTILITIES;
        }

        if (text.contains("bill")
                || text.contains("emi")
                || text.contains("payment")) {
            return SpendingCategory.BILLS;
        }

        if (text.contains("atm")
                || text.contains("cash")
                || text.contains("withdraw")) {
            return SpendingCategory.CASH;
        }

        return SpendingCategory.OTHER;
    }
}