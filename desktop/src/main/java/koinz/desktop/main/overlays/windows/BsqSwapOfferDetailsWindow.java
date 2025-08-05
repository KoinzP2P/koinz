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

package koinz.desktop.main.overlays.windows;

import koinz.desktop.components.AutoTooltipButton;
import koinz.desktop.components.BusyAnimation;
import koinz.desktop.main.offer.OfferViewUtil;
import koinz.desktop.main.overlays.Overlay;
import koinz.desktop.util.DisplayUtils;
import koinz.desktop.util.Layout;

import koinz.core.locale.Res;
import koinz.core.monetary.Price;
import koinz.core.monetary.Volume;
import koinz.core.offer.Offer;
import koinz.core.offer.OfferDirection;
import koinz.core.payment.PaymentAccount;
import koinz.core.payment.payload.PaymentMethod;
import koinz.core.user.User;
import koinz.core.util.FormattingUtils;
import koinz.core.util.VolumeUtil;
import koinz.core.util.coin.CoinFormatter;

import koinz.common.crypto.KeyRing;
import koinz.common.util.Tuple4;

import org.bitcoinj.core.Coin;

import javax.inject.Inject;
import javax.inject.Named;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

import javafx.geometry.Insets;

import java.util.Optional;

import lombok.extern.slf4j.Slf4j;

import static koinz.desktop.util.FormBuilder.*;

@Slf4j
public class BsqSwapOfferDetailsWindow extends Overlay<BsqSwapOfferDetailsWindow> {
    private final CoinFormatter formatter;
    private final User user;
    private final KeyRing keyRing;
    private Offer offer;
    private Coin tradeAmount;
    private Price tradePrice;
    private Optional<Runnable> placeOfferHandlerOptional = Optional.empty();
    private Optional<Runnable> takeOfferHandlerOptional = Optional.empty();
    private BusyAnimation busyAnimation;


    ///////////////////////////////////////////////////////////////////////////////////////////
    // Public API
    ///////////////////////////////////////////////////////////////////////////////////////////

    @Inject
    public BsqSwapOfferDetailsWindow(@Named(FormattingUtils.BTC_FORMATTER_KEY) CoinFormatter formatter,
                                     User user,
                                     KeyRing keyRing) {
        this.formatter = formatter;
        this.user = user;
        this.keyRing = keyRing;
        type = Type.Confirmation;
    }

    public void show(Offer offer, Coin tradeAmount, Price tradePrice) {
        this.offer = offer;
        this.tradeAmount = tradeAmount;
        this.tradePrice = tradePrice;

        rowIndex = -1;
        width = 1118;
        createGridPane();
        addContent();
        display();
    }

    public void show(Offer offer) {
        this.offer = offer;
        rowIndex = -1;
        width = 1118;
        createGridPane();
        addContent();
        display();
    }

    public BsqSwapOfferDetailsWindow onPlaceOffer(Runnable placeOfferHandler) {
        this.placeOfferHandlerOptional = Optional.of(placeOfferHandler);
        return this;
    }

    public BsqSwapOfferDetailsWindow onTakeOffer(Runnable takeOfferHandler) {
        this.takeOfferHandlerOptional = Optional.of(takeOfferHandler);
        return this;
    }


