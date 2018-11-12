package qbe;

import java.util.Map.Entry;
import java.util.*;

import com.google.gson.JsonArray; 
import com.google.gson.JsonElement; 
import com.google.gson.JsonObject; 
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;


public class Trie {
	private TrieNode root = new TrieNode();
    
    private JsonParser parser = new JsonParser();  
	
	public TrieNode getRoot(){
		return root;
	}
	
	public void add(String jsonString, TrieNode trieNode, int jsonId) {
		JsonElement rootElement = parser.parse(jsonString); 
		
		add(rootElement, trieNode, jsonId);
	}
	
	public Set<Integer> get(String jsonString, TrieNode trieNode) {
		
		JsonElement rootElement = parser.parse(jsonString);
		
		return get(rootElement, trieNode);
	}
	
	public void delete(String jsonString, TrieNode trieNode) {
		JsonElement rootElement = parser.parse(jsonString);
		delete(rootElement, trieNode);
	}
	
	//handle json elements
	public void add(JsonElement jsonElement, TrieNode trieNode, int jsonId) {
		if(jsonElement==null) {return;}
		if(jsonElement.isJsonObject()) {
			trieNode.setKeyType("Key");
			
			JsonObject jsonObject = jsonElement.getAsJsonObject();
			
			Set<Entry<String, JsonElement>> jsonEntrySet = jsonObject.entrySet();
			for(Entry<String, JsonElement> jsonEntry : jsonEntrySet) {
				String key = jsonEntry.getKey();
				String keyType = "Key"; //default
				
				Set<TrieNode> childrenTrieNodes = trieNode.getChildren();
				boolean exists = false;
				for(TrieNode childTrieNode : childrenTrieNodes) { //not fast, need optimization
					if(childTrieNode.getKey().equals(key) && childTrieNode.getKeyType().equals(keyType)) {
						exists = true;
						add(jsonEntry.getValue(), childTrieNode, jsonId);
					}
				}
				if(!exists) {
					TrieNode newChildTrieNode = new TrieNode();
					childrenTrieNodes.add(newChildTrieNode);
					newChildTrieNode.setKey(key);
					newChildTrieNode.setKeyType(keyType);
					add(jsonEntry.getValue(), newChildTrieNode, jsonId);					
				}
			}
		}else if(jsonElement.isJsonArray()) {
			JsonArray jsonArray = jsonElement.getAsJsonArray();
			trieNode.setKeyType("Key");
			
			for(JsonElement element : jsonArray) {
				add(element, trieNode, jsonId);
			}
		}else if(jsonElement.isJsonPrimitive()) {
		    JsonPrimitive primitive = jsonElement.getAsJsonPrimitive();
		    
		    String key;
		    String keyType;
		    
		    if (primitive.isNumber()) {
		    	key = String.valueOf(primitive.getAsNumber());
		    	keyType = "Number";
		    } else if (primitive.isBoolean()) {
		    	key = Boolean.toString(primitive.getAsBoolean());
		    	keyType = "Boolean";
		    } else if (primitive.isString()) {
		    	key = primitive.getAsString();
		    	keyType = "String";
		    } else {
		    	System.out.println("wrong primative: " + primitive);
		    	throw new AssertionError();
		    }
		    
			Set<TrieNode> childrenTrieNodes = trieNode.getChildren();
			boolean exists = false;
			for(TrieNode childTrieNode : childrenTrieNodes) { //not fast, need optimization
				if(childTrieNode.getKey().equals(key) && childTrieNode.getKeyType().equals(keyType)) {
					childTrieNode.getJsonIds().add(jsonId);
					exists = true;
					break;
				}
			}
			if(!exists) {
				TrieNode newChildTrieNode = new TrieNode();
				childrenTrieNodes.add(newChildTrieNode);
				newChildTrieNode.setKey(key);

				newChildTrieNode.setLeaf(true);
				newChildTrieNode.setKeyType(keyType);
				newChildTrieNode.getJsonIds().add(jsonId);
				
			}
		}
	}
	
