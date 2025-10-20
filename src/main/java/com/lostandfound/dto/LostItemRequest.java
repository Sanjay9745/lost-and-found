package com.lostandfound.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LostItemRequest {
    private String itemName;
    private String description;
    private String location;
    private String studentName;
    private String contactInfo;
}
