import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.*;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class PassVault {
    // ========================================
    // Globals
    // ========================================

    private int passAttempts;
    private Scanner in = new Scanner(System.in);
    private boolean authorized = false;

    // ========================================
    // Constructor
    // ========================================

    public PassVault(int passAttempts) {
        this.passAttempts = passAttempts;
    }

    public PassVault() {
        // Defaults
        this.passAttempts = 5;
    }

    // ========================================
    // Authorization
    // ========================================

    public boolean login() {
        System.out.println("=====[ Login ]=====");
        return authorize();
    }

    public void logout() {
        authorized = false;
    }

    public boolean signUp() {
        System.out.println("Looks like this is your first time!");
        try {
            EncryptedPassword.createPassFile();
        } catch (IOException e) {
            System.out.println(); // Todo: error
        }
        return createMasterPass();
    }

    private boolean createMasterPass() {
        System.out.println("Please set the master password");

        char[] master = getSecureInput();
        Master.setMasterPassword(master);

        // Clear
        CharArrayUtils.clear(master);
        authorized = true;
        return true;
    }

    public boolean isAuthorized() { return authorized; }

    public boolean authorize() {
        logout();

        int currentAttempts = passAttempts;
        while (currentAttempts > 0) {
            try {
                char[] master = Master.getMasterPassword();

                System.out.println("Please type the current master password");
                System.out.println();

                char[] attempt = getSecureInput();
                char[] hashedAttempt = Master.hash(attempt);

                if (Arrays.equals(hashedAttempt, master)) {
                    // Clear
                    CharArrayUtils.clear(attempt);
                    CharArrayUtils.clear(hashedAttempt);
                    CharArrayUtils.clear(master);

                    authorized = true;
                    return true;
                } else {
                    // Clear
                    CharArrayUtils.clear(attempt);
                    CharArrayUtils.clear(hashedAttempt);
                    CharArrayUtils.clear(master);
                    currentAttempts--;
                    System.out.println(
                            "Unfortunately that password is incorrect, you have "
                                    + currentAttempts
                                    + " attempt"
                                    + ((currentAttempts == 1) ? "" : "s")
                                    + " left");
                }
            } catch (FileNotFoundException e) {
                return signUp();
            } catch (NoSuchAlgorithmException | IOException e) {
                System.out.println("ERROR CODE: "); // Todo Update Error
            }
        }
        System.out.println("You have reached your maximum allowed attempts, the program will now exit");
        System.exit(-1);
        return false;
    }

    // ========================================
    // Supporting Functions
    // ========================================

    private char[] getSecureInput() {

        Scanner input = new Scanner(System.in);

        Console cons;
        char[] line;

        if ((cons = System.console()) != null) {
            line = cons.readPassword("[%s]", "Input:");
        } else {
            System.out.println(
                    "*WARNING* Your IDE does not support System.console(), using unsafe password read");
            System.out.println("Input:");
            line = input.nextLine().replaceAll("\\s+", "").toCharArray();
        }

        return line;
    }

    private String getUnsecuredInput() {
        Scanner input = new Scanner(System.in);
        return input.nextLine().replaceAll("\\s+", "");
    }

    // ========================================
    // Options
    // ========================================

    public void addPass() {
        System.out.println("=====[ Add Password ]=====");
        if (authorize()) {
            boolean complete = false;

            while (!complete) {
                try {
                    System.out.print("Enter id\n");
                    char[] id = getSecureInput();

                    List<char[]> listOfIds = EncryptedPassword.getListOfIds();

                    if (!listOfIds.contains(id)) {
                        System.out.print("Enter user\n");
                        char[] user = getSecureInput();

                        System.out.println("Would you like to generate a password? [Y/n]:\n");
                        String response = getUnsecuredInput();

                        char[] password = {};

                        if (response.toUpperCase().equals("Y") || response.toUpperCase().equals("YES")) {
                            password = PasswordGenerator.generatePassword().toCharArray();
                        } else {
                            password = getSecureInput();
                        }

                        EncryptedPassword.addPassword(id, user, password);

                        // Clear
                        CharArrayUtils.clear(id);
                        CharArrayUtils.clear(user);
                        CharArrayUtils.clear(password);

                        complete = true;
                    } else {
                        System.out.println("The id you entered already exists");
                    }
                } catch (IOException e) {
                    System.out.println(); // Todo update error
                } catch (InvalidKeySpecException e) {
                    e.printStackTrace();
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                } catch (BadPaddingException e) {
                    e.printStackTrace();
                } catch (InvalidKeyException e) {
                    e.printStackTrace();
                } catch (InvalidAlgorithmParameterException e) {
                    e.printStackTrace();
                } catch (NoSuchPaddingException e) {
                    e.printStackTrace();
                } catch (IllegalBlockSizeException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void listIds() {
        System.out.println("=====[ List Ids ]=====");
        try {
            for (char[] id : EncryptedPassword.getListOfIds()) {
                System.out.println(id);
                CharArrayUtils.clear(id);
            }
        } catch (IOException e) {
            System.out.println(); // Todo update error
        }
    }

    public void findPass() {
        System.out.println("=====[ Find Password ]=====");
        if (authorize()) {
            boolean complete = false;

            while (!complete) {
                try {
                    System.out.println("Enter id of password\n");
                    char[] id = getSecureInput();

                    List<char[]> listOfIds = EncryptedPassword.getListOfIds();

                    if (CharArrayUtils.listContains(listOfIds, id)) {
                        char[] foundPassword = EncryptedPassword.getCipherText(id);

                        System.out.print("id = ");
                        System.out.println(id);

                        char[] decryptedCipherText = Decrypt.decrypt(id, foundPassword);
                        CharArrayUtils.clear(id);

                        List<char[]> spiltList = CharArrayUtils.spilt(decryptedCipherText);
                        CharArrayUtils.clear(decryptedCipherText);

                        System.out.print("user = ");
                        System.out.println(spiltList.get(0));

                        System.out.print("password = ");
                        System.out.print(spiltList.get(1));

                        CharArrayUtils.clearList(spiltList);

                        complete = true;
                    } else {
                        System.err.println("There are no passwords with that id"); // id not found.
                    }
                } catch (IOException e) {
                    System.out.println(e); // Todo update error
                } catch (Exception e) {
                    System.out.println(e); // Todo update error
                }
            }
        }
    }

    public void sharePass() {
        System.out.println("=====[ Add Password ]=====");

        if (authorize()) {
            boolean complete = false;

            while (!complete) {
                try {
                    System.out.println("Enter id of password: ");
                    char[] id = getSecureInput();

                    if (EncryptedPassword.getListOfIds().contains(id) && CheckCert.checkFriendCert()) {

                        System.out.println("Enter file name: ");
                        char[] fileName = getUnsecuredInput().toCharArray();

                        try {
                            FileWriter writer = new FileWriter(Arrays.toString(fileName), true);
                            BufferedWriter bufferedWriter = new BufferedWriter(writer);

                            // Write id to file
                            bufferedWriter.write("id=");
                            bufferedWriter.write(id);
                            bufferedWriter.newLine();

                            // Decrypt username and password
                            char[] cipherText = EncryptedPassword.getCipherText(id);
                            char[] decryptedCipherText = Decrypt.decrypt(id, cipherText);

                            List<char[]> spiltList = CharArrayUtils.spilt(decryptedCipherText);
                            CharArrayUtils.clear(decryptedCipherText);

                            // Clear id
                            CharArrayUtils.clear(id);

                            bufferedWriter.write("user=");
                            bufferedWriter.write(spiltList.get(0)); // username
                            bufferedWriter.newLine();
                            bufferedWriter.write("password=");
                            bufferedWriter.write(spiltList.get(1)); // password
                            bufferedWriter.newLine();
                            bufferedWriter.close();

                            CharArrayUtils.clearList(spiltList); // Clear username and password

                        } catch (IOException e) {
                            System.err.println("Error creating shared password file.");
                            e.printStackTrace();
                        }
                        complete = true;
                    } else {
                        System.err.println("Error #00012"); // id not found.
                    }
                } catch (IOException e) {
                    System.out.println(); // Todo update error
                } catch (Exception e) {
                    System.out.println(); // Todo update error
                }
            }
        }
    }

    public void changeMasterPass() {
        System.out.println("=====[ Change Master Password ]=====");
        if (authorize()) {
            createMasterPass();
        }
    }
}
