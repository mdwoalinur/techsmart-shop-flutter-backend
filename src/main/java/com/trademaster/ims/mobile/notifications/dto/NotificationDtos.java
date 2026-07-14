package com.trademaster.ims.mobile.notifications.dto;

import java.time.Instant;
import java.util.List;

public final class NotificationDtos { private NotificationDtos(){}
 public record NotificationPage(List<NotificationSummary> content,int page,int size,long totalElements,int totalPages,boolean first,boolean last){}
 public record NotificationSummary(String notificationNumber,String type,String category,String title,String shortMessage,String severity,boolean read,Instant createdAt,String relatedEntityType,String relatedEntityReference,String actionType){}
 public record NotificationDetail(String notificationNumber,String type,String category,String title,String message,String severity,boolean read,Instant createdAt,Instant readAt,String relatedEntityType,String relatedEntityReference,String actionType,String actionReference){}
 public record UnreadCountResponse(long unreadCount){}
 public record NotificationPreferenceResponse(String category,boolean inAppEnabled,boolean emailEnabled,boolean critical){}
 public record PreferenceUpdateRequest(List<PreferenceUpdateItem> preferences){}
 public record PreferenceUpdateItem(String category,Boolean inAppEnabled,Boolean emailEnabled){}
}