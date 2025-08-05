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

package koinz.desktop.main.overlays.windows.supporttool;

import koinz.desktop.components.AutoTooltipButton;
import koinz.desktop.components.InputTextField;
import koinz.desktop.main.overlays.popups.Popup;

import koinz.core.btc.wallet.TradeWalletService;
import koinz.core.btc.wallet.WalletsManager;
import koinz.core.locale.Res;

import koinz.common.UserThread;
import koinz.common.util.Tuple2;
import koinz.common.util.Utilities;

import de.jensd.fx.fontawesome.AwesomeDude;
import de.jensd.fx.fontawesome.AwesomeIcon;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;

import javafx.geometry.Pos;

import lombok.extern.slf4j.Slf4j;

import static koinz.desktop.util.FormBuilder.addInputTextField;

@Slf4j
public class SignPane extends CommonPane {

    private final TradeWalletService tradeWalletService;
    private final InputsPane inputsPane;
    private final InputTextField privateKeyHex;
    private final InputTextField signatureHex;

    SignPane(WalletsManager walletsManager, TradeWalletService tradeWalletService, InputsPane inputsPane) {
        this.tradeWalletService = tradeWalletService;
        this.inputsPane = inputsPane;
        int rowIndexB = 0;
        GridPane signTxGridPane = this;
        privateKeyHex = addInputTextField(signTxGridPane, ++rowIndexB, "privateKeyHex");
        signatureHex = addInputTextField(signTxGridPane, ++rowIndexB, "signatureHex");
        signatureHex.setPrefWidth(800);
        signatureHex.setEditable(false);
        Label copyIcon = new Label();
        copyIcon.setTooltip(new Tooltip(Res.get("txIdTextField.copyIcon.tooltip")));
        AwesomeDude.setIcon(copyIcon, AwesomeIcon.COPY);
        copyIcon.getStyleClass().addAll("icon", "highlight");
        copyIcon.setMinWidth(20);
        copyIcon.setOnMouseClicked(mouseEvent -> Utilities.copyToClipboard(signatureHex.getText()));
        HBox hBoxSig = new HBox(12, signatureHex, copyIcon);
        hBoxSig.setAlignment(Pos.BASELINE_LEFT);
        hBoxSig.setPrefWidth(800);
        signTxGridPane.add(new Label(""), 0, ++rowIndexB);  // spacer
        signTxGridPane.add(hBoxSig, 0, ++rowIndexB);
        signTxGridPane.add(new Label(""), 0, ++rowIndexB);  // spacer
        Button buttonLocate = new AutoTooltipButton("Locate key in wallet");
        Button buttonSign = new AutoTooltipButton("Generate Signature");
        HBox hBox = new HBox(12, buttonLocate, buttonSign);
        hBox.setAlignment(Pos.BASELINE_CENTER);
        hBox.setPrefWidth(800);
        signTxGridPane.add(hBox, 0, ++rowIndexB);
        buttonLocate.setOnAction(e -> {
            if (!inputsPane.validateInputFields()) {
                signatureHex.setText("You need to fill in the inputs tab first");
                return;
            }
            String walletInfo = walletsManager.getWalletsAsString(true);
            String privateKeyText = findPrivForPubOrAddress(walletInfo, inputsPane.getBuyerPubKeyAsHex().getText());
            if (privateKeyText == null) {
                privateKeyText = findPrivForPubOrAddress(walletInfo, inputsPane.getSellerPubKeyAsHex().getText());
            }
            if (privateKeyText == null) {
                privateKeyText = "Not found in wallet";
            }
            privateKeyHex.setText(privateKeyText);
        });
        buttonSign.setOnAction(e -> {
            signatureHex.setText(generateSignature());
        });
    }

    public void activate() {
        privateKeyHex.setText("");
        signatureHex.setText("");
        super.activate();
    }

    @Override
    public String getName() {
        return "Sign";
    }

    private String generateSignature() {
        inputsPane.calculateTxFee();
        // check that all input fields have been entered, except signatures
        if (!inputsPane.validateInputFields() || privateKeyHex.getText().length() < 1) {
            return "You need to fill in the inputs first";
        }

        String retVal = "";
        try {
            Tuple2<String, String> combined = tradeWalletService.emergencyBuildPayoutTxFrom2of2MultiSig(
                    inputsPane.getDepositTxHex().getText(),
                    InputsPane.getInputFieldAsCoin(inputsPane.getBuyerPayoutAmount()),
                    InputsPane.getInputFieldAsCoin(inputsPane.getSellerPayoutAmount()),
                    InputsPane.getInputFieldAsCoin(inputsPane.getTxFee()),
                    inputsPane.getBuyerAddressString().getText(),
                    inputsPane.getSellerAddressString().getText(),
                    inputsPane.getBuyerPubKeyAsHex().getText(),
                    inputsPane.getSellerPubKeyAsHex().getText(),
                    inputsPane.getDepositTxLegacy().isSelected());
            String redeemScriptHex = combined.first;
            String unsignedTxHex =  combined.second;
            retVal = tradeWalletService.emergencyGenerateSignature(
                    unsignedTxHex,
                    redeemScriptHex,
                    InputsPane.getInputFieldAsCoin(inputsPane.getAmountInMultisig()),
                    privateKeyHex.getText());
        } catch (IllegalArgumentException ee) {
            log.error(ee.toString());
            ee.printStackTrace();
            UserThread.execute(() -> new Popup().warning(ee.toString()).show());
        }
        return retVal;
    }
}
