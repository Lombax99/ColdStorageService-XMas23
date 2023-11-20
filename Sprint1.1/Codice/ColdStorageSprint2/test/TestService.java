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

            //send message
            out.write("msg(depositRequestF,request,test,facade,depositRequestF(1),1)\n");
            out.flush();
            //wait for response
            String response= in.readLine();
            response = response.split(",")[4];
            System.out.println(response);

            String result = response.split("F")[0];
            assertTrue(result.equalsIgnoreCase("accept"));

            String ticket = response.replace("acceptF(","");
            ticket = ticket.replace(")","");

            //Thread.sleep(800);
            System.out.println("sleep 2 minutes");
            TimeUnit.MINUTES.sleep(2);


            out.write("msg(checkmyticketF,request,test,facade,checkmyticketF(" + ticket + "),1)\n");
            out.flush();
            //wait for response
            String response1 = in.readLine();
            System.out.println(response1);

            response1 = response1.split(",")[4];
            String checked = response1.replace("ticketcheckedF(","");
            checked = checked.replace(")","");
            assertTrue(checked.equalsIgnoreCase("false"));

        }catch(Exception e){
            fail();
            System.out.println(e.getStackTrace());
        }
    }
}