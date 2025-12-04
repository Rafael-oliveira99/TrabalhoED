package Game;

public class Enigma {
    private String question;
    private String answer;

    public Enigma(String question, String answer) {
        this.question = question;
        this.answer = answer;
    }

    public String getQuestion() {
        return question;
    }

    public boolean checkAnswer(String input) {
        return answer.equalsIgnoreCase(input);
    }

    @Override
    public String toString() {
        return question;
    }
}