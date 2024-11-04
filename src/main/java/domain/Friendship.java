package domain;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 *
 *  Friendship entity
 *  Represents a friendship between two users
 *  A friendship is defined by the two users and the date when the friendship was created
 */
public class Friendship extends Entity<UUID>{
    User user1;
    User user2;
    LocalDateTime friendshipDate;

    public Friendship(User user1, User user2) {
        this.user1 = user1;
        this.user2 = user2;
        this.setId(UUID.randomUUID());
        this.friendshipDate = LocalDateTime.now();
    }

    public Friendship(User user1, User user2, LocalDateTime date) {
        this.user1 = user1;
        this.user2 = user2;
        this.setId(UUID.randomUUID());
        this.friendshipDate = date;
    }

    public User getUser1() {
        return user1;
    }

    public void setUser1(User user1) {
        this.user1 = user1;
    }

    public User getUser2() {
        return user2;
    }

    public void setUser2(User user2) {
        this.user2 = user2;
    }

    public LocalDateTime getFriendshipDate() {
        return friendshipDate;
    }

    public void setFriendshipDate(LocalDateTime friendshipDate) {
        this.friendshipDate = friendshipDate;
    }

    @Override
    public String toString() {
        return "Friendship{" +
                "user1=" + user1 +
                ", user2=" + user2 +
                ", friendshipDate=" + friendshipDate +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Friendship)) return false;
        Friendship that = (Friendship) o;
        return getUser1().equals(that.getUser1()) &&
                getUser2().equals(that.getUser2());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getUser1(), getUser2());
    }



}
