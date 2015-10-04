package fantasya.library.io.db.complex;

import fantasya.library.io.db.util.SerializedData;


/** Defines a complex loading procedure.
  *
  * While the basic serializers define how a single object is loaded,
  * implementations of this interface process a whole set of data values. For
  * example, if you want to load all units, you need to first purge the unit
  * cache, and then load the units one by one and put them into the cache. This
  * is out of the scope of the basic serializers, and done by appropriate
  * implementations of this class.
  */

public interface ComplexHandler {
	
	/** generates all needed objects of the data from the bunch of data items.
	  *
	  * @param input the input to deserialize into a set of objects.
	  */
	
	public void generateAll(SerializedData input);

	/** Loads the data from the bunch of data items.
	  *
	  * @param input the input to deserialize into a set of objects.
	  */
	public void loadAll(SerializedData input);

	/** Saves the whole bunch of objects into a map.
	  *
	  * Essentially the reverse of loadAll().
	  */
	public SerializedData saveAll();
}
