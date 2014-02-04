package net.pferdimanzug.hearthstone.analyzer.game.cards.concrete.neutral;

import net.pferdimanzug.hearthstone.analyzer.game.cards.MinionCard;
import net.pferdimanzug.hearthstone.analyzer.game.cards.Rarity;
import net.pferdimanzug.hearthstone.analyzer.game.entities.Entity;
import net.pferdimanzug.hearthstone.analyzer.game.entities.EntityType;
import net.pferdimanzug.hearthstone.analyzer.game.entities.heroes.HeroClass;
import net.pferdimanzug.hearthstone.analyzer.game.entities.minions.Minion;
import net.pferdimanzug.hearthstone.analyzer.game.events.IGameEvent;
import net.pferdimanzug.hearthstone.analyzer.game.events.KillEvent;
import net.pferdimanzug.hearthstone.analyzer.game.spells.DrawCardSpell;
import net.pferdimanzug.hearthstone.analyzer.game.spells.trigger.GameEventTrigger;
import net.pferdimanzug.hearthstone.analyzer.game.spells.trigger.MinionDeathTrigger;
import net.pferdimanzug.hearthstone.analyzer.game.spells.trigger.SpellTrigger;

public class CultMaster extends MinionCard {
	
	private class CultMasterTrigger extends MinionDeathTrigger {

		@Override
		public boolean fire(IGameEvent event, Entity host) {
			KillEvent killEvent = (KillEvent) event;
			// not a minion
			if (killEvent.getVictim().getEntityType() != EntityType.MINION) {
				return false;
			}
			// not a friendly minion
			if (killEvent.getVictim().getOwner() != host.getOwner()) {
				return false;
			}
			
			// card says 'when one of your OTHER minion dies'
			return killEvent.getVictim() != host;
		}
		
	}

	public CultMaster() {
		super("Cult Master", Rarity.COMMON, HeroClass.ANY, 4);
	}

	@Override
	public Minion summon() {
		GameEventTrigger minionDieTrigger = new CultMasterTrigger();
		SpellTrigger trigger = new SpellTrigger(minionDieTrigger, new DrawCardSpell());
		Minion cultMaster = createMinion(4, 2);
		cultMaster.setSpellTrigger(trigger);
		return cultMaster;
	}

}