package com.company.internalmgmt.common.util;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;

/**
 * Utility class for date and time operations
 */
public class DateTimeUtil {

    private static final ZoneOffset DEFAULT_ZONE_OFFSET = ZoneOffset.UTC;

    /**
     * Convert LocalDateTime to OffsetDateTime using system default zone
     * @param localDateTime the LocalDateTime to convert
     * @return the converted OffsetDateTime
     */
    public static OffsetDateTime toOffsetDateTime(LocalDateTime localDateTime) {
        if (localDateTime == null) {
            return null;
        }
        return localDateTime.atOffset(DEFAULT_ZONE_OFFSET);
    }

    /**
     * Convert OffsetDateTime to LocalDateTime
     * @param offsetDateTime the OffsetDateTime to convert
     * @return the converted LocalDateTime
     */
    public static LocalDateTime toLocalDateTime(OffsetDateTime offsetDateTime) {
        if (offsetDateTime == null) {
            return null;
        }
        return offsetDateTime.toLocalDateTime();
    }
    
    /**
     * Convert LocalDateTime to OffsetDateTime using a specific ZoneId
     * @param localDateTime the LocalDateTime to convert
     * @param zoneId the ZoneId to use for the conversion
     * @return the converted OffsetDateTime
     */
    public static OffsetDateTime toOffsetDateTime(LocalDateTime localDateTime, ZoneId zoneId) {
        if (localDateTime == null) {
            return null;
        }
        return localDateTime.atZone(zoneId).toOffsetDateTime();
    }
    
    /**
     * Get current LocalDateTime
     * @return current LocalDateTime
     */
    public static LocalDateTime now() {
        return LocalDateTime.now();
    }
} 