package fantasya.library.effect.impl;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.junit.Test;

import fantasya.library.effect.Effect;

public class FixedEffectsImplTest {

	@Test
	public void testMultipleEffects() {
		final Unit unit = new Unit("id");
		if (!unit.setEffect(new RaceModSkill(2))) {
			fail("Not set RaceModSkill");
		}
		if (!unit.setEffect(new CultureModSkill(-1))) {
			fail("Not set CultureModSkill");
		}
		
		Collection<Effect> skillModEffects = unit.getChildrenOfEffect(ModSkill.class);
		
		int bonus = 0;
		
		for (Effect effect : skillModEffects) {
			bonus += ((ModSkill) effect).getSkillMod();
		}
		
		assertEquals("Bonus is not 1!", bonus, 1);
		assertEquals("AllEffectSite is not 2!", unit.allEffectSize(), 2);
		assertEquals("EffectSize is not 2!", unit.effectSize(), 2);
		
		// fail("Not yet implemented");
	}
	
	@Test
	public void testDoubleEffects() {
		final Unit unit = new Unit("id");
		if (!unit.setEffect(new RaceModSkill(2))) {
			fail("Not set RaceModSkill I");
		}
		if (!unit.setEffect(new CultureModSkill(-1))) {
			fail("Not set CultureModSkill");
		}
		if (!unit.setEffect(new RaceModSkill(2))) {
			fail("Not set RaceModSkill II");
		}
		
		Collection<Effect> skillModEffects = unit.getChildrenOfEffect(ModSkill.class);
		
		int bonus = 0;
		
		for (Effect effect : skillModEffects) {
			bonus += ((ModSkill) effect).getSkillMod();
		}
		
		assertEquals("Bonus is not 1!", bonus, 1);
		assertEquals("AllEffectSite is not 2!", unit.allEffectSize(), 2);
		assertEquals("EffectSize is not 2!", unit.effectSize(), 2);
		
		// fail("Not yet implemented");
	}
	
	@Test
	public void testMultipleDeepEffects() {
		final Unit unit = new Unit("id");
		final UnitType unitType = new UnitType("Type");
		if (!unitType.setEffect(new RaceModSkill(2))) {
			fail("Not set RaceModSkill");
		}
		if (!unitType.setEffect(new SpecialRaceModSkill(4))) {
			fail("Not set RaceModSkill");
		}
		
		if (!unit.setEffect(new CultureModSkill(-1))) {
			fail("Not set CultureModSkill");
		}
		if (!unit.setEffect(new CultureModSkill(-1))) {
			fail("Not set CultureModSkill");
		}
		if (!unit.setEffect(unitType)) {
			fail("Not set unit type");
		}
		
		Collection<Effect> skillModEffects = unit.getChildrenOfEffect(ModSkill.class);
		
		int bonus = 0;
		
		for (Effect effect : skillModEffects) {
			bonus += ((ModSkill) effect).getSkillMod();
		}
		
		assertEquals("Bonus is not 5!", bonus, 5);
		assertEquals("AllEffectSite is not 3!", unit.allEffectSize(), 3);
		assertEquals("EffectSize is not 2!", unit.effectSize(), 2);
		
		// fail("Not yet implemented");
	}
	
	@Test
	public void testCannotReplaceDeepEffect() {
		final Unit unit = new Unit("id");
		final UnitType unitType = new UnitType("Type");
		if (!unitType.setEffect(new RaceModSkill(2))) {
			fail("Not set RaceModSkill");
		}
		if (!unit.setEffect(new CultureModSkill(-1))) {
			fail("Not set CultureModSkill");
		}
		if (!unit.setEffect(unitType)) {
			fail("Not set unit type");
		}
		
		if(unit.setEffect(new RaceModSkill(3))) {
			fail("Set a deep Effect! Should not do this!");
		}
		
		Collection<Effect> skillModEffects = unit.getChildrenOfEffect(ModSkill.class);
		
		int bonus = 0;
		
		for (Effect effect : skillModEffects) {
			bonus += ((ModSkill) effect).getSkillMod();
		}
		
		assertEquals(bonus, 1);
		
		// fail("Not yet implemented");
	}
	
