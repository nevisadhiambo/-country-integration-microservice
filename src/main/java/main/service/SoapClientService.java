package main.service;

import com.example.soap.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.ws.client.core.WebServiceTemplate;

@Slf4j
@Service
@RequiredArgsConstructor
public class SoapClientService {

    private final WebServiceTemplate webServiceTemplate;

    public String getCountryIsoCode(String countryName) {
        log.info("Fetching ISO code for country: {}", countryName);
        CountryISOCode request = new CountryISOCode();
        request.setSCountryName(countryName);

        CountryISOCodeResponse response = (CountryISOCodeResponse)
                webServiceTemplate.marshalSendAndReceive(request);

        String isoCode = response.getCountryISOCodeResult();
        log.info("ISO code received: {}", isoCode);
        return isoCode;
    }
    public TCountryInfo getFullCountryInfo(String isoCode) {
        log.info("Fetching full country info for ISO code: {}", isoCode);
        FullCountryInfo request = new FullCountryInfo();
        request.setSCountryISOCode(isoCode);

        FullCountryInfoResponse response = (FullCountryInfoResponse)
                webServiceTemplate.marshalSendAndReceive(request);

        TCountryInfo info = response.getFullCountryInfoResult();
        log.info("Full country info fetched: {}", info.getSName());
        return info;
    }
}