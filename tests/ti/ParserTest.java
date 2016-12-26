package ti;

import org.junit.Test;

import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author Pavel Máca <maca.pavel@gmail.com>
 */
public class ParserTest {


    @Test
    public void testValidate() throws Exception {
        Parser parser = new Parser();
        //parser.printException = true;
        Collection<Object[]> data = data();

        for (Object[] objects : data) {
            boolean expected = (boolean) objects[1];
            String input = (String) objects[0];
            //System.out.println(input);
            if (expected) {
                assertTrue(parser.validate(input));
            } else {
                assertFalse(parser.validate(input));
            }
        }


    }

    /**
     * Převzatá sada testů
     * @author Ondřej Doktor
     */
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{

                // positive tests
                {"a", true},
                {"abc", true},
                {"a(bc)", true},
                {"((a)bc)", true},
                {"((bc))", true},
                {"(((b)c))", true},
                {"a+b", true},
                {"(a+b)", true},
                {"(ace+b)", true},
                {"(a(s+f)ce+b)", true},
                {"(a+b)*", true},
                {"(a*+b)*", true},
                {"(a*+b*)*", true},
                {"a*bc*", true},
                {"a*(bc)*", true},
                {"a*(bc)", true},
                {"(a)*(bc)", true},
                {"(aa + ab(bb)*ba)*(b + ab(bb)*a) (a(bb)*a + (b + a(bb)*ba)(aa + ab(bb)*ba)*(b + ab(bb)*a))*", true},

                // negative tests
                {"", false},
                {"()", false},
                {"(", false},
                {")", false},
                {"*", false},
                {"(*", false},
                {"(+*", false},
                {"+*", false},
                {"a+*", false},
                {"(a)bc)", false},
                {"((()c))", false},
        });
    }

}