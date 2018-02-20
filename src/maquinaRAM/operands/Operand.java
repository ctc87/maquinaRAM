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
		return  printClass(operandClass) + oper;
	}
	
	private String printClass(String operand){
		String str = "";
		if(operand.equals("direct"))
			str = "";
		if(operand.equals("indirect"))
			str = "*";
		if(operand.equals("constant"))
			str = "=";
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
