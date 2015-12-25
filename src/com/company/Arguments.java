package com.company;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Arguments {
    // TODO: ADD Enum - Arguments HOST, LOW_PRICE, HIGH_PRICE

    final private String[] originalArguments;
    //Todo верни массив
    private List<String> arguments;
    private boolean  valid = false;


    public Arguments(String[] args) {
        originalArguments = args;
        arguments = new ArrayList<>(Arrays.asList(args));
        try {
            if(Integer.parseInt(args[1]) > Integer.parseInt(args[2])){
                String tmp = arguments.get(1);
                arguments.set(1,arguments.get(2));
                arguments.set(2,tmp);
            }

        } catch (NumberFormatException e) {
            System.err.println("Неправильные аргументы. Ошибка в формате цен. Необходимые аргументы: ");
            System.err.println(" ссылка на сайт: http://rozetka.com.ua/");
            System.err.println(" цена от(целое число): 1000");
            System.err.println(" цена до(целое число): 1100");

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
