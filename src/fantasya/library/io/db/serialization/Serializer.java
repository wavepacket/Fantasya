package fantasya.library.io.db.serialization;

import java.util.LinkedHashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fantasya.library.io.db.complex.ComplexHandler;
import fantasya.library.io.db.util.SerializedData;

/** Loads or saves all data.
 * 
 * The serializer basically takes an adapter (to convert the data from the
 * underlying database, xml file etc. to a device-independent SerializedData representation
 * and back), a list of ComplexHandlers (to convert the SerializedData objects
 * into game structures and back) with associated table names
 * (to load the data in proper order, e.g., items for units after the units
 * themselves), and a list of post processors (which are called for extra setup,
 * after loading certain tables). Then it throws together all that and loads or
 * saves the game data in one go.
 * 
 * Note: You should not normally construct such an object yourself. Use a
 * factory instead, which contains all the detailed knowledge about the load
 * order and setup.
 */
public class Serializer {

	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	private Adapter adapter;
	private Map<String,ComplexHandler> handlerMap;
	
	/** Constructs a new Serializer.
	 * 
	 * Note that serializers should only be build through the SerializerFactory.
	 * 
	 * @param adapter  An adapter to convert table data from/to the underlying data 
	 *                 source
	 * @param handlers A mapping from table names to handlers that do the actual
	 *                 loading and saving. Note that the order of the keys determines
	 *                 the order in which tables are loaded, this is why we require a
	 *                 LinkedHashMap here.
	 * @param postMap  a mapping from table names to PostProcessors. If no
	 *                 post-processors are supplied for some table, this step is
	 *                 simply skipped, so in the simplest case, this may be an
	 *                 empty map.
	 */
	public Serializer(Adapter adapter, 
			LinkedHashMap<String,ComplexHandler> handlerMap) {
		if (adapter == null) {
			throw new IllegalArgumentException("Serializer requires a valid adapter.");
		}
		if (handlerMap == null) {
			throw new IllegalArgumentException("Serializer requires a list of tables and handlers.");
		}
		
		this.adapter = adapter;
		this.handlerMap = handlerMap;
	}
	
	/** Opens the adapter, loads all the data, and closes the adapter.
	 * 
	 * After calling this function on valid game data, you should have a working
	 * and completely set up game.
	 * 
	 * @throws RuntimeException if anything goes wrong. The adapter is closed in
	 * any case.
	 */
	public void loadAll() {
		adapter.open();
		
		SerializedData data = null;
		
		try {
			for (String table : handlerMap.keySet()) {
				logger.info("Loading table: {}", table);
				
				data = adapter.readData(table);
				handlerMap.get(table).generateAll(data);
			}
			for (String table : handlerMap.keySet()) {
				logger.info("Loading objects: {}", table);
				
				handlerMap.get(table).loadAll(data);
			}
		} catch (Exception e) {
			adapter.close();
			throw new RuntimeException("Error while loading data.", e);
		}
	
		adapter.close();
	}
	
	/** Opens the adapter, saves all game data, and closes the adapter.
	 * 
	 * After calling this function with valid set up game data, you should obtain
	 * a valid serialized version of the game.
	 * 
	 * @throws RuntimeException if anything goes wrong. The adapter is closed in
	 * any case.
	 */
	public void saveAll() {
		adapter.open();
		
		try {
			for (String table : handlerMap.keySet()) {
				logger.info("Saving table: {}", table);
				SerializedData data = handlerMap.get(table).saveAll();
				adapter.writeData(table, data);
			}
		} catch (Exception e) {
			adapter.close();
			throw new RuntimeException("Error while saving data.", e);
		}
		
		adapter.close();
	}
}