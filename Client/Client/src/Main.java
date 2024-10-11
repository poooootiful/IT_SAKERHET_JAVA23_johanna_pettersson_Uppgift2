import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        System.out.println("Client");
        StartMenu();

    }

    public static void StartMenu () {
        System.out.println("1.Register");
        System.out.println("2.Log In");
        Scanner scanner = new Scanner(System.in);
        String input = scanner.nextLine();
        if (input.matches("1")) {
            RegisterMenu();
        }else if (input.matches("2")) {
            LoginMenu();
        }else {
            System.out.println("wrong input program closing");
        }
    }

    public static void RegisterMenu () {
        System.out.println("Email:");
        Scanner email = new Scanner(System.in);
        String e = email.nextLine();
        System.out.println("Password");
        Scanner password = new Scanner(System.in);
        String pass = password.nextLine();

        String hashpass = Security.hashing(pass);

        SqlFunctions.Register(e,hashpass);
        LoginMenu();
    }

    public static void LoginMenu() {
        System.out.println("LoginMenu");
        System.out.println("Email");
        Scanner email = new Scanner(System.in);
        String e = email.nextLine();
        System.out.println("Password");
        Scanner password = new Scanner(System.in);
        String pass = password.nextLine();
        int id = SqlFunctions.Login(e,pass);

        if (id > 0) {
            SqlFunctions.setId(id);
            Menu();
        } else {
            System.out.println("Login failed try again");
            LoginMenu();
        }
    }

    public static void Menu (){
        System.out.println("1.Make new Capsule");
        System.out.println("2.Show Capsules");
        System.out.println("3.LogOut");
        Scanner scanner = new Scanner(System.in);
        String input = scanner.nextLine();
        if (input.matches("1")) {
            NewCapsule();
        } else if (input.matches("2")) {
            ShowCapsules();
        } else if (input.matches("3")) {
            Logout();
        } else {
            System.out.println("Wrong input restarting menu");
            Menu();
        }
    }

    public static void NewCapsule() {
        try {

            System.out.println("Enter the message you want to save: ");
            Scanner scanner = new Scanner(System.in);
            String message = scanner.nextLine();

            SecretKey aesKey = Security.generateAESKey();

            String encryptedMessage = Security.encrypt(message, aesKey);

            String encodedKey = Base64.getEncoder().encodeToString(aesKey.getEncoded());

            int userid = SqlFunctions.getId();
            SqlFunctions.SaveNewCapsule(userid,encodedKey, encryptedMessage);

            System.out.println("Capsule saved successfully.");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Failed to save capsule.");
        }
        Menu();
    }

    public static void ShowCapsules () {
        int id = SqlFunctions.getId();
        SqlFunctions.ShowAllCapsules(id);
        Menu();
    }

    public static void Logout() {
        SqlFunctions.setId(0);
        StartMenu();
    }
}