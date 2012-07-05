/*
 *  BEEN: Benchmarking Environment
 *  ==============================
 *
 *  File author: Branislav Repcek
 *
 *  GNU Lesser General Public License Version 2.1
 *  ---------------------------------------------
 *  Copyright (C) 2004-2006 Distributed Systems Research Group,
 *  Faculty of Mathematics and Physics, Charles University in Prague
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License version 2.1, as published by the Free Software Foundation.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston,
 *  MA  02111-1307  USA
 */
package cz.cuni.mff.been.common;

import java.util.List;
import java.util.Map;

import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * This class can be used to parse strings which contain declarations of variables. Declarations are
 * substituted with values of the variables.
 * <p>
 * For example, string <tt>value is: ${value} units</tt> if value of <tt>value</tt> variable is 100
 * will be transformed into <tt>value is: 100 units</tt>.
 * </p>
 *
 * @author Branislav Repcek
 */
public class SubstituteVariableValues {

	/**
	 * Interface for the data providers that can be used to supply values of the variables during parsing.
	 * 
	 * @param <T> Type of data returned by the data provider.
	 *
	 * @author Branislav Repcek
	 */
	public static interface VariableValueProviderInterface< T > {
		
		/**
		 * Get value of the variable with given name.
		 * 
		 * @param variableName Name of the variable.
		 * 
		 * @return Value of the variable. This cannot be <code>null</code> since that is used to
		 *         indicate undefined variable.
		 */
		T getValue(String variableName);
	}
	
	/**
	 * Interface for the syntax checker of the names of variables found when parsing input. 
	 *
	 * @author Branislav Repcek
	 */
	public static interface VariableNameSyntaxCheckerInterface {

		/**
		 * Check syntax of the variable name.
		 * 
		 * @param name Name of the variable to check.
		 * 
		 * @return <code>true</code> is variable name is syntactically correct, <code>false</code>
		 *         otherwise.
		 */
		boolean checkName(String name);
	}
	
	/**
	 * Regular expression used in default syntax checker.
	 */
	private static final String DEFAULT_VARIABLE_SYNTAX_REGEXP = "([\\p{Alnum}_-]+)+(\\.?[\\p{Alnum}_-]+)*";
	
	/**
	 * Tokenizer which is used to split string into tokens.
	 */
	private StringTokenizer tokenizer;
	
	/**
	 * Syntax checker for variable names.
	 */
	private VariableNameSyntaxCheckerInterface syntaxChecker;

	/**
	 * Create variable substition class with default syntax checker. Default syntax cehcker uses
	 * following regular expression to check variable names:<br>
	 * <br>
	 * <tt>([\p{Alnum}_-]+)+(\.?[\p{Alnum}_-]+)*</tt><br>
	 * <br>
	 * This expression is designed to work with variable names similar to those from Ant.
	 * <p>That is, variable name is enclosed in ${ and }. Variable name can contain lower- and upper-case
	 * letters, numbers, dots, dashes and underscore characters. Names are case-sensitive. Dot character
	 * cannot appear at the beginning or at the end of the variable name and there cannot be two
	 * dots next to each other.</p>
	 * <p>If you want to use $, { or } outside of the variable declaration, you have to use escape
	 * sequences: \$, \{, \}. To write \ use \\ escape sequence. Note that you have to escape each
	 * character, so for example \${ results in an error (correct form is \$\{).
	 * </p>
	 */
	public SubstituteVariableValues() {
		
		syntaxChecker = new RegexpVariableNameSyntaxChecker(DEFAULT_VARIABLE_SYNTAX_REGEXP);
		tokenizer = new StringTokenizer("\\${}");
	}
	
