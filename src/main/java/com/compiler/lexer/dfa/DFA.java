package com.compiler.lexer.dfa;

import java.util.List;
import java.util.Set;

import com.compiler.lexer.nfa.State;
import com.compiler.lexer.nfa.Transition;

import java.util.HashSet;

/**
 * DFA
 * ---
 * Represents a complete Deterministic Finite Automaton (DFA).
 * Contains the start state and a list of all states in the automaton.
 */
public class DFA {
    /**
     * The starting state of the DFA.
     */
    public final DfaState startState;

    /**
     * A list of all states in the DFA.
     */
    public final List<DfaState> allStates;

    /**
     * A set of all allowed characters (aplhabet) in the DFA.
     */
    public final Set<Character> alphabet;


    /**
     * Constructs a new DFA.
     * @param startState The starting state of the DFA.
     * @param allStates  A list of all states in the DFA.
     */
    public DFA(DfaState startState, List<DfaState> allStates) {
        this.startState = startState;
        this.allStates = allStates;
        this.alphabet = new HashSet<>();
        for (DfaState dfaState : allStates) {
            for (State nfaState : dfaState.getName()) {
                for (Transition t : nfaState.transitions){
                    if(t.symbol != null){
                        this.alphabet.add(t.symbol);
                    }
                }
            }
        }
    }

    /**
     * Returns a string representation of the DFA, including its all DfaStates and its transitions.
     * @return String representation of the DFA.
     */
    @Override
    public String toString() {
        StringBuilder cadena = new StringBuilder("\nRepresentation of the DFA.");
        cadena.append("\nAplhabet: ");
        
        for (Character character : this.alphabet){
            cadena.append(character.charValue() + ", ");
        }
        
        cadena.append("\nInitial DfaState: " + this.startState.id);
        cadena.append("\nList of DfaStates and its transitions:");
        
        for (DfaState dfaState : this.allStates){
            cadena.append("\n" + dfaState);
        }

        return cadena.append("\n").toString();
    }
}