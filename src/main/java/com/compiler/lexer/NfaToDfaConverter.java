package com.compiler.lexer;

import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.Queue;
import java.util.LinkedList;
import java.util.Stack;

import com.compiler.lexer.dfa.DFA;
import com.compiler.lexer.dfa.DfaState;
import com.compiler.lexer.nfa.NFA;
import com.compiler.lexer.nfa.State;

// Extra packages for local test
// import com.compiler.lexer.regex.RegexParser;
// import com.compiler.lexer.DfaSimulator;

/**
 * NfaToDfaConverter
 * -----------------
 * This class provides a static method to convert a Non-deterministic Finite Automaton (NFA)
 * into a Deterministic Finite Automaton (DFA) using the standard subset construction algorithm.
 */
/**
 * Utility class for converting NFAs to DFAs using the subset construction algorithm.
 */
public class NfaToDfaConverter {
	/**
	 * Default constructor for NfaToDfaConverter.
	 */
	public NfaToDfaConverter() {
		// Implement constructor if needed
	}

	/**
	 * Converts an NFA to a DFA using the subset construction algorithm.
	 * Each DFA state represents a set of NFA states. Final states are marked if any NFA state in the set is final.
	 *
	 * @param nfa The input NFA
	 * @param alphabet The input alphabet (set of characters)
	 * @return The resulting DFA
	 */
	public static DFA convertNfaToDfa(NFA nfa, Set<Character> alphabet) {
		/*
		 Pseudocode:
		 1. Create initial DFA state from epsilon-closure of NFA start state
		 2. While there are unmarked DFA states:
			  - For each symbol in alphabet:
				  - Compute move and epsilon-closure for current DFA state
				  - If target set is new, create new DFA state and add to list/queue
				  - Add transition from current to target DFA state
		 3. Mark DFA states as final if any NFA state in their set is final
		 4. Return DFA with start state and all DFA states
		*/
		
		// Paso 1
		Set<State> closure = new HashSet<>();				// Creamos un nuevo conjunto de estados NFA
		closure.add(nfa.getStartState());					// Agregamos el estado inicial del NFA recibido al conjunto
		DfaState startDfaState = new DfaState(epsilonClosure(closure));	// Creamos el estado dfa inicial con la cerradura epsilon del conjunto
	
		// Paso 2
		Queue<DfaState> unmarkedStates = new LinkedList<>();// Cola que representa los estados NFA que se van a procesar
		List<DfaState> dfaStates = new ArrayList<>();		// Lista que representa todos los nuevos estados NFA generados
		
		dfaStates.add(startDfaState);						// Agregamos el estado inicial del dfa a la lista de estados...
		unmarkedStates.add(startDfaState);					// ... y además lo agregamos a la cola. Esto corresponde a la acción de "desmarcar" un estado DFA.

		while(!unmarkedStates.isEmpty()){					// Evaluamos los estados desmarcados hasta que no haya más en la pila.

			DfaState currentDfaState = unmarkedStates.poll();	// Obtenemos el siguiente estado DFA de la cola. Es decir, lo "marcamos" (removemos).

			for (Character character : alphabet){
				Set<State> moveResult = move(currentDfaState.getName(), character.charValue());
				Set<State> closureResult = epsilonClosure(moveResult);
				
				DfaState newDfaState = findDfaState(dfaStates, closureResult);	// Recuperamos la referencia al estado DFA si es que ya se había creado.

				if (newDfaState == null && !closureResult.isEmpty()){	// Evalua si el estado DFA es nuevo, es decir null resultado de la asignación anterior. Además descartamos el conjunto vacío.
					newDfaState = new DfaState(closureResult);			// Si es el caso, entonces el conjunto es nuevo y creamos el nuevo estado DFA.

					dfaStates.add(newDfaState);							// Agregamos el nuevo estado DFA a la lista completa de estados.
					unmarkedStates.add(newDfaState);					// Agregamos (desmarcamos) el nuevo estado DFA a la cola.
				}

				if (!closureResult.isEmpty()){
					currentDfaState.addTransition(character, newDfaState); // Agregamos al estado DFA actual, la transición hacia el nuevo estado con el caracter correspondiente.
				}
			}
		}

		// Paso 3
		for (DfaState dfaState : dfaStates){
			for (State state : dfaState.getName()){
				if (state.isFinal()){
					dfaState.setFinal(true);
					break;
				}
			}
		}

		// Paso 4
		DFA newDFA = new DFA(startDfaState, dfaStates);
		return newDFA;
	}

