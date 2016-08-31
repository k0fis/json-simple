/* See: README for this file's copyright, terms, and conditions. */
package org.json.simple;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/** JsonObject is a common non-thread safe data format for string to data mappings. The contents of a JsonObject are only
 * validated as JSON values on serialization.
 * @see Jsoner
 * @since 2.0.0 */
public class JsonObject extends HashMap<String, Object> implements Jsonable{
    /** The serialization version this class is compatible
     * with. This value doesn't need to be incremented if and only if the only changes to occur were updating comments,
     * updating javadocs, adding new
     * fields to the class, changing the fields from static to non-static, or changing the fields from transient to non
     * transient. All other changes require this number be incremented. */
    private static final long serialVersionUID = 1L;

    /** Instantiates an empty JsonObject. */
    public JsonObject(){
        super();
    }

    /** Instantiate a new JsonObject by accepting a map's entries, which could lead to de/serialization issues of the
     * resulting JsonObject since the entry values aren't validated as JSON values.
     * @param map represents the mappings to produce the JsonObject with. */
    public JsonObject(final Map<String, ?> map){
        super(map);
    }

    /** A convenience method that assumes there is a BigDecimal, Number, or String at the given key. If a Number is there
     * its Number#doubleValue() is used to construct a new BigDecimal(double). If a String is there it is used to
     * construct a new BigDecimal(String).
     * @param key representing where the value ought to be stored at.
     * @param defaultValue representing what is returned when the key isn't in the JsonObject.
     * @return the value stored at the key or the default provided if the key doesn't exist.
     * @throws ClassCastException if there was a value but didn't match the assumed return types.
     * @throws NumberFormatException if a String isn't a valid representation of a BigDecimal.
     * @see BigDecimal
     * @see Number#doubleValue() */
    public BigDecimal getBigDecimalOrDefault(final String key, final BigDecimal defaultValue){
        Object returnable = this.getOrDefault(key, defaultValue);
        if(returnable instanceof BigDecimal){
            /* Success there was a BigDecimal or it defaulted. */
        }else if(returnable instanceof Number){
            /* A number can be used to construct a BigDecimal */
            returnable = new BigDecimal(returnable.toString());
        }else if(returnable instanceof String){
            /* A number can be used to construct a BigDecimal */
            returnable = new BigDecimal((String)returnable);
        }
        return (BigDecimal)returnable;
    }

    /** A convenience method that assumes there is a boolean value at the given key.
     * @param key representing where the value ought to be stored at.
     * @param defaultValue representing what is returned when the key isn't in the JsonObject.
     * @return the value stored at the key or the default provided if the key doesn't exist.
     * @throws ClassCastException if there was a value but didn't match the assumed return type. */
    public boolean getBooleanOrDefault(final String key, final boolean defaultValue){
        return (boolean)this.getOrDefault(key, defaultValue);
    }

    /** A convenience method that assumes there is a Number value at the given key.
     * @param key representing where the value ought to be stored at.
     * @param defaultValue representing what is returned when the key isn't in the JsonObject.
     * @return the value stored at the key (which may involve rounding or truncation) or the default provided if the key
     *         doesn't exist.
     * @throws ClassCastException if there was a value but didn't match the assumed return type.
     * @see Number#byteValue() */
    public float getByteOrDefault(final String key, final byte defaultValue){
        return ((Number)this.getOrDefault(key, defaultValue)).byteValue();
    }

    /** A convenience method that assumes there is a Number value at the given key.
     * @param key representing where the value ought to be stored at.
     * @param defaultValue representing what is returned when the key isn't in the JsonObject.
     * @return the value stored at the key (which may involve rounding or truncation) or the default provided if the key
     *         doesn't exist.
     * @throws ClassCastException if there was a value but didn't match the assumed return type.
     * @see Number#doubleValue() */
    public double getDoubleOrDefault(final String key, final double defaultValue){
        return ((Number)this.getOrDefault(key, defaultValue)).doubleValue();
    }

