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

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * Parse string and split it into tokens. Token delimiters are characters specified during creation
 * time. This is class is optimized for "create once, use many times" scenario. This is different
 * from the StringTokenizer provided in java.util package, since that one has to be created for every
 * string again.
 *
 * @author Branislav Repcek
 */
public class StringTokenizer {

	/**
	 * Array of delimiter characters. Sorted in ascending order.
	 */
	private char []delimiters;
	
	/**
	 * Minimum value of the delimiter character.
	 */
	private char min;
	
	/**
	 * Maximum value of the delimiter character.
	 */
	private char max;
	
	/**
	 * Abstract base class for tokens.
	 *
	 * @author Branislav Repcek
	 */
	public abstract static class Token {

		/**
		 * Position of the token in the input.
		 */
		private int position;
		
		/**
		 * Create new token.
		 * 
		 * @param position Position of the token in the input.
		 */
		public Token(int position) {
			
			this.position = position;
		}
		
		/**
		 * Test if given token is delimiter token.
		 * 
		 * @return <code>true</code> if token is delimiter character, <code>false</code> otherwise.
		 */
		public abstract boolean isDelimiter();
		
		/**
		 * @return Position of the token in the input.
		 */
		public int getPosition() {
			
			return position;
		}
	}
	
	/**
	 * Class which represents delimiter token from the string.
	 *
	 * @author Branislav Repcek
	 */
	public static class DelimiterToken extends Token {

		/**
		 * Delimiter character.
		 */
		private char ch;
		
		/**
		 * Create new delimiter token.
		 * 
		 * @param ch Delimiter character.
		 * @param position Position of the token in the input.
		 */
		public DelimiterToken(char ch, int position) {
			
			super(position);
			
			this.ch = ch;
		}

		/*
		 * @see cz.cuni.mff.been.common.StringTokenizer.Token#isDelimiter()
		 */
		@Override
		public boolean isDelimiter() {
			
			return true;
		}
		
		/**
		 * Get delimiter character.
		 * 
		 * @return Delimiter character.
		 */
		public char delimiterChar() {
			
			return ch;
		}
		
		/*
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			
			return String.valueOf(ch);
		}
	}
	
	/**
	 * String token - token that is not a delimiter.
	 *
	 * @author Branislav Repcek
	 */
	public static class StringToken extends Token {
		
		/**
		 * Value of the token.
		 */
		private String s;
		
		/**
		 * Create new token.
		 * 
		 * @param s String which will be stored in the token.
		 * @param position Position of the token in the input.
		 */
		public StringToken(String s, int position) {
			
			super(position);
			
			this.s = s;
		}
		
		/*
		 * @see cz.cuni.mff.been.common.StringTokenizer.Token#isDelimiter()
		 */
		@Override
		public boolean isDelimiter() {
			
			return false;
		}
		
		/**
		 * @return Value of this token.
		 */
		public String getString() {
			
			return s;
		}
		
		/*
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			
			return s;
		}
	}
	
	/**
	 * Create new tokenizer with given delimiters.
	 * 
	 * @param delimiters String containing delimiters this tokenizer will use.
	 * 
	 * @throws IllegalArgumentException if input string with delimiters is empty or <code>null</code>.
	 */
	public StringTokenizer(String delimiters) throws IllegalArgumentException {
		
		if (delimiters == null) {
			throw new IllegalArgumentException("null delimiters are not allowed in tokenizer.");
		}
		
		if (delimiters.length() == 0) {
			throw new IllegalArgumentException("Empty delimiter string is not allowed in tokenizer.");
		}
		
		this.delimiters = delimiters.toCharArray();
		Arrays.sort(this.delimiters);
		min = this.delimiters[0];
		max = this.delimiters[this.delimiters.length - 1];
	}
	
	/**
	 * @return Array of delimiter characters.
	 */
	public char []getDelimiters() {
		
		return delimiters.clone();
	}

	/**
	 * Parse string and split it into tokens. Note that delimiter characters are also considered as
	 * tokens and therefore are also in the resulting list.
	 * 
	 * @param input String to parse.
	 * 
	 * @return List containing all tokens from the string.
	 */
	public List< Token > tokenize(String input) {

		return tokenize(input, true);
	}

	/**
	 * Parse given string and split it into tokens separated with delimiter characters.
	 * 
	 * @param input Input string.
	 * @param includeDelimiters If set to <code>true</code> output will include tokens with 
	 *        delimiter characters. If set to <code>false</code> only non-delimiter tokens will be
	 *        included in the output.
	 *        
	 * @return List of tokens from the string.
	 */
	public List< Token > tokenize(String input, boolean includeDelimiters) {

		LinkedList< Token > tokens = new LinkedList< Token >();
		StringBuilder tokenBuilder = new StringBuilder();
		
		for (int i = 0; i < input.length(); ++i) {
			
			char c = input.charAt(i);
			if ((c >= min) && (c <= max) && (Arrays.binarySearch(delimiters, c) >= 0)) {
				if (tokenBuilder.length() > 0) {
					tokens.add(new StringToken(tokenBuilder.toString(), i - tokenBuilder.length()));
					tokenBuilder.setLength(0);
				}
				
				if (includeDelimiters) {
					tokens.add(new DelimiterToken(c, i));
				}
			} else {
				tokenBuilder.append(c);
			}
		}
		
		if (tokenBuilder.length() > 0) {
			tokens.add(new StringToken(tokenBuilder.toString(), input.length() - tokenBuilder.length()));
		}
		
		return tokens;
	}
}
