package MyFileWriter;

import java.io.FileWriter;
import java.io.IOException;

public class MyFileWriter {


    public static void createAndWriteToFile(String filePath, String contents) {
        try {
            FileWriter writer = new FileWriter(filePath);
            writer.write(contents);
            writer.close();
            System.out.println("File created and written successfully at " + filePath);
        } catch (Exception e) {
            System.out.println("An error occurred: " + e.getMessage());
        }
    }

}
