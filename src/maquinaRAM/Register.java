package maquinaRAM;

public class Register<T> {
	T data;
	String Registerlabel;
	
	public Register(T data, String RegisterLabel) {
		this.data = data;
		this.Registerlabel = RegisterLabel;
	}
	
	@Override
	public String toString() {
		String str = Registerlabel.isEmpty() ?  ""  : " (LABEL: " + Registerlabel + ")";
		return str + data.toString(); 
	}

	/**
	 * @return the data
	 */
	public T getData() {
		return data;
	}

	/**
	 * @param data the data to set
	 */
	public void setData(T data) {
		this.data = data;
		System.out.println("data to set " + this.data);
	}

	/**
	 * @return the registerlabel
	 */
	public String getRegisterlabel() {
		return Registerlabel;
	}

	/**
	 * @param registerlabel the registerlabel to set
	 */
	public void setRegisterlabel(String registerlabel) {
		Registerlabel = registerlabel;
	}
}
