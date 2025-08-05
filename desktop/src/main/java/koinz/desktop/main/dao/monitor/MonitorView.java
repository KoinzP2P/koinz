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

package koinz.desktop.main.dao.monitor;

import koinz.desktop.Navigation;
import koinz.desktop.common.view.ActivatableView;
import koinz.desktop.common.view.CachingViewLoader;
import koinz.desktop.common.view.FxmlView;
import koinz.desktop.common.view.View;
import koinz.desktop.common.view.ViewLoader;
import koinz.desktop.common.view.ViewPath;
import koinz.desktop.components.MenuItem;
import koinz.desktop.main.MainView;
import koinz.desktop.main.dao.DaoView;
import koinz.desktop.main.dao.monitor.blindvotes.BlindVoteStateMonitorView;
import koinz.desktop.main.dao.monitor.daostate.DaoStateMonitorView;
import koinz.desktop.main.dao.monitor.proposals.ProposalStateMonitorView;

import koinz.core.locale.Res;

import javax.inject.Inject;

import javafx.fxml.FXML;

import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

import java.util.Arrays;
import java.util.List;

@FxmlView
public class MonitorView extends ActivatableView<AnchorPane, Void> {
    private final ViewLoader viewLoader;
    private final Navigation navigation;

    private MenuItem daoState, proposals, blindVotes;
    private Navigation.Listener navigationListener;

    @FXML
    private VBox leftVBox;
    @FXML
    private AnchorPane content;
    private Class<? extends View> selectedViewClass;
    private ToggleGroup toggleGroup;

    @Inject
    private MonitorView(CachingViewLoader viewLoader, Navigation navigation) {
        this.viewLoader = viewLoader;
        this.navigation = navigation;
    }

    @Override
    public void initialize() {
        navigationListener = (viewPath, data) -> {
            if (viewPath.size() != 4 || viewPath.indexOf(MonitorView.class) != 2)
                return;

            selectedViewClass = viewPath.tip();
            loadView(selectedViewClass);
        };

        toggleGroup = new ToggleGroup();
        List<Class<? extends View>> baseNavPath = Arrays.asList(MainView.class, DaoView.class, MonitorView.class);
        daoState = new MenuItem(navigation, toggleGroup, Res.get("dao.monitor.daoState"),
                DaoStateMonitorView.class, baseNavPath);
        proposals = new MenuItem(navigation, toggleGroup, Res.get("dao.monitor.proposals"),
                ProposalStateMonitorView.class, baseNavPath);
        blindVotes = new MenuItem(navigation, toggleGroup, Res.get("dao.monitor.blindVotes"),
                BlindVoteStateMonitorView.class, baseNavPath);

        leftVBox.getChildren().addAll(daoState, proposals, blindVotes);
    }

    @Override
    protected void activate() {
        proposals.activate();
        blindVotes.activate();
        daoState.activate();

        navigation.addListener(navigationListener);
        ViewPath viewPath = navigation.getCurrentPath();
        if (viewPath.size() == 3 && viewPath.indexOf(MonitorView.class) == 2 ||
                viewPath.size() == 2 && viewPath.indexOf(DaoView.class) == 1) {
            if (selectedViewClass == null)
                selectedViewClass = DaoStateMonitorView.class;

            loadView(selectedViewClass);

        } else if (viewPath.size() == 4 && viewPath.indexOf(MonitorView.class) == 2) {
            selectedViewClass = viewPath.get(3);
            loadView(selectedViewClass);
        }
    }

    @SuppressWarnings("Duplicates")
    @Override
    protected void deactivate() {
        navigation.removeListener(navigationListener);

        proposals.deactivate();
        blindVotes.deactivate();
        daoState.deactivate();
    }

    private void loadView(Class<? extends View> viewClass) {
        View view = viewLoader.load(viewClass);
        content.getChildren().setAll(view.getRoot());

        if (view instanceof DaoStateMonitorView) toggleGroup.selectToggle(daoState);
        else if (view instanceof ProposalStateMonitorView) toggleGroup.selectToggle(proposals);
        else if (view instanceof BlindVoteStateMonitorView) toggleGroup.selectToggle(blindVotes);
    }
}


