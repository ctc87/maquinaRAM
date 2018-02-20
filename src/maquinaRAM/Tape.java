package maquinaRAM;

import java.util.ArrayList;

public class Tape {
	
	ArrayList<Integer> tape;
	int pointer;
	
	public Tape() {
		tape = new ArrayList<Integer>();
		pointer = 0;
	}
	
	public Tape(ArrayList<Integer> tape) {
		pointer = 0;
		this.tape = tape;
	}
	
	public int get() {
		return tape.get(pointer++);
	}
	
	public void add(int value) {
		tape.add(value);
	}
	
	public int size() {
		return tape.size();
	}
	
	public String toString() {
		String str = "Tape:";
		int i;
		for (i = 0; i < tape.size(); i++) {
			String quote =  i == (tape.size() - 1) ? "" : ",";
			str += tape.get(i) + quote;
		}
		if(i == 0) str += "Void"; 
		return str;
	}
}
