package com.compiler.lexer.nfa;

import java.util.Set;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * Represents a Non-deterministic Finite Automaton (NFA) with a start and end state.
 * <p>
 * An NFA is used in lexical analysis to model regular expressions and pattern matching.
 * This class encapsulates the start and end states of the automaton.
 */

public class NFA {
    /**
     * The initial (start) state of the NFA.
     */
    public final State startState;

    /**
     * The final (accepting) state of the NFA.
     */
    public final State endState;

    /**
     * Constructs a new NFA with the given start and end states.
     * @param start The initial state.
     * @param end The final (accepting) state.
     */
    public NFA(State start, State end) {
        this.startState = start;
        this.endState = end;
        this.endState.isFinal = true; // Cambiamos el valor de la bandera del estado final
    }

    /**
     * Returns the initial (start) state of the NFA.
     * @return the start state
     */
    public State getStartState() {
        return startState;
    }

    /**
     * Additional implementation.
     * Returns a set of states that represents all states of the NFA.
     * @return a set of states of the NFA.
     */
    public Set<State> getStates() {
        Set<State> allStates = new HashSet<>();
        allStates.add(startState);

        Queue<Transition> currentStateTransitions = new LinkedList<>(startState.transitions);
        while (!currentStateTransitions.isEmpty()) {
            State currentState = currentStateTransitions.poll().toState;
            if(!allStates.contains(currentState)){
                allStates.add(currentState);
                for (Transition transition : currentState.transitions) {
                    if(!allStates.contains(transition.toState)){
                        currentStateTransitions.add(transition);
                    }
                }
            }
        }
        return allStates;
    }

    /**
     * Additional implementation.
     * Returns a string representation of the NFA, including its states and transitions.
     * @return String representation of the NFA.
     */
    @Override
    public String toString(){
        StringBuilder cadena = new StringBuilder("\nList of NFA's States and Transitions:");
        Set<State> statesNFA = this.getStates();
        for (State state : statesNFA) {
            cadena.append("\n->" + state);
        }
        return cadena.toString();
    }

}