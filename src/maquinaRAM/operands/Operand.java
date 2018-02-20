package maquinaRAM.operands;

public class Operand {
	String operandClass;
	int oper;
	
	public Operand(String operandClass, int oper) {
		this.oper = oper;
		this.operandClass = operandClass;
	}

	
	@Override
	public String toString() {
		
		return " { operand: " + oper + " ||  class: " + printClass(operandClass) + " }";
	}
	
	private String printClass(String operand){
		String str = "";
		if(operand.equals("direct"))
			str = "Direct addressing";
		if(operand.equals("indirect"))
			str = "IN-Direct addressing";
		if(operand.equals("constant"))
			str = "Constant addressing";
		return str;
	}
	
	/**
	 * @return the operandClass
	 */
	public String getOperandClass() {
		return operandClass;
	}

	/**
	 * @param operandClass the operandClass to set
	 */
	public void setOperandClass(String operandClass) {
		this.operandClass = operandClass;
	}

	/**
	 * @return the oper
	 */
	public int getOper() {
		return oper;
	}

	/**
	 * @param oper the oper to set
	 */
	public void setOper(int oper) {
		this.oper = oper;
	}

}
