package koinz.core.offer.placeoffer.bisq_v1.tasks;

import koinz.core.locale.Res;
import koinz.core.offer.placeoffer.bisq_v1.PlaceOfferModel;

import koinz.common.taskrunner.Task;
import koinz.common.taskrunner.TaskRunner;

public class CheckNumberOfUnconfirmedTransactions extends Task<PlaceOfferModel> {
    public CheckNumberOfUnconfirmedTransactions(TaskRunner<PlaceOfferModel> taskHandler, PlaceOfferModel model) {
        super(taskHandler, model);
    }

    @Override
    protected void run() {
        if (model.getWalletService().isUnconfirmedTransactionsLimitHit() || model.getBsqWalletService().isUnconfirmedTransactionsLimitHit()) {
            failed(Res.get("shared.unconfirmedTransactionsLimitReached"));
        } else {
            complete();
        }
    }
}
