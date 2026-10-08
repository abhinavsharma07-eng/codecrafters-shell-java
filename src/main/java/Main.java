
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Scanner;

public class Main {

    static String path = System.getenv("PATH");

    public static void main(String[] args) throws Exception {

        Scanner scanner = new Scanner(System.in);

        while (true) {

            System.out.print("$ ");
            String input = scanner.nextLine();

            if (input.startsWith("exit ")) {
                break;

            } else if (input.startsWith("echo ")) {
                System.out.println(input.substring(5));

            } else if (input.startsWith("type ")) {

                String command = input.substring(5).trim();
                System.out.println(command + Main.type(command));

            } else if(input.equals("pwd")) {
                File currentPath = new File(".");
                System.out.println(currentPath.getCanonicalPath());

            } else {
                String[] parts = input.split(" ");
                String executable = getPath(parts[0]);
                if (executable != null) {
                    ArrayList<String> command = new ArrayList<>();
                    command.add(parts[0]);
                    for (int i = 1; i < parts.length; i++) {
                        command.add(parts[i]);
                    }
                    Process process = new ProcessBuilder(command)
                            .inheritIO()
                            .start();
                    process.waitFor();
                } else {
                    System.out.println(input + ": command not found");
                }
            }
        }
    }

    private static String type(String command) {

        if (command.equals("echo") || command.equals("exit") || command.equals("type")|| command.equals("pwd")) {
            return " is a shell builtin";
        } else {
            String filePath = getPath(command);
            return (filePath != null ? " is " + filePath : ": not found");
        }
    }

    public static String getPath(String command) {

        String[] directories = path.split(File.pathSeparator);
        for (String dir : directories) {
            File file = new File(dir, command);
            if (file.isFile() && file.canExecute()) {
                return file.getAbsolutePath();
            }
        }
        return null;
    }
}
