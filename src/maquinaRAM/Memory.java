package maquinaRAM;

import java.util.ArrayList;

public class Memory<T> {


	ArrayList<Register<T>> memory;
	public Memory() {
		memory = new ArrayList<Register<T>>();
	}


	public Memory(int maxRegNumbers, T initVal) {
		memory = new ArrayList<Register<T>>();
		for (int i = 0; i < maxRegNumbers + 1; i++) {
			memory.add(new Register<T>(initVal, ""));
		}
		// System.out.println(toString());
	}

	int pointer;
	
	/**
	 * @return the memory
	 */
	public ArrayList<Register<T>> getMemory() {
		return memory;
	}
	/**
	 * @param memory the memory to set
	 */
	public void setMemory(ArrayList<Register<T>> memory) {
		this.memory = memory;
	}
	/**
	 * @return the pointer
	 */
	public int getPointer() {
		return pointer;
	}
	/**
	 * @param pointer the pointer to set
	 */
	public void setPointer(int pointer) {
		this.pointer = pointer;
	}
	
	int returnDirByTag(String tag) {
		boolean found = false;
		int i = 0;
		while(!found) {
			found = memory.get(i).getRegisterlabel().equals(tag);
			//String str = memory.get(i).getRegisterlabel().isEmpty() ? "-Sin etiqueta-" : memory.get(i).getRegisterlabel();
			if(!found)i++;
		}
		return i;
	}
	
	public String toString() {
		String str = "Memory:\n ";
		for (int i = 0; i < memory.size(); i++) {
			str += "R" + i + ": ";
			str += memory.get(i).toString() + "\n ";
		}
		return str;
	}


	public void resize(int pos) {
		for (int i = getMemory().size() - 1; i <= pos; i++) {
			getMemory().add(new Register<T>(null, ""));
		}
		
	}
	
}
