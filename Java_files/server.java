import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;

public class server implements Runnable{
	static int[] loc;

	@Override
	public void run() {
		runServer();
		
		
		// TODO Auto-generated method stub
		
	}
	
	public static void runServer()
	{
		int portNumber = 5000;
		 
	    try {
	      ServerSocket serverSocket = new ServerSocket(portNumber);
	      Socket clientSocket = serverSocket.accept();
	 
	      PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
	      BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
	      while(in.readLine() != null)
	      {
	    	  	
	    	  	getLoc(in.readLine());
	    	  //System.out.println(in.readLine());
	    	  	
	      }
	 
	      
	    } catch (Exception e) {
	      e.printStackTrace();
	    }
	}
	static void getLoc(String s)
	{
		
		String[] temp = s.split(" ");
		int[] temp2 = new int[temp.length];
		for(int i = 0; i < temp.length; i++)
		{
			temp2[i] = Integer.parseInt(temp[i]);
			
		}
		loc = temp2;
		//System.out.println(Arrays.toString(temp2));
		
		
		
		
	}

}
