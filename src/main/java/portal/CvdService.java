package portal;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static portal.DateTransformUtil.convertToDateViaInstant;
import static portal.DateTransformUtil.convertToLocalDateViaInstant;

public class CvdService {
     private  String server = "https://api.covid19api.com/country/";
    private   String ending = "/status/confirmed";
    private   Map<String, Map<Date, Integer>> countryCasesMap = new HashMap();

    private   SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US);


    public ResultDTO find(String country, Date dateFrom, Date dateTo) throws IOException, ParseException {
        ResultDTO resultDTO = findCached(country, dateFrom, dateTo);
        if (resultDTO == null) {
            Map<Date, Integer> casesMap = collectData(country, dateFrom, dateTo);
            resultDTO = iterateToDate(dateFrom, dateTo, casesMap, country);
        } else {
            System.out.println("Get from Cache");
        }
        return resultDTO;
    }

    public ResultDTO findCached(String country, Date dateFrom, Date dateTo) {
        Map<Date, Integer> casesMap = countryCasesMap.get(country);
        if (casesMap == null) {
            return null;
        }
        return iterateToDate(dateFrom, dateTo, casesMap, country);
    }

    public ResultDTO iterateToDate(Date dateFrom, Date dateTo, Map<Date, Integer> casesMap, String country) {
        Integer min = Integer.MAX_VALUE;
        Integer max = 0;
        LocalDate startDate = convertToLocalDateViaInstant(dateFrom);
        LocalDate endDate = convertToLocalDateViaInstant(dateTo).plusDays(1);
        for (LocalDate date = startDate; date.isBefore(endDate); date = date.plusDays(1)) {
            Date dateKey=convertToDateViaInstant(date);
            Integer cases = casesMap.get(dateKey);
            if (cases == null) {
                return null;
            }
            if (cases > max) {
                max = cases;
            }
            if (cases < min) {
                min = cases;
            }
        }
        ResultDTO resultDTO = new ResultDTO(country, min, max);
        return resultDTO;
    }

    public  Map<Date, Integer> collectData(String country, Date dateFrom, Date dateTo) throws IOException, ParseException {
        System.out.println("Get from Server");
        String url = server + country + ending;
        RestTemplate rest = new RestTemplate();
        HttpEntity<String> requestEntity = new HttpEntity("");
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url)
                .queryParam("from", format.format(dateFrom))
                .queryParam("to", format.format(dateTo));
        ResponseEntity<String> responseEntity = rest.exchange(builder.toUriString(), HttpMethod.GET, requestEntity, String.class);
        ObjectMapper mapper = new ObjectMapper();
        String json = responseEntity.getBody().toLowerCase();
        ConfirmedCasesDTO[] pp1 = mapper.readValue(json, ConfirmedCasesDTO[].class);
        Map<Date, Integer> caseMap = countryCasesMap.getOrDefault(country, new HashMap<>());
        for (ConfirmedCasesDTO dto : pp1) {
            Date date = format.parse(dto.getDate().toUpperCase());
            caseMap.put(date, dto.cases);
        }
        countryCasesMap.put(country, caseMap);
        return caseMap;
    }

}
