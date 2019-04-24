package br.agr.terras.corelibrary.infraestructure.resources;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

public class MaskCustom implements TextWatcher {

	private String mask;
	private EditText ediTxt;

	final public static int CPF = 1;
	final public static int CNPJ = 2;
	final public static int DATE = 3;
	final public static int INT = 4;
	final public static int PONHE = 5;
	final public static int PONHE_NINE = 5;


	private static String patternCPF = "###.###.###-##";
	private static String patternCNPJ = "###.###.###-##";
	private static String patternDATE = "##/##/####";
	private static String patternINT = "#####";
	private static String patternPhone = "(##) ####-####";
	private static String patternPhoneNine = "(##) #####-####";

	public MaskCustom(EditText ediTxt, int tipoMask) {
		this.ediTxt = ediTxt;
		
		if (tipoMask == CPF) {
			this.mask = patternCPF;
		} else if (tipoMask == CNPJ) {
			this.mask = patternCNPJ;
		} else if (tipoMask == DATE) {
			this.mask = patternDATE;
		}else if (tipoMask == INT) {
			this.mask = patternINT;
		}else if (tipoMask == PONHE) {
			this.mask = patternPhone;
		}else if (tipoMask == PONHE_NINE) {
			this.mask = patternPhoneNine;
		}


		
		
	}

	public String unmask(String s) {
		return s.replaceAll("[.]", "").replaceAll("[-]", "")
				.replaceAll("[/]", "").replaceAll("[(]", "")
				.replaceAll("[)]", "").replaceAll("[ ]", "");
	}

	boolean isUpdating;
	String old = "";

	public void onTextChanged(CharSequence s, int start, int before, int count) {
		String str = unmask(s.toString());
		String mascara = "";
		if (isUpdating) {
			old = str;
			isUpdating = false;
			return;
		}
		int i = 0;
		for (char m : mask.toCharArray()) {
			if (m != '#' && str.length() > old.length()) {
				mascara += m;
				continue;
			}
			try {
				mascara += str.charAt(i);
			} catch (Exception e) {
				break;
			}
			i++;
		}
		isUpdating = true;
		ediTxt.setText(mascara);
		ediTxt.setSelection(mascara.length());
	}

	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
	}

	public void afterTextChanged(Editable editable) {
	}
}