	public Set<Integer> get(JsonElement jsonElement, TrieNode trieNode) {
		if(jsonElement.isJsonObject()) {			
			JsonObject jsonObject = jsonElement.getAsJsonObject();
			
			Set<Entry<String, JsonElement>> jsonEntrySet = jsonObject.entrySet();
			List<Entry<String, JsonElement>> jsonEntryList = new ArrayList<>(jsonEntrySet);
			Set<Integer> set = new HashSet<Integer>();
			
			if(jsonEntryList.size()==0) {
				return Solution.getIdToJsonMap().keySet();//TODO not sure 
			}else if (jsonEntryList.size() == 1) {
				Entry<String, JsonElement> firstEntry = jsonEntryList.get(0);
				for(TrieNode childTrieNode : trieNode.getChildren()) { //TODO optimize
					if(childTrieNode.getKey().equals(firstEntry.getKey())) {
						set.addAll( get(firstEntry.getValue(), childTrieNode));
					}
				}
				return set;
			}else {
				Entry<String, JsonElement> firstEntry = jsonEntryList.get(0);
				for(TrieNode childTrieNode : trieNode.getChildren()) {
					if(childTrieNode.getKey().equals(firstEntry.getKey())) {
						set.addAll( get(firstEntry.getValue(), childTrieNode));
					}
				}
				
				for(int i=1; i<jsonEntryList.size(); i++) {
					Entry<String, JsonElement> entry = jsonEntryList.get(i);
					
					boolean found = false;
					for(TrieNode childTrieNode: trieNode.getChildren()) {
						if(childTrieNode.getKey().equals(entry.getKey())) {
							found = true;
							set.retainAll( get(entry.getValue(), childTrieNode));
						}
					}
					
					if(!found) {
						set.clear();
						return set;
					}
				}
				
				return set;
			}
			
		}else if(jsonElement.isJsonArray()) {
			JsonArray jsonArray = jsonElement.getAsJsonArray();			
			Set<Integer> set = new HashSet<Integer>();
			
			int arraySize = jsonArray.size();
			if(arraySize==0) {
				//TODO return all ids;
				return Solution.getIdToJsonMap().keySet();
			}else if(arraySize == 1) {
				return get(jsonArray.get(0), trieNode);
			}else {
				set = get(jsonArray.get(0), trieNode);
				
				for (int i=1;i<jsonArray.size(); i++) {
					set.retainAll(get(jsonArray.get(i), trieNode));
				}
				
				return set;
			}
			
		}else if(jsonElement.isJsonPrimitive()) {
		    JsonPrimitive primitive = jsonElement.getAsJsonPrimitive();
		    
		    String key;
		    String keyType;
		    
		    if (primitive.isNumber()) {
		    	key = String.valueOf(primitive.getAsNumber());
		    	keyType = "Number";
		    } else if (primitive.isBoolean()) {
		    	key = Boolean.toString(primitive.getAsBoolean());
		    	keyType = "Boolean";
		    } else if (primitive.isString()) {
		    	key = primitive.getAsString();
		    	keyType = "String";
		    } else {
		    	System.out.println("wrong primative: " + primitive);
		    	throw new AssertionError();
		    }
		    
			Set<TrieNode> childrenTrieNodes = trieNode.getChildren();
			for(TrieNode childTrieNode : childrenTrieNodes) { //not fast, need optimization
				if(childTrieNode.getKey().equals(key) && childTrieNode.getKeyType().equals(keyType)) {
					return childTrieNode.getJsonIds();
				}
			}
			return new HashSet<Integer>();
		} else {
			System.out.println("wrong jsonElement type: " + jsonElement);
			return new HashSet<Integer>();
		}
	}
	
	public void delete(JsonElement jsonElement, TrieNode trieNode) {
		if(jsonElement==null) {return;}
		
		Set<Integer> jsonIds = get(jsonElement, trieNode);
		for(int jsonId : jsonIds) {
			Solution.getIdToJsonMap().remove(jsonId);
		}
	}
	
	
	public String toString(){
		return root.toString();
	}
}
