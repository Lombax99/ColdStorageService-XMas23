import java.io.*;
import java.net.Socket;
import java.sql.SQLOutput;
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
            String ticket = response.replace("accept(","");
            ticket = ticket.replace(")","");

            out.write("msg(checkmyticket,request,test2,facade,checkmyticket(" + ticket + "),1)\n");
            out.flush();
            String responseC= in.readLine();

            out.write("msg(loaddone,request,test2,facade,loaddone(1),1)\n");
            out.flush();
            String responseL= in.readLine();


            System.out.println("sleep 2 seconds");
            TimeUnit.SECONDS.sleep(4);

            out.write("msg(stop,dispatch,test2,controller,stop(),1)\n");


            System.out.println("sleep 1 seconds");
            TimeUnit.SECONDS.sleep(1);

            out.write("msg(getrobotstate,request,test2,robotpos,getrobotstate(ARG),1)\n");
            System.out.println("attendo il flush");
            out.flush();
            String responsePos1= in.readLine();
            responsePos1 = responsePos1.split(",")[4];
            System.out.println("pos1: "+responsePos1); //robotstate(pos(0,4),DOWN)

            System.out.println("sleep 1 seconds for pos");
            TimeUnit.SECONDS.sleep(1);

            out.write("msg(getrobotstate,request,test2,robotpos,getrobotstate(ARG),1)\n");
            out.flush();
            String responsePos2= in.readLine();
            responsePos2 = responsePos2.split(",")[4];
            System.out.println("pos2: "+responsePos2);   //robotstate(pos(0,4),DOWN)

            assertTrue(responsePos1.equalsIgnoreCase(responsePos2));

            out.write("msg(continue,dispatch,test2,controller,continue(),1)\n");


            System.out.println("sleep 1 seconds for pos");
            TimeUnit.SECONDS.sleep(1);

            out.write("msg(getrobotstate,request,test2,robotpos,getrobotstate(ARG),1)\n");
            out.flush();
            String responsePos3= in.readLine();

            System.out.println("pos3: "+responsePos3);

            assertFalse(responsePos1.equalsIgnoreCase(responsePos3));


        }catch(Exception e){
            fail();
            System.out.println(e.getStackTrace());
        }
    }
}