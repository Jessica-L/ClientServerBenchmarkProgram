import java.io.*;

public class PipeServer {

	public static void main(String[] args) throws Exception {

		if (args.length < 1) {
			System.out.println("Usage: PipeServer <numberOfThreads>");
			return;
		}

		int numberOfThreads = Integer.parseInt(args[0]);

		String inputFilePath = "/tmp/input";
		if (!new File(inputFilePath).exists()) {
			PrintWriter writer = new PrintWriter(inputFilePath, "UTF-8");
			writer.close();
		}

		for (int i = 0; i < numberOfThreads; i++) {
			new MessageFetcher();
		}

		BufferedReader reader = new BufferedReader(new FileReader(inputFilePath));
		while (true) {
			String line = reader.readLine();
			if (line != null) {
				new MessageReceiver(line);
			}
		}
	}

}
