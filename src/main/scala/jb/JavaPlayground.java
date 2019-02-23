package jb;

import java.util.stream.IntStream;

public class JavaPlayground {

    public static void main(String[] args) {
        IntStream.range(1, 10).mapToObj(i -> IntStream.range(1, 10).mapToObj(j -> i * j).map(Object::toString).reduce((s1, s2) -> s1 + " " + s2).get()).forEach(System.out::println);
    }

}
