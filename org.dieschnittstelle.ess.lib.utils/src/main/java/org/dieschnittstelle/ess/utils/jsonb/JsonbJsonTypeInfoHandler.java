package org.dieschnittstelle.ess.utils.jsonb;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.Logger;

import jakarta.json.JsonObject;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.json.bind.annotation.JsonbTransient;
import jakarta.json.bind.annotation.JsonbTypeDeserializer;
import jakarta.json.bind.serializer.DeserializationContext;
import jakarta.json.bind.serializer.JsonbDeserializer;
import jakarta.json.bind.serializer.JsonbSerializer;
import jakarta.json.bind.serializer.SerializationContext;
import jakarta.json.stream.JsonGenerator;
import jakarta.json.stream.JsonParser;
import java.lang.reflect.Type;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import static org.dieschnittstelle.ess.utils.Utils.show;

/*
 * this is our own implementation of the @JsonTypeInfo semantics from Jackson using
 * json-b standard expressive means, see: https://stackoverflow.com/questions/62398858/deserialize-json-into-polymorphic-pojo-with-json-b-yasson (the following ones are only partially useful: https://itsallbinary.com/java-json-polymorphic-type-handling-jackson-vs-gson-vs-json-b/, see also: https://javaee.github.io/jsonb-spec/users-guide.html)
 *
 * starting from tomee maven plugin version 8.0.2, there are two issues that require more or less dirty workarounds:
 * 1. even though JsonbTypeDeserializer is not declared as inheritable, it is treated as such, which results in a loop
 * when trying to deserialise a concrete subtype of an abstract type on which the annotation has been set
 * 2. before calling serialize(), the object context in the json generator is already created, which does not allow to
 * serialise from a map that contains the original object's attributes, possibly extended by additional ones (like the class name).
 * The implementation of jsonGenerator that is passed (DynamicMappingGenerator$SkipLastWriteEndGenerator@7f8aaa67), further
 * shows an issue when serialising arrays of objects, in which case the writeEnd() call is being ignored and results
 * in an inconsistent state. For this reason, we access the writeEnd() method of the generator's underlying delegate to
 * enforce it...
 *
 * for seeing debug messages, configure log4j accordingly
 */
public class JsonbJsonTypeInfoHandler<T> implements JsonbDeserializer<T>, JsonbSerializer<T> {

    public static final String KLASSNAME_PROPERTY = "@class";

    private static final Jsonb jsonb = JsonbBuilder.create();

    private ObjectMapper jacksonMapper;

    protected static Logger logger = org.apache.logging.log4j.LogManager.getLogger(JsonbJsonTypeInfoHandler.class);

