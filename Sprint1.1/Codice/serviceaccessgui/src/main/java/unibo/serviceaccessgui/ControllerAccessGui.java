package unibo.serviceaccessgui;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

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


    //String loaddoneText = "Richiesta di scaricare inviata. \nAttendi per sapere quando andare via.";
    Long peso = 0l;
    String rispostatest = "";
    String ticket = "";

    String responseButton = "";
    Socket client;
    BufferedReader reader;
    BufferedWriter writer;

    //HOMEPAGE
    @GetMapping("/")
    public String homePage(Model model) {
        //ADD COSE PER STAMPARE NUMERO COLDROOM
        model.addAttribute("out", "XMAS Love" );
        model.addAttribute("disreq", false);
        model.addAttribute("discheck", true);
        model.addAttribute("disload", true);
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
        System.out.println("SONO ENTRATO IN FUNCTION");
        //msg( MSGID, MSGTYPE, SENDER, RECEIVER, CONTENT, SEQNUM )

        System.out.println(fw);
        peso = Long.parseLong(fw);
        String msg = "msg(depositRequest,request,roberto,tickethandler,depositRequest(" + peso + "),1)\n";
        String response;

        try {
            // sending message
            connectToColdStorageService();
            writer.write(msg);
            writer.flush();
            System.out.println("depositRequest sent");
            // handling response

            response = reader.readLine();
            String msgType = getMsgType(response);
            responseButton = msgType;
            ticket = getMsgValue(response);

            rispostatest = "La tua richiesta è stata: "+msgType+", il tuo biglietto è: " + ticket ;


        } catch (IOException e) {
            e.printStackTrace();
        }

        return "redirect:/responseReq";
    }

    @GetMapping("/responseReq")
    public String responseReq(Model model) {
        model.addAttribute("out", rispostatest);
        if(responseButton.equals("accept")){
            model.addAttribute("disreq", true);
            model.addAttribute("discheck", false);
            model.addAttribute("disload", true);
        }else{
            model.addAttribute("disreq", false);
            model.addAttribute("discheck", true);
            model.addAttribute("disload", true);
        }
        return "/static/ServiceAccessGuiWebPage";
    }


    @PostMapping("/checkmyticketreq")
    public String checkmyticketreq(Model model){
        System.out.println("SONO ENTRATO IN checkticket");
        //msg( MSGID, MSGTYPE, SENDER, RECEIVER, CONTENT, SEQNUM )
        String msg = "msg(checkmyticket,request,roberto,tickethandler,checkmyticket(" +ticket + "),1)\n";
        String response;

        String ticketValid = "false";

        try {
            // sending message
            connectToColdStorageService();

            writer.write(msg);
            writer.flush();
            System.out.println("message sent");

            // handling response
            response = reader.readLine();

            ticketValid = getMsgValue(response);
            rispostatest = ticketValid;
            responseButton = ticketValid;

        } catch (IOException e) {
            e.printStackTrace();
        }


        return "redirect:/responseCheck";
    }

    @GetMapping("/responseCheck")
    public String responseCheck(Model model) {
        model.addAttribute("out", rispostatest);
        if(responseButton.equals("true")){
            model.addAttribute("disreq", true);
            model.addAttribute("discheck", true);
            model.addAttribute("disload", false);
        }else{
            model.addAttribute("disreq", false);
            model.addAttribute("discheck", true);
            model.addAttribute("disload", true);
        }
        return "/static/ServiceAccessGuiWebPage";
    }

    @PostMapping("/loaddonereq")
    public String loaddonereq(Model model){

        String msg = "msg(loaddone,request,roberto,controller,loaddone(" +peso + "),1)\n";
        String response;

        String ticketValid = "false";

        try {
            // sending message
            connectToColdStorageService();
            writer.write(msg);
            writer.flush();
            System.out.println("message sent");

            // handling response
            response = reader.readLine();
            System.out.println("message read");
            System.out.println(response);
            rispostatest = "Il tuo peso è stato preso in carico! ADDIO";

        } catch (IOException e) {
            e.printStackTrace();
        }

        return "redirect:/responseLoad";
    }

    @GetMapping("/responseLoad")
    public String printResponse(Model model) {
        model.addAttribute("out", rispostatest);
        model.addAttribute("disreq", false);
        model.addAttribute("discheck", true);
        model.addAttribute("disload", true);
        return "/static/ServiceAccessGuiWebPage";
    }




    //// UTILITIES////////////////////////////////
    private void connectToColdStorageService() throws IOException {

        client = new Socket(COLDSTORAGESERVICEIPADDRESS, COLDSTORAGESERVICEPORT);
        writer = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));
        reader = new BufferedReader(new InputStreamReader(client.getInputStream()));

    }

    private String[] getParameters(String msg) {
        return msg.split("\\(")[2].split("\\)")[0].split(",");
    }

    private String getMsgType(String msg) {
        return msg.split("\\(")[1].split(",")[0];
    }

    private String getMsgValue(String msg) {
        return msg.split("\\(|\\)")[2];
    }


}
