package unibo.serviceaccessgui;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@Controller
@EnableScheduling
public class ControllerAccessGui {
    @Value("${spring.application.name}")
    String appName;
    MessageSender sender = new MessageSender();

    @GetMapping("/")
    public String homePage(Model model) {
        this.aggiornaPesoCorrente(model);
        return "/static/ServiceAccessGuiWebPage";
    }

    @ExceptionHandler
    public ResponseEntity handle(Exception ex) {
        HttpHeaders responseHeaders = new HttpHeaders();
        return new ResponseEntity(
                "HIControllerDemo ERROR " + ex.getMessage(),
                responseHeaders, HttpStatus.CREATED);
    }

    private void aggiornaPesoCorrente(Model model){
        String msg = "msg(getweightF,request,accessgui,facade,getweight(NO_PARAM),1)\n";
        String response = sender.sendMessage(msg);
        String[] weights = response.split("\\(|\\)")[2].split(",");
        //model.addAttribute("cw", weights[0]);
        model.addAttribute("ew", weights[1]);
    }

}
