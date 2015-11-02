package net.pgrid.sequencepredictor;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Parses doubles from comma-separated Strings.
 * @author Patrick Kramer
 */
public class InputParser {
    
    /**
     * Regular expression for matching fractional notations.
     * <p>
     * This regular expression is designed to match fraction including an optional
     * integer part. For example, both "3/4" and "2 1/5" are recognized successfully.
     * </p>
     * <p>
     * This pattern does not validate, however, if the individual double values
     * are valid doubles (e.g. they would be parsed correctly by the 
     * {@code Double.parseDouble} method). 
     * </p>
     */
    public static final Pattern FRACTION_PATTERN = Pattern.compile("((.*?)\\s+)?(.*?)/(.*?)");
    
    private final String input;
    
    public InputParser(String input) {
        this.input = Objects.requireNonNull(input);
    }

    public String getInput() {
        return this.input;
    }
    
    /**
     * Returns a Stream over the double values parsed from the input.
     * @return The Stream.
     * @throws InvalidTokenException - If the input contained a token that could not be parsed.
     */
    public Stream<Double> parse() throws InvalidTokenException {
        Stream.Builder<Double> builder = Stream.builder();
        for (String token : this.tokenize()) {
            builder.accept(this.parseToken(token));
        }
        
        return builder.build();
    }
    
    public double parseToken(String token) throws InvalidTokenException {
        if (token.contains("/")) {
            return parseFraction(token);
        } else {
            return parseDecimal(token);
        }
    }
    
    public double parseDecimal(String token) throws InvalidTokenException {
        try {
            return Double.parseDouble(token);
        } catch (NumberFormatException ex) {
            throw new InvalidTokenException(token, "Not a valid number", ex);
        }
    }
    
    public double parseFraction(String token) throws InvalidTokenException {
        Matcher matcher = FRACTION_PATTERN.matcher(token);
        if (matcher.find()) {
            String integerString = matcher.group(2);
            double integerPart = integerString == null ? 0 : parseDecimal(integerString);
            double nominator   = parseDecimal(matcher.group(3));
            double denominator = parseDecimal(matcher.group(4));
            
            return integerPart + (nominator / denominator);
        } else {
            throw new InvalidTokenException(token, "Not a valid Fraction");
        }
    }
    
    /**
     * Splits the input into tokens and returns a List with the individual 
     * tokens.
     * @return The List of tokens. 
     */
    public List<String> tokenize() {
        return Arrays.stream(this.input.split("\\s*,\\s*"))
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());
    }
}
