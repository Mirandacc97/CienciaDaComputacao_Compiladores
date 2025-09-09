package lexical;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import util.TokenType;

public class Scanner {
  private int state;
  private String contentFile;
  private char[] sourceCode;
  private int pos;

  public Scanner(String filename) throws Exception {
    Path arquivo = Paths.get(filename);
    if (!arquivo.toFile().exists())
      throw new Exception("Arquivo não encontrado!");
    byte[] bytesArquivo = Files.readAllBytes(arquivo);
    if (bytesArquivo == null || bytesArquivo.length == 0)
      throw new Exception("Arquivo encontrado porém não contem nada dentro!");
    contentFile = new String(bytesArquivo, StandardCharsets.UTF_8);
    sourceCode = contentFile.toCharArray();
    pos = 0;
  }

  public Token nextToken() {
    char currentChar;
    String content = "";
    state = 0;

    String tokenStr = "";
    while (true) {
      if (isEoF()) {
        return null;
      }
      currentChar = nextChar();
      tokenStr += currentChar;

      switch(state) {
        case 0:
          if (isLetter(currentChar)) {
            content+=currentChar;
            state = 1;
          }
          break;
        case 1:
          if (isLetter(currentChar) || isDigit(currentChar)) {
            content+=currentChar;
            state = 1;
          } else {
            state = 2;
          }
          break;
        case 2:
          back();
          return new Token(TokenType.IDENTIFIER, content);
      }
    }
  }

  private boolean isLetter(char c) {
    return (c>='a' && c <= 'z') || (c>='A' && c <= 'Z');
  }

  private boolean isDigit(char c) {
    return c >= '0' && c <= '9';
  }

  private boolean isMathOperator(char c) {
    return c == '+' || c == '-' || c == '*' || c == '/';
  }

  private boolean isRelOperator(char c) {
    return c == '>' || c == '<' || c == '=' || c == '!';
  }

  private char nextChar() {
    return sourceCode[pos++];
  }

  private void back() {
    pos--;
  }

  private boolean isEoF() {
    return pos >= sourceCode.length;
  }

  private static String obtemConteudoArquivo(String filename) throws Exception {
    Path arquivo = Paths.get(filename);
    if (arquivo == null)
      throw new Exception("Arquivo não encontrado!");
    byte[] bytesArquivo = Files.readAllBytes(arquivo);
    if (bytesArquivo == null || bytesArquivo.length == 0)
      throw new Exception("Arquivo encontrado porém não contem nada dentro!");
    return new String(bytesArquivo, StandardCharsets.UTF_8);
  }

}