    ///////////////////////////////////////////////////////////////////////////////////////////
    // Protected
    ///////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected void onHidden() {
        if (busyAnimation != null)
            busyAnimation.stop();
    }

    @Override
    protected void createGridPane() {
        super.createGridPane();
        gridPane.setPadding(new Insets(35, 40, 30, 40));
        gridPane.getStyleClass().add("grid-pane");
    }

    private void addContent() {
        gridPane.getColumnConstraints().get(0).setMinWidth(224);

        int rows = 5;
        boolean isTakeOfferScreen = takeOfferHandlerOptional.isPresent();
        boolean isMakeOfferScreen = placeOfferHandlerOptional.isPresent();
        boolean isMyOffer = offer.isMyOffer(keyRing);

        if (!isTakeOfferScreen)
            rows++;

        addTitledGroupBg(gridPane, ++rowIndex, rows, Res.get("shared.Offer"));

        String bsqDirectionInfo;
        String btcDirectionInfo;
        OfferDirection direction = offer.getDirection();
        String currencyCode = offer.getCurrencyCode();
        String offerTypeLabel = Res.get("shared.offerType");
        String toReceive = " " + Res.get("shared.toReceive");
        String toSpend = " " + Res.get("shared.toSpend");
        String minus = " - ";
        String plus = " + ";
        String minerFeePostFix = Res.get("tradeDetailsWindow.txFee");
        String tradeFeePostFix = Res.get("shared.tradeFee");
        String btcAmount;
        String bsqAmount;
        double firstRowDistance = Layout.TWICE_FIRST_ROW_DISTANCE;
        boolean isSellOffer = direction == OfferDirection.SELL;
        boolean isBuyOffer = direction == OfferDirection.BUY;
        boolean isBuyer;
        String offerType;
        Coin amount = isTakeOfferScreen ? tradeAmount : offer.getAmount();
        Volume volume = isTakeOfferScreen ? offer.getVolumeByAmount(tradeAmount) : offer.getVolume();
        btcAmount = formatter.formatCoinWithCode(amount);
        bsqAmount = VolumeUtil.formatVolumeWithCode(volume);
        boolean isMaker = isMakeOfferScreen || isMyOffer;
        boolean isTaker = !isMaker;

        if (isTaker) {
            bsqDirectionInfo = isBuyOffer ? toReceive : toSpend;
            btcDirectionInfo = isSellOffer ? toReceive : toSpend;
            isBuyer = isSellOffer;
        } else {
            bsqDirectionInfo = isSellOffer ? toReceive : toSpend;
            btcDirectionInfo = isBuyOffer ? toReceive : toSpend;
            isBuyer = isBuyOffer;
        }
        if (isTakeOfferScreen) {
            offerType = DisplayUtils.getDirectionForTakeOffer(direction, currencyCode);
        } else if (isMakeOfferScreen) {
            offerType = DisplayUtils.getOfferDirectionForCreateOffer(direction, currencyCode);
        } else {
            offerType = isBuyer ?
                    DisplayUtils.getDirectionForBuyer(isMyOffer, offer.getCurrencyCode()) :
                    DisplayUtils.getDirectionForSeller(isMyOffer, offer.getCurrencyCode());
        }
        if (!isTakeOfferScreen &&
                offer.getVolume() != null &&
                offer.getMinVolume() != null &&
                !offer.getVolume().equals(offer.getMinVolume())) {
            bsqAmount += " " + Res.get("offerDetailsWindow.min", VolumeUtil.formatVolumeWithCode(offer.getMinVolume()));
        }

        addConfirmationLabelLabel(gridPane, rowIndex, offerTypeLabel, offerType, firstRowDistance);

        if (isBuyer) {
            btcAmount += minus + minerFeePostFix;
            bsqAmount += plus + tradeFeePostFix;
        } else {
            btcAmount += plus + minerFeePostFix;
            bsqAmount += minus + tradeFeePostFix;
        }

        String btcAmountTitle = Res.get("shared.btcAmount");
        addConfirmationLabelLabel(gridPane, ++rowIndex, btcAmountTitle + btcDirectionInfo, btcAmount);
        if (!isTakeOfferScreen) {
            addConfirmationLabelLabel(gridPane, ++rowIndex, Res.get("offerDetailsWindow.minBtcAmount"),
                    formatter.formatCoinWithCode(offer.getMinAmount()));

        }
        addConfirmationLabelLabel(gridPane, ++rowIndex,
                VolumeUtil.formatVolumeLabel(currencyCode) + bsqDirectionInfo, bsqAmount);

        String priceLabel = Res.get("shared.price");
        if (isTakeOfferScreen) {
            addConfirmationLabelLabel(gridPane, ++rowIndex, priceLabel, FormattingUtils.formatPrice(tradePrice));
        } else {
            addConfirmationLabelLabel(gridPane, ++rowIndex, priceLabel, FormattingUtils.formatPrice(offer.getPrice()));
        }
        PaymentMethod paymentMethod = offer.getPaymentMethod();
        String makerPaymentAccountId = offer.getMakerPaymentAccountId();
        PaymentAccount myPaymentAccount = user.getPaymentAccount(makerPaymentAccountId);
        if (isMyOffer && makerPaymentAccountId != null && myPaymentAccount != null) {
            addConfirmationLabelLabel(gridPane, ++rowIndex, Res.get("offerDetailsWindow.myTradingAccount"), myPaymentAccount.getAccountName());
        } else {
            String method = Res.get(paymentMethod.getId());
            addConfirmationLabelLabel(gridPane, ++rowIndex, Res.get("shared.paymentMethod"), method);
        }

        rows = 3;

        // details

        addTitledGroupBg(gridPane, ++rowIndex, rows, Res.get("shared.details"), Layout.GROUP_DISTANCE);
        addConfirmationLabelTextFieldWithCopyIcon(gridPane, rowIndex, Res.get("shared.offerId"), offer.getId(),
                Layout.TWICE_FIRST_ROW_AND_GROUP_DISTANCE);
        addConfirmationLabelTextFieldWithCopyIcon(gridPane, ++rowIndex, Res.get("offerDetailsWindow.makersOnion"),
                offer.getMakerNodeAddress().getFullAddress());
        addConfirmationLabelLabel(gridPane, ++rowIndex, Res.get("offerDetailsWindow.creationDate"),
                DisplayUtils.formatDateTime(offer.getDate()));

        // commitment

        if (isMakeOfferScreen) {
            addConfirmAndCancelButtons(true);
        } else if (isTakeOfferScreen) {
            addConfirmAndCancelButtons(false);
        } else {
            addCloseButton(gridPane, ++rowIndex, () -> {
                closeHandlerOptional.ifPresent(Runnable::run);
                hide();
            });
        }
    }

    private void addConfirmAndCancelButtons(boolean isPlaceOffer) {
        boolean isBuyOffer = OfferViewUtil.isShownAsBuyOffer(offer);
        boolean isBuyerRole = isPlaceOffer == isBuyOffer;
        String tradeCurrencyByCode = offer.getCurrencyCode();
        String placeOfferButtonText = isBuyerRole ?
                Res.get("offerDetailsWindow.confirm.makerAltcoin", Res.get("shared.buy"), tradeCurrencyByCode) :
                Res.get("offerDetailsWindow.confirm.makerAltcoin", Res.get("shared.sell"), tradeCurrencyByCode);
        String takeOfferButtonText = isBuyerRole ?
                Res.get("offerDetailsWindow.confirm.takerAltcoin", Res.get("shared.buy"), tradeCurrencyByCode) :
                Res.get("offerDetailsWindow.confirm.takerAltcoin", Res.get("shared.sell"), tradeCurrencyByCode);

        ImageView iconView = new ImageView();
        iconView.setId(isBuyerRole ? "image-buy-white" : "image-sell-white");

        Tuple4<Button, BusyAnimation, Label, HBox> placeOfferTuple = addButtonBusyAnimationLabelAfterGroup(gridPane,
                ++rowIndex, 1,
                isPlaceOffer ? placeOfferButtonText : takeOfferButtonText);

        AutoTooltipButton button = (AutoTooltipButton) placeOfferTuple.first;
        button.setMinHeight(40);
        button.setPadding(new Insets(0, 20, 0, 20));
        button.setGraphic(iconView);
        button.setGraphicTextGap(10);
        button.setId(isBuyerRole ? "buy-button-big" : "sell-button-big");
        button.updateText(isPlaceOffer ? placeOfferButtonText : takeOfferButtonText);

        busyAnimation = placeOfferTuple.second;
        Label spinnerInfoLabel = placeOfferTuple.third;

        Button cancelButton = new AutoTooltipButton(Res.get("shared.cancel"));
        cancelButton.setDefaultButton(false);
        cancelButton.setOnAction(e -> {
            closeHandlerOptional.ifPresent(Runnable::run);
            hide();
        });

        placeOfferTuple.fourth.getChildren().add(cancelButton);

        button.setOnAction(e -> {
            button.setDisable(true);
            cancelButton.setDisable(true);
            // temporarily disabled due to high CPU usage (per issue #4649)
            //  busyAnimation.play();
            if (isPlaceOffer) {
                spinnerInfoLabel.setText(Res.get("createOffer.fundsBox.placeOfferSpinnerInfo"));
                placeOfferHandlerOptional.ifPresent(Runnable::run);
            } else {
                spinnerInfoLabel.setText(Res.get("takeOffer.fundsBox.takeOfferSpinnerInfo"));
                takeOfferHandlerOptional.ifPresent(Runnable::run);
            }
            hide();
        });
    }
}
