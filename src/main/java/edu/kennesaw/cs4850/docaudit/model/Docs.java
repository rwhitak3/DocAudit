package edu.kennesaw.cs4850.docaudit.model;

import com.esotericsoftware.kryo.serializers.CollectionSerializer;
import com.esotericsoftware.kryo.serializers.DefaultSerializers;
import com.esotericsoftware.kryo.serializers.MapSerializer;
import edu.kennesaw.cs4850.docaudit.model.Document;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Docs {
    @MapSerializer.BindMap(
            valueSerializer = DefaultSerializers.StringSerializer.class,
            keySerializer = DefaultSerializers.StringSerializer.class,
            valueClass = String.class,
            keyClass = String.class,
            keysCanBeNull = false)
    private Map<String, String> docs;
    private List<Document> documentList;

    public Map<String, String> getDocs() {
        return docs;
    }

    public void setDocs(Map<String, String> docs) {
        this.docs = docs;
    }

    public List<Document> getDocumentList() {
        return documentList;
    }

    public void setDocumentList(List<Document> documentList) {
        this.documentList = documentList;
    }
}
