package main.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CountryRequest {
    @NotBlank(message = "Country name must not be blank")
    private String name;
}
