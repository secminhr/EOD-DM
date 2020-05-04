package eod;

import eod.card.abstraction.Card;
import eod.card.abstraction.action.ActionCard;
import eod.card.concrete.conditional.Dodge;
import eod.event.AttackEvent;
import eod.event.DirectAttackEvent;
import eod.event.EventManager;
import eod.event.listener.AttackListener;
import eod.exceptions.GameLosingException;
import eod.snapshots.BoardSnapshot;
import eod.snapshots.GameSnapshot;
import eod.snapshots.Snapshotted;

import java.util.ArrayList;
import java.util.Random;

//represent a game instance
//each manages a ongoing game
public class Game implements Snapshotted, GameObject {

    private Player A;
    private Player B;
    private Gameboard gameboard;
    private Player[] playerOrder;
    private EventManager eventManager = new EventManager();

    public Game(Player A, Player B) {
        this.A = A;
        this.B = B;

        A.attachToGame(this);
        B.attachToGame(this);
    }


    public void start() {
        A.validateDeck();
        B.validateDeck();

        playerOrder = decidePlayerOrder();
        A.drawFromDeck(3);
        B.drawFromDeck(3);

        boolean hasInvalid;
        int drawRound = 0;
        do {
            hasInvalid = false;
            for(int i = 0;i < playerOrder.length;i++) {
                Player player = playerOrder[i];
                Player other = playerOrder[i==0?1:0];

                if(handIsInvalid(player)) {
                    hasInvalid = true;
                    other.drawFromDeck(1);
                    player.dropCards();
                    player.drawFromDeck(3);
                }
            }
            if(hasInvalid) {
                drawRound++;
            }
        } while(drawRound<3 || hasInvalid);


        // the second player should receive a Dodge at the start
        ArrayList<Card> firstHand = new ArrayList<>() {{
            new Dodge(playerOrder[1]);
        }};
        firstHand.add(new Dodge(playerOrder[1]));
        playerOrder[1].handReceive(firstHand);

        while(true) {
            try {
                gameLoop();
            } catch (GameLosingException e) {
                break;
            }
        }

        // TODO: things to do when a person wins
        if(A.isLeaderAlive()) {
            //A wins
        } else if(B.isLeaderAlive()) {
            //B wins
        }

        teardown();
    }

    private Player[] decidePlayerOrder() {
        Random random = new Random();
        boolean AFirst = random.nextBoolean();
        if(AFirst) {
            return new Player[]{A, B};
        } else {
            return new Player[]{B, A};
        }
    }

    public Player getRivalPlayer(Player player) {
        if(player == playerOrder[0]) {
            return playerOrder[1];
        } else {
            return playerOrder[0];
        }
    }

    private boolean handIsInvalid(Player player) {
        return !player.checkInHand(ActionCard.class);
    }

    private void gameLoop() throws GameLosingException {
    }

    public void registerListener(AttackListener listener) {
        eventManager.registerAttackEvent(listener);
    }

    public void sendEvent(AttackEvent event) {
        eventManager.sendAttack(event);
    }

    public void unregisterListener(AttackListener listener) {
        eventManager.unregisterAttackEvent(listener);
    }

    @Override
    public void teardown() {
        for(Player player: playerOrder) {
            player.teardown();
        }
        A = null;
        B = null;

        gameboard.teardown();
        gameboard = null;

        eventManager.teardown();
        eventManager = null;
    }//TODO: finish teardown

    public Gameboard getBoard() {
        return gameboard;
    }

    @Override
    public GameSnapshot snapshot() {
        Player Aclone = A.snapshot();
        Player Bclone = B.snapshot();
        BoardSnapshot boardSnapshot = gameboard.snapshot();

        return new GameSnapshot(Aclone, Bclone, boardSnapshot);
    }
}
