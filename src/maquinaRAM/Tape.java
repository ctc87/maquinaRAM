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
	
	public String toString() {
		String str = "tape:";
		for (int i = 0; i < tape.size(); i++) {
			str += tape.get(i) + ", ";
		}
		return str;
	}
}
