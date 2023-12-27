import java.io.*;
import java.net.Socket;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

import static org.junit.Assert.*;

public class TestService{

    @Test
    public void mainUseCaseTest(){
        //connect to port
        try{
            Socket client= new Socket("localhost", 8040);
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));
            BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));


            out.write("msg(depositRequest,request,test2,facade,depositRequest(1),1)\n");
            out.flush();
            //wait for response
            String response= in.readLine();
            response = response.split(",")[4];

            String result = response.split("T")[0];
            assertTrue(result.equalsIgnoreCase("accept("));

            String ticket = response.replace("accept(","");
            ticket = ticket.replace(")","");
            System.out.println("ticket: "+ticket);


            //Thread.sleep(800);
            System.out.println("sleep 1 minutes");
            TimeUnit.MINUTES.sleep(1);

            //send message second client check ticket
            out.write("msg(checkmyticket,request,test2,facade,checkmyticket(" + ticket + "),1)\n");
            out.flush();
            //wait for response
            String response1 = in.readLine();
            System.out.println("response check: "+response1);

            response1 = response1.split(",")[4];
            String checked = response1.replace("ticketchecked(","");
            checked = checked.replace(")","");
            assertTrue(checked.equalsIgnoreCase("false"));

        }catch(Exception e){
            fail();
            System.out.println(e.getStackTrace());
        }
    }
}