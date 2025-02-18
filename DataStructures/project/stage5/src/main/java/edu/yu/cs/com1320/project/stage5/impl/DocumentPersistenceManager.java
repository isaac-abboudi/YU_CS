package edu.yu.cs.com1320.project.stage5.impl;

import com.google.gson.*;
import edu.yu.cs.com1320.project.stage5.Document;
import edu.yu.cs.com1320.project.stage5.PersistenceManager;

import java.io.*;
import java.lang.reflect.Type;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import javax.print.Doc;
import jakarta.xml.bind.DatatypeConverter;

/**
 * created by the document store and given to the BTree via a call to BTree.setPersistenceManager
 */
public class DocumentPersistenceManager implements PersistenceManager<URI, Document> {

    private class Serializer implements JsonSerializer<Document>{

        @Override
        public JsonElement serialize(Document document, Type type, JsonSerializationContext context) {
            JsonObject jason = new JsonObject();
            jason.addProperty("URI", document.getKey().toString());
            String mapAsString = serializeMap(document.getWordMap());
            if (document.getDocumentTxt() != null){
                jason.addProperty("text", document.getDocumentTxt());
                jason.addProperty("wordMap", mapAsString);
            } else {
                String base64Encoded = DatatypeConverter.printBase64Binary(document.getDocumentBinaryData());
                jason.addProperty("binaryData", base64Encoded);
            }
            return jason;
        }

        private String serializeMap(Map wordMap){
            String mapAsString = null;
            try {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ObjectOutputStream oos = new ObjectOutputStream(baos);
                oos.writeObject(wordMap);
                oos.close();
                mapAsString = Base64.getEncoder().encodeToString(baos.toByteArray());
            } catch (Exception e){
                e.printStackTrace();
            }
            return mapAsString;
        }

    }

    private class Deserializer implements JsonDeserializer<DocumentImpl>{

        @Override
        public DocumentImpl deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext)
                throws JsonParseException {

            JsonObject jason = jsonElement.getAsJsonObject();
            JsonElement jsonURI = jason.get("uri");
            //String uriAsString = jsonURI.getAsString(); // why is jsonURI null???
            URI uri = URI.create(jsonURI.getAsString());
            JsonElement docData;
            if (jason.has("text")){
                docData = jason.get("text");
                String text = docData.getAsString();
                //JsonElement wordMap = jason.get("wordCount");
                //Map<String, Integer> wordMap = deserializeMap(jason.get("wordCount").getAsString());
                return new DocumentImpl(uri, text, null);
            } else {
                docData = jason.get("binaryData");
                byte[] binaryData = DatatypeConverter.parseBase64Binary(docData.getAsString());
                return new DocumentImpl(uri, binaryData);
            }
        }

//        private Map<String, Integer> deserializeMap(String mapAsString){
//            Object mapAsMap = null;
//            try{
//                byte[] data = Base64.getDecoder().decode(mapAsString);
//                ObjectInputStream ois = new ObjectInputStream(
//                        new ByteArrayInputStream(data));
//                mapAsMap = ois.readObject();
//                ois.close();
//            } catch (Exception e){
//                e.printStackTrace();
//            }
//            return (Map<String, Integer>) mapAsMap;
//        }
    }

    private File baseDir;

    public DocumentPersistenceManager(File baseDir){

        if (baseDir == null){
            this.baseDir = new File(System.getProperty("user.dir"));
        } else {
            this.baseDir = baseDir;
        }
        try {
            Path path = this.baseDir.toPath();
            Files.createDirectories(path);
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    @Override
    public void serialize(URI uri, Document val) throws IOException { // return pointer to memory?

        Gson gson = new GsonBuilder().registerTypeAdapter(Document.class,
                new Serializer()).setPrettyPrinting().create();
        String gsonString = gson.toJson(val);
        try {
            String filePath = uri.getSchemeSpecificPart();
            filePath += ".json";
            File file = new File(baseDir, filePath);
            Files.createDirectories(Paths.get(file.getParent()));
            boolean created = file.createNewFile();
            FileWriter writer = new FileWriter(file);
            writer.write(gsonString);
            writer.close();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public Document deserialize(URI uri) throws IOException {
        Gson gson = new GsonBuilder().registerTypeAdapter(DocumentImpl.class,
                new Deserializer()).setPrettyPrinting().create();
        DocumentImpl doc = null;
        try{
            //gotta check first if the uri exists
            String filePath = uri.getSchemeSpecificPart();
            filePath += ".json";
            File file = new File(baseDir, filePath);
            FileReader fileReader = new FileReader(file);
            doc = gson.fromJson(fileReader, DocumentImpl.class);
            file.delete();
        } catch (Exception e){
            e.printStackTrace();
        }
        return doc;
    }

    @Override
    public boolean delete(URI uri) throws IOException {
        boolean deleted = false;
        try{
            String filePath = uri.getSchemeSpecificPart();
            filePath += ".json";
            File file = new File(baseDir, filePath);
            deleted = file.delete();
        } catch (Exception e){
            e.printStackTrace();
            //throw new IOException(); // is this correct handling?
        }
        return deleted;
    }


}