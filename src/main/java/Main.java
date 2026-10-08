
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

public class Main {

    static String path = System.getenv("PATH");
    static File currentDirectory = new File(System.getProperty("user.dir"));
    static final Set<String> BUILTINS = Set.of(
            "echo",
            "exit",
            "type",
            "pwd",
            "cd"
    );

    public static void main(String[] args) throws Exception {

        Scanner scanner = new Scanner(System.in);

        while (true) {

            System.out.print("$ ");
            String input = scanner.nextLine();
            String command = input.split(" ")[0];

            if (command.equals("exit")) {
                break;

            } else if (command.equals("echo")) {
                System.out.println(input.substring(5));

            } else if (command.equals("type")) {

                String argument = input.substring(5).trim();
                System.out.println(argument + Main.type(argument));

            } else if(command.equals("pwd")) {
                File currentPath = new File(".");
                System.out.println(currentDirectory.getCanonicalPath());

            } else if(command.equals("cd")) {
                String argument = Main.cd(input.substring(2).trim());
                if(argument != null){
                    System.out.println(command + argument);
                }
            } else {
                String[] parts = input.split(" ");
                String executable = getPath(parts[0]);
                if (executable != null) {
                    ArrayList<String> argument = new ArrayList<>();
                    argument.add(parts[0]);
                    for (int i = 1; i < parts.length; i++) {
                        argument.add(parts[i]);
                    }
                    ProcessBuilder pb = new ProcessBuilder(argument);
                    pb.directory(currentDirectory);
                    pb.inheritIO();

                    Process process = pb.start();
                    process.waitFor();
                } else {
                    System.out.println(input + ": command not found");
                }
            }
        }
    }

    public static String type(String argument) {

        if (BUILTINS.contains(argument)){
            return " is a shell builtin";
        } else {
            String filePath = getPath(argument);
            return (filePath != null ? " is " + filePath : ": not found");
        }
    }

    public static String cd(String argument) throws IOException {
        if(argument.equals("~")||argument.isEmpty()) {
            currentDirectory =  new File(System.getenv("HOME")).getCanonicalFile();
            return null;
        }
        File newDirectory;
        if(new File(argument).isAbsolute()){
            newDirectory = new File(argument);
        }
        else{
            newDirectory = new File(currentDirectory, argument);
        }
        if (newDirectory.isDirectory()) {
            currentDirectory = newDirectory.getCanonicalFile();
                return null;
        }
        else{
           return ": "+argument+": No such file or directory";
        }
    }

    public static String getPath(String argument) {

        String[] directories = path.split(File.pathSeparator);
        for (String dir : directories) {
            File file = new File(dir, argument);
            if (file.isFile() && file.canExecute()) {
                return file.getAbsolutePath();
            }
        }
        return null;
    }
}
