package main.controller;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import main.dto.CountryInfoDto;
import main.dto.CountryRequest;
import main.service.CountryService;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/countries")
@RequiredArgsConstructor
public class CountryController {

    private final CountryService countryService;
    //save
    @PostMapping
    public ResponseEntity<CountryInfoDto> createCountry(@Valid @RequestBody CountryRequest request) {
        CountryInfoDto dto = countryService.fetchAndSaveCountry(request.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }

    //fetch all
    @GetMapping
    public ResponseEntity<List<CountryInfoDto>> getAllCountries() {
        return ResponseEntity.ok(countryService.getAllCountries());
    }

    //fetch by id
    @GetMapping("/{id}")
    public ResponseEntity<CountryInfoDto> getCountryById(@PathVariable Long id) {
        return ResponseEntity.ok(countryService.getCountryById(id));
    }

    //update
    @PutMapping("/{id}")
    public ResponseEntity<CountryInfoDto> updateCountry(
            @PathVariable Long id,
            @Valid @RequestBody CountryInfoDto dto) {
        return ResponseEntity.ok(countryService.updateCountry(id, dto));
    }

    //delete
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCountry(@PathVariable Long id) {
        countryService.deleteCountry(id);
        return ResponseEntity.noContent().build();
    }
}
