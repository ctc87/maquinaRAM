package maquinaRAM;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
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
	ArrayList<String> legalInstructions;
	boolean debug;  
	
	public static void main(String[] args) throws Exception {
		HashMap<String, String> files = new HashMap<String, String>();
		if(args.length < 4) {
			throw new Exception(
					"\nError the execution needs four param \nUsage: \n"
					+ "ram_program.ram: file with the RAM program.\n"
					+ "input_tape.in: file with the contents of the input tape.\n"
					+ "output_tape.out: file with the contents of the output tape.\n"
					+ "debug: If the value of this parameter is 1, a step simulation will be carried out\n"
					+ "step by step, showing by console in each of them the content of the IP record,\n"
					+ "of the data memory, the program memory, the input tapes\n"
					+ "and exit, and the total number of instructions executed up to that moment. He\n"
					+ "value 0 carries out the complete simulation. At the end of it, you should only\n"
					+ "show by console the total number of instructions executed.");
		}
		files.put("program", args[0]);
		files.put("input", args[1]);
		files.put("output", args[2]);
		RAM_Machine ramM;
		boolean debug = args[3].equals("1");
		
		try {
			ramM = new RAM_Machine(files, debug);
			ramM.writeInputTape();
			ramM.alucu.execution();
		} catch (SintaxError e) {
			e.printStackTrace();
		}
		
		
	}

	public RAM_Machine(HashMap<String, String> files, boolean debug) throws SintaxError {
		createLegalInstructionArraylist();
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
		alucu = new ALUCU(dataMemory, programMemory, inputTape, outputTape, debug);

	}
	
	private void createLegalInstructionArraylist() {
		legalInstructions = new ArrayList<String>();
		legalInstructions.add("load");
		legalInstructions.add("store");
		legalInstructions.add("add");
		legalInstructions.add("sub");
		legalInstructions.add("mul");
		legalInstructions.add("div");
		legalInstructions.add("read");
		legalInstructions.add("write");
		legalInstructions.add("jump");
		legalInstructions.add("jzero");
		legalInstructions.add("jgtz");
		legalInstructions.add("halt");
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
					if (itsTaggedRegister(actualLine)) {
						tagReg = actualLine.split(":")[0];
						actualLine = actualLine.split(":")[1].trim();
					}
					if (itsControlInstruction(actualLine)) {
						String insName = actualLine.split("\\p{Blank}+")[0];
						if(!itsLegalInstruction(insName)) {
							throw new SintaxError((i+1), "Uknown instruction " + insName , files.get("program"));
						}
						ins = new Controlnstruction(actualLine.split("\\p{Blank}+")[0], actualLine.split("\\p{Blank}+")[1]);
					} else {
						String operandClass = "";
						String operand = "";
						if (!Pattern.matches("[Hh][Aa][Ll][Tt]", actualLine)) {
							operand = actualLine.trim().split("\\p{Blank}+")[actualLine.trim().split("\\p{Blank}+").length - 1];
						} else {
							operand = "0";
						}
						if (itsConstantOp(operand)) {
							operandClass = "constant";
							operand = operand.split("=")[1];
					
						} else if (itsIndirectOp(operand)) {
							operandClass = "indirect";
							operand = operand.replaceAll("\\*", "*"); 
							operand = operand.split("\\*")[1];
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
						String insName = actualLine.split("\\p{Blank}+")[0];
						Operand op = new Operand(operandClass, Integer.parseInt(operand));
						if(!itsLegalInstruction(insName)) {
							throw new SintaxError((i+1), "Uknown instruction " + insName , files.get("program"));
						}
						ins = new Instruction(insName, op);
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
					Register<BaseInstruction> reg = new Register<BaseInstruction>(ins, tagReg);
					programMemory.getMemory().add(reg);
				}
			}
		}
		return maxRegNumber;
	}
	
	private boolean itsLegalInstruction(String ins) {
		return legalInstructions.contains(ins.toLowerCase());
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
		b.close();
		return outputTape;
	}
	
	public void writeInputTape() {
		PrintWriter writer;
		try {
			writer = new PrintWriter(files.get("output"), "UTF-8");
			for (int i = 0; i < outputTape.size(); i++) {
				writer.println(outputTape.get());
				
			}
			writer.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
