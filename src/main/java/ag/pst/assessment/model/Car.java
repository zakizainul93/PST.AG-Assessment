package ag.pst.assessment.model;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

@Data
public class Car {
    private String brand;
    private LocalDate releaseDate;
    private String type;
    private String model;
    private Map<String, BigDecimal> prices;
}