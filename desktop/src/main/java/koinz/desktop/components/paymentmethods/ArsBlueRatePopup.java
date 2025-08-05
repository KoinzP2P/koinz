package koinz.desktop.components.paymentmethods;

import koinz.desktop.main.overlays.popups.Popup;

import koinz.core.locale.FiatCurrency;
import koinz.core.locale.Res;
import koinz.core.locale.TradeCurrency;
import koinz.core.user.DontShowAgainLookup;

public class ArsBlueRatePopup {
    public static boolean isTradeCurrencyArgentinePesos(TradeCurrency tradeCurrency) {
        FiatCurrency arsCurrency = new FiatCurrency("ARS");
        return tradeCurrency.equals(arsCurrency);
    }

    public static void show() {
        createPopup().show();
    }

    public static void showMaybe() {
        String key = "arsBlueMarketNotificationPopup";
        if (DontShowAgainLookup.showAgain(key)) {
            createPopup().dontShowAgainId(key)
                    .show();
        }
    }

    private static Popup createPopup() {
        return new Popup()
                .headLine(Res.get("popup.arsBlueMarket.title"))
                .information(Res.get("popup.arsBlueMarket.info"))
                .actionButtonText(Res.get("shared.iUnderstand"))
                .hideCloseButton();
    }
}
