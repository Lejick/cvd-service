package portal;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;

import static portal.DateTransformUtil.convertToDateViaInstant;
import static portal.DateTransformUtil.convertToLocalDateViaInstant;

@Service
public class CvdService {
    private String server = "https://api.covid19api.com/country/";
    private String ending = "/status/confirmed";
    private Map<String, Map<Date, Integer>> countryCasesMap = new HashMap();
    private static final Logger LOGGER = LogManager.getLogger(CvdService.class);
    private SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US);


    public List<ResultDTO> findAll(String[] countries, Date dateFrom, Date dateTo) {

        List<ResultDTO> resultDTOList = new ArrayList<>();
        for (String country : countries) {
            try {
                resultDTOList.add(find(country, dateFrom, dateTo));
            } catch (IOException | ParseException e) {
                LOGGER.error(e.getMessage(), e);
            }
        }
        return resultDTOList;
    }

    private ResultDTO find(String country, Date dateFrom, Date dateTo) throws IOException, ParseException {
        ResultDTO resultDTO = findCached(country, dateFrom, dateTo);
        if (resultDTO == null) {
            Map<Date, Integer> casesMap = collectData(country, dateFrom, dateTo);
            resultDTO = iterateToDate(dateFrom, dateTo, casesMap, country);
            resultDTO.setQueryType("Server");
        } else {
            resultDTO.setQueryType("Cache");
        }
        return resultDTO;
    }

    private ResultDTO findCached(String country, Date dateFrom, Date dateTo) {
        Map<Date, Integer> casesMap = countryCasesMap.get(country);
        if (casesMap == null) {
            return null;
        }
        return iterateToDate(dateFrom, dateTo, casesMap, country);
    }

    private ResultDTO iterateToDate(Date dateFrom, Date dateTo, Map<Date, Integer> casesMap, String country) {
        Integer min = Integer.MAX_VALUE;
        Integer max = 0;
        LocalDate startDate = convertToLocalDateViaInstant(dateFrom);
        LocalDate endDate = convertToLocalDateViaInstant(dateTo).plusDays(1);
        for (LocalDate date = startDate; date.isBefore(endDate); date = date.plusDays(1)) {
            Date dateKey = convertToDateViaInstant(date);
            Date preDateKey = convertToDateViaInstant(date.minusDays(1));
            Integer cases = casesMap.get(dateKey);
            Integer casesPre = casesMap.get(preDateKey);
            if (cases == null || casesPre == null) {
                return null;
            }
            Integer diff = cases - casesPre;
            if (diff > max) {
                max = diff;
            }
            if (diff < min) {
                min = diff;
            }
        }
        ResultDTO resultDTO = new ResultDTO(country, min, max);
        return resultDTO;
    }

    private Map<Date, Integer> collectData(String country, Date dateFrom, Date dateTo) throws IOException, ParseException {
        Calendar cal = Calendar.getInstance();
        cal.setTime(dateFrom);
        cal.add(Calendar.DAY_OF_YEAR, -2);
        dateFrom = cal.getTime();
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
