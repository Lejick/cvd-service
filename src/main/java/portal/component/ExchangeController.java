package portal.component;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class ExchangeController {

    private final Logger LOGGER = LoggerFactory.getLogger(ExchangeController.class);
    public static final String UPLOAD_FORM = "inputForm";

    @GetMapping("/")
    public String index(Model model) {
        return UPLOAD_FORM;
    }

    @GetMapping("/resultForm")
    public String resultForm(){
        return "resultForm";
    }


    @GetMapping("/exchangeRate")
    public String exchangeRate( @RequestParam(name="amount") String amount ,
                                RedirectAttributes redirectAttributes) {

        redirectAttributes.addFlashAttribute("message_amount", "your amoint in rub: "+amount);

        return "redirect:/resultForm";
    }


}