	/**
	 * Computes the epsilon-closure of a set of NFA states.
	 * The epsilon-closure is the set of states reachable by epsilon (null) transitions.
	 *
	 * @param states The set of NFA states.
	 * @return The epsilon-closure of the input states.
	 */
	private static Set<State> epsilonClosure(Set<State> states) {
	/*
	 Pseudocode:
	 1. Initialize closure with input states
	 2. Use stack to process states
	 3. For each state, add all reachable states via epsilon transitions
	 4. Return closure set
	*/
		// Paso 1
		Set<State> closure = new HashSet<>(states);

		// Paso 2
		Stack<State> stack = new Stack<>();
		stack.addAll(closure);

		// Paso 3
		while (!stack.isEmpty()){
			State currentState = stack.pop();
			for (State nextState : currentState.getEpsilonTransitions()){
				if (!closure.contains(nextState)){
					closure.add(nextState);
					stack.push(nextState);
				}
			}
		}

		// Paso 4
		return closure;
	}

	/**
	 * Returns the set of states reachable from a set of NFA states by a given symbol.
	 *
	 * @param states The set of NFA states.
	 * @param symbol The input symbol.
	 * @return The set of reachable states.
	 */
	private static Set<State> move(Set<State> states, char symbol) {
		/*
		 Pseudocode:
		 1. For each state in input set:
			  - For each transition with given symbol:
				  - Add destination state to result set
		 2. Return result set
		*/

		Set<State> moveSet = new HashSet<>(); // A diferencia de la clausura, el conjunto resultante lo inicializamos vacío.
		Stack<State> stack = new Stack<>();
		stack.addAll(states); // Llenamos la pila con el conjunto estados recibidos como parámetro.

		// Paso 1
		while (!stack.isEmpty()){
			State currentState = stack.pop();
			for (State nextState : currentState.getTransitions(symbol)){
				if (!moveSet.contains(nextState)){
					moveSet.add(nextState);
					stack.push(nextState);
				}
			}
		}

		// Paso 2
		return moveSet;
	}

	/**
	 * Finds an existing DFA state representing a given set of NFA states.
	 *
	 * @param dfaStates The list of DFA states.
	 * @param targetNfaStates The set of NFA states to search for.
	 * @return The matching DFA state, or null if not found.
	 */
	private static DfaState findDfaState(List<DfaState> dfaStates, Set<State> targetNfaStates) {
	   /*
	    Pseudocode:
	    1. For each DFA state in list:
		    - If its NFA state set equals target set, return DFA state
	    2. If not found, return null
	   */
	   
		// Paso 1
		for (DfaState dfaState : dfaStates) {
			if(targetNfaStates.equals(dfaState.getName())) return dfaState;
		}
		
		// Paso 2
		return null;
	}
	
	/*
	// Local tests
    public static void main(String args[]){

        RegexParser parser = new RegexParser();
        String regex = "(a|b)*(c)+";
        NFA nfa = parser.parse(regex);

		Set<Character> alphabet = new HashSet<>();
		alphabet.add('a');
		alphabet.add('b');
		alphabet.add('c');
		DFA dfa = convertNfaToDfa(nfa, alphabet);
		System.out.println(dfa);

		String cadena = "ababababac";
		DfaSimulator simulator = new DfaSimulator();
		boolean b = simulator.simulate(dfa, cadena);
		if (b){
			System.out.println("La cadena '" + cadena + "' es aceptada.\n");
		} else{
			System.out.println("La cadena '" + cadena + "' NO es aceptada.\n");
		}

    } */
}