	/**
	 * Create variable substitution class which will use your own regular expression to check
	 * syntax of the variable names.
	 *  
	 * @param variableSyntaxRegexp String containing regular expression that will be used to check
	 *        syntax of the names of the variables. Regular expressions use same syntax as is used
	 *        in <tt>java.util.regex.Pattern</tt> class. 
	 * 
	 * @throws PatternSyntaxException If given regular expression has invalid syntax.
	 * @throws IllegalArgumentException If string with regular expression is empty or <tt>null</tt>.
	 */
	public SubstituteVariableValues(String variableSyntaxRegexp) 
		throws PatternSyntaxException, IllegalArgumentException {

		if (variableSyntaxRegexp == null) {
			
			throw new IllegalArgumentException("null variable syntax regular expression is not allowed.");
		}
		
		if (variableSyntaxRegexp.length() == 0) {
			
			throw new IllegalArgumentException("Empty variable syntax regular expression is not allowed.");
		}
		
		syntaxChecker = new RegexpVariableNameSyntaxChecker(variableSyntaxRegexp);
		tokenizer = new StringTokenizer("\\${}");
	}
	
	/**
	 * Create variable substition class with custom variable name syntax checker.
	 * 
	 * @param syntaxChecker Syntax checker which will be used to validate names of the variables.
	 * 
	 * @throws IllegalArgumentException If given syntax checker is null.
	 */
	public SubstituteVariableValues(VariableNameSyntaxCheckerInterface syntaxChecker) 
		throws IllegalArgumentException {
		
		if (syntaxChecker == null) {
			throw new IllegalArgumentException("null syntax checker is not allowed.");
		}
		
		this.syntaxChecker = syntaxChecker;
	}
	
	/**
	 * Parse string and substitute all variables with their values.
	 * 
	 * @param <T> Type of values (can be Object if multiple types are required). 
	 * 
	 * @param input Input string.
	 * @param valueProvider Data provider which stores values of variables.
	 * 
	 * @return String with variables substituted with their values.
	 * 
	 * @throws IllegalArgumentException If string is invalid.
	 */
	public < T > String parseString(String input, VariableValueProviderInterface< T > valueProvider) 
		throws IllegalArgumentException {

		StringBuilder resultBuilder = new StringBuilder(input.length());

		List< StringTokenizer.Token > tokens = tokenizer.tokenize(input);
		int state = 0;
		String currentVariableName = "";
		int currentVariableStart = 0;
		
		for (StringTokenizer.Token token: tokens) {
			
			if (token.isDelimiter()) {
				char tokenChar = ((StringTokenizer.DelimiterToken) token).delimiterChar();
				
				switch (state) {
					case 0: // we have delimiter after string token or at the begining of the input
						switch (tokenChar) {
							case '$':
								state = 1;
								break;
								
							case '\\':
								state = 2;
								break;
								
							case '{':
							case '}':
								throw new IllegalArgumentException("Unexpected token \"" 
										+ tokenChar + "\" at position " + token.getPosition() + ".");
								
							default:
								throw new IllegalArgumentException("Internal error.");
						}
						break;
						
					case 1: // variable declaration start
						switch (tokenChar) {
							case '{': // next token has to be variable declaration
								state = 3;
								break;
								
							default:
								throw new IllegalArgumentException("Unexpected token \""
										+ tokenChar + "\" at position " + token.getPosition() + "."
										+ " \"{\" expected.");
						}
						break;
						
					case 2: // we are in the escape sequence, append character we are escaping
						resultBuilder.append(tokenChar);
						state = 0;
						break;
						
					case 3:
						switch (tokenChar) {

							case '}': // closing brace of the name, not good
								throw new IllegalArgumentException("Empty variable name at position "
										+ (token.getPosition() - 2) + ".");
							
							default: // every other delimiter is also error, but it will not get its
								     // own exception message
								throw new IllegalArgumentException("Unexpected token at \""
										+ tokenChar + "\" at position " + token.getPosition()
										+ ". Variable name expected.");
						}

					case 4: // we expect closing curly thingy here
						switch (tokenChar) {
							
							case '}':
								// we have closing brace for the variable name
								// so get the name and query value provider for the value
								
								// but first, check syntax
								if (!syntaxChecker.checkName(currentVariableName)) {
									throw new IllegalArgumentException("Invalid variable declaration \""
											+ currentVariableName + "\" at position "
											+ currentVariableStart + ".");
								}

								T variableValue = valueProvider.getValue(currentVariableName);
								
								if (variableValue == null) {
									throw new IllegalArgumentException("Unknown variable \""
											+ currentVariableName + "\" at position " 
											+ currentVariableStart + ".");
								}
								
								resultBuilder.append(variableValue.toString());
								
								state = 0;
								break;
								
							default:
								throw new IllegalArgumentException("Unexpected token \"" 
										+ tokenChar + "\" at position " + token.getPosition()
										+ ". Closing \"}\" expected.");
						}
						
						break;
						
					default:
						throw new IllegalArgumentException("Internal error.");
				}
			} else {
				String tokenString = ((StringTokenizer.StringToken) token).getString();
				
				switch (state) {
					case 0: // string token
						resultBuilder.append(tokenString);
						state = 0;
						break;
					
					case 1: // after the $ sign, we expect { and not string token
						throw new IllegalArgumentException("Unexpected token at position "
								+ token.getPosition() + ". \"{\" expected.");
						
					case 2: // escape sequence, this means, we have unknown escape, we will leave it be
						resultBuilder.append('\\');
						resultBuilder.append(tokenString);
						state = 0;
						break;
						
					case 3: // this token has to be variable name
						
						// we will store it for later use
						currentVariableName = tokenString;
						currentVariableStart = token.getPosition() - 2;
						
						state = 4;
						break;

					default:
					case 4: // ok, so this is not cool
						throw new IllegalArgumentException("Internal error at position \""
								+ token.getPosition() + "\".");
				}
			}			
		}
		
		switch (state) {
			case 0:
				// this is ok
				break;
				
			case 1:
				throw new IllegalArgumentException("Unexpected end of input. Variable declaration expected.");
				
			case 2:
				throw new IllegalArgumentException("Unexpected end of input."
						+ " Second character of escape sequence expected.");
				
			case 3:
				throw new IllegalArgumentException("Unexpected end of input. Variable name expected.");
				
			default:
			case 4:
				throw new IllegalArgumentException("Unexpected end of input. Closing \"}\" expected.");
		}
		
		return resultBuilder.toString();
	}

