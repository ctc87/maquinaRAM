package maquinaRAM.Instructions;


/**
 * @author Carlos Troyano Carmona
 *
 */
public class BaseInstruction {
	String InstructionName;	
	
	public BaseInstruction(String InstructionName) {
		this.InstructionName = InstructionName;
	}
	
	@Override
	public String toString() {
		return InstructionName + " " ;
	}
	
	/**
	 * @return the instructionName
	 */
	public String getInstructionName() {
		return InstructionName;
	}
	/**
	 * @param instructionName the instructionName to set
	 */
	public void setInstructionName(String instructionName) {
		InstructionName = instructionName;
	}


}
