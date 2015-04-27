import java.io.*;
import java.util.UUID;

public class PipeClient {

	public static void main(String[] args) throws Exception {
		if (args.length < 1) {
			System.out.println("Usage: PipeClient <numberOfMessages>");
			return;
		}
		double numberOfMessages = Double.parseDouble(args[0]);

		double sumTime = 0;

		for (int i = 0; i < numberOfMessages; i++) {
			UUID id = UUID.randomUUID();
			String returnFilePath = "/tmp/"+id;
			if (!new File(returnFilePath).exists()) {
				PrintWriter writer = new PrintWriter(returnFilePath, "UTF-8");
				writer.close();
			}

			double startTime = (double)System.currentTimeMillis();
			PrintWriter serverWriter = new PrintWriter(new BufferedOutputStream(new FileOutputStream("/tmp/input",true)));
			serverWriter.println(id+":Hello Andrew");
			serverWriter.flush();
			serverWriter.close();
			double endTime = (double)System.currentTimeMillis();

			sumTime += endTime - startTime;
			
			BufferedReader reader = new BufferedReader(new FileReader(returnFilePath));
			while (true) {

				String line = reader.readLine();
				if (line != null) {
					break;
				}

			}
		}
		double averageTime = sumTime / numberOfMessages;
		System.out.println(averageTime);
		
	}

}