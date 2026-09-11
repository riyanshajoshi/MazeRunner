package game;

import java.util.List;

public class CommandResult {
    public enum Status {
        CONTINUE,
        WIN,
        LOSS,
        QUIT
    }

    private final List<String> messages;
    private final Status status;
    private final boolean playerMoved;
    private final boolean enemyMoved;

    public CommandResult(List<String> messages, Status status, boolean playerMoved, boolean enemyMoved) {
        this.messages = List.copyOf(messages);
        this.status = status;
        this.playerMoved = playerMoved;
        this.enemyMoved = enemyMoved;
    }

    public List<String> getMessages() {
        return messages;
    }

    public Status getStatus() {
        return status;
    }

    public boolean didPlayerMove() {
        return playerMoved;
    }

    public boolean didEnemyMove() {
        return enemyMoved;
    }
}
