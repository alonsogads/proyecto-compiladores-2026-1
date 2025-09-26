package com.compiler.lexer.nfa;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

// import com.compiler.lexer.dfa.DfaState;

/**
 * Represents a state in a Non-deterministic Finite Automaton (NFA).
 * Each state has a unique identifier, a list of transitions to other states,
 * and a flag indicating whether it is a final (accepting) state.
 *
 * <p>
 * Fields:
 * <ul>
 *   <li>{@code id} - Unique identifier for the state.</li>
 *   <li>{@code transitions} - List of transitions from this state to others.</li>
 *   <li>{@code isFinal} - Indicates if this state is an accepting state.</li>
 * </ul>
 *
 *
 * <p>
 * The {@code nextId} static field is used to assign unique IDs to each state.
 * </p>
 */
public class State {
    private static int nextId = 0;
    /**
     * Unique identifier for this state.
     */
    public final int id;

    /**
     * List of transitions from this state to other states.
     */
    public List<Transition> transitions;

    /**
     * Indicates if this state is a final (accepting) state.
     */
    public boolean isFinal;

    /**
     * Constructs a new state with a unique identifier and no transitions.
     * The state is not final by default.
     */
    public State() {
        this.id = nextId++;
        this.transitions = new ArrayList<>();
        this.isFinal = false;
    }

    /**
     * Checks if this state is a final (accepting) state.
     * @return true if this state is final, false otherwise
     */
    public boolean isFinal() {
        return isFinal;
    }

    /**
     * Returns the states reachable from this state via epsilon transitions (symbol == null).
     * @return a list of states reachable by epsilon transitions
     */
    public List<State> getEpsilonTransitions() {
        List<State> episilonStates = new ArrayList<>(); // Lista para guardar los estados alcanzables
        episilonStates.add(this); // El mismo estado es alcanzable bajo la transición epsilon.
        for (Transition t : this.transitions) { // Recorremos toda la lista de transiciones
            if(t.symbol == null){ // Evaluamos si la transicion es epsilon
                episilonStates.add(t.toState); // Si es el caso, el estado se agrega a la lista de estados de transición epsilon.
            }
        }
        return episilonStates;
    }

    /**
     * Returns the states reachable from this state via a transition with the given symbol.
     * @param symbol the symbol for the transition
     * @return a list of states reachable by the given symbol
     */
    public List<State> getTransitions(char symbol) { // La implementación es análoga al metodo anterior
        List<State> symbolStates = new ArrayList<>();
        for (Transition t : this.transitions) {
            if (t.symbol != null && t.symbol.equals(symbol)){
                symbolStates.add(t.toState);
            }
        }
        return symbolStates;
    }

    /**
     * Additional implementation.
     * Two States are considered equal if they represent the same id of states.
     * @param obj The object to compare.
     * @return True if the states are equal, false otherwise.
     */
    @Override
    public boolean equals(Object obj){
        if(this == obj) return true;
        if(obj == null || this.getClass() != obj.getClass()) return false;
        State state = (State) obj;
        return (this.id == state.id);
    }

    /**
     * Additional implementation.
     * The hash code is based on the id states. 
     * @return The hash code for this state.
     */
    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    /**
     * Additional implementation.
     * Returns a string representation of the state, including its id and transitions.
     * @return String representation of the state.
     */
    @Override
    public String toString(){
        StringBuilder cadena = new StringBuilder("State " + this.id);
        if(this.isFinal) cadena.append("*");
        cadena.append(". Transitions:");
        for (Transition transition : transitions) {
            cadena.append("\n\tSymbol: " + transition.symbol + ". ToState: " + transition.toState.id);
        }
        return cadena.toString();
    }

}