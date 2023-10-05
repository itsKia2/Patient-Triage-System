package priorityqueue;

import java.lang.reflect.Array;
import java.util.Comparator;

public class Heap<T> implements PriorityQueueADT<T> {

  private int numElements;
  private T[] heap;
  private boolean isMaxHeap;
  private Comparator<T> comparator;
  private final static int INIT_SIZE = 5;

  /**
   * Constructor for the heap.
   * @param comparator comparator object to define a sorting order for the heap elements.
   * @param isMaxHeap Flag to set if the heap should be a max heap or a min heap.
   */
  public Heap(Comparator<T> comparator, boolean isMaxHeap) {
      this.isMaxHeap = isMaxHeap;
      this.comparator = comparator;
      //for (int i = 0; i < INIT_SIZE; i++) {
      //  heap[i] = null;
      //} --> not working
      this.numElements = 0;
      this.heap = (T[])new Object[INIT_SIZE];
      // not sure why yellow lines appearing under this
  }

  /**
   * This results in the entry at the specified index "bubbling up" to a location
   * such that the property of the heap are maintained. This method should run in
   * O(log(size)) time.
   * Note: When enqueue is called, an entry is placed at the next available index in 
   * the array and then this method is called on that index. 
   *
   * @param index the index to bubble up
   * @throws IndexOutOfBoundsException if invalid index
   */
  public void bubbleUp(int index) {
    //check if given index is out of bounds
    if ((index < 0) || (index > numElements)) {
      throw new IndexOutOfBoundsException();
    }
    while (!(index < 0)) {
      int parentIndex = getParentOf(index);
      if (compareElements(heap[index], heap[parentIndex]) > 0) {
        swapElements(index, parentIndex);
        index = parentIndex;
      } else {
        return;
      }
    }
  }

  /**
   * This method results in the entry at the specified index "bubbling down" to a
   * location such that the property of the heap are maintained. This method
   * should run in O(log(size)) time.
   * Note: When remove is called, if there are elements remaining in this
   *  the bottom most element of the heap is placed at
   * the 0th index and bubbleDown(0) is called.
   * 
   * @param index
   * @throws IndexOutOfBoundsException if invalid index
   */
  public void bubbleDown(int index) {
    if ((index < 0) || (index > this.numElements)) {
      throw new IndexOutOfBoundsException();
    }
    int leftChildIndex = getLeftChildOf(index);
    T currData = this.heap[index];
    while (this.numElements > leftChildIndex) {
      T maxData = currData;
      int maxDataIndex = -1; // not sure what the index is yet so init to -1
      
      int i = 0; // instance var
      // i < 2 condition is necessary since only 2 children for a node
      while (((i + leftChildIndex) < this.numElements) && (i < 2)) {
        if (compareElements(maxData, heap[i + leftChildIndex]) < 0) {
          // update the max value vars
          maxDataIndex = i + leftChildIndex;
          maxData = heap[i + leftChildIndex];
        }
        i = i + 1;
      }

      if (!(maxData == currData)) {
        swapElements(index, maxDataIndex);
        index = maxDataIndex;
        leftChildIndex = getLeftChildOf(index);
      } else {
        return; // no need to do anything if maxData == currData
      }
    }
  }

  /**
   * Test for if the queue is empty.
   * @return true if queue is empty, false otherwise.
   */
  public boolean isEmpty() {
    boolean flag = this.getSize() == 0;
    return flag;
  }

  /**
   * Number of data elements in the queue.
   * @return the size
   */
  public int getSize(){
    return this.numElements;
  }

  /**
   * Compare method to implement max/min heap behavior. It changes the value of a variable, compareSign, 
   * based on the state of the boolean variable isMaxHeap. It then calls the compare method from the 
   * comparator object and multiplies its output by compareSign.
   * @param element1 first element to be compared
   * @param element2 second element to be compared
   * @return positive int if {@code element1 > element2}, 0 if {@code element1 == element2}, 
   * negative int otherwise (if isMaxHeap),
   * return negative int if {@code element1 > element2}, 0 if {@code element1 == element2}, 
   * positive int otherwise (if ! isMinHeap).
   */
  public int compareElements(T element1 , T element2) {
    int result = 0;
    int compareSign =  -1;
    if (isMaxHeap) {
      compareSign = 1;
    }
    result = compareSign * comparator.compare(element1, element2);
    return result;
  }

  /**
   * Return the element with highest (or lowest if min heap) priority in the heap 
   * without removing the element.
   * @return T, the top element
   * @throws QueueUnderflowException if empty
   */
  public T peek() throws QueueUnderflowException {
    if (this.isEmpty()) {
      throw new QueueUnderflowException();
    }
    T data = null;
    data = heap[0];
    return data;
  }  

  /**
   * Removes and returns the element with highest (or lowest if min heap) priority in the heap.
   * @return T, the top element
   * @throws QueueUnderflowException if empty
   */
  public T dequeueElement() throws QueueUnderflowException{
    if (this.isEmpty()) {
      throw new QueueUnderflowException();
    }
    T data = null;
    data = heap[0];
    this.numElements = this.numElements - 1;
    this.heap[0] = this.heap[this.numElements];
    this.heap[this.numElements] = null;
    bubbleDown(0);
    return data;
  }

  /**
   * Enqueue the element.
   * @param the new element
   */
  public void enqueueElement(T newElement) {
    if (newElement == null) {
      throw new NullPointerException();
    }
    if (this.numElements == heap.length) {
      // array has run out of space, just double the array size
      doubleArraySize();
    }
    heap[this.numElements] = newElement;
    bubbleUp(this.numElements);
    this.numElements = this.numElements + 1;
    // increment numElements
  }

  private int getLeftChildOf(int parentIndex) throws IndexOutOfBoundsException {
    if (parentIndex < 0) {
      throw new IndexOutOfBoundsException();
    }
    return (2 * parentIndex + 1);
  }

  private int getRightChildOf(int parentIndex) throws IndexOutOfBoundsException {
    // this method is not used
    if (parentIndex < 0) {
      throw new IndexOutOfBoundsException();
    }
    return (2 * parentIndex + 2);
  }

  private int getParentOf(int index) throws IndexOutOfBoundsException {
    if (index < 0) {
      throw new IndexOutOfBoundsException();
    }
    return ((index - 1) / 2);
  }

  private void swapElements(int index1, int index2) {
    T temp = heap[index1];
    heap[index1] = heap[index2];
    heap[index2] = temp;
  }

  private void doubleArraySize() {
    // not sure why this line has a yellow line below it 
    T[] doubledArray = (T[])new Object[heap.length * 2];
    // copy all elements from old array to new one
    for (int i = 0; i < heap.length; i++) {
      doubledArray[i] = heap[i];
    }
    this.heap = doubledArray;
  }

}