import java.io.*;

public class PipeServer {

	public static void main(String[] args) throws Exception {
		String inputFilePath = "/tmp/input";
		if (!new File(inputFilePath).exists()) {
			PrintWriter writer = new PrintWriter(inputFilePath, "UTF-8");
			writer.close();
		}

		for (int i = 0; i < 5; i++) {
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