/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package developertest;

import java.io.UnsupportedEncodingException;

/**
 *
 * @author ALex
 */
public class messagePrepare {

    private static final String startMessage = "alexlobobsb@gmail.com";
    public static final String retransmitMessage = "0900534159414741494E02";
    private static final String stopMessage = "53544F50";
    private static final String failMessage = "47414D454F564552";
    private static final String byteSpace = "20";

    //initial message
    public static String startMessage() throws UnsupportedEncodingException {
        String startMessageHex = HexStringConverter.getHexStringConverterInstance().stringToHex(startMessage);
        startMessageHex = insertLength(startMessageHex);
        startMessageHex = insertBitParity(startMessageHex);
        return startMessageHex;
    }

//insert payLoad
    public static String insertLength(String startMessageHex) {
        int lengPayload = startMessageHex.length() / 2 + 1;
        String messagePayload = getLength(lengPayload);
        return messagePayload + startMessageHex;
    }

    public static String insertLengthL(String startMessageHex) {

        int lengPayload = startMessageHex.length() / 2 + 1;
        String messagePayload = getLength2(lengPayload);
        return messagePayload + startMessageHex;
    }

//insert bitparity
    public static String insertBitParity(String MessageHex) {
        String resultFinal = "";

        char[] result = MessageHex.toCharArray();
        MessageHex = formatString(result);

        String[] arrayValores = MessageHex.split("\\s+");

        int countOdd = 0;
        int countEven = 0;
        for (String s : arrayValores) {
            s = HexStringConverter.hexToBinary(s);
            s = HexStringConverter.insertBitsToComplet(s);
            countOdd += countOdd(s);
            countEven += countEven(s);
        }
        if (countOdd % 2 != 0 && countEven % 2 != 0) {
            resultFinal = insertOddEven(MessageHex);
        } else if (countOdd % 2 != 0) {
            resultFinal = insertOdd(MessageHex);
        } else if (countEven % 2 != 0) {
            resultFinal = insertEven(MessageHex);
        } else {
            resultFinal = MessageHex + " 00";
        }

        resultFinal = resultFinal.replace(" ", "");
        return resultFinal;
    }

    //parity verification
    public static boolean checkParityByte(String MessageHex) throws InterruptedException {

        char[] result = MessageHex.toCharArray();
        MessageHex = formatString(result);

        String[] arrayValores = MessageHex.split("\\s+");
        int countOdd = 0;
        int countEven = 0;
        for (String s : arrayValores) {
            s = HexStringConverter.hexToBinary(s);
            s = HexStringConverter.insertBitsToComplet(s);
            countEven += countEven(s);
            countOdd += countOdd(s);
        }
        if (countOdd % 2 != 0) {
            return false;
        } else if (countEven % 2 != 0) {
            return false;
        }

        return true;

    }

    //decript message
    public static String breakTheCipherText(String messageCripted) throws UnsupportedEncodingException {

        //common
        char[] result1 = messageCripted.toCharArray();
        messageCripted = formatString(result1);
        String commonByte = checkCommmonByte(messageCripted);
        //key
        String key = HexStringConverter.xorHex(commonByte, byteSpace);
        String messageDecript = useKeyDecript(messageCripted, key);
        messageDecript = HexStringConverter.convertHexToString(messageDecript);
        System.out.println("Mensagem decriptada = " + messageDecript + "   ->>L = " + messageDecript.length());
        //remove spaces
        messageDecript = removeBlankSpaces(messageDecript);
        //string to hex
        messageDecript = HexStringConverter.stringToHex(messageDecript);
        //insert leng
        messageDecript = insertLengthL(messageDecript);
        //insert bitParity
        messageDecript = insertBitParity(messageDecript);
        messageDecript = messageDecript.replace(" ", "");

        return messageDecript;

    }

    public static String removeBlankSpaces(String messageDecript) {
        String result = "";
        char[] array = messageDecript.toCharArray();

        for (int i = 0; i < array.length; i++) {
            if (array[i] != ' ') {
                result = result + array[i];
            }
        }
        return result;
    }

