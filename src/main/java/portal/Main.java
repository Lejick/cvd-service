package portal;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

public class Main {
    static String server = "https://api.covid19api.com/country/";
    static String ending = "/status/confirmed";
    static Map<String, Map<Date, Integer>> countryCasesMap = new HashMap();

    static SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US);

    public static void main(String[] args) throws IOException, ParseException {


        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, 2020);
        cal.set(Calendar.MONTH, 6);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.set(Calendar.HOUR, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        Date dateFrom = cal.getTime();
        System.out.println(format.format(dateFrom));
        cal.set(Calendar.YEAR, 2020);
        cal.set(Calendar.MONTH, 6);
        cal.set(Calendar.DAY_OF_MONTH, 5);
        Date dateTo = cal.getTime();
        System.out.println(format.format(dateTo));
        String[] countries = {"south-africa", "palestine", "serbia"};
        collectData(countries, dateFrom, dateTo);

        cal.set(Calendar.YEAR, 2020);
        cal.set(Calendar.MONTH, 6);
        cal.set(Calendar.DAY_OF_MONTH, 3);
        dateFrom = cal.getTime();
        System.out.println(format.format(dateFrom));
        cal.set(Calendar.YEAR, 2020);
        cal.set(Calendar.MONTH, 6);
        cal.set(Calendar.DAY_OF_MONTH, 6);
        dateTo = cal.getTime();
        collectData(countries, dateFrom, dateTo);
        System.out.println();
    }

    public static LocalDate convertToLocalDateViaInstant(Date dateToConvert) {
        return dateToConvert.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
    }

    public static ResultDTO findCached(String country, Date dateFrom, Date dateTo) {
        Map<Date, Integer> casesMap = countryCasesMap.get(country);

        Integer min = Integer.MAX_VALUE;
        Integer max = 0;
        if (countryCasesMap.get(country) == null) {
            return null;
        }
        LocalDate startDate = convertToLocalDateViaInstant(dateFrom);
        LocalDate endDate = convertToLocalDateViaInstant(dateTo);
        for (LocalDate date = startDate; date.isBefore(endDate); date = date.plusDays(1)) {
            Integer cases = casesMap.get(date);
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

    public static void collectData(String[] countries, Date dateFrom, Date dateTo) throws IOException, ParseException {
        for (String country : countries) {
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
                Date date = format.parse(dto.getDate());
                caseMap.put(date, dto.cases);
            }
            countryCasesMap.put(country, caseMap);

        }
    }
}
