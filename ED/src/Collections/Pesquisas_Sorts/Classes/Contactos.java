package Collections.Pesquisas_Sorts.Classes;

public class Contactos implements Comparable<Contactos> {
    String nome;
    int numero;

    public Contactos(String nome, int numero) {
        this.nome = nome;
        this.numero = numero;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public int getNumero() {
        return numero;
    }

    public void setNumero(int numero) {
        this.numero = numero;
    }
    public String toString (){
        return "Nome: " + nome + " Numero: " + numero;
    }

    public int compareTo(Contactos o) {
        int result;
        if (nome.equals(o.nome))
            result = numero - o.numero;
        else
            result = nome.compareTo(o.nome);
        return result;
    }
}
