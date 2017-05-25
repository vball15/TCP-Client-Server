package project1part2;

/**
 * Vincent Ball
 * CIS 457 Project 1
 * October 3rd, 2016
 * Client class that requests a file from server
 * then receives that file from server
 */
import java.io.*;
import java.net.Socket;

import java.net.SocketException;

public class tcpclientfinal {
    
    /**
     * Main method, file request sent
     * File received from server
     * Error checking
     * @param args
     * @throws IOException
     */
    public static void main(String args[]) throws IOException{

        /*String for IP address*/
        String address = null;

        /*Socket for client*/
        Socket clientSocket = null;

        /*String for file name*/
        String file = null;

        /*BufferedReader to read user input*/
        BufferedReader inFromUser = null;

        /*DataInputStream variable*/
        DataInputStream dis = null;

        /*initialize BufferedReader*/
        inFromUser = new BufferedReader(new InputStreamReader(System.in));

        try{
            System.out.println("Enter IP Adress: "); //enter valid IP address
            address = inFromUser.readLine();
            System.out.println("Enter a Port Number: "); //enter valid port number
            int port = Integer.parseInt(inFromUser.readLine());

            while(true){
                //create socket using user entered IP address and port
                clientSocket = new Socket(address, port);
                DataOutputStream outToServer =
                        new DataOutputStream(clientSocket.getOutputStream());
                System.out.println("Client Address : " + address);
                //user enters file name to be requested from server
                System.out.println("Enter file name ( Enter QUIT to end):");
                file = inFromUser.readLine();

                if (file.equals("QUIT")) {
                    outToServer.writeBytes(file + "\n");
                    clientSocket.close(); //close socket if user wants to exit
                }
                else
                    outToServer.writeBytes(file + "\n");
                    dis = new DataInputStream(clientSocket.getInputStream()); //get the socket's input stream
                    long size = dis.readLong(); //get the size of the file.
                    InputStream in = clientSocket.getInputStream();
                    OutputStream out = new FileOutputStream(file); //stream to write out file
                    byte[] buf = new byte[(int) size]; //buffer
                    int len = 0;
                    while ((len = in.read(buf)) > -1){
                        out.write(buf, 0, len); //write buffer
                    }
                    out.close();// clean up
                // System.out.println("\nFile received\n");
            }
        }

        catch(NullPointerException e){
            System.out.println("Socket Error");
        }

        catch (IllegalArgumentException e){
            //invalid port number entered
            System.out.println("Error: Port number must not be negative, or contain letters");
        }

        catch(EOFException e){
            //trying to read file, but reached the end of the file already
            System.out.println("Reached end of file unexpectedly");
        }

        catch(SocketException e){
            //socket closed
            //either by user's request, or unexpected close
            System.out.println("Socket Closed");
        }

        catch(IOException e){
            //Could not connect to address
            System.out.println("Socket read Error.  Could not connect to address");
        }
        
        finally{
            //close socket if input from user was "QUIT"
            //client has chosen to exit, and close socket
            try {
                inFromUser.close();
                clientSocket.close();
                System.out.println("Connection Closed");
            }
            catch (NullPointerException e){
                //if invalid IP address or port # entered
                //trying to close a socket will produce NullPointer
                //Tell user their input was invalid
                System.out.println("Invalid IP address or port number");
            }
        } //end finally
    }
}