package com.example.project;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ObjectMapperParserTest {

    private ObjectMapperParser objectMapperParser;

    @BeforeEach
    public void setUp() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapperParser = new ObjectMapperParser(objectMapper);
    }

    @Test
    public void testParseValidJson() throws Exception {
        String validJson = "{\"name\": \"John\", \"age\": 30}";
        MyObject result = objectMapperParser.parseJson(validJson);

        assertEquals("John", result.getName());
        assertEquals(30, result.getAge());
    }

    @Test
    public void testParseInvalidJson() {
        String invalidJson = "invalid_json";
        assertThrows(Exception.class, () -> objectMapperParser.parseJson(invalidJson));
    }



    /** Test 1 - Style A - COMPILER WONT ALLOW THIS
     *  - Code never reaches the assertion line, would need a try-catch clause with assertion in catch clause
     */
    @Test
    public void testParseValidJson_ParseError1() throws JsonMappingException {
        MyObject myObject = objectMapperParser.throwJsonMappingException();
        assertNull(myObject); //
    }

    /** Test 1 - Style B
     *  - Code never reaches the assertion line, would need a try-catch clause with assertion in catch clause
     *  - SneakyThrows makes it so you don't need to handle the exception in the code nor in the method signature.
     *  - PROBLEM: Even when you have SneakyThrows,  you still need to put a try-catch around the "negative test" to
     *      properly stop and assert the condition.  Having a try-catch removes your need for SneakyThrows, and so
     *      the SneakyThrows adds no extra benefit when you have a try-catch anyways.
     */
    @SneakyThrows
    @Test
    public void testParseValidJson_ParseError2() {
        MyObject myObject = objectMapperParser.throwJsonMappingException();
        assertNull(myObject); // code never reaches this line, would need a try-catch clause with assertion in catch clause
    }

    /** Test 3 - Style C - BAD PRACTICE
     *  - This assertion works but assertion handling is awkward because handling it inside or outside of the catch
     *      clause has a different behavior.
     *  - PROBLEM: Test only works if catch clause is reached and test may appear to pass due to exception
     *      having been swallowed.  I work around this by adding assertNotNull as last statement, but this is awkward.
     *  - PROBLEM: In this test case, because the assert is not testing the exception contents, on error the actual
     *      cause of the test failure is obscured!
     *  - PROBLEM: On complex tests, it is easy to make this oversight, which could lead to bugs down the road.  This is
     *      why I don't recommend this pattern.
     */
    @Test
    public void testParseValidJson_ParseError3() {
        MyObject myObject = null;
        try {
            myObject = objectMapperParser.throwJsonMappingException();
        } catch (JsonMappingException e) {
            assertNull(myObject, "Don't DO this.");
            return;
        }
        assertNotNull(myObject, "Test did not pass.");
    }

    /** Test 4 - Style D - GOOD PRACTICE
     *  - On test failure, throws a clear explanation of error in the error console output, in the caused-by clause.
     *  - On error, also says exception was "Unexpected Exception".
     */
    @Test
    public void testParseValidJson_ParseError4() {
        Assertions.assertThrows(RuntimeException.class, () -> {
            objectMapperParser.throwJsonMappingException();
        });
    }

    /** Test 5 - Style C - BAD PRACTICE
     *  - When test input passes, it works.
     *  - Method signature doesn't need 'throws Exception' because of try-catch clause.
     *  - PROBLEM: On an error, the line number of actual error does NOT appear in the caused by clause because the
     *      error is swallowed and the test is validated only by the assertion line that is reached after the catch clause.
     *      To get around this,  you must always include a System.err.println or similar in the catch block.
     *  - PROBLEM: The assertion gives no information about the actual error that occurred here, but gives a misleading error.
     *  - PROBLEM: On complex tests, it is easy to make this oversight, which could lead to bugs down the road.  This is
     *      why I don't recommend this pattern.
     */
    @Test
    public void testt_ParseValidJson_ShouldPass() {
        String validJson = "{\"name\": \"John\", \"ag\": 30}";
        MyObject result = null;
        try {
            result = objectMapperParser.parseJson(validJson);
        } catch (Exception e) {
            System.err.println("ERROR: " + e.getMessage());
        }
      assertEquals(result.getAge(), 30);
    }

    /** Test 6 - Style D - GOOD PRACTICE
     *  - Shows a clear "Unexpected Exception" message with a "caused by" clause in the error stack trace when test fails.
     *  - Line number of error does appear in the caused by clause.
     */
    @Test
    public void testParseValidJson_ParseError6() {
        String validJson = "{\"name\": \"John\", \"ag\": 30}";
        Assertions.assertDoesNotThrow(() -> {
            MyObject result = objectMapperParser.parseJson(validJson);
            assertEquals(result.getAge(), 31);
        });

    }

    /** Test 1 - Style B - GOOD PRACTICE
     *  - Does not force test method to have 'throws Exception" on the signature.
     *  - Shows a clear "Unexpected Exception" message with a "caused by" clause in the error stack trace when test fails.
     */
    @Test
    public void testParseValidJson_SetupError7() {
        Assertions.assertDoesNotThrow( () -> {
            String validJson = "{\"name\": \"John\", \"age\": 30}";
            objectMapperParser.parseJson(validJson);
            // try {
            Exception thrown = Assertions.assertThrows(Exception.class, () -> {
                objectMapperParser.throwJsonMappingException();
            });
            // } catch (Exception e) {
            //     when using a try-catch, compiler will try to make you put a throws clause on signature because of objectmapper
            // }
            assertEquals(thrown.getMessage(), "Forced JsonMappingException.");
        });
    }

}