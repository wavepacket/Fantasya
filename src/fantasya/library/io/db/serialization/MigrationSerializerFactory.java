package fantasya.library.io.db.serialization;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import de.x8bit.Fantasya.Atlantis.Partei;
import de.x8bit.Fantasya.Host.serialization.basic.ParteiSerializer;
import de.x8bit.Fantasya.Host.serialization.complex.CacheFillerHandler;
import fantasya.library.id.Identifiable;
import fantasya.library.io.db.Database;
import fantasya.library.io.db.DatabaseAdapter;
import fantasya.library.io.db.complex.ComplexHandler;
import fantasya.library.io.db.serialization.Adapter;
import fantasya.library.io.db.serialization.Serializer;

public class MigrationSerializerFactory {
	
	private static Map<String, Map<String, ? extends Identifiable>> cacheMap = new HashMap<String, Map<String, ? extends Identifiable>>(); 
	
	public static Serializer buildSerializer(Database db) {
		Adapter adapter = new DatabaseAdapter(db);
		
		LinkedHashMap<String,ComplexHandler> handlerMap = new LinkedHashMap<String,ComplexHandler>();
		
		return new Serializer(adapter, handlerMap);
	}
}