    /** A convenience method that assumes there is a String value at the given key representing a fully qualified name in
     * dot notation of an enum.
     * @param key representing where the value ought to be stored at.
     * @param defaultValue representing what is returned when the key isn't in the JsonObject.
     * @param <T> the Enum type the value at the key is expected to belong to.
     * @return the enum based on the string found at the key, or the defaultValue provided if the key doesn't exist or
     *         an IllegalArgumentException or NullPointerException occurs due to the value lookup.
     * @throws ClassNotFoundException if the value was a String but the declaring enum type couldn't be determined with
     *         it.
     * @throws ClassCastException if the element at the index was not a String or if the fully qualified enum name is of
     *         the wrong type.
     * @throws IllegalArgumentException if an enum type was determined but it doesn't define an enum with the determined
     *         name.
     * @see Enum static method valueOf(Class, String) */
    @SuppressWarnings("unchecked")
    public <T extends Enum<T>> T getEnumOrDefault(final String key, final T defaultValue) throws ClassNotFoundException{
        /* Supressing the unchecked warning because the returnType is dynamically identified and could lead to a
         * ClassCastException when returnType is cast to Class<T>, which is expected by the method's contract. */
        T returnable;
        final String value;
        final String[] splitValues;
        final int numberOfSplitValues;
        final StringBuilder returnTypeName;
        final StringBuilder enumName;
        final Class<T> returnType;
        /* Check to make sure the key wasn't actually there and wasn't coincidentally the defaulted String as its value. */
        if(this.containsKey(key)){
            /* Make sure the value at the key is a String. */
            value = this.getStringOrDefault(key, "");
            /* Get the package, class, and enum names. */
            splitValues = value.split("\\.");
            numberOfSplitValues = splitValues.length;
            returnTypeName = new StringBuilder();
            enumName = new StringBuilder();
            for(int i = 0; i < numberOfSplitValues; i++){
                if(i == (numberOfSplitValues - 1)){
                    /* If it is the last split value then it should be the name of the Enum since dots are not allowed
                     * in enum names. */
                    enumName.append(splitValues[i]);
                }else if(i == (numberOfSplitValues - 2)){
                    /* If it is the penultimate split value then it should be the end of the package/enum type and not
                     * need a dot appended to it. */
                    returnTypeName.append(splitValues[i]);
                }else{
                    /* Must be part of the package/enum type and will need a dot appended to it since they got removed
                     * in the split. */
                    returnTypeName.append(splitValues[i]);
                    returnTypeName.append(".");
                }
            }
            /* Use the package/class and enum names to get the Enum<T>. */
            returnType = (Class<T>)Class.forName(returnTypeName.toString());
            returnable = Enum.valueOf(returnType, enumName.toString());
            return returnable;
        }else{
            /* It wasn't there and according to the method's contract we return the default value. */
            returnable = defaultValue;
        }
        return returnable;
    }

    /** A convenience method that assumes there is a Number value at the given key.
     * @param key representing where the value ought to be stored at.
     * @param defaultValue representing what is returned when the key isn't in the JsonObject.
     * @return the value stored at the key (which may involve rounding or truncation) or the default provided if the key
     *         doesn't exist.
     * @throws ClassCastException if there was a value but didn't match the assumed return type.
     * @see Number#floatValue() */
    public float getFloatOrDefault(final String key, final float defaultValue){
        return ((Number)this.getOrDefault(key, defaultValue)).floatValue();
    }

    /** A convenience method that assumes there is a Number value at the given key.
     * @param key representing where the value ought to be stored at.
     * @param defaultValue representing what is returned when the key isn't in the JsonObject.
     * @return the value stored at the key (which may involve rounding or truncation) or the default provided if the key
     *         doesn't exist.
     * @throws ClassCastException if there was a value but didn't match the assumed return type.
     * @see Number#intValue() */
    public int getIntegerOrDefault(final String key, final int defaultValue){
        return ((Number)this.getOrDefault(key, defaultValue)).intValue();
    }

