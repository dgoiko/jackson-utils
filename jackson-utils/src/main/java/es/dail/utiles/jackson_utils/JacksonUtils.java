package es.dail.utiles.jackson_utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

/**
 * Simple envelope to use Jackson basic functions.
 * 
 * Encapsulates all thrown exceptions into {@link JsonException}
 * 
 * @author Dario Goikoetxea
 *
 */
public class JacksonUtils {

    /**
     * Converts a string into it's JSON node representation.
     *
     * @param input JSON string. Must be well-formed JSON
     * @return the node that represents the input string
     * @throws JsonException IF there's an error processing JSON. Should only happen with malformed JSON strings
     */
    public static JsonNode stringToNode(String input) throws JsonException {
        return fromJSON(input, JsonNode.class);
    }

    /**
     * Converts a JSON String into the provided Object using {@link ObjectMapper#readValue(String, Class)}
     *
     * @param <T> Destination class type
     * @param input JSON input
     * @param clazz Class object representing the destination class
     * @return new instance of the desired class
     * @throws JsonException If there's an error processing JSON. May be because of syntax or because the provided JSON does not match the desired object
     */
    public static <T>T fromJSON(String input, Class<T> clazz) throws JsonException {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(input, clazz);
        } catch (JsonParseException e) {
            throw new JsonException("Malformed JSON string", e);
        }catch (JsonMappingException e) {
            throw new JsonException("Mapping error. Could not cast JSON to object", e);
        }catch (IOException e) {
            throw new JsonException("I/O exception. This should never happen", e);
        }catch (RuntimeException e) {
            throw new JsonException("Runtime exception parsing JSON", e);
        }
    }

    /**
     * Converts a JSON node into the provided Object using {@link ObjectMapper#readValue(String, Class)}
     * 
     * @param <T> Destination class type
     * @param input JSON input
     * @param clazz Class object representing the destination class
     * @return new instance of the desired class
     * @throws JsonException If there's an error processing JSON. The provided JSON does not match the desired object
     */
    public static <T>T fromJSON(JsonNode input, Class<T> clazz) throws JsonException {
        ObjectMapper mapper = new ObjectMapper();
        try {
            String str = toJSON(input, false);
            return mapper.readValue(str, clazz);
        } catch (JsonMappingException e) {
            throw new JsonException("Mapping error. Could not cast JSON to object", e);
        } catch (IOException e) {
            throw new JsonException("I/O exception. This should never happen", e);
        } catch (RuntimeException e) {
            throw new JsonException("Runtime exception parsing JSON", e);
        }
    }

    /**
     *
     * Converts a JSON String into the provided Object using {@link ObjectMapper#readValue(String, Class)}.
     * 
     * It assumes the JSON represents a list of T
     *
     * @param <T> Destination class type
     * @param input JSON input
     * @param clazz Class object representing the destination class
     * @return list with new instances of the desired class
     * @throws JsonException If there's an error processing JSON. May be because of syntax or because the provided JSON does not match the desired object
     */
    public static <T>List<T> listFromJSON(String input, Class<T> clazz) throws JsonException {
        JsonNode node = stringToNode(input);
        if(!node.isArray()) throw new JsonException("El JSON proporcionado no es una lista");
        return listFromJSON((ArrayNode) node, clazz);
    }

    /**
     * Converts a JSON array node into the provided Object using {@link ObjectMapper#readValue(String, Class)}
     * 
     * @param <T> Destination class type
     * @param input JSON input
     * @param clazz Class object representing the destination class
     * @return list with new instances of the desired class
     * @throws JsonException If there's an error processing JSON. The provided JSON does not match the desired object
     */
    public static <T>List<T> listFromJSON(ArrayNode input, Class<T> clazz) throws JsonException {
        Iterator<JsonNode> iterator = input.elements();
        List<T> output = new ArrayList<T>();
        while (iterator.hasNext()) {
            JsonNode nodo = iterator.next();
            T elemento = fromJSON(nodo, clazz);
            output.add(elemento);
        }
        return output;
    }

    /**
     * Converts the given object into it's JSON representation
     * 
     * @param o the instance to convert to JSON
     * @return JSON representation of the object as String
     * @throws JsonException If there's an error converting to JSON
     */
    public static String toJSON(Object o) throws JsonException {
        return toJSON(o, false);
    }

    /**
     * Converts the given object into it's JSON representation
     * 
     * @param o the instance to convert to JSON
     * @param pretty if true, will use pretty printer (adds newlines and tabulation to output=
     * @return JSON representation of the object as String
     * @throws JsonException If there's an error converting to JSON
     */
    public static String toJSON(Object o, boolean pretty) throws JsonException {
        ObjectMapper mapper = new ObjectMapper();
        try {
            // Java objects to JSON string - pretty-print
            String str;
            if(pretty) str = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(o);
            else str = mapper.writeValueAsString(o);
            return str;
        } catch (JsonProcessingException e) {
            throw new JsonException("Error processing JSON", e);
        }catch (RuntimeException e) {
            throw new JsonException("Runtime exception parsing JSON", e);
        }
    }

    /**
     * Converts the given object into it's JSON representation
     * 
     * @param o the instance to convert to JSON
     * @return JSON representation of the object as JsonNode
     * @throws JsonException If there's an error converting to JSON
     */
    public static JsonNode toJSONNode(Object o) throws JsonException {
        if(o instanceof JsonNode) return (JsonNode) o;
        String str = toJSON(o, false);
        return stringToNode(str);
    }
}
