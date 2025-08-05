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

package koinz.desktop.main.settings.preferences;


import koinz.desktop.common.model.ActivatableViewModel;

import koinz.core.locale.LanguageUtil;
import koinz.core.support.dispute.mediation.mediator.MediatorManager;
import koinz.core.support.dispute.refund.refundagent.RefundAgentManager;
import koinz.core.user.Preferences;

import com.google.inject.Inject;

import java.util.stream.Collectors;

public class PreferencesViewModel extends ActivatableViewModel {

    private final RefundAgentManager refundAgentManager;
    private final MediatorManager mediationManager;
    private final Preferences preferences;

    @Inject
    public PreferencesViewModel(Preferences preferences,
                                RefundAgentManager refundAgentManager,
                                MediatorManager mediationManager) {
        this.preferences = preferences;
        this.refundAgentManager = refundAgentManager;
        this.mediationManager = mediationManager;
    }

    boolean needsSupportLanguageWarning() {
        return !refundAgentManager.isAgentAvailableForLanguage(preferences.getUserLanguage()) ||
                !mediationManager.isAgentAvailableForLanguage(preferences.getUserLanguage());
    }

    String getArbitrationLanguages() {
        return refundAgentManager.getObservableMap().values().stream()
                .flatMap(arbitrator -> arbitrator.getLanguageCodes().stream())
                .distinct()
                .map(LanguageUtil::getDisplayName)
                .collect(Collectors.joining(", "));
    }

    public String getMediationLanguages() {
        return mediationManager.getObservableMap().values().stream()
                .flatMap(mediator -> mediator.getLanguageCodes().stream())
                .distinct()
                .map(LanguageUtil::getDisplayName)
                .collect(Collectors.joining(", "));
    }
}