	/**
	 * Parse string and substitute all variables with their values.
	 * 
	 * @param <T> Type of values.
	 * 
	 * @param input Input string.
	 * @param values Map which contains names and values of variables that are used in the input string.
	 * 
	 * @return String with variables substituted with their values.
	 * 
	 * @throws IllegalArgumentException If input string is invalid.
	 */
	public < T > String parseString(String input, Map< String, T > values) 
		throws IllegalArgumentException {
		
		return parseString(input, new MapVariableValueProvider< T >(values));
	}
	
	/**
	 * Data provider which uses Map as a source of the data.
	 * 
	 * @param <T> Type of data provider stores.
	 *
	 * @author Branislav Repcek
	 */
	private class MapVariableValueProvider< T > implements VariableValueProviderInterface< T > {

		/**
		 * Names and values of variables.
		 */
		private Map< String, T > values;
		
		/**
		 * Create new data provider.
		 * 
		 * @param values Map which contains names and values of variables.
		 */
		public MapVariableValueProvider(Map< String, T > values) {
			
			if (values == null) {
				
				throw new IllegalArgumentException("No values defined.");
			}
			
			this.values = values;
		}
		
		/*
		 * @see almostdefault.VariableParser.VariableValueProviderInterface#getValue(java.lang.String)
		 */
		public T getValue(String variableName) {
			
			return values.get(variableName);
		}
	}
	
	/**
	 * This class uses regular expressions to construct checker for names of variables.
	 *
	 * @author Branislav Repcek
	 */
	private class RegexpVariableNameSyntaxChecker implements VariableNameSyntaxCheckerInterface {

		/**
		 * Regexp used to check variable names.
		 */
		private Pattern pattern;
		
		/**
		 * Create syntax checker from given regular expression.
		 * 
		 * @param regexp Regular exprssion that will be used to check variable names.
		 *  
		 * @throws PatternSyntaxException If given regexp is invalid.
		 */
		public RegexpVariableNameSyntaxChecker(String regexp) throws PatternSyntaxException {
			
			pattern = Pattern.compile(regexp);
		}
		
		/*
		 * @see almostdefault.VariableParser.VariableNameSyntaxCheckerInterface#checkName(java.lang.String)
		 */
		public boolean checkName(String name) {
			
			return pattern.matcher(name).matches();
		}
	}
}
