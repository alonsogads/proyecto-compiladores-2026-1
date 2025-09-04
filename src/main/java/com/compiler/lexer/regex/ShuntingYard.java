package com.compiler.lexer.regex;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

/**
 * Utility class for regular expression parsing using the Shunting Yard
 * algorithm.
 * <p>
 * Provides methods to preprocess regular expressions by inserting explicit
 * concatenation operators, and to convert infix regular expressions to postfix
 * notation for easier parsing and NFA construction.
 */
/**
 * Utility class for regular expression parsing using the Shunting Yard
 * algorithm.
 */
public class ShuntingYard {

    /**
     * Default constructor for ShuntingYard.
     */
    public ShuntingYard() {
        // Implement constructor if needed
    }

    /**
     * Inserts the explicit concatenation operator ('·') into the regular
     * expression according to standard rules. This makes implicit
     * concatenations explicit, simplifying later parsing.
     *
     * @param regex Input regular expression (may have implicit concatenation).
     * @return Regular expression with explicit concatenation operators.
     */
    public static String insertConcatenationOperator(String regex) {

        if(regex.length()==0||regex.length()==1) return regex; // Devuelve la misma ER si la cadena es de longitud 1 o 2

        StringBuilder newRegex = new StringBuilder(); // Variable para guardar la ER con los operadores de concatenacion

        newRegex.append(regex.charAt(0)); ;; // Agrega el primer caracter de la ER a la nueva ER

        for(int i=0; i<=regex.length()-2; i++){ // Se revisarán todas las subcadenas de la ER ordenadamente en grupos de dos.
            if(isOperand(regex.charAt(i)) && isOperand(regex.charAt(i+1))){ // Evalua y sustituye cadenas de la forma ab = a·b
                newRegex.append('·').append(regex.charAt(i+1));
            }else if(isOperand(regex.charAt(i)) && (regex.charAt(i+1)=='(')){ // Evalua y sustituye cadenas de la forma a( = a·(
                newRegex.append('·').append(regex.charAt(i+1));
            }else if((regex.charAt(i)==')') && isOperand(regex.charAt(i+1))){ // Evalua y sustituye cadenas de la forma )a = )·a
                newRegex.append('·').append(regex.charAt(i+1)); 
            }else if((regex.charAt(i)==')') && (regex.charAt(i+1)=='(')){ // Evalua y sustituye cadenas de la forma )( = )·(
                newRegex.append('·').append(regex.charAt(i+1));
            }else if((regex.charAt(i)=='*') && isOperand(regex.charAt(i+1))){ // Evalua y sustituye cadenas de la forma *a = *·a
                newRegex.append('·').append(regex.charAt(i+1));
            }else if((regex.charAt(i)=='*') && (regex.charAt(i+1)=='(')){ // Evalua y sustituye cadenas de la forma *( = *·(
                newRegex.append('·').append(regex.charAt(i+1));
            }else if((regex.charAt(i)=='+') && isOperand(regex.charAt(i+1))){ // Evalua y sustituye cadenas de la forma +a = +·a
                newRegex.append('·').append(regex.charAt(i+1));
            }else if((regex.charAt(i)=='+') && (regex.charAt(i+1)=='(')){ // Evalua y sustituye cadenas de la forma +( = +·(
                newRegex.append('·').append(regex.charAt(i+1));
            }else if((regex.charAt(i)=='?') && isOperand(regex.charAt(i+1))){ // Evalua y sustituye cadenas de la forma ?a = ?·a
                newRegex.append('·').append(regex.charAt(i+1));
            }else if((regex.charAt(i)=='?') && (regex.charAt(i+1)=='(')){ // Evalua y sustituye cadenas de la forma ?( = ?·(
                newRegex.append('·').append(regex.charAt(i+1));
            }else{ // Si la cadena no es de las formas anteriores, la cadena se queda tal como esta
                newRegex.append(regex.charAt(i+1));
            } 
        }

        return newRegex.toString(); // Regresa la nueva ER con los operadores de concatenación (si es el caso)
    }

