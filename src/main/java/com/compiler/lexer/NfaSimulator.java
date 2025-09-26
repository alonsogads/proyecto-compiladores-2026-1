package com.compiler.lexer;

import java.util.Set;
import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;

import com.compiler.lexer.nfa.NFA;
import com.compiler.lexer.nfa.State;
import com.compiler.lexer.nfa.Transition;
import com.compiler.lexer.regex.RegexParser;
import com.compiler.lexer.regex.ShuntingYard;

/**
 * NfaSimulator
 * ------------
 * This class provides functionality to simulate a Non-deterministic Finite Automaton (NFA)
 * on a given input string. It determines whether the input string is accepted by the NFA by processing
 * each character and tracking the set of possible states, including those reachable via epsilon (ε) transitions.
 *
 * Simulation steps:
 * - Initialize the set of current states with the ε-closure of the NFA's start state.
 * - For each character in the input, compute the next set of states by following transitions labeled with that character,
 *   and include all states reachable via ε-transitions from those states.
 * - After processing the input, check if any of the current states is a final (accepting) state.
 *
 * The class also provides a helper method to compute the ε-closure of a given state, which is the set of all states
 * reachable from the given state using only ε-transitions.
 */
/**
 * Simulator for running input strings on an NFA.
 */
public class NfaSimulator {
    /**
     * Default constructor for NfaSimulator.
     */
    public NfaSimulator() {
        // Implement constructor if needed
    }

    /**
     * Simulates the NFA on the given input string.
     * Starts at the NFA's start state and processes each character, following transitions and epsilon closures.
     * If any final state is reached after processing the input, the string is accepted.
     *
     * @param nfa The NFA to simulate.
     * @param input The input string to test.
     * @return True if the input is accepted by the NFA, false otherwise.
     */
    public boolean simulate(NFA nfa, String input) {
        /*
         Pseudocode:
         1. Initialize currentStates with epsilon-closure of NFA start state
         2. For each character in input:
              - For each state in currentStates:
                  - For each transition:
                      - If transition symbol matches character:
                          - Add epsilon-closure of destination state to nextStates
              - Set currentStates to nextStates
         3. After input, if any state in currentStates is final, return true
         4. Otherwise, return false
        */
        // Paso 1
        Set<State> closureSet = new HashSet<>();
        this.addEpsilonClosure(nfa.getStartState(), closureSet);

        // Paso 2
        char[] characters = input.toCharArray();
        Set<State> currentStates = closureSet;
        Set<State> nextStates = new HashSet<>();

        for (char c : characters) {

            for (State state : currentStates) {

                List<Transition> transitions = state.transitions;
                for (Transition transition : transitions) {

                    if(transition.symbol != null && transition.symbol.charValue() == c){
                        addEpsilonClosure(transition.toState, nextStates);
                    }
                }
            }
            if (nextStates.isEmpty()) return false; // El simbolo evaluado actual, indica que la cadena no es aceptada. Paramos y retornamos false.
            currentStates = new HashSet<>(nextStates);
            nextStates.clear(); // Limpiamos el conjunto de estados siguientes
        }

        // Paso 3
        for (State state : currentStates) {
            if(state.isFinal()) return true;
        }

        return false;
    }

    /**
     * Computes the epsilon-closure: all states reachable from 'start' using only epsilon (null) transitions.
     *
     * @param start The starting state.
     * @param closureSet The set to accumulate reachable states.
     */
    private void addEpsilonClosure(State start, Set<State> closureSet) {

        List<State> currentStates = start.getEpsilonTransitions();
        List<State> nextStates = new ArrayList<>();

        do {
            nextStates.clear();
            for (State currentState : currentStates) {
                closureSet.add(currentState);
                for (State state : currentState.getEpsilonTransitions()) {
                    if(!closureSet.contains(state)){
                        nextStates.add(state);
                    }
                }
            }
            
            currentStates.clear();
            for (State state : nextStates) {
                currentStates.add(state);
            }

        } while (!nextStates.isEmpty());
    }

    /*
    public static void main(String args[]){

        RegexParser parser = new RegexParser();
        String regex = "(a*)*";
        NFA nfa = parser.parse(regex);

        NfaSimulator nfaSimulator = new NfaSimulator();
        String cadena = "b";
        boolean b = nfaSimulator.simulate(nfa, cadena);
        if(b){
            System.out.println("\nLa cadena '" + cadena + "' es aceptada por el NFA.");
        }else{
            System.out.println("\nLa cadena '" + cadena + "' no es aceptada por el NFA.");
        }
    }*/
}