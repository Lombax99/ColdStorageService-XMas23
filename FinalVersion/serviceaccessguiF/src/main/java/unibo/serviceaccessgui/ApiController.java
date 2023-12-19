package unibo.serviceaccessgui;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.*;
import java.net.Socket;

@RestController
@RequestMapping("/api")
public class ApiController {

    private MessageSender sender = new MessageSender();


    @PostMapping("/weightreq")
    public String weightreq(){
        String msg = "msg(getweight,request,accessgui,facade,getweight(NO_PARAM),1)\n";
        return sender.sendMessage(msg);
    }

    @PostMapping("/depositreq")
    public String depositreq(@RequestParam String fw){
        String msg = "msg(depositRequest,request,accessgui,facade,depositRequest(" + fw + "),1)\n";
        return sender.sendMessage(msg);
    }

    @PostMapping("/checkreq")
    public String checkreq(@RequestParam(name = "ticket") String ticket){
        String msg = "msg(checkmyticket,request,accessgui,facade,checkmyticket(" + ticket + "),1)\n";
        return sender.sendMessage(msg);
    }

    @PostMapping("/loadreq")
    public String loadreq(@RequestParam(name = "weight") String weight){
        String msg = "msg(loaddone,request,accessgui,facade,loaddone(" +weight + "),1)\n";
        return sender.sendMessage(msg);
    }

}
