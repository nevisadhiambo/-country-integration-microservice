package main.service;
import com.example.soap.TCountryInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import main.dto.CountryInfoDto;
import main.entity.CountryInfo;
import main.entity.Language;
import main.exception.CountryNotFoundException;
import main.repository.CountryInfoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CountryService {

    private final SoapClientService soapClientService;
    private final CountryInfoRepository countryInfoRepository;

    @Transactional
    public CountryInfoDto fetchAndSaveCountry(String rawName) {
        //Convert to sentence case
        String countryName = toSentenceCase(rawName);
        log.info("Processing country: {}", countryName);

        //Get ISO code from SOAP
        String isoCode = soapClientService.getCountryIsoCode(countryName);

        // Return existing record if already stored
        if (countryInfoRepository.existsByIsoCode(isoCode)) {
            log.info("Country {} already exists in DB, returning cached record", isoCode);
            return toDto(countryInfoRepository.findByIsoCode(isoCode).get());
        }

        //Get full country info from SOAP
        TCountryInfo soapInfo = soapClientService.getFullCountryInfo(isoCode);

        //Persist
        CountryInfo entity = CountryInfo.builder()
                .name(soapInfo.getSName())
                .isoCode(soapInfo.getSISOCode())
                .capitalCity(soapInfo.getSCapitalCity())
                .phoneCode(soapInfo.getSPhoneCode())
                .continentCode(soapInfo.getSContinentCode())
                .currencyIsoCode(soapInfo.getSCurrencyISOCode())
                .flagUrl(soapInfo.getSCountryFlag())
                .build();

        List<Language> languages = soapInfo.getLanguages().getTLanguage().stream()
                .map(l -> Language.builder()
                        .isoCode(l.getSISOCode())
                        .name(l.getSName())
                        .countryInfo(entity)
                        .build())
                .collect(Collectors.toList());

        entity.setLanguages(languages);
        CountryInfo saved = countryInfoRepository.save(entity);
        log.info("Saved country {} with id {}", saved.getName(), saved.getId());
        return toDto(saved);
    }

    //Fetch all
    public List<CountryInfoDto> getAllCountries() {
        return countryInfoRepository.findAll().stream()
                .map(this::toDto).collect(Collectors.toList());
    }

    //Fetch by ID
    public CountryInfoDto getCountryById(Long id) {
        return countryInfoRepository.findById(id)
                .map(this::toDto)
                .orElseThrow(() -> new CountryNotFoundException("Country not found with id: " + id));
    }

    //Update
    @Transactional
    public CountryInfoDto updateCountry(Long id, CountryInfoDto dto) {
        CountryInfo existing = countryInfoRepository.findById(id)
                .orElseThrow(() -> new CountryNotFoundException("Country not found with id: " + id));
        existing.setName(dto.getName());
        existing.setCapitalCity(dto.getCapitalCity());
        existing.setPhoneCode(dto.getPhoneCode());
        log.info("Updated country id {}", id);
        return toDto(countryInfoRepository.save(existing));
    }

    //Delete
    @Transactional
    public void deleteCountry(Long id) {
        if (!countryInfoRepository.existsById(id)) {
            throw new CountryNotFoundException("Country not found with id: " + id);
        }
        countryInfoRepository.deleteById(id);
        log.info("Deleted country id {}", id);
    }

    //sentence case
    private String toSentenceCase(String input) {
        if (input == null || input.isBlank()) return input;
        return input.substring(0, 1).toUpperCase() + input.substring(1).toLowerCase();
    }

    // Mapper
    private CountryInfoDto toDto(CountryInfo c) {
        List<String> langs = c.getLanguages() == null ? List.of() :
                c.getLanguages().stream().map(Language::getName).collect(Collectors.toList());
        return CountryInfoDto.builder()
                .id(c.getId())
                .name(c.getName())
                .isoCode(c.getIsoCode())
                .capitalCity(c.getCapitalCity())
                .phoneCode(c.getPhoneCode())
                .continentCode(c.getContinentCode())
                .currencyIsoCode(c.getCurrencyIsoCode())
                .flagUrl(c.getFlagUrl())
                .languages(langs)
                .build();
    }
}