package maquinaRAM.Instructions;

public class Controlnstruction extends BaseInstruction {
	public Controlnstruction(String InstructionName, String tag) {
		super(InstructionName);
		this.tag = tag;
	}

	
	@Override
	public String toString() {
		return super.toString() + " ( Tag: " + tag + " )"; 
	}

	String tag;

	/**
	 * @return the tag
	 */
	public String getTag() {
		return tag;
	}

	/**
	 * @param tag the tag to set
	 */
	public void setTag(String tag) {
		this.tag = tag;
	}
	
}
