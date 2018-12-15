import java.io.*;
import java.util.Scanner;

public class Main {

  private static int count = 0;
  public static void main(String args[]) {
    final Scanner input = new Scanner(System.in);

    System.out.println("File path example: /home/geronsales/Downloads/programs/adsd/videos/test/");

    System.out.print("Enter the file path: ");
    final String directory = input.nextLine();
    System.out.print("");

    File folder = new File(directory);
    File[] listOfFiles = folder.listFiles();
    listOfFiles = listOfFiles == null ? new File[0] : listOfFiles;

    for (final File file : listOfFiles) {
      if (file.isFile()) {
        convertVideo(directory, file.getName());
      }
    }

    input.close();
    System.exit(0);


  }

  private static void convertVideo(final String directory, final String fileName) {
    final String[] tools = {"callgrind", "massif"};
    final String[] scales = {"1280:720", "1920:1080"};
    final String[] outputFormat = {"MOV", "avi", "webm", "3gp"};

    for (final String tool : tools) {
      for (final String scale : scales) {
        for (final String format : outputFormat) {
          System.out.println("Converting file " + directory + fileName + " to " + format + " format, " + scale + " scale using " + tool + " tool.");
          executeCommand(directory, fileName, tool, format, scale);
        }
      }
    }
  }

  private static void executeCommand(final String movieDirectory, final String movieInputName, final String tool, final String outputFormat, final String scale) {

    String s = null;

    try {
      final String outputDirectory = movieDirectory + movieInputName.replace(".", "_") + "_TO_" + scale.replace(":", "x") + "_" + outputFormat + "_" + tool + "_" + count + "st_conversion/";
      final String movieOutputName = movieInputName.replace(".","_") + "." + outputFormat;

      final String toolOutFile = outputDirectory + tool + ".out.%p";
      final String execOutFile = "exec.out";

      String inputPath = movieDirectory + movieInputName;
      String outputPath = outputDirectory + movieOutputName;

      writeTextOnFile(outputDirectory, execOutFile, "------------BEGIN------------");

      final String resolutionConversion = "ffmpeg -i " + inputPath + " -strict -2 -vf scale=" + scale + " " + outputPath;
      System.out.println("resolutionConversion: " + resolutionConversion );

      final String conversionAnalysis = "valgrind --tool=" + tool + " --" + tool + "-out-file=" + toolOutFile + " " + resolutionConversion;
      System.out.println("conversionAnalysis: " + conversionAnalysis);

      Process p = Runtime.getRuntime().exec(conversionAnalysis);


      BufferedReader stdInput = new BufferedReader(new
          InputStreamReader(p.getInputStream()));

      BufferedReader stdError = new BufferedReader(new
          InputStreamReader(p.getErrorStream()));

      while ((s = stdInput.readLine()) != null) {
        writeTextOnFile(outputDirectory, execOutFile, s);
      }

      while ((s = stdError.readLine()) != null) {
        writeTextOnFile(outputDirectory, execOutFile, s);
      }

      writeTextOnFile(outputDirectory, execOutFile, "------------END------------");

    } catch (IOException e) {
      System.out.println("exception happened - here's what I know: ");
      e.printStackTrace();
      System.exit(-1);
    }
    count++;

  }

  private static void writeTextOnFile(String path, String title, String text) throws IOException {
    File dir = new File(path);
    if (!(dir.exists())) {
      dir.mkdirs();
    }

    File file = new File(path + title);
    FileWriter fileWriter = new FileWriter(file, true);
    BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

    fileWriter.write(text + "\n");

    bufferedWriter.close();

  }

}