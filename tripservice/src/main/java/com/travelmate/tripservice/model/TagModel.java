package com.travelmate.tripservice.model;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TagModel {
    private Long id;
    private String name;
    private Long usageCount;
}

