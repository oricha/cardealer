package com.cardealer.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BreadcrumbItem {
    private String label;
    private String url;
    private boolean current;
}
