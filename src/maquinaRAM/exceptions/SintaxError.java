package maquinaRAM.exceptions;


public class SintaxError extends Exception{


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public SintaxError(int line, String error, String file){
		super("Sintax error: " + error  + " (at " + file + " line :" + line + ")");
	}
}