    /** A convenience method that assumes there is a Collection at the given key.
     * @param <T> the kind of collection to expect at the key.
     * @param key representing where the value ought to be stored at.
     * @param defaultValue representing what is returned when the key isn't in the JsonObject.
     * @return the value stored at the key or the default provided if the key doesn't exist.
     * @throws ClassCastException if there was a value but didn't match the assumed return type. */
    @SuppressWarnings("unchecked")
    public <T extends Collection<?>> T getCollectionOrDefault(final String key, final T defaultValue){
        /* The unchecked warning is suppressed because there is no way of guaranteeing at compile time the cast will work. */
        return (T)this.getOrDefault(key, defaultValue);
    }

    /** A convenience method that assumes there is a Map at the given key.
     * @param <T> the kind of map to expect at the key.
     * @param key representing where the value ought to be stored at.
     * @param defaultValue representing what is returned when the key isn't in the JsonObject.
     * @return the value stored at the key or the default provided if the key doesn't exist.
     * @throws ClassCastException if there was a value but didn't match the assumed return type. */
    @SuppressWarnings("unchecked")
    public <T extends Map<?, ?>> T getMapOrDefault(final String key, final T defaultValue){
        /* The unchecked warning is suppressed because there is no way of guaranteeing at compile time the cast will work. */
        return (T)this.getOrDefault(key, defaultValue);
    }

    /** A convenience method that assumes there is a Number value at the given key.
     * @param key representing where the value ought to be stored at.
     * @param defaultValue representing what is returned when the key isn't in the JsonObject.
     * @return the value stored at the key (which may involve rounding or truncation) or the default provided if the key
     *         doesn't exist.
     * @throws ClassCastException if there was a value but didn't match the assumed return type.
     * @see Number#longValue() */
    public long getLongOrDefault(final String key, final long defaultValue){
        return ((Number)this.getOrDefault(key, defaultValue)).longValue();
    }

    /** A convenience method that assumes there is a Number value at the given key.
     * @param key representing where the value ought to be stored at.
     * @param defaultValue representing what is returned when the key isn't in the JsonObject.
     * @return the value stored at the key (which may involve rounding or truncation) or the default provided if the key
     *         doesn't exist.
     * @throws ClassCastException if there was a value but didn't match the assumed return type.
     * @see Number#shortValue() */
    public short getShortOrDefault(final String key, final short defaultValue){
        return ((Number)this.getOrDefault(key, defaultValue)).shortValue();
    }

    /** A convenience method that assumes there is a String value at the given key.
     * @param key representing where the value ought to be stored at.
     * @param defaultValue representing what is returned when the key isn't in the JsonObject.
     * @return the value stored at the key or the default provided if the key doesn't exist.
     * @throws ClassCastException if there was a value but didn't match the assumed return type. */
    public String getStringOrDefault(final String key, final String defaultValue){
        return (String)this.getOrDefault(key, defaultValue);
    }

    /* (non-Javadoc)
     * @see org.json.simple.Jsonable#asJsonString() */
    @Override
    public String toJson(){
        final StringWriter writable = new StringWriter();
        try{
            this.toJson(writable);
        }catch(final IOException caught){
            /* See java.io.StringWriter. */
        }
        return writable.toString();
    }

    /* (non-Javadoc)
     * @see org.json.simple.Jsonable#toJsonString(java.io.Writer) */
    @Override
    public void toJson(final Writer writable) throws IOException{
        /* Writes the map in JSON object format. */
        boolean isFirstEntry = true;
        final Iterator<Entry<String, Object>> entries = this.entrySet().iterator();
        writable.write('{');
        while(entries.hasNext()){
            if(isFirstEntry){
                isFirstEntry = false;
            }else{
                writable.write(',');
            }
            final Map.Entry<String, Object> entry = entries.next();
            writable.write(Jsoner.serialize(entry.getKey()));
            writable.write(':');
            writable.write(Jsoner.serialize(entry.getValue()));
        }
        writable.write('}');
    }
}
