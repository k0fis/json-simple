package eu.kofis.json_simple;

import org.junit.Assert;
import org.junit.Test;

public class JsonCommentsTest {

    @Test
    public void test1() throws JsonException {
        Object deserialized;
        JsonObject expected = new JsonObject();
        expected.put("pako", true);
        expected.put("defo", "true");
        deserialized = Jsoner.deserialize("{\"pako\":true\n //pica\n \"defo\":\"true\" /*  kunda */}");
        Assert.assertEquals(expected, deserialized);
    }
}
