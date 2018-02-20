package maquinaRAM;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Pattern;

import maquinaRAM.Instructions.BaseInstruction;
import maquinaRAM.Instructions.Controlnstruction;
import maquinaRAM.Instructions.Instruction;
import maquinaRAM.exceptions.SintaxError;
import maquinaRAM.operands.Operand;

public class RAM_Machine {
	Memory<Integer> dataMemory;
	Memory<BaseInstruction> programMemory;
	Tape inputTape;
	Tape outputTape;
	HashMap<String, String> files;
	ALUCU alucu;

	public static void main(String[] args) {
		HashMap<String, String> files = new HashMap<String, String>();
		files.put("program", args[0]);
		files.put("input", args[1]);
		files.put("output", args[2]);
		RAM_Machine ramM;
		try {
			ramM = new RAM_Machine(files);
			ramM.alucu.execution();
		} catch (SintaxError e) {
			e.printStackTrace();
		}
		
	}

	public RAM_Machine(HashMap<String, String> files) throws SintaxError {
		this.files = files;
		initializeTapes(files.get("input"));
		int maxRegNumbers = 1;
		try {
				maxRegNumbers = initializeProgram(files.get("program"));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		dataMemory = new Memory<Integer>(maxRegNumbers, 0);
		alucu = new ALUCU(dataMemory, programMemory, inputTape, outputTape);

	}

	public int initializeProgram(String programFileName) throws FileNotFoundException, IOException, SintaxError {
		ArrayList<String> programStringArray = new ArrayList<String>();
		String str;
		FileReader f = new FileReader(programFileName);
		BufferedReader b = new BufferedReader(f);
		while ((str = b.readLine()) != null) {
			programStringArray.add(str);
		}
		b.close();
		return createProgramMemory(programStringArray);
	}

	public int createProgramMemory(ArrayList<String> programStringArray) throws SintaxError {
		int maxRegNumber = 0;
		programMemory = new Memory<BaseInstruction>();
		for (int i = 0; i < programStringArray.size(); i++) {
			if (isNotEmptyLine(programStringArray.get(i))) {
				if (isNotComment(programStringArray.get(i))) {
					String actualLine = programStringArray.get(i).trim();
					String tagReg = "";
					BaseInstruction ins = new BaseInstruction("");
					System.out.println("linea trim: " + actualLine.trim());
					if (itsTaggedRegister(actualLine)) {
						tagReg = actualLine.split(":")[0];
						System.out.println("et: " + tagReg );
						actualLine = actualLine.split(":")[1].trim();
					}
					if (itsControlInstruction(actualLine)) {
						System.out.println("CONSTROL!!!!!!!!!!");
						ins = new Controlnstruction(actualLine.split("\\p{Blank}+")[0], actualLine.split("\\p{Blank}+")[1]);
					} else {
						String operandClass = "";
						String operand = "";
						if (!Pattern.matches("[Hh][Aa][Ll][Tt]", actualLine)) {
							operand = actualLine.trim().split("\\p{Blank}+")[actualLine.trim().split("\\p{Blank}+").length - 1];
						} else {
							operand = "0";
						}
						System.out.println("operand:" + operand);
						if (itsConstantOp(operand)) {
							operandClass = "constant";
							System.out.println(operand.split("=")[1]);
							operand = operand.split("=")[1];
					
						} else if (itsIndirectOp(operand)) {
							operandClass = "indirect";
							System.out.println(operand);
							operand = operand.replaceAll("\\*", "*"); 
							System.out.println(operand);
							operand = operand.split("\\*")[1];
							System.out.println(operand);
							maxRegNumber = Integer.parseInt(operand) > maxRegNumber ? Integer.parseInt(operand) : maxRegNumber;
						} else if(itsDirectOp(operand)){
							operandClass = "direct";
							maxRegNumber = Integer.parseInt(operand) > maxRegNumber ? Integer.parseInt(operand) : maxRegNumber;
						} 
						else {
							throw new SintaxError((i+1), "Not valid Operand", files.get("input"));
						}
						if (actualLine.equals("halt")) {
							operand = "-1";
						}
						System.out.println("OPERAND:  " + operand);
						Operand op = new Operand(operandClass, Integer.parseInt(operand));
						ins = new Instruction(actualLine.split("\\p{Blank}+")[0], op);
						boolean sintaxStoreRead = ins.getInstructionName().toLowerCase().equals("store") || ins.getInstructionName().toLowerCase().equals("read");
						boolean sintaxWriteRead = ins.getInstructionName().toLowerCase().equals("write") || ins.getInstructionName().toLowerCase().equals("read");
		
						if(sintaxStoreRead) {
							if(((Instruction) ins).getOper().getOperandClass().equals("constant"))
								throw new SintaxError((i+1), "Incorrect constant operand for " + ins.getInstructionName(), files.get("program"));
						} else if(sintaxWriteRead)  {
							if(((Instruction) ins).getOper().getOper() == 0 && ((Instruction) ins).getOper().getOperandClass().equals("direct") )
								throw new SintaxError((i+1), "Incorrect reg value, " + ins.getInstructionName() + " can' t operate in R0.", files.get("program"));
						}
					}
					System.out.println(ins);
					Register<BaseInstruction> reg = new Register<BaseInstruction>(ins, tagReg);
					programMemory.getMemory().add(reg);
				}
			}
		}
		System.out.println(programMemory);
		return maxRegNumber;
	}

	private boolean itsConstantOp(String str) {
		return Pattern.matches("=[0-9]+", str);
	}

	private boolean itsDirectOp(String str) {
		return Pattern.matches("[0-9]+", str);
	}

	private boolean itsIndirectOp(String str) {
		return Pattern.matches("\\*[0-9]+", str);
	}

	private boolean isNotEmptyLine(String str) {
		return !str.trim().isEmpty();
	}

	private boolean isNotComment(String str) {
		return str.charAt(0) != '#';
	}

	private boolean itsTaggedRegister(String str) {
		System.out.println("tiene etiqueta? " + str +  " " + (Pattern.matches("[a-z_A-Z]+:.*", str)));
		return Pattern.matches("[a-z_A-Z]+[0-9]*:.*", str);
	}

	private boolean itsControlInstruction(String str) {

		return Pattern.matches("([a-z_A-Z])+\\s+[a-z_A-Z]+[0-9]*+", str);
	}

	public void initializeTapes(String inputTapeFile) {
		outputTape = new Tape();
		try {
			inputTape = new Tape(readInputTape(inputTapeFile));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public ArrayList<Integer> readInputTape(String fileName) throws FileNotFoundException, IOException {
		ArrayList<Integer> outputTape = new ArrayList<Integer>();
		String str;
		FileReader f = new FileReader(fileName);
		BufferedReader b = new BufferedReader(f);
		while ((str = b.readLine()) != null) {
			outputTape.add(Integer.parseInt(str));
		}
		System.out.println("input tape tostring  " + outputTape.toString() );
		b.close();
		return outputTape;
	}

}
