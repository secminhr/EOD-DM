package eod;

import eod.IO.Input;
import eod.IO.Output;
import eod.card.abstraction.Card;
import eod.card.collection.Deck;
import eod.card.collection.Hand;
import eod.card.collection.SpecialDeck;
import eod.characters.Character;
import eod.event.listener.EventListener;
import eod.exceptions.GameLosingException;
import eod.snapshots.Snapshotted;

import java.awt.*;
import java.util.*;

public class Player implements Snapshotted, GameObject {

    private Deck deck;
    private Game game;
    private SpecialDeck specialDeck;
    private Hand hand;
    private Leader leader;
    private Input input;
    private Output output;

    public Player(Deck deck, Leader leader) {
        this(deck, leader, new Hand());
    }

    public Player(Deck deck, Leader leader, Hand hand) {
        this.deck = deck;
        this.specialDeck = SpecialDeck.generateSpecialDeck(deck);
        this.leader = leader;
        this.hand = hand;
    }

    public void attachToGame(Game game) {
        this.game = game;
    }

    public void handReceive(ArrayList<Card> h) {
        hand.receive(h);
    }

    public void drawFromDeck(int count) {
        Card[] cards = deck.draw(count);
        handReceive(new ArrayList<>(Arrays.asList(cards)));
    }

    public boolean checkInHand(Class<? extends Card> c) {
        return hand.containsType(c);
    }

    public void dropCards() {
        hand.dropAllCards();
    }

    public void dropCards(int k) throws IllegalArgumentException {
        hand.randomlyDropCards(k);
    }
    
    public void announceWon() {
        
    }
    
    public void announceLost() {
        
    }

    //TODO: implement validateDeck, just by checking the number of cards and other things.
    // There's no need to look into the deck.
    public boolean validateDeck() {
        return true;
    }

    public Leader getLeader() {
        return leader;
    }

    public Gameboard getBoard() {
        return game.getBoard();
    }

    public boolean isLeaderAlive() {
        return leader.isAlive();
    }

    @Override
    public void teardown() {
        hand.teardown();
        hand = null;
        deck.teardown();
        deck = null;
        specialDeck.teardown();
        specialDeck = null;
        leader.teardown();
        leader = null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Player player = (Player) o;
        return Objects.equals(deck, player.deck) &&
                Objects.equals(specialDeck, player.specialDeck) &&
                Objects.equals(hand, player.hand);
    }

    @Override
    public int hashCode() {
        return Objects.hash(deck, specialDeck, hand);
    }

    @Override
    public Player snapshot() {
        Deck newDeck = deck.snapshot();
        Player clone = new Player(newDeck, leader, hand);
        clone.attachToGame(game);
        return clone;
    }

    public Player rival() {
        return game.getRivalPlayer(this);
    }

    public Character selectCharacter(Character[] characters) {
        //TODO: asks the player to select a character
        Random random = new Random();
        return characters[random.nextInt(characters.length)];
    }

    public Point selectPosition(ArrayList<Point> points) {
        //TODO: connection with the frontend
        Random random = new Random();
        return points.get(random.nextInt(points.size()));
    }

    public <T extends Card> T selectCard(T[] cards) {
        //TODO:connection with the frontend
        Random random = new Random();
        return cards[random.nextInt(cards.length)];
    }

    public void moveCharacter(Character character, Point point) {
        game.getBoard().moveElement(character.position, point);
        character.updatePosition(point);
    }

    public void loseCharacter(Character character) {
        getBoard().removeCharacter(character);
    }

    public void loseLeader() throws GameLosingException {
        throw new GameLosingException("Player "+this+" loses.");
    }
}
