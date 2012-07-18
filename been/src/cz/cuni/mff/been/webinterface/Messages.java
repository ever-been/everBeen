/*
 *  BEEN: Benchmarking Environment
 *  ==============================
 *
 *  File author: David Majda
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
package cz.cuni.mff.been.webinterface;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Class encapsulating a list of messages, which can be added and iterated
 * over.
 * 
 * @author David Majda
 */
public class Messages implements Iterable<Message> {
	/** List of messages. */
	private List<Message> messages = new LinkedList<Message>();

	/**
	 * Iterator used for iterating over the messages. We can't use the iterator
	 * of the <code>messages</code> itself, because we don't want to support the
	 * <code>remove</code> operation. However, our iterator contains a reference
	 * to the <code>messages</code>' iterator and all operations except
	 * <code>remove</code> are simply forwarded to it.
	 * 
	 * @author David Majda
	 */
	public class MessagesIterator implements Iterator<Message> {
		/** Message list iterator. */
		private Iterator<Message> listIterator;

		/**
		 * Returns <code>true</code> if the iteration has more elements. (In other
		 * words, returns <code>true</code> if <code>next</code> would return an
		 * element rather than throwing an exception.)
		 * 
		 * @return <code>true</code> if the iterator has more elements
		 * 
		 * @see java.util.Iterator#hasNext()
		 */
		public boolean hasNext() {
			return listIterator.hasNext();
		}

		/**
		 * Returns the next element in the iteration. Calling this method
		 * repeatedly until the {@link #hasNext()} method returns false will return
		 * each element in the underlying collection exactly once.
		 * 
		 * @return the next element in the iteration
		 * @exception NoSuchElementException if iteration has no more elements
		 *
		 * @see java.util.Iterator#next()
		 */
		public Message next() {
			return listIterator.next();
		}

		/**
		 * Throws <code>UnsupportedOperationException</code>, because list of
		 * messages can't be modified.
		 * 
		 * @throws UnsupportedOperationException thrown always, list of
		 *                                        messages can't be modified
		 *                                        
		 * @see java.util.Iterator#remove()
		 */
		public void remove() {
			throw new UnsupportedOperationException("Messages can't be removed.");
		}

		public MessagesIterator(Iterator<Message> listIterator) {
			this.listIterator = listIterator;
		}

	}

	/**
	 * Adds new message in plain text format.
	 * 
	 * @param text message text
	 */
	public void addTextMessage(String text) {
		messages.add(new Message(text, Message.Format.TEXT));
	}

	/**
	 * Adds new message in HTML format.
	 * 
	 * @param text message text
	 */
	public void addHTMLMessage(String text) {
		messages.add(new Message(text, Message.Format.HTML));
	}

	/**
	 * Returns the number of messages in the list.
	 * 
	 * @return number of messages in the list
	 */
	public int size() {
		return messages.size();
	}

	/**
	 * Determines whether the list of messages is empty.
	 * 
	 * @return <code>true</code> if there are no messages in the list;
	 *          <code>false</code> otherwise
	 */
	public boolean isEmpty() {
		return messages.isEmpty();
	}

	/**
	 * Returns an iterator over a list of messages.
	 * 
	 * @return an iterator
	 * 
	 * @see java.lang.Iterable#iterator()
	 */
	public Iterator<Message> iterator() {
		return new MessagesIterator(messages.iterator());
	}

	/**
	 * Removes all messages from the list.
	 */
	public void clear() {
		messages.clear();
	}
}
