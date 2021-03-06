package eod.card.concrete.summon;

import eod.Party;
import eod.card.abstraction.Card;
import eod.card.abstraction.summon.SummonCard;
import eod.card.abstraction.summon.SummonCardType;
import eod.effect.Summon;
import eod.warObject.character.concrete.red.LittleGhost;

import static eod.effect.EffectFunctions.Summon;

public class LittleGhostSummon extends SummonCard {
    public LittleGhostSummon() {
        super(0, SummonCardType.TOKEN);
    }

    @Override
    public Summon summonEffect() {
        return Summon(new LittleGhost(player)).onOnePointOf(player, player.getBaseEmpty());
    }

    @Override
    public Card copy() {
        Card c = new LittleGhostSummon();
        c.setPlayer(player);
        return c;
    }

    @Override
    public String getName() {
        return "召喚 小亡靈";
    }

    @Override
    public Party getParty() {
        return Party.RED;
    }
}
