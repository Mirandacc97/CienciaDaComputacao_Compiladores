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
    try {
      System.out.println("Bem vindo ao Compilar de linguagem Javeco!");
      System.out.println("==================== D i r e t i v a s   d o   p r o j e t o ====================");
      System.out.println("So vou aceitar dois tipos primitivos, podendo ser:");
      System.out.println("  String: comecando caracteres lowerCase");
      System.out.println("  Long: comecando digitos");

      System.out.println("Vamor iniciar efetuando a leitura do nosso arquivo");
      Scanner sc = new Scanner("programa.javeco");
      System.out.println("Leitura finalizada! Arquivo reconhecido e o mesmo contem código a ser compilado!");

      System.out.println("Vamor Atribuir os identificadores reservados");
      sc.setPalavrasReservadas(new String[]{"if", "else", "while", "for", "int", "float", "string"});
      System.out.println("Identificadores Registrados!");

      Token tk = null;
      tk = sc.nextToken();
      do {
        System.out.println(tk.toString());
        tk = sc.nextToken();
      } while (tk!=null);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}