package lexical;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import util.TokenType;

public class Scanner {
  private int state;
  private String contentFile;
  private char[] sourceCode;
  private int pos;
  private int linhaPos;
  private int colunaPos;
  public String[] palavrasReservadas;

  private final List<Character> caracteresProibidos = Arrays.asList('@', '`', '´', 'ç', '¨','~', '^');

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
    linhaPos = 1;
    colunaPos = 1;
    this.palavrasReservadas = new String[0];
  }

  private Token createIdentifierToken(String content) {
    for (String palavra : this.palavrasReservadas) {
      if (palavra.equals(content)) {
        return new Token(TokenType.RESERVED_WORD, content);
      }
    }
    return new Token(TokenType.IDENTIFIER, content);
  }

  public Token nextToken() throws Exception {
    char currentChar;
    String content = "";
    state = 0;

    while (!isEoF()) {
      currentChar = nextChar();

      //Questao 8 -> Comentario de linha unica
      if (currentChar == '#') {
        while (!isEoF()) {
          currentChar = nextCharInComment();
          if (currentChar == '\n' || currentChar == '\r') {
            break;
          }
        }
        continue;
      }

      //Questao 8 -> Comentario de multiplas linhas
      if (currentChar == '/') {
        if (pos < sourceCode.length && sourceCode[pos] == '*') {
          nextChar();
          boolean comentarioFechado = false;
          while (!isEoF()) {
            char dentroDoComentario = nextChar();
            if (dentroDoComentario == '*' && pos < sourceCode.length && sourceCode[pos] == '/') {
              nextChar();
              comentarioFechado = true;
              break;
            }
          }
          if (!comentarioFechado) {
            throw new Exception("Comentário de múltiplas linhas não foi fechado!");
          }
          continue;
        }
      }

      if (currentChar == ' ' || currentChar == '\t' || currentChar == '\n' || currentChar == '\r') {
        continue;
      }

      back();
      break;
    }


    if (isEoF()) {
      return null;
    }

    while (!isEoF()) {
      currentChar = nextChar();

      switch (state) {
        case 0:
          if (isLetter(currentChar) || isunderline(currentChar)) {
            content += currentChar;
            state = 1;
          } else if (isDigit(currentChar)) {
            content += currentChar;
            state = 3;
          } else if(isPOINT(currentChar)) {
            content += currentChar;
            if (pos >= sourceCode.length || !isDigit(sourceCode[pos])) {
              return new Token(TokenType.POINT,content);
            }else {
              state = 5;
            }
          }else if (isMathOperator(currentChar)) {
            content += currentChar;
            return new Token(TokenType.MATH_OPERATOR, content);
          } else if (isAssignment(currentChar) || isRelOperator(currentChar)) {
            content += currentChar;
            state = 2;
          } else if (isLeftRelatives(currentChar)) {
            content += currentChar;
            return new Token(TokenType.LEFT_RELATIVES, content);
          } else if (isRightRelatives(currentChar)) {
            content += currentChar;
            return new Token(TokenType.RIGHT_RELATIVES, content);
          } else {
            if (caracteresProibidos.contains(currentChar))
              throw new Exception("Caractere inválido inserido '" + currentChar + "' na linha " + linhaPos + ", coluna " + colunaPos);
            throw new RuntimeException("Caractere não reconhecido '" + currentChar + "' na linha " + linhaPos + ", coluna " + colunaPos);
          }
          break;

        case 1:
          if (isLetter(currentChar) || isDigit(currentChar) || isunderline(currentChar)) {
            content += currentChar;
          } else {
            back();
            return createIdentifierToken(content);
          }
          break;

        case 2:
          if (currentChar == '=') {
            content += currentChar;
            return new Token(TokenType.REL_OPERATOR, content);
          } else {
            back();
            if (content.equals("=")) {
              return new Token(TokenType.ASSIGNMENT, content);
            } else {
              return new Token(TokenType.REL_OPERATOR, content);
            }
          }

        case 3:
          if (isDigit(currentChar)) {
            content += currentChar;
          } else if (isPOINT(currentChar)) {
            content += currentChar;
            state = 4;
          } else if (isLetter(currentChar)){
            content += currentChar;
            throw new RuntimeException("Numero mal formado:" + content);
          } else {
            back();
            return new Token(TokenType.NUMBER, content);
          }
          break;

        case 4:
          if (isDigit(currentChar)) {
            content += currentChar;
          } else {
            if (isPOINT(content.charAt(content.length() - 1))) {
              throw new RuntimeException("Número mal formado: " + content);
            }
            back();
            return new Token(TokenType.FRACTIONAL_NUMBER, content);
          }
          break;

        case 5:
          if (isDigit(currentChar)){
            content += currentChar;
          }else{
            if (isPOINT(currentChar)) {
              content += currentChar;
              throw new RuntimeException("Numero mal formado:" + content);
            }
            back();
            return new Token(TokenType.FRACTIONAL_NUMBER,content);
          }

          break;
      }
    }

    if (!content.isEmpty()) {
      if (state == 1) {
        return createIdentifierToken(content);
      } else if (state == 2) {
        if (content.equals("=")) {
          return new Token(TokenType.ASSIGNMENT, content);
        } else {
          return new Token(TokenType.REL_OPERATOR, content);
        }
      } else if (state == 3 || state == 4) {
        if (isPOINT(content.charAt(content.length() - 1))) {
          throw new RuntimeException("Número mal formado: " + content);
        }
        if (content.contains(".")) {
          return new Token(TokenType.FRACTIONAL_NUMBER, content);
        } else {
          return new Token(TokenType.NUMBER, content);
        }
      }
    }
    return null;
  }

  private boolean isLetter(char c) { return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z'); }
  private boolean isDigit(char c) { return c >= '0' && c <= '9'; }
  private boolean isMathOperator(char c) { return c == '+' || c == '-' || c == '*' || c == '/'; }
  private boolean isRelOperator(char c) { return c == '>' || c == '<' || c == '!'; }
  private boolean isAssignment(char c) { return c == '='; }
  private boolean isunderline(char c) { return c == '_'; }
  private boolean isLeftRelatives(char c) { return c == '('; }
  private boolean isRightRelatives(char c) { return c == ')'; }
  private boolean isPOINT(char c) { return c == '.'; }

  private char nextChar() throws Exception {
    char caracter = sourceCode[pos++];
    if (caracter == '\n') {
      linhaPos++;
      colunaPos = 1;
    } else
      colunaPos++;
    if (caracteresProibidos.contains(caracter))
      throw new Exception("Caractere invalido inserido '" + caracter + "' na linha " + linhaPos + ", coluna " + colunaPos);
    return caracter;
  }

  private char nextCharInComment() throws Exception {
    try {
      return nextChar();
    } catch (Exception e) {
      //Sem excecao, so para comentarios sem gerar erro
    }
    return sourceCode[pos++];
  }

  private void back() {
    pos--;
    if (pos < sourceCode.length) {
      char c = sourceCode[pos];
      if (c == '\n') {
        linhaPos--;
        colunaPos = 1;
      } else {
        colunaPos--;
        if (colunaPos < 1)
          colunaPos = 1;
      }
    }
  }

  private boolean isEoF() { return pos >= sourceCode.length; }

  private static String obtemConteudoArquivo(String filename) throws Exception {
    Path arquivo = Paths.get(filename);
    if (arquivo == null) throw new Exception("Arquivo não encontrado!");
    byte[] bytesArquivo = Files.readAllBytes(arquivo);
    if (bytesArquivo == null || bytesArquivo.length == 0) throw new Exception("Arquivo encontrado porém não contem nada dentro!");
    return new String(bytesArquivo, StandardCharsets.UTF_8);
  }
}
