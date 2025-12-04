package Collections.Stacks;

public class CalculadoraArrayStack {
        public static int calcularPostfix(String expressao) {
            ArrayStack<Integer> calculadora = new ArrayStack<>();
            String[] elementos = expressao.split(" ");
            for (String element : elementos) {
                if (element.matches("-?\\d+")) {
                    calculadora.push(Integer.parseInt(element));
                } else {
                    int num2 = calculadora.pop();
                    int num1 = calculadora.pop();
                    int resultado = executarOperacao(element, num1, num2);
                    calculadora.push(resultado);
                }
            }
            return calculadora.pop();
        }

        public static int executarOperacao(String operador, int a, int b) {
            switch (operador) {
                case "+":
                    return a + b;
                case "-":
                    return a - b;
                case "*":
                    return a * b;
                case "/":
                    return a / b;
                default:
                    throw new IllegalArgumentException("Inválido: " + operador);
            }
        }
    }

