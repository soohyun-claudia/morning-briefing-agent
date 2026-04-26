package com.briefing.agent.dto;

import java.time.ZonedDateTime;

public record CalendarEvent(String title, ZonedDateTime startTime) {
}
