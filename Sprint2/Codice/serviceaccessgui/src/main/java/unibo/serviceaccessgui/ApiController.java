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
        String msg = "msg(getweight,request,accessgui,coldroom,getweight(NO_PARAM),1)\n";
        return sender.sendMessage(msg);
    }

    @PostMapping("/depositreq")
    public String depositreq(@RequestParam String fw){
        String msg = "msg(depositRequestF,request,accessgui,facade,depositRequestF(" + fw + "),1)\n";
        return sender.sendMessage(msg);
    }

    @PostMapping("/checkreq")
    public String checkreq(@RequestParam(name = "ticket") String ticket){
        String msg = "msg(checkmyticketF,request,accessgui,facade,checkmyticketF(" + ticket + "),1)\n";
        return sender.sendMessage(msg);
    }

    @PostMapping("/loadreq")
    public String loadreq(@RequestParam(name = "weight") String weight){
        String msg = "msg(loaddoneF,request,accessgui,facade,loaddoneF(" +weight + "),1)\n";
        return sender.sendMessage(msg);
    }

}
