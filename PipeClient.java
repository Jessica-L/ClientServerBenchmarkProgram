/*
 * Project: Client Server Performance Measurement Program
 * Authors: Jessica Lynch and Andrew Arnopoulos
 * Date:    27-Apr-2015
 */

import java.io.*;
import java.util.UUID;

public class PipeClient
{
    public static void main(String[] args) throws Exception {
        UUID id = UUID.randomUUID();
        String returnFilePath = "/tmp/"+id;
        if (!new File(returnFilePath).exists())
        {
            PrintWriter writer = new PrintWriter(returnFilePath, "UTF-8");
            writer.close();
        }

        PrintWriter serverWriter = new PrintWriter(new BufferedOutputStream(new FileOutputStream("/tmp/input",true)));
        serverWriter.println(id+":Hello Andrew");
        serverWriter.flush();
        serverWriter.close();

        BufferedReader reader = new BufferedReader(new FileReader(returnFilePath));
        while (true)
        {
            String line = reader.readLine();
            if (line != null) {
                System.out.println(line);
                break;
            }

        }
    }
}