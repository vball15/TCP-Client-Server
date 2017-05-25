package project1part2;

/**
 * Vincent Ball
 * CIS 457 Project 1
 * October 3rd, 2016
 * Server class that receives a file name
 * requested by client, and sends that file to the client
 */
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class tcpserverfinal {
    /**
     * Main method, connection created
     * Threads created
     * @param args
     * @throws IOException
     */
    public static void main(String args[]) throws IOException {

        Socket socket = null;
        ServerSocket serverSocket = null;
        try{
            BufferedReader inFromUser =
                    new BufferedReader(new InputStreamReader(System.in));
            System.out.println("Enter a port number: ");
            int port = Integer.parseInt(inFromUser.readLine());
            serverSocket = new ServerSocket(port); //connect to specified port
            System.out.println("Server Listening......"); //waits for a client to connect
        }
        catch(IOException e){
            e.printStackTrace();
            System.out.println("Server error");

        }

        while(true){
            try{
                socket= serverSocket.accept();
                System.out.println("connection Established\n");
                //client connected, create thread
                ServerThread serverThread = new ServerThread(socket);
                serverThread.start();
            }

            catch(Exception e){
                e.printStackTrace();
                System.out.println("Connection Error");

            }
        }
    }
}

class ServerThread extends Thread{

    /*String for file name*/
    String file = null;

    /*BufferedReader to read file name from client*/
    BufferedReader inFromClient = null;

    /*Socket for client connections*/
    Socket socket2 = null;

    /*DataOutputStream variable*/
    DataOutputStream outToClient = null;

    /*InputStream variable*/
    InputStream in = null;

    /*OutputStream variable*/
    OutputStream out = null;

    /*File to be sent*/
    File myFile = null;

    /******
     * Constructor for ServerThread class
     * @param s a Socket
     * @throws IOException
     */
    public ServerThread(Socket s) throws IOException {
        this.socket2 = s;
    }

    /****
     * File transfer
     */
    public void run() {
        try{
            inFromClient = new BufferedReader(new InputStreamReader(socket2.getInputStream()));

        }catch(IOException e){
            System.out.println("IO error in server thread");
        }
        try {
            file = inFromClient.readLine();
            //receive file name from client
            System.out.println("File specified by client: " + file + "\n");
            while(!file.equals("QUIT")){
                //send file to client as long as file name isn't "QUIT"
                final String fileToSend = "C:\\Users\\User\\IdeaProjects\\CIS457Project1\\src\\sample\\" + file;
                myFile = new File(fileToSend);

                if(myFile.exists() && myFile.isFile() )
                {
                    outToClient = new DataOutputStream(socket2.getOutputStream());
                    outToClient.writeLong(myFile.length()); //file length
                    InputStream input = new FileInputStream(myFile);
                    out = socket2.getOutputStream(); //get output stream
                    byte[] buf = new byte[1024]; //create buffer
                    int read = 0;
                    while ((read = input.read(buf)) > -1) {
                        out.write(buf, 0, read); //write buffer
                    }
                    input.close();
                    out.close();
                    System.out.println("File successfully sent!\n");
                }
                else
                    System.out.println("Could not send file");
                }

        } catch (IOException e) {
            file = this.getName(); //reused file name for getting thread name
            //specifies which client/thread disconnected/had an error
            System.out.println("IO Error with Client " + file + "\n");
        }
        catch(NullPointerException e){
            file = this.getName(); //reused file name for getting thread name
            //specifies which client/thread disconnected/had an error
            System.out.println("Client " + file +" Closed\n");
        }

        finally{
            //if input from client was "QUIT", close socket and streams for that client
            try{
                System.out.println("Connection Closing..");
                if (inFromClient != null){
                    inFromClient.close();
                }

                if (outToClient != null){
                    outToClient.close();
                }

                if (socket2 != null) {
                    socket2.close();
                    System.out.println("\nClient disconnected, socket closed\n");
                }
            }
            catch(IOException ie){
                System.out.println("Error while closing socket");
            }
        }//end finally
    }
}
