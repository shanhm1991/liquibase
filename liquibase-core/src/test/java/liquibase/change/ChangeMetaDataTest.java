package liquibase.change;

import liquibase.database.core.H2Database;
import liquibase.database.core.MySQLDatabase;
import liquibase.database.core.OracleDatabase;
import liquibase.structure.DatabaseObject;
import liquibase.structure.core.Column;
import liquibase.structure.core.Table;
import liquibase.structure.core.View;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertSame;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.matches;
import static org.mockito.Mockito.mock;

public class ChangeMetaDataTest {

    @Test
    public void constructor() {
        HashSet<ChangeParameterMetaData> params = new HashSet<ChangeParameterMetaData>();
        params.add(new ChangeParameterMetaData("a", "a", null, null, null, Integer.class, null, null, null));

        HashMap<String, String> notes = new HashMap<String, String>();
        notes.put("db1", "note1");
        notes.put("db2", "note2");

        String[] appliesTo = new String[] {"table", "column"};
        ChangeMetaData metaData = new ChangeMetaData("x", "y", 10, appliesTo, notes, params);

        assertEquals("x", metaData.getName());
        assertEquals("y", metaData.getDescription());
        assertEquals(10, metaData.getPriority());

        assertEquals(2, metaData.getAppliesTo().size());

        assertEquals(1, metaData.getParameters().size());
        assertEquals("a", metaData.getParameters().keySet().iterator().next());
        assertEquals("note1", metaData.getNotes("db1"));
        assertEquals("note2", metaData.getNotes("db2"));
        assertNull(metaData.getNotes("db3"));

    }

    @Test
    public void constructor_nullParams() {
        ChangeMetaData metaData = new ChangeMetaData("x", "y", 5, null, null, null);
        assertEquals(0, metaData.getParameters().size());
    }

    @Test
    public void constructor_nullAppliesTo() {
        ChangeMetaData metaData = new ChangeMetaData("x", "y", 5, null, null, null);
        assertNull(metaData.getAppliesTo());
    }

    @Test
    public void constructor_emptyAppliesTo() {
        ChangeMetaData metaData = new ChangeMetaData("x", "y", 5, new String[0], null, null);
        assertNull("Empty appliesTo should convert to a null appliesTo", metaData.getAppliesTo());
    }

    @Test(expected = UnsupportedOperationException.class)
    public void getParameters_unmodifyable() {
        new ChangeMetaData("x", "y", 1, null, null, new HashSet()).getParameters().put("new", mock(ChangeParameterMetaData.class));
    }

    @Test
    public void getRequiredParameters_empty() {
        ChangeMetaData changeMetaData = new ChangeMetaData("x", "y", 1, null, null, null);

        assertEquals(0, changeMetaData.getRequiredParameters(new H2Database()).size());
    }

    @Test
    public void getRequiredParameters() {
        HashSet<ChangeParameterMetaData> parameters = new HashSet<ChangeParameterMetaData>();
        parameters.add(new ChangeParameterMetaData("noneRequired", "x", null, null, null, Integer.class, new String[]{"none"}, null, null));
        parameters.add(new ChangeParameterMetaData("allRequired", "x", null, null, null, Integer.class, new String[]{"all"}, null, null));
        parameters.add(new ChangeParameterMetaData("h2Required", "x", null, null, null, Integer.class, new String[] {"h2"}, null, null));
        parameters.add(new ChangeParameterMetaData("oracleRequired", "x", null, null, null, Integer.class, new String[] {"oracle"}, null, null));
        ChangeMetaData changeMetaData = new ChangeMetaData("x", "y", 1, null, null, parameters);

        assertSetEquals(new String[]{"allRequired", "h2Required"}, changeMetaData.getRequiredParameters(new H2Database()).keySet());
        assertSetEquals(new String[]{"allRequired", "oracleRequired"}, changeMetaData.getRequiredParameters(new OracleDatabase()).keySet());
        assertSetEquals(new String[]{"allRequired"}, changeMetaData.getRequiredParameters(new MySQLDatabase()).keySet());
    }

    private void assertSetEquals(String[] expected, Set<String> set) {
        Assert.assertEquals("Set size does not match", expected.length, set.size());
        for (String string : expected) {
            Assert.assertTrue("Missing expected element "+string, set.contains(string));
        }
        for (String found : set) {
            Assert.assertTrue("Unexpected element in set: "+found, Arrays.asList(expected).contains(found));
        }
    }

    @Test
    public void appliesTo() {
        ChangeMetaData metaData = new ChangeMetaData("x", "y", 5, new String[] {"table", "column"}, null, null);
        assertTrue(metaData.appliesTo(new Table()));
        assertTrue(metaData.appliesTo(new Column()));
        assertFalse(metaData.appliesTo(new View()));
    }

    @Test
    public void appliesTo_nullAppliesTo() {
        ChangeMetaData metaData = new ChangeMetaData("x", "y", 5, new String[0], null, null);
        assertFalse(metaData.appliesTo(new Table()));
        assertFalse(metaData.appliesTo(new Column()));
    }
}