    /**
     * Determines if the given character is an operand (not an operator or
     * parenthesis).
     *
     * @param c Character to evaluate.
     * @return true if it is an operand, false otherwise.
     */
    private static boolean isOperand(char c) {
        switch (c) {
          case '|': return false;
          case '*': return false;
          case '?': return false;
          case '+': return false;
          case '(': return false;
          case ')': return false;
          case '·': return false;
          default: return true;
        }
    }

    /**
     * Converts an infix regular expression to postfix notation using the
     * Shunting Yard algorithm. This is useful for constructing NFAs from
     * regular expressions.
     *
     * @param infixRegex Regular expression in infix notation.
     * @return Regular expression in postfix notation.
     */
    public static String toPostfix(String infixRegex) {

        String regex = insertConcatenationOperator(infixRegex); // Primero hacemos la conversión de la cadena a su notación postfija

        StringBuilder salida = new StringBuilder();
        Stack<Character> operadores = new Stack<>();

        Map<Character, Integer> precedencia = new HashMap<>();
        precedencia.put('*',3);
        precedencia.put('+',3);
        precedencia.put('?',3);
        precedencia.put('·',2);
        precedencia.put('|',1);
        
        char c;     // Caracter de la ER a evaluarse
        for(int i=0; i<regex.length(); i++){ // Ciclo para recorrer todos los caracteres de la ER
            c = regex.charAt(i); // Guarda el caracter actual a evualuar

            if(isOperand(c)){   // Evalua si el caracter evaluado es un operando
                salida.append(c);   // Guarda el operando directamente en la salida
            }else{              // Caso si el caracter evaluado es un operador
                if(c=='('){         // Caso si el operador evaluado es "("
                    operadores.push(c); // El operador evaluado se agrega a la pila
                }else if(c==')'){ // Caso si el operador evaluado es ")"
                    char t = operadores.pop(); // Variable temporal para almacenar la cima de la pila
                    while(!(t=='(')){ // Ciclo para recorrer la pila de operadores hasta el primer "("
                        salida.append(t); // Agregamos el operador en la cima de la pila en la salida
                        t = operadores.pop(); // Obtenemos el siguiente operador en la cima de la pila
                    }
                }else{  // Caso si el operador evaluado es alguno de los siguientes: "*","+", "?", "·", "|"
                    if(!(operadores.empty())){ // Evaluamos si la pila de operadores tiene elementos
                        char t = operadores.pop(); // Variable temporal para almacenar la cima de la pila
                        while(true){ // Ciclo que se repite hasta que se cumplan las condiciones para guardar el operador evaluado en la pila
                            if(t == '('){ // Caso si el operador en la cima de la pila es "("
                                operadores.push(t); // La pila se queda igual
                                break; // Salimos del ciclo
                            }else if(precedencia.get(t) < precedencia.get(c)){ // Caso si la precedencia del operador en la cima de la pila es menor que la del operador evaluado
                                operadores.push(t); // La pila se queda igual
                                break; // Salimos del ciclo
                            }else{ // Caso si la precedencia del operador en la cima de la pila es mayor o igual a la del operador evaluado
                                salida.append(t); // El caracter que estaba en la cima de la pila se guarda en la salida
                                if(!(operadores.empty())){ // Evaluamos si la pila aún tiene operadores
                                    t = operadores.pop(); // Obtenemos el siguiente caracter en la cima de la pila y repetimos el ciclo
                                }else{
                                    break; // Salimos si la pila es vacía
                                }
                            }
                        }
                    }
                    operadores.push(c); // Se guarda el operador evaluado en la pila
                }
            }          
        }

        while(!(operadores.empty())){ // Ciclo para pasar toda la pila de operadores a la salida
            salida.append(operadores.pop());
        }

        return salida.toString();
    }

    /*
    public static void main(String args[]){
        String er = "((a|b)+)|(def)*";
        System.out.println(toPostfix(er));
    } */
}
