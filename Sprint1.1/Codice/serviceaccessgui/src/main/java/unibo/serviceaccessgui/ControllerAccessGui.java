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


    Socket client;
    BufferedReader reader;
    BufferedWriter writer;

    //HOMEPAGE
    @GetMapping("/")
    public String homePage(Model model) {
        //ADD COSE PER STAMPARE NUMERO COLDROOM
        //model.addAttribute("arg", appName);
        return "/static/ServiceAccessGuiWebPage";
    }

    @ExceptionHandler
    public ResponseEntity handle(Exception ex) {
        HttpHeaders responseHeaders = new HttpHeaders();
        return new ResponseEntity(
                "HIControllerDemo ERROR " + ex.getMessage(),
                responseHeaders, HttpStatus.CREATED);
    }

    @PostMapping("/storefoodreq")
    public String storefoodreq(Model model, @RequestParam(name = "foodweight") String fw){
        System.out.println("SONO ENTRATO IN FUNCTION");
        //msg( MSGID, MSGTYPE, SENDER, RECEIVER, CONTENT, SEQNUM )
        String msg = "msg(depositRequest,request,roberto,tickethandler,depositRequest(" +fw + "),1)\n";
        String response;

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
            String msgType = getMsgType(response);
            System.out.println(msgType);
           /** if (msgType.equals("ticketaccepted")) {
                String[] parameters = getParameters(response);
                model.addAttribute("ticketcode", parameters[0]);
                model.addAttribute("ticketsecret", parameters[1]);

            }

           const ot = document.getElementById("outputText");
            var loaddoneText = "Richiesta di scaricare inviata. \nAttendi per sapere quando andare via.";
            ot.innerHTML = loaddoneText;**/



        } catch (IOException e) {
            e.printStackTrace();
        }

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

}
