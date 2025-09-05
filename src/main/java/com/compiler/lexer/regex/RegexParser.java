package com.compiler.lexer.regex;

import java.util.Stack;
import com.compiler.lexer.nfa.NFA;
import com.compiler.lexer.nfa.State;
import com.compiler.lexer.nfa.Transition;

/**
 * RegexParser
 * -----------
 * This class provides functionality to convert infix regular expressions into nondeterministic finite automata (NFA)
 * using Thompson's construction algorithm. It supports standard regex operators: concatenation (·), union (|),
 * Kleene star (*), optional (?), and plus (+). The conversion process uses the Shunting Yard algorithm to transform
 * infix regex into postfix notation, then builds the corresponding NFA.
 *
 * Features:
 * - Parses infix regular expressions and converts them to NFA.
 * - Supports regex operators: concatenation, union, Kleene star, optional, plus.
 * - Implements Thompson's construction rules for NFA generation.
 *
 * Example usage:
 * <pre>
 *     RegexParser parser = new RegexParser();
 *     NFA nfa = parser.parse("a(b|c)*");
 * </pre>
 */

/**
 * Parses regular expressions and constructs NFAs using Thompson's construction.
 */
public class RegexParser {
    /**
     * Default constructor for RegexParser.
     */
    public RegexParser() {
    }

    /**
     * Converts an infix regular expression to an NFA.
     *
     * @param infixRegex The regular expression in infix notation.
     * @return The constructed NFA.
     */
    public NFA parse(String infixRegex) {
    // Pseudocode: Convert infix to postfix, then build NFA from postfix
        String postfixRegex = ShuntingYard.toPostfix(infixRegex);
        return buildNfaFromPostfix(postfixRegex);
    }

    /**
     * Builds an NFA from a postfix regular expression.
     *
     * @param postfixRegex The regular expression in postfix notation.
     * @return The constructed NFA.
     */
    private NFA buildNfaFromPostfix(String postfixRegex) {
    // Pseudocode: For each char in postfix, handle operators and operands using a stack
    
        Stack<NFA> stack = new Stack<>();

        for(int i=0 ; i<postfixRegex.length() ; i++){

            char c = postfixRegex.charAt(i);

            if (isOperand(c)) {
                stack.push(createNfaForCharacter(c));
            } else{
                switch (c) {
                case '|': 
                    handleUnion(stack);
                    break;
                case '*': 
                    handleKleeneStar(stack);
                    break;
                case '?': 
                    handleOptional(stack);
                    break;
                case '+': 
                    handlePlus(stack);
                    break;
                case '·': 
                    handleConcatenation(stack);
                    break;
                default: 
                    throw new IllegalArgumentException("Unknown operator: " + c);
                }
            }
        }

        if (stack.size() != 1) {
            throw new IllegalStateException("Invalid postfix regex, stack size: " + stack.size());
        }

        return stack.pop();
    }

    /**
     * Handles the '?' operator (zero or one occurrence).
     * Pops an NFA from the stack and creates a new NFA that accepts zero or one occurrence.
     * @param stack The NFA stack.
     */
    private void handleOptional(Stack<NFA> stack) {
    // Pseudocode: Pop NFA, create new start/end, add epsilon transitions for zero/one occurrence
        NFA nfaA = stack.pop(); // Obtenemos el NFA de la pila
        
        State newStart = new State(); // Creamos el nuevo estado inicial
        State newEnd = new State(); // Creamos el nuevo estado final
        newEnd.isFinal = true; // Activamos la bandera del nuevo estado final

        Transition epsilonToStartA = new Transition(null, nfaA.getStartState()); // Creamos la transicion epsilon hacia el estado inicial de nfaA
        Transition epsilonToEnd = new Transition(null, newEnd); // Creamos la transición epsilon hacia el nuevo estado final

        newStart.transitions.add(epsilonToStartA); // Agregamos al nuevo estado inicial la transición epsilon hacia el estado inicial de nfaA
        newStart.transitions.add(epsilonToEnd); // Agregamos al nuevo estado inicial la transición epsilon hacia el estado final

        // Tambien agregamos al estado final de nfaA la transición epsilon hacia el estado final. Luego cambiamos el valor de su bandera.
        nfaA.endState.transitions.add(epsilonToEnd);
        nfaA.endState.isFinal = false;

        NFA nfa = new NFA(newStart, newEnd); // Creamos el nuevo NFA
        stack.push(nfa); // Agreamos el nuevo NFA a la pila
    }

