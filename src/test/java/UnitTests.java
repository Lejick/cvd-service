
import org.junit.Test;
import portal.component.CvdService;
import portal.dto.ResultDTO;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static portal.processing.DateTransformUtil.getDate;

public class UnitTests {

    @Test
    public void minMaxValue() throws IOException, ParseException, InterruptedException {
        CvdService cvdService = new CvdService();
        Date dateFrom = getDate(1, 7, 2020);
        Date dateTo = getDate(3, 7, 2020);
        String[] countries = {"south-africa", "palestine", "serbia"};
        Thread.sleep(1000);
        List<ResultDTO> resultList = cvdService.findAll(countries, dateFrom, dateTo);
        for (ResultDTO resultDTO : resultList) {
            if (resultDTO.getCountry().equals("south-africa")) {
                assertEquals(java.util.Optional.of(resultDTO.getMin()), java.util.Optional.of(8124));
            } else if (resultDTO.getCountry().equals("palestine")) {
                assertEquals(java.util.Optional.of(resultDTO.getMin()), java.util.Optional.of(254));
            } else if (resultDTO.getCountry().equals("serbia")) {
                assertEquals(java.util.Optional.of(resultDTO.getMin()), java.util.Optional.of(272));
            }
        }
    }

    @Test
    public void sizeValue() throws IOException, ParseException, InterruptedException {
        CvdService cvdService = new CvdService();
        Date dateFrom = getDate(1, 7, 2020);
        Date dateTo = getDate(3, 7, 2020);
        String[] countries = {"south-africa", "palestine", "serbia"};
        List<ResultDTO> resultList = new ArrayList<>();
        Thread.sleep(1000);
        resultList = cvdService.findAll(countries, dateFrom, dateTo);
        assertEquals(resultList.size(), 3);

        dateFrom = getDate(1, 7, 2020);
        dateTo = getDate(3, 7, 2020);
        String[] countries2 = {"south-africa", "palestine", "serbia", "australia"};
        Thread.sleep(1000);
        resultList = cvdService.findAll(countries2, dateFrom, dateTo);
        assertEquals(resultList.size(), 4);

    }


    @Test
    public void cachedValue() throws IOException, ParseException, InterruptedException {
        CvdService cvdService = new CvdService();
        Date dateFrom = getDate(1, 7, 2020);
        Date dateTo = getDate(3, 7, 2020);
        String[] countries = {"south-africa", "palestine", "serbia"};
        Thread.sleep(1000);
        List<ResultDTO> resultList = cvdService.findAll(countries, dateFrom, dateTo);
        for (ResultDTO resultDTO : resultList) {
            assertEquals(resultDTO.getQueryType(), "Server");
        }
        dateFrom = getDate(2, 7, 2020);
        dateTo = getDate(7, 7, 2020);
        Thread.sleep(1000);
        resultList = cvdService.findAll(countries, dateFrom, dateTo);
        for (ResultDTO resultDTO : resultList) {
            assertEquals(resultDTO.getQueryType(), "Server");
        }
        dateFrom = getDate(4, 7, 2020);
        dateTo = getDate(5, 7, 2020);

        resultList = cvdService.findAll(countries, dateFrom, dateTo);
        for (ResultDTO resultDTO : resultList) {
            assertEquals(resultDTO.getQueryType(), "Cache");
        }
    }


}
