import java.io.*;

public class Preprocessor {
    public static void main(String [] args) {

        String fileName = "logs.csv"; // The name of the file to open.

        try {
            byte[] buffer = new byte[1000];

            FileInputStream inputStream = 
                new FileInputStream(fileName);

            int total = 0;
            int nRead = 0;
            while((nRead = inputStream.read(buffer)) != -1) {
                System.out.println(new String(buffer));
                total += nRead;
            }   

            inputStream.close();        

            System.out.println("Read " + total + " bytes");
        }
        catch(FileNotFoundException ex) {
            System.out.println(
                "Unable to open file '" + 
                fileName + "'");                
        }
        catch(IOException ex) {
            System.out.println(
                "Error reading file '" 
                + fileName + "'");      
        }
    }
}