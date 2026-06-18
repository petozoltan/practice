package pet.practice.coding;

import org.junit.jupiter.api.Test;

import java.io.CharArrayReader;
import java.io.IOException;
import java.util.Stack;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static pet.practice.coding.JSONPrinter.Mode.UNKNOWN;

/// TODO UNFINISHED
public class JSONPrinter {

    private static final String INPUT = """
            "books":
            [
              {
                "bookName": "AAA",
                "bookYear": 1989,
                "authorNames":
                [
                  "xyz",
                  "abc"
                ]
              }
            ],
            "count": 4,
            "libraryName": "x"
            """;

    private static final String EXPECTED_OUTPUT = """
            books[0].bookName=AAA
            books[0].bookYear=1989
            books[0].authorNames[0]=xyz
            books[0].authorNames[1]=abc
            
            count=4
            
            libraryName=x
            """;

    @Test
    void printTerminalNodesTest() throws IOException {
        assertEquals(EXPECTED_OUTPUT, printTerminalNodes(INPUT));
    }

    static class InvalidJSONException extends RuntimeException {
        public InvalidJSONException() {
            super("Invalid JSON");
        }
    }

    static class Context {

        Stack<String> path = new Stack<>();
        Mode mode = null;
        StringBuilder text = null;
    }

    enum Mode {

        UNKNOWN {
            @Override
            void parseCharacter(final char ch, final Context context) throws InvalidJSONException {
                switch (ch) {
                    case '}':
                    case ']':
                    case ':':
                        throw new InvalidJSONException();
                    case ' ':
                        break;
                    case '{':
                        context.mode = OBJECT;
                        break;
                    case '[':
                        context.mode = ARRAY;
                        break;
                    case '"':
                        context.mode = NAME;
                        context.text = new StringBuilder();
                        break;
                    default:
                        throw new InvalidJSONException();
                }
            }
        },
        NAME {
            @Override
            void parseCharacter(final char ch, final Context context) throws InvalidJSONException {
                switch (ch) {
                    case '"':
                        context.path.push("." + context.text);
                        context.text = null;
                        context.mode = SEPARATOR;
                        break;
                    default:
                        context.text.append(ch);
                }
            }
        },
        SEPARATOR {
            @Override
            void parseCharacter(final char ch, final Context context) throws InvalidJSONException {
                switch (ch) {
                    case '{':
                    case '}':
                    case '[':
                    case ']':
                    case '"':
                        throw new InvalidJSONException();
                    case ' ':
                        break;
                    case ':':
                        context.mode = UNKNOWN;
                        break;
                    default:
                        throw new InvalidJSONException();
                }
            }
        },
        VALUE {
            @Override
            void parseCharacter(final char ch, final Context context) throws InvalidJSONException {
                switch (ch) {
                    case '}':
                    case ']':
                    case ':':
                        throw new InvalidJSONException();
                    case ' ':
                        break;
                    case '{':
                        context.mode = OBJECT;
                        break;
                    case '[':
                        context.mode = ARRAY;
                        break;
                    case '"':
                        context.mode = TEXT;
                        context.text = new StringBuilder();
                        break;
                    default:
                        context.mode = NUMBER;
                        context.text = new StringBuilder().append(ch);
                }
            }
        },
        TEXT {
            @Override
            void parseCharacter(final char ch, final Context context) throws InvalidJSONException {
                switch (ch) {
                    case '"':
                        context.path.push("=" + context.text);
                        context.text = null;
                        context.mode = SEPARATOR;
                        break;
                    default:
                        context.text.append(ch);
                }
            }
        },
        NUMBER {
            @Override
            void parseCharacter(final char ch, final Context context) throws InvalidJSONException {
                {
                    switch (ch) {
                        case '{':
                        case '}':
                        case '[':
                        case ']':
                        case '"':
                            throw new InvalidJSONException();
                        case ' ':
                            context.path.push("=" + context.text);
                            context.text = null;
                            context.mode = SEPARATOR;
                            break;
                        default:
                            context.text.append(ch);
                    }
                }
            }
        },
        OBJECT {
            @Override
            void parseCharacter(final char ch, final Context context) throws InvalidJSONException {

            }
        },
        ARRAY {
            @Override
            void parseCharacter(final char ch, final Context context) throws InvalidJSONException {

            }
        },
        ARRAY_ITEM {
            @Override
            void parseCharacter(final char ch, final Context context) throws InvalidJSONException {

            }
        };

        abstract void parseCharacter(char ch, Context context) throws InvalidJSONException;
    }

    String printTerminalNodes(String json) throws IOException {

        try (CharArrayReader reader = new CharArrayReader(json.toCharArray())) {

            Context context = new Context();
            context.mode = UNKNOWN;
            context.text = null;

            int c;
            while ((c = reader.read()) != -1) {
                char ch = (char) c;
                context.mode.parseCharacter(ch, context);
            }
        }

        return EXPECTED_OUTPUT;
    }
}
