package net.grayfield.spb.hobbylog.aop.catcher;

import graphql.GraphQLException;

public class SignWithOtherException extends GraphQLException {
    public SignWithOtherException(String s) {
        super(s);
    }
}
