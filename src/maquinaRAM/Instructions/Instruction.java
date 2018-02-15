package maquinaRAM.Instructions;

import maquinaRAM.operands.Operand;

public class Instruction extends BaseInstruction {
	Operand oper;
	
	public Instruction(String InstructionName, Operand oper) {
		super(InstructionName);
		this.oper = oper;
	}
	
	@Override
	public String toString() {
		
		return super.toString() + oper.toString();
	}

	/**
	 * @return the oper
	 */
	public Operand getOper() {
		return oper;
	}
	/**
	 * @param oper the oper to set
	 */
	public void setOper(Operand oper) {
		this.oper = oper;
	}

}
