import org.apache.commons.io.FilenameUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Iban {

    public static Map<String, Integer> parseJson() {

        Map<String, Integer> ibanList = new HashMap<>();
        jsonToMap(ibanList);

        return ibanList;

    }

    public static void jsonToMap(Map<String, Integer> map) {
        JSONParser parser = new JSONParser();
        try {
            Object obj = parser.parse(new FileReader("iban.json"));
            JSONObject element = (JSONObject) obj;
            JSONArray jsonArray = (JSONArray) element.get("values");
            for (Object ms : jsonArray) {
                element = (JSONObject) ms;
                String country = (String) element.get("code");
                int length = Integer.parseInt((String) element.get("length"));
                map.put(country, length);
            }
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
    }

    public static boolean checkIban(String iban) {

        Map<String, Integer> ibanCountryList = parseJson();

        iban = iban.trim().replaceAll("\\s+", "");

        if (!ibanCountryList.containsKey(iban.substring(0, 2)) || iban.length() != ibanCountryList.get(iban.substring(0, 2))) {

            return false;

        }

        String reformatedIban = iban.substring(4) + iban.substring(0, 4);
        char[] charIban = reformatedIban.toCharArray();
        StringBuilder stringBuilder = new StringBuilder();

        for (char i : charIban) {
            if (i >= 65 && i <= 91) {
                stringBuilder.append(i - 55);
            } else if (i >= 48 && i <= 57) {
                stringBuilder.append(i);
            }
        }

        BigInteger bigInteger = new BigInteger(stringBuilder.toString());
        BigInteger mod = new BigInteger("97");
        if (bigInteger.mod(mod).toString().equals("1")) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean ckeckIbanFromFile(String input) {

        if (!input.endsWith(".txt")) {

            input += ".txt";

        }

        File file = new File(input);
        if (file.exists() && !file.isDirectory() && FilenameUtils.getExtension(file.toString()).equals("txt")) {

            BufferedWriter writer = null;
            try {
                writer = new BufferedWriter(new FileWriter("output.txt"));
            } catch (IOException e) {
                e.printStackTrace();
            }

            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = br.readLine()) != null) {


                    writer.append(line + ";" + checkIban(line) + "\n");

                }
                System.out.println("Your file is generated. Please check for a file output.txt");
                writer.close();

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {

            System.out.println("File does not exist or it's a directory");

        }

        return true;
    }

    public static void main(String[] args) {

        Scanner scan = new Scanner(System.in);
        System.out.println("Do you want to enter iban here or read iban from file? 'C' goes for console, 'F' goes for file");
        String input = scan.nextLine();

        if (!input.equals("C") && !input.equals("F")) {

            System.out.println("Incorrect answer, please enter again");

        }

        if (input.equals("C")) {
            System.out.println("Please enter iban into console");
            String iban = scan.nextLine();
            System.out.println(iban+";"+checkIban(iban));

        }
        if (input.equals("F")) {
            System.out.println("Please enter file path into console");
            String file = scan.nextLine();

            ckeckIbanFromFile(file);

        }


    }

}
