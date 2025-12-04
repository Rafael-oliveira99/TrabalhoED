package Collections.Queue;

public class Cifra {
    private String mensagemInicial;
    char[] mensagem;
    private int first;
    private int last;
    private int size;
    private int DEFAULT_CAPACITY=100;

         public Cifra() {
            mensagem = new char[DEFAULT_CAPACITY];
            first = 0;
            last = -1;
            size = 0;
        }



    public void mensagemQueue(String mensagem) {
        for (char caracter : mensagem.toCharArray()) {
            enqueue(caracter);
        }
    }

    public void codificar(int[] codigos) {
        if (codigos == null || codigos.length == 0) {
            throw new IllegalArgumentException("Codigos array is empty or null.");
        }

        for (int i = 0; i < size; i++) {
            int index = (first + i) % mensagem.length;
            char originalChar = mensagem[index];
            char encodedChar = encodeChar(originalChar, codigos[i % codigos.length]);
            mensagem[index] = encodedChar;
        }
    }

    private char encodeChar(char character, int shift) {
        if (Character.isLetter(character)) {
            char base = Character.isUpperCase(character) ? 'A' : 'a';
            return (char) (((character - base + shift) % 26 + 26) % 26 + base);
        }
        return character;
    }



    public void enqueue(char caracter) {
        if(size()==mensagem.length) {expandCapacity();}
        last = (last + 1) % mensagem.length;
        mensagem[last] = caracter;
        size++;
    }

    private void expandCapacity() {
        char[] larger = new char[mensagem.length*2];
        for (int i = 0; i < mensagem.length; i++) {
            larger[i] = mensagem[(first + i) % mensagem.length];
        }
        first = 0;
        last = size - 1;
        mensagem = larger;
    }


    public char dequeue() {
        if (isEmpty()) {
            throw new IllegalStateException("Queue esta vazia.");
        }
        char caracter =  mensagem[first];
        first = (first + 1) % mensagem.length;
        size--;
        return caracter;
    }



    public char first() {
        if(isEmpty()) {throw new IllegalStateException("Queue esta vazia.");
        }
        char caracter = mensagem[first];
        return caracter;
    }

    public boolean isEmpty() {
        if (size ==0)return true;
        return false;
    }


    public int size() {
        return size;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < size; i++) {
            int index = (first + i) % mensagem.length;
            sb.append(mensagem[index]);
        }
        return sb.toString();
    }
}
