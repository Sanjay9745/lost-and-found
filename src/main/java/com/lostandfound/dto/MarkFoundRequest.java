package com.lostandfound.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MarkFoundRequest {
    private String foundBy;
    private String remarks;
}
