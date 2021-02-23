package ir.darkdeveloper.sma;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin("*")
public class MainController {

   @RequestMapping("/api")
    public String index(){
        return "index";
    }
}
