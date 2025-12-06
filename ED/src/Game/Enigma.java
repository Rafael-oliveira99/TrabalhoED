package Game;

public class Enigma {
    private String pergunta;
    private String resposta;

    public Enigma(String pergunta, String resposta) {
        this.pergunta = pergunta;
        this.resposta = resposta;
    }

    public String getPergunta() {
        return pergunta;
    }

    public boolean verificarResposta(String input) {
        return resposta.equalsIgnoreCase(input);
    }

    @Override
    public String toString() {
        return pergunta;
    }
}