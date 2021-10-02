package ru.netology;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static ru.netology.Frog.MAX_POSITION;
import static ru.netology.Frog.MIN_POSITION;

public class Main {
    public static final String UNKNOWN_COMMAND = "Неизвестная команда";

    public static void main(String[] args) {
        final Scanner scanner = new Scanner(System.in);

        final Frog frog = new Frog();
        final List<FrogCommand> commands = new ArrayList<>();
        int curCommand = -1;

        while (true) {
            //считываем ввод и конструируем команду, если
            //пользователь не хотел выйти
            System.out.println("Введите команду");
            final String newCommand = scanner.nextLine();
            switch (newCommand) {
                case "0": {
                    System.out.println("Завершение программы...");
                    return;
                }
                case "<<": {
                    if (undoCommand(commands, curCommand)) {
                        curCommand--;
                    }
                    break;
                }
                case ">>": {
                    if (redoCommand(commands, curCommand)) {
                        curCommand++;
                    }
                    break;
                }
                case "!!": {
                    if (repeatCommand(commands, curCommand)) {
                        curCommand++;
                    }
                    break;
                }
                default: {
                    final FrogCommand frogCommand;
                    try {
                        frogCommand = tryParseCommand(newCommand, frog);
                    } catch (Exception e) {
                        System.out.println(UNKNOWN_COMMAND);
                        break;
                    }
                    if (doCommand(commands, curCommand, frogCommand)) {
                        curCommand++;
                    }
                    break;
                }
            }

            //рисуем поле после команды
            paintField(frog);
        }
    }

    private static boolean undoCommand(List<FrogCommand> commands, int curCommand) {
        if (curCommand < 0) {
            System.out.println("Нечего отменять!");
            return false;
        } else {
            commands.get(curCommand).undo();
            return true;
        }
    }

    private static boolean redoCommand(List<FrogCommand> commands, int curCommand) {
        if (curCommand == commands.size() - 1) {
            System.out.println("Нечего повторять!");
            return false;
        } else {
            curCommand++;
            commands.get(curCommand).doAction();
            return true;
        }
    }

    private static boolean repeatCommand(List<FrogCommand> commands, int curCommand) {
        if (curCommand < 0) {
            System.out.println("Нечего повторять!");
            return false;
        } else {
            deleteRedundantCommands(commands, curCommand);
            final FrogCommand frogCommand = commands.get(curCommand);
            commands.add(frogCommand);
            frogCommand.doAction();
            return true;
        }
    }

    private static void deleteRedundantCommands(List<FrogCommand> commands, int curCommand) {
        if (curCommand != commands.size() - 1) {
            //удаляем все команды которые были отменены
            commands.subList(curCommand + 1, commands.size()).clear();
        }
    }

    private static boolean doCommand(List<FrogCommand> commands, int curCommand, FrogCommand frogCommand) {
        deleteRedundantCommands(commands, curCommand);
        if (frogCommand.doAction()) {
            commands.add(frogCommand);
            return true;
        } else {
            System.out.println("Не удалось выполнить команду");
            return false;
        }
    }

    private static FrogCommand tryParseCommand(String newCommand, Frog frog) {
        if (newCommand.length() < 2) {
            throw new RuntimeException(UNKNOWN_COMMAND);
        }
        final String sign = newCommand.substring(0, 1);
        final int steps = Integer.parseInt(newCommand.substring(1));

        switch (sign) {
            case "-": {
                return FrogCommands.jumpLeftCommand(frog, steps);
            }
            case "+": {
                return FrogCommands.jumpRightCommand(frog, steps);
            }
            default: {
                throw new RuntimeException(UNKNOWN_COMMAND);
            }
        }
    }

    private static void paintField(Frog frog) {
        for (int i = MIN_POSITION; i <= MAX_POSITION; i++) {
            if (i == frog.position) {
                System.out.print("x");
            } else {
                System.out.print("_");
            }
        }
        System.out.println();
    }
}
