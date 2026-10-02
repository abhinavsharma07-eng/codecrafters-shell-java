import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws Exception {

        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.print("$ ");

            String input = scanner.nextLine();
            if (input.equals("exit")) {
                break;
            } else if (input.startsWith("echo ")){
                System.out.println(input.substring(5));
            } else if(input.startsWith("type ")){
                String type = input.substring(5);
                if(type.trim().equals("echo")||type.trim().equals("exit")||type.trim().equals("type")){
                    System.out.println(type.trim()+" is a shell builtin");
                }
                else {
                    System.out.println(type.trim()+": not found");
                }
            } else {
                System.out.println(input + ": command not found");
            }
        }

    }
}
