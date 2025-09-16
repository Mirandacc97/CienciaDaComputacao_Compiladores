package org.example;

import lexical.Scanner;
import lexical.Token;

public class Main {

  /* ==================== D i r e t i v a s   d o   p r o j e t o ====================
   * So vou aceitar dois tipos primitivos, podendo ser:
   * String: comecando caracteres lowerCase
   * Long: comecando digitos
   * */

  public static void main(String[] args) throws Exception {
    // O Scanner é criado aqui
    Scanner sc = new Scanner("programa.javeco");

    String[] palavrasReservadas = {"if", "else", "while", "for", "int", "float", "string"};
    sc.setPalavrasReservadas(palavrasReservadas);


    System.out.println("Leitura finalizada! Arquivo reconhecido e o mesmo contem código a ser compilado!");
    try {
      System.out.println("Bem vindo ao Compilar de linguagem Javeco!");
      System.out.println("==================== D i r e t i v a s   d o   p r o j e t o ====================");
      System.out.println("So vou aceitar dois tipos primitivos, podendo ser:");
      System.out.println("  String: comecando caracteres lowerCase");
      System.out.println("  Long: comecando digitos");

      System.out.println("Vamor iniciar efetuando a leitura do nosso arquivo");
      System.out.println("Vamos inicialmente localizar os identificadores lexicos");
      Token tk;
      do {
        tk = sc.nextToken();
        if (tk != null) {
          System.out.println(tk);
        }
      } while (tk!=null);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}