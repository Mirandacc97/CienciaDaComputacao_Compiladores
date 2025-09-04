package org.example;

import lexical.Scanner;
import lexical.Token;

public class Main {

  public static void main(String[] args) {
    try {
      Scanner sc = new Scanner("programa.javeco");
      Token tk;
      do {
        tk = sc.nextToken();
        System.out.println(tk);
      } while (tk!=null);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