    /**
     * Handles the '+' operator (one or more occurrences).
     * Pops an NFA from the stack and creates a new NFA that accepts one or more occurrences.
     * @param stack The NFA stack.
     */
    private void handlePlus(Stack<NFA> stack) {
    // Pseudocode: Pop NFA, create new start/end, add transitions for one or more occurrence
        NFA nfaA = stack.pop(); // Obtenemos el NFA de la pila
        
        State newStart = new State(); // Creamos el nuevo estado inicial
        State newEnd = new State(); // Creamos el nuevo estado final
        newEnd.isFinal = true; // Activamos la bandera del nuevo estado final

        // Creamos la transicion epsilon hacia el estado inicial de nfaA y la agregamos a las transiciones del estado inicial.
        Transition epsilonToStartA = new Transition(null, nfaA.getStartState());
        newStart.transitions.add(epsilonToStartA);

        // También agregamos la transición epsilon a las transiciones del estado final de nfaA (para modelar la repetición o bucle).
        nfaA.endState.transitions.add(epsilonToStartA);

        // Creamos la transición epsilon hacia el estado final. Luego agregamos a las transiciones del estado final de nfaA. Por último cambiamos el valor de la bandera del estado final de nfaA.
        Transition epsilonToEnd = new Transition(null, newEnd);
        nfaA.endState.transitions.add(epsilonToEnd);
        nfaA.endState.isFinal = false;

        NFA nfa = new NFA(newStart, newEnd); // Creamos el nuevo NFA
        stack.push(nfa); // Agreamos el nuevo NFA a la pila
    }
    
    /**
     * Creates an NFA for a single character.
     * @param c The character to create an NFA for.
     * @return The constructed NFA.
     */
    private NFA createNfaForCharacter(char c) {
    // Pseudocode: Create start/end state, add transition for character
        State startState = new State(); // Creamos el estado inicial
        State endState = new State(); // Creamos el estado final
        endState.isFinal = true; // Cambiamos el valor de la bandera del estado final

        Transition t = new Transition(c, endState); // Creamos una transición hacia el estado final con 'c' como simbolo
        startState.transitions.add(t); // Agregamos la transicion a la lista de transiciones del estado inicial

        NFA nfa =new NFA(startState, endState); // Creamos el NFA con los estados creados
        return nfa;
    }

    /**
     * Handles the concatenation operator (·).
     * Pops two NFAs from the stack and connects them in sequence.
     * @param stack The NFA stack.
     */
    private void handleConcatenation(Stack<NFA> stack) {
    // Pseudocode: Pop two NFAs, connect end of first to start of second

        NFA nfaB = stack.pop(); // Obtenemos el segundo NFA de la pila
        NFA nfaA = stack.pop(); // Obtenemos el primer NFA de la pila
        
        Transition t = new Transition(null, nfaB.getStartState()); // Creamos una nueva transicion epsilon hacia el estado inicial de nfaB
        nfaA.endState.transitions.add(t); // Agregamos la transición anterior al estado final de nfaA.
        nfaA.endState.isFinal = false; // Cambiamos la bandera de estado final de nfaA

        NFA nfa = new NFA(nfaA.getStartState(), nfaB.endState); // Creamos el nuevo NFA
        stack.push(nfa); // Agregamos el nuevo NFA a la pila
    }

