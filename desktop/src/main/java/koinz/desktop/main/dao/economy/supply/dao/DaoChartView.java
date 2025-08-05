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

package koinz.desktop.main.dao.economy.supply.dao;

import koinz.desktop.components.AutoTooltipSlideToggleButton;
import koinz.desktop.components.chart.ChartView;

import koinz.core.locale.Res;

import koinz.common.util.CompletableFutureUtils;

import javax.inject.Inject;

import javafx.scene.chart.XYChart;
import javafx.scene.control.Tooltip;

import javafx.beans.property.LongProperty;
import javafx.beans.property.ReadOnlyLongProperty;
import javafx.beans.property.SimpleLongProperty;

import javafx.util.Duration;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DaoChartView extends ChartView<DaoChartViewModel> {
    private final LongProperty compensationAmountProperty = new SimpleLongProperty();
    private final LongProperty reimbursementAmountProperty = new SimpleLongProperty();
    private final LongProperty bsqTradeFeeAmountProperty = new SimpleLongProperty();
    private final LongProperty proofOfBurnAmountProperty = new SimpleLongProperty();

    private XYChart.Series<Number, Number> seriesBsqTradeFee, seriesProofOfBurn, seriesCompensation,
            seriesReimbursement, seriesTotalSupply, seriesSupplyChange, seriesTotalIssued, seriesTotalBurned,
            seriesTotalTradeFees, seriesProofOfBurnFromBtcFees, seriesMiscBurn,
            seriesProofOfBurnFromArbitration, seriesArbitrationDiff,
            seriesReimbursementAfterTagging, seriesBsqTradeFeeAfterTagging;


    @Inject
    public DaoChartView(DaoChartViewModel model) {
        super(model);
    }


    ///////////////////////////////////////////////////////////////////////////////////////////
    // API Total amounts
    ///////////////////////////////////////////////////////////////////////////////////////////

    public ReadOnlyLongProperty compensationAmountProperty() {
        return compensationAmountProperty;
    }

    public ReadOnlyLongProperty reimbursementAmountProperty() {
        return reimbursementAmountProperty;
    }

    public ReadOnlyLongProperty bsqTradeFeeAmountProperty() {
        return bsqTradeFeeAmountProperty;
    }

    public ReadOnlyLongProperty proofOfBurnAmountProperty() {
        return proofOfBurnAmountProperty;
    }


    ///////////////////////////////////////////////////////////////////////////////////////////
    // Legend
    ///////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected Collection<XYChart.Series<Number, Number>> getSeriesForLegend1() {
        // Total issued BSQ is sum of compensation requests and reimbursement requests
        return List.of(seriesTotalIssued, seriesCompensation, seriesReimbursement);
    }

    @Override
    protected Collection<XYChart.Series<Number, Number>> getSeriesForLegend2() {
        // Total burned BSQ is sum of BSQ trade fees and all Proof of Burn txs
        return List.of(seriesTotalBurned, seriesBsqTradeFee, seriesProofOfBurn);
    }

    @Override
    protected Collection<XYChart.Series<Number, Number>> getSeriesForLegend3() {
        //  Total trade fees is sum of BSQ trade fees and burned BSQ from BTC trade fees. Started to separate data in Nov 2021.
        return List.of(seriesTotalTradeFees, seriesBsqTradeFeeAfterTagging, seriesProofOfBurnFromBtcFees);
    }

    protected Collection<XYChart.Series<Number, Number>> getSeriesForLegend4() {
        // Difference of reimbursement requests and burned BSQ from arbitration cases. Should tend to zero. Started to separate data in Nov 2021.
        return List.of(seriesArbitrationDiff, seriesReimbursementAfterTagging, seriesProofOfBurnFromArbitration);
    }

    protected Collection<XYChart.Series<Number, Number>> getSeriesForLegend5() {
        return List.of(seriesSupplyChange, seriesTotalSupply, seriesMiscBurn);
    }


    ///////////////////////////////////////////////////////////////////////////////////////////
    // Timeline navigation
    ///////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected void initBoundsForTimelineNavigation() {
        if (seriesSupplyChange.getData().size() > 0) {
            setBoundsForTimelineNavigation(seriesSupplyChange.getData());
        } else {
            setBoundsForTimelineNavigation(seriesTotalBurned.getData());
        }
    }


    ///////////////////////////////////////////////////////////////////////////////////////////
    // Series
    ///////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected void createSeries() {
        seriesTotalIssued = new XYChart.Series<>();
        seriesTotalIssued.setName(Res.get("dao.factsAndFigures.supply.totalIssued"));
        seriesIndexMap.put(getSeriesId(seriesTotalIssued), 0);

        seriesTotalBurned = new XYChart.Series<>();
        seriesTotalBurned.setName(Res.get("dao.factsAndFigures.supply.totalBurned"));
        seriesIndexMap.put(getSeriesId(seriesTotalBurned), 1);

        seriesCompensation = new XYChart.Series<>();
        seriesCompensation.setName(Res.get("dao.factsAndFigures.supply.compReq"));
        seriesIndexMap.put(getSeriesId(seriesCompensation), 2);

        seriesReimbursement = new XYChart.Series<>();
        seriesReimbursement.setName(Res.get("dao.factsAndFigures.supply.reimbursement"));
        seriesIndexMap.put(getSeriesId(seriesReimbursement), 3);

        seriesBsqTradeFee = new XYChart.Series<>();
        seriesBsqTradeFee.setName(Res.get("dao.factsAndFigures.supply.bsqTradeFee"));
        seriesIndexMap.put(getSeriesId(seriesBsqTradeFee), 4);

        seriesProofOfBurn = new XYChart.Series<>();
        seriesProofOfBurn.setName(Res.get("dao.factsAndFigures.supply.proofOfBurn"));
        seriesIndexMap.put(getSeriesId(seriesProofOfBurn), 5);

        seriesTotalSupply = new XYChart.Series<>();
        seriesTotalSupply.setName(Res.get("dao.factsAndFigures.supply.totalSupply"));
        seriesIndexMap.put(getSeriesId(seriesTotalSupply), 6);

        seriesTotalTradeFees = new XYChart.Series<>();
        seriesTotalTradeFees.setName(Res.get("dao.factsAndFigures.supply.totalTradeFees"));
        seriesIndexMap.put(getSeriesId(seriesTotalTradeFees), 7);

        seriesProofOfBurnFromBtcFees = new XYChart.Series<>();
        seriesProofOfBurnFromBtcFees.setName(Res.get("dao.factsAndFigures.supply.btcFees"));
        seriesIndexMap.put(getSeriesId(seriesProofOfBurnFromBtcFees), 8);

        seriesProofOfBurnFromArbitration = new XYChart.Series<>();
        seriesProofOfBurnFromArbitration.setName(Res.get("dao.factsAndFigures.supply.arbitration"));
        seriesIndexMap.put(getSeriesId(seriesProofOfBurnFromArbitration), 9);

        seriesArbitrationDiff = new XYChart.Series<>();
        seriesArbitrationDiff.setName(Res.get("dao.factsAndFigures.supply.arbitrationDiff"));
        seriesIndexMap.put(getSeriesId(seriesArbitrationDiff), 10);

        seriesReimbursementAfterTagging = new XYChart.Series<>();
        seriesReimbursementAfterTagging.setName(Res.get("dao.factsAndFigures.supply.reimbursementAfterTagging"));
        seriesIndexMap.put(getSeriesId(seriesReimbursementAfterTagging), 11);

        seriesBsqTradeFeeAfterTagging = new XYChart.Series<>();
        seriesBsqTradeFeeAfterTagging.setName(Res.get("dao.factsAndFigures.supply.bsqTradeFeeAfterTagging"));
        seriesIndexMap.put(getSeriesId(seriesBsqTradeFeeAfterTagging), 12);

        seriesSupplyChange = new XYChart.Series<>();
        seriesSupplyChange.setName(Res.get("dao.factsAndFigures.supply.supplyChange"));
        seriesIndexMap.put(getSeriesId(seriesSupplyChange), 13);

        seriesMiscBurn = new XYChart.Series<>();
        seriesMiscBurn.setName(Res.get("dao.factsAndFigures.supply.miscBurn"));
        seriesIndexMap.put(getSeriesId(seriesMiscBurn), 14);
    }

    @Override
    protected void defineAndAddActiveSeries() {
        activateSeries(seriesSupplyChange);
    }

    @Override
    protected void maybeAddToolTip(AutoTooltipSlideToggleButton toggle, XYChart.Series<Number, Number> series) {
        if (series.equals(seriesTotalIssued)) {
            Tooltip tooltip = new Tooltip(Res.get("dao.factsAndFigures.supply.totalIssued.tooltip"));
            tooltip.setShowDelay(Duration.millis(100));
            Tooltip.install(toggle, tooltip);
        } else if (series.equals(seriesTotalBurned)) {
            Tooltip tooltip = new Tooltip(Res.get("dao.factsAndFigures.supply.totalBurned.tooltip"));
            tooltip.setShowDelay(Duration.millis(100));
            Tooltip.install(toggle, tooltip);
        } else if (series.equals(seriesTotalTradeFees)) {
            Tooltip tooltip = new Tooltip(Res.get("dao.factsAndFigures.supply.totalTradeFees.tooltip"));
            tooltip.setShowDelay(Duration.millis(100));
            Tooltip.install(toggle, tooltip);
        } else if (series.equals(seriesArbitrationDiff)) {
            Tooltip tooltip = new Tooltip(Res.get("dao.factsAndFigures.supply.arbitrationDiff.tooltip"));
            tooltip.setShowDelay(Duration.millis(100));
            Tooltip.install(toggle, tooltip);
        } else if (series.equals(seriesTotalSupply)) {
            Tooltip tooltip = new Tooltip(Res.get("dao.factsAndFigures.supply.totalSupply.tooltip"));
            tooltip.setShowDelay(Duration.millis(100));
            Tooltip.install(toggle, tooltip);
        } else if (series.equals(seriesSupplyChange)) {
            Tooltip tooltip = new Tooltip(Res.get("dao.factsAndFigures.supply.supplyChange.tooltip"));
            tooltip.setShowDelay(Duration.millis(100));
            Tooltip.install(toggle, tooltip);
        } else if (series.equals(seriesMiscBurn)) {
            Tooltip tooltip = new Tooltip(Res.get("dao.factsAndFigures.supply.miscBurn.tooltip"));
            tooltip.setShowDelay(Duration.millis(100));
            Tooltip.install(toggle, tooltip);
        }
    }


    ///////////////////////////////////////////////////////////////////////////////////////////
    // Data
    ///////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected CompletableFuture<Boolean> applyData() {
        List<CompletableFuture<Boolean>> allFutures = new ArrayList<>();
        if (activeSeries.contains(seriesTotalIssued)) {
            CompletableFuture<Boolean> future = new CompletableFuture<>();
            allFutures.add(future);
            applyTotalIssued(future);
        }
        if (activeSeries.contains(seriesCompensation)) {
            CompletableFuture<Boolean> future = new CompletableFuture<>();
            allFutures.add(future);
            applyCompensation(future);
        }
        if (activeSeries.contains(seriesReimbursement)) {
            CompletableFuture<Boolean> future = new CompletableFuture<>();
            allFutures.add(future);
            applyReimbursement(future);
        }
        if (activeSeries.contains(seriesTotalBurned)) {
            CompletableFuture<Boolean> future = new CompletableFuture<>();
            allFutures.add(future);
            applyTotalBurned(future);
        }
        if (activeSeries.contains(seriesBsqTradeFee)) {
            CompletableFuture<Boolean> future = new CompletableFuture<>();
            allFutures.add(future);
            applyBsqTradeFee(future);
        }
        if (activeSeries.contains(seriesProofOfBurn)) {
            CompletableFuture<Boolean> future = new CompletableFuture<>();
            allFutures.add(future);
            applyProofOfBurn(future);
        }
        if (activeSeries.contains(seriesTotalSupply)) {
            CompletableFuture<Boolean> future = new CompletableFuture<>();
            allFutures.add(future);
            applyTotalSupply(future);
        }
        if (activeSeries.contains(seriesSupplyChange)) {
            CompletableFuture<Boolean> future = new CompletableFuture<>();
            allFutures.add(future);
            applySupplyChange(future);
        }
        if (activeSeries.contains(seriesTotalTradeFees)) {
            CompletableFuture<Boolean> future = new CompletableFuture<>();
            allFutures.add(future);
            applyTotalTradeFees(future);
        }
        if (activeSeries.contains(seriesProofOfBurnFromBtcFees)) {
            CompletableFuture<Boolean> future = new CompletableFuture<>();
            allFutures.add(future);
            applyProofOfBurnFromBtcFees(future);
        }
        if (activeSeries.contains(seriesProofOfBurnFromArbitration)) {
            CompletableFuture<Boolean> future = new CompletableFuture<>();
            allFutures.add(future);
            applyProofOfBurnFromArbitration(future);
        }
        if (activeSeries.contains(seriesArbitrationDiff)) {
            CompletableFuture<Boolean> future = new CompletableFuture<>();
            allFutures.add(future);
            applyArbitrationDiff(future);
        }
        if (activeSeries.contains(seriesReimbursementAfterTagging)) {
            CompletableFuture<Boolean> future = new CompletableFuture<>();
            allFutures.add(future);
            applyReimbursementAfterTagging(future);
        }
        if (activeSeries.contains(seriesBsqTradeFeeAfterTagging)) {
            CompletableFuture<Boolean> future = new CompletableFuture<>();
            allFutures.add(future);
            applyBsqTradeFeeAfterTagging(future);
        }
        if (activeSeries.contains(seriesMiscBurn)) {
            CompletableFuture<Boolean> future = new CompletableFuture<>();
            allFutures.add(future);
            applyMiscBurn(future);
        }


        CompletableFuture<Boolean> task15Done = new CompletableFuture<>();
        allFutures.add(task15Done);
        model.getCompensationAmount()
                .whenComplete((data, t) ->
                        mapToUserThread(() -> {
                            Optional.ofNullable(data).ifPresent(compensationAmountProperty::set);
                            task15Done.complete(true);
                        }));

        CompletableFuture<Boolean> task16Done = new CompletableFuture<>();
        allFutures.add(task16Done);
        model.getReimbursementAmount()
                .whenComplete((data, t) ->
                        mapToUserThread(() -> {
                            Optional.ofNullable(data).ifPresent(reimbursementAmountProperty::set);
                            task16Done.complete(true);
                        }));

        CompletableFuture<Boolean> task17Done = new CompletableFuture<>();
        allFutures.add(task17Done);
        model.getBsqTradeFeeAmount()
                .whenComplete((data, t) ->
                        mapToUserThread(() -> {
                            Optional.ofNullable(data).ifPresent(bsqTradeFeeAmountProperty::set);
                            task17Done.complete(true);
                        }));

        CompletableFuture<Boolean> task18Done = new CompletableFuture<>();
        allFutures.add(task18Done);
        model.getProofOfBurnAmount()
                .whenComplete((data, t) ->
                        mapToUserThread(() -> {
                            Optional.ofNullable(data).ifPresent(proofOfBurnAmountProperty::set);
                            task18Done.complete(true);
                        }));

        return CompletableFutureUtils.allOf(allFutures).thenApply(e -> true);
    }

    private void applyTotalSupply(CompletableFuture<Boolean> completeFuture) {
        model.getTotalSupplyChartData()
                .whenComplete((data, t) ->
                        mapToUserThread(() -> {
                            Optional.ofNullable(data).ifPresent(seriesTotalSupply.getData()::setAll);
                            completeFuture.complete(true);
                        }));
    }

    private void applySupplyChange(CompletableFuture<Boolean> completeFuture) {
        model.getSupplyChangeChartData()
                .whenComplete((data, t) ->
                        mapToUserThread(() -> {
                            Optional.ofNullable(data).ifPresent(seriesSupplyChange.getData()::setAll);
                            completeFuture.complete(true);
                        }));
    }

    private void applyTotalTradeFees(CompletableFuture<Boolean> completeFuture) {
        model.getTotalTradeFeesChartData()
                .whenComplete((data, t) ->
                        mapToUserThread(() -> {
                            Optional.ofNullable(data).ifPresent(seriesTotalTradeFees.getData()::setAll);
                            completeFuture.complete(true);
                        }));
    }

    private void applyProofOfBurnFromBtcFees(CompletableFuture<Boolean> completeFuture) {
        model.getProofOfBurnFromBtcFeesChartData()
                .whenComplete((data, t) ->
                        mapToUserThread(() -> {
                            Optional.ofNullable(data).ifPresent(seriesProofOfBurnFromBtcFees.getData()::setAll);
                            completeFuture.complete(true);
                        }));
    }

    private void applyProofOfBurnFromArbitration(CompletableFuture<Boolean> completeFuture) {
        model.getProofOfBurnFromArbitrationChartData()
                .whenComplete((data, t) ->
                        mapToUserThread(() -> {
                            Optional.ofNullable(data).ifPresent(seriesProofOfBurnFromArbitration.getData()::setAll);
                            completeFuture.complete(true);
                        }));
    }

    private void applyArbitrationDiff(CompletableFuture<Boolean> completeFuture) {
        model.getArbitrationDiffByInterval()
                .whenComplete((data, t) ->
                        mapToUserThread(() -> {
                            Optional.ofNullable(data).ifPresent(seriesArbitrationDiff.getData()::setAll);
                            completeFuture.complete(true);
                        }));
    }

    private void applyTotalIssued(CompletableFuture<Boolean> completeFuture) {
        model.getTotalIssuedChartData()
                .whenComplete((data, t) ->
                        mapToUserThread(() -> {
                            Optional.ofNullable(data).ifPresent(seriesTotalIssued.getData()::setAll);
                            completeFuture.complete(true);
                        }));
    }

    private void applyCompensation(CompletableFuture<Boolean> completeFuture) {
        model.getCompensationChartData()
                .whenComplete((data, t) ->
                        mapToUserThread(() -> {
                            Optional.ofNullable(data).ifPresent(seriesCompensation.getData()::setAll);
                            completeFuture.complete(true);
                        }));
    }

    private void applyReimbursement(CompletableFuture<Boolean> completeFuture) {
        model.getReimbursementChartData()
                .whenComplete((data, t) ->
                        mapToUserThread(() -> {
                            Optional.ofNullable(data).ifPresent(seriesReimbursement.getData()::setAll);
                            completeFuture.complete(true);
                        }));
    }

    private void applyTotalBurned(CompletableFuture<Boolean> completeFuture) {
        model.getTotalBurnedChartData()
                .whenComplete((data, t) ->
                        mapToUserThread(() -> {
                            Optional.ofNullable(data).ifPresent(seriesTotalBurned.getData()::setAll);
                            completeFuture.complete(true);
                        }));
    }

    private void applyBsqTradeFee(CompletableFuture<Boolean> completeFuture) {
        model.getBsqTradeFeeChartData()
                .whenComplete((data, t) ->
                        mapToUserThread(() -> {
                            Optional.ofNullable(data).ifPresent(seriesBsqTradeFee.getData()::setAll);
                            completeFuture.complete(true);
                        }));
    }

    private void applyProofOfBurn(CompletableFuture<Boolean> completeFuture) {
        model.getProofOfBurnChartData()
                .whenComplete((data, t) ->
                        mapToUserThread(() -> {
                            Optional.ofNullable(data).ifPresent(seriesProofOfBurn.getData()::setAll);
                            completeFuture.complete(true);
                        }));
    }

    private void applyReimbursementAfterTagging(CompletableFuture<Boolean> completeFuture) {
        model.getReimbursementAfterTaggingChartData()
                .whenComplete((data, t) ->
                        mapToUserThread(() -> {
                            Optional.ofNullable(data).ifPresent(seriesReimbursementAfterTagging.getData()::setAll);
                            completeFuture.complete(true);
                        }));
    }

    private void applyBsqTradeFeeAfterTagging(CompletableFuture<Boolean> completeFuture) {
        model.getBsqTradeFeeAfterTaggingChartData()
                .whenComplete((data, t) ->
                        mapToUserThread(() -> {
                            Optional.ofNullable(data).ifPresent(seriesBsqTradeFeeAfterTagging.getData()::setAll);
                            completeFuture.complete(true);
                        }));
    }

    private void applyMiscBurn(CompletableFuture<Boolean> completeFuture) {
        model.getMiscBurnChartData()
                .whenComplete((data, t) ->
                        mapToUserThread(() -> {
                            Optional.ofNullable(data).ifPresent(seriesMiscBurn.getData()::setAll);
                            completeFuture.complete(true);
                        }));
    }
}
