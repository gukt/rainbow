/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package com.codedog.rainbow.app;

import com.codedog.rainbow.list.LinkedList;

import static com.codedog.rainbow.utilities.StringUtils.join;
import static com.codedog.rainbow.utilities.StringUtils.split;
import static com.codedog.rainbow.app.MessageUtils.getMessage;

import org.apache.commons.text.WordUtils;

public class App {
    public static void main(String[] args) {
        LinkedList tokens;
        tokens = split(getMessage());
        String result = join(tokens);
        System.out.println(WordUtils.capitalize(result));
    }
}
