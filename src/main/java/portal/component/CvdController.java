package portal.component;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import portal.CvdService;
import portal.ResultDTO;

import java.util.Date;
import java.util.List;

@Controller
public class CvdController {

    @Autowired
    CvdService cvdService;

    @GetMapping("/find")
    @ResponseBody
    public ResponseEntity<List<ResultDTO>> exchangeRate(@RequestParam String[] countries,@RequestParam(name="dateFrom") @DateTimeFormat(pattern = "dd.MM.yyyy") Date dateFrom ,
                                                        @RequestParam(name="dateTo") @DateTimeFormat(pattern = "dd.MM.yyyy") Date dateTo ) {

       List<ResultDTO> list=cvdService.findAll(countries,dateFrom,dateTo);
        return new ResponseEntity<List<ResultDTO>>(list, HttpStatus.OK);
    }


}