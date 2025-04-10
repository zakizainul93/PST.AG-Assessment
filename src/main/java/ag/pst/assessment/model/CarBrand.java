package ag.pst.assessment.model;

import lombok.Data;

import java.time.LocalDate;

@Data
public class CarBrand {
    private String brand;
    private LocalDate releaseDate;
}