package net.demilich.metastone.game.cards.costmodifier;

import net.demilich.metastone.game.Attribute;
import net.demilich.metastone.game.GameContext;
import net.demilich.metastone.game.cards.Card;
import net.demilich.metastone.game.cards.CardType;
import net.demilich.metastone.game.entities.Entity;
import net.demilich.metastone.game.entities.minions.Race;
import net.demilich.metastone.game.events.GameEvent;
import net.demilich.metastone.game.events.GameEventType;
import net.demilich.metastone.game.logic.CustomCloneable;
import net.demilich.metastone.game.spells.TargetPlayer;
import net.demilich.metastone.game.spells.desc.manamodifier.CardCostModifierArg;
import net.demilich.metastone.game.spells.desc.manamodifier.CardCostModifierDesc;
import net.demilich.metastone.game.spells.desc.trigger.EventTriggerDesc;
import net.demilich.metastone.game.spells.trigger.GameEventTrigger;
import net.demilich.metastone.game.spells.trigger.IGameEventListener;
import net.demilich.metastone.game.spells.trigger.TriggerLayer;
import net.demilich.metastone.game.targeting.EntityReference;

public class CardCostModifier extends CustomCloneable implements IGameEventListener {

	private boolean expired;
	private int owner;
	private EntityReference hostReference;
	private GameEventTrigger expirationTrigger;

	private CardCostModifierDesc desc;

	public CardCostModifier(CardCostModifierDesc desc) {
		this.desc = desc;
		EventTriggerDesc triggerDesc = (EventTriggerDesc) desc.get(CardCostModifierArg.EXPIRATION_TRIGGER);
		if (triggerDesc != null) {
			this.expirationTrigger = triggerDesc.create();
		}
	}

	public boolean appliesTo(Card card) {
		if (expired) {
			return false;
		}

		if (getRequiredAttribute() != null && !card.hasAttribute(getRequiredAttribute())) {
			return false;
		}

		if (getRequiredRace() != null && card.getAttribute(Attribute.RACE) != getRequiredRace()) {
			return false;
		}

		switch (getTargetPlayer()) {
		case BOTH:
			break;
		case OPPONENT:
			if (card.getOwner() == getOwner()) {
				return false;
			}
			break;
		case SELF:
			if (card.getOwner() != getOwner()) {
				return false;
			}
			break;
		default:
			break;

		}
		return card.getCardType() == getCardType();
	}

	@Override
	public CardCostModifier clone() {
		CardCostModifier clone = (CardCostModifier) super.clone();
		clone.expirationTrigger = expirationTrigger != null ? (GameEventTrigger) expirationTrigger.clone() : null;
		return clone;
	}

	protected void expire() {
		expired = true;
	}

	protected Object get(CardCostModifierArg arg) {
		return desc.get(arg);
	}

	protected CardType getCardType() {
		return (CardType) desc.get(CardCostModifierArg.CARD_TYPE);
	}

	@Override
	public EntityReference getHostReference() {
		return hostReference;
	}

	@Override
	public TriggerLayer getLayer() {
		return TriggerLayer.DEFAULT;
	}

	public int getMinValue() {
		return desc.getInt(CardCostModifierArg.MIN_VALUE);
	}

	@Override
	public int getOwner() {
		return owner;
	}

	protected Attribute getRequiredAttribute() {
		return (Attribute) desc.get(CardCostModifierArg.REQUIRED_ATTRIBUTE);
	}

	protected Race getRequiredRace() {
		return (Race) get(CardCostModifierArg.RACE);
	}

	protected TargetPlayer getTargetPlayer() {
		if (!desc.contains(CardCostModifierArg.TARGET_PLAYER)) {
			return TargetPlayer.SELF;
		}
		return (TargetPlayer) desc.get(CardCostModifierArg.TARGET_PLAYER);
	}

	@Override
	public boolean interestedIn(GameEventType eventType) {
		if (expirationTrigger == null) {
			return false;
		}
		return eventType == expirationTrigger.interestedIn() || expirationTrigger.interestedIn() == GameEventType.ALL;
	}

	@Override
	public boolean isExpired() {
		return expired;
	}

	@Override
	public void onAdd(GameContext context) {
	}

	@Override
	public void onGameEvent(GameEvent event) {
		Entity host = event.getGameContext().resolveSingleTarget(getHostReference());
		if (expirationTrigger != null && expirationTrigger.interestedIn() == event.getEventType() && expirationTrigger.fires(event, host)) {
			expire();
		}
	}

	@Override
	public void onRemove(GameContext context) {
		expired = true;
	}

	public int process(Card card, int currentManaCost) {
		if (desc.contains(CardCostModifierArg.FIXED_VALUE)) {
			return desc.getInt(CardCostModifierArg.FIXED_VALUE);
		}
		int modifiedManaCost = currentManaCost + desc.getInt(CardCostModifierArg.VALUE);
		return modifiedManaCost;
	}

	@Override
	public void setHost(Entity host) {
		hostReference = host.getReference();
	}

	@Override
	public void setOwner(int playerIndex) {
		this.owner = playerIndex;
		if (expirationTrigger != null) {
			expirationTrigger.setOwner(playerIndex);
		}
	}

}
