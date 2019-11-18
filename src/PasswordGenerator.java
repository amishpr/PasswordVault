/*
 *   Christian Overton (cto5068@psu.edu) & Amish Prajapati (avp5564@psu.edu)
 *   Assignment 1
 *   CMPSC 444
 *   09/12/19
 */

import java.security.SecureRandom;

public class PasswordGenerator {
  public static String generatePassword() {
    int passLength = 12;

    int minLowercase = 1;
    int minUppercase = 1;
    int minNumber = 1;
    int minSymbol = 1;

    int minLength = minUppercase + minLowercase + minNumber + minSymbol;
    if (minLength > passLength) {
      passLength = minLength;
    }

    String lowercaseAlphabet = "abcdefghijklmnopqrstuvwxyz";
    String uppercaseAlphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    String numbersAlphabet = "0123456789";
    String symbolsAlphabet = "!@#$%^&*_+-";

    String password =
        ""
            + addCharToPassword(minLowercase, lowercaseAlphabet)
            + addCharToPassword(minUppercase, uppercaseAlphabet)
            + addCharToPassword(minNumber, numbersAlphabet)
            + addCharToPassword(minSymbol, symbolsAlphabet);

    while (password.length() < passLength) {
      String[] alphabets = {lowercaseAlphabet, uppercaseAlphabet, numbersAlphabet, symbolsAlphabet};

      SecureRandom random = new SecureRandom();
      int index = random.nextInt(alphabets.length);

      password = password + addCharToPassword(1, alphabets[index]);
    }

    return shuffleString(password);
  }

  private static String addCharToPassword(int amount, String alphabet) {
    String s = "";

    for (int i = 0; i < amount; i++) {
      s = s + selectRandomChar(alphabet);
    }

    return s;
  }

  private static char selectRandomChar(String s) {

    SecureRandom random = new SecureRandom();
    int index = random.nextInt(s.length());
    return s.charAt(index);
  }

  private static String shuffleString(String s) {
    char[] characters = s.toCharArray();
    for (int i = 0; i < characters.length; i++) {
      int randomIndex = (int) (Math.random() * characters.length);
      char temp = characters[i];
      characters[i] = characters[randomIndex];
      characters[randomIndex] = temp;
    }
    return new String(characters);
  }
}
