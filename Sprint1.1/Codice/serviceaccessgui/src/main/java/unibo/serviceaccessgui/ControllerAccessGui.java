package unibo.serviceaccessgui;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

import io.micrometer.common.lang.Nullable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller //ANNOTAZIONE IMPORTANTE
public class ControllerAccessGui {
    @Value("${spring.application.name}")
    String appName;
    String COLDSTORAGESERVICEIPADDRESS = "127.0.0.1";
    int COLDSTORAGESERVICEPORT = 8040;

    String currentWeight = "";

    //String loaddoneText = "Richiesta di scaricare inviata. \nAttendi per sapere quando andare via.";
    Long peso = 0l;


    Socket client;
    BufferedReader reader;
    BufferedWriter writer;

    //HOMEPAGE
    @GetMapping("/")
    public String homePage(Model model) {
        //ADD COSE PER STAMPARE NUMERO COLDROOM
        model.addAttribute("out", "XMAS Love" );
        this.aggiornaPesoCorrente(model);
        this.enableButtons("default", model);
        return "/static/ServiceAccessGuiWebPage";
    }

    @ExceptionHandler
    public ResponseEntity handle(Exception ex) {
        HttpHeaders responseHeaders = new HttpHeaders();
        return new ResponseEntity(
                "HIControllerDemo ERROR " + ex.getMessage(),
                responseHeaders, HttpStatus.CREATED);
    }

    @PostMapping("/depositreq")
    public String depositreq(Model model, @RequestParam(name = "foodweight") String fw){

        this.aggiornaPesoCorrente(model);
        peso = Long.parseLong(fw);
        String msg = "msg(depositRequest,request,roberto,tickethandler,depositRequest(" + peso + "),1)\n";

        String ticket = "";
        String rispostatest = "";
        String responseButton = "";
        try {
            String response = this.sendMessage(msg);
            responseButton = getMsgType(response);
            ticket = getMsgValue(response);

            rispostatest = "La tua richiesta è stata:" + responseButton + ", il tuo biglietto è:" + ticket ;

        } catch (IOException e) {
            e.printStackTrace();
        }

        model.addAttribute("out", rispostatest);
        model.addAttribute("varticket", ticket);

        if(responseButton.equals("accept"))
            this.enableButtons("requestaccepted",model);
        else
            this.enableButtons("default",model);

        return "/static/ServiceAccessGuiWebPage";
    }


    @PostMapping("/checkmyticketreq")
    public String checkmyticketreq(Model model, @RequestParam(name = "varticket") String ticket){
        String msg = "msg(checkmyticket,request,roberto,tickethandler,checkmyticket(" + ticket + "),1)\n";
        String ticketValid = "false";
        try {
            String response = this.sendMessage(msg);
            ticketValid = getMsgValue(response);
        } catch (IOException e) {
            e.printStackTrace();
        }

        model.addAttribute("out", ticketValid);

        if(ticketValid.equals("true"))
            this.enableButtons("ticketaccepted", model);
        else
            this.enableButtons("default", model);

        this.aggiornaPesoCorrente(model);

        return "/static/ServiceAccessGuiWebPage";
    }

    @PostMapping("/loaddonereq")
    public String loaddonereq(Model model){
        String msg = "msg(loaddone,request,roberto,controller,loaddone(" +peso + "),1)\n";

        try {
            this.sendMessage(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }

        String rispostatest = "Il tuo peso è stato preso in carico! ADDIO";

        this.aggiornaPesoCorrente(model);
        model.addAttribute("out", rispostatest);
        this.enableButtons("default", model);
        return "/static/ServiceAccessGuiWebPage";
    }

    //// UTILITIES////////////////////////////////
    private void aggiornaPesoCorrente(Model model){
        String msg = "msg(getweight,request,roberto,coldroom,getweight(NO_PARAM),1)\n";
        String response = "";
        try{
            response = this.sendMessage(msg);
        } catch (IOException e){
            e.printStackTrace();
        }
        currentWeight = getMsgValue(response);
        model.addAttribute("coldroomweight", currentWeight);
    }

    private String sendMessage(String msg) throws IOException{
        this.connectToColdStorageService();
        writer.write(msg);
        writer.flush();
        System.out.println("message sent");

        // handling response
        return reader.readLine();
    }

    private void connectToColdStorageService() throws IOException {

        client = new Socket(COLDSTORAGESERVICEIPADDRESS, COLDSTORAGESERVICEPORT);
        writer = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));
        reader = new BufferedReader(new InputStreamReader(client.getInputStream()));

    }

    private void enableButtons(String  mode, Model model){
        switch (mode){
            case "requestaccepted":
                model.addAttribute("disreq", true);
                model.addAttribute("discheck", false);
                model.addAttribute("disload", true);
                break;
            case "ticketaccepted":
                model.addAttribute("disreq", true);
                model.addAttribute("discheck", true);
                model.addAttribute("disload", false);
                break;
            default:
                model.addAttribute("disreq", false);
                model.addAttribute("discheck", true);
                model.addAttribute("disload", true);

        }
    }

    private String getMsgType(String msg) {
        return msg.split("\\(")[1].split(",")[0];
    }

    private String getMsgValue(String msg) {
        return msg.split("\\(|\\)")[2];
    }
}
