import java.io.*;
import java.util.Scanner;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Main {
    static int totalLines = 0;
    static int totalWords = 0;
    private record Processor(File file) implements Runnable {
        @Override
        public void run() {
            String d = null;
            try {
                d = readFile(file);
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
            System.out.println("File processed: " + file.getName() + " On Thread: " + Thread.currentThread().getName());
            try {
                int numWords = countWords(file);
                int numLines = countLines(file);
                totalWords += numWords;
                totalLines += numLines;
                System.out.println("File: " + file.getName() + " Words: " + numWords + " Lines: " + numLines);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    static String readFile(File file) throws FileNotFoundException {
        Scanner reader = new Scanner(file);
        StringBuilder content = new StringBuilder();
        while (reader.hasNextLine()) {
            content.append(reader.nextLine());
        }
        return content.toString();
    }

    static int countWords(File file) throws FileNotFoundException {
        Scanner myReader = new Scanner(file);
        String data = "";
        while (myReader.hasNextLine()) {
            data += myReader.nextLine();
            data += "\n";
        }
        myReader.close();
        if (data.trim().isEmpty()) {
            return 0;
        }
        return data.trim().split("\\s+").length;

    }

    static int countLines(File file) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(file));
        int lines = 0;
        while (reader.readLine() != null) lines++;
        reader.close();
        return lines;
    }

    public static void main(String[] args) throws FileNotFoundException, IOException, InterruptedException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter the path to the folder: ");
        String path = scanner.nextLine();
        System.out.println("Displaying the number of lines and words for each text file in the folder: " + path);
        File folder = new File(path);
        File[] listOfFiles = folder.listFiles();
        assert listOfFiles != null;
        ExecutorService executor = Executors.newFixedThreadPool(4);
        for (File file : listOfFiles) {
            if (file.isFile() && file.getName().endsWith(".txt")) {
                executor.execute(new Processor(file));
            }
        }
        executor.shutdown();
        if (executor.awaitTermination(1, TimeUnit.MINUTES)) {
            System.out.println("Total lines: " + totalLines);
            System.out.println("Total words: " + totalWords);
        } else {
            System.out.println("Program timed out");
        }
    }
}
