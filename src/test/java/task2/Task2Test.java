package task2;

import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.format.DateTimeParseException;

import static org.junit.jupiter.api.Assertions.*;

class Task2Test {

    @Test
    void shouldCreateCorrectInstance() throws InvocationTargetException, IllegalAccessException, NoSuchMethodException, InstantiationException {
        Clazz expectedInstance = new Clazz();
        expectedInstance.setStringProperty("value1");
        expectedInstance.setMyNumber(10);
        expectedInstance.setTimeProperty(Instant.parse("2022-11-29T18:30:00.00Z"));

        Clazz instance = Task2.loadFromProperties(Clazz.class, Paths.get("./target/classes/task2/task2.properties"));

        assertEquals(expectedInstance, instance);
    }

    @Test
    void wrongTypeShouldThrowsException() {
        Exception exception = assertThrows(RuntimeException.class,
                () -> Task2.loadFromProperties(WrongClazz.class, Paths.get("./target/classes/task2/task2.properties")));
        assertEquals("Unknown type boolean", exception.getMessage());
    }

    @Test
    void wrongDateShouldThrowsException() {
        Exception exception = assertThrows(DateTimeParseException.class,
                () -> Task2.loadFromProperties(Clazz.class, Paths.get("./target/classes/task2/wrongdate.properties")));
        assertTrue(exception.getMessage().contains("could not be parsed"));
    }

    @Test
    void wrongIntShouldThrowsException() {
        Exception exception = assertThrows(NumberFormatException.class,
                () -> Task2.loadFromProperties(Clazz.class, Paths.get("./target/classes/task2/wrongint.properties")));
        assertTrue(exception.getMessage().contains("For input string"));
    }

}