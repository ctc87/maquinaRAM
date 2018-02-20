package maquinaRAM;


import maquinaRAM.Instructions.BaseInstruction;
import maquinaRAM.Instructions.Controlnstruction;
import maquinaRAM.Instructions.Instruction;
import maquinaRAM.operands.Operand;

/**
 * <h1>ALUCU</h1> ALUCU 
 * 
 * @author Carlos Troyano Carmona
 * @version 1.0
 * @date 1 mar. 2017
 * @see System
 */
/**
 * @author root
 *
 */
/**
 * @author root
 *
 */
public class ALUCU {

	/**
	 * The abstract connection to the data memory.
	 */
	Memory<Integer> dataMemoryAcces;

	/**
	 * The abstract connection to the program memory.
	 */
	Memory<BaseInstruction> programMemoryAcces;
	
	Register<Integer> R0;

	/**
	 * The abstract input unit.
	 */
	Tape inputTapeAcces;

	/**
	 * The abstract output unit.
	 */
	Tape outputTapeAcces;

	/**
	 * The instruction pointer(Have the number of the next instruction to execute).
	 */
	int IP;

	/**
	 * The final state representation.
	 */
	boolean halt;
	
	/**
	 * Constructor of the class instance the initial parameters.
	 * @param dataMemory Read data memory.
	 * @param programMemory Read program memory.
	 * @param inputTape 
	 * @param outputTape
	 */
	public ALUCU(
			Memory<Integer> dataMemory, 
			Memory<BaseInstruction> programMemory, 
			Tape inputTape, 
			Tape outputTape
			) 
	{
		dataMemoryAcces = dataMemory;
		programMemoryAcces = programMemory;
		inputTapeAcces = inputTape;
		outputTapeAcces = outputTape;
		R0 = dataMemory.getMemory().get(0);
		IP = 0;
		halt = false;
		
	}
	
	public void execution()  {
		System.out.println(programMemoryAcces.toString());
		int i =0;
		while(!halt) {
			System.out.println("Leyendo en R:" + IP);
			readInstruction(programMemoryAcces.getMemory().get(IP).getData());
			System.out.println("tape out: " + outputTapeAcces);
		}
	}
	
	/**
	 * lEEMOS LA INSTRUCCION vamos a ver como la ejecuta
	 * @param instruction
	 */
	public void readInstruction(BaseInstruction instruction) {
		String instructionName = instruction.getInstructionName().toLowerCase();
		System.out.println("Nombre de la instruccion leida "  + instructionName );
		/**
		 * Es una instruccion base?
		 */
		if(instruction instanceof Instruction) {
			Instruction instructionN = (Instruction)(instruction);
			switch (instructionName) {
			case "load":
				load(instructionN.getOper());
			break;
			case "store":
				store(instructionN.getOper());
			break;

			case "add":
				add(instructionN.getOper());
				
			break;

			case "sub":
				sub(instructionN.getOper());
			break;

			case "mul":
				mul(instructionN.getOper());
			break;

			case "div":
				div(instructionN.getOper());
			break;

			case "read":
				System.out.println("EFECTIVAMENTE LEE READ");
				read(instructionN.getOper());
			break;

			case "write":
				write(instructionN.getOper());
			break;

			case "halt":
				halt();
			break;

			default:
				break;
			}
			IP++;

			/**
			 * Es una instruccion de control
			 */
		} else if( instruction instanceof Controlnstruction) {
			Controlnstruction instructionC = (Controlnstruction)(instruction);
			switch (instructionName) {
			case "jump":
				jump(instructionC.getTag());
			break;
			case "jzero":
				jzero(instructionC.getTag());
			break;
			case "jgtz":
				jgtz(instructionC.getTag());
			break;
			}
		};
	};
	
	public int resolveOperand(Operand op) {
		int value = -10;
		System.out.println("Resolvidneod oprando " + op.getOperandClass());
		if(op.getOperandClass().equals("constant")) {
			value =  op.getOper();
		} else if(op.getOperandClass().equals("direct")) {
			value =  directAccesRegister(op.getOper()).getData();
		} else if(op.getOperandClass().equals("indirect")) {
			value =  indirectAccesRegister(op.getOper()).getData();
		}
		System.out.println("Valor dado: " + value);
		
		return value;
	}

	
	public Register<Integer> resolveOperandReg(Operand op) {
		Register<Integer> aux = new Register<Integer>(null, null);
		if(op.getOperandClass().equals("direct")) {
			System.out.println("directo");
			aux =  directAccesRegister(op.getOper());
		} else if(op.getOperandClass().equals("indirect")) {
			aux =  indirectAccesRegister(op.getOper());
		}
		return aux;
	}
	
	public Register<Integer> directAccesRegister(int pos) {
		System.out.println(dataMemoryAcces);
		if(pos >= dataMemoryAcces.getMemory().size()) {
			System.out.println("MAyor");
			dataMemoryAcces.resize(pos);
		}
		return dataMemoryAcces.getMemory().get(pos);
	}
	
	public Register<Integer> indirectAccesRegister(int pos) {
		if(pos >= dataMemoryAcces.getMemory().size()) {
			System.out.println("MAyor");
			dataMemoryAcces.resize(pos);
		}
		int regDirection =  dataMemoryAcces.getMemory().get(pos).getData();
		if(regDirection >= dataMemoryAcces.getMemory().size()) {
			System.out.println("MAyor");
			dataMemoryAcces.resize(regDirection);
			
		}
		return dataMemoryAcces.getMemory().get(regDirection);
	}
	
	public void load(Operand op) {
		System.out.println("load: "+ op);
		R0.setData(resolveOperand(op));
	}
	
	public void store(Operand op) {
		resolveOperandReg(op).setData(R0.getData());
	}
	
	public void add(Operand op) {
		R0.setData(resolveOperand(op) + R0.getData());
	}
	
	public void sub(Operand op) {
		R0.setData(R0.getData() - resolveOperand(op));
	}
	
	public void mul(Operand op) {
		System.out.println(R0.getData() +"X"+ resolveOperand(op));
		R0.setData(R0.getData() * resolveOperand(op));
	}
	
	public void div(Operand op) {
		R0.setData(R0.getData() / resolveOperand(op));
	}
	
	public void read(Operand op) {
		resolveOperandReg(op).setData(inputTapeAcces.get());
		System.out.println("read operando :"+ resolveOperand(op));
	}
	
	public void write(Operand op) {
		System.out.println("write: " + resolveOperand(op));
		outputTapeAcces.add(resolveOperand(op));
	}
	
	public void jump(String tag) {
		IP = programMemoryAcces.returnDirByTag(tag);
	}
	
	public void jzero(String tag) {
		if(R0.getData() == 0) {
			jump(tag);
		} else {
			IP++;
		}
	}
	
	public void jgtz(String tag) {
		if(R0.getData() > 0) {
			jump(tag);
		} else {
			IP++;
		}
	}
	
	public void halt() {
		halt = true;
	}

	
	
}
