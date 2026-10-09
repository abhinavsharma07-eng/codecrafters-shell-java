import java.io.File;
import java.io.IOException;
import java.util.*;

public class Main {

    static String path = System.getenv("PATH");
    static File currentDirectory = new File(System.getProperty("user.dir"));
    static final Set<String> BUILTINS = Set.of("echo", "exit", "type", "pwd", "cd");

    public static List<String> parseArgs(String argument) {
        List<String> arguments = new ArrayList<>();
        StringBuilder current = new StringBuilder();

        boolean isSingleQuote = false;
        boolean tokenStarted = false;

        for (int i = 0; i < argument.length(); i++) {
            char c = argument.charAt(i);
            if (c == '\'') {
                isSingleQuote = !isSingleQuote;
                tokenStarted = true;
            } else if (Character.isWhitespace(c) && !isSingleQuote) {
                if (tokenStarted) {
                    arguments.add(current.toString());
                    current.setLength(0);
                    tokenStarted = false;
                }
            } else {
                current.append(c);
                tokenStarted = true;
            }
        }
        if (tokenStarted) {
            arguments.add(current.toString());
        }
        return arguments;
    }

    public static void main(String[] args) throws Exception {

        Scanner scanner = new Scanner(System.in);

        while (true) {

            System.out.print("$ ");
            String input = scanner.nextLine();
            List<String> parts = parseArgs(input);
            if (parts.isEmpty()) {
                continue;
            }
            String command = parts.getFirst();

            if (command.equals("exit")) {
                break;

            } else if (command.equals("echo")) {
                System.out.println(String.join(" ", parts.subList(1, parts.size())));

            } else if (command.equals("type")) {

                if (parts.size() > 1) {
                    String argument = parts.get(1);
                    System.out.println(Main.type(argument));
                }
            } else if (command.equals("pwd")) {
                File currentPath = new File(".");
                System.out.println(currentDirectory.getCanonicalPath());

            } else if (command.equals("cd")) {
                String argument = parts.size() > 1 ? parts.get(1) : "";
                String result = Main.cd(argument);
                if (result != null) {
                    System.out.println(command + result);
                }
            } else {

                String executable = getPath(command);
                if (executable != null) {
                    ProcessBuilder pb = new ProcessBuilder(parts);
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

        if (BUILTINS.contains(argument)) {
            return argument + " is a shell builtin";
        } else {
            String filePath = getPath(argument);
            return (filePath != null ? argument + " is " + filePath : argument + ": not found");
        }
    }

    public static String cd(String argument) throws IOException {

        if (argument.equals("~") || argument.isEmpty()) {
            currentDirectory = new File(System.getenv("HOME")).getCanonicalFile();
            return null;
        }
        File newDirectory;
        if (new File(argument).isAbsolute()) {
            newDirectory = new File(argument);
        } else {
            newDirectory = new File(currentDirectory, argument);
        }
        if (newDirectory.isDirectory()) {
            currentDirectory = newDirectory.getCanonicalFile();
            return null;
        } else {
            return ": " + argument + ": No such file or directory";
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
