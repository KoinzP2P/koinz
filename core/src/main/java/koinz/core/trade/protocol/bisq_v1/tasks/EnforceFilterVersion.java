package koinz.core.trade.protocol.bisq_v1.tasks;

import koinz.core.filter.FilterManager;
import koinz.core.trade.bisq_v1.TradeUtil;
import koinz.core.trade.model.bisq_v1.Trade;

import koinz.common.taskrunner.TaskRunner;

public class EnforceFilterVersion extends TradeTask {
    public EnforceFilterVersion(TaskRunner<Trade> taskHandler, Trade trade) {
        super(taskHandler, trade);
    }

    @Override
    protected void run() {
        try {
            runInterceptHook();
            FilterManager filterManager = processModel.getFilterManager();
            TradeUtil.enforceFilterVersion(filterManager, this::complete, this::failed);
        } catch (Throwable t) {
            failed(t);
        }
    }
}
