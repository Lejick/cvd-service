package portal;

import java.io.IOException;
import java.text.ParseException;
import java.util.*;

import static portal.DateTransformUtil.getDate;

public class Main {


    public static void main(String[] args) throws IOException, ParseException {

        CvdService cvdService = new CvdService();
        Date dateFrom = getDate(1, 6, 2020);
        Date dateTo = getDate(5, 6, 2020);
        String[] countries = {"south-africa", "palestine", "serbia"};
        List<ResultDTO> resultList = new ArrayList<>();
        for (String country : countries) {
            resultList.add(cvdService.find(country, dateFrom, dateTo));
        }
        dateFrom = getDate(3, 6, 2020);
        dateTo = getDate(6, 6, 2020);
        ;

        resultList = new ArrayList<>();
        for (String country : countries) {
            resultList.add(cvdService.find(country, dateFrom, dateTo));
        }
        dateFrom = getDate(4, 6, 2020);
        dateTo = getDate(5, 6, 2020);

        resultList = new ArrayList<>();
        for (String country : countries) {
            resultList.add(cvdService.find(country, dateFrom, dateTo));
        }
    }

}
