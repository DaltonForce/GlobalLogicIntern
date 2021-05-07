import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.function.Predicate;

import static java.util.stream.Collectors.*;

public class Main {

    public static void main(String[] args) {

        Scanner myObj = new Scanner(System.in);
        System.out.println("Wpisz zdanie:");

        String sWithWhite = myObj.nextLine();
        String sWithoutWhite = sWithWhite.replaceAll("[^a-zA-Z]", "");
        int length = sWithoutWhite.length();

        String[] splitString = sWithWhite.split(" ");

        List<String> words = Arrays.stream(splitString)
                .map(e -> e.replaceAll("[^a-zA-Z]", ""))
                .map(String::toUpperCase)
                .collect(toList());

        List<Integer> numberOfLetters = words.stream()
                .filter(e -> joinPredicatesContains(List.of("L", "O", "G", "I", "C")).test(e))
                .map(String::length)
                .collect(toList());

        List<List<Map.Entry<Object, Long>>> occurenciesOfLetters = words.stream()
                .filter(e -> joinPredicatesContains(List.of("L", "O", "G", "I", "C")).test(e))
                .map(word -> Main.getOccurenciesOfLetters(word, List.of("L", "O", "G", "I", "C")))
                .collect(toList());

        Long numberOfOccurenciesOfAllLetters = occurenciesOfLetters.stream()
                .flatMap(Collection::stream)
                .map(Map.Entry::getValue)
                .reduce(Long::sum)
                .orElseThrow();

        List<Long> numberOfOccurenciesPerWord = occurenciesOfLetters.stream()
                .map(e -> e.stream()
                        .map(Map.Entry::getValue)
                        .reduce(Long::sum)
                        .orElse(0L))
                .collect(toList());

        printOutput(numberOfLetters, numberOfOccurenciesOfAllLetters, numberOfOccurenciesPerWord, occurenciesOfLetters, length);
    }

    private static List<Map.Entry<Object, Long>> getOccurenciesOfLetters(String word, Collection<String> seekedLetters) {
        Map<Object, Long> letterToNumberOfOccurencies = Arrays.stream(word.split(""))
                .collect(groupingBy(c -> c, counting()));

        return letterToNumberOfOccurencies.entrySet().stream()
                .filter(e -> {
                    String key = (String) e.getKey();
                    Predicate<String> joinedPredicate = joinPredicatesEquals(seekedLetters);
                    return joinedPredicate.test(key);
                })
                .collect(toList());
    }

    private static Predicate<String> joinPredicatesEquals(Collection<String> seekedLetters) {
        Predicate<String> predicate = e -> false;

        return seekedLetters.stream()
                .map(letter -> predicate.or(e -> e.equals(letter)))
                .reduce(Predicate::or)
                .orElseThrow();
    }

    private static Predicate<String> joinPredicatesContains(Collection<String> seekedLetters) {
        Predicate<String> predicate = e -> false;

        return seekedLetters.stream()
                .map(letter -> predicate.or(e -> e.contains(letter)))
                .reduce(Predicate::or)
                .orElseThrow();
    }

    private static void printOutput(
            List<Integer> numberOfLetters,
            Long numberOfOccurenciesOfAllLetters,
            List<Long> numberOfOccurenciesPerWord,
            List<List<Map.Entry<Object, Long>>> occurenciesOfLetters,
            int length) {
        Map<List<BigDecimal>, String> output = new LinkedHashMap<>();

        for (int i = 0; i < numberOfLetters.size(); i++) {
            Integer numberOfLetter = numberOfLetters.get(i);
            Long numberOfOccurenciesForWord = numberOfOccurenciesPerWord.get(i);
            BigDecimal divide = BigDecimal.valueOf(numberOfOccurenciesForWord)
                    .divide(BigDecimal.valueOf(numberOfOccurenciesOfAllLetters), 2, RoundingMode.HALF_EVEN);
            String occurenciesOfLetter = occurenciesOfLetters.get(i)
                    .stream()
                    .map(Map.Entry::getKey)
                    .map(String::valueOf)
                    .reduce((a, b) -> a + ", " + b)
                    .orElse(null);

            if(occurenciesOfLetter == null){
                continue;
            }

            output.put(List.of(BigDecimal.valueOf(i), divide), "{(" + occurenciesOfLetter.toLowerCase() + "), " + numberOfLetter + "} = " + divide + " (" + numberOfOccurenciesForWord + "/" + numberOfOccurenciesOfAllLetters + ")");
        }

        List<String> orderedOutput = output.entrySet().stream()
                .sorted(Comparator.comparing(a -> a.getKey().get(1)))
                .map(Map.Entry::getValue)
                .collect(toList());

        orderedOutput
                .forEach(System.out::println);

        BigDecimal divide = BigDecimal.valueOf(numberOfOccurenciesOfAllLetters)
                .divide(BigDecimal.valueOf(length), 2, RoundingMode.HALF_EVEN);
        System.out.println("TOTAL Frequency: " + divide + " (" + numberOfOccurenciesOfAllLetters + "/" + length + ")");
    }
}
