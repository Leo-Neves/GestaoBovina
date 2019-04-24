package br.agr.terras.corelibrary.infraestructure.resources.strings;

import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;

public class ValidaCPF {

    public static boolean isCPF(String CPF) {
        if (CPF.equals("00000000000") || CPF.equals("11111111111") ||
                CPF.equals("22222222222") || CPF.equals("33333333333") ||
                CPF.equals("44444444444") || CPF.equals("55555555555") ||
                CPF.equals("66666666666") || CPF.equals("77777777777") ||
                CPF.equals("88888888888") || CPF.equals("99999999999") ||
                (CPF.length() != 11))
            return(false);

        char dig10, dig11;

        try {
            dig10 = getDigito10(CPF);
            dig11 = getDigito11(CPF);
            return (dig10 == CPF.charAt(9)) && (dig11 == CPF.charAt(10));
        } catch (InputMismatchException erro) {
            return(false);
        }
    }

    private static char getDigito10(String CPF){
        char dig10;
        int sm, i, r, num, peso;
        sm = 0;
        peso = 10;
        for (i=0; i<9; i++) {
            num = (int)(CPF.charAt(i) - 48);
            sm = sm + (num * peso);
            peso = peso - 1;
        }

        r = 11 - (sm % 11);
        if ((r == 10) || (r == 11))
            dig10 = '0';
        else dig10 = (char)(r + 48);
        return dig10;
    }

    private static char getDigito11(String CPF){
        char dig11;
        int  i, r, num, peso;
        int sm = 0;
        peso = 11;
        for(i=0; i<10; i++) {
            num = (int)(CPF.charAt(i) - 48);
            sm = sm + (num * peso);
            peso = peso - 1;
        }

        r = 11 - (sm % 11);
        if ((r == 10) || (r == 11))
            dig11 = '0';
        else dig11 = (char)(r + 48);
        return dig11;
    }

    public static String imprimeCPF(String CPF) {
        return(CPF.substring(0, 3) + "." + CPF.substring(3, 6) + "." +
                CPF.substring(6, 9) + "-" + CPF.substring(9, 11));
    }

    public static String geraCPF() {
        List<Integer> listaAleatoria = new ArrayList<>();
        for(int i = 0; i < 9; i++){
            listaAleatoria.add((int) (Math.random() * 10));
        }
        listaAleatoria = geraPrimeiroDigito(listaAleatoria);
        listaAleatoria = geraSegundoDigito(listaAleatoria);
        String cpf = "";
        String texto = "";
        for(int item : listaAleatoria){
            texto += item;
        }
        try{
            cpf = imprimeCPF(texto);
        }catch(Exception ex){
            ex.printStackTrace();
        }
        return cpf;
    }

    private static List<Integer> geraPrimeiroDigito(List<Integer> listaAleatoria){
        List<Integer> listaNumMultiplicados = new ArrayList<>();
        int primeiroDigito;
        int totalSomatoria = 0;
        int restoDivisao;
        int peso = 10;
        for(int item : listaAleatoria){
            listaNumMultiplicados.add(item * peso);
            peso--;
        }
        for(int item : listaNumMultiplicados){
            totalSomatoria += item;
        }
        restoDivisao = (totalSomatoria % 11);
        if(restoDivisao < 2){
            primeiroDigito = 0;
        } else{
            primeiroDigito = 11 - restoDivisao;
        }
        listaAleatoria.add(primeiroDigito);
        return listaAleatoria;
    }

    private static List<Integer> geraSegundoDigito(List<Integer> listaAleatoria){
        List<Integer> listaNumMultiplicados = new ArrayList<>();
        int segundoDigito;
        int totalSomatoria = 0;
        int restoDivisao;
        int peso = 11;
        for(int item : listaAleatoria){
            listaNumMultiplicados.add(item * peso);
            peso--;
        }
        for(int item : listaNumMultiplicados){
            totalSomatoria += item;
        }
        restoDivisao = (totalSomatoria % 11);
        if(restoDivisao < 2){
            segundoDigito = 0;
        } else{
            segundoDigito = 11 - restoDivisao;
        }
        listaAleatoria.add(segundoDigito);
        return listaAleatoria;
    }

}