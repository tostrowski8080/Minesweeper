import java.util.Scanner;

public class Main {
  private static final Game game = new Game();
  private static final Scanner scanner = new Scanner(System.in);

  public enum ShapeOption {
    RECTANGLE, CROSS, Z, CIRCLE, RING
  }

  public static void main(String[] args) {
    String input;
    boolean end = false;

    System.out.println("Console Minesweeper in Java.");
    System.out.println("Write help at any point during the game to get a tutorial on how to play and a list of commands.");

    generateGame();

    while (!end) {
      if (game.isPlaying()) {
        input = scanner.nextLine().trim();
        String[] parts = input.split("\\s+");

        if (parts.length == 1) {
          switch (parts[0].toUpperCase()) {
            case "HELP":
              printCommands();
              break;
            case "GENERATE":
              generateGame();
              break;
            case "REGENERATE":
              regenerateGame();
              break;
            case "END":
              System.out.println("Ending the game.");
              end = true;
              break;
            case "STUCK":
              game.stuck();
              break;
            default:
              System.out.println("Unknown command.");
              break;
          }
        }
        else if (parts.length == 3) {
          String action = parts[0].toUpperCase();
          try {
            int col = Integer.parseInt(parts[1]);
            int row = Integer.parseInt(parts[2]);
            Point target = new Point(col, row);

            switch (action) {
              case "F":
                game.setFlag(target);
                break;
              case "C":
                game.check(target, true);
                break;
              default:
                System.out.println("Unknown move command: " + action);
                break;
            }
          } catch (NumberFormatException e) {
            System.out.println("Invalid move format. Expected: F/C col row");
          }
        }
        else {
          System.out.println("Invalid input format.");
        }
      }
      else {
        boolean optionPicked = false;

        while (!optionPicked) {
          System.out.println("Do you want to generate a new board? Y/N, N ends the game");
          input = scanner.nextLine().trim();

          switch (input) {
            case "Y":
              generateGame();
              optionPicked = true;
              break;
            case "N":
              System.out.println("Ending the game.");
              end = true;
              optionPicked = true;
              break;
            default:
              System.out.println("Wrong option.");
          }
        }
      }
    }
  }

  public static void generateGame(){
    game.resetGame();

    String input;
    boolean isGenerated = false;

    System.out.println("Choose an option: Beginner, Intermediate, Expert, Custom");

    while (!isGenerated) {
      input = scanner.nextLine();

      switch (input) {
        case "Beginner":
          game.generateBoard(9, 9, 0.1, ShapeOption.RECTANGLE);
          isGenerated = true;
          break;
        case "Intermediate":
          game.generateBoard(16, 16, 0.15, ShapeOption.RECTANGLE);
          isGenerated = true;
          break;
        case "Expert":
          game.generateBoard(30, 16, 0.2, ShapeOption.RECTANGLE);
          isGenerated = true;
          break;
        case "Custom":
          generateCustomGame();
          isGenerated = true;
          break;
        default:
          System.out.println("Wrong option, try again.");
      }
    }

    System.out.println("To play type a move in a format - F/C Column Row");
    System.out.println("F - sets a flag on set point, C - checks set point, if it is a bomb you lose, otherwise reveals the point");
    System.out.println("Ex. F 2 4 - flags a point in column 2 at 4th row");

    game.board.drawBoard();
  }

  public static void generateCustomGame() {
    int cols = -1;

    while (cols < 1) {
      System.out.print("Enter number of columns (int > 0): ");
      try {
        cols = Integer.parseInt(scanner.nextLine().trim());
        if (cols < 1) {
          System.out.println("Number of columns must be at least 1.");
        }
      } catch (NumberFormatException e) {
        System.out.println("Invalid number.");
      }
    }

    int rows = -1;

    while (rows < 1) {
      System.out.print("Enter number of rows (int > 0): ");
      try {
        rows = Integer.parseInt(scanner.nextLine().trim());
        if (rows < 1) {
          System.out.println("Number of rows must be at least 1.");
        }
      } catch (NumberFormatException e) {
        System.out.println("Invalid number.");
      }
    }

    double bombRatio;

    while (true) {
      System.out.print("Enter a bomb-per-tile ratio (double between 0 and 1): ");
      try {
        bombRatio = Double.parseDouble(scanner.nextLine().trim());

        if (bombRatio <= 0.0 || bombRatio >= 1.0) {
          System.out.println("Bomb ratio must be between 0 and 1.");
        } else {
          break;
        }
      } catch (NumberFormatException e) {
        System.out.println("Invalid format. Please enter a decimal number.");
      }
    }

    ShapeOption chosenShape = null;

    while (chosenShape == null) {
      System.out.println("Choose a shape from the following options:");
      for (ShapeOption option : ShapeOption.values()) {
        System.out.println("  - " + option);
      }

      System.out.print("Enter your choice: ");

      try {
        chosenShape = ShapeOption.valueOf(scanner.nextLine().trim().toUpperCase());
      } catch (IllegalArgumentException e) {
        System.out.println("Wrong choice.");
      }
    }

    game.generateBoard(cols, rows, bombRatio, chosenShape);
  }

  public static void regenerateGame(){
    System.out.println("Regenerating game.");

    int cols = game.board.getCols();
    int rows = game.board.getRows();
    double bombRatio = game.getBombRatio();
    ShapeOption shape = game.getShape();

    game.resetGame();
    game.generateBoard(cols, rows, bombRatio, shape);

    System.out.println("To play type a move in a format - F/C Column Row");
    System.out.println("F - sets a flag on set point, C - checks set point, if it is a bomb you lose, otherwise reveals the point");
    System.out.println("Ex. F 2 4 - flags a point in column 2 at 4th row");

    game.board.drawBoard();
  }

  public static void printCommands(){
    System.out.println("To play type a move in a format - F/C Column Row");
    System.out.println("F - sets a flag on set point, C - checks set point, if it is a bomb you lose, otherwise reveals the point");
    System.out.println("Ex. F 2 4 - flags a point in column 2 at 4th row");

    System.out.println("List of commands:");
    System.out.println("generate - generates a new board");
    System.out.println("regenerate - generates using last settings");
    System.out.println("stuck - attempts to flag a bomb near your discovered cell");
    System.out.println("end - ends the game");
    System.out.println("help - prints this message");

  }
}