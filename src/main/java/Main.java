import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws Exception {

        System.out.print("$ ");

        Scanner scanner = new Scanner(System.in);
        String input = scanner.nextLine();
        System.out.print(input + ": command not found");
    }
}
