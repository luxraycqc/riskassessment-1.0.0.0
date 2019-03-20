package cn.net.aichain.edge.ms.security.idm;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DefaultController {

    @GetMapping("/")
    public String home1() {
        return "/index.html";
    }

}
