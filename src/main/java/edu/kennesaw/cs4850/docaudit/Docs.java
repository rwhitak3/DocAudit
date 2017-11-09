package edu.kennesaw.cs4850.docaudit;

import com.esotericsoftware.kryo.serializers.DefaultSerializers;
import com.esotericsoftware.kryo.serializers.MapSerializer;

import java.util.HashMap;
import java.util.Map;

public class Docs {
    @MapSerializer.BindMap(
            valueSerializer = DefaultSerializers.StringSerializer.class,
            keySerializer = DefaultSerializers.StringSerializer.class,
            valueClass = String.class,
            keyClass = String.class,
            keysCanBeNull = false)
    public Map<String, String> docs;


}
