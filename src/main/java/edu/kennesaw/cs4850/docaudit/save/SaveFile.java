package edu.kennesaw.cs4850.docaudit.save;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.serializers.MapSerializer;
import edu.kennesaw.cs4850.docaudit.Docs;
import edu.kennesaw.cs4850.docaudit.model.Document;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Map;

public class SaveFile {
    private static String fileName = "/tmp/DocAudi/out.file";
    private static org.slf4j.Logger logger = LoggerFactory.getLogger(SaveFile.class);
    private static Kryo kryo;

    public static void init() {
        kryo = new Kryo();
        /*MapSerializer serializer = new MapSerializer();
        kryo.register(HashMap.class, serializer);
        kryo.register(String.class);
        kryo.register(Docs.class);
        kryo.register(MapSerializer.class);
        serializer.setKeyClass(String.class, kryo.getSerializer(String.class));
        serializer.setValueClass(String.class, kryo.getSerializer(String.class));
        serializer.setKeysCanBeNull(false);*/
    }

    public static boolean save(Docs strings) {
        try {
            //kryo.register(Docs.class);
            init();
            Output output = new Output(new FileOutputStream(fileName));
            kryo.writeObject(output,strings);
            output.close();
            return true;

        } catch (Exception ex) {
            logger.error("Issue with saving", ex);
            return false;
        }
    }

    public static Docs load() {
        try {
            //kryo.register(Docs.class);
            init();
            Input input = new Input(new FileInputStream(fileName));
            Docs result = (Docs) kryo.readObject(input, Docs.class);
            input.close();
            if (result.getDocumentList() != null ) {
                for ( Document d : result.getDocumentList() ) {
                    d.init();
                }
            }
            return result;
        } catch (Exception ex) {
            logger.error("Issue with Loading", ex);
            return null;
        }
    }


}
