/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package developertest;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 *
 * @author ALex
 */
public class Cliente {

    Socket socket = null;
    DataOutputStream os = null;
    DataInputStream is = null;
    BufferedReader reader = null;

    public void run() throws IOException, InterruptedException {

        String startMessage = messagePrepare.startMessage();
        startMessage = startMessage.toUpperCase();
        byte[] data = HexStringConverter.hexStringToByteArray(startMessage);
        //try open conection and open streams
        try {
            socket = new Socket("191.237.249.140", 64013);
            System.out.println("Conectado ao servidor 191.237.249.140 pela porta 64013");
            os = new DataOutputStream(socket.getOutputStream());
            is = new DataInputStream(socket.getInputStream());
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (UnknownHostException e) {
            System.err.println("Don't know about host: hostname");
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to: hostname");
        }

        if (socket != null && os != null && is != null) {
            try {
                System.out.println("Tudo pronto para troca de msgs!");
                Thread.sleep(2000);
                os.write(data);
                os.flush();

                System.out.println("Cliente > Server                    " + startMessage);
                String responseLine = null;
                int flag = 0;

                while (flag != 1) {

                    //take the two firsts bytes for find the length
                    byte[] len = new byte[2];
                    is.read(len, 0, 2);
                    String length;
                    length = HexStringConverter.bytesToHex(len);
                    length = length.substring(2, 3) + length.substring(3, 4) + length.substring(0, 1) + length.substring(1, 2);
                    int sizeForRead = HexStringConverter.hexTodecimal(length);
                    //take payload and bparity
                    byte[] payLoadBP = new byte[sizeForRead];

                    //wait for full transmition// bad comunication                    
                    is.readFully(payLoadBP, 0, sizeForRead);

                    String payLoadBparity;
                    payLoadBparity = HexStringConverter.bytesToHex(payLoadBP);

                    responseLine = length + payLoadBparity;
                    responseLine = responseLine.toUpperCase();
                    System.out.println("Cliente < Server                     " + responseLine);

                    if (!messagePrepare.checkStopOrFailMessage(responseLine)) {
                        System.out.println("######## Finalizando conexao .... ");
                        os.close();
                        is.close();
                        socket.close();
                        flag = 1;
                    } else if (!messagePrepare.checkParityByte(responseLine)) {
                        byte[] dataRT = HexStringConverter.hexStringToByteArray(messagePrepare.retransmitMessage);
                        System.out.println("Cliente > Server                " + messagePrepare.retransmitMessage);
                        os.write(dataRT);
                        os.flush();
                    } else {
                        //break the ciphertext
                        String payLoad = messagePrepare.getPayLoad(responseLine);
                        String answerToServer = messagePrepare.breakTheCipherText(payLoad);
                        //send decripted answer
                        System.out.println("Cliente > server = " + answerToServer);
                        byte[] dataAnswer = HexStringConverter.hexStringToByteArray(answerToServer);
                        os.write(dataAnswer);
                        os.flush();
                    }

                    //testar sem essas linhas aqu

                }
            } catch (UnknownHostException e) {
                System.err.println("Trying to connect to unknown host: " + e);
            } catch (IOException e) {
                System.err.println("IOException:  " + e);
            }

        }
        
        System.out.println("Não foi possível estabelecer a conexão");

    }

}
