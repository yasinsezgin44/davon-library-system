package com.davon.library.model;

import lombok.*;
import lombok.experimental.SuperBuilder;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Report extends BaseEntity {
    private String title;
    private LocalDate dateGenerated;
    private LocalDate startDate;
    private LocalDate endDate;
    @lombok.Builder.Default
    private Map<String, String> content = new HashMap<>();
    private String generatedBy;

    public void addContent(String key, String value) {
        content.put(key, value);
    }

    public String getContent(String key) {
        return content.get(key);
    }

    public String generateTextReport() {
        StringBuilder sb = new StringBuilder();
        sb.append(title).append("\n");
        sb.append("Generated: ").append(dateGenerated).append("\n");

        if (startDate != null && endDate != null) {
            sb.append("Period: ").append(startDate).append(" to ").append(endDate).append("\n");
        }

        sb.append("\n");

        for (Map.Entry<String, String> entry : content.entrySet()) {
            sb.append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
        }

        return sb.toString();
    }
}
