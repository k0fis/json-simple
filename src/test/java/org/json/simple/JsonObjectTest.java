/* See: README for this file's copyright, terms, and conditions. */
package org.json.simple;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/** Ensures that JsonObject hasn't regressed in functionality or breaks its API contract. */
public class JsonObjectTest{
    private enum TestEnums{
        A,
        B;
    }

    private static enum TestStaticEnums{
        ONE,
        TWO;
    }

    /** Called before each Test Method. */
    @Before
    public void setUp(){
        /* All of the implemented tests use local variables in their own respective method. */
    }

    /** Called after each Test method. */
    @After
    public void tearDown(){
        /* All of the implemented tests use local variables in their own respective method. */
    }

    /** Ensures another collection can be used to instantiate a JsonObject. */
    @Test
    public void testConstructor(){
        JsonObject json;
        LinkedHashMap<String, Integer> parameter;
        parameter = new LinkedHashMap<String, Integer>();
        parameter.put("key0", 5);
        parameter.put("key1", 10);
        parameter.put("key2", 15);
        json = new JsonObject(parameter);
        Assert.assertTrue(json.containsKey("key0"));
        Assert.assertTrue(json.containsKey("key1"));
        Assert.assertTrue(json.containsKey("key2"));
        Assert.assertTrue(json.containsValue(5));
        Assert.assertTrue(json.containsValue(10));
        Assert.assertTrue(json.containsValue(15));
    }

    /** Ensures a BigDecimal can be gotten if there is a BigDecimal, Number, or String at the key. */
    @Test
    public void testGetBigDecimalOrDefault(){
        final JsonObject json = new JsonObject();
        final BigDecimal defaultValue = new BigDecimal("101");
        json.put("big", new BigDecimal("0"));
        json.put("double", new Double(0));
        json.put("float", new Float(0));
        json.put("long", new Long(0));
        json.put("int", new Integer(0));
        json.put("short", new Short((short)0));
        json.put("byte", new Byte((byte)0));
        json.put("string", new String("0"));
        Assert.assertEquals(new BigDecimal("0"), json.getBigDecimalOrDefault("big", defaultValue));
        Assert.assertEquals(new BigDecimal("0.0"), json.getBigDecimalOrDefault("double", defaultValue));
        Assert.assertEquals(new BigDecimal("0.0"), json.getBigDecimalOrDefault("float", defaultValue));
        Assert.assertEquals(new BigDecimal("0"), json.getBigDecimalOrDefault("long", defaultValue));
        Assert.assertEquals(new BigDecimal("0"), json.getBigDecimalOrDefault("int", defaultValue));
        Assert.assertEquals(new BigDecimal("0"), json.getBigDecimalOrDefault("short", defaultValue));
        Assert.assertEquals(new BigDecimal("0"), json.getBigDecimalOrDefault("byte", defaultValue));
        Assert.assertEquals(new BigDecimal("0"), json.getBigDecimalOrDefault("string", defaultValue));
        Assert.assertEquals(new BigDecimal("101"), json.getBigDecimalOrDefault("doesnotexist", defaultValue));
    }

    /** Ensures a Collection can be returned from a key. */
    @Test
    public void testGetCollectionOrDefault(){
        final JsonObject json = new JsonObject();
        LinkedList<Integer> list;
        HashSet<Integer> set;
        JsonArray array;
        List<?> output0;
        Set<?> output1;
        JsonArray output2;
        list = new LinkedList<Integer>();
        list.add(5);
        list.add(10);
        list.add(15);
        set = new HashSet<Integer>();
        set.add(20);
        set.add(25);
        set.add(30);
        array = new JsonArray();
        array.add(35);
        array.add(40);
        array.add(45);
        json.put("list", list);
        json.put("set", set);
        json.put("array", array);
        output0 = json.getCollectionOrDefault("list", new LinkedList<Integer>());
        Assert.assertTrue(output0.contains(5));
        Assert.assertTrue(output0.contains(10));
        Assert.assertTrue(output0.contains(15));
        output1 = json.getCollectionOrDefault("set", new HashSet<Integer>());
        Assert.assertTrue(output1.contains(20));
        Assert.assertTrue(output1.contains(25));
        Assert.assertTrue(output1.contains(30));
        output2 = json.getCollectionOrDefault("array", new JsonArray());
        Assert.assertTrue(output2.contains(35));
        Assert.assertTrue(output2.contains(40));
        Assert.assertTrue(output2.contains(45));
        Assert.assertEquals(new JsonArray(), json.getCollectionOrDefault("doesnotexist", new JsonArray()));
    }

    /** Ensures enums can be returned from a String value at an index.
     * @throws ClassNotFoundException if the test failed. */
    @Test
    public void testGetEnumOrDefault() throws ClassNotFoundException{
        final JsonObject json = new JsonObject();
        json.put("key0", "org.json.simple.JsonObjectTest$TestStaticEnums.ONE");
        json.put("key1", "org.json.simple.JsonObjectTest$TestEnums.A");
        Assert.assertEquals(JsonObjectTest.TestStaticEnums.ONE, json.getEnumOrDefault("key0", JsonObjectTest.TestStaticEnums.TWO));
        Assert.assertEquals(JsonObjectTest.TestEnums.A, json.getEnumOrDefault("key1", JsonObjectTest.TestEnums.B));
        Assert.assertEquals(JsonObjectTest.TestEnums.A, json.getEnumOrDefault("doesnotexist", JsonObjectTest.TestEnums.A));
    }

    /** Ensure a map can be returned from a key. */
    @Test
    public void testGetMap(){
        final JsonObject json = new JsonObject();
        final LinkedHashMap<Object, Object> map = new LinkedHashMap<Object, Object>();
        final JsonObject object = new JsonObject();
        Map<?, ?> output0;
        JsonObject output1;
        map.put("key0", 0);
        map.put("key1", 1);
        map.put("key2", 2);
        object.put("key3", 3);
        object.put("key4", 4);
        object.put("key5", 5);
        json.put("map", map);
        json.put("object", object);
        output0 = json.<LinkedHashMap<Object, Object>> getMapOrDefault("map", new LinkedHashMap<Object, Object>());
        Assert.assertTrue(output0.containsKey("key0"));
        Assert.assertTrue(output0.containsKey("key1"));
        Assert.assertTrue(output0.containsKey("key2"));
        Assert.assertTrue(output0.containsValue(0));
        Assert.assertTrue(output0.containsValue(1));
        Assert.assertTrue(output0.containsValue(2));
        output1 = json.<JsonObject> getMapOrDefault("object", new JsonObject());
        Assert.assertTrue(output1.containsKey("key3"));
        Assert.assertTrue(output1.containsKey("key4"));
        Assert.assertTrue(output1.containsKey("key5"));
        Assert.assertTrue(output1.containsValue(3));
        Assert.assertTrue(output1.containsValue(4));
        Assert.assertTrue(output1.containsValue(5));
        Assert.assertEquals(new JsonObject(), json.getMapOrDefault("doesnotexist", new JsonObject()));
    }
}
