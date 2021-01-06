package main;

/**
 * A general use linked list structure with a circular trail of connections.  There's no beginning or end, just an
 * iterator variable marking the current node.
 * 
 * There are two iterator implemented: current and peek.  The current iterator marks the current item in the list.
 * The peek iterator can be used to examine the list items without disturbing the current iterator.
 * 
 * @author john
 * @version 1.0 2014-12-05
 * @param <T> the class type for use in the structure.
 */
public class CircularLinkedList<T>
{
	private Node<T> mCurrent = null;
	private Node<T> mPeekCurrent = null; // For iterating the list without changing the current node.
	private boolean mAtEnd = false; // flags where the peek trail has made a full circle.
	private int mSize = 0; // number of items in the list.
	
	public CircularLinkedList() { return; }
	
	/**
	 * Inserts an item into the list ahead of the current iterator.
	 * @param item T type used for the class.
	 * @return a copy of the item inserted.
	 */
	public T insert(T item)
	{
		Node<T> newItem = new Node<T>(item);
		if(this.mCurrent == null || this.mCurrent.previous == null){
			this.mCurrent = newItem;
			this.mCurrent.next = this.mCurrent;
			this.mCurrent.previous = this.mCurrent;
			this.mPeekCurrent = this.mCurrent;
		}else{
			newItem.next = this.mCurrent.next;
			newItem.previous = this.mCurrent;
			this.mCurrent.next = newItem;
			this.mCurrent = newItem;
		}
		this.mSize++;
		return item;
	}
	
	/**
	 * Advances the current iterator and retrieves the next item in the list. 
	 * @return T type used for the class.
	 */
	public T getNext()
	{
		if(this.mCurrent == null || this.mCurrent.next == null){
			return null;
		}	
		this.mCurrent = this.mCurrent.next;
		return this.mCurrent.item;
	}
	
	/**
	 * Sets the peek iterator at the same position as current iterator.  This should be used before attempting
	 * a full iteration of the list in order to ensure all items are visited.
	 */
	public void peekRewind()
	{
		this.mPeekCurrent = this.mCurrent;
	}
	
	/**
	 * For use in a loop's conditional while iterating. 
	 * @return boolean true if peek iterator has not yet returned to the current iterator, false otherwise.
	 */
	public boolean hasNextPeek()
	{
		if(this.mPeekCurrent == null || this.mPeekCurrent.next == null){
			return false;
		}
		// Using the mAtEnd flag here makes this method allow one more iteration before returning false.
		// Otherwise the peek iterator would miss the current item.
		if(this.mAtEnd){
			return false;
		}
		if(this.mPeekCurrent.next == this.mCurrent){
			this.mAtEnd = true;
		}
		return true;
	}
	
	/**
	 * Peeks at the next item in the list.
	 * @return T type list item.  Can return null if the peek iterator has already returned to the current
	 * iterator. 
	 */
	public T peekNext()
	{
		T item = null;
		if(this.mPeekCurrent == null || this.mPeekCurrent.next == null){
			item = null;
		}else{
			this.mPeekCurrent = this.mPeekCurrent.next;
			item = this.mPeekCurrent.item;
		}
		return item;
	}
	
	/**
	 * The number of items currently in the list.
	 * @return int number of items.
	 */
	public int size()
	{
		return this.mSize;
	}
	
	private class Node<T>
	{
		public T item = null;
		public Node<T> previous = null;
		public Node<T> next = null;
		
		public Node(T item)
		{
			this.item = item;
			return;
		}
	}
}
