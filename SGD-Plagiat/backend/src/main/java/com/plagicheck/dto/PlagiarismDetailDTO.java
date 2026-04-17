package com.plagicheck.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlagiarismDetailDTO {
    private Long rapportId;
    private Double localScore;
    private Double webScore;
    private Double finalScore;
    private Boolean webSearchPerformed;
    private String analysis;

    public String getAnalysis() {
        StringBuilder sb = new StringBuilder();
        sb.append("Analyse locale: ").append(String.format("%.2f", localScore)).append("%\n");
        sb.append("Analyse web: ").append(String.format("%.2f", webScore)).append("%\n");
        sb.append("Score final: ").append(String.format("%.2f", finalScore)).append("%");
        
        return sb.toString();
    }
}
