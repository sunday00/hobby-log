package net.grayfield.spb.hobbylog.aop.catcher;

import graphql.ErrorClassification;

public enum CustomErrorType implements ErrorClassification {
    BAD_REQUEST,
    UNAUTHORIZED,
    FORBIDDEN,
    NOT_FOUND,
    INTERNAL_ERROR,
    FORCED_INTENDED_ERROR
}
