package com.kusitms29.backendH.api.sync.service.dto.response;

import com.kusitms29.backendH.domain.category.entity.Type;
import com.kusitms29.backendH.domain.sync.entity.SyncType;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DecimalStyle;
import java.util.Locale;
@Getter
@Builder
public class SyncInfoResponse {
    private Long syncId;
    private String syncType;
    private String type;
    private String image;
    private int userCnt;
    private int totalCnt;
    private String syncName;
    private String location;
    private String date;
    private Boolean isMarked;

    private static final DateTimeFormatter DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("M/d(EEE) a h시")
                    .withLocale(Locale.KOREAN)
                    .withDecimalStyle(DecimalStyle.of(Locale.KOREAN));

    public SyncInfoResponse(){

    }
    public SyncInfoResponse(
            Long syncId,
            String syncType,
            String type,
            String image,
            int userCnt,
            int totalCnt,
            String syncName,
            String location,
            String date,
            Boolean isMarked) {
        this.syncId = syncId;
        this.syncType = syncType;
        this.type = type;
        this.image = image;
        this.userCnt = userCnt;
        this.totalCnt = totalCnt;
        this.syncName = syncName;
        this.location = location;
        this.date = date;
        this.isMarked = isMarked;
    }

    public static SyncInfoResponse of(
            Long syncId,
            SyncType syncType,
            Type type,
            String image,
            int userCnt,
            int totalCnt,
            String syncName,
            String location,
            LocalDateTime date,
            Boolean isMarked) {
        String dateString = date != null ? date.format(DATE_TIME_FORMATTER) : null;
        return new SyncInfoResponse(
                syncId,
                syncType.getStringSyncType(),
                type.getStringType(),
                image,
                userCnt,
                totalCnt,
                syncName,
                location,
                dateString,
                isMarked
        );
    }

}