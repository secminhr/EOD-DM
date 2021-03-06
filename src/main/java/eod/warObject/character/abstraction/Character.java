package eod.warObject.character.abstraction;

import eod.Gameboard;
import eod.Party;
import eod.Player;
import eod.card.abstraction.summon.SummonCard;
import eod.effect.EffectExecutor;
import eod.param.AttackParam;
import eod.warObject.CanAttack;
import eod.warObject.Damageable;
import eod.warObject.Status;
import eod.warObject.WarObject;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;

public abstract class Character extends WarObject implements Damageable, CanAttack {
    protected ArrayList<Status> status = new ArrayList<>();
    protected int max_hp;
    protected int hp;
    protected int attack;
    protected final Party party;
    protected CanAttack attacker;

    public Character(Player player, int hp, int attack, Party party) {
        super(player);
        this.player = player;
        max_hp = hp;
        this.hp = max_hp;
        this.attack = attack;
        this.party = party;
    }

    @Override
    public void heal(int gain) {
        if(hp+gain >= max_hp) {
            hp = max_hp;
        } else {
            hp+=gain;
        }
    }

    @Override
    public void addHealth(int hp) {
        this.hp += hp;
        this.max_hp += hp;
    }

    @Override
    public int getAttack() {
        return attack;
    }

    @Override
    public void addAttack(int a) {
        attack += a;
    }

    @Override
    public void attack(EffectExecutor executor) {
        removeStatus(Status.SNEAK);
    }

    @Override
    public ArrayList<Damageable> attack(Gameboard gameboard, ArrayList<Point> targets, AttackParam param) {
        int hp = param.hp;
        ArrayList<Damageable> affected = new ArrayList<>();
        for(Point p:targets) {
            try {
                Damageable target = gameboard.getObjectOn(p.x, p.y);
                if(param.realDamage) {
                    target.realDamage(hp);
                } else {
                    target.attacked(this, hp);
                }
                affected.add(target);
                ((WarObject)target).addStatus(Status.ATTACKED);
            } catch (IllegalArgumentException e) {
                System.out.println(e.toString());
            }
        }
        return affected;
    }

    @Override
    public ArrayList<Damageable> attack(Damageable target, AttackParam param) {
        return attack(new Damageable[] {target}, param);
    }

    @Override
    public ArrayList<Damageable> attack(Damageable[] targets, AttackParam param) {
        int hp = param.hp;
        ArrayList<Damageable> affected = new ArrayList<>();
        Arrays.stream(targets)
                .forEach(target -> {
                    if(param.realDamage) {
                        target.realDamage(hp);
                    } else {
                        target.attacked(this, hp);
                    }
                    affected.add(target);
                    ((WarObject)target).addStatus(Status.ATTACKED);
                });
        return affected;
    }

    @Override
    public void realDamage(int val) {
        hp -= val;
        if(hp <= 0) {
            die();
        }
    }

    @Override
    public void damage(int val) {
        realDamage(val);
    }

    @Override
    public void attacked(CanAttack attacker, int hp) {
        this.attacker = attacker;
        damage(hp);
        this.attacker = null;
    }

    @Override
    public CanAttack getAttacker() {
        return attacker;
    }

    @Override
    public void die() {
        player.loseObject(this);
        teardown();
    }

    @Override
    public void teardown() {
        super.teardown();
        status.clear();
    }

    public Party getParty() {
        return party;
    }

    @Override
    public int getHp() {
        return hp;
    }

    public abstract SummonCard getSummonCard();
}
