package com.company;

import java.net.MalformedURLException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Arguments {
    // TODO: ADD Enum - Arguments HOST, LOW_PRICE, HIGH_PRICE

    final private String[] originalArguments;

    private List<String> arguments;
    private boolean  valid = false;

// TODO конструктор кидает исключение - исправить
    public Arguments(String[] args) {
        originalArguments = args;
        arguments = new ArrayList<>(Arrays.asList(args));
        try {
            arguments.set(0,URI.create(args[0]).toURL().toString());
            if(Integer.parseInt(args[1]) > Integer.parseInt(args[2])){
                String tmp = arguments.get(1);
                arguments.set(1,arguments.get(2));
                arguments.set(2,tmp);
            }

        } catch (NumberFormatException | MalformedURLException e) {
            System.err.println(" Неправильные аргументы. Ошибка в порядке аргументов либо в ссылке на сайт, либо в формате цен. Необходимые аргументы: ");
            System.err.println(" 1-й аргумент ссылка на сайт: http://rozetka.com.ua/");
            System.err.println(" 2-й аргумент цена от(целое число): 1000");
            System.err.println(" 3-й аргумент цена до(целое число): 1100");
            System.err.println(" Пример аргументов: http://rozetka.com.ua/ 1000 1100");

            throw new RuntimeException(e);
        }

        if (args != null && args.length == 3) {
            valid = true;
        }

    }

    public boolean isValidArguments() {
        return valid;
    }

    public String getArg(int index) {
        return arguments.get(index);
    }


}
