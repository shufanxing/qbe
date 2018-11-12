package qbe;


import java.util.HashSet;
import java.util.Set;

public class TrieNode {
    private String key = null;

    private boolean isLeaf = false;

    private String keyType = null; //used only when isLeaf is true;
    private Set<Integer> jsonIds = new HashSet<>(); //used only when isLeaf is true
    
    private Set<TrieNode> children = new HashSet<>(); //used only when isLeaf is false;
    private Set<String> childrenKeys = new HashSet<>(); //used only when isLeaf is false;
    //TODO use childrenKeys
    //TODO create sets only when it's necessary
    
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public boolean isLeaf() {
		return isLeaf;
	}
	public void setLeaf(boolean isLeaf) {
		this.isLeaf = isLeaf;
	}
	public Set<TrieNode> getChildren() {
		return children;
	}
	public void setChildren(Set<TrieNode> children) {
		this.children = children;
	}
	public Set<Integer> getJsonIds() {
		return jsonIds;
	}
	public void setJsonIds(Set<Integer> jsonIds) {
		this.jsonIds = jsonIds;
	}
	public String getKeyType() {
		return keyType;
	}
	public void setKeyType(String keyType) {
		this.keyType = keyType;
	}
    
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("trieNode key: " + key + "\n");
		if(isLeaf) {
			sb.append("ids: ");
			for(int id: jsonIds) {
				sb.append(id + " ");
			}
			sb.append("\n");
		}else{
			for(TrieNode child : children) {
				sb.append(child.toString());
			}
		}
		return sb.toString();
	}
}