    /**
     * Handles the union operator (|).
     * Pops two NFAs from the stack and creates a new NFA that accepts either.
     * @param stack The NFA stack.
     */
    private void handleUnion(Stack<NFA> stack) {
    // Pseudocode: Pop two NFAs, create new start/end, add epsilon transitions for union

        NFA nfaB = stack.pop(); // Obtenemos el segundo NFA de la pila
        NFA nfaA = stack.pop(); // Obtenemos el primer NFA de la pila
        
        State newStart = new State(); // Creamos el nuevo estado inicial
        State newEnd = new State(); // Creamos el nuevo estado final
        newEnd.isFinal = true; // Activamos la bandera del nuevo estado final

        Transition epsilonToStartA = new Transition(null, nfaA.getStartState()); // Creamos la transición epsilon hacia el estado inical de nfaA
        Transition epsilonToStartB = new Transition(null, nfaB.getStartState()); // Creamos la transición epsilon hacia el estado inicial de nfaB.
        // Agregamos las dos transiciones a la lista de transiciones del nuevo estado inicial
        newStart.transitions.add(epsilonToStartA);
        newStart.transitions.add(epsilonToStartB);

        Transition epsilonToEnd = new Transition(null, newEnd); // Creamos una transición epsilon hacia el nuevo estado final.
        // Agregamos la transición al estado final de nfaA y nfaB y cambiamos el valor de sus banderas
        nfaA.endState.transitions.add(epsilonToEnd);
        nfaB.endState.transitions.add(epsilonToEnd);
        nfaA.endState.isFinal = false;
        nfaB.endState.isFinal = false;

        NFA nfa = new NFA(newStart, newEnd); // Creamos el nuevo NFA
        stack.push(nfa); // Agreamos el nuevo NFA a la pila
    }

    /**
     * Handles the Kleene star operator (*).
     * Pops an NFA from the stack and creates a new NFA that accepts zero or more repetitions.
     * @param stack The NFA stack.
     */
    private void handleKleeneStar(Stack<NFA> stack) {
    // Pseudocode: Pop NFA, create new start/end, add transitions for zero or more repetitions
        NFA nfaA = stack.pop(); // Obtenemos el NFA de la pila
        
        State newStart = new State(); // Creamos el nuevo estado inicial
        State newEnd = new State(); // Creamos el nuevo estado final
        newEnd.isFinal = true; // Activamos la bandera del nuevo estado final

        // Creamos la transicion epsilon hacia el estado inicial de nfaA, y la agregamos a las transiciones del estado final de nfaA (para modelar la repetición o bucle)
        Transition epsilonToStartA = new Transition(null, nfaA.getStartState());
        nfaA.endState.transitions.add(epsilonToStartA);

        Transition epsilonToEnd = new Transition(null, newEnd); // Creamos la transición epsilon hacia el nuevo estado final
        newStart.transitions.add(epsilonToStartA); // Agregamos al nuevo estado inicial la transición epsilon hacia el estado inicial de nfaA
        newStart.transitions.add(epsilonToEnd); // Agregamos al nuevo estado inicial la transición epsilon hacia el estado final

        // Tambien agregamos al estado final de nfaA la transición epsilon hacia el estado final. Luego cambiamos el valor de su bandera.
        nfaA.endState.transitions.add(epsilonToEnd);
        nfaA.endState.isFinal = false;

        NFA nfa = new NFA(newStart, newEnd); // Creamos el nuevo NFA
        stack.push(nfa); // Agreamos el nuevo NFA a la pila
    }

    /**
     * Checks if a character is an operand (not an operator).
     * @param c The character to check.
     * @return True if the character is an operand, false if it is an operator.
     */
    private boolean isOperand(char c) {
    // Pseudocode: Return true if c is not an operator
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

    /*
    public static void main(String args[]){

        RegexParser parser = new RegexParser();
        String str = "(a|b)*(c)+";
        NFA nfa = parser.parse(str);
        System.out.println(ShuntingYard.toPostfix(str));
        System.out.println("Estado inicial:");
        System.out.println(nfa.getStartState().id);
        System.out.println("Estado final:");
        System.out.println(nfa.endState.id);;
    }*/
}