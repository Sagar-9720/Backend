package com.travelmate.tripservice.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class TripDTO {
    private String id;
    private String title;
    private String description;
    private String userId;
    private String destination;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private boolean isPublic;
    private List<String> images;
    private int likesCount;
    private int commentsCount;
    private boolean isLikedByCurrentUser;
    private boolean isSavedByCurrentUser;
}
