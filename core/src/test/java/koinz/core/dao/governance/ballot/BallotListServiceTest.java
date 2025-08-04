package koinz.core.dao.governance.ballot;

import koinz.core.dao.governance.ballot.BallotListService.BallotListChangeListener;
import koinz.core.dao.governance.period.PeriodService;
import koinz.core.dao.governance.proposal.ProposalService;
import koinz.core.dao.governance.proposal.ProposalValidatorProvider;
import koinz.core.dao.governance.proposal.storage.appendonly.ProposalPayload;

import koinz.common.persistence.PersistenceManager;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import org.junit.jupiter.api.Test;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class BallotListServiceTest {
    @Test
    @SuppressWarnings("unchecked")
    public void testAddListenersWhenNewPayloadAdded() {
        // given
        ObservableList<ProposalPayload> payloads = FXCollections.observableArrayList();

        ProposalService proposalService = mock(ProposalService.class);
        when(proposalService.getProposalPayloads()).thenReturn(payloads);

        BallotListService service = new BallotListService(proposalService, mock(PeriodService.class),
                mock(ProposalValidatorProvider.class), mock(PersistenceManager.class));

        BallotListChangeListener listener = mock(BallotListChangeListener.class);
        service.addListener(listener);

        service.addListeners();

        // when
        payloads.add(mock(ProposalPayload.class, RETURNS_DEEP_STUBS));

        // then
        verify(listener).onListChanged(any());
    }
}
