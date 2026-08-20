package com.arafath.quickpay.domain.usecase;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.arafath.quickpay.domain.model.TransactionStatus;

import org.junit.Test;

public class TransactionStateMachineTest {

    @Test
    public void pending_canGoToProcessingOrFailed() {
        assertTrue(TransactionStateMachine.canTransition(TransactionStatus.PENDING, TransactionStatus.PROCESSING));
        assertTrue(TransactionStateMachine.canTransition(TransactionStatus.PENDING, TransactionStatus.FAILED));
        assertFalse(TransactionStateMachine.canTransition(TransactionStatus.PENDING, TransactionStatus.SUCCESS));
    }

    @Test
    public void processing_canGoToSuccessFailedOrReversed() {
        assertTrue(TransactionStateMachine.canTransition(TransactionStatus.PROCESSING, TransactionStatus.SUCCESS));
        assertTrue(TransactionStateMachine.canTransition(TransactionStatus.PROCESSING, TransactionStatus.FAILED));
        assertTrue(TransactionStateMachine.canTransition(TransactionStatus.PROCESSING, TransactionStatus.REVERSED));
    }

    @Test
    public void terminalStates_cannotTransition() {
        assertFalse(TransactionStateMachine.canTransition(TransactionStatus.SUCCESS, TransactionStatus.REVERSED));
        assertFalse(TransactionStateMachine.canTransition(TransactionStatus.FAILED, TransactionStatus.SUCCESS));
        assertFalse(TransactionStateMachine.canTransition(TransactionStatus.REVERSED, TransactionStatus.SUCCESS));
    }

    @Test
    public void selfTransition_isRejected() {
        assertFalse(TransactionStateMachine.canTransition(TransactionStatus.PENDING, TransactionStatus.PENDING));
        assertFalse(TransactionStateMachine.canTransition(TransactionStatus.PROCESSING, TransactionStatus.PROCESSING));
    }

    @Test
    public void nullInputs_areRejected() {
        assertFalse(TransactionStateMachine.canTransition(null, TransactionStatus.SUCCESS));
        assertFalse(TransactionStateMachine.canTransition(TransactionStatus.PENDING, null));
    }

    @Test
    public void terminalStatuses_areDetected() {
        assertTrue(TransactionStateMachine.isTerminal(TransactionStatus.SUCCESS));
        assertTrue(TransactionStateMachine.isTerminal(TransactionStatus.FAILED));
        assertTrue(TransactionStateMachine.isTerminal(TransactionStatus.REVERSED));
        assertFalse(TransactionStateMachine.isTerminal(TransactionStatus.PENDING));
        assertFalse(TransactionStateMachine.isTerminal(TransactionStatus.PROCESSING));
    }
}