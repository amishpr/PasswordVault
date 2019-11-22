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

    PassVault(int passAttempts) {
        this.passAttempts = passAttempts;
    }

    public PassVault() {
        // Defaults
        this.passAttempts = 5;
    }

    // ========================================
    // Authorization
    // ========================================

    boolean login() throws Exception {
        System.out.println("=====[ Login ]=====");
        return authorize();
    }

    private void logout() {
        authorized = false;
    }

    private boolean signUp() throws Exception {
        System.out.println("Looks like this is your first time!");
        try {
            EncryptedPassword.createPassFile();
        } catch (IOException e) {
            System.out.println("Error Code: #00001");
        }
        return createMasterPass(false);
    }

    private boolean createMasterPass(boolean rewritePassword) throws Exception {
        System.out.println("Please set the master password");

        char[] master = getSecureInput();

        if (rewritePassword) {
            EncryptedPassword.rewritePasswords(master);
        } else {
            Master.setMasterPassword(master);
        }


        // Clear
        CharArrayUtils.clear(master);
        authorized = true;
        return true;
    }

    public boolean isAuthorized() { return authorized; }

    private boolean authorize() throws Exception {
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
                System.out.println("Error Code: #00004");
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
            line = cons.readPassword("[%s]", "Secure Input:");
        } else {
//            System.err.println("*WARNING* Your IDE does not support System.console()"
//                + ", using unsafe password read");
//            System.out.println("");
            System.out.print("Unsecure Input: ");
//            line = input.nextLine().replaceAll("\\s+", "").toCharArray();
            line = input.nextLine().toCharArray();
        }

        return line;
    }

    private String getUnsecuredInput() {
        Scanner input = new Scanner(System.in);
        return input.nextLine();
    }

    // ========================================
    // Options
    // ========================================

    void addPass() throws Exception {
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

                        System.out.print("Would you like to generate a password? [Y/n]: ");
                        String response = getUnsecuredInput();

                        char[] password = {};

                        if (response.toUpperCase().equals("Y") || response.toUpperCase().equals("YES")) {
                            password = PasswordGenerator.generatePassword().toCharArray();
                        } else {
                            System.out.println("Enter password: ");
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
                } catch (IOException | InvalidKeySpecException | NoSuchAlgorithmException | BadPaddingException | InvalidKeyException | InvalidAlgorithmParameterException | NoSuchPaddingException | IllegalBlockSizeException e) {
                    System.out.println("Error Code: #00003");
                }
            }
        }
    }

    void listIds() {
        System.out.println("=====[ List Ids ]=====");
        try {
            List<char[]> charList = EncryptedPassword.getListOfIds();
            for (char[] id : charList) {
                System.out.println(id);
//                CharArrayUtils.clear(id);
            }
        } catch (IOException e) {
            System.out.println("Error Code: #00002");
        }
    }

    void findPass() throws Exception {
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

                        char[] decryptedCipherText = EncryptDecrypt.decrypt(id, foundPassword);

                        List<char[]> spiltList = CharArrayUtils.spilt(decryptedCipherText);

                        System.out.print("user = ");
                        assert spiltList != null;
                        System.out.println(spiltList.get(0));

                        System.out.print("password = ");
                        System.out.println(spiltList.get(1));

                        // Clear
                        CharArrayUtils.clear(decryptedCipherText);
                        CharArrayUtils.clear(id);
                        CharArrayUtils.clearList(spiltList);

                        complete = true;
                    } else {
                        System.err.println("There are no passwords with that id"); // id not found.
                    }
                } catch (Exception e) {
                    System.out.println("Error Code: #00002");
                }
            }
        }
    }

    void sharePass() throws Exception {
        System.out.println("=====[ Add Password ]=====");

        if (authorize()) {
            boolean complete = false;

            while (!complete) {
                try {
                    System.out.println("Enter id of password: ");
                    char[] id = getSecureInput();

                    if (CharArrayUtils.listContains(EncryptedPassword.getListOfIds(), id) && CheckCert.checkFriendCert()) {

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
                            char[] decryptedCipherText = EncryptDecrypt.decrypt(id, cipherText);

                            List<char[]> spiltList = CharArrayUtils.spilt(decryptedCipherText);

                            bufferedWriter.write("user=");
                            assert spiltList != null;
                            bufferedWriter.write(spiltList.get(0)); // username
                            bufferedWriter.newLine();
                            bufferedWriter.write("password=");
                            bufferedWriter.write(spiltList.get(1)); // password
                            bufferedWriter.newLine();
                            bufferedWriter.close();

                            // Clear
                            CharArrayUtils.clear(id);
                            CharArrayUtils.clear(decryptedCipherText);
                            CharArrayUtils.clearList(spiltList); // Clear username and password

                        } catch (IOException e) {
                            System.out.println("Error Code: #00001");
                        }
                        complete = true;
                    } else {
                        System.err.println("Id not found."); // id not found.
                    }
                } catch (Exception e) {
                    System.out.println("Error Code: #00005");
                }
            }
        }
    }

    void changeMasterPass() throws Exception {
        System.out.println("=====[ Change Master Password ]=====");
        if (authorize()) {
            createMasterPass(true);
        }
    }
}
