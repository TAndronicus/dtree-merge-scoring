package jb;

import java.util.Arrays;

public class JavaPlayground {

    public static void main(String[] args) {
        System.out.println(countOccurences("hehe/hehe/heh", "/"));
        System.out.println(isBalanced("()("));
        System.out.println(isBalanced("()())("));
    }

    public static int countOccurences(String word, String substring) {
        return word.split(substring).length - 1;
    }

    public static boolean isBalanced(String word) {
        return word.codePoints()
                .mapToObj(c -> String.valueOf((char) c))
                .map(str -> getParValue(str)).reduce((i1, i2) -> i1 + i2).get() == 0;
    }

    private static int getParValue(String str) {
        if (str.equals("(")) {
            return 1;
        } else if (str.equals(")")) {
            return -1;
        }
        return 0;
    }

}
