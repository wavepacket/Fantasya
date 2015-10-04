package fantasya.library.id;

/**
 * @author Michael Jahn
 * 
 * An interface identification of an object.
 */

public interface Identifiable extends Comparable<Object>, Cloneable {
	
	/**
	 * Returns the id as String.
	 * 
	 * @return id as String.
	 */
	public String getId();
	
	/**
	 * Only one Object. So clone is not supported.
	 * 
	 * @throws CloneNotSupportedException
	 */
	public Object clone() throws CloneNotSupportedException;
	
	/**
	 * 	Equation of id's. First integer id otherwise String id.
	 */
	@Override
	public boolean equals(Object obj);
	
	/**
	 * hashcode is generated out of the id. First integer otherwise String.
	 * 
	 * @return Den HashCode of the object.
	 */
	public int hashCode();
	
	/**
	 * Imposes a natural ordering on Identifiable objects. Especially with implementing sub classes of
	 * Identifiable, such orderings will often be established by the natural order of ids.
	 */
	public int compareTo(Object obj);
}
