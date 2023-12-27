import java.io.*;
import java.net.Socket;

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
            out.write("msg(doJob,request,test,transporttrolley,doJob(5),12)\n");
            out.flush();
            //wait for response
            String response= in.readLine();
            System.out.println(response);
            assertTrue(response.contains("jobdone"));


        }catch(Exception e){
            fail();
            System.out.println(e.getStackTrace());
        }
    }
}