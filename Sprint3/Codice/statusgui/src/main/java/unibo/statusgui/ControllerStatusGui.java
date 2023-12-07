package unibo.statusgui;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;

@CrossOrigin
@Controller
@EnableScheduling
public class ControllerStatusGui {
    @Value("${spring.application.name}")
    String appName;
    MessageSender sender = new MessageSender();

    @GetMapping("/")
    public String homePage(Model model) {
        this.aggiornaPesoCorrente(model);
        this.aggiornaBigliettiRifiutati(model);
        this.aggiornaRobotPos(model);
        return "/static/ServiceStatusGui";
    }

    @ExceptionHandler
    public ResponseEntity handle(Exception ex) {
        HttpHeaders responseHeaders = new HttpHeaders();
        return new ResponseEntity(
                "HIControllerDemo ERROR " + ex.getMessage(),
                responseHeaders, HttpStatus.CREATED);
    }

    private void aggiornaPesoCorrente(Model model){
        String msg = "msg(getweight,request,statusgui,facade,getweight(NO_PARAM),1)\n";
        String response = sender.sendMessage(msg);
        System.out.println(response);
        String[] weights = response.split("\\(|\\)")[2].split(",");
        model.addAttribute("ew", weights[0]);
        model.addAttribute("pw", weights[1]);
    }

    private void aggiornaBigliettiRifiutati(Model model){
        String msg = "msg(getrejectedtickets,request,statusgui,facade,getrejectedtickets(NO_PARAM),1)\n";
        String response = sender.sendMessage(msg);
        System.out.println(response);
        String rejectednum = response.split("\\(|\\)")[2];
        model.addAttribute("rt", rejectednum);
    }

    private void aggiornaRobotPos(Model model){
        String msg = "msg(getrobotstate,request,statusgui,facade,getrobotstate(NO_PARAM),1)\n";
        String response = sender.sendMessage(msg);
        System.out.println(response);
        String[] robotpos = response.split("\\(|\\)|,");
        model.addAttribute("maintext", "RobotPos=("+ robotpos[7] +"," + robotpos[7] +") direction="+robotpos[10]);

    }

}
