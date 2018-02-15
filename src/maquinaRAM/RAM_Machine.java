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
		RAM_Machine ramM = new RAM_Machine(files);
		ramM.alucu.execution();
	}

	public RAM_Machine(HashMap<String, String> files) {
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
		System.out.println("MAX BLA BLA " + maxRegNumbers);
		dataMemory = new Memory<Integer>(maxRegNumbers, 0);
		alucu = new ALUCU(dataMemory, programMemory, inputTape, outputTape);

	}

	public int initializeProgram(String programFileName) throws FileNotFoundException, IOException {
		ArrayList<String> programStringArray = new ArrayList<String>();
		String str;
		FileReader f = new FileReader(programFileName);
		BufferedReader b = new BufferedReader(f);
		while ((str = b.readLine()) != null) {
			if (isNotEmptyLine(str)) {
				if (isNotComment(str)) {
					programStringArray.add(str);
				}
			}
		}
		b.close();
		return createProgramMemory(programStringArray);
	}

	public int createProgramMemory(ArrayList<String> programStringArray) {
		int maxRegNumber = 0;
		programMemory = new Memory<BaseInstruction>();
		for (int i = 0; i < programStringArray.size(); i++) {
			String actualLine = programStringArray.get(i).trim();
			;
			String tagReg = "";
			BaseInstruction ins = new BaseInstruction("");
			if (itsTaggedRegister(actualLine)) {
				tagReg = actualLine.split(":")[0];
				actualLine = actualLine.split(":")[1].trim();
			}
			if (itsControlInstruction(actualLine)) {
				ins = new Controlnstruction(actualLine.split("\\s")[0], actualLine.split("\\s")[1]);
			} else {
				// System.out.println(actualLine);
				String operandClass = "";
				String operand = "";
				if (!actualLine.equals("halt")) {
					operand = actualLine.split("\\s")[1];
				} else {
					operand = "0";
				}
				if (itsConstantOp(operand)) {
					operandClass = "constant";
					operand = operand.split("=")[0];
				} else if (itsIndirectOp(operand)) {
					operandClass = "indirect";
					operand = operand.split("*")[0];
					maxRegNumber = Integer.parseInt(operand) > maxRegNumber ? Integer.parseInt(operand) : maxRegNumber;
				} else {
					operandClass = "direct";
					maxRegNumber = Integer.parseInt(operand) > maxRegNumber ? Integer.parseInt(operand) : maxRegNumber;
				}
				if (actualLine.equals("halt")) {
					operand = "-1";
				}
				// System.out.println("OPERAND" + operand);
				Operand op = new Operand(operandClass, Integer.parseInt(operand));
				ins = new Instruction(actualLine.split("\\s")[0], op);
			}
			Register<BaseInstruction> reg = new Register<BaseInstruction>(ins, tagReg);
			programMemory.getMemory().add(reg);
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
		return Pattern.matches("[a-zA-Z]+:.*", str);
	}

	private boolean itsControlInstruction(String str) {
		return Pattern.matches("([a-zA-Z])+\\s+([a-zA-Z])+", str);
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