    //use the key for decript
    public static String useKeyDecript(String messageCripted, String key) {
        String[] arrayString = messageCripted.split(" ");
        String resultado = "";
        for (String s : arrayString) {
            resultado = resultado + HexStringConverter.xorHex(s, key);
        }
        return resultado;
    }

    //search for the most common byte
    public static String checkCommmonByte(String messageCripted) {
        String[] array = messageCripted.split(" ");
        String[] arrayAux = messageCripted.split(" ");
        int contador = 0;
        int countAux = 0;
        String MostC = "";
        for (String s : array) {
            for (String a : arrayAux) {
                if (s.equals(a)) {
                    countAux++;
                }
            }
            if (contador < countAux) {
                MostC = s;
                contador = countAux;
                countAux = 0;
            }
            countAux = 0;
        }

        return MostC;
    }

//verify the lenght of message
    public static boolean checkMessageLenght(String message) {
        //if exist spaces
        message = message.replace(" ", "");
        //get lenght
        char[] aux = message.substring(0, 4).toCharArray();
        String auxHex = "";
        for (int i = 0; i < aux.length; i++) {
            if (aux[i] != '0') {
                auxHex = auxHex + aux[i];
            }
        }

        int dec = HexStringConverter.hexTodecimal(auxHex);

        return (message.length() - 4) / 2 == dec;
    }

    //check fail and stop message
    public static boolean checkStopOrFailMessage(String message) throws InterruptedException {

        String aux = getPayLoad(message);

        if (aux.equals(stopMessage) || aux.equals(failMessage)) {
            System.out.println(aux + " === " + stopMessage + " " + failMessage);
            return false;
        }
        return true;
    }

    //format string : || || || || ||
    public static String formatString(char[] message) {
        int x = 0;
        String resultadoFinal = "";
        for (int i = 0; i < message.length; i++) {
            if (x == 2) {
                resultadoFinal = resultadoFinal + " ";
                resultadoFinal = resultadoFinal + message[i];
                x = 1;
            } else {
                resultadoFinal = resultadoFinal + message[i];
                x++;
            }
        }

        return resultadoFinal;
    }

    //get just PayLoad
    public static String getPayLoad(String message) {

        String payLoad;
        int aux = message.length() - 2;
        payLoad = message.substring(4, aux);

        return payLoad;
    }

    //get just Length
    public static String getLength(int payLoad) {
        String str = Integer.toHexString(payLoad);

        switch (str.length()) {
            case 1:
                str = "0" + str + "00";
                break;
            case 2:
                str = str + "00";
                break;
            case 3:
                str = str.substring(0, 2) + "0" + str.substring(2, 3);
                break;
            default:
                break;
        }
        return str;
    }
    

    public static String getLength2(int payLoad) {
        String str = Integer.toHexString(payLoad);

        switch (str.length()) {
            case 1:
                str = "0" + str + "00";
                break;
            case 2:
                str = str + "00";
                break;
            case 3:
                str = str.substring(1, 3) + "0" + str.substring(0, 1);
                break;
            default:
                break;
        }
        return str;
    }

    //count even
    public static int countEven(String s) {
        int e = 0;
        char[] resultado = s.toCharArray();
        for (int i = resultado.length-1; i > -1; i--) {
            if (resultado[i] == '1') {
                e++;
            }
            i--;
        }
        return e;
    }

    //count ODD
    public static int countOdd(String s) {
        int o = 0;
        char[] resultado = s.toCharArray();

        for (int i = resultado.length - 2; i > -1; i--) {
            if (resultado[i] == '1') {
                o++;
            }
            i--;
        }

        return o;
    }

    public static String insertEven(String s) {
        s = s + " 01";
        return s;
    }

    public static String insertOdd(String s) {
        s = s + " 02";
        return s;
    }

    public static String insertOddEven(String s) {
        s = s + " 03";
        return s;
    }

}
