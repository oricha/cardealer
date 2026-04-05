package com.cardealer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class CarDealerApplication {

    public static void main(String[] args) {
        SpringApplication.run(CarDealerApplication.class, args);
    }
}
