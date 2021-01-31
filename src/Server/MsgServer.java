package Server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;

public class MsgServer {
	private static PrintWriter writer;
	private static ResultSet rs;
	private static java.sql.Statement st;
	private static ArrayList<PrintWriter> streams;
	private static Connection c;

	public static void main(String[] args) throws Exception{
		go();
	}

	private static void go() throws Exception{
		streams = new ArrayList<PrintWriter>();
		setDB();
		try {
			ServerSocket ss = new ServerSocket(5000);
			while(true){
				Socket socket = ss.accept();
				System.out.print("Got user!\n");
				writer = new PrintWriter(socket.getOutputStream());
				sendHistory();
				streams.add(writer);
				
				Thread t = new Thread(new Listener(socket));
				t.start();
			}
		} catch (Exception ex) {}	
	}
	
	private static void sendHistory() throws Exception{
		String SQL = "SELECT Message FROM Chat";
		ResultSet rs = st.executeQuery(SQL);
		
		while(rs.next()){
				writer.println(rs.getString("Message"));
				writer.flush();
			
		}
		
	}


	private static class Listener implements Runnable{
		
		BufferedReader reader;
		
		Listener(Socket socket){
			InputStreamReader is;
			try {
				is = new InputStreamReader(socket.getInputStream());
				reader = new BufferedReader(is);
			} catch (Exception e) {}			
		}
		@Override
		public void run() {
			String msg;
			try{
				while((msg=reader.readLine())!=null){
					System.out.print(msg);
					tellEveryone(msg);
				}
			}catch(Exception ex){}
			
		}
		
	}

	private static void tellEveryone(String msg) throws Exception{
		int x = msg.indexOf(':');
		String login = msg.substring(0, x);
		
		save(login,msg);
		
		Iterator<PrintWriter> iter = streams.iterator();
		while(iter.hasNext()){
			try{
				writer = iter.next();
				writer.println(msg);
				writer.flush();
				
			}catch(Exception ex){}
		}
		
	}

	private static void save(String login, String msg) throws Exception {
		String SQL ="INSERT INTO Chat (Login,Message) VALUES ('"+login+"','"+msg+"');";
		st.executeUpdate(SQL);
		System.out.println("OK!");
	}
	private static void setDB() throws Exception{
		 String USER = new String("appuser1");
	     String PASSWORD = new String("root");
	     String URL = new String("jdbc:sqlserver://localhost:1433;database=Messenger");
		try {
            Class.forName(com.microsoft.sqlserver.jdbc.SQLServerDriver.class.getName());
            c = java.sql.DriverManager.getConnection(URL,USER,PASSWORD);
            st = c.createStatement();
            if(c!=null) System.out.println("\nConnection Successful!");
        }
        catch (SQLException ex){
            ex.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
	}
}
