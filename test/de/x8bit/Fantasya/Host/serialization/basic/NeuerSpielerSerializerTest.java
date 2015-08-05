package de.x8bit.Fantasya.Host.serialization.basic;

import de.x8bit.Fantasya.Atlantis.Units.Elf;
import de.x8bit.Fantasya.Atlantis.Units.Troll;
import de.x8bit.Fantasya.Host.serialization.util.DataAnalyzer;
import de.x8bit.Fantasya.Host.EVA.util.NeuerSpieler;
import java.util.HashMap;
import java.util.Map;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

public class NeuerSpielerSerializerTest {
	
	private Map<String,String> serializedMap = new HashMap<String,String>();
	
	private NeuerSpielerSerializer serializer = new NeuerSpielerSerializer();
	
	@Before
	public void setup() {
		serializedMap.put("email", "player@domain");
		serializedMap.put("rasse", "Elf");
		serializedMap.put("tarnung", "");
		serializedMap.put("holz", "15");
		serializedMap.put("eisen", "30");
		serializedMap.put("steine", "48");
		serializedMap.put("insel", "35");
		serializedMap.put("Name", "Test");
		serializedMap.put("Description", "This is a test.");
		serializedMap.put("PlayerId", "10");
	}
	
	@Test
	public void keysetsAreClassifiedAsValidOrInvalid() {
		assertTrue("Valid keyset was not recognized.",
				serializer.isValidKeyset(serializedMap.keySet()));
		
		serializedMap.remove("email");
		assertFalse("Invalid keyset was not recognized.",
				serializer.isValidKeyset(serializedMap.keySet()));
	}
	
	@Test
	public void objectIsLoadedCorrectly() {
		NeuerSpieler ns = serializer.load(serializedMap);
		
		assertEquals("Inncorrect email set.",
				serializedMap.get("email"), ns.getEmail());
		assertEquals("Incorrect race set.",
				Elf.class, ns.getRasse());
		assertNull("Incorrect tarn race set.", ns.getTarnung());
		assertEquals("Incorrect amount of wood set.",
				Integer.decode(serializedMap.get("holz")), (Integer)ns.getHolz());
		assertEquals("Incorrect amount of iron set.",
				Integer.decode(serializedMap.get("eisen")), (Integer)ns.getEisen());
		assertEquals("Incorrect amount of stone set.",
				Integer.decode(serializedMap.get("steine")), (Integer)ns.getSteine());
		assertEquals("Incorrect island set.",
				Integer.decode(serializedMap.get("insel")), (Integer)ns.getInsel());
		assertEquals("Incorrect name.",
				serializedMap.get("Name"), ns.getName());
		assertEquals("Incorrect description.",
				serializedMap.get("Description"), ns.getDescription());
		assertEquals("Incorrect playerId.",
				Integer.decode(serializedMap.get("PlayerId")), (Integer)ns.getPlayerId());
	}
	
	@Test
	public void tarnRaceIsLoadedCorrectly() {
		serializedMap.put("tarnung", "Troll");
		
		NeuerSpieler ns = serializer.load(serializedMap);
		
		assertEquals("Tarn race was not set correctly.",
				Troll.class, ns.getTarnung());
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void invalidRaceCausesAnException() {
		serializedMap.put("rasse", "notExistant");
		serializer.load(serializedMap);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void invalidTarnRaceCausesAnException() {
		serializedMap.put("tarnung", "notExistant");
		serializer.load(serializedMap);
	}
	
	@Test
	public void savingWorksWithEmptyTarnRace() {
		NeuerSpieler ns = serializer.load(serializedMap);
		DataAnalyzer analyzer = new DataAnalyzer(serializer.save(ns));

		assertEquals("Wrong number of elements saved.", 1, analyzer.size());
		assertTrue("Wrong data saved.", analyzer.contains(serializedMap));
	}
	
	@Test
	public void savingWorksWithTarnRace() {
		serializedMap.put("tarnung", "Troll");
		NeuerSpieler ns = serializer.load(serializedMap);
		DataAnalyzer analyzer = new DataAnalyzer(serializer.save(ns));
		
		assertEquals("Wrong number of elements saved.", 1, analyzer.size());
		assertTrue("Wrong data saved.", analyzer.contains(serializedMap));
	}
}