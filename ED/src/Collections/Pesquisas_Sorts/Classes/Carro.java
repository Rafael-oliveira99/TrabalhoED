package Collections.Pesquisas_Sorts.Classes;

public class Carro implements Comparable<Carro>{
    String marca;
    String modelo;
    float preco;

    public Carro(String marca, String modelo, float preco) {
        this.marca = marca;
        this.modelo = modelo;
        this.preco = preco;
    }
    public String toString(){
        return "Marca: " + marca + " Modelo: " + modelo + " Preço: " + preco;
    }

    @Override
    public int compareTo(Carro o) {
        int result;
        if (marca.equals(o.marca))
            result = modelo.compareTo(o.modelo);
        else
            result = marca.compareTo(o.marca);
        return result;
    }


}