    public JsonbJsonTypeInfoHandler() {
        this.jacksonMapper =  new ObjectMapper();
        this.jacksonMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,false);
    }

    public static class PolymorphicTypeException extends RuntimeException {
        public PolymorphicTypeException(String message) {
            super(message);
        }

        public PolymorphicTypeException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    @Override
    public T deserialize(JsonParser parser, DeserializationContext context, Type rtType) {

        logger.info("deserialise(): type is: " + rtType);

        JsonObject jsonObj = parser.getObject();
        String jsonString = jsonObj.toString();
        logger.debug("deserialise(): jsonString is: " + jsonString);

        if (rtType instanceof Class
                && !Modifier.isAbstract(((Class)rtType).getModifiers())) {
            if (superclassUsesCustomisedDeserialiser((Class) rtType)) {
                // if we do not have an abstract class, we just use the type for deserialisation
                logger.debug("deserialise(): we do not have an abstract type, but our supertype declares custom deserialisation. Use Jackson for deserialising: " + rtType);
//            T obj = (T)jsonb.fromJson(jsonString, ((Class)rtType));
                try {
                    T obj = (T) jacksonMapper.readValue(jsonString, ((Class) rtType));
                    logger.debug("deserialise(): deserialised json to concrete type using jackson as johnzon recursion workaround: " + obj);
                    return obj;
                } catch (Exception e) {
                    String err = "got exception trying to deserialise json using jackson as johnzon workaround: " + e;
                    throw new PolymorphicTypeException(err, e);
                }
            }
            else {
                T obj = (T)jsonb.fromJson(jsonString, ((Class)rtType));
                logger.debug("deserialise(): deserialised json to concrete type using johnzon: " + obj);
                return obj;
            }
        }
        else {
            // this is a custom implementation of the JsonTypeInfo logics that is possible for deserialisation
            // however, as serialisation does not seem to appear possible without duplicating large part of
            // serialisation logic, we just embed the jackson processing here
            logger.debug("deserialise(): we have an abstract type. Lookup classname of concrete class...");

            String klassname = jsonObj.getString(lookupClassnameProperty((Class)rtType));
            logger.debug("deserialise(): klassname is: " + klassname);

            try {
                Class klass = Class.forName(klassname);
                logger.debug("deserialise(): klass is: " + klass);
                T obj = (T)jsonb.fromJson(jsonString, klass);
                logger.debug("deserialise(): deserialised instance of polymorphic type: " + obj);
                return obj;
            }
            catch (Exception e) {
                throw new PolymorphicTypeException("Cannot deserialise object of class: " + klassname + ". Got: " + e,e);
            }
        }
    }

    public boolean superclassUsesCustomisedDeserialiser(Class klass) {
        if (klass == null) {
            return false;
        }
        else if (klass == Object.class) {
            return false;
        }
        else if (klass.isAnnotationPresent(JsonbTypeDeserializer.class) &&
                ((JsonbTypeDeserializer)klass.getAnnotation(JsonbTypeDeserializer.class)).value() == JsonbJsonTypeInfoHandler.class) {
            return true;
        }
        else {
            return superclassUsesCustomisedDeserialiser(klass.getSuperclass());
        }
    }

    @Override
    public void serialize(T obj, JsonGenerator jsonGenerator, SerializationContext serializationContext) {
        logger.info("serialise(): object is: " + obj + ", generator is: " + jsonGenerator);
        try {
            logger.debug("serialise(): object is: " + obj + ", generator is: " + jsonGenerator);
            showLevel(jsonGenerator);
            try {
                // we need to embed the complete serialisation logics here as there does not seem
                // to exist a way how to simply add a single property (specifying the classname) and
                // then run the normal serialisation for the object. It seems that serialise is called
                // after the object boundaries have already been marked in the generator, therefore
                // we also need to embed the logics for embedded objects and arrays, which may then,
                // however, delegate to the serialisation context. I.e. only those objects whose class
                // uses the JsonTypeInfo annotation need to be handled.
                try {
                    String classnameProperty = lookupClassnameProperty(obj.getClass());
                    jsonGenerator.write(classnameProperty, obj.getClass().getName());
                }
                catch (PolymorphicTypeException pt) {
                    // ignore this exception as we use this method also for any objects embedded in ones
                    // marked for custom deserialisation
                }
                for (Method m : collectGetters(obj.getClass())) {
                    String fieldname = getFieldnameForGetter(m.getName());
                    Object fieldvalue = m.invoke(obj);
                    if (fieldvalue != null) {
                        Type fieldtype = m.getGenericReturnType();
                        // cover the primitive types
                        // TODO: for full coverage, this would need to be extended
                        if (fieldtype == Integer.TYPE || fieldtype == Integer.class) {
                            logger.debug("serialise(): int value: " + fieldname + "=" + fieldvalue);
                            jsonGenerator.write(fieldname,(int)fieldvalue);
                        }
                        else if (fieldtype == Long.TYPE || fieldtype == Long.class) {
                            logger.debug("serialise(): long value: " + fieldname + "=" + fieldvalue);
                            jsonGenerator.write(fieldname,(long)fieldvalue);
                        }
                        else if (fieldtype == Double.TYPE || fieldtype == Double.class) {
                            logger.debug("serialise(): double value: " + fieldname + "=" + fieldvalue);
                            jsonGenerator.write(fieldname,(double)fieldvalue);
                        }
                        else if (fieldtype == Boolean.TYPE || fieldtype == Boolean.class) {
                            logger.debug("serialise(): boolean value: " + fieldname + "=" + fieldvalue);
                            jsonGenerator.write(fieldname,(boolean)fieldvalue);
                        }
                        else if (fieldtype == String.class) {
                            logger.debug("serialise(): String value: " + fieldname + "=" + fieldvalue);
                            jsonGenerator.write(fieldname,(String)fieldvalue);
                        }
                        // handle enums
                        else if (fieldtype instanceof Class && ((Class)fieldtype).isEnum()) {
                            logger.debug("serialise(): enum value: " + fieldname + "=" + fieldvalue);
                            jsonGenerator.write(fieldname,String.valueOf(fieldvalue));
                        }
                        // handle arrays - note that currently, only arrays containing objects are considered
                        else if (fieldvalue instanceof Collection) {
                            logger.debug("serialise(): serialise embedded array of field " + fieldname);
                            jsonGenerator.writeStartArray(fieldname);
                            showLevel(jsonGenerator);
                            ((Collection)fieldvalue).forEach(el -> {
                                jsonGenerator.writeStartObject();
                                logger.debug("serialise(): serialise embedded array element of field " + fieldname + " using standard serialiser: " + el);
                                // serialise the embedded value
                                serializationContext.serialize(el,jsonGenerator);
                                jsonGenerator.writeEnd();
                                logger.debug("serialise(): done serialising embedded array element of field " + fieldname);
                            });
                            logger.debug("serialise(): finalising embedded array of field " + fieldname);
                            showLevel(jsonGenerator);
                            jsonGenerator.writeEnd();
                            enforceEnd(jsonGenerator);
                            logger.debug("serialise(): done serialising embedded array of field " + fieldname);
                            showLevel(jsonGenerator);
                        }
                        // handle objects
                        else {
                            jsonGenerator.writeStartObject(fieldname);
                            logger.debug("serialise(): serialise embedded field value of " + fieldname + " using standard serialiser: " + fieldvalue);
                            showLevel(jsonGenerator);
                            // serialise the embedded value
                            serializationContext.serialize(fieldvalue,jsonGenerator);
                            jsonGenerator.writeEnd();
                            logger.debug("serialise(): done serialising embedded field value of field " + fieldname);
                            showLevel(jsonGenerator);
                        }
                    }
                }

                logger.debug("serialise(): done serialising " + obj.getClass());
            }
            catch (Exception e) {
                throw new PolymorphicTypeException("Cannot serialise object: " + obj + ". Got: " + e,e);
            }
        }
        catch (PolymorphicTypeException e) {
            throw e;
        }
        catch (Exception e) {
            throw new PolymorphicTypeException("Cannot serialise object: " + obj + ". Got: " + e,e);
        }
    }

    private static List<Method> collectGetters(Class klass) {
        if (klass == Object.class) {
            return new ArrayList<>();
        }
        else {
            List methods = Arrays.asList(klass.getDeclaredMethods())
                    .stream()
                    .filter(m -> Modifier.isPublic(m.getModifiers()) &&  m.getParameterCount() == 0 && !m.isAnnotationPresent(JsonbTransient.class) && m.getName().startsWith("get") || m.getName().startsWith("is"))
                    .collect(Collectors.toList());
            methods.addAll(collectGetters(klass.getSuperclass()));
            return methods;
        }
    }

    private static String getFieldnameForGetter(String getterName) {
        if (getterName.startsWith("is")) {
            return getterName.substring(2,3).toLowerCase() + getterName.substring(3);
        }
        else {
            return getterName.substring(3,4).toLowerCase() + getterName.substring(4);
        }
    }

    private String lookupClassnameProperty(Class klass) {
        if (klass.isAnnotationPresent(JsonTypeInfo.class)) {
            logger.debug("lookup classname property from annotated klass " + klass);
            return ((JsonTypeInfo)klass.getAnnotation(JsonTypeInfo.class)).property();
        }
        else if (klass.getSuperclass() != null && klass.getSuperclass() != Object.class) {
            return lookupClassnameProperty(klass.getSuperclass());
        }
        else {
            throw new PolymorphicTypeException("Could not determine classname property from class " + klass);
        }
    }

    private void showLevel(JsonGenerator generator) {
        try {
            Field level = generator.getClass().getDeclaredField("level");
            level.setAccessible(true);
            logger.debug("generator level is: " + level.get(generator));
        }
        catch (Exception e) {
            // ignore
        }
    }

    private void enforceEnd(JsonGenerator generator) {
        if (generator.getClass().getName().endsWith("SkipLastWriteEndGenerator")) {
            try {
                Class klass = generator.getClass().getSuperclass();
                Field delegateField = klass.getDeclaredField("delegate");
                delegateField.setAccessible(true);
                Object delegateObj = delegateField.get(generator);
                Method writeEnd = delegateObj.getClass().getMethod("writeEnd");
                writeEnd.invoke(delegateObj);
            }
            catch (Exception e) {
                throw new PolymorphicTypeException("Exception trying to enforce end on generator, it might be that johnzon implementation has changed since tomee maven plugin version 8.0.8. Exception is: " + e,e);
            }
        }
    }

}
