
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import static org.junit.Assert.assertEquals;


@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment=SpringBootTest.WebEnvironment.RANDOM_PORT)
public class IntegrationTests {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void InsertAndGetUrl() {
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        String url="http://pipcat.ru/wp-content/uploads/2016/08/kedilere-strese-sokabilecek-durumlar1.jpg";
        body.add("url", url);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        HttpEntity<MultiValueMap<String, Object>> requestEntity
                = new HttpEntity<>(body, headers);

        restTemplate.postForEntity("/inputForm", requestEntity, String.class);
        ResponseEntity responseEntity = restTemplate.getForEntity("/images/" , String.class, "");
        assertEquals("image/jpeg", responseEntity.getHeaders().get("Content-Type").get(0));
    }
}
