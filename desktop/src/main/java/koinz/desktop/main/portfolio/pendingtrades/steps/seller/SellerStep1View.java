/*
 * This file is part of KOINZ.
 *
 * KOINZ is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at
 * your option) any later version.
 *
 * KOINZ is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with KOINZ. If not, see <http://www.gnu.org/licenses/>.
 */

package koinz.desktop.main.portfolio.pendingtrades.steps.seller;

import koinz.desktop.main.overlays.popups.Popup;
import koinz.desktop.main.portfolio.pendingtrades.PendingTradesViewModel;
import koinz.desktop.main.portfolio.pendingtrades.steps.TradeStepView;

import koinz.core.locale.Res;
import koinz.core.trade.bisq_v1.TradeDataValidation;

public class SellerStep1View extends TradeStepView {

    ///////////////////////////////////////////////////////////////////////////////////////////
    // Constructor, Initialisation
    ///////////////////////////////////////////////////////////////////////////////////////////

    public SellerStep1View(PendingTradesViewModel model) {
        super(model);
    }

    @Override
    protected void onPendingTradesInitialized() {
        super.onPendingTradesInitialized();
        validateDepositInputs();
        model.checkForTimeoutAtTradeStep1();
    }

    ///////////////////////////////////////////////////////////////////////////////////////////
    // Info
    ///////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected String getInfoBlockTitle() {
        return Res.get("portfolio.pending.step1.waitForConf");
    }

    @Override
    protected String getInfoText() {
        return Res.get("portfolio.pending.step1.info", Res.get("shared.TheBTCBuyer"));
    }

    ///////////////////////////////////////////////////////////////////////////////////////////
    // Warning
    ///////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected String getFirstHalfOverWarnText() {
        return Res.get("portfolio.pending.step1.warn");
    }

    ///////////////////////////////////////////////////////////////////////////////////////////
    // Dispute
    ///////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected String getPeriodOverWarnText() {
        return Res.get("portfolio.pending.step1.openForDispute");
    }

    ///////////////////////////////////////////////////////////////////////////////////////////
    // Private
    ///////////////////////////////////////////////////////////////////////////////////////////

    // Verify that deposit tx inputs are matching the trade fee txs outputs.
    private void validateDepositInputs() {
        try {
            TradeDataValidation.validateDepositInputs(trade);
        } catch (TradeDataValidation.ValidationException e) {
            if (!model.dataModel.tradeManager.isAllowFaultyDelayedTxs()) {
                new Popup().warning(Res.get("portfolio.pending.invalidTx", e.getMessage())).show();
            }
        }
    }
}


