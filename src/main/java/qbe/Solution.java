package qbe;

import java.util.*;
import java.io.*; 

public class Solution {
	
	private static Trie trie = new Trie();
	
	private static Map<Integer, String> idToJsonMap = new HashMap<>();
	
	public static Map<Integer, String> getIdToJsonMap() {
		return idToJsonMap;
	}

	public static void main(String[] args) {
		List<String> strs = new ArrayList<>();

		File file = new File("src/main/resources/test.txt"); 
		BufferedReader br;
		try {
			br = new BufferedReader( new FileReader(file));
			String st; 		
			while ((st = br.readLine()) != null) {
				strs.add(st);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 

		for(int i=0; i<strs.size(); i++) {
			handleOps(strs.get(i), i); //TODO handle jsonId
		}
		
		System.out.println(trie.toString());

	}
	
	private static void handleOps(String str, int jsonId) {
		
		int spaceIdx = str.indexOf(" ");
		String op = str.substring(0, spaceIdx);
		String json = str.substring(spaceIdx+1);
		
		System.out.println(op);
		System.out.println(json);
		
		switch(op){
			case "add":
				trie.add(json, trie.getRoot(), jsonId);
				idToJsonMap.put(jsonId, json);
				break;
			case "get":
				List<Integer> ids = trie.get(json, trie.getRoot());
				for(int id : ids) {
					if(idToJsonMap.containsKey(id)) {
						System.out.println(idToJsonMap.get(id)); //TODO change output method
					}
				}
				break;
			case "delete":
				trie.delete(json, trie.getRoot());
				break;
			default:
				System.out.println("wrong operation: " + str );
		}
	}
	
	
	
	
}
	