	@Test
	public void testHasEffects() {
		final Unit unit = new Unit("id");
		if (!unit.setEffect(new RaceModSkill(2))) {
			fail("Not set RaceModSkill");
		}
		if (!unit.setEffect(new CultureModSkill(-1))) {
			fail("Not set CultureModSkill");
		}
		
		if (!unit.hasEffects()) {
			fail("Unit has effects!");
		}
		
		final Unit unit2 = new Unit("id");
		
		if (unit2.hasEffects()) {
			fail("Unit has no effects!");
		}
		
		final Unit unit3 = new Unit("id");
		final UnitType unitType = new UnitType("Type");
		if (!unitType.setEffect(new RaceModSkill(2))) {
			fail("Not set RaceModSkill");
		}
		if (!unit3.setEffect(unitType)) {
			fail("Not set unit type");
		}
		
		if (!unit.hasEffects()) {
			fail("Unit has an effect!");
		}
		
		// fail("Not yet implemented");
	}
	
	@Test
	public void testSetEffects() {
		final Unit unit = new Unit("id");
		if (!unit.setEffect(new RaceModSkill(3))) {
			fail("Not set RaceModSkill");
		}
		if (!unit.setEffect(new CultureModSkill(1))) {
			fail("Not set CultureModSkill");
		}
		
		List<Effect> effectList = new ArrayList<Effect>();
		
		effectList.add(new RaceModSkill(2));
		effectList.add(new CultureModSkill(-1));
		
		unit.setEffects(effectList);
		
		Collection<Effect> skillModEffects = unit.getChildrenOfEffect(ModSkill.class);
		
		int bonus = 0;
		
		for (Effect effect : skillModEffects) {
			bonus += ((ModSkill) effect).getSkillMod();
		}
		
		assertEquals("Bonus is not 1!", bonus, 1);
		assertEquals("AllEffectSite is not 2!", unit.allEffectSize(), 2);
		assertEquals("EffectSize is not 2!", unit.effectSize(), 2);
		
		// fail("Not yet implemented");
	}
	
	@Test
	public void testgetEffects() {
		final Unit unit = new Unit("id");
		if (!unit.setEffect(new RaceModSkill(2))) {
			fail("Not set RaceModSkill");
		}
		if (!unit.setEffect(new CultureModSkill(-1))) {
			fail("Not set CultureModSkill");
		}
		
		Collection<Effect> skillModEffects = unit.getEffects();
		
		assertEquals("Effect list size givven from unit is not 2!", skillModEffects.size(), 2);
		
		// fail("Not yet implemented");
	}
	
	private abstract class ModSkill implements Effect {
		
		int mod = 0;
		
		public ModSkill(int mod) {
			this.mod = mod;
		}
		
		public int getSkillMod() {
			return mod;
		}
	}
	
	private class RaceModSkill extends ModSkill {
		public  RaceModSkill(int mod) {
			super(mod);
		}
	}
	
	private class SpecialRaceModSkill extends ModSkill {
		public  SpecialRaceModSkill(int mod) {
			super(mod);
		}
	}
	
	private class CultureModSkill extends ModSkill {
		public  CultureModSkill(int mod) {
			super(mod);
		}
	}
	
	private class UnitType extends FixedEffectsImpl implements Effect {

		public UnitType(String id) {
			super(id);
		}
	}
	
	private class Unit extends FixedEffectsImpl {

		public Unit(String id) {
			super(id);
		}
	}
	
	// Problem: Partei hat Rasse und Einheit auch. Da sich die gleiche Klasse als Attribut ausschließt, gibt es hier ein Problem.
	// Rasse der Partei ist kein Attribut, sondern wird fuer sich gespeichert. Entscheidend fuer ist fuer die Berechnung der Rassenboni die Einheit.
	
	
}
