package domain.validators;

import domain.Friendship;

/**
 * Friendship validator
 * Validates a friendship
 */
public class FriendshipValidator implements Validator<Friendship> {
    @Override
    public void validate(Friendship entity) throws ValidationException {
        if (entity.getUser1().equals(entity.getUser2())) {
            throw new ValidationException("Real smart move, have you ever tried going outside?");
        }
    }
}
